package com.codeQuest.userManagment.service;

import com.codeQuest.userManagment.dto.LoginRequest;
import com.codeQuest.userManagment.dto.UserDto;
import com.codeQuest.userManagment.dto.UserProfileDto;
import com.codeQuest.userManagment.entities.User;
import com.codeQuest.userManagment.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String PHONE_REGEX = "^(010|011|012|015)[0-9]{8}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.com|outlook\\.com)$";

    public User createUser(com.codeQuest.userManagment.dto.@Valid UserDto userDto) {
        if (!isValidPhoneNumber(userDto.getPhoneNum())) {
            throw new IllegalArgumentException("Invalid phone number format! It must be 10 digits, optionally with a country code.");
        }

        if (!isValidEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Invalid email format! Must be a valid Gmail, Yahoo, or Outlook address.");
        }

        if (userRepository.existsByPhoneNum(userDto.getPhoneNum())) {
            throw new IllegalArgumentException("Phone number already exists!");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists!");
        }

        if (!isStrongPassword(userDto.getPassword())) {
            throw new IllegalArgumentException("Weak password! Use at least 8 characters, one uppercase, one lowercase, one digit, and one special character.");
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNum(userDto.getPhoneNum());
        user.setGender(userDto.getGender());
        user.setBirthDate(userDto.getBirthDate());

        return userRepository.save(user);
    }

    public boolean login(LoginRequest loginRequest) {
        User user = userRepository.findByPhoneNum(loginRequest.getPhoneNum());
        if (user == null) {
            user = userRepository.findByEmail(loginRequest.getPhoneNum());
        }
        return user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
    }

    private boolean isStrongPassword(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");
    }

    private boolean isValidPhoneNumber(String phone) {
        return Pattern.matches(PHONE_REGEX, phone);
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public User getUserByAccId(Long accId) {
        Optional<User> userOptional = userRepository.findById(accId);
        return userOptional.orElse(null);
    }

    public boolean doesEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserProfileDto getUserProfileByPhone(String phoneNum) {
        User user = userRepository.findByPhoneNum(phoneNum);
        if (user == null) {
            throw new IllegalArgumentException("User not found with phone number: " + phoneNum);
        }

        return new UserProfileDto(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNum(),
                user.getGender(),
                user.getBirthDate()
        );
    }
}
