package com.codeQuest.userManagment.dto;

public class LoginRequest {
    private String phoneNum;
    private String password;
    private String email;
    // Getters and Setters
    public String getPhoneNum() {
        return phoneNum;
    }

    public String getEmail(){return email;};

    public void setEmail(String email){this.email= email;};

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}