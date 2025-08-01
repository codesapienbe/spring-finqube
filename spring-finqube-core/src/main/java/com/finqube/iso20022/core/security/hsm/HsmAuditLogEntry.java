package com.finqube.iso20022.core.security.hsm;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Audit log entry for HSM operations.
 *
 * <p>This class represents an audit log entry for HSM operations, providing
 * detailed information about who performed what operation when, along with
 * the result and any relevant context for compliance and security auditing.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class HsmAuditLogEntry {

    private final String entryId;
    private final LocalDateTime timestamp;
    private final String userId;
    private final String sessionId;
    private final String operation;
    private final String keyId;
    private final String certificateId;
    private final HsmProvider.OperationStatus status;
    private final String result;
    private final String errorMessage;
    private final String clientIp;
    private final String userAgent;
    private final long responseTimeMs;
    private final String additionalData;

    /**
     * Constructs a new HsmAuditLogEntry instance.
     *
     * @param entryId the unique entry identifier
     * @param timestamp the timestamp when the operation occurred
     * @param userId the user ID who performed the operation
     * @param sessionId the session ID
     * @param operation the operation performed
     * @param keyId the key ID involved in the operation
     * @param certificateId the certificate ID involved in the operation
     * @param status the operation status
     * @param result the operation result
     * @param errorMessage the error message if the operation failed
     * @param clientIp the client IP address
     * @param userAgent the user agent string
     * @param responseTimeMs the response time in milliseconds
     * @param additionalData additional data related to the operation
     */
    public HsmAuditLogEntry(String entryId, LocalDateTime timestamp, String userId, String sessionId,
                           String operation, String keyId, String certificateId, HsmProvider.OperationStatus status,
                           String result, String errorMessage, String clientIp, String userAgent,
                           long responseTimeMs, String additionalData) {
        this.entryId = entryId;
        this.timestamp = timestamp;
        this.userId = userId;
        this.sessionId = sessionId;
        this.operation = operation;
        this.keyId = keyId;
        this.certificateId = certificateId;
        this.status = status;
        this.result = result;
        this.errorMessage = errorMessage;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.responseTimeMs = responseTimeMs;
        this.additionalData = additionalData;
    }

    /**
     * Gets the unique entry identifier.
     *
     * @return the entry ID
     */
    public String getEntryId() {
        return entryId;
    }

    /**
     * Gets the timestamp when the operation occurred.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the user ID who performed the operation.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the session ID.
     *
     * @return the session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Gets the operation performed.
     *
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Gets the key ID involved in the operation.
     *
     * @return the key ID, or null if not applicable
     */
    public String getKeyId() {
        return keyId;
    }

    /**
     * Gets the certificate ID involved in the operation.
     *
     * @return the certificate ID, or null if not applicable
     */
    public String getCertificateId() {
        return certificateId;
    }

    /**
     * Gets the operation status.
     *
     * @return the operation status
     */
    public HsmProvider.OperationStatus getStatus() {
        return status;
    }

    /**
     * Gets the operation result.
     *
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * Gets the error message if the operation failed.
     *
     * @return the error message, or null if the operation succeeded
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the client IP address.
     *
     * @return the client IP address
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * Gets the user agent string.
     *
     * @return the user agent string
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Gets the response time in milliseconds.
     *
     * @return the response time
     */
    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    /**
     * Gets additional data related to the operation.
     *
     * @return the additional data, or null if not available
     */
    public String getAdditionalData() {
        return additionalData;
    }

    /**
     * Checks if the operation was successful.
     *
     * @return true if the operation was successful, false otherwise
     */
    public boolean isSuccessful() {
        return HsmProvider.OperationStatus.SUCCESS.equals(status);
    }

    /**
     * Checks if the operation failed.
     *
     * @return true if the operation failed, false otherwise
     */
    public boolean isFailed() {
        return HsmProvider.OperationStatus.FAILED.equals(status);
    }

    /**
     * Checks if the operation timed out.
     *
     * @return true if the operation timed out, false otherwise
     */
    public boolean isTimeout() {
        return HsmProvider.OperationStatus.TIMEOUT.equals(status);
    }

    /**
     * Checks if the operation was cancelled.
     *
     * @return true if the operation was cancelled, false otherwise
     */
    public boolean isCancelled() {
        return HsmProvider.OperationStatus.CANCELLED.equals(status);
    }

    /**
     * Gets the response time in a human-readable format.
     *
     * @return the response time as a formatted string
     */
    public String getResponseTimeFormatted() {
        if (responseTimeMs < 1000) {
            return responseTimeMs + "ms";
        } else if (responseTimeMs < 60000) {
            return String.format("%.2fs", responseTimeMs / 1000.0);
        } else {
            long minutes = responseTimeMs / 60000;
            long seconds = (responseTimeMs % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }

    /**
     * Gets a summary of the audit log entry.
     *
     * @return a summary string
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(operation);
        if (keyId != null) {
            summary.append(" (Key: ").append(keyId).append(")");
        }
        if (certificateId != null) {
            summary.append(" (Cert: ").append(certificateId).append(")");
        }
        summary.append(" - ").append(status);
        if (isFailed() && errorMessage != null) {
            summary.append(": ").append(errorMessage);
        }
        return summary.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HsmAuditLogEntry that = (HsmAuditLogEntry) obj;
        return Objects.equals(entryId, that.entryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryId);
    }

    @Override
    public String toString() {
        return String.format("HsmAuditLogEntry{entryId='%s', timestamp=%s, userId='%s', operation='%s', " +
                           "keyId='%s', status=%s, responseTimeMs=%d}",
                           entryId, timestamp, userId, operation, keyId, status, responseTimeMs);
    }
}
