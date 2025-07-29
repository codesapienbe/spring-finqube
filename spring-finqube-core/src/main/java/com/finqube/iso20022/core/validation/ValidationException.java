package com.finqube.iso20022.core.validation;

/**
 * Exception thrown when validation operations fail.
 *
 * <p>This exception is thrown when there are problems with the validation process itself,
 * such as schema loading failures, configuration issues, or system errors.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class ValidationException extends RuntimeException {

    private final String validatorId;
    private final String messageId;
    private final ValidationErrorSeverity severity;

    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message);
        this.validatorId = null;
        this.messageId = null;
        this.severity = ValidationErrorSeverity.ERROR;
    }

    /**
     * Constructs a new ValidationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.validatorId = null;
        this.messageId = null;
        this.severity = ValidationErrorSeverity.ERROR;
    }

    /**
     * Constructs a new ValidationException with validation details.
     *
     * @param message the detail message
     * @param validatorId the validator identifier
     * @param messageId the message identifier
     * @param severity the error severity
     */
    public ValidationException(String message, String validatorId, String messageId, ValidationErrorSeverity severity) {
        super(message);
        this.validatorId = validatorId;
        this.messageId = messageId;
        this.severity = severity;
    }

    /**
     * Constructs a new ValidationException with validation details and cause.
     *
     * @param message the detail message
     * @param validatorId the validator identifier
     * @param messageId the message identifier
     * @param severity the error severity
     * @param cause the cause
     */
    public ValidationException(String message, String validatorId, String messageId, ValidationErrorSeverity severity, Throwable cause) {
        super(message, cause);
        this.validatorId = validatorId;
        this.messageId = messageId;
        this.severity = severity;
    }

    /**
     * Gets the validator identifier.
     *
     * @return the validator identifier, or null if not available
     */
    public String getValidatorId() {
        return validatorId;
    }

    /**
     * Gets the message identifier.
     *
     * @return the message identifier, or null if not available
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Gets the error severity.
     *
     * @return the error severity
     */
    public ValidationErrorSeverity getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        return "ValidationException{" +
                "message='" + getMessage() + '\'' +
                ", validatorId='" + validatorId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", severity=" + severity +
                '}';
    }

    /**
     * Validation error severity levels.
     */
    public enum ValidationErrorSeverity {
        WARNING("Warning", "Non-critical validation issue"),
        ERROR("Error", "Validation error that prevents processing"),
        CRITICAL("Critical", "Critical validation error"),
        FATAL("Fatal", "Fatal validation error that stops all processing");

        private final String displayName;
        private final String description;

        ValidationErrorSeverity(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}