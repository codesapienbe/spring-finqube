package com.finqube.iso20022.core.security.key;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Statistics for key rotation operations.
 *
 * <p>This class provides comprehensive statistics and metrics for key rotation
 * operations, including success rates, timing information, and operational metrics.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class KeyRotationStatistics {

    private final String keyRotationManagerId;
    private final String displayName;
    private final String version;
    private final LocalDateTime timestamp;
    private final Map<String, Long> operationCounts;
    private final Map<String, Long> errorCounts;
    private final long totalRotations;
    private final long successfulRotations;
    private final long failedRotations;
    private final long pendingRotations;
    private final long scheduledRotations;
    private final long rolledBackRotations;
    private final double successRate;
    private final long averageRotationTimeMs;
    private final long minRotationTimeMs;
    private final long maxRotationTimeMs;
    private final int totalKeys;
    private final int activeKeys;
    private final int expiringKeys;
    private final int expiredKeys;
    private final boolean healthy;

    /**
     * Constructs a new KeyRotationStatistics with the specified parameters.
     *
     * @param keyRotationManagerId the key rotation manager identifier
     * @param displayName the display name
     * @param version the version
     * @param timestamp the timestamp
     * @param operationCounts the operation counts
     * @param errorCounts the error counts
     * @param totalRotations the total number of rotations
     * @param successfulRotations the number of successful rotations
     * @param failedRotations the number of failed rotations
     * @param pendingRotations the number of pending rotations
     * @param scheduledRotations the number of scheduled rotations
     * @param rolledBackRotations the number of rolled back rotations
     * @param successRate the success rate
     * @param averageRotationTimeMs the average rotation time in milliseconds
     * @param minRotationTimeMs the minimum rotation time in milliseconds
     * @param maxRotationTimeMs the maximum rotation time in milliseconds
     * @param totalKeys the total number of keys
     * @param activeKeys the number of active keys
     * @param expiringKeys the number of expiring keys
     * @param expiredKeys the number of expired keys
     * @param healthy whether the system is healthy
     */
    public KeyRotationStatistics(String keyRotationManagerId, String displayName, String version,
                               LocalDateTime timestamp, Map<String, Long> operationCounts, Map<String, Long> errorCounts,
                               long totalRotations, long successfulRotations, long failedRotations,
                               long pendingRotations, long scheduledRotations, long rolledBackRotations,
                               double successRate, long averageRotationTimeMs, long minRotationTimeMs,
                               long maxRotationTimeMs, int totalKeys, int activeKeys, int expiringKeys,
                               int expiredKeys, boolean healthy) {
        this.keyRotationManagerId = keyRotationManagerId;
        this.displayName = displayName;
        this.version = version;
        this.timestamp = timestamp;
        this.operationCounts = operationCounts;
        this.errorCounts = errorCounts;
        this.totalRotations = totalRotations;
        this.successfulRotations = successfulRotations;
        this.failedRotations = failedRotations;
        this.pendingRotations = pendingRotations;
        this.scheduledRotations = scheduledRotations;
        this.rolledBackRotations = rolledBackRotations;
        this.successRate = successRate;
        this.averageRotationTimeMs = averageRotationTimeMs;
        this.minRotationTimeMs = minRotationTimeMs;
        this.maxRotationTimeMs = maxRotationTimeMs;
        this.totalKeys = totalKeys;
        this.activeKeys = activeKeys;
        this.expiringKeys = expiringKeys;
        this.expiredKeys = expiredKeys;
        this.healthy = healthy;
    }

    /**
     * Gets the key rotation manager identifier.
     *
     * @return the key rotation manager identifier
     */
    public String getKeyRotationManagerId() {
        return keyRotationManagerId;
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
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
     * Gets the operation counts.
     *
     * @return the operation counts
     */
    public Map<String, Long> getOperationCounts() {
        return operationCounts;
    }

    /**
     * Gets the error counts.
     *
     * @return the error counts
     */
    public Map<String, Long> getErrorCounts() {
        return errorCounts;
    }

    /**
     * Gets the total number of rotations.
     *
     * @return the total number of rotations
     */
    public long getTotalRotations() {
        return totalRotations;
    }

    /**
     * Gets the number of successful rotations.
     *
     * @return the number of successful rotations
     */
    public long getSuccessfulRotations() {
        return successfulRotations;
    }

    /**
     * Gets the number of failed rotations.
     *
     * @return the number of failed rotations
     */
    public long getFailedRotations() {
        return failedRotations;
    }

    /**
     * Gets the number of pending rotations.
     *
     * @return the number of pending rotations
     */
    public long getPendingRotations() {
        return pendingRotations;
    }

    /**
     * Gets the number of scheduled rotations.
     *
     * @return the number of scheduled rotations
     */
    public long getScheduledRotations() {
        return scheduledRotations;
    }

    /**
     * Gets the number of rolled back rotations.
     *
     * @return the number of rolled back rotations
     */
    public long getRolledBackRotations() {
        return rolledBackRotations;
    }

    /**
     * Gets the success rate.
     *
     * @return the success rate as a percentage
     */
    public double getSuccessRate() {
        return successRate;
    }

    /**
     * Gets the formatted success rate.
     *
     * @return the formatted success rate string
     */
    public String getFormattedSuccessRate() {
        return String.format("%.2f%%", successRate);
    }

    /**
     * Gets the average rotation time in milliseconds.
     *
     * @return the average rotation time in milliseconds
     */
    public long getAverageRotationTimeMs() {
        return averageRotationTimeMs;
    }

    /**
     * Gets the formatted average rotation time.
     *
     * @return the formatted average rotation time string
     */
    public String getFormattedAverageRotationTime() {
        if (averageRotationTimeMs < 1000) {
            return averageRotationTimeMs + "ms";
        } else if (averageRotationTimeMs < 60000) {
            return String.format("%.2fs", averageRotationTimeMs / 1000.0);
        } else {
            long minutes = averageRotationTimeMs / 60000;
            long seconds = (averageRotationTimeMs % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }

    /**
     * Gets the minimum rotation time in milliseconds.
     *
     * @return the minimum rotation time in milliseconds
     */
    public long getMinRotationTimeMs() {
        return minRotationTimeMs;
    }

    /**
     * Gets the formatted minimum rotation time.
     *
     * @return the formatted minimum rotation time string
     */
    public String getFormattedMinRotationTime() {
        if (minRotationTimeMs < 1000) {
            return minRotationTimeMs + "ms";
        } else if (minRotationTimeMs < 60000) {
            return String.format("%.2fs", minRotationTimeMs / 1000.0);
        } else {
            long minutes = minRotationTimeMs / 60000;
            long seconds = (minRotationTimeMs % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }

    /**
     * Gets the maximum rotation time in milliseconds.
     *
     * @return the maximum rotation time in milliseconds
     */
    public long getMaxRotationTimeMs() {
        return maxRotationTimeMs;
    }

    /**
     * Gets the formatted maximum rotation time.
     *
     * @return the formatted maximum rotation time string
     */
    public String getFormattedMaxRotationTime() {
        if (maxRotationTimeMs < 1000) {
            return maxRotationTimeMs + "ms";
        } else if (maxRotationTimeMs < 60000) {
            return String.format("%.2fs", maxRotationTimeMs / 1000.0);
        } else {
            long minutes = maxRotationTimeMs / 60000;
            long seconds = (maxRotationTimeMs % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }

    /**
     * Gets the total number of keys.
     *
     * @return the total number of keys
     */
    public int getTotalKeys() {
        return totalKeys;
    }

    /**
     * Gets the number of active keys.
     *
     * @return the number of active keys
     */
    public int getActiveKeys() {
        return activeKeys;
    }

    /**
     * Gets the number of expiring keys.
     *
     * @return the number of expiring keys
     */
    public int getExpiringKeys() {
        return expiringKeys;
    }

    /**
     * Gets the number of expired keys.
     *
     * @return the number of expired keys
     */
    public int getExpiredKeys() {
        return expiredKeys;
    }

    /**
     * Checks if the system is healthy.
     *
     * @return true if healthy, false otherwise
     */
    public boolean isHealthy() {
        return healthy;
    }

    /**
     * Gets a summary of the statistics.
     *
     * @return the summary
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Key Rotation Statistics: ");
        sb.append(totalRotations).append(" total rotations, ");
        sb.append(successfulRotations).append(" successful (");
        sb.append(getFormattedSuccessRate()).append("), ");
        sb.append(failedRotations).append(" failed, ");
        sb.append("avg time: ").append(getFormattedAverageRotationTime());
        sb.append(", ").append(totalKeys).append(" total keys, ");
        sb.append(activeKeys).append(" active, ");
        sb.append(expiringKeys).append(" expiring, ");
        sb.append(expiredKeys).append(" expired");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("KeyRotationStatistics{managerId='%s', totalRotations=%d, successRate=%s, healthy=%s}",
                keyRotationManagerId, totalRotations, getFormattedSuccessRate(), healthy);
    }
}
