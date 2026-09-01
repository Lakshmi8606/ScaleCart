package com.scalecart.auth.service;

import com.scalecart.auth.entity.Role;
import com.scalecart.auth.entity.User;
import com.scalecart.auth.repository.RoleRepository;
import com.scalecart.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class) = use Mockito in this test class
// No Spring context loaded — pure unit test, very fast
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRegistrationService Tests")
class UserRegistrationServiceTest {

    // @Mock = create a fake implementation of this interface
    // Mockito generates a proxy that returns null/empty by default
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // @InjectMocks = create real instance of this class
    // and inject the @Mock fields into its constructor automatically
    @InjectMocks
    private UserRegistrationService userRegistrationService;

    // Reusable test data — created fresh before each test
    private Role defaultRole;

    @BeforeEach
    void setUp() {
        defaultRole = new Role("ROLE_USER");
        defaultRole.setId(1L);
    }

    // ── Happy path tests ───────────────────────────────────────────────

    @Test
    @DisplayName("Should register new user successfully when email and username are unique")
    void registerNewUser_Success() {
        // ── Arrange ────────────────────────────────────────────────────
        String username = "laxdev";
        String email    = "lax@example.com";
        String password = "SecurePass123";

        // Tell the mock what to return when called
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(Optional.of(defaultRole));
        when(passwordEncoder.encode(password))
                .thenReturn("$2a$10$hashedpassword");

        // When save() is called, return a user with an ID (simulating DB insert)
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);   // simulate auto-generated ID
            return user;
        });

        // ── Act ────────────────────────────────────────────────────────
        User result = userRegistrationService.registerNewUser(
                username, email, password);

        // ── Assert ─────────────────────────────────────────────────────
        // AssertJ's fluent assertions — more readable than JUnit assertEquals
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getUsername()).isEqualTo(username);

        // CRITICAL: password must NEVER be stored as plain text
        assertThat(result.getPassword()).isNotEqualTo(password);
        assertThat(result.getPassword()).isEqualTo("$2a$10$hashedpassword");

        // Verify interactions — did the service call what we expected?
        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, times(1)).existsByUsername(username);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ── Failure path tests ─────────────────────────────────────────────

    @Test
    @DisplayName("Should throw IllegalStateException when email already exists")
    void registerNewUser_DuplicateEmail_ThrowsException() {
        // Arrange
        when(userRepository.existsByEmail("existing@example.com"))
                .thenReturn(true); // email already in DB

        // Act + Assert — assertThatThrownBy catches the exception
        assertThatThrownBy(() ->
                userRegistrationService.registerNewUser(
                        "newuser", "existing@example.com", "password123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Email already registered");

        // Verify save() was NEVER called — no partial registration
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when username already taken")
    void registerNewUser_DuplicateUsername_ThrowsException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername("takenname")).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() ->
                userRegistrationService.registerNewUser(
                        "takenname", "new@example.com", "password123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Username already taken");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when ROLE_USER not found in DB")
    void registerNewUser_RoleNotFound_ThrowsException() {
        // Arrange — simulate missing role (bad DB state)
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(Optional.empty()); // role missing!

        // Act + Assert
        assertThatThrownBy(() ->
                userRegistrationService.registerNewUser(
                        "user", "user@example.com", "password"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Default role ROLE_USER not found");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should encode password before saving — never store plain text")
    void registerNewUser_PasswordIsEncoded() {
        // Arrange
        String rawPassword    = "MyPlainPassword";
        String encodedPassword = "$2a$10$encodedversion";

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(Optional.of(defaultRole));
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        // Act
        User result = userRegistrationService.registerNewUser(
                "user", "user@example.com", rawPassword);

        // Assert — the saved password is the encoded version
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        assertThat(result.getPassword()).doesNotContain("MyPlainPassword");

        // Verify encode was called with the raw password exactly once
        verify(passwordEncoder).encode(rawPassword);
    }
}