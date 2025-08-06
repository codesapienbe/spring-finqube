package com.finqube.iso20022.core.security.key;

import java.time.Duration;
import java.util.List;

/**
 * Options for configuring key rotation operations.
 *
 * <p>This class provides comprehensive configuration options for key rotation
 * operations, including timing, validation, and rollback settings.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class KeyRotationOptions {

    private final boolean enableRollback;
    private final Duration rollbackTimeout;
    private final boolean validateNewKey;
    private final boolean backupOldKey;
    private final boolean notifyOnCompletion;
    private final List<String> notificationRecipients;
    private final Duration operationTimeout;
    private final boolean forceRotation;
    private final String rotationReason;
    private final boolean enableAuditLogging;

    /**
     * Constructs a new KeyRotationOptions with the specified parameters.
     *
     * @param enableRollback whether to enable rollback capability
     * @param rollbackTimeout the rollback timeout
     * @param validateNewKey whether to validate the new key
     * @param backupOldKey whether to backup the old key
     * @param notifyOnCompletion whether to send notifications on completion
     * @param notificationRecipients list of notification recipients
     * @param operationTimeout the operation timeout
     * @param forceRotation whether to force rotation even if validation fails
     * @param rotationReason the reason for rotation
     * @param enableAuditLogging whether to enable audit logging
     */
    public KeyRotationOptions(boolean enableRollback, Duration rollbackTimeout, boolean validateNewKey,
                            boolean backupOldKey, boolean notifyOnCompletion, List<String> notificationRecipients,
                            Duration operationTimeout, boolean forceRotation, String rotationReason,
                            boolean enableAuditLogging) {
        this.enableRollback = enableRollback;
        this.rollbackTimeout = rollbackTimeout;
        this.validateNewKey = validateNewKey;
        this.backupOldKey = backupOldKey;
        this.notifyOnCompletion = notifyOnCompletion;
        this.notificationRecipients = notificationRecipients;
        this.operationTimeout = operationTimeout;
        this.forceRotation = forceRotation;
        this.rotationReason = rotationReason;
        this.enableAuditLogging = enableAuditLogging;
    }

    /**
     * Checks if rollback is enabled.
     *
     * @return true if rollback is enabled, false otherwise
     */
    public boolean isEnableRollback() {
        return enableRollback;
    }

    /**
     * Gets the rollback timeout.
     *
     * @return the rollback timeout
     */
    public Duration getRollbackTimeout() {
        return rollbackTimeout;
    }

    /**
     * Checks if new key validation is enabled.
     *
     * @return true if validation is enabled, false otherwise
     */
    public boolean isValidateNewKey() {
        return validateNewKey;
    }

    /**
     * Checks if old key backup is enabled.
     *
     * @return true if backup is enabled, false otherwise
     */
    public boolean isBackupOldKey() {
        return backupOldKey;
    }

    /**
     * Checks if completion notifications are enabled.
     *
     * @return true if notifications are enabled, false otherwise
     */
    public boolean isNotifyOnCompletion() {
        return notifyOnCompletion;
    }

    /**
     * Gets the notification recipients.
     *
     * @return the notification recipients
     */
    public List<String> getNotificationRecipients() {
        return notificationRecipients;
    }

    /**
     * Gets the operation timeout.
     *
     * @return the operation timeout
     */
    public Duration getOperationTimeout() {
        return operationTimeout;
    }

    /**
     * Checks if force rotation is enabled.
     *
     * @return true if force rotation is enabled, false otherwise
     */
    public boolean isForceRotation() {
        return forceRotation;
    }

    /**
     * Gets the rotation reason.
     *
     * @return the rotation reason
     */
    public String getRotationReason() {
        return rotationReason;
    }

    /**
     * Checks if audit logging is enabled.
     *
     * @return true if audit logging is enabled, false otherwise
     */
    public boolean isEnableAuditLogging() {
        return enableAuditLogging;
    }

    /**
     * Creates a default KeyRotationOptions instance.
     *
     * @return a default KeyRotationOptions instance
     */
    public static KeyRotationOptions defaults() {
        return new KeyRotationOptions(
            true,                           // enableRollback
            Duration.ofMinutes(5),          // rollbackTimeout
            true,                           // validateNewKey
            true,                           // backupOldKey
            false,                          // notifyOnCompletion
            List.of(),                      // notificationRecipients
            Duration.ofMinutes(10),         // operationTimeout
            false,                          // forceRotation
            "Scheduled rotation",           // rotationReason
            true                            // enableAuditLogging
        );
    }

    /**
     * Creates a KeyRotationOptions instance for emergency rotation.
     *
     * @return a KeyRotationOptions instance for emergency rotation
     */
    public static KeyRotationOptions emergency() {
        return new KeyRotationOptions(
            true,                           // enableRollback
            Duration.ofMinutes(2),          // rollbackTimeout
            false,                          // validateNewKey
            true,                           // backupOldKey
            true,                           // notifyOnCompletion
            List.of("admin@example.com"),   // notificationRecipients
            Duration.ofMinutes(5),          // operationTimeout
            true,                           // forceRotation
            "Emergency rotation",           // rotationReason
            true                            // enableAuditLogging
        );
    }

    /**
     * Creates a KeyRotationOptions instance for scheduled rotation.
     *
     * @return a KeyRotationOptions instance for scheduled rotation
     */
    public static KeyRotationOptions scheduled() {
        return new KeyRotationOptions(
            true,                           // enableRollback
            Duration.ofMinutes(10),         // rollbackTimeout
            true,                           // validateNewKey
            true,                           // backupOldKey
            false,                          // notifyOnCompletion
            List.of(),                      // notificationRecipients
            Duration.ofMinutes(15),         // operationTimeout
            false,                          // forceRotation
            "Scheduled rotation",           // rotationReason
            true                            // enableAuditLogging
        );
    }

    /**
     * Builder for KeyRotationOptions objects.
     */
    public static class Builder {
        private boolean enableRollback = true;
        private Duration rollbackTimeout = Duration.ofMinutes(5);
        private boolean validateNewKey = true;
        private boolean backupOldKey = true;
        private boolean notifyOnCompletion = false;
        private List<String> notificationRecipients = List.of();
        private Duration operationTimeout = Duration.ofMinutes(10);
        private boolean forceRotation = false;
        private String rotationReason = "Manual rotation";
        private boolean enableAuditLogging = true;

        public Builder enableRollback(boolean enableRollback) {
            this.enableRollback = enableRollback;
            return this;
        }

        public Builder rollbackTimeout(Duration rollbackTimeout) {
            this.rollbackTimeout = rollbackTimeout;
            return this;
        }

        public Builder validateNewKey(boolean validateNewKey) {
            this.validateNewKey = validateNewKey;
            return this;
        }

        public Builder backupOldKey(boolean backupOldKey) {
            this.backupOldKey = backupOldKey;
            return this;
        }

        public Builder notifyOnCompletion(boolean notifyOnCompletion) {
            this.notifyOnCompletion = notifyOnCompletion;
            return this;
        }

        public Builder notificationRecipients(List<String> notificationRecipients) {
            this.notificationRecipients = notificationRecipients;
            return this;
        }

        public Builder operationTimeout(Duration operationTimeout) {
            this.operationTimeout = operationTimeout;
            return this;
        }

        public Builder forceRotation(boolean forceRotation) {
            this.forceRotation = forceRotation;
            return this;
        }

        public Builder rotationReason(String rotationReason) {
            this.rotationReason = rotationReason;
            return this;
        }

        public Builder enableAuditLogging(boolean enableAuditLogging) {
            this.enableAuditLogging = enableAuditLogging;
            return this;
        }

        public KeyRotationOptions build() {
            return new KeyRotationOptions(enableRollback, rollbackTimeout, validateNewKey, backupOldKey,
                    notifyOnCompletion, notificationRecipients, operationTimeout, forceRotation,
                    rotationReason, enableAuditLogging);
        }
    }
}
