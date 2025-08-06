package com.finqube.iso20022.core.security.key;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entry in the key rotation history.
 *
 * <p>This class represents a single entry in the key rotation history,
 * providing detailed information about a key rotation operation.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class KeyRotationHistoryEntry {

    private final String rotationId;
    private final String keyId;
    private final KeyRotationManager.RotationStatus status;
    private final LocalDateTime timestamp;
    private final String performedBy;
    private final String reason;
    private final String oldKeyId;
    private final String newKeyId;
    private final KeyRotationManager.KeyType newKeyType;
    private final long durationMs;
    private final boolean successful;

    /**
     * Constructs a new KeyRotationHistoryEntry with the specified parameters.
     *
     * @param rotationId the rotation identifier
     * @param keyId the key identifier
     * @param status the rotation status
     * @param timestamp the timestamp
     * @param performedBy the user who performed the rotation
     * @param reason the reason for rotation
     * @param oldKeyId the old key identifier
     * @param newKeyId the new key identifier
     * @param newKeyType the new key type
     * @param durationMs the duration in milliseconds
     * @param successful whether the rotation was successful
     */
    public KeyRotationHistoryEntry(String rotationId, String keyId, KeyRotationManager.RotationStatus status,
                                 LocalDateTime timestamp, String performedBy, String reason,
                                 String oldKeyId, String newKeyId, KeyRotationManager.KeyType newKeyType,
                                 long durationMs, boolean successful) {
        this.rotationId = rotationId;
        this.keyId = keyId;
        this.status = status;
        this.timestamp = timestamp;
        this.performedBy = performedBy;
        this.reason = reason;
        this.oldKeyId = oldKeyId;
        this.newKeyId = newKeyId;
        this.newKeyType = newKeyType;
        this.durationMs = durationMs;
        this.successful = successful;
    }

    /**
     * Gets the rotation identifier.
     *
     * @return the rotation identifier
     */
    public String getRotationId() {
        return rotationId;
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
     * Gets the rotation status.
     *
     * @return the rotation status
     */
    public KeyRotationManager.RotationStatus getStatus() {
        return status;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the user who performed the rotation.
     *
     * @return the user who performed the rotation
     */
    public String getPerformedBy() {
        return performedBy;
    }

    /**
     * Gets the reason for rotation.
     *
     * @return the reason for rotation
     */
    public String getReason() {
        return reason;
    }

    /**
     * Gets the old key identifier.
     *
     * @return the old key identifier
     */
    public String getOldKeyId() {
        return oldKeyId;
    }

    /**
     * Gets the new key identifier.
     *
     * @return the new key identifier
     */
    public String getNewKeyId() {
        return newKeyId;
    }

    /**
     * Gets the new key type.
     *
     * @return the new key type
     */
    public KeyRotationManager.KeyType getNewKeyType() {
        return newKeyType;
    }

    /**
     * Gets the duration in milliseconds.
     *
     * @return the duration in milliseconds
     */
    public long getDurationMs() {
        return durationMs;
    }

    /**
     * Gets the formatted duration.
     *
     * @return the formatted duration string
     */
    public String getFormattedDuration() {
        if (durationMs < 1000) {
            return durationMs + "ms";
        } else if (durationMs < 60000) {
            return String.format("%.2fs", durationMs / 1000.0);
        } else {
            long minutes = durationMs / 60000;
            long seconds = (durationMs % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }

    /**
     * Checks if the rotation was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        KeyRotationHistoryEntry that = (KeyRotationHistoryEntry) obj;
        return Objects.equals(rotationId, that.rotationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rotationId);
    }

    @Override
    public String toString() {
        return String.format("KeyRotationHistoryEntry{rotationId='%s', keyId='%s', status=%s, timestamp=%s, successful=%s}",
                rotationId, keyId, status, timestamp, successful);
    }
}
