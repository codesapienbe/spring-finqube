package com.finqube.iso20022.core.validation;

import java.util.Objects;

/**
 * Represents a validation warning.
 *
 * <p>This class encapsulates information about validation warnings,
 * which are non-fatal issues that should be addressed but don't prevent processing.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class ValidationWarning {

    private final String code;
    private final String message;
    private final WarningSeverity severity;
    private final String location;
    private final String field;
    private final String value;
    private final String validator;

    /**
     * Constructs a new ValidationWarning.
     *
     * @param code the warning code
     * @param message the warning message
     * @param severity the warning severity
     * @param location the warning location
     * @param field the field that caused the warning
     * @param value the value that caused the warning
     * @param validator the validator that reported the warning
     */
    public ValidationWarning(String code, String message, WarningSeverity severity,
                           String location, String field, String value, String validator) {
        this.code = Objects.requireNonNull(code, "Warning code cannot be null");
        this.message = Objects.requireNonNull(message, "Warning message cannot be null");
        this.severity = Objects.requireNonNull(severity, "Warning severity cannot be null");
        this.location = location;
        this.field = field;
        this.value = value;
        this.validator = validator;
    }

    /**
     * Gets the warning code.
     *
     * @return the warning code
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the warning message.
     *
     * @return the warning message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the warning severity.
     *
     * @return the warning severity
     */
    public WarningSeverity getSeverity() {
        return severity;
    }

    /**
     * Gets the warning location.
     *
     * @return the warning location, or null if not available
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the field that caused the warning.
     *
     * @return the field name, or null if not available
     */
    public String getField() {
        return field;
    }

    /**
     * Gets the value that caused the warning.
     *
     * @return the problematic value, or null if not available
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the validator that reported the warning.
     *
     * @return the validator name, or null if not available
     */
    public String getValidator() {
        return validator;
    }

    @Override
    public String toString() {
        return "ValidationWarning{" +
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
        ValidationWarning that = (ValidationWarning) o;
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
     * Warning severity levels.
     */
    public enum WarningSeverity {
        LOW("Low", "Minor issue that can be ignored"),
        MEDIUM("Medium", "Issue that should be addressed"),
        HIGH("High", "Important issue that should be addressed soon");

        private final String displayName;
        private final String description;

        WarningSeverity(String displayName, String description) {
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