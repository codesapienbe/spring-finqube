package com.finqube.iso20022.core.security.storage;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.finqube.iso20022.core.security.audit.AuditLogLevel;
import com.finqube.iso20022.core.security.audit.AuditLogger;
import com.finqube.iso20022.core.security.audit.RiskLevel;

/**
 * Scheduled backup and retention manager for {@link KeyStorageService}.
 *
 * <p>Performs secure periodic backups of key metadata using the configured schedule
 * and retention from {@link KeyStorageProperties}. Each run:
 * <ul>
 *   <li>Creates an encrypted backup file via {@link KeyStorageService#createBackup(String)}</li>
 *   <li>Verifies backup integrity by recalculating checksum</li>
 *   <li>Enforces retention by deleting expired backup files</li>
 *   <li>Emits structured audit and application logs</li>
 * </ul>
 *
 * <p>Business logic and existing APIs are preserved. This class only orchestrates
 * existing functionality on a schedule controlled by configuration.</p>
 */
@Configuration
@EnableScheduling
public class KeyStorageBackupScheduler implements SchedulingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(KeyStorageBackupScheduler.class);

    private final KeyStorageService keyStorageService;
    private final KeyStorageProperties keyStorageProperties;
    private final AuditLogger auditLogger;

    private final ReentrantLock executionLock = new ReentrantLock();

    @Autowired
    /**
     * Creates a new scheduler instance.
     *
     * @param keyStorageService the key storage service to backup and restore
     * @param keyStorageProperties the externalized properties controlling scheduling and retention
     * @param auditLogger the audit logger used for immutable security audit events
     */
    public KeyStorageBackupScheduler(KeyStorageService keyStorageService,
                                     KeyStorageProperties keyStorageProperties,
                                     AuditLogger auditLogger) {
        this.keyStorageService = Objects.requireNonNull(keyStorageService, "keyStorageService");
        this.keyStorageProperties = Objects.requireNonNull(keyStorageProperties, "keyStorageProperties");
        this.auditLogger = Objects.requireNonNull(auditLogger, "auditLogger");
    }

    @Override
    /**
     * Registers the scheduled backup task according to the configured cron expression.
     *
     * <p>Registration occurs only when key storage and auto-backup are enabled via
     * {@link KeyStorageProperties}. The cron expression is taken from nested backup
     * configuration when present, falling back to top-level properties.</p>
     *
     * @param taskRegistrar Spring scheduled task registrar used to register the cron task
     */
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (!keyStorageProperties.isEnabled()) {
            log.info("{}", buildLogJson("INFO", "KeyStorageBackupScheduler", "Key storage disabled - backup scheduler not registered", null));
            return;
        }

        boolean autoTop = keyStorageProperties.isAutoBackupEnabled();
        boolean autoNested = keyStorageProperties.getBackup() != null && keyStorageProperties.getBackup().isAutoBackupEnabled();
        boolean autoEnabled = autoTop || autoNested;

        if (!autoEnabled) {
            log.info("{}", buildLogJson("INFO", "KeyStorageBackupScheduler", "Auto-backup disabled - scheduler not registered", null));
            return;
        }

        String cron = resolveCronExpression();
        taskRegistrar.addCronTask(this::runBackupCycleSafely, cron);
        log.info("{}", buildLogJson("INFO", "KeyStorageBackupScheduler", "Registered backup scheduler with cron: " + cron, null));
    }

    private void runBackupCycleSafely() {
        if (!executionLock.tryLock()) {
            log.warn("{}", buildLogJson("WARN", "KeyStorageBackupScheduler", "Previous backup cycle still running - skipping this schedule", null));
            return;
        }
        String correlationId = UUID.randomUUID().toString();
        try {
            runBackupCycle(correlationId);
        } catch (Exception ex) {
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_BACKUP_SCHEDULED",
                    "Scheduled backup cycle failed: " + ex.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);
            log.error("{}", buildLogJson("ERROR", "KeyStorageBackupScheduler", "Scheduled backup cycle failed", correlationId), ex);
        } finally {
            executionLock.unlock();
        }
    }

    private void runBackupCycle(String correlationId) {
        String location = resolveBackupLocation();
        KeyBackupInfo info = keyStorageService.createBackup(location);

        // Verify checksum integrity
        boolean verified = verifyBackupChecksum(info.getBackupLocation(), info.getChecksum());
        if (!verified) {
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_BACKUP_VERIFY",
                    "Backup checksum verification failed for " + info.getBackupId(), AuditLogLevel.ERROR, RiskLevel.MEDIUM);
            log.warn("{}", buildLogJson("WARN", "KeyStorageBackupScheduler", "Backup checksum verification failed for " + info.getBackupId(), correlationId));
        } else {
            auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "KEY_BACKUP_VERIFY",
                    "Backup checksum verified for " + info.getBackupId(), AuditLogLevel.SECURITY, RiskLevel.LOW);
            log.info("{}", buildLogJson("INFO", "KeyStorageBackupScheduler", "Backup created and verified: " + info.getBackupId(), correlationId));
        }

        // Enforce retention policy
        int deleted = enforceRetention(location, resolveRetention());
        if (deleted > 0) {
            log.info("{}", buildLogJson("INFO", "KeyStorageBackupScheduler",
                    "Retention cleanup deleted " + deleted + " old backup file(s)", correlationId));
        }
    }

    private boolean verifyBackupChecksum(String backupAbsolutePath, String expectedChecksum) {
        if (backupAbsolutePath == null || expectedChecksum == null || expectedChecksum.isBlank()) {
            return false;
        }
        try {
            File file = new File(backupAbsolutePath);
            if (!file.exists() || !file.isFile()) {
                return false;
            }
            String actual = calculateSha256(file);
            return expectedChecksum.equalsIgnoreCase(actual);
        } catch (Exception ex) {
            return false;
        }
    }

    private int enforceRetention(String backupDirPath, Duration retention) {
        try {
            File dir = new File(backupDirPath);
            if (!dir.exists() || !dir.isDirectory()) {
                return 0;
            }
            long cutoffMillis = System.currentTimeMillis() - retention.toMillis();
            File[] candidates = dir.listFiles((d, name) -> name != null && name.startsWith("backup-") && name.endsWith(".enc"));
            if (candidates == null || candidates.length == 0) {
                return 0;
            }
            int deleted = 0;
            for (File f : candidates) {
                if (f.lastModified() < cutoffMillis) {
                    try {
                        Files.deleteIfExists(f.toPath());
                        deleted++;
                    } catch (Exception ignore) {
                        // Keep going; deletion failures are non-fatal
                    }
                }
            }
            return deleted;
        } catch (Exception ex) {
            return 0;
        }
    }

    private String resolveBackupLocation() {
        if (keyStorageProperties.getBackup() != null && keyStorageProperties.getBackup().getLocation() != null
                && !keyStorageProperties.getBackup().getLocation().isBlank()) {
            return keyStorageProperties.getBackup().getLocation();
        }
        return keyStorageProperties.getBackupLocation();
    }

    private Duration resolveRetention() {
        if (keyStorageProperties.getBackup() != null && keyStorageProperties.getBackup().getRetention() != null) {
            return keyStorageProperties.getBackup().getRetention();
        }
        return keyStorageProperties.getBackupRetention();
    }

    private String resolveCronExpression() {
        String candidate = null;
        if (keyStorageProperties.getBackup() != null) {
            candidate = keyStorageProperties.getBackup().getSchedule();
        }
        if (candidate == null || candidate.isBlank()) {
            candidate = keyStorageProperties.getBackupSchedule();
        }
        if (candidate == null || candidate.isBlank()) {
            // default daily at 02:00
            candidate = "0 0 2 * * *";
        }
        // Normalize: if it's 5-field (min-based) CRON, prefix seconds
        String[] parts = candidate.trim().split("\\s+");
        if (parts.length == 5) {
            return "0 " + candidate.trim();
        }
        return candidate.trim();
    }

    private String calculateSha256(File file) throws Exception {
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] hash = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }

    private String buildLogJson(String level, String component, String message, String correlationId) {
        // Minimal structured JSON to application.log (no sensitive data)
        String ts = DateTimeFormatter.ISO_INSTANT.format(Instant.now().atZone(ZoneOffset.UTC));
        String cid = correlationId == null ? "" : ("\", \"correlation_id\": \"" + correlationId + "\"");
        return "{\"timestamp\": \"" + ts + "\", \"level\": \"" + level +
                "\", \"component\": \"" + component + "\", \"message\": \"" + escape(message) + "\"" + cid + "}";
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
