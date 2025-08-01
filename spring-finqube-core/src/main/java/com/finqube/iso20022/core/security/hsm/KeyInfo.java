package com.finqube.iso20022.core.security.hsm;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Information about a key stored in the HSM.
 *
 * <p>This class provides metadata about keys stored in the HSM, including
 * key type, usage, creation date, expiration date, and other relevant
 * information for key lifecycle management.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class KeyInfo {

    private final String keyId;
    private final String keyLabel;
    private final HsmProvider.KeyType keyType;
    private final HsmProvider.KeyUsage keyUsage;
    private final int keySize;
    private final LocalDateTime creationDate;
    private final LocalDateTime expirationDate;
    private final boolean enabled;
    private final boolean extractable;
    private final String algorithm;
    private final String description;

    /**
     * Constructs a new KeyInfo instance.
     *
     * @param keyId the key identifier
     * @param keyLabel the key label
     * @param keyType the key type
     * @param keyUsage the key usage
     * @param keySize the key size in bits
     * @param creationDate the creation date
     * @param expirationDate the expiration date
     * @param enabled whether the key is enabled
     * @param extractable whether the key is extractable
     * @param algorithm the algorithm used by the key
     * @param description the key description
     */
    public KeyInfo(String keyId, String keyLabel, HsmProvider.KeyType keyType, HsmProvider.KeyUsage keyUsage,
                   int keySize, LocalDateTime creationDate, LocalDateTime expirationDate, boolean enabled,
                   boolean extractable, String algorithm, String description) {
        this.keyId = keyId;
        this.keyLabel = keyLabel;
        this.keyType = keyType;
        this.keyUsage = keyUsage;
        this.keySize = keySize;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.enabled = enabled;
        this.extractable = extractable;
        this.algorithm = algorithm;
        this.description = description;
    }

    /**
     * Gets the key identifier.
     *
     * @return the key ID
     */
    public String getKeyId() {
        return keyId;
    }

    /**
     * Gets the key label.
     *
     * @return the key label
     */
    public String getKeyLabel() {
        return keyLabel;
    }

    /**
     * Gets the key type.
     *
     * @return the key type
     */
    public HsmProvider.KeyType getKeyType() {
        return keyType;
    }

    /**
     * Gets the key usage.
     *
     * @return the key usage
     */
    public HsmProvider.KeyUsage getKeyUsage() {
        return keyUsage;
    }

    /**
     * Gets the key size in bits.
     *
     * @return the key size
     */
    public int getKeySize() {
        return keySize;
    }

    /**
     * Gets the creation date.
     *
     * @return the creation date
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Gets the expiration date.
     *
     * @return the expiration date, or null if the key doesn't expire
     */
    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    /**
     * Checks if the key is enabled.
     *
     * @return true if the key is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Checks if the key is extractable.
     *
     * @return true if the key is extractable, false otherwise
     */
    public boolean isExtractable() {
        return extractable;
    }

    /**
     * Gets the algorithm used by the key.
     *
     * @return the algorithm
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Gets the key description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if the key has expired.
     *
     * @return true if the key has expired, false otherwise
     */
    public boolean isExpired() {
        return expirationDate != null && LocalDateTime.now().isAfter(expirationDate);
    }

    /**
     * Checks if the key will expire soon (within the specified number of days).
     *
     * @param days the number of days to check
     * @return true if the key will expire within the specified days, false otherwise
     */
    public boolean isExpiringSoon(int days) {
        if (expirationDate == null) {
            return false;
        }
        LocalDateTime warningDate = LocalDateTime.now().plusDays(days);
        return LocalDateTime.now().isBefore(expirationDate) && expirationDate.isBefore(warningDate);
    }

    /**
     * Gets the remaining validity period in days.
     *
     * @return the number of days until expiration, or -1 if the key doesn't expire
     */
    public long getRemainingValidityDays() {
        if (expirationDate == null) {
            return -1;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expirationDate)) {
            return 0;
        }
        return java.time.Duration.between(now, expirationDate).toDays();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KeyInfo keyInfo = (KeyInfo) obj;
        return Objects.equals(keyId, keyInfo.keyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyId);
    }

    @Override
    public String toString() {
        return String.format("KeyInfo{keyId='%s', keyLabel='%s', keyType=%s, keyUsage=%s, keySize=%d, " +
                           "creationDate=%s, expirationDate=%s, enabled=%s, extractable=%s, algorithm='%s'}",
                           keyId, keyLabel, keyType, keyUsage, keySize, creationDate, expirationDate,
                           enabled, extractable, algorithm);
    }
}
