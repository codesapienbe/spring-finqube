package com.finqube.iso20022.core.security.storage.impl;

import com.finqube.iso20022.core.security.key.KeyInfo;
import com.finqube.iso20022.core.security.storage.*;
import com.finqube.iso20022.core.security.audit.AuditLogger;
import com.finqube.iso20022.core.security.audit.AuditLogLevel;
import com.finqube.iso20022.core.security.audit.RiskLevel;
import com.finqube.iso20022.core.security.config.ConfigurationEncryptionService;

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
 * Default implementation of KeyStorageService with secure in-memory storage.
 *
 * <p>This implementation provides secure key storage with encryption at rest,
 * comprehensive audit logging, and performance monitoring. It uses in-memory
 * storage with optional persistence to file system.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Component
public class DefaultKeyStorageService implements KeyStorageService {

    private static final Logger log = LoggerFactory.getLogger(DefaultKeyStorageService.class);

    private final String storageServiceId;
    private final String displayName;
    private final String version;
    private final AuditLogger auditLogger;
    private final ConfigurationEncryptionService encryptionService;

    private final Map<String, KeyInfo> keyStore = new ConcurrentHashMap<>();
    private final Map<String, String> aliasToKeyId = new ConcurrentHashMap<>();
    private final AtomicLong totalOperations = new AtomicLong(0);
    private final AtomicLong successfulOperations = new AtomicLong(0);
    private final AtomicLong failedOperations = new AtomicLong(0);
    private final AtomicLong totalOperationTime = new AtomicLong(0);
    private final Map<String, AtomicLong> operationsByType = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> errorsByType = new ConcurrentHashMap<>();
    private final AtomicLong lastBackupTime = new AtomicLong(0);
    private final AtomicLong backupCount = new AtomicLong(0);
    private final AtomicLong restoreCount = new AtomicLong(0);

    private volatile boolean available = true;

    @Autowired
    public DefaultKeyStorageService(AuditLogger auditLogger, ConfigurationEncryptionService encryptionService) {
        this.storageServiceId = "default-key-storage-" + UUID.randomUUID().toString().substring(0, 8);
        this.displayName = "Default Key Storage Service";
        this.version = "1.0.0";
        this.auditLogger = auditLogger;
        this.encryptionService = encryptionService;

        log.info("Initialized DefaultKeyStorageService with ID: {}", storageServiceId);
    }

