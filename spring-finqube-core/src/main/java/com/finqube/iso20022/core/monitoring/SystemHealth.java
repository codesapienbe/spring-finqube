package com.finqube.iso20022.core.monitoring;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the overall health status of the ISO 20022 system.
 *
 * <p>This class encapsulates the overall health status of the system,
 * including individual component health, system metrics, and operational status.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class SystemHealth {

    private final String systemId;
    private final HealthStatus status;
    private final String message;
    private final Instant checkedAt;
    private final long responseTimeMillis;
    private final List<ComponentHealth> components;
    private final Map<String, Object> metrics;
    private final Map<String, Object> metadata;

    /**
     * Constructs a new SystemHealth.
     *
     * @param systemId the system identifier
     * @param status the overall health status
     * @param message the health check message
     * @param checkedAt when the health check was performed
     * @param responseTimeMillis the response time in milliseconds
     * @param components individual component health checks
     * @param metrics system metrics
     * @param metadata additional metadata
     */
    public SystemHealth(String systemId, HealthStatus status, String message,
                       Instant checkedAt, long responseTimeMillis,
                       List<ComponentHealth> components, Map<String, Object> metrics, Map<String, Object> metadata) {
        this.systemId = Objects.requireNonNull(systemId, "System ID cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.message = message;
        this.checkedAt = Objects.requireNonNull(checkedAt, "Checked timestamp cannot be null");
        this.responseTimeMillis = responseTimeMillis;
        this.components = components;
        this.metrics = metrics;
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
     * Gets individual component health checks.
     *
     * @return the component health checks
     */
    public List<ComponentHealth> getComponents() {
        return components;
    }

    /**
     * Gets system metrics.
     *
     * @return the system metrics
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
     * Checks if the system is healthy.
     *
     * @return true if healthy, false otherwise
     */
    public boolean isHealthy() {
        return status == HealthStatus.HEALTHY;
    }

    /**
     * Checks if the system is degraded.
     *
     * @return true if degraded, false otherwise
     */
    public boolean isDegraded() {
        return status == HealthStatus.DEGRADED;
    }

    /**
     * Checks if the system is unhealthy.
     *
     * @return true if unhealthy, false otherwise
     */
    public boolean isUnhealthy() {
        return status == HealthStatus.UNHEALTHY;
    }

    /**
     * Gets the number of healthy components.
     *
     * @return the number of healthy components
     */
    public long getHealthyComponentCount() {
        return components.stream().filter(ComponentHealth::isHealthy).count();
    }

    /**
     * Gets the number of unhealthy components.
     *
     * @return the number of unhealthy components
     */
    public long getUnhealthyComponentCount() {
        return components.stream().filter(c -> !c.isHealthy()).count();
    }

    /**
     * Gets the overall health percentage.
     *
     * @return the health percentage (0-100)
     */
    public double getHealthPercentage() {
        if (components.isEmpty()) {
            return isHealthy() ? 100.0 : 0.0;
        }
        return (double) getHealthyComponentCount() / components.size() * 100.0;
    }

    @Override
    public String toString() {
        return "SystemHealth{" +
                "systemId='" + systemId + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", checkedAt=" + checkedAt +
                ", responseTimeMillis=" + responseTimeMillis +
                ", healthyComponents=" + getHealthyComponentCount() +
                ", totalComponents=" + components.size() +
                ", healthPercentage=" + String.format("%.1f%%", getHealthPercentage()) +
                '}';
    }

    /**
     * Health status enumeration.
     */
    public enum HealthStatus {
        HEALTHY("Healthy", "System is functioning normally"),
        DEGRADED("Degraded", "System is functioning but with reduced performance"),
        UNHEALTHY("Unhealthy", "System is not functioning properly");

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
