package com.finqube.iso20022.core.security.signature;

/**
 * Exception thrown when digital signature operations fail.
 *
 * @author Spring Finqube Team
 */
public class SignatureException extends Exception {
    private static final long serialVersionUID = 1L;
    private final String operation;
    private final String algorithm;

    public SignatureException(String message) {
        super(message);
        this.operation = null;
        this.algorithm = null;
    }

    public SignatureException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.algorithm = null;
    }

    public SignatureException(String message, String operation, String algorithm) {
        super(message);
        this.operation = operation;
        this.algorithm = algorithm;
    }

    public SignatureException(String message, Throwable cause, String operation, String algorithm) {
        super(message, cause);
        this.operation = operation;
        this.algorithm = algorithm;
    }

    public String getOperation() {
        return operation;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SignatureException: ").append(getMessage());
        if (operation != null) {
            sb.append(" (Operation: ").append(operation).append(")");
        }
        if (algorithm != null) {
            sb.append(" (Algorithm: ").append(algorithm).append(")");
        }
        return sb.toString();
    }
}
