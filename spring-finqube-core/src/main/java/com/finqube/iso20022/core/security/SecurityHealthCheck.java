package com.finqube.iso20022.core.security;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Result of a security manager health check.
 *
 * <p>This class encapsulates the result of performing a health check on a security manager,
 * including overall status, individual component checks, and timing information.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class SecurityHealthCheck {

    private final String securityManagerId;
    private final HealthStatus status;
    private final String message;
    private final Instant checkedAt;
    private final long responseTimeMillis;
    private final Map<String, ComponentHealth> components;

    /**
     * Constructs a new SecurityHealthCheck.
     *
     * @param securityManagerId the security manager identifier
     * @param status the overall health status
     * @param message the health check message
     * @param checkedAt when the health check was performed
     * @param responseTimeMillis the response time in milliseconds
     * @param components individual component health checks
     */
    public SecurityHealthCheck(String securityManagerId, HealthStatus status, String message,
                             Instant checkedAt, long responseTimeMillis, Map<String, ComponentHealth> components) {
        this.securityManagerId = Objects.requireNonNull(securityManagerId, "Security manager ID cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.message = message;
        this.checkedAt = Objects.requireNonNull(checkedAt, "Checked timestamp cannot be null");
        this.responseTimeMillis = responseTimeMillis;
        this.components = components;
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
    public Map<String, ComponentHealth> getComponents() {
        return components;
    }

    /**
     * Checks if the security manager is healthy.
     *
     * @return true if healthy, false otherwise
     */
    public boolean isHealthy() {
        return status == HealthStatus.HEALTHY;
    }

    /**
     * Checks if the security manager is degraded.
     *
     * @return true if degraded, false otherwise
     */
    public boolean isDegraded() {
        return status == HealthStatus.DEGRADED;
    }

    /**
     * Checks if the security manager is unhealthy.
     *
     * @return true if unhealthy, false otherwise
     */
    public boolean isUnhealthy() {
        return status == HealthStatus.UNHEALTHY;
    }

    @Override
    public String toString() {
        return "SecurityHealthCheck{" +
                "securityManagerId='" + securityManagerId + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", checkedAt=" + checkedAt +
                ", responseTimeMillis=" + responseTimeMillis +
                ", components=" + components +
                '}';
    }

    /**
     * Health status enumeration.
     */
    public enum HealthStatus {
        HEALTHY("Healthy", "Security manager is functioning normally"),
        DEGRADED("Degraded", "Security manager is functioning but with reduced performance"),
        UNHEALTHY("Unhealthy", "Security manager is not functioning properly");

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

    /**
     * Individual component health check result.
     */
    public static class ComponentHealth {
        private final String componentName;
        private final HealthStatus status;
        private final String message;
        private final long responseTimeMillis;

        public ComponentHealth(String componentName, HealthStatus status, String message, long responseTimeMillis) {
            this.componentName = Objects.requireNonNull(componentName, "Component name cannot be null");
            this.status = Objects.requireNonNull(status, "Status cannot be null");
            this.message = message;
            this.responseTimeMillis = responseTimeMillis;
        }

        public String getComponentName() {
            return componentName;
        }

        public HealthStatus getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public long getResponseTimeMillis() {
            return responseTimeMillis;
        }

        public boolean isHealthy() {
            return status == HealthStatus.HEALTHY;
        }

        @Override
        public String toString() {
            return "ComponentHealth{" +
                    "componentName='" + componentName + '\'' +
                    ", status=" + status +
                    ", message='" + message + '\'' +
                    ", responseTimeMillis=" + responseTimeMillis +
                    '}';
        }
    }
}
