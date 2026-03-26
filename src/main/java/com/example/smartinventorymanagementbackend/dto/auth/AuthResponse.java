package com.example.smartinventorymanagementbackend.dto.auth;

public record AuthResponse(
        String token,
        String tokenType,
        String name,
        String email,
        String role
) {
}
