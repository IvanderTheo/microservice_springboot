package com.example.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

    @NotBlank (message = "Name is required")
    @Size (max = 255, message = "Name must not exceed 255 characters")
    String name,

    @NotBlank  (message = "Email is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    String email,

    @NotBlank (message = "Password is Required")
    @Size(min = 8, message = "Password min 8 Character")
    @Size(max = 16, message = "Password max 16 Character")
    String password
) {}