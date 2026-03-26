package com.example.smartinventorymanagementbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonAlias;

public record LoginRequest(
        String email,
        @JsonAlias({"pass", "password"})
        String password
) {
}
