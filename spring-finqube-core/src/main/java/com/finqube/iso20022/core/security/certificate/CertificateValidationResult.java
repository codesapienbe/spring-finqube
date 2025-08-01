package com.finqube.iso20022.core.security.certificate;

import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Result of certificate validation operations.
 *
 * <p>This class provides detailed information about the result of certificate
 * validation operations, including validation status, error details, and
 * certificate path information.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class CertificateValidationResult {

    private final boolean valid;
    private final CertificateManager.ValidationStatus status;
    private final String errorMessage;
    private final LocalDateTime validationTime;
    private final List<X509Certificate> certificatePath;
    private final List<String> validationErrors;
    private final List<String> validationWarnings;
    private final long validationDurationMs;

    /**
     * Constructs a new certificate validation result.
     *
     * @param valid whether the certificate is valid
     * @param status the validation status
     * @param errorMessage the error message, or null if validation succeeded
     * @param validationTime the time when validation was performed
     * @param certificatePath the certificate path used for validation
     * @param validationErrors list of validation errors
     * @param validationWarnings list of validation warnings
     * @param validationDurationMs the duration of validation in milliseconds
     */
    public CertificateValidationResult(boolean valid, CertificateManager.ValidationStatus status,
                                     String errorMessage, LocalDateTime validationTime,
                                     List<X509Certificate> certificatePath,
                                     List<String> validationErrors, List<String> validationWarnings,
                                     long validationDurationMs) {
        this.valid = valid;
        this.status = status;
        this.errorMessage = errorMessage;
        this.validationTime = validationTime;
        this.certificatePath = certificatePath;
        this.validationErrors = validationErrors;
        this.validationWarnings = validationWarnings;
        this.validationDurationMs = validationDurationMs;
    }

    /**
     * Creates a successful validation result.
     *
     * @param certificatePath the certificate path used for validation
     * @param validationDurationMs the duration of validation in milliseconds
     * @return a successful validation result
     */
    public static CertificateValidationResult success(List<X509Certificate> certificatePath, long validationDurationMs) {
        return new CertificateValidationResult(true, CertificateManager.ValidationStatus.VALID, null,
                                             LocalDateTime.now(), certificatePath, List.of(), List.of(), validationDurationMs);
    }

    /**
     * Creates a failed validation result.
     *
     * @param status the validation status
     * @param errorMessage the error message
     * @param validationErrors list of validation errors
     * @param validationWarnings list of validation warnings
     * @param validationDurationMs the duration of validation in milliseconds
     * @return a failed validation result
     */
    public static CertificateValidationResult failure(CertificateManager.ValidationStatus status, String errorMessage,
                                                     List<String> validationErrors, List<String> validationWarnings,
                                                     long validationDurationMs) {
        return new CertificateValidationResult(false, status, errorMessage, LocalDateTime.now(),
                                             List.of(), validationErrors, validationWarnings, validationDurationMs);
    }

    /**
     * Checks if the certificate is valid.
     *
     * @return true if the certificate is valid, false otherwise
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Gets the validation status.
     *
     * @return the validation status
     */
    public CertificateManager.ValidationStatus getStatus() {
        return status;
    }

    /**
     * Gets the error message.
     *
     * @return the error message, or null if validation succeeded
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the time when validation was performed.
     *
     * @return the validation time
     */
    public LocalDateTime getValidationTime() {
        return validationTime;
    }

    /**
     * Gets the certificate path used for validation.
     *
     * @return the certificate path
     */
    public List<X509Certificate> getCertificatePath() {
        return certificatePath;
    }

    /**
     * Gets the list of validation errors.
     *
     * @return the validation errors
     */
    public List<String> getValidationErrors() {
        return validationErrors;
    }

    /**
     * Gets the list of validation warnings.
     *
     * @return the validation warnings
     */
    public List<String> getValidationWarnings() {
        return validationWarnings;
    }

    /**
     * Gets the duration of validation in milliseconds.
     *
     * @return the validation duration
     */
    public long getValidationDurationMs() {
        return validationDurationMs;
    }

    /**
     * Gets the validation duration in a human-readable format.
     *
     * @return the validation duration as a formatted string
     */
    public String getValidationDurationFormatted() {
        if (validationDurationMs < 1000) {
            return validationDurationMs + "ms";
        } else if (validationDurationMs < 60000) {
            return String.format("%.2fs", validationDurationMs / 1000.0);
        } else {
            long minutes = validationDurationMs / 60000;
            long seconds = (validationDurationMs % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }

    /**
     * Gets the number of certificates in the certificate path.
     *
     * @return the certificate path length
     */
    public int getCertificatePathLength() {
        return certificatePath.size();
    }

    /**
     * Checks if there are any validation errors.
     *
     * @return true if there are validation errors, false otherwise
     */
    public boolean hasErrors() {
        return !validationErrors.isEmpty();
    }

    /**
     * Checks if there are any validation warnings.
     *
     * @return true if there are validation warnings, false otherwise
     */
    public boolean hasWarnings() {
        return !validationWarnings.isEmpty();
    }

    /**
     * Gets a summary of the validation result.
     *
     * @return a summary string
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Certificate validation ").append(valid ? "succeeded" : "failed");
        summary.append(" with status ").append(status);
        if (errorMessage != null) {
            summary.append(": ").append(errorMessage);
        }
        summary.append(" (duration: ").append(getValidationDurationFormatted()).append(")");
        return summary.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CertificateValidationResult that = (CertificateValidationResult) obj;
        return valid == that.valid && status == that.status && Objects.equals(validationTime, that.validationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, status, validationTime);
    }

    @Override
    public String toString() {
        return String.format("CertificateValidationResult{valid=%s, status=%s, errorMessage='%s', " +
                           "validationTime=%s, certificatePathLength=%d, validationDurationMs=%d}",
                           valid, status, errorMessage, validationTime, getCertificatePathLength(), validationDurationMs);
    }
}
