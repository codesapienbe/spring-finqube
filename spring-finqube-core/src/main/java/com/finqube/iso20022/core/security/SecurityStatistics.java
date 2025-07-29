package com.finqube.iso20022.core.security;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Statistics and metrics for security operations.
 *
 * <p>This class tracks various performance metrics and statistics for security operations,
 * including encryption, decryption, signing, and verification counts and timing.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class SecurityStatistics {

    private final String securityManagerId;
    private final Instant startTime;
    private final AtomicLong totalOperations;
    private final AtomicLong successfulOperations;
    private final AtomicLong failedOperations;
    private final AtomicLong totalProcessingTimeMillis;
    private final AtomicLong minProcessingTimeMillis;
    private final AtomicLong maxProcessingTimeMillis;
    private final Map<String, Long> operationTypeCounts;
    private final Map<String, Long> errorTypeCounts;

    /**
     * Constructs a new SecurityStatistics.
     *
     * @param securityManagerId the security manager identifier
     * @param startTime when statistics collection started
     * @param operationTypeCounts map of operation types to counts
     * @param errorTypeCounts map of error types to counts
     */
    public SecurityStatistics(String securityManagerId, Instant startTime,
                            Map<String, Long> operationTypeCounts, Map<String, Long> errorTypeCounts) {
        this.securityManagerId = Objects.requireNonNull(securityManagerId, "Security manager ID cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.totalOperations = new AtomicLong(0);
        this.successfulOperations = new AtomicLong(0);
        this.failedOperations = new AtomicLong(0);
        this.totalProcessingTimeMillis = new AtomicLong(0);
        this.minProcessingTimeMillis = new AtomicLong(Long.MAX_VALUE);
        this.maxProcessingTimeMillis = new AtomicLong(0);
        this.operationTypeCounts = operationTypeCounts;
        this.errorTypeCounts = errorTypeCounts;
    }

    /**
     * Records a successful security operation.
     *
     * @param operationType the type of operation
     * @param processingTimeMillis the processing time in milliseconds
     */
    public void recordSuccess(String operationType, long processingTimeMillis) {
        totalOperations.incrementAndGet();
        successfulOperations.incrementAndGet();
        totalProcessingTimeMillis.addAndGet(processingTimeMillis);
        updateMinMaxProcessingTime(processingTimeMillis);

        if (operationType != null) {
            operationTypeCounts.merge(operationType, 1L, Long::sum);
        }
    }

    /**
     * Records a failed security operation.
     *
     * @param operationType the type of operation
     * @param errorType the type of error
     * @param processingTimeMillis the processing time in milliseconds
     */
    public void recordFailure(String operationType, String errorType, long processingTimeMillis) {
        totalOperations.incrementAndGet();
        failedOperations.incrementAndGet();
        totalProcessingTimeMillis.addAndGet(processingTimeMillis);
        updateMinMaxProcessingTime(processingTimeMillis);

        if (operationType != null) {
            operationTypeCounts.merge(operationType, 1L, Long::sum);
        }

        if (errorType != null) {
            errorTypeCounts.merge(errorType, 1L, Long::sum);
        }
    }

    private void updateMinMaxProcessingTime(long processingTimeMillis) {
        minProcessingTimeMillis.updateAndGet(current -> Math.min(current, processingTimeMillis));
        maxProcessingTimeMillis.updateAndGet(current -> Math.max(current, processingTimeMillis));
    }

    /**
     * Gets the security manager identifier.
     *
     * @return the security manager identifier
     */
    public String getSecurityManagerId() {
        return securityManagerId;
    }

    /**
     * Gets when statistics collection started.
     *
     * @return the start time
     */
    public Instant getStartTime() {
        return startTime;
    }

    /**
     * Gets the total number of operations.
     *
     * @return the total operations count
     */
    public long getTotalOperations() {
        return totalOperations.get();
    }

    /**
     * Gets the number of successful operations.
     *
     * @return the successful operations count
     */
    public long getSuccessfulOperations() {
        return successfulOperations.get();
    }

    /**
     * Gets the number of failed operations.
     *
     * @return the failed operations count
     */
    public long getFailedOperations() {
        return failedOperations.get();
    }

    /**
     * Gets the success rate as a percentage.
     *
     * @return the success rate percentage
     */
    public double getSuccessRate() {
        long total = totalOperations.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) successfulOperations.get() / total * 100.0;
    }

    /**
     * Gets the failure rate as a percentage.
     *
     * @return the failure rate percentage
     */
    public double getFailureRate() {
        long total = totalOperations.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) failedOperations.get() / total * 100.0;
    }

    /**
     * Gets the average processing time in milliseconds.
     *
     * @return the average processing time
     */
    public double getAverageProcessingTimeMillis() {
        long total = totalOperations.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) totalProcessingTimeMillis.get() / total;
    }

    /**
     * Gets the minimum processing time in milliseconds.
     *
     * @return the minimum processing time
     */
    public long getMinProcessingTimeMillis() {
        long min = minProcessingTimeMillis.get();
        return min == Long.MAX_VALUE ? 0 : min;
    }

    /**
     * Gets the maximum processing time in milliseconds.
     *
     * @return the maximum processing time
     */
    public long getMaxProcessingTimeMillis() {
        return maxProcessingTimeMillis.get();
    }

    /**
     * Gets the total processing time in milliseconds.
     *
     * @return the total processing time
     */
    public long getTotalProcessingTimeMillis() {
        return totalProcessingTimeMillis.get();
    }

    /**
     * Gets the operation counts by type.
     *
     * @return the operation type counts map
     */
    public Map<String, Long> getOperationTypeCounts() {
        return operationTypeCounts;
    }

    /**
     * Gets the error counts by type.
     *
     * @return the error type counts map
     */
    public Map<String, Long> getErrorTypeCounts() {
        return errorTypeCounts;
    }

    /**
     * Gets the uptime in milliseconds.
     *
     * @return the uptime in milliseconds
     */
    public long getUptimeMillis() {
        return Instant.now().toEpochMilli() - startTime.toEpochMilli();
    }

    /**
     * Gets the operations per second rate.
     *
     * @return the operations per second
     */
    public double getOperationsPerSecond() {
        long uptimeSeconds = getUptimeMillis() / 1000;
        if (uptimeSeconds == 0) {
            return 0.0;
        }
        return (double) totalOperations.get() / uptimeSeconds;
    }

    @Override
    public String toString() {
        return "SecurityStatistics{" +
                "securityManagerId='" + securityManagerId + '\'' +
                ", startTime=" + startTime +
                ", totalOperations=" + totalOperations.get() +
                ", successfulOperations=" + successfulOperations.get() +
                ", failedOperations=" + failedOperations.get() +
                ", successRate=" + String.format("%.2f%%", getSuccessRate()) +
                ", averageProcessingTime=" + String.format("%.2fms", getAverageProcessingTimeMillis()) +
                ", operationsPerSecond=" + String.format("%.2f", getOperationsPerSecond()) +
                '}';
    }
}
