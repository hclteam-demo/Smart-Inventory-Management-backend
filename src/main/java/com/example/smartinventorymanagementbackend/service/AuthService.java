package com.example.smartinventorymanagementbackend.service;

import com.example.smartinventorymanagementbackend.dto.auth.AuthResponse;
import com.example.smartinventorymanagementbackend.dto.auth.LoginRequest;
import com.example.smartinventorymanagementbackend.dto.auth.SignupRequest;
import com.example.smartinventorymanagementbackend.entity.UserAccount;
import com.example.smartinventorymanagementbackend.entity.enums.UserRole;
import com.example.smartinventorymanagementbackend.repository.UserAccountRepository;
import com.example.smartinventorymanagementbackend.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse signup(SignupRequest request) {
        validateSignup(request);

        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        if (userAccountRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        UserRole role;
        try {
            role = UserRole.valueOf(request.role().trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be MANAGER, ADMIN, or STAFF");
        }

        UserAccount user = new UserAccount();
        user.setName(request.name().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);

        UserAccount savedUser = userAccountRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                token,
                "Bearer",
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    public AuthResponse login(LoginRequest request) {
        validateLogin(request);

        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        UserAccount user = userAccountRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                "Bearer",
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    private void validateSignup(SignupRequest request) {
        if (request == null || isBlank(request.name()) || isBlank(request.email()) || isBlank(request.password()) || isBlank(request.role())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name, email, password and role are required");
        }
    }

    private void validateLogin(LoginRequest request) {
        if (request == null || isBlank(request.email()) || isBlank(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email and password are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
