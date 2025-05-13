package com.embracon.anotations.service;

import com.embracon.anotations.config.AnonymizationConfig;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AnonymizationService {

    private final AnonymizationConfig config;

    private static String SECRET_KEY;
    private static final String ALGORITHM = "AES";

    public AnonymizationService(AnonymizationConfig config) {
        this.config = config;
    }

    @PostConstruct
    private void init() {
        String key = config.getSecretKey();
        if (key == null || !(key.length() == 16 || key.length() == 24 || key.length() == 32)) {
            throw new IllegalArgumentException("Configuração 'anonymization.secret-key' inválida (precisa ter 16, 24 ou 32 caracteres)");
        }
        SECRET_KEY = key;
    }

    public static Object anonymize(Object original, Class<?> type) {
        if (original == null) return null;

        String valueAsString = original.toString();
        return encrypt(valueAsString);
    }

    public static String deAnonymize(String anonymized) {
        if (anonymized == null) return null;
        return decrypt(anonymized);
    }

    private static String encrypt(String input) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar valor", e);
        }
    }

    private static String decrypt(String encrypted) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getUrlDecoder().decode(encrypted);
            byte[] original = cipher.doFinal(decoded);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar valor", e);
        }
    }
}
