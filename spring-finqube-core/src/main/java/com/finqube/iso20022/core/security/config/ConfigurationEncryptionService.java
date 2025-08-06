package com.finqube.iso20022.core.security.config;

import com.finqube.iso20022.core.security.encryption.EncryptionAlgorithm;

/**
 * Service for encrypting and decrypting sensitive configuration values.
 *
 * <p>This service provides methods to encrypt sensitive configuration values
 * such as passwords, API keys, and other secrets, and decrypt them at runtime
 * for use by the application.</p>
 *
 * <p>The service supports multiple encryption algorithms and can be configured
 * to use different keys for different environments or security levels.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface ConfigurationEncryptionService {

    /**
     * Encrypts a sensitive configuration value.
     *
     * @param plaintext the plaintext value to encrypt
     * @param algorithm the encryption algorithm to use
     * @return the encrypted value (base64 encoded)
     * @throws ConfigurationEncryptionException if encryption fails
     */
    String encrypt(String plaintext, EncryptionAlgorithm algorithm) throws ConfigurationEncryptionException;

    /**
     * Encrypts a sensitive configuration value using the default algorithm.
     *
     * @param plaintext the plaintext value to encrypt
     * @return the encrypted value (base64 encoded)
     * @throws ConfigurationEncryptionException if encryption fails
     */
    String encrypt(String plaintext) throws ConfigurationEncryptionException;

    /**
     * Decrypts an encrypted configuration value.
     *
     * @param encryptedValue the encrypted value (base64 encoded)
     * @param algorithm the encryption algorithm used
     * @return the decrypted plaintext value
     * @throws ConfigurationEncryptionException if decryption fails
     */
    String decrypt(String encryptedValue, EncryptionAlgorithm algorithm) throws ConfigurationEncryptionException;

    /**
     * Decrypts an encrypted configuration value using the default algorithm.
     *
     * @param encryptedValue the encrypted value (base64 encoded)
     * @return the decrypted plaintext value
     * @throws ConfigurationEncryptionException if decryption fails
     */
    String decrypt(String encryptedValue) throws ConfigurationEncryptionException;

    /**
     * Checks if a value appears to be encrypted.
     *
     * @param value the value to check
     * @return true if the value appears to be encrypted, false otherwise
     */
    boolean isEncrypted(String value);

    /**
     * Gets the default encryption algorithm.
     *
     * @return the default algorithm
     */
    EncryptionAlgorithm getDefaultAlgorithm();

    /**
     * Gets the configuration encryption service identifier.
     *
     * @return the service identifier
     */
    String getServiceId();

    /**
     * Gets the configuration encryption service display name.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Gets the configuration encryption service version.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Checks if the configuration encryption service is available.
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();

    /**
     * Performs a health check on the configuration encryption service.
     *
     * @return the health check result
     */
    ConfigurationEncryptionHealthCheck healthCheck();
}
