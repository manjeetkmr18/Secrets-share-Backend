package com.OnePassLink.backend.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class IdGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int ID_LENGTH_BYTES = 16; // 128 bits entropy

    /**
     * Generates a cryptographically secure, URL-safe random ID with 128 bits of entropy
     * @return Base64 URL-safe encoded string (22 characters)
     */
    public String generateId() {
        byte[] randomBytes = new byte[ID_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
