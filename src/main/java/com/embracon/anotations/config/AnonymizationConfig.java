package com.embracon.anotations.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnonymizationConfig {

    @Value("${anonymization.secret-key}")
    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }
}