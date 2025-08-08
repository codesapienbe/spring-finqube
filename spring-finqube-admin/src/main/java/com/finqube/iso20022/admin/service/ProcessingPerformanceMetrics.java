package com.finqube.iso20022.admin.service;

import java.time.LocalDateTime;

/**
 * Data class representing processing performance metrics for financial messages.
 *
 * <p>This class encapsulates key performance indicators used for monitoring
 * and alerting in the admin dashboard.</p>
 *
 * @author Spring Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class ProcessingPerformanceMetrics {

    private final double averageProcessingTimeMs;
    private final double messagesPerSecond;
    private final double errorRate;
    private final int activeConnections;
    private final LocalDateTime lastUpdate;

    /**
     * Constructs a new ProcessingPerformanceMetrics instance.
     *
     * @param averageProcessingTimeMs average processing time in milliseconds
     * @param messagesPerSecond messages processed per second
     * @param errorRate error rate as a percentage
     * @param activeConnections number of active connections
     * @param lastUpdate timestamp of last update
     */
    public ProcessingPerformanceMetrics(double averageProcessingTimeMs,
                                      double messagesPerSecond,
                                      double errorRate,
                                      int activeConnections,
                                      LocalDateTime lastUpdate) {
        this.averageProcessingTimeMs = averageProcessingTimeMs;
        this.messagesPerSecond = messagesPerSecond;
        this.errorRate = errorRate;
        this.activeConnections = activeConnections;
        this.lastUpdate = lastUpdate;
    }

    /**
     * Gets the average processing time in milliseconds.
     *
     * @return average processing time
     */
    public double getAverageProcessingTimeMs() {
        return averageProcessingTimeMs;
    }

    /**
     * Gets the messages processed per second.
     *
     * @return messages per second
     */
    public double getMessagesPerSecond() {
        return messagesPerSecond;
    }

    /**
     * Gets the error rate as a percentage.
     *
     * @return error rate
     */
    public double getErrorRate() {
        return errorRate;
    }

    /**
     * Gets the number of active connections.
     *
     * @return active connections count
     */
    public int getActiveConnections() {
        return activeConnections;
    }

    /**
     * Gets the timestamp of the last update.
     *
     * @return last update timestamp
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public String toString() {
        return "ProcessingPerformanceMetrics{" +
                "averageProcessingTimeMs=" + averageProcessingTimeMs +
                ", messagesPerSecond=" + messagesPerSecond +
                ", errorRate=" + errorRate +
                ", activeConnections=" + activeConnections +
                ", lastUpdate=" + lastUpdate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessingPerformanceMetrics that = (ProcessingPerformanceMetrics) o;

        if (Double.compare(that.averageProcessingTimeMs, averageProcessingTimeMs) != 0) return false;
        if (Double.compare(that.messagesPerSecond, messagesPerSecond) != 0) return false;
        if (Double.compare(that.errorRate, errorRate) != 0) return false;
        if (activeConnections != that.activeConnections) return false;
        return lastUpdate != null ? lastUpdate.equals(that.lastUpdate) : that.lastUpdate == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(averageProcessingTimeMs);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(messagesPerSecond);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(errorRate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + activeConnections;
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        return result;
    }
}
