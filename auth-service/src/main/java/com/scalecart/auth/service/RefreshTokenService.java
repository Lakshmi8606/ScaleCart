package com.scalecart.auth.service;

import com.scalecart.auth.entity.RefreshToken;
import com.scalecart.auth.entity.User;
import com.scalecart.auth.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiry-days}")
    private long refreshTokenExpiryDays;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = new RefreshToken(
                UUID.randomUUID().toString(),   // random, unguessable token
                user,
                LocalDateTime.now().plusDays(refreshTokenExpiryDays)
        );
        return refreshTokenRepository.save(token);
    }

    public RefreshToken validateRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new IllegalArgumentException("Refresh token is invalid or expired"));
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}