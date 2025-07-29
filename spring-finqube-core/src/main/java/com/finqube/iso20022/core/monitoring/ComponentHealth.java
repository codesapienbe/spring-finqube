package com.finqube.iso20022.core.monitoring;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the health status of an individual component.
 *
 * <p>This class encapsulates the health status of a specific component,
 * including status, response time, and component-specific metrics.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class ComponentHealth {

    private final String componentId;
    private final String componentName;
    private final HealthStatus status;
    private final String message;
    private final Instant checkedAt;
    private final long responseTimeMillis;
    private final Map<String, Object> metrics;
    private final Map<String, Object> metadata;

    /**
     * Constructs a new ComponentHealth.
     *
     * @param componentId the component identifier
     * @param componentName the component name
     * @param status the health status
     * @param message the health check message
     * @param checkedAt when the health check was performed
     * @param responseTimeMillis the response time in milliseconds
     * @param metrics component-specific metrics
     * @param metadata additional metadata
     */
    public ComponentHealth(String componentId, String componentName, HealthStatus status, String message,
                          Instant checkedAt, long responseTimeMillis, Map<String, Object> metrics, Map<String, Object> metadata) {
        this.componentId = Objects.requireNonNull(componentId, "Component ID cannot be null");
        this.componentName = Objects.requireNonNull(componentName, "Component name cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.message = message;
        this.checkedAt = Objects.requireNonNull(checkedAt, "Checked timestamp cannot be null");
        this.responseTimeMillis = responseTimeMillis;
        this.metrics = metrics;
        this.metadata = metadata;
    }

    /**
     * Gets the component identifier.
     *
     * @return the component identifier
     */
    public String getComponentId() {
        return componentId;
    }

    /**
     * Gets the component name.
     *
     * @return the component name
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * Gets the health status.
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
     * Gets component-specific metrics.
     *
     * @return the component metrics
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
     * Checks if the component is healthy.
     *
     * @return true if healthy, false otherwise
     */
    public boolean isHealthy() {
        return status == HealthStatus.HEALTHY;
    }

    /**
     * Checks if the component is degraded.
     *
     * @return true if degraded, false otherwise
     */
    public boolean isDegraded() {
        return status == HealthStatus.DEGRADED;
    }

    /**
     * Checks if the component is unhealthy.
     *
     * @return true if unhealthy, false otherwise
     */
    public boolean isUnhealthy() {
        return status == HealthStatus.UNHEALTHY;
    }

    @Override
    public String toString() {
        return "ComponentHealth{" +
                "componentId='" + componentId + '\'' +
                ", componentName='" + componentName + '\'' +
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
        ComponentHealth that = (ComponentHealth) o;
        return Objects.equals(componentId, that.componentId) &&
                Objects.equals(componentName, that.componentName) &&
                status == that.status &&
                Objects.equals(checkedAt, that.checkedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId, componentName, status, checkedAt);
    }

    /**
     * Health status enumeration.
     */
    public enum HealthStatus {
        HEALTHY("Healthy", "Component is functioning normally"),
        DEGRADED("Degraded", "Component is functioning but with reduced performance"),
        UNHEALTHY("Unhealthy", "Component is not functioning properly");

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