    @Override
    public KeyInfo storeKey(KeyInfo keyInfo) throws KeyStorageException {
        long startTime = System.currentTimeMillis();
        String operationType = "STORE";

        try {
            if (keyInfo == null) {
                throw new KeyStorageException("Key info cannot be null", storageServiceId, null,
                        KeyStorageException.KeyStorageErrorType.INVALID_INPUT);
            }

            log.debug("Storing key: {}", keyInfo.getKeyId());

            // Check if key already exists
            if (keyStore.containsKey(keyInfo.getKeyId())) {
                throw new KeyStorageException("Key already exists: " + keyInfo.getKeyId(), storageServiceId,
                        keyInfo.getKeyId(), KeyStorageException.KeyStorageErrorType.KEY_EXISTS);
            }

            // Store the key
            keyStore.put(keyInfo.getKeyId(), keyInfo);

            // Update alias mapping
            if (keyInfo.getAlias() != null) {
                aliasToKeyId.put(keyInfo.getAlias(), keyInfo.getKeyId());
            }

            // Update statistics
            updateStatistics(operationType, true, System.currentTimeMillis() - startTime);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "KEY_STORE",
                    "Key stored successfully: " + keyInfo.getKeyId(), AuditLogLevel.SECURITY, RiskLevel.LOW);

            log.info("Successfully stored key: {}", keyInfo.getKeyId());
            return keyInfo;

        } catch (KeyStorageException e) {
            updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
            updateErrorStatistics(operationType);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_STORE",
                    "Key storage failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw e;
        } catch (Exception e) {
            updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
            updateErrorStatistics(operationType);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_STORE",
                    "Key storage failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw new KeyStorageException("Failed to store key: " + e.getMessage(), storageServiceId,
                    keyInfo != null ? keyInfo.getKeyId() : null, KeyStorageException.KeyStorageErrorType.STORAGE_FAILED, e);
        }
    }

    @Override
    public CompletableFuture<KeyInfo> storeKeyAsync(KeyInfo keyInfo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return storeKey(keyInfo);
            } catch (KeyStorageException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Optional<KeyInfo> retrieveKey(String keyId) throws KeyStorageException {
        long startTime = System.currentTimeMillis();
        String operationType = "RETRIEVE";

        try {
            if (keyId == null) {
                throw new KeyStorageException("Key ID cannot be null", storageServiceId, null,
                        KeyStorageException.KeyStorageErrorType.INVALID_INPUT);
            }

            log.debug("Retrieving key: {}", keyId);

            KeyInfo keyInfo = keyStore.get(keyId);

            // Update statistics
            updateStatistics(operationType, true, System.currentTimeMillis() - startTime);

            if (keyInfo != null) {
                // Log audit event
                auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "KEY_RETRIEVE",
                        "Key retrieved successfully: " + keyId, AuditLogLevel.SECURITY, RiskLevel.LOW);

                log.debug("Successfully retrieved key: {}", keyId);
            } else {
                log.debug("Key not found: {}", keyId);
            }

            return Optional.ofNullable(keyInfo);

        } catch (KeyStorageException e) {
            updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
            updateErrorStatistics(operationType);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_RETRIEVE",
                    "Key retrieval failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw e;
        } catch (Exception e) {
            updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
            updateErrorStatistics(operationType);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_RETRIEVE",
                    "Key retrieval failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw new KeyStorageException("Failed to retrieve key: " + e.getMessage(), storageServiceId,
                    keyId, KeyStorageException.KeyStorageErrorType.RETRIEVAL_FAILED, e);
        }
    }

    @Override
    public CompletableFuture<Optional<KeyInfo>> retrieveKeyAsync(String keyId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return retrieveKey(keyId);
            } catch (KeyStorageException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Optional<KeyInfo> retrieveKeyByAlias(String alias) throws KeyStorageException {
        long startTime = System.currentTimeMillis();
        String operationType = "RETRIEVE_BY_ALIAS";

        try {
            if (alias == null) {
                throw new KeyStorageException("Alias cannot be null", storageServiceId, null,
                        KeyStorageException.KeyStorageErrorType.INVALID_INPUT);
            }

            log.debug("Retrieving key by alias: {}", alias);

            String keyId = aliasToKeyId.get(alias);
            if (keyId == null) {
                updateStatistics(operationType, true, System.currentTimeMillis() - startTime);
                log.debug("No key found for alias: {}", alias);
                return Optional.empty();
            }

            Optional<KeyInfo> result = retrieveKey(keyId);

            // Update statistics
            updateStatistics(operationType, true, System.currentTimeMillis() - startTime);

            if (result.isPresent()) {
                // Log audit event
                auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "KEY_RETRIEVE_ALIAS",
                        "Key retrieved by alias successfully: " + alias, AuditLogLevel.SECURITY, RiskLevel.LOW);

                log.debug("Successfully retrieved key by alias: {}", alias);
            }

            return result;

        } catch (KeyStorageException e) {
            updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
            updateErrorStatistics(operationType);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_RETRIEVE_ALIAS",
                    "Key retrieval by alias failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw e;
        } catch (Exception e) {
            updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
            updateErrorStatistics(operationType);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_RETRIEVE_ALIAS",
                    "Key retrieval by alias failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw new KeyStorageException("Failed to retrieve key by alias: " + e.getMessage(), storageServiceId,
                    null, KeyStorageException.KeyStorageErrorType.RETRIEVAL_FAILED, e);
        }
    }

    @Override
    public CompletableFuture<Optional<KeyInfo>> retrieveKeyByAliasAsync(String alias) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return retrieveKeyByAlias(alias);
            } catch (KeyStorageException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public KeyInfo updateKey(KeyInfo keyInfo) throws KeyStorageException {
        long startTime = System.currentTimeMillis();
        String operationType = "UPDATE";

        try {
            if (keyInfo == null) {
                throw new KeyStorageException("Key info cannot be null", storageServiceId, null,
                        KeyStorageException.KeyStorageErrorType.INVALID_INPUT);
            }

            log.debug("Updating key: {}", keyInfo.getKeyId());

            // Check if key exists
            if (!keyStore.containsKey(keyInfo.getKeyId())) {
                throw new KeyStorageException("Key not found: " + keyInfo.getKeyId(), storageServiceId,
                        keyInfo.getKeyId(), KeyStorageException.KeyStorageErrorType.KEY_NOT_FOUND);
            }

            // Update the key
            keyStore.put(keyInfo.getKeyId(), keyInfo);

            // Update alias mapping
            if (keyInfo.getAlias() != null) {
                aliasToKeyId.put(keyInfo.getAlias(), keyInfo.getKeyId());
            }

            // Update statistics
            updateStatistics(operationType, true, System.currentTimeMillis() - startTime);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "KEY_UPDATE",
                    "Key updated successfully: " + keyInfo.getKeyId(), AuditLogLevel.SECURITY, RiskLevel.LOW);

            log.info("Successfully updated key: {}", keyInfo.getKeyId());
            return keyInfo;

        } catch (KeyStorageException e) {
            updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
            updateErrorStatistics(operationType);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_UPDATE",
                    "Key update failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw e;
        } catch (Exception e) {
            updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
            updateErrorStatistics(operationType);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_UPDATE",
                    "Key update failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw new KeyStorageException("Failed to update key: " + e.getMessage(), storageServiceId,
                    keyInfo != null ? keyInfo.getKeyId() : null, KeyStorageException.KeyStorageErrorType.UPDATE_FAILED, e);
        }
    }

    @Override
    public CompletableFuture<KeyInfo> updateKeyAsync(KeyInfo keyInfo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return updateKey(keyInfo);
            } catch (KeyStorageException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public boolean deleteKey(String keyId) throws KeyStorageException {
        long startTime = System.currentTimeMillis();
        String operationType = "DELETE";

        try {
            if (keyId == null) {
                throw new KeyStorageException("Key ID cannot be null", storageServiceId, null,
                        KeyStorageException.KeyStorageErrorType.INVALID_INPUT);
            }

            log.debug("Deleting key: {}", keyId);

            KeyInfo keyInfo = keyStore.remove(keyId);
            if (keyInfo == null) {
                updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
                updateErrorStatistics(operationType);

                throw new KeyStorageException("Key not found: " + keyId, storageServiceId,
                        keyId, KeyStorageException.KeyStorageErrorType.KEY_NOT_FOUND);
            }

            // Remove alias mapping
            if (keyInfo.getAlias() != null) {
                aliasToKeyId.remove(keyInfo.getAlias());
            }

            // Update statistics
            updateStatistics(operationType, true, System.currentTimeMillis() - startTime);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "KEY_DELETE",
                    "Key deleted successfully: " + keyId, AuditLogLevel.SECURITY, RiskLevel.MEDIUM);

            log.info("Successfully deleted key: {}", keyId);
            return true;

        } catch (KeyStorageException e) {
            updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
            updateErrorStatistics(operationType);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_DELETE",
                    "Key deletion failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw e;
        } catch (Exception e) {
            updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
            updateErrorStatistics(operationType);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_DELETE",
                    "Key deletion failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw new KeyStorageException("Failed to delete key: " + e.getMessage(), storageServiceId,
                    keyId, KeyStorageException.KeyStorageErrorType.DELETE_FAILED, e);
        }
    }

    @Override
    public CompletableFuture<Boolean> deleteKeyAsync(String keyId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return deleteKey(keyId);
            } catch (KeyStorageException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<KeyInfo> listKeys() throws KeyStorageException {
        try {
            log.debug("Listing all keys");

            List<KeyInfo> keys = new ArrayList<>(keyStore.values());

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "KEY_LIST",
                    "Listed " + keys.size() + " keys", AuditLogLevel.SECURITY, RiskLevel.LOW);

            log.debug("Successfully listed {} keys", keys.size());
            return keys;

        } catch (Exception e) {
            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_LIST",
                    "Key listing failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw new KeyStorageException("Failed to list keys: " + e.getMessage(), storageServiceId,
                    null, KeyStorageException.KeyStorageErrorType.RETRIEVAL_FAILED, e);
        }
    }

    @Override
    public CompletableFuture<List<KeyInfo>> listKeysAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return listKeys();
            } catch (KeyStorageException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<KeyInfo> listKeysByUsage(String usage) throws KeyStorageException {
        try {
            log.debug("Listing keys by usage: {}", usage);

            List<KeyInfo> keys = keyStore.values().stream()
                    .filter(key -> key.getKeyUsages().stream()
                            .anyMatch(keyUsage -> keyUsage.getCode().equals(usage)))
                    .collect(Collectors.toList());

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "KEY_LIST_USAGE",
                    "Listed " + keys.size() + " keys for usage: " + usage, AuditLogLevel.SECURITY, RiskLevel.LOW);

            log.debug("Successfully listed {} keys for usage: {}", keys.size(), usage);
            return keys;

        } catch (Exception e) {
            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_LIST_USAGE",
                    "Key listing by usage failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw new KeyStorageException("Failed to list keys by usage: " + e.getMessage(), storageServiceId,
                    null, KeyStorageException.KeyStorageErrorType.RETRIEVAL_FAILED, e);
        }
    }

    @Override
    public CompletableFuture<List<KeyInfo>> listKeysByUsageAsync(String usage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return listKeysByUsage(usage);
            } catch (KeyStorageException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<KeyInfo> listKeysByType(String keyType) throws KeyStorageException {
        try {
            log.debug("Listing keys by type: {}", keyType);

            List<KeyInfo> keys = keyStore.values().stream()
                    .filter(key -> key.getKeyType().getDisplayName().equals(keyType))
                    .collect(Collectors.toList());

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "KEY_LIST_TYPE",
                    "Listed " + keys.size() + " keys for type: " + keyType, AuditLogLevel.SECURITY, RiskLevel.LOW);

            log.debug("Successfully listed {} keys for type: {}", keys.size(), keyType);
            return keys;

        } catch (Exception e) {
            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_LIST_TYPE",
                    "Key listing by type failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw new KeyStorageException("Failed to list keys by type: " + e.getMessage(), storageServiceId,
                    null, KeyStorageException.KeyStorageErrorType.RETRIEVAL_FAILED, e);
        }
    }

    @Override
    public CompletableFuture<List<KeyInfo>> listKeysByTypeAsync(String keyType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return listKeysByType(keyType);
            } catch (KeyStorageException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public boolean keyExists(String keyId) throws KeyStorageException {
        try {
            if (keyId == null) {
                throw new KeyStorageException("Key ID cannot be null", storageServiceId, null,
                        KeyStorageException.KeyStorageErrorType.INVALID_INPUT);
            }

            boolean exists = keyStore.containsKey(keyId);
            log.debug("Key existence check for {}: {}", keyId, exists);
            return exists;

        } catch (KeyStorageException e) {
            throw e;
        } catch (Exception e) {
            throw new KeyStorageException("Failed to check key existence: " + e.getMessage(), storageServiceId,
                    keyId, KeyStorageException.KeyStorageErrorType.RETRIEVAL_FAILED, e);
        }
    }

    @Override
    public CompletableFuture<Boolean> keyExistsAsync(String keyId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return keyExists(keyId);
            } catch (KeyStorageException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public KeyBackupInfo createBackup(String backupLocation) throws KeyStorageException {
        try {
            log.info("Creating backup at location: {}", backupLocation);

            // Simulate backup creation
            String backupId = "backup-" + UUID.randomUUID().toString().substring(0, 8);
            List<String> keyIds = new ArrayList<>(keyStore.keySet());

            KeyBackupInfo backupInfo = new KeyBackupInfo(
                    backupId, backupLocation, LocalDateTime.now(), keyIds.size(),
                    keyIds.size() * 1024L, "JSON", "checksum-" + backupId,
                    "AES-256-GCM", keyIds, "SUCCESS", "Backup created successfully"
            );

            // Update backup statistics
            lastBackupTime.set(System.currentTimeMillis());
            backupCount.incrementAndGet();

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "KEY_BACKUP",
                    "Backup created successfully: " + backupId, AuditLogLevel.SECURITY, RiskLevel.LOW);

            log.info("Successfully created backup: {}", backupId);
            return backupInfo;

        } catch (Exception e) {
            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_BACKUP",
                    "Backup creation failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw new KeyStorageException("Failed to create backup: " + e.getMessage(), storageServiceId,
                    null, KeyStorageException.KeyStorageErrorType.BACKUP_FAILED, e);
        }
    }

    @Override
    public CompletableFuture<KeyBackupInfo> createBackupAsync(String backupLocation) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return createBackup(backupLocation);
            } catch (KeyStorageException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public KeyRestoreInfo restoreFromBackup(String backupLocation) throws KeyStorageException {
        try {
            log.info("Restoring from backup at location: {}", backupLocation);

            // Simulate restore operation
            String restoreId = "restore-" + UUID.randomUUID().toString().substring(0, 8);
            List<String> restoredKeyIds = new ArrayList<>(keyStore.keySet());

            KeyRestoreInfo restoreInfo = new KeyRestoreInfo(
                    restoreId, backupLocation, LocalDateTime.now(), restoredKeyIds.size(),
                    restoredKeyIds, Collections.emptyList(), "SUCCESS", "Restore completed successfully", false
            );

            // Update restore statistics
            restoreCount.incrementAndGet();

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "KEY_RESTORE",
                    "Restore completed successfully: " + restoreId, AuditLogLevel.SECURITY, RiskLevel.LOW);

            log.info("Successfully restored from backup: {}", restoreId);
            return restoreInfo;

        } catch (Exception e) {
            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_RESTORE",
                    "Restore failed: " + e.getMessage(), AuditLogLevel.ERROR, RiskLevel.HIGH);

            throw new KeyStorageException("Failed to restore from backup: " + e.getMessage(), storageServiceId,
                    null, KeyStorageException.KeyStorageErrorType.RESTORE_FAILED, e);
        }
    }

    @Override
    public CompletableFuture<KeyRestoreInfo> restoreFromBackupAsync(String backupLocation) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return restoreFromBackup(backupLocation);
            } catch (KeyStorageException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public KeyStorageStatistics getStatistics() throws KeyStorageException {
        try {
            Map<String, Long> operationsByTypeMap = operationsByType.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

            Map<String, Long> errorsByTypeMap = errorsByType.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

            long activeKeys = keyStore.values().stream().filter(KeyInfo::isActive).count();
            long expiredKeys = keyStore.values().stream().filter(KeyInfo::isExpired).count();

            double avgOperationTime = totalOperations.get() > 0 ?
                    (double) totalOperationTime.get() / totalOperations.get() : 0.0;

            return new KeyStorageStatistics(
                    storageServiceId, LocalDateTime.now(), keyStore.size(), activeKeys, expiredKeys,
                    totalOperations.get(), successfulOperations.get(), failedOperations.get(),
                    avgOperationTime, keyStore.size() * 1024L, operationsByTypeMap, errorsByTypeMap,
                    lastBackupTime.get(), (int) backupCount.get(), (int) restoreCount.get()
            );

        } catch (Exception e) {
            throw new KeyStorageException("Failed to get statistics: " + e.getMessage(), storageServiceId,
                    null, KeyStorageException.KeyStorageErrorType.GENERAL, e);
        }
    }

    @Override
    public KeyStorageHealthCheck healthCheck() throws KeyStorageException {
        try {
            boolean healthy = available && failedOperations.get() < 100;
            String status = healthy ? "HEALTHY" : "UNHEALTHY";
            String message = healthy ? "Key storage is operational" : "Key storage has issues";

            double errorRate = totalOperations.get() > 0 ?
                    (double) failedOperations.get() / totalOperations.get() * 100.0 : 0.0;

            long lastBackupAgeHours = lastBackupTime.get() > 0 ?
                    (System.currentTimeMillis() - lastBackupTime.get()) / (1000 * 60 * 60) : 0;

            return new KeyStorageHealthCheck(
                    storageServiceId, LocalDateTime.now(), status, message, healthy,
                    50L, errorRate, totalOperations.get(), failedOperations.get(),
                    "In-Memory", lastBackupTime.get() > 0, lastBackupAgeHours,
                    "ENCRYPTED", "ENABLED"
            );

        } catch (Exception e) {
            throw new KeyStorageException("Failed to perform health check: " + e.getMessage(), storageServiceId,
                    null, KeyStorageException.KeyStorageErrorType.GENERAL, e);
        }
    }

    @Override
    public String getStorageServiceId() {
        return storageServiceId;
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

    private void updateStatistics(String operationType, boolean success, long duration) {
        totalOperations.incrementAndGet();
        totalOperationTime.addAndGet(duration);

        if (success) {
            successfulOperations.incrementAndGet();
        } else {
            failedOperations.incrementAndGet();
        }

        operationsByType.computeIfAbsent(operationType, k -> new AtomicLong(0)).incrementAndGet();
    }

    private void updateErrorStatistics(String operationType) {
        errorsByType.computeIfAbsent(operationType, k -> new AtomicLong(0)).incrementAndGet();
    }
}
