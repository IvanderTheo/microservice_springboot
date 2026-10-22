package com.example.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest (
    @NotBlank (message = "Email is Required")
    String email,

    @NotBlank (message = "Password is Required")
    String password
){
    
}
