package com.finqube.iso20022.core.security.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

/**
 * Configuration properties for key storage operations.
 *
 * <p>This class provides externalized configuration for key storage settings
 * including storage backend, encryption, backup, and performance parameters.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Component
@ConfigurationProperties(prefix = "iso20022.security.key-storage")
public class KeyStorageProperties {

    /**
     * Whether key storage is enabled.
     */
    private boolean enabled = true;

    /**
     * The storage backend type.
     */
    private String backend = "memory";

    /**
     * Storage location for file-based backends.
     */
    private String location = "keys/";

    /**
     * Whether to enable encryption at rest.
     */
    private boolean encryptionEnabled = true;

    /**
     * Encryption algorithm for keys at rest.
     */
    private String encryptionAlgorithm = "AES-256-GCM";

    /**
     * Master key for encrypting stored keys.
     */
    private String masterKey;

    /**
     * Whether to enable automatic backup.
     */
    private boolean autoBackupEnabled = true;

    /**
     * Backup schedule (cron expression).
     */
    private String backupSchedule = "0 2 * * *"; // Daily at 2 AM

    /**
     * Backup retention period.
     */
    private Duration backupRetention = Duration.ofDays(30);

    /**
     * Backup location.
     */
    private String backupLocation = "backups/";

    /**
     * Whether to enable access control.
     */
    private boolean accessControlEnabled = true;

    /**
     * Maximum number of keys to store.
     */
    private int maxKeys = 10000;

    /**
     * Key expiration warning threshold.
     */
    private Duration expirationWarningThreshold = Duration.ofDays(30);

    /**
     * Operation timeout.
     */
    private Duration operationTimeout = Duration.ofSeconds(30);

    /**
     * Health check interval.
     */
    private Duration healthCheckInterval = Duration.ofMinutes(5);

    /**
     * Performance monitoring settings.
     */
    private Performance performance = new Performance();

    /**
     * Security settings.
     */
    private Security security = new Security();

    /**
     * Backup settings.
     */
    private Backup backup = new Backup();

    /**
     * Performance configuration.
     */
    public static class Performance {
        /**
         * Whether to enable performance monitoring.
         */
        private boolean monitoringEnabled = true;

        /**
         * Whether to enable operation caching.
         */
        private boolean cachingEnabled = true;

        /**
         * Cache size for key operations.
         */
        private int cacheSize = 1000;

        /**
         * Cache expiration time.
         */
        private Duration cacheExpiration = Duration.ofHours(1);

        /**
         * Whether to enable async operations.
         */
        private boolean asyncEnabled = true;

        /**
         * Thread pool size for async operations.
         */
        private int threadPoolSize = 4;

        public boolean isMonitoringEnabled() {
            return monitoringEnabled;
        }

        public void setMonitoringEnabled(boolean monitoringEnabled) {
            this.monitoringEnabled = monitoringEnabled;
        }

        public boolean isCachingEnabled() {
            return cachingEnabled;
        }

        public void setCachingEnabled(boolean cachingEnabled) {
            this.cachingEnabled = cachingEnabled;
        }

        public int getCacheSize() {
            return cacheSize;
        }

        public void setCacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
        }

        public Duration getCacheExpiration() {
            return cacheExpiration;
        }

        public void setCacheExpiration(Duration cacheExpiration) {
            this.cacheExpiration = cacheExpiration;
        }

        public boolean isAsyncEnabled() {
            return asyncEnabled;
        }

        public void setAsyncEnabled(boolean asyncEnabled) {
            this.asyncEnabled = asyncEnabled;
        }

        public int getThreadPoolSize() {
            return threadPoolSize;
        }

        public void setThreadPoolSize(int threadPoolSize) {
            this.threadPoolSize = threadPoolSize;
        }
    }

    /**
     * Security configuration.
     */
    public static class Security {
        /**
         * Whether to enable audit logging.
         */
        private boolean auditLoggingEnabled = true;

        /**
         * Whether to enable key rotation.
         */
        private boolean keyRotationEnabled = true;

        /**
         * Key rotation interval.
         */
        private Duration keyRotationInterval = Duration.ofDays(90);

        /**
         * Whether to enable key expiration.
         */
        private boolean keyExpirationEnabled = true;

        /**
         * Default key expiration period.
         */
        private Duration defaultKeyExpiration = Duration.ofDays(365);

        /**
         * Whether to enable secure deletion.
         */
        private boolean secureDeletionEnabled = true;

        /**
         * Number of overwrite passes for secure deletion.
         */
        private int secureDeletionPasses = 3;

        public boolean isAuditLoggingEnabled() {
            return auditLoggingEnabled;
        }

        public void setAuditLoggingEnabled(boolean auditLoggingEnabled) {
            this.auditLoggingEnabled = auditLoggingEnabled;
        }

        public boolean isKeyRotationEnabled() {
            return keyRotationEnabled;
        }

        public void setKeyRotationEnabled(boolean keyRotationEnabled) {
            this.keyRotationEnabled = keyRotationEnabled;
        }

        public Duration getKeyRotationInterval() {
            return keyRotationInterval;
        }

        public void setKeyRotationInterval(Duration keyRotationInterval) {
            this.keyRotationInterval = keyRotationInterval;
        }

        public boolean isKeyExpirationEnabled() {
            return keyExpirationEnabled;
        }

        public void setKeyExpirationEnabled(boolean keyExpirationEnabled) {
            this.keyExpirationEnabled = keyExpirationEnabled;
        }

        public Duration getDefaultKeyExpiration() {
            return defaultKeyExpiration;
        }

        public void setDefaultKeyExpiration(Duration defaultKeyExpiration) {
            this.defaultKeyExpiration = defaultKeyExpiration;
        }

        public boolean isSecureDeletionEnabled() {
            return secureDeletionEnabled;
        }

        public void setSecureDeletionEnabled(boolean secureDeletionEnabled) {
            this.secureDeletionEnabled = secureDeletionEnabled;
        }

        public int getSecureDeletionPasses() {
            return secureDeletionPasses;
        }

        public void setSecureDeletionPasses(int secureDeletionPasses) {
            this.secureDeletionPasses = secureDeletionPasses;
        }
    }

    /**
     * Backup configuration.
     */
    public static class Backup {
        /**
         * Whether to enable automatic backup.
         */
        private boolean autoBackupEnabled = true;

        /**
         * Backup schedule (cron expression).
         */
        private String schedule = "0 2 * * *"; // Daily at 2 AM

        /**
         * Backup retention period.
         */
        private Duration retention = Duration.ofDays(30);

        /**
         * Backup location.
         */
        private String location = "backups/";

        /**
         * Whether to enable backup encryption.
         */
        private boolean encryptionEnabled = true;

        /**
         * Backup encryption algorithm.
         */
        private String encryptionAlgorithm = "AES-256-GCM";

        /**
         * Whether to enable backup compression.
         */
        private boolean compressionEnabled = true;

        /**
         * Backup compression level (1-9).
         */
        private int compressionLevel = 6;

        public boolean isAutoBackupEnabled() {
            return autoBackupEnabled;
        }

        public void setAutoBackupEnabled(boolean autoBackupEnabled) {
            this.autoBackupEnabled = autoBackupEnabled;
        }

        public String getSchedule() {
            return schedule;
        }

        public void setSchedule(String schedule) {
            this.schedule = schedule;
        }

        public Duration getRetention() {
            return retention;
        }

        public void setRetention(Duration retention) {
            this.retention = retention;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public boolean isEncryptionEnabled() {
            return encryptionEnabled;
        }

        public void setEncryptionEnabled(boolean encryptionEnabled) {
            this.encryptionEnabled = encryptionEnabled;
        }

        public String getEncryptionAlgorithm() {
            return encryptionAlgorithm;
        }

        public void setEncryptionAlgorithm(String encryptionAlgorithm) {
            this.encryptionAlgorithm = encryptionAlgorithm;
        }

        public boolean isCompressionEnabled() {
            return compressionEnabled;
        }

        public void setCompressionEnabled(boolean compressionEnabled) {
            this.compressionEnabled = compressionEnabled;
        }

        public int getCompressionLevel() {
            return compressionLevel;
        }

        public void setCompressionLevel(int compressionLevel) {
            this.compressionLevel = compressionLevel;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBackend() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isEncryptionEnabled() {
        return encryptionEnabled;
    }

    public void setEncryptionEnabled(boolean encryptionEnabled) {
        this.encryptionEnabled = encryptionEnabled;
    }

    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    public boolean isAutoBackupEnabled() {
        return autoBackupEnabled;
    }

    public void setAutoBackupEnabled(boolean autoBackupEnabled) {
        this.autoBackupEnabled = autoBackupEnabled;
    }

    public String getBackupSchedule() {
        return backupSchedule;
    }

    public void setBackupSchedule(String backupSchedule) {
        this.backupSchedule = backupSchedule;
    }

    public Duration getBackupRetention() {
        return backupRetention;
    }

    public void setBackupRetention(Duration backupRetention) {
        this.backupRetention = backupRetention;
    }

    public String getBackupLocation() {
        return backupLocation;
    }

    public void setBackupLocation(String backupLocation) {
        this.backupLocation = backupLocation;
    }

    public boolean isAccessControlEnabled() {
        return accessControlEnabled;
    }

    public void setAccessControlEnabled(boolean accessControlEnabled) {
        this.accessControlEnabled = accessControlEnabled;
    }

    public int getMaxKeys() {
        return maxKeys;
    }

    public void setMaxKeys(int maxKeys) {
        this.maxKeys = maxKeys;
    }

    public Duration getExpirationWarningThreshold() {
        return expirationWarningThreshold;
    }

    public void setExpirationWarningThreshold(Duration expirationWarningThreshold) {
        this.expirationWarningThreshold = expirationWarningThreshold;
    }

    public Duration getOperationTimeout() {
        return operationTimeout;
    }

    public void setOperationTimeout(Duration operationTimeout) {
        this.operationTimeout = operationTimeout;
    }

    public Duration getHealthCheckInterval() {
        return healthCheckInterval;
    }

    public void setHealthCheckInterval(Duration healthCheckInterval) {
        this.healthCheckInterval = healthCheckInterval;
    }

    public Performance getPerformance() {
        return performance;
    }

    public void setPerformance(Performance performance) {
        this.performance = performance;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Backup getBackup() {
        return backup;
    }

    public void setBackup(Backup backup) {
        this.backup = backup;
    }
}
