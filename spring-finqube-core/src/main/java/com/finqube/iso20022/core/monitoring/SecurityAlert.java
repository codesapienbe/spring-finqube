package com.finqube.iso20022.core.monitoring;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a security alert in the monitoring system.
 *
 * <p>This class provides comprehensive information about security events that require
 * attention, including alert severity, source, and detailed context information.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class SecurityAlert {

    private final String alertId;
    private final String alertType;
    private final SecurityAlertSeverity severity;
    private final String source;
    private final String message;
    private final LocalDateTime timestamp;
    private final Map<String, Object> context;
    private final boolean acknowledged;
    private final String acknowledgedBy;
    private final LocalDateTime acknowledgedAt;
    private final String correlationId;

    /**
     * Constructs a new SecurityAlert with the specified parameters.
     *
     * @param alertId the unique alert identifier
     * @param alertType the type of security alert
     * @param severity the alert severity level
     * @param source the source of the alert
     * @param message the alert message
     * @param timestamp the alert timestamp
     * @param context additional context information
     * @param correlationId the correlation identifier for tracking related events
     */
    public SecurityAlert(String alertId, String alertType, SecurityAlertSeverity severity,
                        String source, String message, LocalDateTime timestamp,
                        Map<String, Object> context, String correlationId) {
        this.alertId = Objects.requireNonNull(alertId, "Alert ID cannot be null");
        this.alertType = Objects.requireNonNull(alertType, "Alert type cannot be null");
        this.severity = Objects.requireNonNull(severity, "Severity cannot be null");
        this.source = Objects.requireNonNull(source, "Source cannot be null");
        this.message = Objects.requireNonNull(message, "Message cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.context = context != null ? Map.copyOf(context) : Map.of();
        this.correlationId = correlationId;
        this.acknowledged = false;
        this.acknowledgedBy = null;
        this.acknowledgedAt = null;
    }

    /**
     * Constructs a SecurityAlert with acknowledgment information.
     *
     * @param alertId the unique alert identifier
     * @param alertType the type of security alert
     * @param severity the alert severity level
     * @param source the source of the alert
     * @param message the alert message
     * @param timestamp the alert timestamp
     * @param context additional context information
     * @param correlationId the correlation identifier
     * @param acknowledged whether the alert has been acknowledged
     * @param acknowledgedBy who acknowledged the alert
     * @param acknowledgedAt when the alert was acknowledged
     */
    public SecurityAlert(String alertId, String alertType, SecurityAlertSeverity severity,
                        String source, String message, LocalDateTime timestamp,
                        Map<String, Object> context, String correlationId,
                        boolean acknowledged, String acknowledgedBy, LocalDateTime acknowledgedAt) {
        this.alertId = Objects.requireNonNull(alertId, "Alert ID cannot be null");
        this.alertType = Objects.requireNonNull(alertType, "Alert type cannot be null");
        this.severity = Objects.requireNonNull(severity, "Severity cannot be null");
        this.source = Objects.requireNonNull(source, "Source cannot be null");
        this.message = Objects.requireNonNull(message, "Message cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.context = context != null ? Map.copyOf(context) : Map.of();
        this.correlationId = correlationId;
        this.acknowledged = acknowledged;
        this.acknowledgedBy = acknowledgedBy;
        this.acknowledgedAt = acknowledgedAt;
    }

    /**
     * Gets the unique alert identifier.
     *
     * @return the alert identifier
     */
    public String getAlertId() {
        return alertId;
    }

    /**
     * Gets the alert type.
     *
     * @return the alert type
     */
    public String getAlertType() {
        return alertType;
    }

    /**
     * Gets the alert severity level.
     *
     * @return the severity level
     */
    public SecurityAlertSeverity getSeverity() {
        return severity;
    }

    /**
     * Gets the source of the alert.
     *
     * @return the alert source
     */
    public String getSource() {
        return source;
    }

    /**
     * Gets the alert message.
     *
     * @return the alert message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the alert timestamp.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the additional context information.
     *
     * @return the context map
     */
    public Map<String, Object> getContext() {
        return context;
    }

    /**
     * Gets the correlation identifier.
     *
     * @return the correlation identifier
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Checks if the alert has been acknowledged.
     *
     * @return true if acknowledged, false otherwise
     */
    public boolean isAcknowledged() {
        return acknowledged;
    }

    /**
     * Gets who acknowledged the alert.
     *
     * @return the acknowledger, or null if not acknowledged
     */
    public String getAcknowledgedBy() {
        return acknowledgedBy;
    }

    /**
     * Gets when the alert was acknowledged.
     *
     * @return the acknowledgment timestamp, or null if not acknowledged
     */
    public LocalDateTime getAcknowledgedAt() {
        return acknowledgedAt;
    }

    /**
     * Checks if this alert requires immediate attention.
     *
     * @return true if immediate attention is required, false otherwise
     */
    public boolean requiresImmediateAttention() {
        return severity == SecurityAlertSeverity.CRITICAL || severity == SecurityAlertSeverity.HIGH;
    }

    /**
     * Gets the age of the alert in minutes.
     *
     * @return the age in minutes
     */
    public long getAgeInMinutes() {
        return java.time.Duration.between(timestamp, LocalDateTime.now()).toMinutes();
    }

    @Override
    public String toString() {
        return String.format("SecurityAlert{alertId='%s', type='%s', severity=%s, source='%s', " +
                "acknowledged=%s, age=%d minutes}", alertId, alertType, severity, source,
                acknowledged, getAgeInMinutes());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SecurityAlert that = (SecurityAlert) obj;
        return Objects.equals(alertId, that.alertId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertId);
    }

    /**
     * Security alert severity levels.
     */
    public enum SecurityAlertSeverity {
        LOW("Low", "Low priority security event"),
        MEDIUM("Medium", "Medium priority security event"),
        HIGH("High", "High priority security event requiring attention"),
        CRITICAL("Critical", "Critical security event requiring immediate action");

        private final String displayName;
        private final String description;

        SecurityAlertSeverity(String displayName, String description) {
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
