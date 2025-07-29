package com.finqube.iso20022.core.security;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import com.finqube.iso20022.core.message.BaseMessage;

/**
 * Result of a secure message operation (verify and decrypt).
 *
 * <p>This class encapsulates the result of verifying and decrypting a secure message,
 * including the decrypted message, verification status, and metadata.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class SecureMessageResult {

    private final String messageId;
    private final boolean successful;
    private final String errorMessage;
    private final BaseMessage decryptedMessage;
    private final SignatureVerificationResult signatureVerification;
    private final Instant processedAt;
    private final Map<String, Object> metadata;

    /**
     * Constructs a new SecureMessageResult.
     *
     * @param messageId the message identifier
     * @param successful whether the operation was successful
     * @param errorMessage the error message, if any
     * @param decryptedMessage the decrypted message, if successful
     * @param signatureVerification the signature verification result
     * @param processedAt when the operation was processed
     * @param metadata additional metadata about the operation
     */
    public SecureMessageResult(String messageId, boolean successful, String errorMessage,
                             BaseMessage decryptedMessage, SignatureVerificationResult signatureVerification,
                             Instant processedAt, Map<String, Object> metadata) {
        this.messageId = Objects.requireNonNull(messageId, "Message ID cannot be null");
        this.successful = successful;
        this.errorMessage = errorMessage;
        this.decryptedMessage = decryptedMessage;
        this.signatureVerification = signatureVerification;
        this.processedAt = Objects.requireNonNull(processedAt, "Processed timestamp cannot be null");
        this.metadata = metadata;
    }

    /**
     * Creates a successful result.
     *
     * @param messageId the message identifier
     * @param decryptedMessage the decrypted message
     * @param signatureVerification the signature verification result
     * @param processedAt when the operation was processed
     * @return a successful result
     */
    public static SecureMessageResult success(String messageId, BaseMessage decryptedMessage,
                                            SignatureVerificationResult signatureVerification, Instant processedAt) {
        return new SecureMessageResult(messageId, true, null, decryptedMessage, signatureVerification, processedAt, Map.of());
    }

    /**
     * Creates a failed result.
     *
     * @param messageId the message identifier
     * @param errorMessage the error message
     * @param processedAt when the operation was processed
     * @return a failed result
     */
    public static SecureMessageResult failure(String messageId, String errorMessage, Instant processedAt) {
        return new SecureMessageResult(messageId, false, errorMessage, null, null, processedAt, Map.of());
    }

    /**
     * Gets the message identifier.
     *
     * @return the message identifier
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Checks if the operation was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Gets the error message.
     *
     * @return the error message, or null if operation was successful
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the decrypted message.
     *
     * @return the decrypted message, or null if operation failed
     */
    public BaseMessage getDecryptedMessage() {
        return decryptedMessage;
    }

    /**
     * Gets the signature verification result.
     *
     * @return the signature verification result, or null if not available
     */
    public SignatureVerificationResult getSignatureVerification() {
        return signatureVerification;
    }

    /**
     * Gets when the operation was processed.
     *
     * @return the processing timestamp
     */
    public Instant getProcessedAt() {
        return processedAt;
    }

    /**
     * Gets additional metadata about the operation.
     *
     * @return the metadata map
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Checks if the signature was verified successfully.
     *
     * @return true if signature verification was successful, false otherwise
     */
    public boolean isSignatureValid() {
        return signatureVerification != null && signatureVerification.isValid();
    }

    @Override
    public String toString() {
        return "SecureMessageResult{" +
                "messageId='" + messageId + '\'' +
                ", successful=" + successful +
                ", errorMessage='" + errorMessage + '\'' +
                ", signatureValid=" + isSignatureValid() +
                ", processedAt=" + processedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecureMessageResult that = (SecureMessageResult) o;
        return successful == that.successful &&
                Objects.equals(messageId, that.messageId) &&
                Objects.equals(errorMessage, that.errorMessage) &&
                Objects.equals(processedAt, that.processedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, successful, errorMessage, processedAt);
    }
}
