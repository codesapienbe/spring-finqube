package com.finqube.iso20022.core.monitoring;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Result of a monitoring manager health check.
 *
 * <p>This class encapsulates the result of performing a health check on a monitoring manager,
 * including overall status, component-level health, and timing information.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class MonitoringHealthCheck {

    private final String monitoringManagerId;
    private final HealthStatus status;
    private final String message;
    private final Instant checkedAt;
    private final long responseTimeMillis;
    private final Map<String, Object> metrics;
    private final Map<String, Object> metadata;

    /**
     * Constructs a new MonitoringHealthCheck.
     *
     * @param monitoringManagerId the monitoring manager identifier
     * @param status the overall health status
     * @param message the health check message
     * @param checkedAt when the health check was performed
     * @param responseTimeMillis the response time in milliseconds
     * @param metrics monitoring metrics
     * @param metadata additional metadata
     */
    public MonitoringHealthCheck(String monitoringManagerId, HealthStatus status, String message,
                               Instant checkedAt, long responseTimeMillis, Map<String, Object> metrics, Map<String, Object> metadata) {
        this.monitoringManagerId = Objects.requireNonNull(monitoringManagerId, "Monitoring manager ID cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.message = message;
        this.checkedAt = Objects.requireNonNull(checkedAt, "Checked timestamp cannot be null");
        this.responseTimeMillis = responseTimeMillis;
        this.metrics = metrics;
        this.metadata = metadata;
    }

    /**
     * Gets the monitoring manager identifier.
     *
     * @return the monitoring manager identifier
     */
    public String getMonitoringManagerId() {
        return monitoringManagerId;
    }

    /**
     * Gets the overall health status.
     *
     * @return the health status
     */
    public HealthStatus getStatus() {
        return status;
    }

    /**
     * Gets the health check message.
     *
     * @return the health check message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets when the health check was performed.
     *
     * @return the health check timestamp
     */
    public Instant getCheckedAt() {
        return checkedAt;
    }

    /**
     * Gets the response time in milliseconds.
     *
     * @return the response time in milliseconds
     */
    public long getResponseTimeMillis() {
        return responseTimeMillis;
    }

    /**
     * Gets monitoring metrics.
     *
     * @return the monitoring metrics
     */
    public Map<String, Object> getMetrics() {
        return metrics;
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
     * Checks if the monitoring manager is healthy.
     *
     * @return true if healthy, false otherwise
     */
    public boolean isHealthy() {
        return status == HealthStatus.HEALTHY;
    }

    /**
     * Checks if the monitoring manager is degraded.
     *
     * @return true if degraded, false otherwise
     */
    public boolean isDegraded() {
        return status == HealthStatus.DEGRADED;
    }

    /**
     * Checks if the monitoring manager is unhealthy.
     *
     * @return true if unhealthy, false otherwise
     */
    public boolean isUnhealthy() {
        return status == HealthStatus.UNHEALTHY;
    }

    @Override
    public String toString() {
        return "MonitoringHealthCheck{" +
                "monitoringManagerId='" + monitoringManagerId + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", checkedAt=" + checkedAt +
                ", responseTimeMillis=" + responseTimeMillis +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitoringHealthCheck that = (MonitoringHealthCheck) o;
        return responseTimeMillis == that.responseTimeMillis &&
                Objects.equals(monitoringManagerId, that.monitoringManagerId) &&
                status == that.status &&
                Objects.equals(message, that.message) &&
                Objects.equals(checkedAt, that.checkedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monitoringManagerId, status, message, checkedAt, responseTimeMillis);
    }

    /**
     * Health status enumeration.
     */
    public enum HealthStatus {
        HEALTHY("Healthy", "Monitoring manager is functioning normally"),
        DEGRADED("Degraded", "Monitoring manager is functioning but with reduced performance"),
        UNHEALTHY("Unhealthy", "Monitoring manager is not functioning properly");

        private final String displayName;
        private final String description;

        HealthStatus(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}