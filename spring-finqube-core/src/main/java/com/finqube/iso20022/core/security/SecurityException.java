package com.finqube.iso20022.core.security;

/**
 * Exception thrown when security operations fail.
 *
 * <p>This exception is thrown when there are problems with security operations
 * such as encryption, decryption, signing, signature verification, or certificate management.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class SecurityException extends RuntimeException {

    private final String securityManagerId;
    private final String messageId;
    private final SecurityErrorType errorType;

    /**
     * Constructs a new SecurityException with the specified detail message.
     *
     * @param message the detail message
     */
    public SecurityException(String message) {
        super(message);
        this.securityManagerId = null;
        this.messageId = null;
        this.errorType = SecurityErrorType.GENERAL;
    }

    /**
     * Constructs a new SecurityException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public SecurityException(String message, Throwable cause) {
        super(message, cause);
        this.securityManagerId = null;
        this.messageId = null;
        this.errorType = SecurityErrorType.GENERAL;
    }

    /**
     * Constructs a new SecurityException with security details.
     *
     * @param message the detail message
     * @param securityManagerId the security manager identifier
     * @param messageId the message identifier
     * @param errorType the type of security error
     */
    public SecurityException(String message, String securityManagerId, String messageId, SecurityErrorType errorType) {
        super(message);
        this.securityManagerId = securityManagerId;
        this.messageId = messageId;
        this.errorType = errorType;
    }

    /**
     * Constructs a new SecurityException with security details and cause.
     *
     * @param message the detail message
     * @param securityManagerId the security manager identifier
     * @param messageId the message identifier
     * @param errorType the type of security error
     * @param cause the cause
     */
    public SecurityException(String message, String securityManagerId, String messageId, SecurityErrorType errorType, Throwable cause) {
        super(message, cause);
        this.securityManagerId = securityManagerId;
        this.messageId = messageId;
        this.errorType = errorType;
    }

    /**
     * Gets the security manager identifier.
     *
     * @return the security manager identifier, or null if not available
     */
    public String getSecurityManagerId() {
        return securityManagerId;
    }

    /**
     * Gets the message identifier.
     *
     * @return the message identifier, or null if not available
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Gets the type of security error.
     *
     * @return the error type
     */
    public SecurityErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return "SecurityException{" +
                "message='" + getMessage() + '\'' +
                ", securityManagerId='" + securityManagerId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", errorType=" + errorType +
                '}';
    }

    /**
     * Security error types.
     */
    public enum SecurityErrorType {
        GENERAL("General", "General security error"),
        INVALID_INPUT("Invalid Input", "Invalid input parameters"),
        ENCRYPTION("Encryption", "Encryption operation failed"),
        DECRYPTION("Decryption", "Decryption operation failed"),
        SIGNING("Signing", "Digital signing operation failed"),
        VERIFICATION("Verification", "Signature verification failed"),
        CERTIFICATE("Certificate", "Certificate-related error"),
        KEY_MANAGEMENT("Key Management", "Key management error"),
        ALGORITHM("Algorithm", "Cryptographic algorithm error"),
        ACCESS_CONTROL("Access Control", "Access control violation");

        private final String displayName;
        private final String description;

        SecurityErrorType(String displayName, String description) {
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
