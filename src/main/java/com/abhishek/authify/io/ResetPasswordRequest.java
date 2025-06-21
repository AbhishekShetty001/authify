package com.abhishek.authify.io;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "enter a valid email")
    private String email;
    @NotBlank(message = "enter a valid otp")
    private String otp;
    @NotBlank(message = "enter the password")
    private String newpassword;
}
