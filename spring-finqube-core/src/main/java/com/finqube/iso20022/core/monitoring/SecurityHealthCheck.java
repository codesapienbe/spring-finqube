package com.finqube.iso20022.core.monitoring;

import java.time.LocalDateTime;

/**
 * Health check result for security monitoring service.
 *
 * <p>This class provides comprehensive health information about the security monitoring
 * service including operational status, performance metrics, and security posture.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class SecurityHealthCheck {

    private final String serviceId;
    private final LocalDateTime timestamp;
    private final boolean healthy;
    private final String status;
    private final String message;
    private final long activeAlerts;
    private final long criticalAlerts;
    private final double responseTime;
    private final boolean monitoringActive;
    private final boolean alertingEnabled;
    private final String securityLevel;

    /**
     * Constructs a new SecurityHealthCheck with the specified parameters.
     *
     * @param serviceId the service identifier
     * @param timestamp the timestamp
     * @param healthy whether the service is healthy
     * @param status the health status
     * @param message the health message
     * @param activeAlerts the number of active alerts
     * @param criticalAlerts the number of critical alerts
     * @param responseTime the response time in milliseconds
     * @param monitoringActive whether monitoring is active
     * @param alertingEnabled whether alerting is enabled
     * @param securityLevel the current security level
     */
    public SecurityHealthCheck(String serviceId, LocalDateTime timestamp, boolean healthy,
                             String status, String message, long activeAlerts, long criticalAlerts,
                             double responseTime, boolean monitoringActive, boolean alertingEnabled,
                             String securityLevel) {
        this.serviceId = serviceId;
        this.timestamp = timestamp;
        this.healthy = healthy;
        this.status = status;
        this.message = message;
        this.activeAlerts = activeAlerts;
        this.criticalAlerts = criticalAlerts;
        this.responseTime = responseTime;
        this.monitoringActive = monitoringActive;
        this.alertingEnabled = alertingEnabled;
        this.securityLevel = securityLevel;
    }

    /**
     * Gets the service identifier.
     *
     * @return the service identifier
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Checks if the service is healthy.
     *
     * @return true if healthy, false otherwise
     */
    public boolean isHealthy() {
        return healthy;
    }

    /**
     * Gets the health status.
     *
     * @return the health status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the health message.
     *
     * @return the health message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the number of active alerts.
     *
     * @return the active alerts
     */
    public long getActiveAlerts() {
        return activeAlerts;
    }

    /**
     * Gets the number of critical alerts.
     *
     * @return the critical alerts
     */
    public long getCriticalAlerts() {
        return criticalAlerts;
    }

    /**
     * Gets the response time in milliseconds.
     *
     * @return the response time
     */
    public double getResponseTime() {
        return responseTime;
    }

    /**
     * Checks if monitoring is active.
     *
     * @return true if monitoring is active, false otherwise
     */
    public boolean isMonitoringActive() {
        return monitoringActive;
    }

    /**
     * Checks if alerting is enabled.
     *
     * @return true if alerting is enabled, false otherwise
     */
    public boolean isAlertingEnabled() {
        return alertingEnabled;
    }

    /**
     * Gets the current security level.
     *
     * @return the security level
     */
    public String getSecurityLevel() {
        return securityLevel;
    }

    /**
     * Checks if there are any critical alerts requiring immediate attention.
     *
     * @return true if critical alerts exist, false otherwise
     */
    public boolean hasCriticalAlerts() {
        return criticalAlerts > 0;
    }

    /**
     * Gets the overall security posture.
     *
     * @return the security posture description
     */
    public String getSecurityPosture() {
        if (criticalAlerts > 0) {
            return "CRITICAL - Immediate attention required";
        } else if (activeAlerts > 10) {
            return "WARNING - High number of active alerts";
        } else if (activeAlerts > 0) {
            return "ATTENTION - Some alerts require review";
        } else {
            return "SECURE - No active alerts";
        }
    }

    @Override
    public String toString() {
        return String.format("SecurityHealthCheck{serviceId='%s', healthy=%s, status='%s', " +
                "activeAlerts=%d, criticalAlerts=%d, securityPosture='%s'}",
                serviceId, healthy, status, activeAlerts, criticalAlerts, getSecurityPosture());
    }
}
