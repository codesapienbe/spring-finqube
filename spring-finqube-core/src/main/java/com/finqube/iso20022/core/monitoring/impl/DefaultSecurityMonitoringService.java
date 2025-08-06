package com.finqube.iso20022.core.monitoring.impl;

import com.finqube.iso20022.core.monitoring.*;
import com.finqube.iso20022.core.security.audit.AuditLogger;
import com.finqube.iso20022.core.security.audit.AuditLogLevel;
import com.finqube.iso20022.core.security.audit.RiskLevel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Default implementation of SecurityMonitoringService with comprehensive security monitoring.
 *
 * <p>This implementation provides real-time security event monitoring, automated threat detection,
 * alert generation, and security event correlation with comprehensive audit logging.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Component
public class DefaultSecurityMonitoringService implements SecurityMonitoringService {

    private static final Logger log = LoggerFactory.getLogger(DefaultSecurityMonitoringService.class);

    private final String serviceId;
    private final String displayName;
    private final String version;
    private final AuditLogger auditLogger;

    private final Map<String, SecurityAlert> activeAlerts = new ConcurrentHashMap<>();
    private final Map<String, SecurityAlert> allAlerts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> eventsByType = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> alertsByType = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> alertsBySeverity = new ConcurrentHashMap<>();

    private final AtomicLong totalSecurityEvents = new AtomicLong(0);
    private final AtomicLong totalAlerts = new AtomicLong(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final AtomicLong totalOperations = new AtomicLong(0);

    private volatile boolean available = true;
    private volatile boolean monitoringActive = true;
    private volatile boolean alertingEnabled = true;

    @Autowired
    public DefaultSecurityMonitoringService(AuditLogger auditLogger) {
        this.serviceId = "default-security-monitoring-" + UUID.randomUUID().toString().substring(0, 8);
        this.displayName = "Default Security Monitoring Service";
        this.version = "1.0.0";
        this.auditLogger = auditLogger;

        log.info("Initialized DefaultSecurityMonitoringService with ID: {}", serviceId);
    }

    @Override
    public void recordSecurityEvent(String eventType, String source, String message,
                                  SecurityAlert.SecurityAlertSeverity severity,
                                  Map<String, Object> context, String correlationId) {
        long startTime = System.currentTimeMillis();

        try {
            if (eventType == null || eventType.trim().isEmpty()) {
                throw new IllegalArgumentException("Event type cannot be null or empty");
            }

            log.debug("Recording security event: {} from {} - {}", eventType, source, message);

            // Update statistics
            totalSecurityEvents.incrementAndGet();
            eventsByType.computeIfAbsent(eventType, k -> new AtomicLong(0)).incrementAndGet();

            // Check if this event should trigger an alert
            if (shouldTriggerAlert(eventType, severity, context)) {
                createAlert(eventType, source, message, severity, context, correlationId);
            }

            // Log audit event
            auditLogger.logSecurityEvent(source, "SUCCESS", "SECURITY_EVENT",
                    "Security event recorded: " + eventType + " - " + message,
                    AuditLogLevel.SECURITY, mapSeverityToRiskLevel(severity));

            long responseTime = System.currentTimeMillis() - startTime;
            totalResponseTime.addAndGet(responseTime);
            totalOperations.incrementAndGet();

            log.debug("Security event recorded successfully: {} in {}ms", eventType, responseTime);

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            totalResponseTime.addAndGet(responseTime);
            totalOperations.incrementAndGet();

            // Log audit event
            auditLogger.logSecurityEvent(source, "FAILED", "SECURITY_EVENT",
                    "Security event recording failed: " + e.getMessage(),
                    AuditLogLevel.ERROR, RiskLevel.HIGH);

            log.error("Failed to record security event: {}", eventType, e);
        }
    }

    @Override
    public CompletableFuture<Void> recordSecurityEventAsync(String eventType, String source, String message,
                                                          SecurityAlert.SecurityAlertSeverity severity,
                                                          Map<String, Object> context, String correlationId) {
        return CompletableFuture.runAsync(() ->
            recordSecurityEvent(eventType, source, message, severity, context, correlationId));
    }

    @Override
    public SecurityAlert createAlert(String alertType, String source, String message,
                                   SecurityAlert.SecurityAlertSeverity severity,
                                   Map<String, Object> context, String correlationId) {
        long startTime = System.currentTimeMillis();

        try {
            if (alertType == null || alertType.trim().isEmpty()) {
                throw new IllegalArgumentException("Alert type cannot be null or empty");
            }

            log.info("Creating security alert: {} from {} - {}", alertType, source, message);

            // Generate alert ID
            String alertId = "alert-" + UUID.randomUUID().toString().substring(0, 8);

            // Create the alert
            SecurityAlert alert = new SecurityAlert(alertId, alertType, severity, source, message,
                    LocalDateTime.now(), context, correlationId);

            // Store the alert
            activeAlerts.put(alertId, alert);
            allAlerts.put(alertId, alert);

            // Update statistics
            totalAlerts.incrementAndGet();
            alertsByType.computeIfAbsent(alertType, k -> new AtomicLong(0)).incrementAndGet();
            alertsBySeverity.computeIfAbsent(severity.name(), k -> new AtomicLong(0)).incrementAndGet();

            // Log audit event
            auditLogger.logSecurityEvent(source, "SUCCESS", "SECURITY_ALERT",
                    "Security alert created: " + alertId + " - " + message,
                    AuditLogLevel.SECURITY, mapSeverityToRiskLevel(severity));

            long responseTime = System.currentTimeMillis() - startTime;
            totalResponseTime.addAndGet(responseTime);
            totalOperations.incrementAndGet();

            log.info("Security alert created successfully: {} in {}ms", alertId, responseTime);
            return alert;

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            totalResponseTime.addAndGet(responseTime);
            totalOperations.incrementAndGet();

            // Log audit event
            auditLogger.logSecurityEvent(source, "FAILED", "SECURITY_ALERT",
                    "Security alert creation failed: " + e.getMessage(),
                    AuditLogLevel.ERROR, RiskLevel.HIGH);

            log.error("Failed to create security alert: {}", alertType, e);
            throw new RuntimeException("Failed to create security alert", e);
        }
    }

    @Override
    public CompletableFuture<SecurityAlert> createAlertAsync(String alertType, String source, String message,
                                                           SecurityAlert.SecurityAlertSeverity severity,
                                                           Map<String, Object> context, String correlationId) {
        return CompletableFuture.supplyAsync(() ->
            createAlert(alertType, source, message, severity, context, correlationId));
    }

    @Override
    public SecurityAlert acknowledgeAlert(String alertId, String acknowledgedBy) {
        long startTime = System.currentTimeMillis();

        try {
            if (alertId == null || alertId.trim().isEmpty()) {
                throw new IllegalArgumentException("Alert ID cannot be null or empty");
            }

            log.info("Acknowledging security alert: {} by {}", alertId, acknowledgedBy);

            SecurityAlert alert = activeAlerts.get(alertId);
            if (alert == null) {
                throw new IllegalArgumentException("Alert not found: " + alertId);
            }

            // Create acknowledged alert
            SecurityAlert acknowledgedAlert = new SecurityAlert(
                    alert.getAlertId(), alert.getAlertType(), alert.getSeverity(),
                    alert.getSource(), alert.getMessage(), alert.getTimestamp(),
                    alert.getContext(), alert.getCorrelationId(),
                    true, acknowledgedBy, LocalDateTime.now()
            );

            // Update storage
            activeAlerts.remove(alertId);
            allAlerts.put(alertId, acknowledgedAlert);

            // Log audit event
            auditLogger.logSecurityEvent(acknowledgedBy, "SUCCESS", "SECURITY_ALERT_ACK",
                    "Security alert acknowledged: " + alertId,
                    AuditLogLevel.SECURITY, RiskLevel.LOW);

            long responseTime = System.currentTimeMillis() - startTime;
            totalResponseTime.addAndGet(responseTime);
            totalOperations.incrementAndGet();

            log.info("Security alert acknowledged successfully: {} in {}ms", alertId, responseTime);
            return acknowledgedAlert;

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            totalResponseTime.addAndGet(responseTime);
            totalOperations.incrementAndGet();

            // Log audit event
            auditLogger.logSecurityEvent(acknowledgedBy, "FAILED", "SECURITY_ALERT_ACK",
                    "Security alert acknowledgment failed: " + e.getMessage(),
                    AuditLogLevel.ERROR, RiskLevel.HIGH);

            log.error("Failed to acknowledge security alert: {}", alertId, e);
            throw new RuntimeException("Failed to acknowledge security alert", e);
        }
    }

    @Override
    public CompletableFuture<SecurityAlert> acknowledgeAlertAsync(String alertId, String acknowledgedBy) {
        return CompletableFuture.supplyAsync(() -> acknowledgeAlert(alertId, acknowledgedBy));
    }

    @Override
    public List<SecurityAlert> getActiveAlerts() {
        return new ArrayList<>(activeAlerts.values());
    }

    @Override
    public CompletableFuture<List<SecurityAlert>> getActiveAlertsAsync() {
        return CompletableFuture.completedFuture(getActiveAlerts());
    }

    @Override
    public List<SecurityAlert> getAlertsBySeverity(SecurityAlert.SecurityAlertSeverity severity) {
        return activeAlerts.values().stream()
                .filter(alert -> alert.getSeverity() == severity)
                .collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<SecurityAlert>> getAlertsBySeverityAsync(SecurityAlert.SecurityAlertSeverity severity) {
        return CompletableFuture.completedFuture(getAlertsBySeverity(severity));
    }

    @Override
    public List<SecurityAlert> getAlertsByType(String alertType) {
        return activeAlerts.values().stream()
                .filter(alert -> alert.getAlertType().equals(alertType))
                .collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<SecurityAlert>> getAlertsByTypeAsync(String alertType) {
        return CompletableFuture.completedFuture(getAlertsByType(alertType));
    }

    @Override
    public List<SecurityAlert> getAlertsInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return activeAlerts.values().stream()
                .filter(alert -> !alert.getTimestamp().isBefore(startTime) && !alert.getTimestamp().isAfter(endTime))
                .collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<SecurityAlert>> getAlertsInTimeRangeAsync(LocalDateTime startTime, LocalDateTime endTime) {
        return CompletableFuture.completedFuture(getAlertsInTimeRange(startTime, endTime));
    }

    @Override
    public SecurityAlert getAlert(String alertId) {
        return allAlerts.get(alertId);
    }

    @Override
    public CompletableFuture<SecurityAlert> getAlertAsync(String alertId) {
        return CompletableFuture.completedFuture(getAlert(alertId));
    }

    @Override
    public SecurityMonitoringStatistics getStatistics() {
        long total = totalAlerts.get();
        long active = activeAlerts.size();
        long acknowledged = total - active;
        long critical = getAlertsBySeverity(SecurityAlert.SecurityAlertSeverity.CRITICAL).size();
        long high = getAlertsBySeverity(SecurityAlert.SecurityAlertSeverity.HIGH).size();
        long medium = getAlertsBySeverity(SecurityAlert.SecurityAlertSeverity.MEDIUM).size();
        long low = getAlertsBySeverity(SecurityAlert.SecurityAlertSeverity.LOW).size();

        double avgResponseTime = totalOperations.get() > 0 ?
                (double) totalResponseTime.get() / totalOperations.get() : 0.0;

        Map<String, Long> eventsByTypeMap = eventsByType.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

        Map<String, Long> alertsByTypeMap = alertsByType.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

        Map<String, Long> alertsBySeverityMap = alertsBySeverity.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

        return new SecurityMonitoringStatistics(serviceId, LocalDateTime.now(),
                totalSecurityEvents.get(), total, active, acknowledged,
                critical, high, medium, low, avgResponseTime,
                eventsByTypeMap, alertsByTypeMap, alertsBySeverityMap);
    }

    @Override
    public CompletableFuture<SecurityMonitoringStatistics> getStatisticsAsync() {
        return CompletableFuture.completedFuture(getStatistics());
    }

    @Override
    public SecurityHealthCheck healthCheck() {
        long activeAlertsCount = activeAlerts.size();
        long criticalAlertsCount = getAlertsBySeverity(SecurityAlert.SecurityAlertSeverity.CRITICAL).size();

        boolean healthy = available && monitoringActive && criticalAlertsCount == 0;
        String status = healthy ? "HEALTHY" : "UNHEALTHY";
        String message = healthy ? "Security monitoring service operational" :
                "Security monitoring service has issues - " + criticalAlertsCount + " critical alerts";

        double responseTime = totalOperations.get() > 0 ?
                (double) totalResponseTime.get() / totalOperations.get() : 0.0;

        return new SecurityHealthCheck(serviceId, LocalDateTime.now(), healthy, status, message,
                activeAlertsCount, criticalAlertsCount, responseTime, monitoringActive, alertingEnabled, "HIGH");
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    /**
     * Determines if a security event should trigger an alert.
     *
     * @param eventType the event type
     * @param severity the event severity
     * @param context the event context
     * @return true if an alert should be triggered, false otherwise
     */
    private boolean shouldTriggerAlert(String eventType, SecurityAlert.SecurityAlertSeverity severity,
                                     Map<String, Object> context) {
        if (!alertingEnabled) {
            return false;
        }

        // Always trigger alerts for critical and high severity events
        if (severity == SecurityAlert.SecurityAlertSeverity.CRITICAL ||
            severity == SecurityAlert.SecurityAlertSeverity.HIGH) {
            return true;
        }

        // Trigger alerts for specific event types
        Set<String> alertableEventTypes = Set.of(
                "AUTHENTICATION_FAILURE", "AUTHORIZATION_FAILURE", "INVALID_SIGNATURE",
                "ENCRYPTION_FAILURE", "DECRYPTION_FAILURE", "KEY_COMPROMISE",
                "CERTIFICATE_EXPIRED", "CERTIFICATE_REVOKED", "HSM_ERROR",
                "BACKUP_FAILURE", "RESTORE_FAILURE", "CONFIGURATION_CHANGE"
        );

        return alertableEventTypes.contains(eventType);
    }

    /**
     * Maps security alert severity to audit risk level.
     *
     * @param severity the security alert severity
     * @return the corresponding risk level
     */
    private RiskLevel mapSeverityToRiskLevel(SecurityAlert.SecurityAlertSeverity severity) {
        return switch (severity) {
            case CRITICAL -> RiskLevel.CRITICAL;
            case HIGH -> RiskLevel.HIGH;
            case MEDIUM -> RiskLevel.MEDIUM;
            case LOW -> RiskLevel.LOW;
        };
    }
}
