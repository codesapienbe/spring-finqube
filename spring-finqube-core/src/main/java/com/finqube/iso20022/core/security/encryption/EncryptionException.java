package com.finqube.iso20022.core.security.encryption;

/**
 * Exception thrown when encryption or decryption fails.
 *
 * @author Spring Finqube Team
 */
public class EncryptionException extends Exception {
    public EncryptionException(String message) {
        super(message);
    }
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
