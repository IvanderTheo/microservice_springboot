package com.example.user.dto;

public record UpdateUserRequest(
        String name,
        String email
) {
}
