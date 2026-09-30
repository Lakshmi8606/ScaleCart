package com.scalecart.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * JWT token service using RSA-256 asymmetric signing.
 *
 * <p>The private key (held only by auth-service) signs tokens.
 * The public key (distributed to all services) verifies tokens.
 * This means compromising any downstream service does not
 * allow token forgery — only the private key can sign.
 *
 * <p>Access tokens expire in 15 minutes.
 * Refresh tokens (stored in PostgreSQL) expire in 7 days.
 */
@Service
public class JwtService {

    @Value("${jwt.private-key-path}")
    private Resource privateKeyResource;

    @Value("${jwt.public-key-path}")
    private Resource publicKeyResource;

    @Value("${jwt.access-token-expiry-minutes}")
    private long accessTokenExpiryMinutes;

    public String generateAccessToken(String email, Map<String, Object> extraClaims) {
        try {
            PrivateKey privateKey = loadPrivateKey();
            Instant now = Instant.now();

            return Jwts.builder()
                    .subject(email)
                    .claims(extraClaims)
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(now.plus(accessTokenExpiryMinutes, ChronoUnit.MINUTES)))
                    .signWith(privateKey, Jwts.SIG.RS256)
                    .compact();

        } catch (IOException | GeneralSecurityException | JwtException e) {
            throw new IllegalStateException("Error generating JWT token", e);
        }
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Instant expiry = claims.getExpiration().toInstant();
            return expiry.isAfter(Instant.now());
        } catch (JwtException | IllegalArgumentException | IllegalStateException e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            PublicKey publicKey = loadPublicKey();
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException("Unable to load JWT public key", e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid or expired JWT token", e);
        }
    }

    private PrivateKey loadPrivateKey() throws IOException, GeneralSecurityException {
        String key = new String(privateKeyResource.getInputStream()
                .readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private PublicKey loadPublicKey() throws IOException, GeneralSecurityException {
        String key = new String(publicKeyResource.getInputStream()
                .readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}
