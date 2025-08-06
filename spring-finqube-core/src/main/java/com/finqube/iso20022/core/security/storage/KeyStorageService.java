package com.finqube.iso20022.core.security.storage;

import com.finqube.iso20022.core.security.key.KeyInfo;

import java.security.Key;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service for secure key storage and management.
 *
 * <p>This interface provides secure storage capabilities for cryptographic keys
 * with support for multiple storage backends including file systems, databases,
 * cloud storage, and hardware security modules (HSM).</p>
 *
 * <p>The service supports:</p>
 * <ul>
 *   <li>Secure key storage with encryption at rest</li>
 *   <li>Key versioning and lifecycle management</li>
 *   <li>Access control and audit logging</li>
 *   <li>Backup and recovery operations</li>
 *   <li>Multiple storage backend support</li>
 *   <li>Asynchronous operations for performance</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface KeyStorageService {

    /**
     * Stores a key securely.
     *
     * @param keyInfo the key information to store
     * @return the stored key information
     * @throws KeyStorageException if storage fails
     */
    KeyInfo storeKey(KeyInfo keyInfo) throws KeyStorageException;

    /**
     * Stores a key securely asynchronously.
     *
     * @param keyInfo the key information to store
     * @return a CompletableFuture that will complete with the stored key information
     */
    CompletableFuture<KeyInfo> storeKeyAsync(KeyInfo keyInfo);

    /**
     * Retrieves a key by its identifier.
     *
     * @param keyId the key identifier
     * @return the key information, or empty if not found
     * @throws KeyStorageException if retrieval fails
     */
    Optional<KeyInfo> retrieveKey(String keyId) throws KeyStorageException;

    /**
     * Retrieves a key by its identifier asynchronously.
     *
     * @param keyId the key identifier
     * @return a CompletableFuture that will complete with the key information
     */
    CompletableFuture<Optional<KeyInfo>> retrieveKeyAsync(String keyId);

    /**
     * Retrieves a key by its alias.
     *
     * @param alias the key alias
     * @return the key information, or empty if not found
     * @throws KeyStorageException if retrieval fails
     */
    Optional<KeyInfo> retrieveKeyByAlias(String alias) throws KeyStorageException;

    /**
     * Retrieves a key by its alias asynchronously.
     *
     * @param alias the key alias
     * @return a CompletableFuture that will complete with the key information
     */
    CompletableFuture<Optional<KeyInfo>> retrieveKeyByAliasAsync(String alias);

    /**
     * Updates an existing key.
     *
     * @param keyInfo the updated key information
     * @return the updated key information
     * @throws KeyStorageException if update fails
     */
    KeyInfo updateKey(KeyInfo keyInfo) throws KeyStorageException;

    /**
     * Updates an existing key asynchronously.
     *
     * @param keyInfo the updated key information
     * @return a CompletableFuture that will complete with the updated key information
     */
    CompletableFuture<KeyInfo> updateKeyAsync(KeyInfo keyInfo);

    /**
     * Deletes a key.
     *
     * @param keyId the key identifier to delete
     * @return true if deletion was successful, false otherwise
     * @throws KeyStorageException if deletion fails
     */
    boolean deleteKey(String keyId) throws KeyStorageException;

    /**
     * Deletes a key asynchronously.
     *
     * @param keyId the key identifier to delete
     * @return a CompletableFuture that will complete with the deletion result
     */
    CompletableFuture<Boolean> deleteKeyAsync(String keyId);

    /**
     * Lists all keys in storage.
     *
     * @return list of all key information
     * @throws KeyStorageException if listing fails
     */
    List<KeyInfo> listKeys() throws KeyStorageException;

    /**
     * Lists all keys in storage asynchronously.
     *
     * @return a CompletableFuture that will complete with the list of key information
     */
    CompletableFuture<List<KeyInfo>> listKeysAsync();

    /**
     * Lists keys by usage.
     *
     * @param usage the key usage to filter by
     * @return list of key information for the specified usage
     * @throws KeyStorageException if listing fails
     */
    List<KeyInfo> listKeysByUsage(String usage) throws KeyStorageException;

    /**
     * Lists keys by usage asynchronously.
     *
     * @param usage the key usage to filter by
     * @return a CompletableFuture that will complete with the list of key information
     */
    CompletableFuture<List<KeyInfo>> listKeysByUsageAsync(String usage);

    /**
     * Lists keys by type.
     *
     * @param keyType the key type to filter by
     * @return list of key information for the specified type
     * @throws KeyStorageException if listing fails
     */
    List<KeyInfo> listKeysByType(String keyType) throws KeyStorageException;

    /**
     * Lists keys by type asynchronously.
     *
     * @param keyType the key type to filter by
     * @return a CompletableFuture that will complete with the list of key information
     */
    CompletableFuture<List<KeyInfo>> listKeysByTypeAsync(String keyType);

    /**
     * Checks if a key exists.
     *
     * @param keyId the key identifier to check
     * @return true if the key exists, false otherwise
     * @throws KeyStorageException if check fails
     */
    boolean keyExists(String keyId) throws KeyStorageException;

    /**
     * Checks if a key exists asynchronously.
     *
     * @param keyId the key identifier to check
     * @return a CompletableFuture that will complete with the existence check result
     */
    CompletableFuture<Boolean> keyExistsAsync(String keyId);

    /**
     * Creates a backup of all keys.
     *
     * @param backupLocation the location to store the backup
     * @return the backup information
     * @throws KeyStorageException if backup fails
     */
    KeyBackupInfo createBackup(String backupLocation) throws KeyStorageException;

    /**
     * Creates a backup of all keys asynchronously.
     *
     * @param backupLocation the location to store the backup
     * @return a CompletableFuture that will complete with the backup information
     */
    CompletableFuture<KeyBackupInfo> createBackupAsync(String backupLocation);

    /**
     * Restores keys from a backup.
     *
     * @param backupLocation the location of the backup to restore from
     * @return the restore information
     * @throws KeyStorageException if restore fails
     */
    KeyRestoreInfo restoreFromBackup(String backupLocation) throws KeyStorageException;

    /**
     * Restores keys from a backup asynchronously.
     *
     * @param backupLocation the location of the backup to restore from
     * @return a CompletableFuture that will complete with the restore information
     */
    CompletableFuture<KeyRestoreInfo> restoreFromBackupAsync(String backupLocation);

    /**
     * Gets storage statistics.
     *
     * @return the storage statistics
     * @throws KeyStorageException if statistics retrieval fails
     */
    KeyStorageStatistics getStatistics() throws KeyStorageException;

    /**
     * Performs a health check on the storage service.
     *
     * @return the health check result
     * @throws KeyStorageException if health check fails
     */
    KeyStorageHealthCheck healthCheck() throws KeyStorageException;

    /**
     * Gets the storage service identifier.
     *
     * @return the service identifier
     */
    String getStorageServiceId();

    /**
     * Gets the storage service display name.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Gets the storage service version.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Checks if the storage service is available.
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();
}
