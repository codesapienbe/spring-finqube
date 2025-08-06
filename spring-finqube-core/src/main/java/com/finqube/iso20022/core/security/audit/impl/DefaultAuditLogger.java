package com.finqube.iso20022.core.security.audit.impl;

import com.finqube.iso20022.core.security.audit.AuditLogEntry;
import com.finqube.iso20022.core.security.audit.AuditLogLevel;
import com.finqube.iso20022.core.security.audit.AuditLogStatus;
import com.finqube.iso20022.core.security.audit.AuditLogger;
import com.finqube.iso20022.core.security.audit.AuditLoggerHealthCheck;
import com.finqube.iso20022.core.security.audit.RiskLevel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Default implementation of the AuditLogger interface.
 *
 * <p>This implementation provides comprehensive audit logging capabilities
 * with structured logging, compliance reporting, and performance monitoring.
 * It maintains an in-memory audit log and provides methods for retrieving
 * and analyzing audit data.</p>
 *
 * <p>Features include:</p>
 * <ul>
 *   <li>Structured audit log entries with rich metadata</li>
 *   <li>Compliance categorization and regulatory framework tracking</li>
 *   <li>Risk assessment and security event correlation</li>
 *   <li>Performance monitoring and health checks</li>
 *   <li>Asynchronous logging for improved performance</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Component
