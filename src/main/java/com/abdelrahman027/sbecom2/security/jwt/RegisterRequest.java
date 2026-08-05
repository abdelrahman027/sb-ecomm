package com.abdelrahman027.sbecom2.security.jwt;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 4,max = 50)
    private String username;

    @NotBlank
    @Email
    @Size(min = 4,max = 50)
    private String email;

    @NotBlank
    private String password;

    Set<String> roles;

}
