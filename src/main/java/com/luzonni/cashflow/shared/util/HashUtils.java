package com.luzonni.cashflow.shared.util;

import io.quarkus.elytron.security.common.BcryptUtil;

public class HashUtils {

    public static String hash(String password) {
        return BcryptUtil.bcryptHash(password, 12);
    }

    public static boolean verify(String password, String hash) {
        return BcryptUtil.matches(password, hash);
    }

}
