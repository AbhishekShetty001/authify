package com.abhishek.authify.io;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
    @NotBlank(message = "enter the name not blank ")
    private String name;
    @Email(message = "enter a valid email")
    @NotNull(message = "should not be null")
    private String email;
    @Size(min = 6,message = " password length should me more than 6")
    private String password;

}
