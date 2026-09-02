package com.scalecart.auth.service;

import com.scalecart.auth.entity.RefreshToken;
import com.scalecart.auth.entity.User;
import com.scalecart.auth.repository.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService Tests")
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("Should create refresh token and persist it")
    void createRefreshToken_Success() {
        // Inject @Value field manually — @Value doesn't work in pure unit tests
        ReflectionTestUtils.setField(
                refreshTokenService, "refreshTokenExpiryDays", 7L);

        User user = new User("testuser", "test@example.com", "hashed");
        user.setId(1L);

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(inv -> {
                    RefreshToken token = inv.getArgument(0);
                    token.setId(1L);
                    return token;
                });

        RefreshToken result = refreshTokenService.createRefreshToken(user);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isNotBlank();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getExpiryDate())
                .isAfter(LocalDateTime.now());

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Should return token when valid and not expired")
    void validateRefreshToken_ValidToken_ReturnsToken() {
        RefreshToken token = new RefreshToken(
                "valid-uuid-token",
                new User(),
                LocalDateTime.now().plusDays(7)  // expires in future
        );

        when(refreshTokenRepository.findByToken("valid-uuid-token"))
                .thenReturn(Optional.of(token));

        RefreshToken result = refreshTokenService
                .validateRefreshToken("valid-uuid-token");

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("valid-uuid-token");
    }

    @Test
    @DisplayName("Should throw exception when token is expired")
    void validateRefreshToken_ExpiredToken_ThrowsException() {
        RefreshToken expiredToken = new RefreshToken(
                "expired-token",
                new User(),
                LocalDateTime.now().minusDays(1)  // expired yesterday
        );

        when(refreshTokenRepository.findByToken("expired-token"))
                .thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() ->
                refreshTokenService.validateRefreshToken("expired-token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid or expired");
    }

    @Test
    @DisplayName("Should throw exception when token not found")
    void validateRefreshToken_NotFound_ThrowsException() {
        when(refreshTokenRepository.findByToken("nonexistent"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                refreshTokenService.validateRefreshToken("nonexistent"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should delete token when logout")
    void deleteRefreshToken_CallsRepository() {
        doNothing().when(refreshTokenRepository)
                .deleteByToken("some-token");

        refreshTokenService.deleteRefreshToken("some-token");

        verify(refreshTokenRepository).deleteByToken("some-token");
    }
}