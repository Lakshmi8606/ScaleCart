package com.scalecart.order.security;

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
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.public-key-path}")
    private Resource publicKeyResource;

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
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
            return Jwts.parser()
                    .verifyWith(loadPublicKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException("Unable to load JWT public key", e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    private PublicKey loadPublicKey() throws IOException, GeneralSecurityException {
        String key = new String(
                publicKeyResource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        return KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
    }
}
