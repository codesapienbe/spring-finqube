package com.finqube.iso20022.core.monitoring;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Statistics and metrics for monitoring operations.
 *
 * <p>This class tracks performance metrics and statistics for monitoring operations,
 * including operation counts, timing information, and performance indicators.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class MonitoringStatistics {

    private final String monitoringManagerId;
    private final Instant startTime;
    private final AtomicLong totalOperations;
    private final AtomicLong successfulOperations;
    private final AtomicLong failedOperations;
    private final AtomicLong totalOperationTimeMillis;
    private final AtomicLong minOperationTimeMillis;
    private final AtomicLong maxOperationTimeMillis;
    private final Map<String, Long> operationTypeCounts;
    private final Map<String, Long> errorTypeCounts;

    /**
     * Creates a new monitoring statistics instance.
     *
     * @param monitoringManagerId the unique identifier for the monitoring manager
     */
    public MonitoringStatistics(String monitoringManagerId) {
        this.monitoringManagerId = Objects.requireNonNull(monitoringManagerId, "Monitoring manager ID cannot be null");
        this.startTime = Instant.now();
        this.totalOperations = new AtomicLong(0);
        this.successfulOperations = new AtomicLong(0);
        this.failedOperations = new AtomicLong(0);
        this.totalOperationTimeMillis = new AtomicLong(0);
        this.minOperationTimeMillis = new AtomicLong(Long.MAX_VALUE);
        this.maxOperationTimeMillis = new AtomicLong(0);
        this.operationTypeCounts = new java.util.concurrent.ConcurrentHashMap<>();
        this.errorTypeCounts = new java.util.concurrent.ConcurrentHashMap<>();
    }

    /**
     * Records a successful operation.
     *
     * @param operationType the type of operation performed
     * @param operationTimeMillis the time taken for the operation in milliseconds
     */
    public void recordSuccessfulOperation(String operationType, long operationTimeMillis) {
        totalOperations.incrementAndGet();
        successfulOperations.incrementAndGet();
        totalOperationTimeMillis.addAndGet(operationTimeMillis);

        // Update min/max times
        minOperationTimeMillis.updateAndGet(current -> Math.min(current, operationTimeMillis));
        maxOperationTimeMillis.updateAndGet(current -> Math.max(current, operationTimeMillis));

        // Update operation type counts
        operationTypeCounts.merge(operationType, 1L, Long::sum);
    }

    /**
     * Records a failed operation.
     *
     * @param operationType the type of operation that failed
     * @param errorType the type of error that occurred
     * @param operationTimeMillis the time taken before failure in milliseconds
     */
    public void recordFailedOperation(String operationType, String errorType, long operationTimeMillis) {
        totalOperations.incrementAndGet();
        failedOperations.incrementAndGet();
        totalOperationTimeMillis.addAndGet(operationTimeMillis);

        // Update min/max times
        minOperationTimeMillis.updateAndGet(current -> Math.min(current, operationTimeMillis));
        maxOperationTimeMillis.updateAndGet(current -> Math.max(current, operationTimeMillis));

        // Update counts
        operationTypeCounts.merge(operationType, 1L, Long::sum);
        errorTypeCounts.merge(errorType, 1L, Long::sum);
    }

    /**
     * Records a metric value.
     *
     * @param metricName the name of the metric
     * @param value the metric value
     */
    public void recordMetric(String metricName, double value) {
        // This method can be extended to track specific metrics
        // For now, we'll just increment the operation count
        totalOperations.incrementAndGet();
    }

    /**
     * Gets the monitoring manager ID.
     *
     * @return the monitoring manager ID
     */
    public String getMonitoringManagerId() {
        return monitoringManagerId;
    }

    /**
     * Gets the start time of monitoring.
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
     * @return the success rate percentage (0.0 to 100.0)
     */
    public double getSuccessRate() {
        long total = totalOperations.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) successfulOperations.get() / total * 100.0;
    }

    /**
     * Gets the total operation time in milliseconds.
     *
     * @return the total operation time
     */
    public long getTotalOperationTimeMillis() {
        return totalOperationTimeMillis.get();
    }

    /**
     * Gets the minimum operation time in milliseconds.
     *
     * @return the minimum operation time, or Long.MAX_VALUE if no operations
     */
    public long getMinOperationTimeMillis() {
        long min = minOperationTimeMillis.get();
        return min == Long.MAX_VALUE ? 0 : min;
    }

    /**
     * Gets the maximum operation time in milliseconds.
     *
     * @return the maximum operation time
     */
    public long getMaxOperationTimeMillis() {
        return maxOperationTimeMillis.get();
    }

    /**
     * Gets the average operation time in milliseconds.
     *
     * @return the average operation time, or 0.0 if no operations
     */
    public double getAverageOperationTimeMillis() {
        long total = totalOperations.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) totalOperationTimeMillis.get() / total;
    }

    /**
     * Gets the operations per second rate.
     *
     * @return the operations per second rate
     */
    public double getOperationsPerSecond() {
        long total = totalOperations.get();
        if (total == 0) {
            return 0.0;
        }

        long uptimeMillis = System.currentTimeMillis() - startTime.toEpochMilli();
        if (uptimeMillis == 0) {
            return 0.0;
        }

        return (double) total / (uptimeMillis / 1000.0);
    }

    /**
     * Gets the operation type counts.
     *
     * @return a map of operation types to their counts
     */
    public Map<String, Long> getOperationTypeCounts() {
        return new java.util.HashMap<>(operationTypeCounts);
    }

    /**
     * Gets the error type counts.
     *
     * @return a map of error types to their counts
     */
    public Map<String, Long> getErrorTypeCounts() {
        return new java.util.HashMap<>(errorTypeCounts);
    }

    /**
     * Resets all statistics.
     */
    public void reset() {
        totalOperations.set(0);
        successfulOperations.set(0);
        failedOperations.set(0);
        totalOperationTimeMillis.set(0);
        minOperationTimeMillis.set(Long.MAX_VALUE);
        maxOperationTimeMillis.set(0);
        operationTypeCounts.clear();
        errorTypeCounts.clear();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MonitoringStatistics that = (MonitoringStatistics) obj;
        return Objects.equals(monitoringManagerId, that.monitoringManagerId) &&
               Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monitoringManagerId, startTime);
    }

    @Override
    public String toString() {
        return String.format("MonitoringStatistics{monitoringManagerId='%s', totalOperations=%d, " +
                           "successfulOperations=%d, failedOperations=%d, successRate=%.2f%%, " +
                           "averageOperationTime=%.2fms, operationsPerSecond=%.2f}",
                           monitoringManagerId, getTotalOperations(), getSuccessfulOperations(),
                           getFailedOperations(), getSuccessRate(), getAverageOperationTimeMillis(),
                           getOperationsPerSecond());
    }
}
