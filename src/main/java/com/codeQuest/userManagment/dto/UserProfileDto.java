package com.codeQuest.userManagment.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNum;
    private String gender;
    private String birthDate;
}