package com.finqube.iso20022.core.security.key;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Information about a cryptographic key.
 *
 * <p>This class provides comprehensive metadata about cryptographic keys including
 * key type, usage, creation and expiration dates, and key material.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class KeyInfo {

    private final String keyId;
    private final String alias;
    private final KeyRotationManager.KeyType keyType;
    private final List<KeyRotationManager.KeyUsage> keyUsages;
    private final Key publicKey;
    private final Key privateKey;
    private final LocalDateTime creationTime;
    private final LocalDateTime expirationTime;
    private final LocalDateTime lastRotationTime;
    private final String version;
    private final boolean active;
    private final String description;
    private final String algorithm;
    private final int keySize;

    /**
     * Constructs a new KeyInfo with the specified parameters.
     *
     * @param keyId the key identifier
     * @param alias the key alias
     * @param keyType the key type
     * @param keyUsages the key usages
     * @param publicKey the public key (can be PublicKey or SecretKey for symmetric encryption)
     * @param privateKey the private key (can be PrivateKey or SecretKey for symmetric encryption)
     * @param creationTime the creation time
     * @param expirationTime the expiration time
     * @param lastRotationTime the last rotation time
     * @param version the key version
     * @param active whether the key is active
     * @param description the key description
     * @param algorithm the key algorithm
     * @param keySize the key size in bits
     */
    public KeyInfo(String keyId, String alias, KeyRotationManager.KeyType keyType,
                   List<KeyRotationManager.KeyUsage> keyUsages, Key publicKey, Key privateKey,
                   LocalDateTime creationTime, LocalDateTime expirationTime, LocalDateTime lastRotationTime,
                   String version, boolean active, String description, String algorithm, int keySize) {
        this.keyId = keyId;
        this.alias = alias;
        this.keyType = keyType;
        this.keyUsages = keyUsages;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
        this.lastRotationTime = lastRotationTime;
        this.version = version;
        this.active = active;
        this.description = description;
        this.algorithm = algorithm;
        this.keySize = keySize;
    }

    /**
     * Gets the key identifier.
     *
     * @return the key identifier
     */
    public String getKeyId() {
        return keyId;
    }

    /**
     * Gets the key alias.
     *
     * @return the key alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Gets the key type.
     *
     * @return the key type
     */
    public KeyRotationManager.KeyType getKeyType() {
        return keyType;
    }

    /**
     * Gets the key usages.
     *
     * @return the key usages
     */
    public List<KeyRotationManager.KeyUsage> getKeyUsages() {
        return keyUsages;
    }

    /**
     * Gets the public key.
     *
     * @return the public key
     */
    public PublicKey getPublicKey() {
        return publicKey instanceof PublicKey ? (PublicKey) publicKey : null;
    }

    /**
     * Gets the private key.
     *
     * @return the private key
     */
    public PrivateKey getPrivateKey() {
        return privateKey instanceof PrivateKey ? (PrivateKey) privateKey : null;
    }

    /**
     * Gets the public key as a generic Key.
     *
     * @return the public key
     */
    public Key getPublicKeyAsKey() {
        return publicKey;
    }

    /**
     * Gets the private key as a generic Key.
     *
     * @return the private key
     */
    public Key getPrivateKeyAsKey() {
        return privateKey;
    }

    /**
     * Gets the creation time.
     *
     * @return the creation time
     */
    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    /**
     * Gets the expiration time.
     *
     * @return the expiration time
     */
    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    /**
     * Gets the last rotation time.
     *
     * @return the last rotation time
     */
    public LocalDateTime getLastRotationTime() {
        return lastRotationTime;
    }

    /**
     * Gets the key version.
     *
     * @return the key version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Checks if the key is active.
     *
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Gets the key description.
     *
     * @return the key description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the key algorithm.
     *
     * @return the key algorithm
     */
    public String getAlgorithm() {
        return algorithm;
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
     * Checks if the key is expired.
     *
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        return expirationTime != null && LocalDateTime.now().isAfter(expirationTime);
    }

    /**
     * Checks if the key is expiring soon.
     *
     * @param days the number of days to check
     * @return true if expiring within the specified days, false otherwise
     */
    public boolean isExpiringSoon(int days) {
        if (expirationTime == null) {
            return false;
        }
        LocalDateTime warningTime = LocalDateTime.now().plusDays(days);
        return expirationTime.isBefore(warningTime);
    }

    /**
     * Gets the key age in days.
     *
     * @return the key age in days
     */
    public long getAgeInDays() {
        if (creationTime == null) {
            return 0;
        }
        return java.time.Duration.between(creationTime, LocalDateTime.now()).toDays();
    }

    /**
     * Gets the days until expiration.
     *
     * @return the days until expiration, or -1 if no expiration time
     */
    public long getDaysUntilExpiration() {
        if (expirationTime == null) {
            return -1;
        }
        return java.time.Duration.between(LocalDateTime.now(), expirationTime).toDays();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        KeyInfo keyInfo = (KeyInfo) obj;
        return Objects.equals(keyId, keyInfo.keyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyId);
    }

    @Override
    public String toString() {
        return String.format("KeyInfo{keyId='%s', alias='%s', keyType=%s, algorithm='%s', keySize=%d, active=%s, version='%s'}",
                keyId, alias, keyType, algorithm, keySize, active, version);
    }

    /**
     * Builder for KeyInfo objects.
     */
    public static class Builder {
        private String keyId;
        private String alias;
        private KeyRotationManager.KeyType keyType;
        private List<KeyRotationManager.KeyUsage> keyUsages;
        private Key publicKey;
        private Key privateKey;
        private LocalDateTime creationTime;
        private LocalDateTime expirationTime;
        private LocalDateTime lastRotationTime;
        private String version;
        private boolean active;
        private String description;
        private String algorithm;
        private int keySize;

        public Builder keyId(String keyId) {
            this.keyId = keyId;
            return this;
        }

        public Builder alias(String alias) {
            this.alias = alias;
            return this;
        }

        public Builder keyType(KeyRotationManager.KeyType keyType) {
            this.keyType = keyType;
            return this;
        }

        public Builder keyUsages(List<KeyRotationManager.KeyUsage> keyUsages) {
            this.keyUsages = keyUsages;
            return this;
        }

        public Builder publicKey(Key publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder privateKey(Key privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public Builder creationTime(LocalDateTime creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public Builder expirationTime(LocalDateTime expirationTime) {
            this.expirationTime = expirationTime;
            return this;
        }

        public Builder lastRotationTime(LocalDateTime lastRotationTime) {
            this.lastRotationTime = lastRotationTime;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder algorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder keySize(int keySize) {
            this.keySize = keySize;
            return this;
        }

        public KeyInfo build() {
            return new KeyInfo(keyId, alias, keyType, keyUsages, publicKey, privateKey,
                    creationTime, expirationTime, lastRotationTime, version, active, description, algorithm, keySize);
        }
    }
}
