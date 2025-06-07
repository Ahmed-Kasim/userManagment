package com.codeQuest.userManagment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordRequest  {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String newPassword;
}
