package com.embracon.anotations;

import com.embracon.anotations.service.AnonymizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class AnonymizationServiceTests {

    private static final Logger logger = LoggerFactory.getLogger(AnonymizationServiceTests.class);


    @BeforeEach
    public void initSecretKey() throws Exception {
        Field field = AnonymizationService.class.getDeclaredField("SECRET_KEY");
        field.setAccessible(true);
        field.set(null, "1234567890123456"); // deve ter 16, 24 ou 32 bytes
    }

    @Test
    public void testAnonymizeLong() {
        Long original = 123456789L;
        logger.info("Test Input - Long: {}", original);

        String anonymized = (String) AnonymizationService.anonymize(original, Long.class);
        logger.info("Anonymized Long: {}", anonymized);

        String deAnonymized = AnonymizationService.deAnonymize(anonymized);
        logger.info("De-anonymized Long: {}", deAnonymized);

        assertNotNull(anonymized);
        assertNotEquals(original.toString(), anonymized);
        assertEquals(original.toString(), deAnonymized);
    }

    @Test
    public void testAnonymizeInteger() {
        Integer original = 9876;
        logger.info("Test Input - Integer: {}", original);

        String anonymized = (String) AnonymizationService.anonymize(original, Integer.class);
        logger.info("Anonymized Integer: {}", anonymized);

        String deAnonymized = AnonymizationService.deAnonymize(anonymized);
        logger.info("De-anonymized Integer: {}", deAnonymized);

        assertNotNull(anonymized);
        assertNotEquals(original.toString(), anonymized);
        assertEquals(original.toString(), deAnonymized);
    }

    @Test
    public void testAnonymizeString() {
        String original = "example";
        logger.info("Test Input - String: {}", original);

        String anonymized = (String) AnonymizationService.anonymize(original, String.class);
        logger.info("Anonymized String: {}", anonymized);

        String deAnonymized = AnonymizationService.deAnonymize(anonymized);
        logger.info("De-anonymized String: {}", deAnonymized);

        assertNotNull(anonymized);
        assertNotEquals(original, anonymized);
        assertEquals(original, deAnonymized);
    }
}
