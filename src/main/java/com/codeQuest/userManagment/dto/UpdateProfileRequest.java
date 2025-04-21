package com.codeQuest.userManagment.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {

    @NotNull
    private Long Id;

    @NotBlank
    @Pattern(regexp = "^(010|011|012|015)[0-9]{8}$", message = "Invalid phone number format.")
    private String newPhoneNum;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.com|outlook\\.com)$",
            message = "Must be a valid Gmail, Yahoo, or Outlook address")
    private String email;

    @NotBlank
    private String birthDate;
}
