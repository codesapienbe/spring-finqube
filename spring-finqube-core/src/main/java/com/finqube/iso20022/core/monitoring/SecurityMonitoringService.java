package com.finqube.iso20022.core.monitoring;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service for secure monitoring and alerting of security events.
 *
 * <p>This service provides comprehensive security monitoring capabilities including
 * real-time threat detection, alert generation, and security event correlation.</p>
 *
 * <p>The service supports:</p>
 * <ul>
 *   <li>Real-time security event monitoring</li>
 *   <li>Automated threat detection and alerting</li>
 *   <li>Security event correlation and analysis</li>
 *   <li>Alert management and acknowledgment</li>
 *   <li>Integration with external security systems</li>
 *   <li>Compliance reporting and audit trails</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface SecurityMonitoringService {

    /**
     * Records a security event for monitoring.
     *
     * @param eventType the type of security event
     * @param source the source of the event
     * @param message the event message
     * @param severity the event severity
     * @param context additional context information
     * @param correlationId the correlation identifier
     */
    void recordSecurityEvent(String eventType, String source, String message,
                           SecurityAlert.SecurityAlertSeverity severity,
                           Map<String, Object> context, String correlationId);

    /**
     * Records a security event asynchronously.
     *
     * @param eventType the type of security event
     * @param source the source of the event
     * @param message the event message
     * @param severity the event severity
     * @param context additional context information
     * @param correlationId the correlation identifier
     * @return a CompletableFuture that completes when the event is recorded
     */
    CompletableFuture<Void> recordSecurityEventAsync(String eventType, String source, String message,
                                                    SecurityAlert.SecurityAlertSeverity severity,
                                                    Map<String, Object> context, String correlationId);

    /**
     * Creates a security alert.
     *
     * @param alertType the type of security alert
     * @param source the source of the alert
     * @param message the alert message
     * @param severity the alert severity
     * @param context additional context information
     * @param correlationId the correlation identifier
     * @return the created security alert
     */
    SecurityAlert createAlert(String alertType, String source, String message,
                            SecurityAlert.SecurityAlertSeverity severity,
                            Map<String, Object> context, String correlationId);

    /**
     * Creates a security alert asynchronously.
     *
     * @param alertType the type of security alert
     * @param source the source of the alert
     * @param message the alert message
     * @param severity the alert severity
     * @param context additional context information
     * @param correlationId the correlation identifier
     * @return a CompletableFuture that completes with the created security alert
     */
    CompletableFuture<SecurityAlert> createAlertAsync(String alertType, String source, String message,
                                                     SecurityAlert.SecurityAlertSeverity severity,
                                                     Map<String, Object> context, String correlationId);

    /**
     * Acknowledges a security alert.
     *
     * @param alertId the alert identifier
     * @param acknowledgedBy who is acknowledging the alert
     * @return the updated security alert
     */
    SecurityAlert acknowledgeAlert(String alertId, String acknowledgedBy);

    /**
     * Acknowledges a security alert asynchronously.
     *
     * @param alertId the alert identifier
     * @param acknowledgedBy who is acknowledging the alert
     * @return a CompletableFuture that completes with the updated security alert
     */
    CompletableFuture<SecurityAlert> acknowledgeAlertAsync(String alertId, String acknowledgedBy);

    /**
     * Gets all active security alerts.
     *
     * @return list of active security alerts
     */
    List<SecurityAlert> getActiveAlerts();

    /**
     * Gets all active security alerts asynchronously.
     *
     * @return a CompletableFuture that completes with the list of active security alerts
     */
    CompletableFuture<List<SecurityAlert>> getActiveAlertsAsync();

    /**
     * Gets security alerts by severity.
     *
     * @param severity the severity level to filter by
     * @return list of security alerts with the specified severity
     */
    List<SecurityAlert> getAlertsBySeverity(SecurityAlert.SecurityAlertSeverity severity);

    /**
     * Gets security alerts by severity asynchronously.
     *
     * @param severity the severity level to filter by
     * @return a CompletableFuture that completes with the list of security alerts
     */
    CompletableFuture<List<SecurityAlert>> getAlertsBySeverityAsync(SecurityAlert.SecurityAlertSeverity severity);

    /**
     * Gets security alerts by type.
     *
     * @param alertType the alert type to filter by
     * @return list of security alerts with the specified type
     */
    List<SecurityAlert> getAlertsByType(String alertType);

    /**
     * Gets security alerts by type asynchronously.
     *
     * @param alertType the alert type to filter by
     * @return a CompletableFuture that completes with the list of security alerts
     */
    CompletableFuture<List<SecurityAlert>> getAlertsByTypeAsync(String alertType);

    /**
     * Gets security alerts within a time range.
     *
     * @param startTime the start time
     * @param endTime the end time
     * @return list of security alerts within the time range
     */
    List<SecurityAlert> getAlertsInTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Gets security alerts within a time range asynchronously.
     *
     * @param startTime the start time
     * @param endTime the end time
     * @return a CompletableFuture that completes with the list of security alerts
     */
    CompletableFuture<List<SecurityAlert>> getAlertsInTimeRangeAsync(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Gets a specific security alert by ID.
     *
     * @param alertId the alert identifier
     * @return the security alert, or null if not found
     */
    SecurityAlert getAlert(String alertId);

    /**
     * Gets a specific security alert by ID asynchronously.
     *
     * @param alertId the alert identifier
     * @return a CompletableFuture that completes with the security alert
     */
    CompletableFuture<SecurityAlert> getAlertAsync(String alertId);

    /**
     * Gets security monitoring statistics.
     *
     * @return the security monitoring statistics
     */
    SecurityMonitoringStatistics getStatistics();

    /**
     * Gets security monitoring statistics asynchronously.
     *
     * @return a CompletableFuture that completes with the security monitoring statistics
     */
    CompletableFuture<SecurityMonitoringStatistics> getStatisticsAsync();

    /**
     * Performs a security health check.
     *
     * @return the security health check result
     */
    SecurityHealthCheck healthCheck();

    /**
     * Gets the security monitoring service identifier.
     *
     * @return the service identifier
     */
    String getServiceId();

    /**
     * Gets the security monitoring service display name.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Gets the security monitoring service version.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Checks if the security monitoring service is available.
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();
}
