package com.codeQuest.userManagment.controller;

import com.codeQuest.userManagment.dto.LoginRequest;
import com.codeQuest.userManagment.dto.UserDto;
import com.codeQuest.userManagment.entities.User;
import com.codeQuest.userManagment.repository.UserRepository;
import com.codeQuest.userManagment.service.UserService;
import com.codeQuest.userManagment.service.OtpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    private Map<String, UserDto> pendingUsers = new ConcurrentHashMap<>();

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        String email = userDto.getEmail();

        // Store user temporarily before OTP verification
        pendingUsers.put(email, userDto);

        // Send OTP using the internal service
        String resultMessage = otpService.sendOtp(email);  // Handle OTP generation and sending internally
        if (!resultMessage.contains("sent")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to send OTP."));
        }

        return ResponseEntity.ok(Map.of("message", "OTP sent! Please enter your OTP to verify."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean otpVerified = otpService.verifyOtp(email, otp);  // Verify OTP using the internal service

        if (!otpVerified) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid OTP!"));
        }

        UserDto userDto = pendingUsers.remove(email);
        if (userDto == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User data not found for this email."));
        }

        userService.createUser(userDto);

        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        boolean isAuthenticated = userService.login(loginRequest);
        return isAuthenticated
                ? ResponseEntity.ok(Map.of("message", "Login Successful"))
                : ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    @GetMapping("/email/{accId}")
    public ResponseEntity<String> getEmailByAccId(@PathVariable Long accId) {
        User user = userService.getUserByAccId(accId);
        return user != null ? ResponseEntity.ok(user.getEmail()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.doesEmailExist(email);
        return ResponseEntity.ok(exists);
    }
}
