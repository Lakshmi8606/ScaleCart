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

    /**
     * Validates that the webhook signature matches what we'd compute
     * from the raw request body using our shared secret.
     *
     * How Razorpay/Stripe do this:
     * 1. Gateway computes: HMAC-SHA256(rawBody, sharedSecret)
     * 2. Gateway sends computed signature in header (X-Razorpay-Signature)
     * 3. We compute: HMAC-SHA256(rawBody, sharedSecret) independently
     * 4. We compare the two — if they match, request is genuine
     */
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

        // Convert bytes to hex string for comparison
        return HexFormat.of().formatHex(hmacBytes);
    }

    /**
     * Constant-time string comparison.
     *
     * Why not just use .equals()?
     * Regular equality check returns false as soon as it finds
     * the first mismatched character. An attacker can measure
     * how long the comparison takes and deduce how many characters
     * of their forged signature are correct (timing attack).
     * Constant-time comparison always checks ALL characters
     * regardless of where the mismatch is — same time always.
     */
    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            // XOR: if chars match, result stays 0; any mismatch sets a bit
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}