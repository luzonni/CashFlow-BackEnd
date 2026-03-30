package com.luzonni.cashflow.shared.util;

import io.quarkus.elytron.security.common.BcryptUtil;

import java.security.MessageDigest;

public class HashUtils {

    public static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String hash(String password) {
        return BcryptUtil.bcryptHash(password, 12);
    }

    public static boolean verify(String password, String hash) {
        return BcryptUtil.matches(password, hash);
    }

}
