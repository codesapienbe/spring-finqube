package com.finqube.iso20022.core.transport;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Statistics for transport operations.
 *
 * <p>This class encapsulates statistics and metrics for transport operations,
 * including message counts, timing information, and error rates.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class TransportStatistics {

    private final String transportId;
    private final Instant startTime;
    private final Map<String, Object> metrics;
    private final Map<String, Object> metadata;

    /**
     * Constructs a new TransportStatistics.
     *
     * @param transportId the transport identifier
     * @param startTime when statistics collection started
     * @param metrics transport metrics
     * @param metadata additional metadata
     */
    public TransportStatistics(String transportId, Instant startTime, Map<String, Object> metrics, Map<String, Object> metadata) {
        this.transportId = Objects.requireNonNull(transportId, "Transport ID cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.metrics = Objects.requireNonNull(metrics, "Metrics cannot be null");
        this.metadata = Objects.requireNonNull(metadata, "Metadata cannot be null");
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
     * Gets transport metrics.
     *
     * @return transport metrics
     */
    public Map<String, Object> getMetrics() {
        return metrics;
    }

    /**
     * Gets additional metadata.
     *
     * @return additional metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Gets the uptime in milliseconds.
     *
     * @return the uptime in milliseconds
     */
    public long getUptimeMillis() {
        return Instant.now().toEpochMilli() - startTime.toEpochMilli();
    }

    @Override
    public String toString() {
        return "TransportStatistics{" +
                "transportId='" + transportId + '\'' +
                ", startTime=" + startTime +
                ", metrics=" + metrics +
                ", metadata=" + metadata +
                '}';
    }
}
