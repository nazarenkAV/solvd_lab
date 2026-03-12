package com.zebrunner.automation.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class WebhooksUtil {

    public static final String SIGNING_ALGORITHM = "HmacSHA256";

    public static String generateSecretKey() {
        byte[] secretKey = new byte[64];

        try {
            SecureRandom.getInstanceStrong().nextBytes(secretKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return Base64.getEncoder().encodeToString(secretKey);
    }

    public static String calculateSignature(String webhookUrl,
                                            String secretKeyBase64,
                                            String requestTimestamp) {
        byte[] secretKeyBytes = Arrays.copyOf(Base64.getDecoder().decode(secretKeyBase64), 64);
        Key secretKey = new SecretKeySpec(secretKeyBytes, SIGNING_ALGORITHM);

        Mac mac = null;
        try {
            mac = Mac.getInstance(SIGNING_ALGORITHM);
            mac.init(secretKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        byte[] messageBytes = (webhookUrl + requestTimestamp).getBytes(StandardCharsets.UTF_8);

        byte[] signatureBytes = mac.doFinal(messageBytes);
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

}
