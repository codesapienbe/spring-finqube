package com.finqube.iso20022.core.security.key;

/**
 * Exception thrown when key rotation operations fail.
 *
 * <p>This exception provides detailed error information for key rotation
 * failures, including the specific operation that failed and the underlying cause.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class KeyRotationException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String operation;
    private final String keyId;

    /**
     * Constructs a new KeyRotationException with the specified detail message.
     *
     * @param message the detail message
     */
    public KeyRotationException(String message) {
        super(message);
        this.operation = null;
        this.keyId = null;
    }

    /**
     * Constructs a new KeyRotationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public KeyRotationException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.keyId = null;
    }

    /**
     * Constructs a new KeyRotationException with the specified detail message, operation, and key ID.
     *
     * @param message the detail message
     * @param operation the operation that failed
     * @param keyId the key identifier involved in the operation
     */
    public KeyRotationException(String message, String operation, String keyId) {
        super(message);
        this.operation = operation;
        this.keyId = keyId;
    }

    /**
     * Constructs a new KeyRotationException with the specified detail message, cause, operation, and key ID.
     *
     * @param message the detail message
     * @param cause the cause
     * @param operation the operation that failed
     * @param keyId the key identifier involved in the operation
     */
    public KeyRotationException(String message, Throwable cause, String operation, String keyId) {
        super(message, cause);
        this.operation = operation;
        this.keyId = keyId;
    }

    /**
     * Gets the operation that failed.
     *
     * @return the operation, or null if not specified
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Gets the key identifier involved in the operation.
     *
     * @return the key identifier, or null if not specified
     */
    public String getKeyId() {
        return keyId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("KeyRotationException");

        if (operation != null || keyId != null) {
            sb.append(" [");
            if (operation != null) {
                sb.append("operation=").append(operation);
            }
            if (keyId != null) {
                if (operation != null) {
                    sb.append(", ");
                }
                sb.append("keyId=").append(keyId);
            }
            sb.append("]");
        }

        sb.append(": ").append(getMessage());

        if (getCause() != null) {
            sb.append("; caused by: ").append(getCause().getMessage());
        }

        return sb.toString();
    }
}
