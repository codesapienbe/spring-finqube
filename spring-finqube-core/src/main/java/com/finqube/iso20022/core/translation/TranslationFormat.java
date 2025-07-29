package com.finqube.iso20022.core.translation;

import java.util.Objects;

/**
 * Represents a supported translation format.
 *
 * <p>This class encapsulates information about a supported translation format,
 * including format type, version, and capabilities.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class TranslationFormat {

    private final String formatId;
    private final String displayName;
    private final String description;
    private final FormatType formatType;
    private final String version;
    private final boolean supported;

    /**
     * Constructs a new TranslationFormat.
     *
     * @param formatId the format identifier
     * @param displayName the display name
     * @param description the description
     * @param formatType the format type
     * @param version the format version
     * @param supported whether the format is supported
     */
    public TranslationFormat(String formatId, String displayName, String description,
                           FormatType formatType, String version, boolean supported) {
        this.formatId = Objects.requireNonNull(formatId, "Format ID cannot be null");
        this.displayName = Objects.requireNonNull(displayName, "Display name cannot be null");
        this.description = description;
        this.formatType = Objects.requireNonNull(formatType, "Format type cannot be null");
        this.version = Objects.requireNonNull(version, "Version cannot be null");
        this.supported = supported;
    }

    /**
     * Gets the format identifier.
     *
     * @return the format identifier
     */
    public String getFormatId() {
        return formatId;
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the format type.
     *
     * @return the format type
     */
    public FormatType getFormatType() {
        return formatType;
    }

    /**
     * Gets the format version.
     *
     * @return the format version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Checks if the format is supported.
     *
     * @return true if supported, false otherwise
     */
    public boolean isSupported() {
        return supported;
    }

    @Override
    public String toString() {
        return "TranslationFormat{" +
                "formatId='" + formatId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", formatType=" + formatType +
                ", version='" + version + '\'' +
                ", supported=" + supported +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationFormat that = (TranslationFormat) o;
        return supported == that.supported &&
                Objects.equals(formatId, that.formatId) &&
                Objects.equals(displayName, that.displayName) &&
                Objects.equals(description, that.description) &&
                formatType == that.formatType &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formatId, displayName, description, formatType, version, supported);
    }

    /**
     * Format type enumeration.
     */
    public enum FormatType {
        MT("MT", "SWIFT MT format"),
        MX("MX", "ISO 20022 XML format"),
        JSON("JSON", "JSON format"),
        CSV("CSV", "CSV format"),
        CUSTOM("CUSTOM", "Custom format");

        private final String code;
        private final String description;

        FormatType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return code;
        }
    }
}
