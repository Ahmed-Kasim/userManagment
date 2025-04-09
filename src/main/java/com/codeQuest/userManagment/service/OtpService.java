package com.codeQuest.userManagment.service;

import com.codeQuest.userManagment.entities.OtpCode;
import com.codeQuest.userManagment.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpRepository otpRepository;

    private static final int OTP_EXPIRATION_MINUTES = 5;

    public String sendOtp(String email) {
        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);

        // Remove any existing OTP for the email before creating a new one
        otpRepository.findByEmail(email).ifPresent(otpRepository::delete);

        OtpCode otpCode = new OtpCode();
        otpCode.setEmail(email);
        otpCode.setOtp(otp);
        otpCode.setExpiryTime(expiryTime);
        otpRepository.save(otpCode);

        sendOtpToEmail(email, otp);
        return "OTP sent successfully!";
    }

    public boolean verifyOtp(String email, String otp) {
        return otpRepository.findByEmail(email)
                .filter(otpCode -> otpCode.getOtp().equals(otp) && LocalDateTime.now().isBefore(otpCode.getExpiryTime()))
                .map(otpCode -> {
                    otpRepository.delete(otpCode); // OTP is valid, remove it after use
                    return true;
                })
                .orElse(false);
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // Generates a 6-digit OTP
    }

    private void sendOtpToEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification OTP");
        message.setText("Your OTP code is: " + otp + "\n\nThis code is valid for " + OTP_EXPIRATION_MINUTES + " minutes.");
        mailSender.send(message);
    }
}
