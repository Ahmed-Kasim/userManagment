package com.codeQuest.userManagment.service;

import com.codeQuest.userManagment.dto.LoginRequest;
import com.codeQuest.userManagment.dto.UserDto;
import com.codeQuest.userManagment.dto.UpdateProfileRequest;
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

    public Optional<User> createUser(UserDto userDto) {
        if (userRepository.existsByPhoneNum(userDto.getPhoneNum())) {
            throw new IllegalArgumentException("Phone number already exists!");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists!");
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNum(userDto.getPhoneNum());
        user.setGender(userDto.getGender());
        user.setBirthDate(userDto.getBirthDate());

        return Optional.of(userRepository.save(user));
    }

    public Optional<User> login(LoginRequest loginRequest) {
        User user = userRepository.findByPhoneNum(loginRequest.getPhoneNum());
        if (user == null) {
            user = userRepository.findByEmail(loginRequest.getPhoneNum());
        }

        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return Optional.of(user);
        }

        return Optional.empty();
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
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNum(),
                user.getBirthDate()
        );
    }

    public User updateUserProfile(UpdateProfileRequest updateRequest) {
        User user = userRepository.findByPhoneNum(updateRequest.getOldPhoneNum());
        if (user == null) {
            throw new IllegalArgumentException("User not found with phone number: " + updateRequest.getOldPhoneNum());
        }
        if (!user.getPhoneNum().equals(updateRequest.getNewPhoneNum()) &&
                userRepository.existsByPhoneNum(updateRequest.getNewPhoneNum())) {
            throw new IllegalArgumentException("Phone number is already taken.");
        }
        if (!user.getEmail().equals(updateRequest.getEmail()) &&
                userRepository.existsByEmail(updateRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already taken.");
        }
        user.setPhoneNum(updateRequest.getNewPhoneNum());
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setEmail(updateRequest.getEmail());
        user.setGender(updateRequest.getGender());
        user.setBirthDate(updateRequest.getBirthDate());
        if (updateRequest.getNewPassword() != null && !updateRequest.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
        }
        return userRepository.save(user);
    }

}
