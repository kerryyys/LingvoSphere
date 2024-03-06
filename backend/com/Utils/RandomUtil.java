package com.lingvosphere.backend.Utils;

import java.security.SecureRandom;
import java.util.Random;

public class RandomUtil {
    private static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String generateToken(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            token.append(randomChar);
        }

        return token.toString();
    }
}
