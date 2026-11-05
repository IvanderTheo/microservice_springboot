package com.example.user.dto;

public record LoginResponse(
        String tokenType,
        String accessToken,
        long expiresIn,
        UserResponse user
) {
}
