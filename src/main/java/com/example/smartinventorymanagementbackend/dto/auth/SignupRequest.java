package com.example.smartinventorymanagementbackend.dto.auth;

public record SignupRequest(
        String name,
        String email,
        String password,
        String role
) {
}
