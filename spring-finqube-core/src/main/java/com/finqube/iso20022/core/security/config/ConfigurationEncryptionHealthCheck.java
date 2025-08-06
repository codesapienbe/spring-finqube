package com.finqube.iso20022.core.security.config;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Health check result for the configuration encryption service.
 *
 * <p>This class represents the health status of the configuration encryption
 * service, providing information about its operational state and any issues
 * that may affect its functionality.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class ConfigurationEncryptionHealthCheck {

    private final String serviceId;
    private final LocalDateTime timestamp;
    private final String status;
    private final String message;
    private final boolean healthy;
    private final long encryptionCount;
    private final long decryptionCount;
    private final long errorCount;
    private final double averageProcessingTimeMs;

    /**
     * Constructs a new ConfigurationEncryptionHealthCheck instance.
     *
     * @param serviceId the service identifier
     * @param timestamp the timestamp of the health check
     * @param status the health status
     * @param message the health check message
     * @param healthy whether the service is healthy
     * @param encryptionCount the number of encryption operations
     * @param decryptionCount the number of decryption operations
     * @param errorCount the number of errors
     * @param averageProcessingTimeMs the average processing time in milliseconds
     */
    public ConfigurationEncryptionHealthCheck(String serviceId, LocalDateTime timestamp, String status,
                                             String message, boolean healthy, long encryptionCount,
                                             long decryptionCount, long errorCount, double averageProcessingTimeMs) {
        this.serviceId = Objects.requireNonNull(serviceId, "Service ID cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.message = message;
        this.healthy = healthy;
        this.encryptionCount = encryptionCount;
        this.decryptionCount = decryptionCount;
        this.errorCount = errorCount;
        this.averageProcessingTimeMs = averageProcessingTimeMs;
    }

    /**
     * Gets the service identifier.
     *
     * @return the service identifier
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Gets the timestamp of the health check.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the health status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the health check message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Checks if the service is healthy.
     *
     * @return true if healthy, false otherwise
     */
    public boolean isHealthy() {
        return healthy;
    }

    /**
     * Gets the number of encryption operations.
     *
     * @return the encryption count
     */
    public long getEncryptionCount() {
        return encryptionCount;
    }

    /**
     * Gets the number of decryption operations.
     *
     * @return the decryption count
     */
    public long getDecryptionCount() {
        return decryptionCount;
    }

    /**
     * Gets the number of errors.
     *
     * @return the error count
     */
    public long getErrorCount() {
        return errorCount;
    }

    /**
     * Gets the average processing time in milliseconds.
     *
     * @return the average processing time
     */
    public double getAverageProcessingTimeMs() {
        return averageProcessingTimeMs;
    }

    /**
     * Checks if the service is unhealthy.
     *
     * @return true if unhealthy, false otherwise
     */
    public boolean isUnhealthy() {
        return !healthy;
    }

    /**
     * Gets a summary of the health check.
     *
     * @return the summary string
     */
    public String getSummary() {
        return String.format("ConfigurationEncryption[%s] %s - %s - %d encryptions, %d decryptions, %d errors",
                serviceId, status, message, encryptionCount, decryptionCount, errorCount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ConfigurationEncryptionHealthCheck that = (ConfigurationEncryptionHealthCheck) obj;
        return Objects.equals(serviceId, that.serviceId) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId, timestamp);
    }

    @Override
    public String toString() {
        return String.format("ConfigurationEncryptionHealthCheck{serviceId='%s', timestamp=%s, status='%s', " +
                "message='%s', healthy=%s, encryptionCount=%d, decryptionCount=%d, errorCount=%d, " +
                "averageProcessingTimeMs=%.2f}",
                serviceId, timestamp, status, message, healthy, encryptionCount, decryptionCount,
                errorCount, averageProcessingTimeMs);
    }
}
