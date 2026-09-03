package com.scalecart.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalecart.auth.dto.AuthResponse;
import com.scalecart.auth.dto.LoginRequest;
import com.scalecart.auth.dto.RegisterRequest;
import com.scalecart.auth.entity.User;
import com.scalecart.auth.service.AuthenticationService;
import com.scalecart.auth.service.JwtService;
import com.scalecart.auth.service.RefreshTokenService;
import com.scalecart.auth.service.UserDetailsServiceImpl;
import com.scalecart.auth.service.UserRegistrationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(AuthController.class) — loads ONLY AuthController
// and the web layer. Nothing else.
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController Web Layer Tests")
class AuthControllerTest {

    // MockMvc: simulates HTTP requests without a real server
    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper: converts Java objects to JSON strings for request bodies
    @Autowired
    private ObjectMapper objectMapper;

    // @MockBean: creates a mock AND registers it as a Spring bean
    // AuthController needs these — without them Spring can't wire it up
    @MockBean
    private UserRegistrationService userRegistrationService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    // ── Register endpoint tests ────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/register — 201 Created on valid request")
    void register_ValidRequest_Returns201() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("Password123");

        User mockUser = new User("testuser", "test@example.com", "hashed");
        mockUser.setId(1L);
        mockUser.setRoles(new HashSet<>());

        when(userRegistrationService.registerNewUser(
                anyString(), anyString(), anyString()))
                .thenReturn(mockUser);

        // Act + Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // HTTP status
                .andExpect(status().isCreated())
                // Response body fields
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.message")
                        .value("User registered successfully"))
                // Password must NEVER appear in response
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/auth/register — 400 Bad Request on blank username")
    void register_BlankUsername_Returns400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("");           // blank — violates @NotBlank
        request.setEmail("test@example.com");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                // GlobalExceptionHandler formats this
                .andExpect(jsonPath("$.error")
                        .value("Validation Failed"))
                .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    @DisplayName("POST /api/auth/register — 400 on invalid email format")
    void register_InvalidEmail_Returns400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("not-an-email");  // violates @Email
        request.setPassword("Password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]")
                        .value("Email must be a valid email address"));
    }

    @Test
    @DisplayName("POST /api/auth/register — 400 on short password")
    void register_ShortPassword_Returns400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("short");  // less than 8 chars — violates @Size

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register — 409 on duplicate email")
    void register_DuplicateEmail_Returns409() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("existing@example.com");
        request.setPassword("Password123");

        // Service throws IllegalStateException for duplicate
        when(userRegistrationService.registerNewUser(
                anyString(), anyString(), anyString()))
                .thenThrow(new IllegalStateException(
                        "Email already registered: existing@example.com"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // GlobalExceptionHandler maps IllegalStateException → 409
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    // ── Login endpoint tests ───────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/login — 200 OK with tokens on valid credentials")
    void login_ValidCredentials_Returns200WithTokens() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password123");

        User mockUser = new User("testuser", "test@example.com", "hashed");
        mockUser.setId(1L);
        mockUser.setRoles(new HashSet<>());

        when(authenticationService.authenticate(anyString(), anyString()))
                .thenReturn(mockUser);
        when(jwtService.generateAccessToken(anyString(), any()))
                .thenReturn("eyJhbGc.mockJwtToken.signature");
        when(refreshTokenService.createRefreshToken(any()))
                .thenReturn(new com.scalecart.auth.entity.RefreshToken(
                        "mock-refresh-uuid",
                        mockUser,
                        java.time.LocalDateTime.now().plusDays(7)));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("eyJhbGc.mockJwtToken.signature"))
                .andExpect(jsonPath("$.refreshToken")
                        .value("mock-refresh-uuid"))
                // Password must NEVER appear in login response either
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/auth/login — 401 on wrong credentials")
    void login_WrongCredentials_Returns401() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("WrongPassword");

        when(authenticationService.authenticate(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException(
                        "Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // GlobalExceptionHandler maps IllegalArgumentException → 401
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error")
                        .value("Authentication Failed"));
    }

    @Test
    @DisplayName("POST /api/auth/login — 400 on missing email")
    void login_MissingEmail_Returns400() throws Exception {
        LoginRequest request = new LoginRequest();
        // email not set — null → @NotBlank fails
        request.setPassword("Password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Validation Failed"));
    }
}