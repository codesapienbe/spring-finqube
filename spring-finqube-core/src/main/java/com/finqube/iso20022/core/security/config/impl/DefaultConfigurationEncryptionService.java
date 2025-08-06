package com.finqube.iso20022.core.security.config.impl;

import com.finqube.iso20022.core.security.config.ConfigurationEncryptionException;
import com.finqube.iso20022.core.security.config.ConfigurationEncryptionHealthCheck;
import com.finqube.iso20022.core.security.config.ConfigurationEncryptionService;
import com.finqube.iso20022.core.security.encryption.EncryptionAlgorithm;
import com.finqube.iso20022.core.security.audit.AuditLogger;
import com.finqube.iso20022.core.security.audit.AuditLogLevel;
import com.finqube.iso20022.core.security.audit.RiskLevel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default implementation of ConfigurationEncryptionService using AES-GCM encryption.
 *
 * <p>This implementation provides secure encryption and decryption of configuration
 * values using AES-GCM with a 256-bit key. Encrypted values are base64 encoded
 * and prefixed with an identifier to distinguish them from plaintext values.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Component
public class DefaultConfigurationEncryptionService implements ConfigurationEncryptionService {

    private static final Logger log = LoggerFactory.getLogger(DefaultConfigurationEncryptionService.class);
    private static final String ENCRYPTED_PREFIX = "ENC(";
    private static final String ENCRYPTED_SUFFIX = ")";
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final int KEY_SIZE = 256;

    private final String serviceId = UUID.randomUUID().toString();
    private final String version = "0.1.0";
    private final SecretKey secretKey;
    private final SecureRandom secureRandom;

