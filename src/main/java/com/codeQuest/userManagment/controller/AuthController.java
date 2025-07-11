package com.codeQuest.userManagment.controller;

import com.codeQuest.userManagment.dto.*;
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
import java.util.Optional;
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
        Optional<User> savedUser = userService.createUser(userDto);
        return savedUser.map(user -> ResponseEntity.ok(Map.of(
                        "message", "User registered successfully!",
                        "userId", user.getId()
                )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Registration failed. Please try again.")));
    }

    //login page
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userService.login(loginRequest);
        return user.map(u -> ResponseEntity.ok(Map.of(
                        "message", "Login Successful",
                        "userId", u.getId()
                )))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
    }

    //profile page
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfileById(@PathVariable Long userId) {
        try {
            UserProfileDto userProfile = userService.getUserProfileById(userId);
            return ResponseEntity.ok(userProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    //edit page
    @PutMapping("/profile/edit")
    public ResponseEntity<?> editProfile(@RequestBody @Valid UpdateProfileRequest updateRequest) {
        try {
            Optional<User> updatedUser = userService.updateUserProfile(updateRequest);
            return ResponseEntity.ok("Profile updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        userService.resetPassword(request.getEmail(),request.getNewPassword());
        return ResponseEntity.ok("Password has been successfully reset.");
    }

}
