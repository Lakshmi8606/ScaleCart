package com.scalecart.payment;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacTempTest {

    public static void main(String[] args) throws Exception {

        String secret = "scalecart-webhook-secret-2024";

        String body = "{\"event\":\"payment.success\",\"paymentId\":2,\"gatewayTransactionId\":\"rzp_txn_rabbitmq_test\"}";

        Mac mac = Mac.getInstance("HmacSHA256");

        mac.init(new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        ));

        byte[] hash = mac.doFinal(
                body.getBytes(StandardCharsets.UTF_8)
        );

        String signature = HexFormat.of().formatHex(hash);

        System.out.println("========== HMAC TEST ==========");
        System.out.println("Signature = " + signature);
        System.out.println("\nBody:");
        System.out.println(body);
        System.out.println("===============================");
    }
}