    private final AtomicLong encryptionCount = new AtomicLong(0);
    private final AtomicLong decryptionCount = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);

    private final AuditLogger auditLogger;

    @Autowired
    public DefaultConfigurationEncryptionService(
            @Value("${iso20022.security.config.encryption.key:#{null}}") String encryptionKey,
            AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
        this.secureRandom = new SecureRandom();

        if (encryptionKey != null && !encryptionKey.trim().isEmpty()) {
            // Use provided key
            this.secretKey = new SecretKeySpec(Base64.getDecoder().decode(encryptionKey), "AES");
            log.info("Configuration encryption service initialized with provided key");
        } else {
            // Generate new key
            this.secretKey = generateSecretKey();
            log.warn("Configuration encryption service initialized with generated key. " +
                    "For production use, set iso20022.security.config.encryption.key property");
        }
    }

    @Override
    public String encrypt(String plaintext, EncryptionAlgorithm algorithm) throws ConfigurationEncryptionException {
        if (plaintext == null) {
            return null;
        }

        long startTime = System.currentTimeMillis();
        try {
            byte[] encrypted = encryptBytes(plaintext.getBytes());
            String result = ENCRYPTED_PREFIX + Base64.getEncoder().encodeToString(encrypted) + ENCRYPTED_SUFFIX;

            encryptionCount.incrementAndGet();
            long processingTime = System.currentTimeMillis() - startTime;

                        // Log encryption event
            auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "CONFIG_ENCRYPT",
                    "Configuration value encrypted", AuditLogLevel.SECURITY, RiskLevel.LOW);

            log.debug("Configuration value encrypted successfully in {}ms", processingTime);
            return result;

        } catch (Exception e) {
            errorCount.incrementAndGet();
            long processingTime = System.currentTimeMillis() - startTime;

                        // Log encryption failure
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "CONFIG_ENCRYPT",
                    "Configuration encryption failed: " + e.getMessage(),
                    AuditLogLevel.ERROR, RiskLevel.HIGH);

            log.error("Configuration encryption failed in {}ms: {}", processingTime, e.getMessage());
            throw new ConfigurationEncryptionException("Configuration encryption failed", serviceId, null,
                    ConfigurationEncryptionException.ConfigurationEncryptionErrorType.ENCRYPTION_FAILED, e);
        }
    }

    @Override
    public String encrypt(String plaintext) throws ConfigurationEncryptionException {
        return encrypt(plaintext, EncryptionAlgorithm.AES_256_GCM);
    }

    @Override
    public String decrypt(String encryptedValue, EncryptionAlgorithm algorithm) throws ConfigurationEncryptionException {
        if (encryptedValue == null) {
            return null;
        }

        if (!isEncrypted(encryptedValue)) {
            return encryptedValue; // Return as-is if not encrypted
        }

        long startTime = System.currentTimeMillis();
        try {
            // Remove prefix and suffix
            String base64Data = encryptedValue.substring(ENCRYPTED_PREFIX.length(),
                    encryptedValue.length() - ENCRYPTED_SUFFIX.length());
            byte[] encrypted = Base64.getDecoder().decode(base64Data);

            byte[] decrypted = decryptBytes(encrypted);
            String result = new String(decrypted);

            decryptionCount.incrementAndGet();
            long processingTime = System.currentTimeMillis() - startTime;

                        // Log decryption event
            auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "CONFIG_DECRYPT",
                    "Configuration value decrypted", AuditLogLevel.SECURITY, RiskLevel.LOW);

            log.debug("Configuration value decrypted successfully in {}ms", processingTime);
            return result;

        } catch (Exception e) {
            errorCount.incrementAndGet();
            long processingTime = System.currentTimeMillis() - startTime;

                        // Log decryption failure
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "CONFIG_DECRYPT",
                    "Configuration decryption failed: " + e.getMessage(),
                    AuditLogLevel.ERROR, RiskLevel.HIGH);

            log.error("Configuration decryption failed in {}ms: {}", processingTime, e.getMessage());
            throw new ConfigurationEncryptionException("Configuration decryption failed", serviceId, null,
                    ConfigurationEncryptionException.ConfigurationEncryptionErrorType.DECRYPTION_FAILED, e);
        }
    }

    @Override
    public String decrypt(String encryptedValue) throws ConfigurationEncryptionException {
        return decrypt(encryptedValue, EncryptionAlgorithm.AES_256_GCM);
    }

    @Override
    public boolean isEncrypted(String value) {
        return value != null && value.startsWith(ENCRYPTED_PREFIX) && value.endsWith(ENCRYPTED_SUFFIX);
    }

    @Override
    public EncryptionAlgorithm getDefaultAlgorithm() {
        return EncryptionAlgorithm.AES_256_GCM;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public String getDisplayName() {
        return "Default Configuration Encryption Service";
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isAvailable() {
        return secretKey != null;
    }

    @Override
    public ConfigurationEncryptionHealthCheck healthCheck() {
        boolean healthy = isAvailable() && errorCount.get() < 100;
        String status = healthy ? "HEALTHY" : "UNHEALTHY";
        String message = healthy ? "Configuration encryption service operational" :
                "Configuration encryption service has issues";

        return new ConfigurationEncryptionHealthCheck(serviceId, LocalDateTime.now(), status, message,
                healthy, encryptionCount.get(), decryptionCount.get(), errorCount.get(), 0.0);
    }

    private byte[] encryptBytes(byte[] plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

        byte[] ciphertext = cipher.doFinal(plaintext);
        byte[] result = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(ciphertext, 0, result, iv.length, ciphertext.length);

        return result;
    }

    private byte[] decryptBytes(byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] ciphertext = new byte[encrypted.length - GCM_IV_LENGTH];

        System.arraycopy(encrypted, 0, iv, 0, GCM_IV_LENGTH);
        System.arraycopy(encrypted, GCM_IV_LENGTH, ciphertext, 0, ciphertext.length);

        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

        return cipher.doFinal(ciphertext);
    }

    private SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(KEY_SIZE, secureRandom);
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new ConfigurationEncryptionException("Failed to generate secret key", serviceId, null,
                    ConfigurationEncryptionException.ConfigurationEncryptionErrorType.INVALID_KEY, e);
        }
    }
}
