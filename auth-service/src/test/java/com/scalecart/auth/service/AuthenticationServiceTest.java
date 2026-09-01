package com.scalecart.auth.service;

import com.scalecart.auth.entity.User;
import com.scalecart.auth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService Tests")
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Should authenticate successfully with correct credentials")
    void authenticate_Success() {
        // Arrange
        String email    = "user@example.com";
        String rawPass  = "MyPassword123";
        String hashPass = "$2a$10$hashed";

        User user = new User("testuser", email, hashPass);
        user.setId(1L);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPass, hashPass))
                .thenReturn(true);

        // Act
        User result = authenticationService.authenticate(email, rawPass);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        verify(passwordEncoder).matches(rawPass, hashPass);
    }

    @Test
    @DisplayName("Should throw same error for wrong email and wrong password — prevents enumeration")
    void authenticate_WrongEmail_ThrowsSameMessageAsWrongPassword() {
        // Arrange — email doesn't exist
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() ->
                authenticationService.authenticate(
                        "nonexistent@example.com", "anypassword"))
                .isInstanceOf(IllegalArgumentException.class)
                // CRITICAL: same message regardless of whether email or password is wrong
                .hasMessage("Invalid email or password");
    }

    @Test
    @DisplayName("Should throw same error for correct email but wrong password")
    void authenticate_WrongPassword_ThrowsSameMessage() {
        // Arrange
        User user = new User("testuser", "user@example.com", "$2a$10$hashed");
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$hashed"))
                .thenReturn(false); // password doesn't match

        // Act + Assert
        assertThatThrownBy(() ->
                authenticationService.authenticate(
                        "user@example.com", "wrongpassword"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email or password");
        // Same message as wrong email — intentional security design
    }
}