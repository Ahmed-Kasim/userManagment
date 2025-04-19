package com.codeQuest.userManagment.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginRequest {
    private String phoneNum;
    private String password;
}