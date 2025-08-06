package com.finqube.iso20022.core.security.storage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Information about a key restore operation.
 *
 * <p>This class provides comprehensive information about a key restore operation
 * including restore source, timestamp, key count, and restore metadata.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class KeyRestoreInfo {

    private final String restoreId;
    private final String backupLocation;
    private final LocalDateTime restoreTime;
    private final int keyCount;
    private final List<String> restoredKeyIds;
    private final List<String> failedKeyIds;
    private final String status;
    private final String message;
    private final boolean overwriteExisting;

    /**
     * Constructs a new KeyRestoreInfo with the specified parameters.
     *
     * @param restoreId the restore identifier
     * @param backupLocation the backup location
     * @param restoreTime the restore timestamp
     * @param keyCount the number of keys restored
     * @param restoredKeyIds the list of successfully restored key identifiers
     * @param failedKeyIds the list of failed key identifiers
     * @param status the restore status
     * @param message the restore message
     * @param overwriteExisting whether existing keys were overwritten
     */
    public KeyRestoreInfo(String restoreId, String backupLocation, LocalDateTime restoreTime,
                         int keyCount, List<String> restoredKeyIds, List<String> failedKeyIds,
                         String status, String message, boolean overwriteExisting) {
        this.restoreId = Objects.requireNonNull(restoreId, "Restore ID cannot be null");
        this.backupLocation = Objects.requireNonNull(backupLocation, "Backup location cannot be null");
        this.restoreTime = Objects.requireNonNull(restoreTime, "Restore time cannot be null");
        this.keyCount = keyCount;
        this.restoredKeyIds = restoredKeyIds;
        this.failedKeyIds = failedKeyIds;
        this.status = status;
        this.message = message;
        this.overwriteExisting = overwriteExisting;
    }

    /**
     * Gets the restore identifier.
     *
     * @return the restore identifier
     */
    public String getRestoreId() {
        return restoreId;
    }

    /**
     * Gets the backup location.
     *
     * @return the backup location
     */
    public String getBackupLocation() {
        return backupLocation;
    }

    /**
     * Gets the restore timestamp.
     *
     * @return the restore timestamp
     */
    public LocalDateTime getRestoreTime() {
        return restoreTime;
    }

    /**
     * Gets the number of keys restored.
     *
     * @return the key count
     */
    public int getKeyCount() {
        return keyCount;
    }

    /**
     * Gets the list of successfully restored key identifiers.
     *
     * @return the list of restored key identifiers
     */
    public List<String> getRestoredKeyIds() {
        return restoredKeyIds;
    }

    /**
     * Gets the list of failed key identifiers.
     *
     * @return the list of failed key identifiers
     */
    public List<String> getFailedKeyIds() {
        return failedKeyIds;
    }

    /**
     * Gets the restore status.
     *
     * @return the restore status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the restore message.
     *
     * @return the restore message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Checks if existing keys were overwritten.
     *
     * @return true if existing keys were overwritten, false otherwise
     */
    public boolean isOverwriteExisting() {
        return overwriteExisting;
    }

    /**
     * Checks if the restore was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return "SUCCESS".equals(status);
    }

    /**
     * Gets the number of successfully restored keys.
     *
     * @return the number of restored keys
     */
    public int getRestoredKeyCount() {
        return restoredKeyIds != null ? restoredKeyIds.size() : 0;
    }

    /**
     * Gets the number of failed keys.
     *
     * @return the number of failed keys
     */
    public int getFailedKeyCount() {
        return failedKeyIds != null ? failedKeyIds.size() : 0;
    }

    /**
     * Gets the success rate as a percentage.
     *
     * @return the success rate percentage
     */
    public double getSuccessRate() {
        if (keyCount == 0) {
            return 0.0;
        }
        return (double) getRestoredKeyCount() / keyCount * 100.0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        KeyRestoreInfo that = (KeyRestoreInfo) obj;
        return Objects.equals(restoreId, that.restoreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restoreId);
    }

    @Override
    public String toString() {
        return String.format("KeyRestoreInfo{restoreId='%s', backupLocation='%s', restoreTime=%s, " +
                "keyCount=%d, restoredCount=%d, failedCount=%d, successRate=%.1f%%, status='%s'}",
                restoreId, backupLocation, restoreTime, keyCount, getRestoredKeyCount(),
                getFailedKeyCount(), getSuccessRate(), status);
    }
}