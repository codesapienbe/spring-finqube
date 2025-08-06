package com.finqube.iso20022.core.security.config;

/**
 * Exception thrown when configuration encryption operations fail.
 *
 * <p>This exception is thrown when there are problems with encrypting or
 * decrypting configuration values, such as invalid keys, unsupported algorithms,
 * or corrupted data.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class ConfigurationEncryptionException extends RuntimeException {

    private final String serviceId;
    private final String configurationKey;
    private final ConfigurationEncryptionErrorType errorType;

    /**
     * Constructs a new ConfigurationEncryptionException with the specified detail message.
     *
     * @param message the detail message
     */
    public ConfigurationEncryptionException(String message) {
        super(message);
        this.serviceId = null;
        this.configurationKey = null;
        this.errorType = ConfigurationEncryptionErrorType.GENERAL;
    }

    /**
     * Constructs a new ConfigurationEncryptionException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ConfigurationEncryptionException(String message, Throwable cause) {
        super(message, cause);
        this.serviceId = null;
        this.configurationKey = null;
        this.errorType = ConfigurationEncryptionErrorType.GENERAL;
    }

    /**
     * Constructs a new ConfigurationEncryptionException with configuration details.
     *
     * @param message the detail message
     * @param serviceId the service identifier
     * @param configurationKey the configuration key being processed
     * @param errorType the type of configuration encryption error
     */
    public ConfigurationEncryptionException(String message, String serviceId, String configurationKey, ConfigurationEncryptionErrorType errorType) {
        super(message);
        this.serviceId = serviceId;
        this.configurationKey = configurationKey;
        this.errorType = errorType;
    }

    /**
     * Constructs a new ConfigurationEncryptionException with configuration details and cause.
     *
     * @param message the detail message
     * @param serviceId the service identifier
     * @param configurationKey the configuration key being processed
     * @param errorType the type of configuration encryption error
     * @param cause the cause
     */
    public ConfigurationEncryptionException(String message, String serviceId, String configurationKey, ConfigurationEncryptionErrorType errorType, Throwable cause) {
        super(message, cause);
        this.serviceId = serviceId;
        this.configurationKey = configurationKey;
        this.errorType = errorType;
    }

    /**
     * Gets the service identifier.
     *
     * @return the service identifier, or null if not available
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Gets the configuration key being processed.
     *
     * @return the configuration key, or null if not available
     */
    public String getConfigurationKey() {
        return configurationKey;
    }

    /**
     * Gets the type of configuration encryption error.
     *
     * @return the error type
     */
    public ConfigurationEncryptionErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return "ConfigurationEncryptionException{" +
                "message='" + getMessage() + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", configurationKey='" + configurationKey + '\'' +
                ", errorType=" + errorType +
                '}';
    }

    /**
     * Configuration encryption error types.
     */
    public enum ConfigurationEncryptionErrorType {
        GENERAL("General", "General configuration encryption error"),
        INVALID_KEY("Invalid Key", "Invalid encryption key"),
        INVALID_ALGORITHM("Invalid Algorithm", "Unsupported or invalid encryption algorithm"),
        ENCRYPTION_FAILED("Encryption Failed", "Configuration value encryption failed"),
        DECRYPTION_FAILED("Decryption Failed", "Configuration value decryption failed"),
        INVALID_FORMAT("Invalid Format", "Invalid encrypted value format"),
        KEY_NOT_FOUND("Key Not Found", "Encryption key not found"),
        PERMISSION_DENIED("Permission Denied", "Insufficient permissions for configuration encryption");

        private final String displayName;
        private final String description;

        ConfigurationEncryptionErrorType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
