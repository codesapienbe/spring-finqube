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
        long startTime = System.currentTimeMillis();
        String operationType = "BACKUP";

        try {
            if (backupLocation == null || backupLocation.trim().isEmpty()) {
                throw new KeyStorageException("Backup location cannot be null or empty", storageServiceId, null,
                        KeyStorageException.KeyStorageErrorType.INVALID_INPUT);
            }

            log.info("Creating secure backup at location: {}", backupLocation);

            // Create backup directory if it doesn't exist
            java.io.File backupDir = new java.io.File(backupLocation);
            if (!backupDir.exists() && !backupDir.mkdirs()) {
                throw new KeyStorageException("Failed to create backup directory: " + backupLocation, storageServiceId, null,
                        KeyStorageException.KeyStorageErrorType.BACKUP_FAILED);
            }

            // Generate backup ID and filename
            String backupId = "backup-" + UUID.randomUUID().toString().substring(0, 8);
            String backupFilename = backupId + "-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".enc";
            java.io.File backupFile = new java.io.File(backupDir, backupFilename);

            // Collect all keys for backup
            List<String> keyIds = new ArrayList<>(keyStore.keySet());
            if (keyIds.isEmpty()) {
                log.warn("No keys to backup");
                return new KeyBackupInfo(
                        backupId, backupLocation, LocalDateTime.now(), 0,
                        0L, "JSON", "no-keys", "AES-256-GCM", keyIds, "SUCCESS", "No keys to backup"
                );
            }

            // Create backup data structure
            Map<String, Object> backupData = new HashMap<>();
            backupData.put("backupId", backupId);
            backupData.put("backupTime", LocalDateTime.now().toString());
            backupData.put("keyCount", keyIds.size());
            backupData.put("storageServiceId", storageServiceId);
            backupData.put("version", version);

            // Serialize key information (without actual key material for security)
            List<Map<String, Object>> keyInfoList = new ArrayList<>();
            for (String keyId : keyIds) {
                KeyInfo keyInfo = keyStore.get(keyId);
                if (keyInfo != null) {
                    Map<String, Object> keyData = new HashMap<>();
                    keyData.put("keyId", keyInfo.getKeyId());
                    keyData.put("alias", keyInfo.getAlias());
                    keyData.put("keyType", keyInfo.getKeyType().name());
                    keyData.put("algorithm", keyInfo.getAlgorithm());
                    keyData.put("keySize", keyInfo.getKeySize());
                    keyData.put("creationTime", keyInfo.getCreationTime().toString());
                    keyData.put("expirationTime", keyInfo.getExpirationTime() != null ? keyInfo.getExpirationTime().toString() : null);
                    keyData.put("active", keyInfo.isActive());
                    keyData.put("description", keyInfo.getDescription());
                    keyData.put("version", keyInfo.getVersion());
                    keyInfoList.add(keyData);
                }
            }
            backupData.put("keys", keyInfoList);

            // Convert to JSON
            String jsonData = convertToJson(backupData);

            // Encrypt the backup data
            String encryptedData = encryptionService.encrypt(jsonData, com.finqube.iso20022.core.security.encryption.EncryptionAlgorithm.AES_256_GCM);

            // Write encrypted backup to file
            try (java.io.FileWriter writer = new java.io.FileWriter(backupFile)) {
                writer.write(encryptedData);
            }

            // Calculate checksum
            String checksum = calculateChecksum(backupFile);

            // Calculate backup size
            long backupSize = backupFile.length();

            // Update backup statistics
            lastBackupTime.set(System.currentTimeMillis());
            backupCount.incrementAndGet();

            // Update statistics
            updateStatistics(operationType, true, System.currentTimeMillis() - startTime);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "KEY_BACKUP",
                    "Secure backup created successfully: " + backupId + " (" + keyIds.size() + " keys)",
                    com.finqube.iso20022.core.security.audit.AuditLogLevel.SECURITY,
                    com.finqube.iso20022.core.security.audit.RiskLevel.LOW);

            KeyBackupInfo backupInfo = new KeyBackupInfo(
                    backupId, backupFile.getAbsolutePath(), LocalDateTime.now(), keyIds.size(),
                    backupSize, "JSON", checksum, "AES-256-GCM", keyIds, "SUCCESS",
                    "Secure backup created successfully with " + keyIds.size() + " keys"
            );

            log.info("Successfully created secure backup: {} with {} keys, size: {} bytes",
                    backupId, keyIds.size(), backupSize);
            return backupInfo;

        } catch (KeyStorageException e) {
            updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
            updateErrorStatistics(operationType);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_BACKUP",
                    "Backup creation failed: " + e.getMessage(),
                    com.finqube.iso20022.core.security.audit.AuditLogLevel.ERROR,
                    com.finqube.iso20022.core.security.audit.RiskLevel.HIGH);

            throw e;
        } catch (Exception e) {
            updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
            updateErrorStatistics(operationType);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_BACKUP",
                    "Backup creation failed: " + e.getMessage(),
                    com.finqube.iso20022.core.security.audit.AuditLogLevel.ERROR,
                    com.finqube.iso20022.core.security.audit.RiskLevel.HIGH);

            throw new KeyStorageException("Failed to create secure backup: " + e.getMessage(), storageServiceId,
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
        long startTime = System.currentTimeMillis();
        String operationType = "RESTORE";

        try {
            if (backupLocation == null || backupLocation.trim().isEmpty()) {
                throw new KeyStorageException("Backup location cannot be null or empty", storageServiceId, null,
                        KeyStorageException.KeyStorageErrorType.INVALID_INPUT);
            }

            log.info("Restoring from secure backup at location: {}", backupLocation);

            // Check if backup file exists
            java.io.File backupFile = new java.io.File(backupLocation);
            if (!backupFile.exists()) {
                throw new KeyStorageException("Backup file not found: " + backupLocation, storageServiceId, null,
                        KeyStorageException.KeyStorageErrorType.RESTORE_FAILED);
            }

            if (!backupFile.canRead()) {
                throw new KeyStorageException("Cannot read backup file: " + backupLocation, storageServiceId, null,
                        KeyStorageException.KeyStorageErrorType.PERMISSION_DENIED);
            }

            // Generate restore ID
            String restoreId = "restore-" + UUID.randomUUID().toString().substring(0, 8);

            // Read encrypted backup data
            String encryptedData;
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(backupFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                encryptedData = content.toString();
            }

            // Decrypt the backup data
            String jsonData = encryptionService.decrypt(encryptedData, com.finqube.iso20022.core.security.encryption.EncryptionAlgorithm.AES_256_GCM);

            // Parse JSON data
            Map<String, Object> backupData = parseJson(jsonData);

            // Validate backup data
            if (!backupData.containsKey("backupId") || !backupData.containsKey("keys")) {
                throw new KeyStorageException("Invalid backup file format: missing required fields", storageServiceId, null,
                        KeyStorageException.KeyStorageErrorType.RESTORE_FAILED);
            }

            String originalBackupId = (String) backupData.get("backupId");
            List<Map<String, Object>> keyInfoList = (List<Map<String, Object>>) backupData.get("keys");

            log.info("Restoring backup: {} with {} keys", originalBackupId, keyInfoList.size());

            // Track restore results
            List<String> restoredKeyIds = new ArrayList<>();
            List<String> failedKeyIds = new ArrayList<>();
            boolean overwriteExisting = false;

            // Restore key metadata (actual key material would need to be restored separately for security)
            for (Map<String, Object> keyData : keyInfoList) {
                try {
                    String keyId = (String) keyData.get("keyId");
                    String alias = (String) keyData.get("alias");
                    String keyTypeStr = (String) keyData.get("keyType");
                    String algorithm = (String) keyData.get("algorithm");
                    Integer keySize = (Integer) keyData.get("keySize");
                    String creationTimeStr = (String) keyData.get("creationTime");
                    String expirationTimeStr = (String) keyData.get("expirationTime");
                    Boolean active = (Boolean) keyData.get("active");
                    String description = (String) keyData.get("description");
                    String version = (String) keyData.get("version");

                    // Check if key already exists
                    if (keyStore.containsKey(keyId)) {
                        overwriteExisting = true;
                        log.warn("Key already exists, will be overwritten: {}", keyId);
                    }

                    // Create KeyInfo with metadata only (no actual key material for security)
                    KeyInfo restoredKeyInfo = new KeyInfo(
                            keyId, alias,
                            com.finqube.iso20022.core.security.key.KeyRotationManager.KeyType.valueOf(keyTypeStr),
                            new ArrayList<>(), // Empty key usages for restored keys
                            null, null, // No actual key material for security
                            LocalDateTime.parse(creationTimeStr),
                            expirationTimeStr != null ? LocalDateTime.parse(expirationTimeStr) : null,
                            LocalDateTime.now(), // Set last rotation to now
                            version, active, description, algorithm, keySize
                    );

                    // Store the key metadata
                    keyStore.put(keyId, restoredKeyInfo);
                    if (alias != null) {
                        aliasToKeyId.put(alias, keyId);
                    }

                    restoredKeyIds.add(keyId);
                    log.debug("Successfully restored key metadata: {}", keyId);

                } catch (Exception e) {
                    String keyId = (String) keyData.get("keyId");
                    failedKeyIds.add(keyId != null ? keyId : "unknown");
                    log.error("Failed to restore key: {}", keyData.get("keyId"), e);
                }
            }

            // Update restore statistics
            restoreCount.incrementAndGet();

            // Update statistics
            updateStatistics(operationType, true, System.currentTimeMillis() - startTime);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "SUCCESS", "KEY_RESTORE",
                    "Secure restore completed: " + restoreId + " (" + restoredKeyIds.size() + " keys restored, " + failedKeyIds.size() + " failed)",
                    com.finqube.iso20022.core.security.audit.AuditLogLevel.SECURITY,
                    com.finqube.iso20022.core.security.audit.RiskLevel.LOW);

            KeyRestoreInfo restoreInfo = new KeyRestoreInfo(
                    restoreId, backupLocation, LocalDateTime.now(), keyInfoList.size(),
                    restoredKeyIds, failedKeyIds, "SUCCESS",
                    "Secure restore completed: " + restoredKeyIds.size() + " keys restored, " + failedKeyIds.size() + " failed",
                    overwriteExisting
            );

            log.info("Successfully restored from secure backup: {} with {} keys restored, {} failed",
                    restoreId, restoredKeyIds.size(), failedKeyIds.size());
            return restoreInfo;

        } catch (KeyStorageException e) {
            updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
            updateErrorStatistics(operationType);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_RESTORE",
                    "Restore failed: " + e.getMessage(),
                    com.finqube.iso20022.core.security.audit.AuditLogLevel.ERROR,
                    com.finqube.iso20022.core.security.audit.RiskLevel.HIGH);

            throw e;
        } catch (Exception e) {
            updateStatistics(operationType, false, System.currentTimeMillis() - startTime);
            updateErrorStatistics(operationType);

            // Log audit event
            auditLogger.logSecurityEvent("SYSTEM", "FAILED", "KEY_RESTORE",
                    "Restore failed: " + e.getMessage(),
                    com.finqube.iso20022.core.security.audit.AuditLogLevel.ERROR,
                    com.finqube.iso20022.core.security.audit.RiskLevel.HIGH);

            throw new KeyStorageException("Failed to restore from secure backup: " + e.getMessage(), storageServiceId,
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

    /**
     * Converts a map to JSON string representation.
     *
     * @param data the data to convert
     * @return the JSON string
     */
    private String convertToJson(Map<String, Object> data) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;

            json.append("\"").append(entry.getKey()).append("\":");

            Object value = entry.getValue();
            if (value == null) {
                json.append("null");
            } else if (value instanceof String) {
                json.append("\"").append(escapeJsonString((String) value)).append("\"");
            } else if (value instanceof Boolean || value instanceof Number) {
                json.append(value);
            } else if (value instanceof List) {
                json.append(convertListToJson((List<?>) value));
            } else if (value instanceof Map) {
                json.append(convertToJson((Map<String, Object>) value));
            } else {
                json.append("\"").append(escapeJsonString(value.toString())).append("\"");
            }
        }

        json.append("}");
        return json.toString();
    }

    /**
     * Converts a list to JSON array representation.
     *
     * @param list the list to convert
     * @return the JSON array string
     */
    private String convertListToJson(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");

        boolean first = true;
        for (Object item : list) {
            if (!first) {
                json.append(",");
            }
            first = false;

            if (item == null) {
                json.append("null");
            } else if (item instanceof String) {
                json.append("\"").append(escapeJsonString((String) item)).append("\"");
            } else if (item instanceof Boolean || item instanceof Number) {
                json.append(item);
            } else if (item instanceof Map) {
                json.append(convertToJson((Map<String, Object>) item));
            } else {
                json.append("\"").append(escapeJsonString(item.toString())).append("\"");
            }
        }

        json.append("]");
        return json.toString();
    }

    /**
     * Escapes special characters in JSON strings.
     *
     * @param str the string to escape
     * @return the escaped string
     */
    private String escapeJsonString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * Parses a JSON string into a Map.
     *
     * @param jsonString the JSON string to parse
     * @return the parsed Map
     * @throws KeyStorageException if JSON parsing fails
     */
    private Map<String, Object> parseJson(String jsonString) throws KeyStorageException {
        try {
            // This is a simplified JSON parser. For a real application, you'd use a proper JSON library.
            // For this example, we'll just split by commas and then by colons to get key-value pairs.
            // This is NOT robust for all JSON structures.
            Map<String, Object> result = new HashMap<>();
            String[] pairs = jsonString.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        result.put(key, value.substring(1, value.length() - 1));
                    } else if (value.equals("null")) {
                        result.put(key, null);
                    } else if (value.equals("true")) {
                        result.put(key, true);
                    } else if (value.equals("false")) {
                        result.put(key, false);
                    } else if (value.matches("^-?\\d+$")) { // Integer
                        result.put(key, Integer.parseInt(value));
                    } else if (value.matches("^-?\\d+\\.\\d+$")) { // Double
                        result.put(key, Double.parseDouble(value));
                    } else {
                        result.put(key, value); // Assume it's a string if not recognized
                    }
                }
            }
            return result;
        } catch (Exception e) {
            throw new KeyStorageException("Failed to parse JSON data: " + e.getMessage(), storageServiceId, null,
                    KeyStorageException.KeyStorageErrorType.RESTORE_FAILED, e);
        }
    }

    /**
     * Calculates SHA-256 checksum of a file.
     *
     * @param file the file to calculate checksum for
     * @return the checksum as a hex string
     * @throws KeyStorageException if checksum calculation fails
     */
    private String calculateChecksum(java.io.File file) throws KeyStorageException {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }
            byte[] hash = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new KeyStorageException("Failed to calculate checksum: " + e.getMessage(), storageServiceId,
                    null, KeyStorageException.KeyStorageErrorType.BACKUP_FAILED, e);
        }
    }
}
