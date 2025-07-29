package com.finqube.iso20022.core.security;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an encrypted ISO 20022 message.
 *
 * <p>This class encapsulates a message that has been encrypted,
 * including the encrypted content, encryption parameters, certificate information,
 * and metadata about the encryption process.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class EncryptedMessage {

    private final String messageId;
    private final byte[] encryptedContent;
    private final byte[] encryptedKey;
    private final byte[] recipientCertificate;
    private final String encryptionAlgorithm;
    private final String keyEncryptionAlgorithm;
    private final Instant encryptedAt;
    private final Map<String, Object> metadata;

    /**
     * Constructs a new EncryptedMessage.
     *
     * @param messageId the message identifier
     * @param encryptedContent the encrypted message content
     * @param encryptedKey the encrypted symmetric key
     * @param recipientCertificate the recipient's certificate
     * @param encryptionAlgorithm the encryption algorithm used
     * @param keyEncryptionAlgorithm the key encryption algorithm used
     * @param encryptedAt when the message was encrypted
     * @param metadata additional metadata about the encryption process
     */
    public EncryptedMessage(String messageId, byte[] encryptedContent, byte[] encryptedKey,
                          byte[] recipientCertificate, String encryptionAlgorithm,
                          String keyEncryptionAlgorithm, Instant encryptedAt, Map<String, Object> metadata) {
        this.messageId = Objects.requireNonNull(messageId, "Message ID cannot be null");
        this.encryptedContent = Objects.requireNonNull(encryptedContent, "Encrypted content cannot be null");
        this.encryptedKey = Objects.requireNonNull(encryptedKey, "Encrypted key cannot be null");
        this.recipientCertificate = Objects.requireNonNull(recipientCertificate, "Recipient certificate cannot be null");
        this.encryptionAlgorithm = Objects.requireNonNull(encryptionAlgorithm, "Encryption algorithm cannot be null");
        this.keyEncryptionAlgorithm = Objects.requireNonNull(keyEncryptionAlgorithm, "Key encryption algorithm cannot be null");
        this.encryptedAt = Objects.requireNonNull(encryptedAt, "Encrypted timestamp cannot be null");
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
     * Gets when the message was encrypted.
     *
     * @return the encryption timestamp
     */
    public Instant getEncryptedAt() {
        return encryptedAt;
    }

    /**
     * Gets additional metadata about the encryption process.
     *
     * @return the metadata map
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "EncryptedMessage{" +
                "messageId='" + messageId + '\'' +
                ", encryptionAlgorithm='" + encryptionAlgorithm + '\'' +
                ", keyEncryptionAlgorithm='" + keyEncryptionAlgorithm + '\'' +
                ", encryptedAt=" + encryptedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EncryptedMessage that = (EncryptedMessage) o;
        return Objects.equals(messageId, that.messageId) &&
                Objects.equals(encryptionAlgorithm, that.encryptionAlgorithm) &&
                Objects.equals(keyEncryptionAlgorithm, that.keyEncryptionAlgorithm) &&
                Objects.equals(encryptedAt, that.encryptedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, encryptionAlgorithm, keyEncryptionAlgorithm, encryptedAt);
    }
}