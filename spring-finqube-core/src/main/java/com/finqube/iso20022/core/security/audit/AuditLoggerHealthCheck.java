package com.finqube.iso20022.core.security.audit;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Health check result for the audit logger.
 *
 * <p>This class represents the health status of the audit logging system,
 * providing information about its operational state, performance metrics,
 * and any issues that may affect its functionality.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class AuditLoggerHealthCheck {

    private final String auditLoggerId;
    private final String status;
    private final LocalDateTime timestamp;
    private final String message;
    private final long totalEntries;
    private final long entriesInLastHour;
    private final long entriesInLastDay;
    private final double averageProcessingTimeMs;
    private final long errorCount;
    private final long warningCount;
    private final String details;
    private final boolean healthy;

    /**
     * Constructs a new AuditLoggerHealthCheck instance.
     *
     * @param auditLoggerId the audit logger identifier
     * @param status the health status
     * @param timestamp the timestamp of the health check
     * @param message the health check message
     * @param totalEntries the total number of audit entries
     * @param entriesInLastHour the number of entries in the last hour
     * @param entriesInLastDay the number of entries in the last day
     * @param averageProcessingTimeMs the average processing time in milliseconds
     * @param errorCount the number of errors
     * @param warningCount the number of warnings
     * @param details additional health check details
     * @param healthy whether the audit logger is healthy
     */
    public AuditLoggerHealthCheck(String auditLoggerId, String status, LocalDateTime timestamp,
                                 String message, long totalEntries, long entriesInLastHour,
                                 long entriesInLastDay, double averageProcessingTimeMs,
                                 long errorCount, long warningCount, String details, boolean healthy) {
        this.auditLoggerId = Objects.requireNonNull(auditLoggerId, "Audit logger ID cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.message = Objects.requireNonNull(message, "Message cannot be null");
        this.totalEntries = totalEntries;
        this.entriesInLastHour = entriesInLastHour;
        this.entriesInLastDay = entriesInLastDay;
        this.averageProcessingTimeMs = averageProcessingTimeMs;
        this.errorCount = errorCount;
        this.warningCount = warningCount;
        this.details = details;
        this.healthy = healthy;
    }

    /**
     * Gets the audit logger identifier.
     *
     * @return the audit logger identifier
     */
    public String getAuditLoggerId() {
        return auditLoggerId;
    }

    /**
     * Gets the health status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the timestamp of the health check.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the health check message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the total number of audit entries.
     *
     * @return the total entries count
     */
    public long getTotalEntries() {
        return totalEntries;
    }

    /**
     * Gets the number of entries in the last hour.
     *
     * @return the entries count in last hour
     */
    public long getEntriesInLastHour() {
        return entriesInLastHour;
    }

    /**
     * Gets the number of entries in the last day.
     *
     * @return the entries count in last day
     */
    public long getEntriesInLastDay() {
        return entriesInLastDay;
    }

    /**
     * Gets the average processing time in milliseconds.
     *
     * @return the average processing time
     */
    public double getAverageProcessingTimeMs() {
        return averageProcessingTimeMs;
    }

    /**
     * Gets the number of errors.
     *
     * @return the error count
     */
    public long getErrorCount() {
        return errorCount;
    }

    /**
     * Gets the number of warnings.
     *
     * @return the warning count
     */
    public long getWarningCount() {
        return warningCount;
    }

    /**
     * Gets additional health check details.
     *
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Checks if the audit logger is healthy.
     *
     * @return true if healthy, false otherwise
     */
    public boolean isHealthy() {
        return healthy;
    }

    /**
     * Checks if the audit logger is unhealthy.
     *
     * @return true if unhealthy, false otherwise
     */
    public boolean isUnhealthy() {
        return !healthy;
    }

    /**
     * Gets a summary of the health check.
     *
     * @return the summary string
     */
    public String getSummary() {
        return String.format("AuditLogger[%s] %s - %s", auditLoggerId, status, message);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AuditLoggerHealthCheck that = (AuditLoggerHealthCheck) obj;
        return Objects.equals(auditLoggerId, that.auditLoggerId) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auditLoggerId, timestamp);
    }

    @Override
    public String toString() {
        return String.format("AuditLoggerHealthCheck{auditLoggerId='%s', status='%s', timestamp=%s, " +
                "message='%s', totalEntries=%d, entriesInLastHour=%d, entriesInLastDay=%d, " +
                "averageProcessingTimeMs=%.2f, errorCount=%d, warningCount=%d, healthy=%s}",
                auditLoggerId, status, timestamp, message, totalEntries, entriesInLastHour,
                entriesInLastDay, averageProcessingTimeMs, errorCount, warningCount, healthy);
    }

    /**
     * Creates a new builder for AuditLoggerHealthCheck.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for AuditLoggerHealthCheck.
     */
    public static class Builder {
        private String auditLoggerId;
        private String status = "UNKNOWN";
        private LocalDateTime timestamp = LocalDateTime.now();
        private String message = "Health check completed";
        private long totalEntries;
        private long entriesInLastHour;
        private long entriesInLastDay;
        private double averageProcessingTimeMs;
        private long errorCount;
        private long warningCount;
        private String details;
        private boolean healthy = true;

        public Builder auditLoggerId(String auditLoggerId) {
            this.auditLoggerId = auditLoggerId;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder totalEntries(long totalEntries) {
            this.totalEntries = totalEntries;
            return this;
        }

        public Builder entriesInLastHour(long entriesInLastHour) {
            this.entriesInLastHour = entriesInLastHour;
            return this;
        }

        public Builder entriesInLastDay(long entriesInLastDay) {
            this.entriesInLastDay = entriesInLastDay;
            return this;
        }

        public Builder averageProcessingTimeMs(double averageProcessingTimeMs) {
            this.averageProcessingTimeMs = averageProcessingTimeMs;
            return this;
        }

        public Builder errorCount(long errorCount) {
            this.errorCount = errorCount;
            return this;
        }

        public Builder warningCount(long warningCount) {
            this.warningCount = warningCount;
            return this;
        }

        public Builder details(String details) {
            this.details = details;
            return this;
        }

        public Builder healthy(boolean healthy) {
            this.healthy = healthy;
            return this;
        }

        public AuditLoggerHealthCheck build() {
            return new AuditLoggerHealthCheck(auditLoggerId, status, timestamp, message,
                    totalEntries, entriesInLastHour, entriesInLastDay, averageProcessingTimeMs,
                    errorCount, warningCount, details, healthy);
        }
    }
}
