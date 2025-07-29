package com.finqube.iso20022.core.monitoring;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents performance metrics for the ISO 20022 system.
 *
 * <p>This class encapsulates various performance metrics including throughput,
 * response times, error rates, and resource utilization.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class PerformanceMetrics {

    private final String systemId;
    private final Instant startTime;
    private final Instant endTime;
    private final long totalRequests;
    private final long successfulRequests;
    private final long failedRequests;
    private final double averageResponseTimeMillis;
    private final long minResponseTimeMillis;
    private final long maxResponseTimeMillis;
    private final double requestsPerSecond;
    private final double errorRate;
    private final Map<String, Object> customMetrics;
    private final Map<String, Object> metadata;

    /**
     * Constructs a new PerformanceMetrics.
     *
     * @param systemId the system identifier
     * @param startTime the start time of the measurement period
     * @param endTime the end time of the measurement period
     * @param totalRequests the total number of requests
     * @param successfulRequests the number of successful requests
     * @param failedRequests the number of failed requests
     * @param averageResponseTimeMillis the average response time in milliseconds
     * @param minResponseTimeMillis the minimum response time in milliseconds
     * @param maxResponseTimeMillis the maximum response time in milliseconds
     * @param requestsPerSecond the requests per second rate
     * @param errorRate the error rate as a percentage
     * @param customMetrics custom performance metrics
     * @param metadata additional metadata
     */
    public PerformanceMetrics(String systemId, Instant startTime, Instant endTime,
                            long totalRequests, long successfulRequests, long failedRequests,
                            double averageResponseTimeMillis, long minResponseTimeMillis, long maxResponseTimeMillis,
                            double requestsPerSecond, double errorRate, Map<String, Object> customMetrics, Map<String, Object> metadata) {
        this.systemId = Objects.requireNonNull(systemId, "System ID cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.endTime = Objects.requireNonNull(endTime, "End time cannot be null");
        this.totalRequests = totalRequests;
        this.successfulRequests = successfulRequests;
        this.failedRequests = failedRequests;
        this.averageResponseTimeMillis = averageResponseTimeMillis;
        this.minResponseTimeMillis = minResponseTimeMillis;
        this.maxResponseTimeMillis = maxResponseTimeMillis;
        this.requestsPerSecond = requestsPerSecond;
        this.errorRate = errorRate;
        this.customMetrics = customMetrics;
        this.metadata = metadata;
    }

    /**
     * Gets the system identifier.
     *
     * @return the system identifier
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * Gets the start time of the measurement period.
     *
     * @return the start time
     */
    public Instant getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time of the measurement period.
     *
     * @return the end time
     */
    public Instant getEndTime() {
        return endTime;
    }

    /**
     * Gets the total number of requests.
     *
     * @return the total requests count
     */
    public long getTotalRequests() {
        return totalRequests;
    }

    /**
     * Gets the number of successful requests.
     *
     * @return the successful requests count
     */
    public long getSuccessfulRequests() {
        return successfulRequests;
    }

    /**
     * Gets the number of failed requests.
     *
     * @return the failed requests count
     */
    public long getFailedRequests() {
        return failedRequests;
    }

    /**
     * Gets the average response time in milliseconds.
     *
     * @return the average response time
     */
    public double getAverageResponseTimeMillis() {
        return averageResponseTimeMillis;
    }

    /**
     * Gets the minimum response time in milliseconds.
     *
     * @return the minimum response time
     */
    public long getMinResponseTimeMillis() {
        return minResponseTimeMillis;
    }

    /**
     * Gets the maximum response time in milliseconds.
     *
     * @return the maximum response time
     */
    public long getMaxResponseTimeMillis() {
        return maxResponseTimeMillis;
    }

    /**
     * Gets the requests per second rate.
     *
     * @return the requests per second
     */
    public double getRequestsPerSecond() {
        return requestsPerSecond;
    }

    /**
     * Gets the error rate as a percentage.
     *
     * @return the error rate percentage
     */
    public double getErrorRate() {
        return errorRate;
    }

    /**
     * Gets the success rate as a percentage.
     *
     * @return the success rate percentage
     */
    public double getSuccessRate() {
        if (totalRequests == 0) {
            return 0.0;
        }
        return (double) successfulRequests / totalRequests * 100.0;
    }

    /**
     * Gets custom performance metrics.
     *
     * @return the custom metrics
     */
    public Map<String, Object> getCustomMetrics() {
        return customMetrics;
    }

    /**
     * Gets additional metadata.
     *
     * @return the metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Gets the measurement duration in milliseconds.
     *
     * @return the duration in milliseconds
     */
    public long getDurationMillis() {
        return endTime.toEpochMilli() - startTime.toEpochMilli();
    }

    /**
     * Gets the measurement duration in seconds.
     *
     * @return the duration in seconds
     */
    public double getDurationSeconds() {
        return getDurationMillis() / 1000.0;
    }

    @Override
    public String toString() {
        return "PerformanceMetrics{" +
                "systemId='" + systemId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", totalRequests=" + totalRequests +
                ", successfulRequests=" + successfulRequests +
                ", failedRequests=" + failedRequests +
                ", successRate=" + String.format("%.2f%%", getSuccessRate()) +
                ", errorRate=" + String.format("%.2f%%", errorRate) +
                ", averageResponseTime=" + String.format("%.2fms", averageResponseTimeMillis) +
                ", requestsPerSecond=" + String.format("%.2f", requestsPerSecond) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerformanceMetrics that = (PerformanceMetrics) o;
        return totalRequests == that.totalRequests &&
                successfulRequests == that.successfulRequests &&
                failedRequests == that.failedRequests &&
                Double.compare(that.averageResponseTimeMillis, averageResponseTimeMillis) == 0 &&
                minResponseTimeMillis == that.minResponseTimeMillis &&
                maxResponseTimeMillis == that.maxResponseTimeMillis &&
                Double.compare(that.requestsPerSecond, requestsPerSecond) == 0 &&
                Double.compare(that.errorRate, errorRate) == 0 &&
                Objects.equals(systemId, that.systemId) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(systemId, startTime, endTime, totalRequests, successfulRequests, failedRequests,
                averageResponseTimeMillis, minResponseTimeMillis, maxResponseTimeMillis, requestsPerSecond, errorRate);
    }
}
