package com.scalecart.auth.controller;

import com.scalecart.auth.dto.AuthResponse;
import com.scalecart.auth.dto.LoginRequest;
import com.scalecart.auth.dto.RegisterRequest;
import com.scalecart.auth.entity.RefreshToken;
import com.scalecart.auth.entity.User;
import com.scalecart.auth.service.AuthenticationService;
import com.scalecart.auth.service.JwtService;
import com.scalecart.auth.service.RefreshTokenService;
import com.scalecart.auth.service.UserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRegistrationService userRegistrationService;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserRegistrationService userRegistrationService,
                          AuthenticationService authenticationService,
                          JwtService jwtService,
                          RefreshTokenService refreshTokenService) {
        this.userRegistrationService = userRegistrationService;
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

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
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        User user = authenticationService.authenticate(
                request.getEmail(),
                request.getPassword()
        );

        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                Map.of(
                        "roles",
                        user.getRoles().stream()
                                .map(r -> r.getName())
                                .toList()
                )
        );

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        AuthResponse response = new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                "Login successful"
        );

        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken.getToken());

        return ResponseEntity.ok(response);
    }
}