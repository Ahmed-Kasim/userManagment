package com.codeQuest.userManagment.controller;

import com.codeQuest.userManagment.dto.LoginRequest;
import com.codeQuest.userManagment.dto.UserDto;
import com.codeQuest.userManagment.entities.User;
import com.codeQuest.userManagment.repository.UserRepository;
import com.codeQuest.userManagment.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

    private final String OTP_API_URL = "http://localhost:8080/otp/send";
    private final String OTP_VERIFY_URL = "http://localhost:8080/otp/verify";

    private final RestTemplate restTemplate = new RestTemplate();

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
        pendingUsers.put(email, userDto);

        ResponseEntity<String> otpResponse = restTemplate.postForEntity(
                OTP_API_URL + "?email=" + email, null, String.class
        );

        return ResponseEntity.ok(Map.of("message", "OTP sent! Please verify your email."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        ResponseEntity<String> otpResponse = restTemplate.postForEntity(
                OTP_VERIFY_URL + "?email=" + email + "&otp=" + otp, null, String.class
        );

        if (!"OTP verified!".equals(otpResponse.getBody())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid OTP!"));
        }

        UserDto userDto = pendingUsers.remove(email);
        if (userDto == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "No user data found for this email."));
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