public class DefaultAuditLogger implements AuditLogger {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAuditLogger.class);

    private final String auditLoggerId;
    private final String displayName;
    private final String version;
    private final List<AuditLogEntry> auditLog;
    private final AtomicLong totalEntries;
    private final AtomicLong errorCount;
    private final AtomicLong warningCount;
    private final Map<String, Long> userActivityCount;
    private final Map<String, Long> operationCount;
    private final Map<RiskLevel, Long> riskLevelCount;
    private final Map<AuditLogLevel, Long> levelCount;
    private final List<Long> processingTimes;
    private final Object lock = new Object();

    /**
     * Constructs a new DefaultAuditLogger instance.
     */
    public DefaultAuditLogger() {
        this.auditLoggerId = UUID.randomUUID().toString();
        this.displayName = "Default Audit Logger";
        this.version = "0.1.0";
        this.auditLog = Collections.synchronizedList(new ArrayList<>());
        this.totalEntries = new AtomicLong(0);
        this.errorCount = new AtomicLong(0);
        this.warningCount = new AtomicLong(0);
        this.userActivityCount = new ConcurrentHashMap<>();
        this.operationCount = new ConcurrentHashMap<>();
        this.riskLevelCount = new ConcurrentHashMap<>();
        this.levelCount = new ConcurrentHashMap<>();
        this.processingTimes = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public AuditLogEntry logAuthentication(String userId, String status, String message,
                                         AuditLogLevel level, RiskLevel riskLevel) {
        long startTime = System.currentTimeMillis();

        AuditLogEntry entry = AuditLogEntry.builder()
                .userId(userId)
                .operation("AUTHENTICATION")
                .action("LOGIN")
                .status(AuditLogStatus.valueOf(status.toUpperCase()))
                .message(message)
                .level(level)
                .riskLevel(riskLevel)
                .complianceCategory("AUTHENTICATION")
                .regulatoryFramework("ISO27001")
                .build();

        AuditLogEntry loggedEntry = log(entry);

        long processingTime = System.currentTimeMillis() - startTime;
        recordProcessingTime(processingTime);

        logger.info("Authentication audit logged: {} - {} - {} - {}ms",
                userId, status, message, processingTime);

        return loggedEntry;
    }

    @Override
    public CompletableFuture<AuditLogEntry> logAuthenticationAsync(String userId, String status, String message,
                                                                 AuditLogLevel level, RiskLevel riskLevel) {
        return CompletableFuture.supplyAsync(() -> logAuthentication(userId, status, message, level, riskLevel));
    }

    @Override
    public AuditLogEntry logAuthorization(String userId, String status, String resource, String action,
                                        String message, AuditLogLevel level, RiskLevel riskLevel) {
        long startTime = System.currentTimeMillis();

        AuditLogEntry entry = AuditLogEntry.builder()
                .userId(userId)
                .operation("AUTHORIZATION")
                .resource(resource)
                .action(action)
                .status(AuditLogStatus.valueOf(status.toUpperCase()))
                .message(message)
                .level(level)
                .riskLevel(riskLevel)
                .complianceCategory("AUTHORIZATION")
                .regulatoryFramework("ISO27001")
                .build();

        AuditLogEntry loggedEntry = log(entry);

        long processingTime = System.currentTimeMillis() - startTime;
        recordProcessingTime(processingTime);

        logger.info("Authorization audit logged: {} - {} - {} - {} - {}ms",
                userId, status, resource, action, processingTime);

        return loggedEntry;
    }

    @Override
    public CompletableFuture<AuditLogEntry> logAuthorizationAsync(String userId, String status, String resource, String action,
                                                                String message, AuditLogLevel level, RiskLevel riskLevel) {
        return CompletableFuture.supplyAsync(() -> logAuthorization(userId, status, resource, action, message, level, riskLevel));
    }

    @Override
    public AuditLogEntry logDataAccess(String userId, String status, String dataType, String dataId,
                                     String message, AuditLogLevel level, RiskLevel riskLevel) {
        long startTime = System.currentTimeMillis();

        AuditLogEntry entry = AuditLogEntry.builder()
                .userId(userId)
                .operation("DATA_ACCESS")
                .resource(dataType)
                .action(status)
                .message(message)
                .level(level)
                .riskLevel(riskLevel)
                .complianceCategory("DATA_PROTECTION")
                .regulatoryFramework("GDPR")
                .context(Map.of("dataId", dataId, "dataType", dataType))
                .build();

        AuditLogEntry loggedEntry = log(entry);

        long processingTime = System.currentTimeMillis() - startTime;
        recordProcessingTime(processingTime);

        logger.info("Data access audit logged: {} - {} - {} - {} - {}ms",
                userId, status, dataType, dataId, processingTime);

        return loggedEntry;
    }

    @Override
    public CompletableFuture<AuditLogEntry> logDataAccessAsync(String userId, String status, String dataType, String dataId,
                                                             String message, AuditLogLevel level, RiskLevel riskLevel) {
        return CompletableFuture.supplyAsync(() -> logDataAccess(userId, status, dataType, dataId, message, level, riskLevel));
    }

    @Override
    public AuditLogEntry logConfigurationChange(String userId, String status, String configuration,
                                               String oldValue, String newValue, String message,
                                               AuditLogLevel level, RiskLevel riskLevel) {
        long startTime = System.currentTimeMillis();

        AuditLogEntry entry = AuditLogEntry.builder()
                .userId(userId)
                .operation("CONFIGURATION_CHANGE")
                .resource(configuration)
                .action("MODIFY")
                .status(AuditLogStatus.valueOf(status.toUpperCase()))
                .message(message)
                .level(level)
                .riskLevel(riskLevel)
                .complianceCategory("SYSTEM_MANAGEMENT")
                .regulatoryFramework("ISO27001")
                .context(Map.of("oldValue", oldValue, "newValue", newValue))
                .build();

        AuditLogEntry loggedEntry = log(entry);

        long processingTime = System.currentTimeMillis() - startTime;
        recordProcessingTime(processingTime);

        logger.info("Configuration change audit logged: {} - {} - {} - {}ms",
                userId, configuration, status, processingTime);

        return loggedEntry;
    }

    @Override
    public CompletableFuture<AuditLogEntry> logConfigurationChangeAsync(String userId, String status, String configuration,
                                                                       String oldValue, String newValue, String message,
                                                                       AuditLogLevel level, RiskLevel riskLevel) {
        return CompletableFuture.supplyAsync(() -> logConfigurationChange(userId, status, configuration, oldValue, newValue, message, level, riskLevel));
    }

    @Override
    public AuditLogEntry logSecurityEvent(String userId, String status, String eventType,
                                        String message, AuditLogLevel level, RiskLevel riskLevel) {
        long startTime = System.currentTimeMillis();

        AuditLogEntry entry = AuditLogEntry.builder()
                .userId(userId)
                .operation("SECURITY_EVENT")
                .action(eventType)
                .status(AuditLogStatus.valueOf(status.toUpperCase()))
                .message(message)
                .level(level)
                .riskLevel(riskLevel)
                .complianceCategory("SECURITY_MONITORING")
                .regulatoryFramework("ISO27001")
                .build();

        AuditLogEntry loggedEntry = log(entry);

        long processingTime = System.currentTimeMillis() - startTime;
        recordProcessingTime(processingTime);

        logger.info("Security event audit logged: {} - {} - {} - {} - {}ms",
                userId, eventType, status, message, processingTime);

        return loggedEntry;
    }

    @Override
    public CompletableFuture<AuditLogEntry> logSecurityEventAsync(String userId, String status, String eventType,
                                                                String message, AuditLogLevel level, RiskLevel riskLevel) {
        return CompletableFuture.supplyAsync(() -> logSecurityEvent(userId, status, eventType, message, level, riskLevel));
    }

    @Override
    public AuditLogEntry log(AuditLogEntry entry) {
        synchronized (lock) {
            auditLog.add(entry);
            totalEntries.incrementAndGet();

            // Update counters
            if (entry.getUserId() != null) {
                userActivityCount.merge(entry.getUserId(), 1L, Long::sum);
            }

            if (entry.getOperation() != null) {
                operationCount.merge(entry.getOperation(), 1L, Long::sum);
            }

            riskLevelCount.merge(entry.getRiskLevel(), 1L, Long::sum);
            levelCount.merge(entry.getLevel(), 1L, Long::sum);

            // Update error/warning counts
            if (AuditLogLevel.ERROR.equals(entry.getLevel()) || AuditLogLevel.CRITICAL.equals(entry.getLevel())) {
                errorCount.incrementAndGet();
            } else if (AuditLogLevel.WARN.equals(entry.getLevel())) {
                warningCount.incrementAndGet();
            }

            // Maintain log size (keep last 100,000 entries)
            if (auditLog.size() > 100000) {
                auditLog.subList(0, auditLog.size() - 100000).clear();
            }
        }

        return entry;
    }

    @Override
    public CompletableFuture<AuditLogEntry> logAsync(AuditLogEntry entry) {
        return CompletableFuture.supplyAsync(() -> log(entry));
    }

    @Override
    public List<AuditLogEntry> getAuditLogs(LocalDateTime fromTimestamp, LocalDateTime toTimestamp) {
        synchronized (lock) {
            return auditLog.stream()
                    .filter(entry -> !entry.getTimestamp().isBefore(fromTimestamp) && !entry.getTimestamp().isAfter(toTimestamp))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<AuditLogEntry> getAuditLogsByUser(String userId, LocalDateTime fromTimestamp, LocalDateTime toTimestamp) {
        synchronized (lock) {
            return auditLog.stream()
                    .filter(entry -> userId.equals(entry.getUserId()))
                    .filter(entry -> !entry.getTimestamp().isBefore(fromTimestamp) && !entry.getTimestamp().isAfter(toTimestamp))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<AuditLogEntry> getAuditLogsByRiskLevel(RiskLevel riskLevel, LocalDateTime fromTimestamp, LocalDateTime toTimestamp) {
        synchronized (lock) {
            return auditLog.stream()
                    .filter(entry -> riskLevel.equals(entry.getRiskLevel()))
                    .filter(entry -> !entry.getTimestamp().isBefore(fromTimestamp) && !entry.getTimestamp().isAfter(toTimestamp))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<AuditLogEntry> getAuditLogsByComplianceCategory(String complianceCategory, LocalDateTime fromTimestamp, LocalDateTime toTimestamp) {
        synchronized (lock) {
            return auditLog.stream()
                    .filter(entry -> complianceCategory.equals(entry.getComplianceCategory()))
                    .filter(entry -> !entry.getTimestamp().isBefore(fromTimestamp) && !entry.getTimestamp().isAfter(toTimestamp))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public String getAuditLoggerId() {
        return auditLoggerId;
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
        return true;
    }

    @Override
    public AuditLoggerHealthCheck healthCheck() {
        synchronized (lock) {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

            long entriesInLastHour = auditLog.stream()
                    .filter(entry -> !entry.getTimestamp().isBefore(oneHourAgo))
                    .count();

            long entriesInLastDay = auditLog.stream()
                    .filter(entry -> !entry.getTimestamp().isBefore(oneDayAgo))
                    .count();

            double averageProcessingTime = processingTimes.isEmpty() ? 0.0 :
                    processingTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

            boolean healthy = errorCount.get() < 100 && warningCount.get() < 1000;
            String status = healthy ? "HEALTHY" : "UNHEALTHY";
            String message = healthy ? "Audit logger is operating normally" :
                    "Audit logger has high error/warning count";

            return AuditLoggerHealthCheck.builder()
                    .auditLoggerId(auditLoggerId)
                    .status(status)
                    .message(message)
                    .totalEntries(totalEntries.get())
                    .entriesInLastHour(entriesInLastHour)
                    .entriesInLastDay(entriesInLastDay)
                    .averageProcessingTimeMs(averageProcessingTime)
                    .errorCount(errorCount.get())
                    .warningCount(warningCount.get())
                    .healthy(healthy)
                    .build();
        }
    }

    private void recordProcessingTime(long processingTime) {
        synchronized (lock) {
            processingTimes.add(processingTime);
            if (processingTimes.size() > 1000) {
                processingTimes.subList(0, processingTimes.size() - 1000).clear();
            }
        }
    }
}
