package com.finqube.iso20022.core.security.certificate;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Certificate management statistics and metrics.
 *
 * <p>This class provides comprehensive statistics about certificate management
 * operations including validation counts, storage metrics, and performance data.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class CertificateStatistics {

    private final LocalDateTime timestamp;
    private final long totalCertificates;
    private final long validCertificates;
    private final long expiredCertificates;
    private final long revokedCertificates;
    private final long trustedCertificates;
    private final long totalValidations;
    private final long successfulValidations;
    private final long failedValidations;
    private final double averageValidationTimeMs;
    private final double minValidationTimeMs;
    private final double maxValidationTimeMs;
    private final long totalRevocationChecks;
    private final long successfulRevocationChecks;
    private final long failedRevocationChecks;
    private final double averageRevocationCheckTimeMs;
    private final long totalStorageOperations;
    private final long successfulStorageOperations;
    private final long failedStorageOperations;
    private final double averageStorageOperationTimeMs;

    /**
     * Constructs a new certificate statistics instance.
     *
     * @param timestamp the timestamp when statistics were collected
     * @param totalCertificates the total number of certificates
     * @param validCertificates the number of valid certificates
     * @param expiredCertificates the number of expired certificates
     * @param revokedCertificates the number of revoked certificates
     * @param trustedCertificates the number of trusted certificates
     * @param totalValidations the total number of validations
     * @param successfulValidations the number of successful validations
     * @param failedValidations the number of failed validations
     * @param averageValidationTimeMs the average validation time in milliseconds
     * @param minValidationTimeMs the minimum validation time in milliseconds
     * @param maxValidationTimeMs the maximum validation time in milliseconds
     * @param totalRevocationChecks the total number of revocation checks
     * @param successfulRevocationChecks the number of successful revocation checks
     * @param failedRevocationChecks the number of failed revocation checks
     * @param averageRevocationCheckTimeMs the average revocation check time in milliseconds
     * @param totalStorageOperations the total number of storage operations
     * @param successfulStorageOperations the number of successful storage operations
     * @param failedStorageOperations the number of failed storage operations
     * @param averageStorageOperationTimeMs the average storage operation time in milliseconds
     */
    public CertificateStatistics(LocalDateTime timestamp, long totalCertificates, long validCertificates,
                               long expiredCertificates, long revokedCertificates, long trustedCertificates,
                               long totalValidations, long successfulValidations, long failedValidations,
                               double averageValidationTimeMs, double minValidationTimeMs, double maxValidationTimeMs,
                               long totalRevocationChecks, long successfulRevocationChecks, long failedRevocationChecks,
                               double averageRevocationCheckTimeMs, long totalStorageOperations,
                               long successfulStorageOperations, long failedStorageOperations,
                               double averageStorageOperationTimeMs) {
        this.timestamp = timestamp;
        this.totalCertificates = totalCertificates;
        this.validCertificates = validCertificates;
        this.expiredCertificates = expiredCertificates;
        this.revokedCertificates = revokedCertificates;
        this.trustedCertificates = trustedCertificates;
        this.totalValidations = totalValidations;
        this.successfulValidations = successfulValidations;
        this.failedValidations = failedValidations;
        this.averageValidationTimeMs = averageValidationTimeMs;
        this.minValidationTimeMs = minValidationTimeMs;
        this.maxValidationTimeMs = maxValidationTimeMs;
        this.totalRevocationChecks = totalRevocationChecks;
        this.successfulRevocationChecks = successfulRevocationChecks;
        this.failedRevocationChecks = failedRevocationChecks;
        this.averageRevocationCheckTimeMs = averageRevocationCheckTimeMs;
        this.totalStorageOperations = totalStorageOperations;
        this.successfulStorageOperations = successfulStorageOperations;
        this.failedStorageOperations = failedStorageOperations;
        this.averageStorageOperationTimeMs = averageStorageOperationTimeMs;
    }

    /**
     * Gets the timestamp when statistics were collected.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the total number of certificates.
     *
     * @return the total certificates count
     */
    public long getTotalCertificates() {
        return totalCertificates;
    }

    /**
     * Gets the number of valid certificates.
     *
     * @return the valid certificates count
     */
    public long getValidCertificates() {
        return validCertificates;
    }

    /**
     * Gets the number of expired certificates.
     *
     * @return the expired certificates count
     */
    public long getExpiredCertificates() {
        return expiredCertificates;
    }

    /**
     * Gets the number of revoked certificates.
     *
     * @return the revoked certificates count
     */
    public long getRevokedCertificates() {
        return revokedCertificates;
    }

    /**
     * Gets the number of trusted certificates.
     *
     * @return the trusted certificates count
     */
    public long getTrustedCertificates() {
        return trustedCertificates;
    }

    /**
     * Gets the total number of validations.
     *
     * @return the total validations count
     */
    public long getTotalValidations() {
        return totalValidations;
    }

    /**
     * Gets the number of successful validations.
     *
     * @return the successful validations count
     */
    public long getSuccessfulValidations() {
        return successfulValidations;
    }

    /**
     * Gets the number of failed validations.
     *
     * @return the failed validations count
     */
    public long getFailedValidations() {
        return failedValidations;
    }

    /**
     * Gets the average validation time in milliseconds.
     *
     * @return the average validation time
     */
    public double getAverageValidationTimeMs() {
        return averageValidationTimeMs;
    }

    /**
     * Gets the minimum validation time in milliseconds.
     *
     * @return the minimum validation time
     */
    public double getMinValidationTimeMs() {
        return minValidationTimeMs;
    }

    /**
     * Gets the maximum validation time in milliseconds.
     *
     * @return the maximum validation time
     */
    public double getMaxValidationTimeMs() {
        return maxValidationTimeMs;
    }

    /**
     * Gets the total number of revocation checks.
     *
     * @return the total revocation checks count
     */
    public long getTotalRevocationChecks() {
        return totalRevocationChecks;
    }

    /**
     * Gets the number of successful revocation checks.
     *
     * @return the successful revocation checks count
     */
    public long getSuccessfulRevocationChecks() {
        return successfulRevocationChecks;
    }

    /**
     * Gets the number of failed revocation checks.
     *
     * @return the failed revocation checks count
     */
    public long getFailedRevocationChecks() {
        return failedRevocationChecks;
    }

    /**
     * Gets the average revocation check time in milliseconds.
     *
     * @return the average revocation check time
     */
    public double getAverageRevocationCheckTimeMs() {
        return averageRevocationCheckTimeMs;
    }

    /**
     * Gets the total number of storage operations.
     *
     * @return the total storage operations count
     */
    public long getTotalStorageOperations() {
        return totalStorageOperations;
    }

    /**
     * Gets the number of successful storage operations.
     *
     * @return the successful storage operations count
     */
    public long getSuccessfulStorageOperations() {
        return successfulStorageOperations;
    }

    /**
     * Gets the number of failed storage operations.
     *
     * @return the failed storage operations count
     */
    public long getFailedStorageOperations() {
        return failedStorageOperations;
    }

    /**
     * Gets the average storage operation time in milliseconds.
     *
     * @return the average storage operation time
     */
    public double getAverageStorageOperationTimeMs() {
        return averageStorageOperationTimeMs;
    }

    /**
     * Calculates the validation success rate as a percentage.
     *
     * @return the validation success rate percentage, or 0.0 if no validations
     */
    public double getValidationSuccessRate() {
        if (totalValidations == 0) {
            return 0.0;
        }
        return (double) successfulValidations / totalValidations * 100.0;
    }

    /**
     * Calculates the validation failure rate as a percentage.
     *
     * @return the validation failure rate percentage, or 0.0 if no validations
     */
    public double getValidationFailureRate() {
        if (totalValidations == 0) {
            return 0.0;
        }
        return (double) failedValidations / totalValidations * 100.0;
    }

    /**
     * Calculates the revocation check success rate as a percentage.
     *
     * @return the revocation check success rate percentage, or 0.0 if no revocation checks
     */
    public double getRevocationCheckSuccessRate() {
        if (totalRevocationChecks == 0) {
            return 0.0;
        }
        return (double) successfulRevocationChecks / totalRevocationChecks * 100.0;
    }

    /**
     * Calculates the revocation check failure rate as a percentage.
     *
     * @return the revocation check failure rate percentage, or 0.0 if no revocation checks
     */
    public double getRevocationCheckFailureRate() {
        if (totalRevocationChecks == 0) {
            return 0.0;
        }
        return (double) failedRevocationChecks / totalRevocationChecks * 100.0;
    }

    /**
     * Calculates the storage operation success rate as a percentage.
     *
     * @return the storage operation success rate percentage, or 0.0 if no storage operations
     */
    public double getStorageOperationSuccessRate() {
        if (totalStorageOperations == 0) {
            return 0.0;
        }
        return (double) successfulStorageOperations / totalStorageOperations * 100.0;
    }

    /**
     * Calculates the storage operation failure rate as a percentage.
     *
     * @return the storage operation failure rate percentage, or 0.0 if no storage operations
     */
    public double getStorageOperationFailureRate() {
        if (totalStorageOperations == 0) {
            return 0.0;
        }
        return (double) failedStorageOperations / totalStorageOperations * 100.0;
    }

    /**
     * Calculates the certificate validity rate as a percentage.
     *
     * @return the certificate validity rate percentage, or 0.0 if no certificates
     */
    public double getCertificateValidityRate() {
        if (totalCertificates == 0) {
            return 0.0;
        }
        return (double) validCertificates / totalCertificates * 100.0;
    }

    /**
     * Calculates the certificate expiration rate as a percentage.
     *
     * @return the certificate expiration rate percentage, or 0.0 if no certificates
     */
    public double getCertificateExpirationRate() {
        if (totalCertificates == 0) {
            return 0.0;
        }
        return (double) expiredCertificates / totalCertificates * 100.0;
    }

    /**
     * Calculates the certificate revocation rate as a percentage.
     *
     * @return the certificate revocation rate percentage, or 0.0 if no certificates
     */
    public double getCertificateRevocationRate() {
        if (totalCertificates == 0) {
            return 0.0;
        }
        return (double) revokedCertificates / totalCertificates * 100.0;
    }

    /**
     * Calculates the certificate trust rate as a percentage.
     *
     * @return the certificate trust rate percentage, or 0.0 if no certificates
     */
    public double getCertificateTrustRate() {
        if (totalCertificates == 0) {
            return 0.0;
        }
        return (double) trustedCertificates / totalCertificates * 100.0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CertificateStatistics that = (CertificateStatistics) obj;
        return Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    @Override
    public String toString() {
        return String.format("CertificateStatistics{timestamp=%s, totalCertificates=%d, validCertificates=%d, " +
                           "expiredCertificates=%d, totalValidations=%d, successfulValidations=%d, " +
                           "validationSuccessRate=%.2f%%, averageValidationTimeMs=%.2f}",
                           timestamp, totalCertificates, validCertificates, expiredCertificates,
                           totalValidations, successfulValidations, getValidationSuccessRate(), averageValidationTimeMs);
    }
}
