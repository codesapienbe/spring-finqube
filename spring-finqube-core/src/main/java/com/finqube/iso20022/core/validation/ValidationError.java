package com.finqube.iso20022.core.validation;

import java.util.Objects;

/**
 * Represents a validation error.
 *
 * <p>This class encapsulates detailed information about a validation error,
 * including error type, message, location, and severity.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class ValidationError {

    private final String code;
    private final String message;
    private final ErrorSeverity severity;
    private final String location;
    private final String field;
    private final String value;
    private final String validator;

    /**
     * Constructs a new ValidationError.
     *
     * @param code the error code
     * @param message the error message
     * @param severity the error severity
     * @param location the error location (XPath, line number, etc.)
     * @param field the field that caused the error
     * @param value the value that caused the error
     * @param validator the validator that reported the error
     */
    public ValidationError(String code, String message, ErrorSeverity severity,
                         String location, String field, String value, String validator) {
        this.code = Objects.requireNonNull(code, "Error code cannot be null");
        this.message = Objects.requireNonNull(message, "Error message cannot be null");
        this.severity = Objects.requireNonNull(severity, "Error severity cannot be null");
        this.location = location;
        this.field = field;
        this.value = value;
        this.validator = validator;
    }

    /**
     * Gets the error code.
     *
     * @return the error code
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the error severity.
     *
     * @return the error severity
     */
    public ErrorSeverity getSeverity() {
        return severity;
    }

    /**
     * Gets the error location.
     *
     * @return the error location, or null if not available
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the field that caused the error.
     *
     * @return the field name, or null if not available
     */
    public String getField() {
        return field;
    }

    /**
     * Gets the value that caused the error.
     *
     * @return the problematic value, or null if not available
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the validator that reported the error.
     *
     * @return the validator name, or null if not available
     */
    public String getValidator() {
        return validator;
    }

    /**
     * Checks if this is a critical error.
     *
     * @return true if critical, false otherwise
     */
    public boolean isCritical() {
        return severity == ErrorSeverity.CRITICAL;
    }

    /**
     * Checks if this is a fatal error.
     *
     * @return true if fatal, false otherwise
     */
    public boolean isFatal() {
        return severity == ErrorSeverity.FATAL;
    }

    @Override
    public String toString() {
        return "ValidationError{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", severity=" + severity +
                ", location='" + location + '\'' +
                ", field='" + field + '\'' +
                ", validator='" + validator + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationError that = (ValidationError) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(message, that.message) &&
                severity == that.severity &&
                Objects.equals(location, that.location) &&
                Objects.equals(field, that.field) &&
                Objects.equals(value, that.value) &&
                Objects.equals(validator, that.validator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, severity, location, field, value, validator);
    }

    /**
     * Error severity levels.
     */
    public enum ErrorSeverity {
        INFO("Info", "Informational message"),
        WARNING("Warning", "Warning that should be addressed"),
        ERROR("Error", "Error that prevents processing"),
        CRITICAL("Critical", "Critical error that requires immediate attention"),
        FATAL("Fatal", "Fatal error that stops all processing");

        private final String displayName;
        private final String description;

        ErrorSeverity(String displayName, String description) {
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