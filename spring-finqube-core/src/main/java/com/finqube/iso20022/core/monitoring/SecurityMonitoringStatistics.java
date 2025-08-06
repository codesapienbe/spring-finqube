package com.finqube.iso20022.core.monitoring;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Statistics for security monitoring operations.
 *
 * <p>This class provides comprehensive statistics about security monitoring activities
 * including event counts, alert statistics, and performance metrics.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class SecurityMonitoringStatistics {

    private final String serviceId;
    private final LocalDateTime timestamp;
    private final long totalSecurityEvents;
    private final long totalAlerts;
    private final long activeAlerts;
    private final long acknowledgedAlerts;
    private final long criticalAlerts;
    private final long highAlerts;
    private final long mediumAlerts;
    private final long lowAlerts;
    private final double averageResponseTime;
    private final Map<String, Long> eventsByType;
    private final Map<String, Long> alertsByType;
    private final Map<String, Long> alertsBySeverity;

    /**
     * Constructs a new SecurityMonitoringStatistics with the specified parameters.
     *
     * @param serviceId the service identifier
     * @param timestamp the timestamp
     * @param totalSecurityEvents the total number of security events
     * @param totalAlerts the total number of alerts
     * @param activeAlerts the number of active alerts
     * @param acknowledgedAlerts the number of acknowledged alerts
     * @param criticalAlerts the number of critical alerts
     * @param highAlerts the number of high severity alerts
     * @param mediumAlerts the number of medium severity alerts
     * @param lowAlerts the number of low severity alerts
     * @param averageResponseTime the average response time in milliseconds
     * @param eventsByType the events grouped by type
     * @param alertsByType the alerts grouped by type
     * @param alertsBySeverity the alerts grouped by severity
     */
    public SecurityMonitoringStatistics(String serviceId, LocalDateTime timestamp,
                                      long totalSecurityEvents, long totalAlerts, long activeAlerts,
                                      long acknowledgedAlerts, long criticalAlerts, long highAlerts,
                                      long mediumAlerts, long lowAlerts, double averageResponseTime,
                                      Map<String, Long> eventsByType, Map<String, Long> alertsByType,
                                      Map<String, Long> alertsBySeverity) {
        this.serviceId = serviceId;
        this.timestamp = timestamp;
        this.totalSecurityEvents = totalSecurityEvents;
        this.totalAlerts = totalAlerts;
        this.activeAlerts = activeAlerts;
        this.acknowledgedAlerts = acknowledgedAlerts;
        this.criticalAlerts = criticalAlerts;
        this.highAlerts = highAlerts;
        this.mediumAlerts = mediumAlerts;
        this.lowAlerts = lowAlerts;
        this.averageResponseTime = averageResponseTime;
        this.eventsByType = eventsByType != null ? Map.copyOf(eventsByType) : Map.of();
        this.alertsByType = alertsByType != null ? Map.copyOf(alertsByType) : Map.of();
        this.alertsBySeverity = alertsBySeverity != null ? Map.copyOf(alertsBySeverity) : Map.of();
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
     * Gets the total number of security events.
     *
     * @return the total security events
     */
    public long getTotalSecurityEvents() {
        return totalSecurityEvents;
    }

    /**
     * Gets the total number of alerts.
     *
     * @return the total alerts
     */
    public long getTotalAlerts() {
        return totalAlerts;
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
     * Gets the number of acknowledged alerts.
     *
     * @return the acknowledged alerts
     */
    public long getAcknowledgedAlerts() {
        return acknowledgedAlerts;
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
     * Gets the number of high severity alerts.
     *
     * @return the high alerts
     */
    public long getHighAlerts() {
        return highAlerts;
    }

    /**
     * Gets the number of medium severity alerts.
     *
     * @return the medium alerts
     */
    public long getMediumAlerts() {
        return mediumAlerts;
    }

    /**
     * Gets the number of low severity alerts.
     *
     * @return the low alerts
     */
    public long getLowAlerts() {
        return lowAlerts;
    }

    /**
     * Gets the average response time in milliseconds.
     *
     * @return the average response time
     */
    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    /**
     * Gets the events grouped by type.
     *
     * @return the events by type
     */
    public Map<String, Long> getEventsByType() {
        return eventsByType;
    }

    /**
     * Gets the alerts grouped by type.
     *
     * @return the alerts by type
     */
    public Map<String, Long> getAlertsByType() {
        return alertsByType;
    }

    /**
     * Gets the alerts grouped by severity.
     *
     * @return the alerts by severity
     */
    public Map<String, Long> getAlertsBySeverity() {
        return alertsBySeverity;
    }

    /**
     * Gets the acknowledgment rate as a percentage.
     *
     * @return the acknowledgment rate percentage
     */
    public double getAcknowledgmentRate() {
        if (totalAlerts == 0) {
            return 0.0;
        }
        return (double) acknowledgedAlerts / totalAlerts * 100.0;
    }

    /**
     * Gets the critical alert rate as a percentage.
     *
     * @return the critical alert rate percentage
     */
    public double getCriticalAlertRate() {
        if (totalAlerts == 0) {
            return 0.0;
        }
        return (double) criticalAlerts / totalAlerts * 100.0;
    }

    @Override
    public String toString() {
        return String.format("SecurityMonitoringStatistics{serviceId='%s', timestamp=%s, " +
                "totalEvents=%d, totalAlerts=%d, activeAlerts=%d, criticalAlerts=%d, " +
                "acknowledgmentRate=%.1f%%, avgResponseTime=%.2fms}",
                serviceId, timestamp, totalSecurityEvents, totalAlerts, activeAlerts,
                criticalAlerts, getAcknowledgmentRate(), averageResponseTime);
    }
}
