package com.finqube.iso20022.core.security.storage;

/**
 * Exception thrown when key storage operations fail.
 *
 * <p>This exception is thrown when there are problems with key storage operations
 * such as storing, retrieving, updating, or deleting cryptographic keys.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class KeyStorageException extends RuntimeException {

    private final String storageServiceId;
    private final String keyId;
    private final KeyStorageErrorType errorType;

    /**
     * Constructs a new KeyStorageException with the specified detail message.
     *
     * @param message the detail message
     */
    public KeyStorageException(String message) {
        super(message);
        this.storageServiceId = null;
        this.keyId = null;
        this.errorType = KeyStorageErrorType.GENERAL;
    }

    /**
     * Constructs a new KeyStorageException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public KeyStorageException(String message, Throwable cause) {
        super(message, cause);
        this.storageServiceId = null;
        this.keyId = null;
        this.errorType = KeyStorageErrorType.GENERAL;
    }

    /**
     * Constructs a new KeyStorageException with storage details.
     *
     * @param message the detail message
     * @param storageServiceId the storage service identifier
     * @param keyId the key identifier
     * @param errorType the type of storage error
     */
    public KeyStorageException(String message, String storageServiceId, String keyId, KeyStorageErrorType errorType) {
        super(message);
        this.storageServiceId = storageServiceId;
        this.keyId = keyId;
        this.errorType = errorType;
    }

    /**
     * Constructs a new KeyStorageException with storage details and cause.
     *
     * @param message the detail message
     * @param storageServiceId the storage service identifier
     * @param keyId the key identifier
     * @param errorType the type of storage error
     * @param cause the cause
     */
    public KeyStorageException(String message, String storageServiceId, String keyId, KeyStorageErrorType errorType, Throwable cause) {
        super(message, cause);
        this.storageServiceId = storageServiceId;
        this.keyId = keyId;
        this.errorType = errorType;
    }

    /**
     * Gets the storage service identifier.
     *
     * @return the storage service identifier, or null if not available
     */
    public String getStorageServiceId() {
        return storageServiceId;
    }

    /**
     * Gets the key identifier.
     *
     * @return the key identifier, or null if not available
     */
    public String getKeyId() {
        return keyId;
    }

    /**
     * Gets the type of storage error.
     *
     * @return the error type
     */
    public KeyStorageErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return "KeyStorageException{" +
                "message='" + getMessage() + '\'' +
                ", storageServiceId='" + storageServiceId + '\'' +
                ", keyId='" + keyId + '\'' +
                ", errorType=" + errorType +
                '}';
    }

    /**
     * Key storage error types.
     */
    public enum KeyStorageErrorType {
        GENERAL("General", "General key storage error"),
        INVALID_INPUT("Invalid Input", "Invalid input parameters"),
        STORAGE_FAILED("Storage Failed", "Key storage operation failed"),
        RETRIEVAL_FAILED("Retrieval Failed", "Key retrieval operation failed"),
        UPDATE_FAILED("Update Failed", "Key update operation failed"),
        DELETE_FAILED("Delete Failed", "Key deletion operation failed"),
        KEY_NOT_FOUND("Key Not Found", "Key not found in storage"),
        KEY_EXISTS("Key Exists", "Key already exists in storage"),
        PERMISSION_DENIED("Permission Denied", "Insufficient permissions for key storage operation"),
        BACKUP_FAILED("Backup Failed", "Key backup operation failed"),
        RESTORE_FAILED("Restore Failed", "Key restore operation failed"),
        ENCRYPTION_FAILED("Encryption Failed", "Key encryption operation failed"),
        DECRYPTION_FAILED("Decryption Failed", "Key decryption operation failed");

        private final String displayName;
        private final String description;

        KeyStorageErrorType(String displayName, String description) {
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
