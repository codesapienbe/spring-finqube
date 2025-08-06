package com.finqube.iso20022.core.security.storage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Information about a key backup operation.
 *
 * <p>This class provides comprehensive information about a key backup operation
 * including backup location, timestamp, key count, and backup metadata.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class KeyBackupInfo {

    private final String backupId;
    private final String backupLocation;
    private final LocalDateTime backupTime;
    private final int keyCount;
    private final long backupSizeBytes;
    private final String backupFormat;
    private final String checksum;
    private final String encryptionAlgorithm;
    private final List<String> backedUpKeyIds;
    private final String status;
    private final String message;

    /**
     * Constructs a new KeyBackupInfo with the specified parameters.
     *
     * @param backupId the backup identifier
     * @param backupLocation the backup location
     * @param backupTime the backup timestamp
     * @param keyCount the number of keys backed up
     * @param backupSizeBytes the backup size in bytes
     * @param backupFormat the backup format
     * @param checksum the backup checksum
     * @param encryptionAlgorithm the encryption algorithm used
     * @param backedUpKeyIds the list of backed up key identifiers
     * @param status the backup status
     * @param message the backup message
     */
    public KeyBackupInfo(String backupId, String backupLocation, LocalDateTime backupTime,
                        int keyCount, long backupSizeBytes, String backupFormat, String checksum,
                        String encryptionAlgorithm, List<String> backedUpKeyIds, String status, String message) {
        this.backupId = Objects.requireNonNull(backupId, "Backup ID cannot be null");
        this.backupLocation = Objects.requireNonNull(backupLocation, "Backup location cannot be null");
        this.backupTime = Objects.requireNonNull(backupTime, "Backup time cannot be null");
        this.keyCount = keyCount;
        this.backupSizeBytes = backupSizeBytes;
        this.backupFormat = backupFormat;
        this.checksum = checksum;
        this.encryptionAlgorithm = encryptionAlgorithm;
        this.backedUpKeyIds = backedUpKeyIds;
        this.status = status;
        this.message = message;
    }

    /**
     * Gets the backup identifier.
     *
     * @return the backup identifier
     */
    public String getBackupId() {
        return backupId;
    }

    /**
     * Gets the backup location.
     *
     * @return the backup location
     */
    public String getBackupLocation() {
        return backupLocation;
    }

    /**
     * Gets the backup timestamp.
     *
     * @return the backup timestamp
     */
    public LocalDateTime getBackupTime() {
        return backupTime;
    }

    /**
     * Gets the number of keys backed up.
     *
     * @return the key count
     */
    public int getKeyCount() {
        return keyCount;
    }

    /**
     * Gets the backup size in bytes.
     *
     * @return the backup size in bytes
     */
    public long getBackupSizeBytes() {
        return backupSizeBytes;
    }

    /**
     * Gets the backup format.
     *
     * @return the backup format
     */
    public String getBackupFormat() {
        return backupFormat;
    }

    /**
     * Gets the backup checksum.
     *
     * @return the backup checksum
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * Gets the encryption algorithm used.
     *
     * @return the encryption algorithm
     */
    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    /**
     * Gets the list of backed up key identifiers.
     *
     * @return the list of key identifiers
     */
    public List<String> getBackedUpKeyIds() {
        return backedUpKeyIds;
    }

    /**
     * Gets the backup status.
     *
     * @return the backup status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the backup message.
     *
     * @return the backup message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Checks if the backup was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return "SUCCESS".equals(status);
    }

    /**
     * Gets the backup size in a human-readable format.
     *
     * @return the formatted backup size
     */
    public String getFormattedBackupSize() {
        if (backupSizeBytes < 1024) {
            return backupSizeBytes + " B";
        } else if (backupSizeBytes < 1024 * 1024) {
            return String.format("%.1f KB", backupSizeBytes / 1024.0);
        } else if (backupSizeBytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", backupSizeBytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", backupSizeBytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        KeyBackupInfo that = (KeyBackupInfo) obj;
        return Objects.equals(backupId, that.backupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(backupId);
    }

    @Override
    public String toString() {
        return String.format("KeyBackupInfo{backupId='%s', location='%s', time=%s, keyCount=%d, " +
                "size=%s, format='%s', status='%s'}", backupId, backupLocation, backupTime,
                keyCount, getFormattedBackupSize(), backupFormat, status);
    }
}
