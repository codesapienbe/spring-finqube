package com.finqube.iso20022.core.security.key;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Key rotation manager for secure key lifecycle management.
 *
 * <p>This interface provides comprehensive key rotation capabilities including:</p>
 * <ul>
 *   <li>Automatic key rotation based on policies</li>
 *   <li>Manual key rotation with rollback capabilities</li>
 *   <li>Key versioning and tracking</li>
 *   <li>Secure key generation and storage</li>
 *   <li>Key expiration monitoring and alerts</li>
 *   <li>Asynchronous key rotation operations</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * @Autowired
 * private KeyRotationManager keyRotationManager;
 *
 * // Perform automatic key rotation
 * KeyRotationResult result = keyRotationManager.performAutomaticRotation();
 *
 * // Manual key rotation with specific parameters
 * KeyRotationResult result = keyRotationManager.rotateKey("signing-key", KeyType.RSA_2048);
 * }</pre>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public interface KeyRotationManager {

    /**
     * Key type enumeration.
     */
    enum KeyType {
        /** RSA 2048-bit key */
        RSA_2048("RSA-2048"),
        /** RSA 4096-bit key */
        RSA_4096("RSA-4096"),
        /** ECDSA P-256 key */
        ECDSA_P256("ECDSA-P256"),
        /** ECDSA P-384 key */
        ECDSA_P384("ECDSA-P384"),
        /** ECDSA P-521 key */
        ECDSA_P521("ECDSA-P521"),
        /** AES 256-bit key */
        AES_256("AES-256"),
        /** AES 512-bit key */
        AES_512("AES-512");

        private final String displayName;

        KeyType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Key rotation status enumeration.
     */
    enum RotationStatus {
        /** Key rotation is pending */
        PENDING("PEND"),
        /** Key rotation is in progress */
        IN_PROGRESS("PROG"),
        /** Key rotation completed successfully */
        COMPLETED("COMP"),
        /** Key rotation failed */
        FAILED("FAIL"),
        /** Key rotation was rolled back */
        ROLLED_BACK("ROLL"),
        /** Key rotation is scheduled */
        SCHEDULED("SCHD");

        private final String code;

        RotationStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Key usage enumeration.
     */
    enum KeyUsage {
        /** Digital signature */
        DIGITAL_SIGNATURE("SIGN"),
        /** Key encipherment */
        KEY_ENCIPHERMENT("ENC"),
        /** Data encipherment */
        DATA_ENCIPHERMENT("DATA"),
        /** Key agreement */
        KEY_AGREEMENT("AGRE"),
        /** Certificate signing */
        CERTIFICATE_SIGNING("CERT"),
        /** CRL signing */
        CRL_SIGNING("CRL"),
        /** Server authentication */
        SERVER_AUTH("SRV"),
        /** Client authentication */
        CLIENT_AUTH("CLI");

        private final String code;

        KeyUsage(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Performs automatic key rotation based on configured policies.
     *
     * @return the rotation result
     * @throws KeyRotationException if rotation fails
     */
    KeyRotationResult performAutomaticRotation() throws KeyRotationException;

    /**
     * Performs automatic key rotation asynchronously.
     *
     * @return a CompletableFuture that will complete with the rotation result
     */
    CompletableFuture<KeyRotationResult> performAutomaticRotationAsync();

    /**
     * Rotates a specific key with manual parameters.
     *
     * @param keyId the key identifier to rotate
     * @param newKeyType the type of the new key
     * @return the rotation result
     * @throws KeyRotationException if rotation fails
     */
    KeyRotationResult rotateKey(String keyId, KeyType newKeyType) throws KeyRotationException;

    /**
     * Rotates a specific key asynchronously.
     *
     * @param keyId the key identifier to rotate
     * @param newKeyType the type of the new key
     * @return a CompletableFuture that will complete with the rotation result
     */
    CompletableFuture<KeyRotationResult> rotateKeyAsync(String keyId, KeyType newKeyType);

    /**
     * Rotates a key with custom parameters.
     *
     * @param keyId the key identifier to rotate
     * @param newKeyType the type of the new key
     * @param rotationOptions the rotation options
     * @return the rotation result
     * @throws KeyRotationException if rotation fails
     */
    KeyRotationResult rotateKey(String keyId, KeyType newKeyType, KeyRotationOptions rotationOptions) throws KeyRotationException;

    /**
     * Rotates a key with custom parameters asynchronously.
     *
     * @param keyId the key identifier to rotate
     * @param newKeyType the type of the new key
     * @param rotationOptions the rotation options
     * @return a CompletableFuture that will complete with the rotation result
     */
    CompletableFuture<KeyRotationResult> rotateKeyAsync(String keyId, KeyType newKeyType, KeyRotationOptions rotationOptions);

    /**
     * Schedules a key rotation for a future time.
     *
     * @param keyId the key identifier to rotate
     * @param newKeyType the type of the new key
     * @param scheduledTime the scheduled rotation time
     * @return the rotation result
     * @throws KeyRotationException if scheduling fails
     */
    KeyRotationResult scheduleKeyRotation(String keyId, KeyType newKeyType, LocalDateTime scheduledTime) throws KeyRotationException;

    /**
     * Cancels a scheduled key rotation.
     *
     * @param rotationId the rotation identifier to cancel
     * @return true if cancellation was successful, false otherwise
     * @throws KeyRotationException if cancellation fails
     */
    boolean cancelScheduledRotation(String rotationId) throws KeyRotationException;

    /**
     * Rolls back a key rotation.
     *
     * @param rotationId the rotation identifier to rollback
     * @return the rollback result
     * @throws KeyRotationException if rollback fails
     */
    KeyRotationResult rollbackRotation(String rotationId) throws KeyRotationException;

    /**
     * Gets the current active key for a specific usage.
     *
     * @param keyUsage the key usage
     * @return the current active key, or empty if not found
     * @throws KeyRotationException if retrieval fails
     */
    Optional<KeyInfo> getCurrentActiveKey(KeyUsage keyUsage) throws KeyRotationException;

    /**
     * Gets all keys for a specific usage.
     *
     * @param keyUsage the key usage
     * @return list of keys for the usage
     * @throws KeyRotationException if retrieval fails
     */
    List<KeyInfo> getKeysForUsage(KeyUsage keyUsage) throws KeyRotationException;

    /**
     * Gets all keys in the key store.
     *
     * @return list of all keys
     * @throws KeyRotationException if retrieval fails
     */
    List<KeyInfo> getAllKeys() throws KeyRotationException;

    /**
     * Gets keys that are expiring soon.
     *
     * @param days the number of days to check
     * @return list of keys expiring within the specified days
     * @throws KeyRotationException if retrieval fails
     */
    List<KeyInfo> getExpiringKeys(int days) throws KeyRotationException;

    /**
     * Gets expired keys.
     *
     * @return list of expired keys
     * @throws KeyRotationException if retrieval fails
     */
    List<KeyInfo> getExpiredKeys() throws KeyRotationException;

    /**
     * Gets key rotation history.
     *
     * @param keyId the key identifier (optional, if null returns all history)
     * @param limit the maximum number of entries to return
     * @return list of rotation history entries
     * @throws KeyRotationException if retrieval fails
     */
    List<KeyRotationHistoryEntry> getRotationHistory(String keyId, int limit) throws KeyRotationException;

    /**
     * Gets key rotation statistics.
     *
     * @return the rotation statistics
     * @throws KeyRotationException if retrieval fails
     */
    KeyRotationStatistics getStatistics() throws KeyRotationException;

    /**
     * Performs a health check on the key rotation manager.
     *
     * @return true if healthy, false otherwise
     * @throws KeyRotationException if health check fails
     */
    boolean isHealthy() throws KeyRotationException;

    /**
     * Gets the key rotation manager identifier.
     *
     * @return the manager identifier
     */
    String getKeyRotationManagerId();

    /**
     * Gets the key rotation manager display name.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Gets the key rotation manager version.
     *
     * @return the version
     */
    String getVersion();
}
