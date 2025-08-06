package com.finqube.iso20022.core.security.audit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Audit logger for recording security events and compliance activities.
 *
 * <p>This interface defines the contract for audit logging operations, providing
 * methods to record various types of security events, compliance activities,
 * and system access for regulatory and security auditing purposes.</p>
 *
 * <p>The audit logger supports:</p>
 * <ul>
 *   <li>Synchronous and asynchronous logging</li>
 *   <li>Structured audit log entries with rich metadata</li>
 *   <li>Compliance categorization and regulatory framework tracking</li>
 *   <li>Risk assessment and security event correlation</li>
 *   <li>Audit log retrieval and reporting capabilities</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * @Autowired
 * private AuditLogger auditLogger;
 *
 * // Log a successful authentication
 * auditLogger.logAuthentication("user123", "SUCCESS", "Password authentication",
 *     AuditLogLevel.SECURITY, RiskLevel.LOW);
 *
 * // Log a failed authorization attempt
 * auditLogger.logAuthorization("user123", "FAILED", "Access denied to resource",
 *     AuditLogLevel.SECURITY, RiskLevel.HIGH);
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface AuditLogger {

    /**
     * Logs an authentication event.
     *
     * @param userId the user identifier
     * @param status the authentication status
     * @param message the audit message
     * @param level the audit log level
     * @param riskLevel the risk level assessment
     * @return the created audit log entry
     */
    AuditLogEntry logAuthentication(String userId, String status, String message,
                                   AuditLogLevel level, RiskLevel riskLevel);

    /**
     * Logs an authentication event asynchronously.
     *
     * @param userId the user identifier
     * @param status the authentication status
     * @param message the audit message
     * @param level the audit log level
     * @param riskLevel the risk level assessment
     * @return a CompletableFuture that will complete with the created audit log entry
     */
    CompletableFuture<AuditLogEntry> logAuthenticationAsync(String userId, String status, String message,
                                                           AuditLogLevel level, RiskLevel riskLevel);

    /**
     * Logs an authorization event.
     *
     * @param userId the user identifier
     * @param status the authorization status
     * @param resource the resource being accessed
     * @param action the action being performed
     * @param message the audit message
     * @param level the audit log level
     * @param riskLevel the risk level assessment
     * @return the created audit log entry
     */
    AuditLogEntry logAuthorization(String userId, String status, String resource, String action,
                                  String message, AuditLogLevel level, RiskLevel riskLevel);

    /**
     * Logs an authorization event asynchronously.
     *
     * @param userId the user identifier
     * @param status the authorization status
     * @param resource the resource being accessed
     * @param action the action being performed
     * @param message the audit message
     * @param level the audit log level
     * @param riskLevel the risk level assessment
     * @return a CompletableFuture that will complete with the created audit log entry
     */
    CompletableFuture<AuditLogEntry> logAuthorizationAsync(String userId, String status, String resource, String action,
                                                          String message, AuditLogLevel level, RiskLevel riskLevel);

    /**
     * Logs a data access event.
     *
     * @param userId the user identifier
     * @param status the access status
     * @param dataType the type of data being accessed
     * @param dataId the identifier of the data
     * @param message the audit message
     * @param level the audit log level
     * @param riskLevel the risk level assessment
     * @return the created audit log entry
     */
    AuditLogEntry logDataAccess(String userId, String status, String dataType, String dataId,
                               String message, AuditLogLevel level, RiskLevel riskLevel);

    /**
     * Logs a data access event asynchronously.
     *
     * @param userId the user identifier
     * @param status the access status
     * @param dataType the type of data being accessed
     * @param dataId the identifier of the data
     * @param message the audit message
     * @param level the audit log level
     * @param riskLevel the risk level assessment
     * @return a CompletableFuture that will complete with the created audit log entry
     */
    CompletableFuture<AuditLogEntry> logDataAccessAsync(String userId, String status, String dataType, String dataId,
                                                       String message, AuditLogLevel level, RiskLevel riskLevel);

    /**
     * Logs a system configuration change.
     *
     * @param userId the user identifier
     * @param status the change status
     * @param configuration the configuration being changed
     * @param oldValue the old value
     * @param newValue the new value
     * @param message the audit message
     * @param level the audit log level
     * @param riskLevel the risk level assessment
     * @return the created audit log entry
     */
    AuditLogEntry logConfigurationChange(String userId, String status, String configuration,
                                        String oldValue, String newValue, String message,
                                        AuditLogLevel level, RiskLevel riskLevel);

    /**
     * Logs a system configuration change asynchronously.
     *
     * @param userId the user identifier
     * @param status the change status
     * @param configuration the configuration being changed
     * @param oldValue the old value
     * @param newValue the new value
     * @param message the audit message
     * @param level the audit log level
     * @param riskLevel the risk level assessment
     * @return a CompletableFuture that will complete with the created audit log entry
     */
    CompletableFuture<AuditLogEntry> logConfigurationChangeAsync(String userId, String status, String configuration,
                                                                String oldValue, String newValue, String message,
                                                                AuditLogLevel level, RiskLevel riskLevel);

    /**
     * Logs a security event.
     *
     * @param userId the user identifier
     * @param status the event status
     * @param eventType the type of security event
     * @param message the audit message
     * @param level the audit log level
     * @param riskLevel the risk level assessment
     * @return the created audit log entry
     */
    AuditLogEntry logSecurityEvent(String userId, String status, String eventType,
                                  String message, AuditLogLevel level, RiskLevel riskLevel);

    /**
     * Logs a security event asynchronously.
     *
     * @param userId the user identifier
     * @param status the event status
     * @param eventType the type of security event
     * @param message the audit message
     * @param level the audit log level
     * @param riskLevel the risk level assessment
     * @return a CompletableFuture that will complete with the created audit log entry
     */
    CompletableFuture<AuditLogEntry> logSecurityEventAsync(String userId, String status, String eventType,
                                                          String message, AuditLogLevel level, RiskLevel riskLevel);

    /**
     * Logs a custom audit event.
     *
     * @param entry the audit log entry to log
     * @return the logged audit log entry
     */
    AuditLogEntry log(AuditLogEntry entry);

    /**
     * Logs a custom audit event asynchronously.
     *
     * @param entry the audit log entry to log
     * @return a CompletableFuture that will complete with the logged audit log entry
     */
    CompletableFuture<AuditLogEntry> logAsync(AuditLogEntry entry);

    /**
     * Retrieves audit log entries for a specific time range.
     *
     * @param fromTimestamp the start timestamp
     * @param toTimestamp the end timestamp
     * @return list of audit log entries in the specified time range
     */
    List<AuditLogEntry> getAuditLogs(LocalDateTime fromTimestamp, LocalDateTime toTimestamp);

    /**
     * Retrieves audit log entries for a specific user.
     *
     * @param userId the user identifier
     * @param fromTimestamp the start timestamp
     * @param toTimestamp the end timestamp
     * @return list of audit log entries for the specified user
     */
    List<AuditLogEntry> getAuditLogsByUser(String userId, LocalDateTime fromTimestamp, LocalDateTime toTimestamp);

    /**
     * Retrieves audit log entries by risk level.
     *
     * @param riskLevel the risk level to filter by
     * @param fromTimestamp the start timestamp
     * @param toTimestamp the end timestamp
     * @return list of audit log entries with the specified risk level
     */
    List<AuditLogEntry> getAuditLogsByRiskLevel(RiskLevel riskLevel, LocalDateTime fromTimestamp, LocalDateTime toTimestamp);

    /**
     * Retrieves audit log entries by compliance category.
     *
     * @param complianceCategory the compliance category to filter by
     * @param fromTimestamp the start timestamp
     * @param toTimestamp the end timestamp
     * @return list of audit log entries for the specified compliance category
     */
    List<AuditLogEntry> getAuditLogsByComplianceCategory(String complianceCategory, LocalDateTime fromTimestamp, LocalDateTime toTimestamp);

    /**
     * Gets the audit logger identifier.
     *
     * @return the audit logger identifier
     */
    String getAuditLoggerId();

    /**
     * Gets the audit logger display name.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Gets the audit logger version.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Checks if the audit logger is available.
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();

    /**
     * Performs a health check on the audit logger.
     *
     * @return the health check result
     */
    AuditLoggerHealthCheck healthCheck();
}
