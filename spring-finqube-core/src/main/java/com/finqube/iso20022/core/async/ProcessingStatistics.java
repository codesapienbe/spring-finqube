package com.finqube.iso20022.core.async;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Statistics and metrics for asynchronous processing operations.
 *
 * <p>This class tracks various performance metrics and statistics for async processing,
 * including processing counts, success rates, timing information, and queue statistics.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class ProcessingStatistics {

    private final String processorId;
    private final Instant startTime;
    private final AtomicLong totalMessagesProcessed;
    private final AtomicLong successfulMessages;
    private final AtomicLong failedMessages;
    private final AtomicLong timeoutMessages;
    private final AtomicLong totalProcessingTimeMillis;
    private final AtomicLong minProcessingTimeMillis;
    private final AtomicLong maxProcessingTimeMillis;
    private final AtomicLong currentQueueSize;
    private final Map<String, Long> errorTypeCounts;

    /**
     * Constructs a new ProcessingStatistics.
     *
     * @param processorId the processor identifier
     * @param startTime when statistics collection started
     * @param errorTypeCounts map of error types to counts
     */
    public ProcessingStatistics(String processorId, Instant startTime, Map<String, Long> errorTypeCounts) {
        this.processorId = Objects.requireNonNull(processorId, "Processor ID cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.totalMessagesProcessed = new AtomicLong(0);
        this.successfulMessages = new AtomicLong(0);
        this.failedMessages = new AtomicLong(0);
        this.timeoutMessages = new AtomicLong(0);
        this.totalProcessingTimeMillis = new AtomicLong(0);
        this.minProcessingTimeMillis = new AtomicLong(Long.MAX_VALUE);
        this.maxProcessingTimeMillis = new AtomicLong(0);
        this.currentQueueSize = new AtomicLong(0);
        this.errorTypeCounts = errorTypeCounts;
    }

    /**
     * Records a successful message processing.
     *
     * @param processingTimeMillis the processing time in milliseconds
     */
    public void recordSuccess(long processingTimeMillis) {
        totalMessagesProcessed.incrementAndGet();
        successfulMessages.incrementAndGet();
        totalProcessingTimeMillis.addAndGet(processingTimeMillis);
        updateMinMaxProcessingTime(processingTimeMillis);
    }

    /**
     * Records a failed message processing.
     *
     * @param processingTimeMillis the processing time in milliseconds
     * @param errorType the type of error
     */
    public void recordFailure(long processingTimeMillis, String errorType) {
        totalMessagesProcessed.incrementAndGet();
        failedMessages.incrementAndGet();
        totalProcessingTimeMillis.addAndGet(processingTimeMillis);
        updateMinMaxProcessingTime(processingTimeMillis);

        if (errorType != null) {
            errorTypeCounts.merge(errorType, 1L, Long::sum);
        }
    }

    /**
     * Records a timeout.
     *
     * @param processingTimeMillis the processing time in milliseconds
     */
    public void recordTimeout(long processingTimeMillis) {
        totalMessagesProcessed.incrementAndGet();
        timeoutMessages.incrementAndGet();
        totalProcessingTimeMillis.addAndGet(processingTimeMillis);
        updateMinMaxProcessingTime(processingTimeMillis);
    }

    /**
     * Updates the current queue size.
     *
     * @param queueSize the current queue size
     */
    public void setCurrentQueueSize(long queueSize) {
        this.currentQueueSize.set(queueSize);
    }

    private void updateMinMaxProcessingTime(long processingTimeMillis) {
        minProcessingTimeMillis.updateAndGet(current -> Math.min(current, processingTimeMillis));
        maxProcessingTimeMillis.updateAndGet(current -> Math.max(current, processingTimeMillis));
    }

    /**
     * Gets the processor identifier.
     *
     * @return the processor identifier
     */
    public String getProcessorId() {
        return processorId;
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
     * Gets the total number of messages processed.
     *
     * @return the total messages processed
     */
    public long getTotalMessagesProcessed() {
        return totalMessagesProcessed.get();
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
     * Gets the current queue size.
     *
     * @return the current queue size
     */
    public long getCurrentQueueSize() {
        return currentQueueSize.get();
    }

    /**
     * Gets the success rate as a percentage.
     *
     * @return the success rate percentage
     */
    public double getSuccessRate() {
        long total = totalMessagesProcessed.get();
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
        long total = totalMessagesProcessed.get();
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
        long total = totalMessagesProcessed.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) timeoutMessages.get() / total * 100.0;
    }

    /**
     * Gets the average processing time in milliseconds.
     *
     * @return the average processing time
     */
    public double getAverageProcessingTimeMillis() {
        long total = totalMessagesProcessed.get();
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
     * Gets the error counts by type.
     *
     * @return the error counts map
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
     * Gets the messages processed per second rate.
     *
     * @return the messages per second
     */
    public double getMessagesPerSecond() {
        long uptimeSeconds = getUptimeMillis() / 1000;
        if (uptimeSeconds == 0) {
            return 0.0;
        }
        return (double) totalMessagesProcessed.get() / uptimeSeconds;
    }

    @Override
    public String toString() {
        return "ProcessingStatistics{" +
                "processorId='" + processorId + '\'' +
                ", startTime=" + startTime +
                ", totalMessagesProcessed=" + totalMessagesProcessed.get() +
                ", successfulMessages=" + successfulMessages.get() +
                ", failedMessages=" + failedMessages.get() +
                ", timeoutMessages=" + timeoutMessages.get() +
                ", currentQueueSize=" + currentQueueSize.get() +
                ", successRate=" + String.format("%.2f%%", getSuccessRate()) +
                ", averageProcessingTime=" + String.format("%.2fms", getAverageProcessingTimeMillis()) +
                ", messagesPerSecond=" + String.format("%.2f", getMessagesPerSecond()) +
                '}';
    }
}
