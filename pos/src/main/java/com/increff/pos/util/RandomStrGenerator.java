package com.increff.pos.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class RandomStrGenerator {
    public static String usingUUID(int length) {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("NativePRNG");
            String allChars = UUID.randomUUID().toString().replace("-", "");
            char[] otp = new char[length];
            for (int i = 0; i < length; i++) {
                otp[i] =
                        allChars.charAt(secureRandom.nextInt(allChars.length()));
            }
            return String.valueOf(otp);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}