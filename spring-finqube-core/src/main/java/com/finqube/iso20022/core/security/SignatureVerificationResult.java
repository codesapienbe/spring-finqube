package com.finqube.iso20022.core.security;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Result of a digital signature verification operation.
 *
 * <p>This class encapsulates the result of verifying a digital signature,
 * including verification status, certificate information, and metadata.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class SignatureVerificationResult {

    private final String messageId;
    private final boolean valid;
    private final String errorMessage;
    private final byte[] signerCertificate;
    private final String signatureAlgorithm;
    private final Instant verifiedAt;
    private final Map<String, Object> metadata;

    /**
     * Constructs a new SignatureVerificationResult.
     *
     * @param messageId the message identifier
     * @param valid whether the signature is valid
     * @param errorMessage the error message, if any
     * @param signerCertificate the signer's certificate
     * @param signatureAlgorithm the signature algorithm used
     * @param verifiedAt when the verification was performed
     * @param metadata additional metadata about the verification
     */
    public SignatureVerificationResult(String messageId, boolean valid, String errorMessage,
                                     byte[] signerCertificate, String signatureAlgorithm,
                                     Instant verifiedAt, Map<String, Object> metadata) {
        this.messageId = Objects.requireNonNull(messageId, "Message ID cannot be null");
        this.valid = valid;
        this.errorMessage = errorMessage;
        this.signerCertificate = signerCertificate;
        this.signatureAlgorithm = signatureAlgorithm;
        this.verifiedAt = Objects.requireNonNull(verifiedAt, "Verified timestamp cannot be null");
        this.metadata = metadata;
    }

    /**
     * Creates a successful verification result.
     *
     * @param messageId the message identifier
     * @param signerCertificate the signer's certificate
     * @param signatureAlgorithm the signature algorithm used
     * @param verifiedAt when the verification was performed
     * @return a successful verification result
     */
    public static SignatureVerificationResult success(String messageId, byte[] signerCertificate,
                                                    String signatureAlgorithm, Instant verifiedAt) {
        return new SignatureVerificationResult(messageId, true, null, signerCertificate,
            signatureAlgorithm, verifiedAt, Map.of());
    }

    /**
     * Creates a failed verification result.
     *
     * @param messageId the message identifier
     * @param errorMessage the error message
     * @param verifiedAt when the verification was performed
     * @return a failed verification result
     */
    public static SignatureVerificationResult failure(String messageId, String errorMessage, Instant verifiedAt) {
        return new SignatureVerificationResult(messageId, false, errorMessage, null, null, verifiedAt, Map.of());
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
     * Checks if the signature is valid.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Gets the error message.
     *
     * @return the error message, or null if verification was successful
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the signer's certificate.
     *
     * @return the signer certificate as byte array, or null if not available
     */
    public byte[] getSignerCertificate() {
        return signerCertificate != null ? signerCertificate.clone() : null; // Return a copy for security
    }

    /**
     * Gets the signature algorithm used.
     *
     * @return the signature algorithm, or null if not available
     */
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    /**
     * Gets when the verification was performed.
     *
     * @return the verification timestamp
     */
    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    /**
     * Gets additional metadata about the verification.
     *
     * @return the metadata map
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Checks if the signer certificate is available.
     *
     * @return true if certificate is available, false otherwise
     */
    public boolean hasSignerCertificate() {
        return signerCertificate != null && signerCertificate.length > 0;
    }

    @Override
    public String toString() {
        return "SignatureVerificationResult{" +
                "messageId='" + messageId + '\'' +
                ", valid=" + valid +
                ", errorMessage='" + errorMessage + '\'' +
                ", signatureAlgorithm='" + signatureAlgorithm + '\'' +
                ", verifiedAt=" + verifiedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignatureVerificationResult that = (SignatureVerificationResult) o;
        return valid == that.valid &&
                Objects.equals(messageId, that.messageId) &&
                Objects.equals(errorMessage, that.errorMessage) &&
                Objects.equals(signatureAlgorithm, that.signatureAlgorithm) &&
                Objects.equals(verifiedAt, that.verifiedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, valid, errorMessage, signatureAlgorithm, verifiedAt);
    }
}
