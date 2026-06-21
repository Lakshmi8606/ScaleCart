package com.scalecart.auth.controller;

import com.scalecart.auth.dto.AuthResponse;
import com.scalecart.auth.dto.LoginRequest;
import com.scalecart.auth.dto.RegisterRequest;
import com.scalecart.auth.entity.User;
import com.scalecart.auth.service.AuthenticationService;
import com.scalecart.auth.service.UserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRegistrationService userRegistrationService;
    private final AuthenticationService authenticationService;

    public AuthController(UserRegistrationService userRegistrationService,
                          AuthenticationService authenticationService) {
        this.userRegistrationService = userRegistrationService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {

        User user = userRegistrationService.registerNewUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        AuthResponse response = new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                "User registered successfully"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        User user = authenticationService.authenticate(
                request.getEmail(),
                request.getPassword()
        );

        AuthResponse response = new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                "Login successful"
        );

        return ResponseEntity.ok(response);
    }
}