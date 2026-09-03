package com.scalecart.payment.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class WebhookSignatureValidator {

    private static final Logger log =
            LoggerFactory.getLogger(WebhookSignatureValidator.class);

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Value("${payment.webhook.secret}")
    private String webhookSecret;

    public boolean isValidSignature(String rawBody, String receivedSignature) {
        try {
            String computedSignature = computeHmacSha256(rawBody);
            return constantTimeEquals(computedSignature, receivedSignature);
        } catch (Exception e) {
            log.warn("Signature validation failed: {}", e.getMessage());
            return false;
        }
    }

    private String computeHmacSha256(String data)
            throws NoSuchAlgorithmException, InvalidKeyException {

        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(
                webhookSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_ALGORITHM
        );
        mac.init(secretKey);

        byte[] hmacBytes = mac.doFinal(
                data.getBytes(StandardCharsets.UTF_8));

        return HexFormat.of().formatHex(hmacBytes);
    }

    // Constant-time compare to prevent timing attacks
    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
