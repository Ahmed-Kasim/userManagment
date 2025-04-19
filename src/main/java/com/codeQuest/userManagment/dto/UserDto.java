package com.codeQuest.userManagment.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNum;
    private String gender;
    private String birthDate;
}