package com.finqube.iso20022.core.security.certificate;

/**
 * Exception thrown when certificate operations fail.
 *
 * <p>This exception provides detailed error information for certificate
 * operations, including validation, storage, and retrieval failures.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class CertificateException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String operation;
    private final String certificateId;

    /**
     * Constructs a new CertificateException with the specified detail message.
     *
     * @param message the detail message
     */
    public CertificateException(String message) {
        super(message);
        this.operation = null;
        this.certificateId = null;
    }

    /**
     * Constructs a new CertificateException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public CertificateException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.certificateId = null;
    }

    /**
     * Constructs a new CertificateException with the specified detail message, operation, and certificate ID.
     *
     * @param message the detail message
     * @param operation the operation that failed
     * @param certificateId the certificate identifier involved in the operation
     */
    public CertificateException(String message, String operation, String certificateId) {
        super(message);
        this.operation = operation;
        this.certificateId = certificateId;
    }

    /**
     * Constructs a new CertificateException with the specified detail message, cause, operation, and certificate ID.
     *
     * @param message the detail message
     * @param cause the cause
     * @param operation the operation that failed
     * @param certificateId the certificate identifier involved in the operation
     */
    public CertificateException(String message, Throwable cause, String operation, String certificateId) {
        super(message, cause);
        this.operation = operation;
        this.certificateId = certificateId;
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
     * Gets the certificate identifier involved in the operation.
     *
     * @return the certificate identifier, or null if not specified
     */
    public String getCertificateId() {
        return certificateId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CertificateException");

        if (operation != null || certificateId != null) {
            sb.append(" [");
            if (operation != null) {
                sb.append("operation=").append(operation);
            }
            if (certificateId != null) {
                if (operation != null) {
                    sb.append(", ");
                }
                sb.append("certificateId=").append(certificateId);
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
