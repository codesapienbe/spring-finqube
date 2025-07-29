package com.finqube.iso20022.core.security;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a secure ISO 20022 message that is both signed and encrypted.
 *
 * <p>This class encapsulates a message that has been digitally signed and encrypted,
 * providing both authenticity and confidentiality guarantees.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class SecureMessage {

    private final String messageId;
    private final byte[] encryptedContent;
    private final byte[] encryptedKey;
    private final byte[] signature;
    private final byte[] signerCertificate;
    private final byte[] recipientCertificate;
    private final String encryptionAlgorithm;
    private final String keyEncryptionAlgorithm;
    private final String signatureAlgorithm;
    private final Instant securedAt;
    private final Map<String, Object> metadata;

    /**
     * Constructs a new SecureMessage.
     *
     * @param messageId the message identifier
     * @param encryptedContent the encrypted message content
     * @param encryptedKey the encrypted symmetric key
     * @param signature the digital signature
     * @param signerCertificate the signer's certificate
     * @param recipientCertificate the recipient's certificate
     * @param encryptionAlgorithm the encryption algorithm used
     * @param keyEncryptionAlgorithm the key encryption algorithm used
     * @param signatureAlgorithm the signature algorithm used
     * @param securedAt when the message was secured
     * @param metadata additional metadata about the security process
     */
    public SecureMessage(String messageId, byte[] encryptedContent, byte[] encryptedKey,
                        byte[] signature, byte[] signerCertificate, byte[] recipientCertificate,
                        String encryptionAlgorithm, String keyEncryptionAlgorithm, String signatureAlgorithm,
                        Instant securedAt, Map<String, Object> metadata) {
        this.messageId = Objects.requireNonNull(messageId, "Message ID cannot be null");
        this.encryptedContent = Objects.requireNonNull(encryptedContent, "Encrypted content cannot be null");
        this.encryptedKey = Objects.requireNonNull(encryptedKey, "Encrypted key cannot be null");
        this.signature = Objects.requireNonNull(signature, "Signature cannot be null");
        this.signerCertificate = Objects.requireNonNull(signerCertificate, "Signer certificate cannot be null");
        this.recipientCertificate = Objects.requireNonNull(recipientCertificate, "Recipient certificate cannot be null");
        this.encryptionAlgorithm = Objects.requireNonNull(encryptionAlgorithm, "Encryption algorithm cannot be null");
        this.keyEncryptionAlgorithm = Objects.requireNonNull(keyEncryptionAlgorithm, "Key encryption algorithm cannot be null");
        this.signatureAlgorithm = Objects.requireNonNull(signatureAlgorithm, "Signature algorithm cannot be null");
        this.securedAt = Objects.requireNonNull(securedAt, "Secured timestamp cannot be null");
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
     * Gets the encrypted message content.
     *
     * @return the encrypted content as byte array
     */
    public byte[] getEncryptedContent() {
        return encryptedContent.clone(); // Return a copy for security
    }

    /**
     * Gets the encrypted symmetric key.
     *
     * @return the encrypted key as byte array
     */
    public byte[] getEncryptedKey() {
        return encryptedKey.clone(); // Return a copy for security
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
     * @return the signer certificate as byte array
     */
    public byte[] getSignerCertificate() {
        return signerCertificate.clone(); // Return a copy for security
    }

    /**
     * Gets the recipient's certificate.
     *
     * @return the recipient certificate as byte array
     */
    public byte[] getRecipientCertificate() {
        return recipientCertificate.clone(); // Return a copy for security
    }

    /**
     * Gets the encryption algorithm used.
     *
     * @return the encryption algorithm
     */
    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    /**
     * Gets the key encryption algorithm used.
     *
     * @return the key encryption algorithm
     */
    public String getKeyEncryptionAlgorithm() {
        return keyEncryptionAlgorithm;
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
     * Gets when the message was secured.
     *
     * @return the securing timestamp
     */
    public Instant getSecuredAt() {
        return securedAt;
    }

    /**
     * Gets additional metadata about the security process.
     *
     * @return the metadata map
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "SecureMessage{" +
                "messageId='" + messageId + '\'' +
                ", encryptionAlgorithm='" + encryptionAlgorithm + '\'' +
                ", keyEncryptionAlgorithm='" + keyEncryptionAlgorithm + '\'' +
                ", signatureAlgorithm='" + signatureAlgorithm + '\'' +
                ", securedAt=" + securedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecureMessage that = (SecureMessage) o;
        return Objects.equals(messageId, that.messageId) &&
                Objects.equals(encryptionAlgorithm, that.encryptionAlgorithm) &&
                Objects.equals(keyEncryptionAlgorithm, that.keyEncryptionAlgorithm) &&
                Objects.equals(signatureAlgorithm, that.signatureAlgorithm) &&
                Objects.equals(securedAt, that.securedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, encryptionAlgorithm, keyEncryptionAlgorithm, signatureAlgorithm, securedAt);
    }
}
