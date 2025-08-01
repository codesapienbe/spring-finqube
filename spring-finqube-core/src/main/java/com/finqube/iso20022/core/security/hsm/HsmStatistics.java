package com.finqube.iso20022.core.security.hsm;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Statistics and performance metrics for HSM operations.
 *
 * <p>This class provides comprehensive statistics about HSM operations including
 * operation counts, response times, error rates, and other performance metrics
 * for monitoring and capacity planning.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class HsmStatistics {

    private final LocalDateTime timestamp;
    private final long totalOperations;
    private final long successfulOperations;
    private final long failedOperations;
    private final long cancelledOperations;
    private final long timeoutOperations;
    private final double averageResponseTimeMs;
    private final double minResponseTimeMs;
    private final double maxResponseTimeMs;
    private final long totalKeys;
    private final long activeKeys;
    private final long expiredKeys;
    private final long totalCertificates;
    private final long validCertificates;
    private final long expiredCertificates;
    private final HsmProvider.ConnectionStatus connectionStatus;
    private final long uptimeSeconds;
    private final double cpuUsagePercent;
    private final double memoryUsagePercent;
    private final long freeMemoryBytes;
    private final long totalMemoryBytes;

    /**
     * Constructs a new HsmStatistics instance.
     *
     * @param timestamp the timestamp when statistics were collected
     * @param totalOperations the total number of operations
     * @param successfulOperations the number of successful operations
     * @param failedOperations the number of failed operations
     * @param cancelledOperations the number of cancelled operations
     * @param timeoutOperations the number of timeout operations
     * @param averageResponseTimeMs the average response time in milliseconds
     * @param minResponseTimeMs the minimum response time in milliseconds
     * @param maxResponseTimeMs the maximum response time in milliseconds
     * @param totalKeys the total number of keys
     * @param activeKeys the number of active keys
     * @param expiredKeys the number of expired keys
     * @param totalCertificates the total number of certificates
     * @param validCertificates the number of valid certificates
     * @param expiredCertificates the number of expired certificates
     * @param connectionStatus the current connection status
     * @param uptimeSeconds the uptime in seconds
     * @param cpuUsagePercent the CPU usage percentage
     * @param memoryUsagePercent the memory usage percentage
     * @param freeMemoryBytes the free memory in bytes
     * @param totalMemoryBytes the total memory in bytes
     */
    public HsmStatistics(LocalDateTime timestamp, long totalOperations, long successfulOperations,
                        long failedOperations, long cancelledOperations, long timeoutOperations,
                        double averageResponseTimeMs, double minResponseTimeMs, double maxResponseTimeMs,
                        long totalKeys, long activeKeys, long expiredKeys, long totalCertificates,
                        long validCertificates, long expiredCertificates, HsmProvider.ConnectionStatus connectionStatus,
                        long uptimeSeconds, double cpuUsagePercent, double memoryUsagePercent,
                        long freeMemoryBytes, long totalMemoryBytes) {
        this.timestamp = timestamp;
        this.totalOperations = totalOperations;
        this.successfulOperations = successfulOperations;
        this.failedOperations = failedOperations;
        this.cancelledOperations = cancelledOperations;
        this.timeoutOperations = timeoutOperations;
        this.averageResponseTimeMs = averageResponseTimeMs;
        this.minResponseTimeMs = minResponseTimeMs;
        this.maxResponseTimeMs = maxResponseTimeMs;
        this.totalKeys = totalKeys;
        this.activeKeys = activeKeys;
        this.expiredKeys = expiredKeys;
        this.totalCertificates = totalCertificates;
        this.validCertificates = validCertificates;
        this.expiredCertificates = expiredCertificates;
        this.connectionStatus = connectionStatus;
        this.uptimeSeconds = uptimeSeconds;
        this.cpuUsagePercent = cpuUsagePercent;
        this.memoryUsagePercent = memoryUsagePercent;
        this.freeMemoryBytes = freeMemoryBytes;
        this.totalMemoryBytes = totalMemoryBytes;
    }

    /**
     * Gets the timestamp when statistics were collected.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the total number of operations.
     *
     * @return the total operations count
     */
    public long getTotalOperations() {
        return totalOperations;
    }

    /**
     * Gets the number of successful operations.
     *
     * @return the successful operations count
     */
    public long getSuccessfulOperations() {
        return successfulOperations;
    }

    /**
     * Gets the number of failed operations.
     *
     * @return the failed operations count
     */
    public long getFailedOperations() {
        return failedOperations;
    }

    /**
     * Gets the number of cancelled operations.
     *
     * @return the cancelled operations count
     */
    public long getCancelledOperations() {
        return cancelledOperations;
    }

    /**
     * Gets the number of timeout operations.
     *
     * @return the timeout operations count
     */
    public long getTimeoutOperations() {
        return timeoutOperations;
    }

    /**
     * Gets the average response time in milliseconds.
     *
     * @return the average response time
     */
    public double getAverageResponseTimeMs() {
        return averageResponseTimeMs;
    }

    /**
     * Gets the minimum response time in milliseconds.
     *
     * @return the minimum response time
     */
    public double getMinResponseTimeMs() {
        return minResponseTimeMs;
    }

    /**
     * Gets the maximum response time in milliseconds.
     *
     * @return the maximum response time
     */
    public double getMaxResponseTimeMs() {
        return maxResponseTimeMs;
    }

    /**
     * Gets the total number of keys.
     *
     * @return the total keys count
     */
    public long getTotalKeys() {
        return totalKeys;
    }

    /**
     * Gets the number of active keys.
     *
     * @return the active keys count
     */
    public long getActiveKeys() {
        return activeKeys;
    }

    /**
     * Gets the number of expired keys.
     *
     * @return the expired keys count
     */
    public long getExpiredKeys() {
        return expiredKeys;
    }

    /**
     * Gets the total number of certificates.
     *
     * @return the total certificates count
     */
    public long getTotalCertificates() {
        return totalCertificates;
    }

    /**
     * Gets the number of valid certificates.
     *
     * @return the valid certificates count
     */
    public long getValidCertificates() {
        return validCertificates;
    }

    /**
     * Gets the number of expired certificates.
     *
     * @return the expired certificates count
     */
    public long getExpiredCertificates() {
        return expiredCertificates;
    }

    /**
     * Gets the current connection status.
     *
     * @return the connection status
     */
    public HsmProvider.ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    /**
     * Gets the uptime in seconds.
     *
     * @return the uptime in seconds
     */
    public long getUptimeSeconds() {
        return uptimeSeconds;
    }

    /**
     * Gets the CPU usage percentage.
     *
     * @return the CPU usage percentage
     */
    public double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    /**
     * Gets the memory usage percentage.
     *
     * @return the memory usage percentage
     */
    public double getMemoryUsagePercent() {
        return memoryUsagePercent;
    }

    /**
     * Gets the free memory in bytes.
     *
     * @return the free memory in bytes
     */
    public long getFreeMemoryBytes() {
        return freeMemoryBytes;
    }

    /**
     * Gets the total memory in bytes.
     *
     * @return the total memory in bytes
     */
    public long getTotalMemoryBytes() {
        return totalMemoryBytes;
    }

    /**
     * Calculates the success rate as a percentage.
     *
     * @return the success rate percentage, or 0.0 if no operations
     */
    public double getSuccessRate() {
        if (totalOperations == 0) {
            return 0.0;
        }
        return (double) successfulOperations / totalOperations * 100.0;
    }

    /**
     * Calculates the failure rate as a percentage.
     *
     * @return the failure rate percentage, or 0.0 if no operations
     */
    public double getFailureRate() {
        if (totalOperations == 0) {
            return 0.0;
        }
        return (double) failedOperations / totalOperations * 100.0;
    }

    /**
     * Calculates the timeout rate as a percentage.
     *
     * @return the timeout rate percentage, or 0.0 if no operations
     */
    public double getTimeoutRate() {
        if (totalOperations == 0) {
            return 0.0;
        }
        return (double) timeoutOperations / totalOperations * 100.0;
    }

    /**
     * Gets the used memory in bytes.
     *
     * @return the used memory in bytes
     */
    public long getUsedMemoryBytes() {
        return totalMemoryBytes - freeMemoryBytes;
    }

    /**
     * Gets the uptime in a human-readable format.
     *
     * @return the uptime as a formatted string
     */
    public String getUptimeFormatted() {
        long days = uptimeSeconds / 86400;
        long hours = (uptimeSeconds % 86400) / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;

        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HsmStatistics that = (HsmStatistics) obj;
        return Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    @Override
    public String toString() {
        return String.format("HsmStatistics{timestamp=%s, totalOperations=%d, successfulOperations=%d, " +
                           "failedOperations=%d, successRate=%.2f%%, averageResponseTimeMs=%.2f, " +
                           "connectionStatus=%s, uptime=%s}",
                           timestamp, totalOperations, successfulOperations, failedOperations,
                           getSuccessRate(), averageResponseTimeMs, connectionStatus, getUptimeFormatted());
    }
}
