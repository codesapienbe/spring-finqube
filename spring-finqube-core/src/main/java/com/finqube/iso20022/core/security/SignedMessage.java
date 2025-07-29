package com.finqube.iso20022.core.security;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a digitally signed ISO 20022 message.
 *
 * <p>This class encapsulates a message that has been digitally signed,
 * including the original message content, signature, certificate information,
 * and metadata about the signing process.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class SignedMessage {

    private final String messageId;
    private final byte[] originalContent;
    private final byte[] signature;
    private final byte[] signerCertificate;
    private final String signatureAlgorithm;
    private final Instant signedAt;
    private final Map<String, Object> metadata;

    /**
     * Constructs a new SignedMessage.
     *
     * @param messageId the message identifier
     * @param originalContent the original message content
     * @param signature the digital signature
     * @param signerCertificate the signer's certificate
     * @param signatureAlgorithm the signature algorithm used
     * @param signedAt when the message was signed
     * @param metadata additional metadata about the signing process
     */
    public SignedMessage(String messageId, byte[] originalContent, byte[] signature,
                        byte[] signerCertificate, String signatureAlgorithm,
                        Instant signedAt, Map<String, Object> metadata) {
        this.messageId = Objects.requireNonNull(messageId, "Message ID cannot be null");
        this.originalContent = Objects.requireNonNull(originalContent, "Original content cannot be null");
        this.signature = Objects.requireNonNull(signature, "Signature cannot be null");
        this.signerCertificate = signerCertificate;
        this.signatureAlgorithm = Objects.requireNonNull(signatureAlgorithm, "Signature algorithm cannot be null");
        this.signedAt = Objects.requireNonNull(signedAt, "Signed timestamp cannot be null");
        this.metadata = metadata;
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
     * Gets the original message content.
     *
     * @return the original content as byte array
     */
    public byte[] getOriginalContent() {
        return originalContent.clone(); // Return a copy for security
    }

    /**
     * Gets the digital signature.
     *
     * @return the signature as byte array
     */
    public byte[] getSignature() {
        return signature.clone(); // Return a copy for security
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
     * @return the signature algorithm
     */
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    /**
     * Gets when the message was signed.
     *
     * @return the signing timestamp
     */
    public Instant getSignedAt() {
        return signedAt;
    }

    /**
     * Gets additional metadata about the signing process.
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
        return "SignedMessage{" +
                "messageId='" + messageId + '\'' +
                ", signatureAlgorithm='" + signatureAlgorithm + '\'' +
                ", signedAt=" + signedAt +
                ", hasCertificate=" + hasSignerCertificate() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignedMessage that = (SignedMessage) o;
        return Objects.equals(messageId, that.messageId) &&
                Objects.equals(signatureAlgorithm, that.signatureAlgorithm) &&
                Objects.equals(signedAt, that.signedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, signatureAlgorithm, signedAt);
    }
}