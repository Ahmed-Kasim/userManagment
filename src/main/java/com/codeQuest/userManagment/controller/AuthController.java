package com.codeQuest.userManagment.controller;

import com.codeQuest.userManagment.dto.LoginRequest;
import com.codeQuest.userManagment.dto.UpdateProfileRequest;
import com.codeQuest.userManagment.dto.UserDto;
import com.codeQuest.userManagment.dto.UserProfileDto;
import com.codeQuest.userManagment.entities.User;
import com.codeQuest.userManagment.repository.UserRepository;
import com.codeQuest.userManagment.service.UserService;
import com.codeQuest.userManagment.service.OtpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private OtpService otpService;

    private Map<String, UserDto> pendingUsers = new ConcurrentHashMap<>();

    //Sign-up
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
        String resultMessage = otpService.sendOtp(email);
        if (!resultMessage.contains("sent")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to send OTP."));
        }
        return ResponseEntity.ok(Map.of("message", "OTP sent! Please enter your OTP to verify."));
    }

    //send otp-after sign-up
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean otpVerified = otpService.verifyOtp(email, otp);
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

    //login page
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        boolean isAuthenticated = userService.login(loginRequest);
        return isAuthenticated
                ? ResponseEntity.ok(Map.of("message", "Login Successful"))
                : ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }



    //profile page
    @GetMapping("/profile/{phoneNum}")
    public ResponseEntity<?> getProfileByPhone(@PathVariable String phoneNum) {
        try {
            UserProfileDto userProfile = userService.getUserProfileByPhone(phoneNum);
            return ResponseEntity.ok(userProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    //edit page
    @PutMapping("/profile/edit")
    public ResponseEntity<?> editProfile(@RequestBody @Valid UpdateProfileRequest updateRequest) {
        try {
            User updatedUser = userService.updateUserProfile(updateRequest);
            return ResponseEntity.ok("Profile updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


}
