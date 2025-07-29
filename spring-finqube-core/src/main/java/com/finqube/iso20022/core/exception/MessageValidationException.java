package com.finqube.iso20022.core.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when ISO 20022 message validation fails.
 *
 * <p>This exception provides detailed information about validation errors,
 * including the specific validation rules that were violated and suggestions
 * for how to fix the issues.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class MessageValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String messageId;
    private final String messageType;
    private final List<ValidationError> validationErrors;

    /**
     * Constructs a new MessageValidationException with the specified detail message.
     *
     * @param message the detail message
     * @param messageId the ID of the message that failed validation
     * @param messageType the type of message that failed validation
     */
    public MessageValidationException(String message, String messageId, String messageType) {
        super(message);
        this.messageId = messageId;
        this.messageType = messageType;
        this.validationErrors = new ArrayList<>();
    }

    /**
     * Constructs a new MessageValidationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the validation failure
     * @param messageId the ID of the message that failed validation
     * @param messageType the type of message that failed validation
     */
    public MessageValidationException(String message, Throwable cause, String messageId, String messageType) {
        super(message, cause);
        this.messageId = messageId;
        this.messageType = messageType;
        this.validationErrors = new ArrayList<>();
    }

    /**
     * Constructs a new MessageValidationException with validation errors.
     *
     * @param message the detail message
     * @param messageId the ID of the message that failed validation
     * @param messageType the type of message that failed validation
     * @param validationErrors the list of validation errors
     */
    public MessageValidationException(String message, String messageId, String messageType,
                                    List<ValidationError> validationErrors) {
        super(message);
        this.messageId = messageId;
        this.messageType = messageType;
        this.validationErrors = new ArrayList<>(validationErrors);
    }

    /**
     * Gets the ID of the message that failed validation.
     *
     * @return the message ID
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Gets the type of message that failed validation.
     *
     * @return the message type
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * Gets the list of validation errors.
     *
     * @return the validation errors
     */
    public List<ValidationError> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }

    /**
     * Adds a validation error to this exception.
     *
     * @param error the validation error to add
     */
    public void addValidationError(ValidationError error) {
        this.validationErrors.add(error);
    }

    /**
     * Checks if this exception has any validation errors.
     *
     * @return true if there are validation errors, false otherwise
     */
    public boolean hasValidationErrors() {
        return !validationErrors.isEmpty();
    }

    /**
     * Gets the number of validation errors.
     *
     * @return the number of validation errors
     */
    public int getValidationErrorCount() {
        return validationErrors.size();
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        if (messageId != null) {
            sb.append(" [Message ID: ").append(messageId).append("]");
        }
        if (messageType != null) {
            sb.append(" [Message Type: ").append(messageType).append("]");
        }
        if (!validationErrors.isEmpty()) {
            sb.append(" [Validation Errors: ").append(validationErrors.size()).append("]");
        }
        return sb.toString();
    }

    /**
     * Represents a single validation error with details about what went wrong.
     */
    public static class ValidationError {
        private final String field;
        private final String code;
        private final String message;
        private final String severity;

        /**
         * Constructs a new ValidationError.
         *
         * @param field the field that failed validation
         * @param code the validation error code
         * @param message the validation error message
         * @param severity the severity level of the error
         */
        public ValidationError(String field, String code, String message, String severity) {
            this.field = field;
            this.code = code;
            this.message = message;
            this.severity = severity;
        }

        /**
         * Gets the field that failed validation.
         *
         * @return the field name
         */
        public String getField() {
            return field;
        }

        /**
         * Gets the validation error code.
         *
         * @return the error code
         */
        public String getCode() {
            return code;
        }

        /**
         * Gets the validation error message.
         *
         * @return the error message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets the severity level of the error.
         *
         * @return the severity level
         */
        public String getSeverity() {
            return severity;
        }

        @Override
        public String toString() {
            return String.format("ValidationError{field='%s', code='%s', message='%s', severity='%s'}",
                               field, code, message, severity);
        }
    }
}
