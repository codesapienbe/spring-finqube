package com.finqube.iso20022.core.transport;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Statistics and metrics for a transport.
 *
 * <p>This class tracks various performance metrics and statistics for transport operations,
 * including message counts, response times, error rates, and other operational data.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class TransportStatistics {

    private final String transportId;
    private final Instant startTime;
    private final AtomicLong totalMessagesSent;
    private final AtomicLong successfulMessages;
    private final AtomicLong failedMessages;
    private final AtomicLong timeoutMessages;
    private final AtomicLong totalResponseTimeMillis;
    private final AtomicLong minResponseTimeMillis;
    private final AtomicLong maxResponseTimeMillis;
    private final Map<String, Long> errorCounts;

    /**
     * Constructs a new TransportStatistics.
     *
     * @param transportId the transport identifier
     * @param startTime when statistics collection started
     * @param errorCounts map of error types to counts
     */
    public TransportStatistics(String transportId, Instant startTime, Map<String, Long> errorCounts) {
        this.transportId = Objects.requireNonNull(transportId, "Transport ID cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.totalMessagesSent = new AtomicLong(0);
        this.successfulMessages = new AtomicLong(0);
        this.failedMessages = new AtomicLong(0);
        this.timeoutMessages = new AtomicLong(0);
        this.totalResponseTimeMillis = new AtomicLong(0);
        this.minResponseTimeMillis = new AtomicLong(Long.MAX_VALUE);
        this.maxResponseTimeMillis = new AtomicLong(0);
        this.errorCounts = errorCounts;
    }

    /**
     * Records a successful message send.
     *
     * @param responseTimeMillis the response time in milliseconds
     */
    public void recordSuccess(long responseTimeMillis) {
        totalMessagesSent.incrementAndGet();
        successfulMessages.incrementAndGet();
        totalResponseTimeMillis.addAndGet(responseTimeMillis);
        updateMinMaxResponseTime(responseTimeMillis);
    }

    /**
     * Records a failed message send.
     *
     * @param responseTimeMillis the response time in milliseconds
     * @param errorType the type of error
     */
    public void recordFailure(long responseTimeMillis, String errorType) {
        totalMessagesSent.incrementAndGet();
        failedMessages.incrementAndGet();
        totalResponseTimeMillis.addAndGet(responseTimeMillis);
        updateMinMaxResponseTime(responseTimeMillis);

        if (errorType != null) {
            errorCounts.merge(errorType, 1L, Long::sum);
        }
    }

    /**
     * Records a timeout.
     *
     * @param responseTimeMillis the response time in milliseconds
     */
    public void recordTimeout(long responseTimeMillis) {
        totalMessagesSent.incrementAndGet();
        timeoutMessages.incrementAndGet();
        totalResponseTimeMillis.addAndGet(responseTimeMillis);
        updateMinMaxResponseTime(responseTimeMillis);
    }

    private void updateMinMaxResponseTime(long responseTimeMillis) {
        minResponseTimeMillis.updateAndGet(current -> Math.min(current, responseTimeMillis));
        maxResponseTimeMillis.updateAndGet(current -> Math.max(current, responseTimeMillis));
    }

    /**
     * Gets the transport identifier.
     *
     * @return the transport identifier
     */
    public String getTransportId() {
        return transportId;
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
     * Gets the total number of messages sent.
     *
     * @return the total messages sent
     */
    public long getTotalMessagesSent() {
        return totalMessagesSent.get();
    }

    /**
     * Gets the number of successful messages.
     *
     * @return the successful messages count
     */
    public long getSuccessfulMessages() {
        return successfulMessages.get();
    }

    /**
     * Gets the number of failed messages.
     *
     * @return the failed messages count
     */
    public long getFailedMessages() {
        return failedMessages.get();
    }

    /**
     * Gets the number of timeout messages.
     *
     * @return the timeout messages count
     */
    public long getTimeoutMessages() {
        return timeoutMessages.get();
    }

    /**
     * Gets the success rate as a percentage.
     *
     * @return the success rate percentage
     */
    public double getSuccessRate() {
        long total = totalMessagesSent.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) successfulMessages.get() / total * 100.0;
    }

    /**
     * Gets the failure rate as a percentage.
     *
     * @return the failure rate percentage
     */
    public double getFailureRate() {
        long total = totalMessagesSent.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) failedMessages.get() / total * 100.0;
    }

    /**
     * Gets the timeout rate as a percentage.
     *
     * @return the timeout rate percentage
     */
    public double getTimeoutRate() {
        long total = totalMessagesSent.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) timeoutMessages.get() / total * 100.0;
    }

    /**
     * Gets the average response time in milliseconds.
     *
     * @return the average response time
     */
    public double getAverageResponseTimeMillis() {
        long total = totalMessagesSent.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) totalResponseTimeMillis.get() / total;
    }

    /**
     * Gets the minimum response time in milliseconds.
     *
     * @return the minimum response time
     */
    public long getMinResponseTimeMillis() {
        long min = minResponseTimeMillis.get();
        return min == Long.MAX_VALUE ? 0 : min;
    }

    /**
     * Gets the maximum response time in milliseconds.
     *
     * @return the maximum response time
     */
    public long getMaxResponseTimeMillis() {
        return maxResponseTimeMillis.get();
    }

    /**
     * Gets the total response time in milliseconds.
     *
     * @return the total response time
     */
    public long getTotalResponseTimeMillis() {
        return totalResponseTimeMillis.get();
    }

    /**
     * Gets the error counts by type.
     *
     * @return the error counts map
     */
    public Map<String, Long> getErrorCounts() {
        return errorCounts;
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
     * Gets the messages per second rate.
     *
     * @return the messages per second
     */
    public double getMessagesPerSecond() {
        long uptimeSeconds = getUptimeMillis() / 1000;
        if (uptimeSeconds == 0) {
            return 0.0;
        }
        return (double) totalMessagesSent.get() / uptimeSeconds;
    }

    @Override
    public String toString() {
        return "TransportStatistics{" +
                "transportId='" + transportId + '\'' +
                ", startTime=" + startTime +
                ", totalMessagesSent=" + totalMessagesSent.get() +
                ", successfulMessages=" + successfulMessages.get() +
                ", failedMessages=" + failedMessages.get() +
                ", timeoutMessages=" + timeoutMessages.get() +
                ", successRate=" + String.format("%.2f%%", getSuccessRate()) +
                ", averageResponseTime=" + String.format("%.2fms", getAverageResponseTimeMillis()) +
                ", messagesPerSecond=" + String.format("%.2f", getMessagesPerSecond()) +
                '}';
    }
}
