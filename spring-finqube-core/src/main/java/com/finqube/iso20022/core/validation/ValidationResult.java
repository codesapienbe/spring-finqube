package com.finqube.iso20022.core.validation;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Result of a validation operation.
 *
 * <p>This class encapsulates the result of validating an ISO 20022 message,
 * including validation status, errors, warnings, and metadata.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class ValidationResult {

    private final String messageId;
    private final boolean valid;
    private final List<ValidationError> errors;
    private final List<ValidationWarning> warnings;
    private final Instant validatedAt;
    private final long validationTimeMillis;
    private final Map<String, Object> metadata;

    /**
     * Constructs a new ValidationResult.
     *
     * @param messageId the message identifier
     * @param valid whether the validation was successful
     * @param errors list of validation errors
     * @param warnings list of validation warnings
     * @param validatedAt when the validation was performed
     * @param validationTimeMillis the validation time in milliseconds
     * @param metadata additional metadata about the validation
     */
    public ValidationResult(String messageId, boolean valid, List<ValidationError> errors,
                          List<ValidationWarning> warnings, Instant validatedAt,
                          long validationTimeMillis, Map<String, Object> metadata) {
        this.messageId = Objects.requireNonNull(messageId, "Message ID cannot be null");
        this.valid = valid;
        this.errors = errors != null ? List.copyOf(errors) : List.of();
        this.warnings = warnings != null ? List.copyOf(warnings) : List.of();
        this.validatedAt = Objects.requireNonNull(validatedAt, "Validation timestamp cannot be null");
        this.validationTimeMillis = validationTimeMillis;
        this.metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    /**
     * Creates a successful validation result.
     *
     * @param messageId the message identifier
     * @param validatedAt when the validation was performed
     * @param validationTimeMillis the validation time in milliseconds
     * @return a successful validation result
     */
    public static ValidationResult success(String messageId, Instant validatedAt, long validationTimeMillis) {
        return new ValidationResult(messageId, true, List.of(), List.of(), validatedAt, validationTimeMillis, Map.of());
    }

    /**
     * Creates a successful validation result with warnings.
     *
     * @param messageId the message identifier
     * @param warnings list of validation warnings
     * @param validatedAt when the validation was performed
     * @param validationTimeMillis the validation time in milliseconds
     * @return a successful validation result with warnings
     */
    public static ValidationResult successWithWarnings(String messageId, List<ValidationWarning> warnings,
                                                     Instant validatedAt, long validationTimeMillis) {
        return new ValidationResult(messageId, true, List.of(), warnings, validatedAt, validationTimeMillis, Map.of());
    }

    /**
     * Creates a failed validation result.
     *
     * @param messageId the message identifier
     * @param errors list of validation errors
     * @param validatedAt when the validation was performed
     * @param validationTimeMillis the validation time in milliseconds
     * @return a failed validation result
     */
    public static ValidationResult failure(String messageId, List<ValidationError> errors,
                                         Instant validatedAt, long validationTimeMillis) {
        return new ValidationResult(messageId, false, errors, List.of(), validatedAt, validationTimeMillis, Map.of());
    }

    /**
     * Gets the message identifier.
     *
     * @return the message identifier
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Checks if the validation was successful.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Gets the validation errors.
     *
     * @return immutable list of validation errors
     */
    public List<ValidationError> getErrors() {
        return errors;
    }

    /**
     * Gets the validation warnings.
     *
     * @return immutable list of validation warnings
     */
    public List<ValidationWarning> getWarnings() {
        return warnings;
    }

    /**
     * Gets when the validation was performed.
     *
     * @return the validation timestamp
     */
    public Instant getValidatedAt() {
        return validatedAt;
    }

    /**
     * Gets the validation time in milliseconds.
     *
     * @return the validation time in milliseconds
     */
    public long getValidationTimeMillis() {
        return validationTimeMillis;
    }

    /**
     * Gets additional metadata about the validation.
     *
     * @return immutable map of metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Checks if there are any errors.
     *
     * @return true if there are errors, false otherwise
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Checks if there are any warnings.
     *
     * @return true if there are warnings, false otherwise
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    /**
     * Gets the number of errors.
     *
     * @return the number of errors
     */
    public int getErrorCount() {
        return errors.size();
    }

    /**
     * Gets the number of warnings.
     *
     * @return the number of warnings
     */
    public int getWarningCount() {
        return warnings.size();
    }

    /**
     * Gets the first error, if any.
     *
     * @return the first error, or null if no errors
     */
    public ValidationError getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }

    /**
     * Gets the first warning, if any.
     *
     * @return the first warning, or null if no warnings
     */
    public ValidationWarning getFirstWarning() {
        return warnings.isEmpty() ? null : warnings.get(0);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "messageId='" + messageId + '\'' +
                ", valid=" + valid +
                ", errorCount=" + getErrorCount() +
                ", warningCount=" + getWarningCount() +
                ", validatedAt=" + validatedAt +
                ", validationTimeMillis=" + validationTimeMillis +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationResult that = (ValidationResult) o;
        return valid == that.valid &&
                validationTimeMillis == that.validationTimeMillis &&
                Objects.equals(messageId, that.messageId) &&
                Objects.equals(errors, that.errors) &&
                Objects.equals(warnings, that.warnings) &&
                Objects.equals(validatedAt, that.validatedAt) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, valid, errors, warnings, validatedAt, validationTimeMillis, metadata);
    }
}
