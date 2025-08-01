package com.finqube.iso20022.core.security.hsm;

/**
 * Exception thrown when HSM operations fail.
 *
 * <p>This exception is used to wrap and provide context for errors that occur
 * during HSM operations such as key management, signing, encryption, and
 * certificate operations.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class HsmException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String operation;
    private final String keyId;
    private final HsmProvider.OperationStatus status;

    /**
     * Constructs a new HSM exception with the specified detail message.
     *
     * @param message the detail message
     */
    public HsmException(String message) {
        super(message);
        this.operation = null;
        this.keyId = null;
        this.status = HsmProvider.OperationStatus.FAILED;
    }

    /**
     * Constructs a new HSM exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public HsmException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.keyId = null;
        this.status = HsmProvider.OperationStatus.FAILED;
    }

    /**
     * Constructs a new HSM exception with the specified detail message, operation, and key ID.
     *
     * @param message the detail message
     * @param operation the operation that failed
     * @param keyId the key ID involved in the operation
     */
    public HsmException(String message, String operation, String keyId) {
        super(message);
        this.operation = operation;
        this.keyId = keyId;
        this.status = HsmProvider.OperationStatus.FAILED;
    }

    /**
     * Constructs a new HSM exception with the specified detail message, operation, key ID, and cause.
     *
     * @param message the detail message
     * @param operation the operation that failed
     * @param keyId the key ID involved in the operation
     * @param cause the cause
     */
    public HsmException(String message, String operation, String keyId, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.keyId = keyId;
        this.status = HsmProvider.OperationStatus.FAILED;
    }

    /**
     * Constructs a new HSM exception with the specified detail message, operation, key ID, and status.
     *
     * @param message the detail message
     * @param operation the operation that failed
     * @param keyId the key ID involved in the operation
     * @param status the operation status
     */
    public HsmException(String message, String operation, String keyId, HsmProvider.OperationStatus status) {
        super(message);
        this.operation = operation;
        this.keyId = keyId;
        this.status = status;
    }

    /**
     * Constructs a new HSM exception with the specified detail message, operation, key ID, status, and cause.
     *
     * @param message the detail message
     * @param operation the operation that failed
     * @param keyId the key ID involved in the operation
     * @param status the operation status
     * @param cause the cause
     */
    public HsmException(String message, String operation, String keyId, HsmProvider.OperationStatus status, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.keyId = keyId;
        this.status = status;
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
     * Gets the key ID involved in the operation.
     *
     * @return the key ID, or null if not specified
     */
    public String getKeyId() {
        return keyId;
    }

    /**
     * Gets the operation status.
     *
     * @return the operation status
     */
    public HsmProvider.OperationStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HsmException{");
        sb.append("message='").append(getMessage()).append("'");
        if (operation != null) {
            sb.append(", operation='").append(operation).append("'");
        }
        if (keyId != null) {
            sb.append(", keyId='").append(keyId).append("'");
        }
        sb.append(", status=").append(status);
        if (getCause() != null) {
            sb.append(", cause=").append(getCause().getMessage());
        }
        sb.append("}");
        return sb.toString();
    }
}
