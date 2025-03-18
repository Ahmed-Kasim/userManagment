package com.codeQuest.userManagment.controller;
//saidhaosoifioashihasfioh
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto, BindingResult result) {
        // Handle validation errors and return structured JSON
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            userService.createUser(userDto);
            return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        boolean isAuthenticated = userService.login(loginRequest);
        return isAuthenticated
                ? ResponseEntity.ok(Map.of("message", "Login Successful"))
                : ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    // Centralized exception handling for IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @GetMapping("/email/{accId}")
    public ResponseEntity<String> getEmailByAccId(@PathVariable Long accId) {
        User user = userService.getUserByAccId(accId); // Create this method in UserService
        return user != null ? ResponseEntity.ok(user.getEmail()) : ResponseEntity.notFound().build();
    }
}

