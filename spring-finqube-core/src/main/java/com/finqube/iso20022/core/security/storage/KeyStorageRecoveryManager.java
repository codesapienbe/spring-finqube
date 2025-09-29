package com.finqube.iso20022.core.security.storage;

import java.io.File;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finqube.iso20022.core.security.audit.AuditLogLevel;
import com.finqube.iso20022.core.security.audit.AuditLogger;
import com.finqube.iso20022.core.security.audit.RiskLevel;

/**
 * Recovery manager for restoring key storage from the latest secure backup.
 *
 * <p>Provides an explicit operation to restore from newest backup file found in the
 * configured backup directory. Uses checksum verification and logs structured
 * events suitable for monitoring.</p>
 */
@Component
public class KeyStorageRecoveryManager {

    private static final Logger log = LoggerFactory.getLogger(KeyStorageRecoveryManager.class);

    private final KeyStorageService keyStorageService;
    private final KeyStorageProperties keyStorageProperties;
    private final AuditLogger auditLogger;

    @Autowired
    public KeyStorageRecoveryManager(KeyStorageService keyStorageService,
                                     KeyStorageProperties keyStorageProperties,
                                     AuditLogger auditLogger) {
        this.keyStorageService = Objects.requireNonNull(keyStorageService, "keyStorageService");
        this.keyStorageProperties = Objects.requireNonNull(keyStorageProperties, "keyStorageProperties");
        this.auditLogger = Objects.requireNonNull(auditLogger, "auditLogger");
    }

    /**
     * Restores keys from the most recent backup in the configured location.
     *
     * @return the restore information when a backup is found
     * @throws KeyStorageException when no backup can be found or restore fails
     */
    public KeyRestoreInfo restoreLatest() throws KeyStorageException {
        String correlationId = UUID.randomUUID().toString();
        String backupDirPath = resolveBackupLocation();
        File backupDir = new File(backupDirPath);
        if (!backupDir.exists() || !backupDir.isDirectory()) {
            throw new KeyStorageException("Backup directory not found: " + backupDirPath,
                    keyStorageService.getStorageServiceId(), null, KeyStorageException.KeyStorageErrorType.RESTORE_FAILED);
        }

        File[] candidates = backupDir.listFiles((d, name) -> name != null && name.startsWith("backup-") && name.endsWith(".enc"));
        if (candidates == null || candidates.length == 0) {
            throw new KeyStorageException("No backup files found in: " + backupDirPath,
                    keyStorageService.getStorageServiceId(), null, KeyStorageException.KeyStorageErrorType.RESTORE_FAILED);
        }

        // Pick the newest by lastModified
        File newest = Arrays.stream(candidates).max(Comparator.comparingLong(File::lastModified))
                .orElseThrow(() -> new KeyStorageException("Failed to select latest backup",
                        keyStorageService.getStorageServiceId(), null, KeyStorageException.KeyStorageErrorType.RESTORE_FAILED));

        auditLogger.logSecurityEvent("SYSTEM", "INFO", "KEY_RESTORE_REQUEST",
                "Attempting restore from latest backup: " + newest.getAbsolutePath(),
                AuditLogLevel.SECURITY, RiskLevel.MEDIUM);

        KeyRestoreInfo info = keyStorageService.restoreFromBackup(newest.getAbsolutePath());

        if (info.isSuccessful()) {
            auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "KEY_RESTORE_LATEST",
                    "Restore from latest backup succeeded with " + info.getRestoredKeyCount() + " keys",
                    AuditLogLevel.SECURITY, RiskLevel.LOW);
            log.info("{}", buildLogJson("INFO", "KeyStorageRecoveryManager",
                    "Restore latest succeeded: " + newest.getName(), correlationId));
        } else {
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_RESTORE_LATEST",
                    "Restore from latest backup completed with failures (restored=" + info.getRestoredKeyCount() + ", failed=" + info.getFailedKeyCount() + ")",
                    AuditLogLevel.ERROR, RiskLevel.MEDIUM);
            log.warn("{}", buildLogJson("WARN", "KeyStorageRecoveryManager",
                    "Restore latest completed with failures: " + newest.getName(), correlationId));
        }

        return info;
    }

    private String resolveBackupLocation() {
        if (keyStorageProperties.getBackup() != null && keyStorageProperties.getBackup().getLocation() != null
                && !keyStorageProperties.getBackup().getLocation().isBlank()) {
            return keyStorageProperties.getBackup().getLocation();
        }
        return keyStorageProperties.getBackupLocation();
    }

    private String buildLogJson(String level, String component, String message, String correlationId) {
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
