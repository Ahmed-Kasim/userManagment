package com.codeQuest.userManagment.controller;

import com.codeQuest.userManagment.service.OtpService;
import com.codeQuest.userManagment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
public class OtpController {
    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmailAndSendOtp(@RequestParam String email) {
        if (!userService.doesEmailExist(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email does not exist.");
        }
        String result = otpService.sendOtp(email);
        return ResponseEntity.ok(result);
    }

    //verify otp page
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = otpService.verifyOtp(email, otp);  // Verify OTP internally
        return isValid ? ResponseEntity.ok("OTP verified!") : ResponseEntity.badRequest().body("Invalid OTP!");
    }
}
