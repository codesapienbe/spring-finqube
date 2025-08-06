package com.finqube.iso20022.core.security.audit;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Comprehensive audit log entry for security operations.
 *
 * <p>This class represents a detailed audit log entry for all security operations
 * including message processing, authentication, authorization, and system access.
 * It provides complete traceability for compliance and security auditing purposes.</p>
 *
 * <p>The audit log entry includes:</p>
 * <ul>
 *   <li>Unique identifier and timestamps</li>
 *   <li>User and session information</li>
 *   <li>Operation details and context</li>
 *   <li>Security outcomes and risk assessments</li>
 *   <li>Compliance metadata and regulatory requirements</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class AuditLogEntry {

    private final String entryId;
    private final LocalDateTime timestamp;
    private final String userId;
    private final String sessionId;
    private final String clientIp;
    private final String userAgent;
    private final String operation;
    private final String resource;
    private final String action;
    private final AuditLogLevel level;
    private final AuditLogStatus status;
    private final String message;
    private final String details;
    private final Map<String, Object> context;
    private final Map<String, Object> metadata;
    private final String correlationId;
    private final String requestId;
    private final String complianceCategory;
    private final String regulatoryFramework;
    private final RiskLevel riskLevel;
    private final String riskFactors;
    private final long processingTimeMs;
    private final String errorCode;
    private final String errorMessage;
    private final String stackTrace;

    /**
     * Constructs a new AuditLogEntry instance.
     *
     * @param entryId the unique identifier for this audit entry
     * @param timestamp the timestamp when the operation occurred
     * @param userId the user identifier who performed the operation
     * @param sessionId the session identifier
     * @param clientIp the client IP address
     * @param userAgent the user agent string
     * @param operation the operation performed
     * @param resource the resource accessed
     * @param action the specific action taken
     * @param level the audit log level
     * @param status the status of the operation
     * @param message the audit message
     * @param details additional details about the operation
     * @param context contextual information about the operation
     * @param metadata additional metadata
     * @param correlationId the correlation identifier for tracing
     * @param requestId the request identifier
     * @param complianceCategory the compliance category
     * @param regulatoryFramework the regulatory framework
     * @param riskLevel the risk level assessment
     * @param riskFactors the risk factors identified
     * @param processingTimeMs the processing time in milliseconds
     * @param errorCode the error code if operation failed
     * @param errorMessage the error message if operation failed
     * @param stackTrace the stack trace if operation failed
     */
    public AuditLogEntry(String entryId, LocalDateTime timestamp, String userId, String sessionId,
                        String clientIp, String userAgent, String operation, String resource, String action,
                        AuditLogLevel level, AuditLogStatus status, String message, String details,
                        Map<String, Object> context, Map<String, Object> metadata, String correlationId,
                        String requestId, String complianceCategory, String regulatoryFramework,
                        RiskLevel riskLevel, String riskFactors, long processingTimeMs, String errorCode,
                        String errorMessage, String stackTrace) {
        this.entryId = Objects.requireNonNull(entryId, "Entry ID cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.userId = userId;
        this.sessionId = sessionId;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.operation = Objects.requireNonNull(operation, "Operation cannot be null");
        this.resource = resource;
        this.action = action;
        this.level = Objects.requireNonNull(level, "Level cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.message = Objects.requireNonNull(message, "Message cannot be null");
        this.details = details;
        this.context = context;
        this.metadata = metadata;
        this.correlationId = correlationId;
        this.requestId = requestId;
        this.complianceCategory = complianceCategory;
        this.regulatoryFramework = regulatoryFramework;
        this.riskLevel = riskLevel;
        this.riskFactors = riskFactors;
        this.processingTimeMs = processingTimeMs;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
    }

    /**
     * Gets the unique entry identifier.
     *
     * @return the entry identifier
     */
    public String getEntryId() {
        return entryId;
    }

    /**
     * Gets the timestamp when the operation occurred.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the user identifier who performed the operation.
     *
     * @return the user identifier
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the session identifier.
     *
     * @return the session identifier
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Gets the client IP address.
     *
     * @return the client IP address
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * Gets the user agent string.
     *
     * @return the user agent string
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Gets the operation performed.
     *
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Gets the resource accessed.
     *
     * @return the resource
     */
    public String getResource() {
        return resource;
    }

    /**
     * Gets the specific action taken.
     *
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Gets the audit log level.
     *
     * @return the level
     */
    public AuditLogLevel getLevel() {
        return level;
    }

    /**
     * Gets the status of the operation.
     *
     * @return the status
     */
    public AuditLogStatus getStatus() {
        return status;
    }

    /**
     * Gets the audit message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets additional details about the operation.
     *
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Gets contextual information about the operation.
     *
     * @return the context map
     */
    public Map<String, Object> getContext() {
        return context;
    }

    /**
     * Gets additional metadata.
     *
     * @return the metadata map
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Gets the correlation identifier for tracing.
     *
     * @return the correlation identifier
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Gets the request identifier.
     *
     * @return the request identifier
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Gets the compliance category.
     *
     * @return the compliance category
     */
    public String getComplianceCategory() {
        return complianceCategory;
    }

    /**
     * Gets the regulatory framework.
     *
     * @return the regulatory framework
     */
    public String getRegulatoryFramework() {
        return regulatoryFramework;
    }

    /**
     * Gets the risk level assessment.
     *
     * @return the risk level
     */
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    /**
     * Gets the risk factors identified.
     *
     * @return the risk factors
     */
    public String getRiskFactors() {
        return riskFactors;
    }

    /**
     * Gets the processing time in milliseconds.
     *
     * @return the processing time
     */
    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    /**
     * Gets the error code if operation failed.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the error message if operation failed.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the stack trace if operation failed.
     *
     * @return the stack trace
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * Checks if the operation was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return AuditLogStatus.SUCCESS.equals(status);
    }

    /**
     * Checks if the operation failed.
     *
     * @return true if failed, false otherwise
     */
    public boolean isFailed() {
        return AuditLogStatus.FAILED.equals(status);
    }

    /**
     * Checks if this is a high-risk operation.
     *
     * @return true if high risk, false otherwise
     */
    public boolean isHighRisk() {
        return RiskLevel.HIGH.equals(riskLevel);
    }

    /**
     * Gets a summary of the audit log entry.
     *
     * @return the summary string
     */
    public String getSummary() {
        return String.format("Audit[%s] %s %s %s - %s", entryId, timestamp, operation, status, message);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AuditLogEntry that = (AuditLogEntry) obj;
        return Objects.equals(entryId, that.entryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryId);
    }

    @Override
    public String toString() {
        return String.format("AuditLogEntry{entryId='%s', timestamp=%s, userId='%s', operation='%s', " +
                "status=%s, level=%s, message='%s', riskLevel=%s, processingTimeMs=%d}",
                entryId, timestamp, userId, operation, status, level, message, riskLevel, processingTimeMs);
    }

    /**
     * Creates a new builder for AuditLogEntry.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for AuditLogEntry.
     */
    public static class Builder {
        private String entryId = UUID.randomUUID().toString();
        private LocalDateTime timestamp = LocalDateTime.now();
        private String userId;
        private String sessionId;
        private String clientIp;
        private String userAgent;
        private String operation;
        private String resource;
        private String action;
        private AuditLogLevel level = AuditLogLevel.INFO;
        private AuditLogStatus status = AuditLogStatus.SUCCESS;
        private String message;
        private String details;
        private Map<String, Object> context;
        private Map<String, Object> metadata;
        private String correlationId;
        private String requestId;
        private String complianceCategory;
        private String regulatoryFramework;
        private RiskLevel riskLevel = RiskLevel.LOW;
        private String riskFactors;
        private long processingTimeMs;
        private String errorCode;
        private String errorMessage;
        private String stackTrace;

        public Builder entryId(String entryId) {
            this.entryId = entryId;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder clientIp(String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder operation(String operation) {
            this.operation = operation;
            return this;
        }

        public Builder resource(String resource) {
            this.resource = resource;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder level(AuditLogLevel level) {
            this.level = level;
            return this;
        }

        public Builder status(AuditLogStatus status) {
            this.status = status;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder details(String details) {
            this.details = details;
            return this;
        }

        public Builder context(Map<String, Object> context) {
            this.context = context;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder complianceCategory(String complianceCategory) {
            this.complianceCategory = complianceCategory;
            return this;
        }

        public Builder regulatoryFramework(String regulatoryFramework) {
            this.regulatoryFramework = regulatoryFramework;
            return this;
        }

        public Builder riskLevel(RiskLevel riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }

        public Builder riskFactors(String riskFactors) {
            this.riskFactors = riskFactors;
            return this;
        }

        public Builder processingTimeMs(long processingTimeMs) {
            this.processingTimeMs = processingTimeMs;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder stackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
            return this;
        }

        public AuditLogEntry build() {
            return new AuditLogEntry(entryId, timestamp, userId, sessionId, clientIp, userAgent,
                    operation, resource, action, level, status, message, details, context, metadata,
                    correlationId, requestId, complianceCategory, regulatoryFramework, riskLevel,
                    riskFactors, processingTimeMs, errorCode, errorMessage, stackTrace);
        }
    }
}
