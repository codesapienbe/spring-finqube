package com.finqube.iso20022.core.security.storage;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Health check result for key storage operations.
 *
 * <p>This class provides detailed information about the health and status
 * of the key storage system, including connectivity, performance, and error rates.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class KeyStorageHealthCheck {

    private final String storageServiceId;
    private final LocalDateTime timestamp;
    private final String status;
    private final String message;
    private final boolean healthy;
    private final long responseTimeMs;
    private final double errorRate;
    private final long totalOperations;
    private final long failedOperations;
    private final String storageBackend;
    private final boolean backupAvailable;
    private final long lastBackupAgeHours;
    private final String encryptionStatus;
    private final String accessControlStatus;

    /**
     * Constructs a new KeyStorageHealthCheck with the specified parameters.
     *
     * @param storageServiceId the storage service identifier
     * @param timestamp the health check timestamp
     * @param status the health status
     * @param message the health check message
     * @param healthy whether the storage is healthy
     * @param responseTimeMs the response time in milliseconds
     * @param errorRate the error rate as a percentage
     * @param totalOperations the total number of operations
     * @param failedOperations the number of failed operations
     * @param storageBackend the storage backend type
     * @param backupAvailable whether backup is available
     * @param lastBackupAgeHours the age of the last backup in hours
     * @param encryptionStatus the encryption status
     * @param accessControlStatus the access control status
     */
    public KeyStorageHealthCheck(String storageServiceId, LocalDateTime timestamp, String status,
                                String message, boolean healthy, long responseTimeMs, double errorRate,
                                long totalOperations, long failedOperations, String storageBackend,
                                boolean backupAvailable, long lastBackupAgeHours, String encryptionStatus,
                                String accessControlStatus) {
        this.storageServiceId = Objects.requireNonNull(storageServiceId, "Storage service ID cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.message = message;
        this.healthy = healthy;
        this.responseTimeMs = responseTimeMs;
        this.errorRate = errorRate;
        this.totalOperations = totalOperations;
        this.failedOperations = failedOperations;
        this.storageBackend = storageBackend;
        this.backupAvailable = backupAvailable;
        this.lastBackupAgeHours = lastBackupAgeHours;
        this.encryptionStatus = encryptionStatus;
        this.accessControlStatus = accessControlStatus;
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
     * Gets the health check timestamp.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the health status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the health check message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Checks if the storage is healthy.
     *
     * @return true if healthy, false otherwise
     */
    public boolean isHealthy() {
        return healthy;
    }

    /**
     * Gets the response time in milliseconds.
     *
     * @return the response time
     */
    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    /**
     * Gets the error rate as a percentage.
     *
     * @return the error rate
     */
    public double getErrorRate() {
        return errorRate;
    }

    /**
     * Gets the total number of operations.
     *
     * @return the total operations
     */
    public long getTotalOperations() {
        return totalOperations;
    }

    /**
     * Gets the number of failed operations.
     *
     * @return the failed operations
     */
    public long getFailedOperations() {
        return failedOperations;
    }

    /**
     * Gets the storage backend type.
     *
     * @return the storage backend
     */
    public String getStorageBackend() {
        return storageBackend;
    }

    /**
     * Checks if backup is available.
     *
     * @return true if backup is available, false otherwise
     */
    public boolean isBackupAvailable() {
        return backupAvailable;
    }

    /**
     * Gets the age of the last backup in hours.
     *
     * @return the last backup age in hours
     */
    public long getLastBackupAgeHours() {
        return lastBackupAgeHours;
    }

    /**
     * Gets the encryption status.
     *
     * @return the encryption status
     */
    public String getEncryptionStatus() {
        return encryptionStatus;
    }

    /**
     * Gets the access control status.
     *
     * @return the access control status
     */
    public String getAccessControlStatus() {
        return accessControlStatus;
    }

    /**
     * Checks if the storage is unhealthy.
     *
     * @return true if unhealthy, false otherwise
     */
    public boolean isUnhealthy() {
        return !healthy;
    }

    /**
     * Gets the success rate as a percentage.
     *
     * @return the success rate percentage
     */
    public double getSuccessRate() {
        if (totalOperations == 0) {
            return 0.0;
        }
        return (double) (totalOperations - failedOperations) / totalOperations * 100.0;
    }

    /**
     * Checks if the response time is acceptable.
     *
     * @param thresholdMs the threshold in milliseconds
     * @return true if response time is acceptable, false otherwise
     */
    public boolean isResponseTimeAcceptable(long thresholdMs) {
        return responseTimeMs <= thresholdMs;
    }

    /**
     * Checks if the error rate is acceptable.
     *
     * @param threshold the error rate threshold as a percentage
     * @return true if error rate is acceptable, false otherwise
     */
    public boolean isErrorRateAcceptable(double threshold) {
        return errorRate <= threshold;
    }

    /**
     * Checks if the backup is recent.
     *
     * @param maxAgeHours the maximum acceptable backup age in hours
     * @return true if backup is recent, false otherwise
     */
    public boolean isBackupRecent(long maxAgeHours) {
        return backupAvailable && lastBackupAgeHours <= maxAgeHours;
    }

    /**
     * Gets a summary of the health check.
     *
     * @return the health check summary
     */
    public String getSummary() {
        return String.format("KeyStorage Health[%s] %s - %s - Response: %dms - Error Rate: %.2f%% - Success Rate: %.1f%%",
                storageServiceId, status, storageBackend, responseTimeMs, errorRate, getSuccessRate());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        KeyStorageHealthCheck that = (KeyStorageHealthCheck) obj;
        return Objects.equals(storageServiceId, that.storageServiceId) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storageServiceId, timestamp);
    }

    @Override
    public String toString() {
        return String.format("KeyStorageHealthCheck{storageServiceId='%s', timestamp=%s, status='%s', " +
                "healthy=%s, responseTimeMs=%d, errorRate=%.2f%%, successRate=%.1f%%, storageBackend='%s', " +
                "backupAvailable=%s, lastBackupAgeHours=%d}", storageServiceId, timestamp, status,
                healthy, responseTimeMs, errorRate, getSuccessRate(), storageBackend,
                backupAvailable, lastBackupAgeHours);
    }
}
