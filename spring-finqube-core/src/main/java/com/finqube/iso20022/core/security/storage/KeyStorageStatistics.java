package com.finqube.iso20022.core.security.storage;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Statistics for key storage operations.
 *
 * <p>This class provides comprehensive statistics about key storage operations
 * including operation counts, performance metrics, and storage usage.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class KeyStorageStatistics {

    private final String storageServiceId;
    private final LocalDateTime timestamp;
    private final long totalKeys;
    private final long activeKeys;
    private final long expiredKeys;
    private final long totalStorageOperations;
    private final long successfulStorageOperations;
    private final long failedStorageOperations;
    private final double averageOperationTimeMs;
    private final long totalStorageSizeBytes;
    private final Map<String, Long> operationsByType;
    private final Map<String, Long> errorsByType;
    private final long lastBackupTime;
    private final int backupCount;
    private final int restoreCount;

    /**
     * Constructs a new KeyStorageStatistics with the specified parameters.
     *
     * @param storageServiceId the storage service identifier
     * @param timestamp the statistics timestamp
     * @param totalKeys the total number of keys
     * @param activeKeys the number of active keys
     * @param expiredKeys the number of expired keys
     * @param totalStorageOperations the total number of storage operations
     * @param successfulStorageOperations the number of successful operations
     * @param failedStorageOperations the number of failed operations
     * @param averageOperationTimeMs the average operation time in milliseconds
     * @param totalStorageSizeBytes the total storage size in bytes
     * @param operationsByType the operations count by type
     * @param errorsByType the errors count by type
     * @param lastBackupTime the last backup timestamp
     * @param backupCount the number of backups performed
     * @param restoreCount the number of restores performed
     */
    public KeyStorageStatistics(String storageServiceId, LocalDateTime timestamp, long totalKeys,
                               long activeKeys, long expiredKeys, long totalStorageOperations,
                               long successfulStorageOperations, long failedStorageOperations,
                               double averageOperationTimeMs, long totalStorageSizeBytes,
                               Map<String, Long> operationsByType, Map<String, Long> errorsByType,
                               long lastBackupTime, int backupCount, int restoreCount) {
        this.storageServiceId = Objects.requireNonNull(storageServiceId, "Storage service ID cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.totalKeys = totalKeys;
        this.activeKeys = activeKeys;
        this.expiredKeys = expiredKeys;
        this.totalStorageOperations = totalStorageOperations;
        this.successfulStorageOperations = successfulStorageOperations;
        this.failedStorageOperations = failedStorageOperations;
        this.averageOperationTimeMs = averageOperationTimeMs;
        this.totalStorageSizeBytes = totalStorageSizeBytes;
        this.operationsByType = operationsByType;
        this.errorsByType = errorsByType;
        this.lastBackupTime = lastBackupTime;
        this.backupCount = backupCount;
        this.restoreCount = restoreCount;
    }

    /**
     * Gets the storage service identifier.
     *
     * @return the storage service identifier
     */
    public String getStorageServiceId() {
        return storageServiceId;
    }

    /**
     * Gets the statistics timestamp.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the total number of keys.
     *
     * @return the total key count
     */
    public long getTotalKeys() {
        return totalKeys;
    }

    /**
     * Gets the number of active keys.
     *
     * @return the active key count
     */
    public long getActiveKeys() {
        return activeKeys;
    }

    /**
     * Gets the number of expired keys.
     *
     * @return the expired key count
     */
    public long getExpiredKeys() {
        return expiredKeys;
    }

    /**
     * Gets the total number of storage operations.
     *
     * @return the total operation count
     */
    public long getTotalStorageOperations() {
        return totalStorageOperations;
    }

    /**
     * Gets the number of successful operations.
     *
     * @return the successful operation count
     */
    public long getSuccessfulStorageOperations() {
        return successfulStorageOperations;
    }

    /**
     * Gets the number of failed operations.
     *
     * @return the failed operation count
     */
    public long getFailedStorageOperations() {
        return failedStorageOperations;
    }

    /**
     * Gets the average operation time in milliseconds.
     *
     * @return the average operation time
     */
    public double getAverageOperationTimeMs() {
        return averageOperationTimeMs;
    }

    /**
     * Gets the total storage size in bytes.
     *
     * @return the total storage size
     */
    public long getTotalStorageSizeBytes() {
        return totalStorageSizeBytes;
    }

    /**
     * Gets the operations count by type.
     *
     * @return the operations by type
     */
    public Map<String, Long> getOperationsByType() {
        return operationsByType;
    }

    /**
     * Gets the errors count by type.
     *
     * @return the errors by type
     */
    public Map<String, Long> getErrorsByType() {
        return errorsByType;
    }

    /**
     * Gets the last backup timestamp.
     *
     * @return the last backup time
     */
    public long getLastBackupTime() {
        return lastBackupTime;
    }

    /**
     * Gets the number of backups performed.
     *
     * @return the backup count
     */
    public int getBackupCount() {
        return backupCount;
    }

    /**
     * Gets the number of restores performed.
     *
     * @return the restore count
     */
    public int getRestoreCount() {
        return restoreCount;
    }

    /**
     * Gets the success rate as a percentage.
     *
     * @return the success rate percentage
     */
    public double getSuccessRate() {
        if (totalStorageOperations == 0) {
            return 0.0;
        }
        return (double) successfulStorageOperations / totalStorageOperations * 100.0;
    }

    /**
     * Gets the failure rate as a percentage.
     *
     * @return the failure rate percentage
     */
    public double getFailureRate() {
        if (totalStorageOperations == 0) {
            return 0.0;
        }
        return (double) failedStorageOperations / totalStorageOperations * 100.0;
    }

    /**
     * Gets the total storage size in a human-readable format.
     *
     * @return the formatted storage size
     */
    public String getFormattedStorageSize() {
        if (totalStorageSizeBytes < 1024) {
            return totalStorageSizeBytes + " B";
        } else if (totalStorageSizeBytes < 1024 * 1024) {
            return String.format("%.1f KB", totalStorageSizeBytes / 1024.0);
        } else if (totalStorageSizeBytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", totalStorageSizeBytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", totalStorageSizeBytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * Gets the average storage size per key.
     *
     * @return the average storage size per key
     */
    public double getAverageStorageSizePerKey() {
        if (totalKeys == 0) {
            return 0.0;
        }
        return (double) totalStorageSizeBytes / totalKeys;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        KeyStorageStatistics that = (KeyStorageStatistics) obj;
        return Objects.equals(storageServiceId, that.storageServiceId) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storageServiceId, timestamp);
    }

    @Override
    public String toString() {
        return String.format("KeyStorageStatistics{storageServiceId='%s', timestamp=%s, " +
                "totalKeys=%d, activeKeys=%d, totalOperations=%d, successRate=%.1f%%, " +
                "avgOperationTime=%.2fms, storageSize=%s}", storageServiceId, timestamp,
                totalKeys, activeKeys, totalStorageOperations, getSuccessRate(),
                averageOperationTimeMs, getFormattedStorageSize());
    }
}
