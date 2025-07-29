package com.finqube.iso20022.core.translation;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import com.finqube.iso20022.core.message.BaseMessage;

/**
 * Result of a translation operation.
 *
 * <p>This class encapsulates the result of translating a message from one format to another,
 * including the translated message, translation metadata, and any warnings or errors.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class TranslationResult {

    private final String translationId;
    private final boolean successful;
    private final String errorMessage;
    private final BaseMessage sourceMessage;
    private final BaseMessage translatedMessage;
    private final String sourceFormat;
    private final String targetFormat;
    private final Instant translatedAt;
    private final long translationTimeMillis;
    private final Map<String, Object> metadata;
    private final Map<String, String> warnings;

    /**
     * Constructs a new TranslationResult.
     *
     * @param translationId the translation identifier
     * @param successful whether the translation was successful
     * @param errorMessage the error message, if any
     * @param sourceMessage the source message
     * @param translatedMessage the translated message, if successful
     * @param sourceFormat the source format
     * @param targetFormat the target format
     * @param translatedAt when the translation was performed
     * @param translationTimeMillis the translation time in milliseconds
     * @param metadata additional metadata about the translation
     * @param warnings translation warnings, if any
     */
    public TranslationResult(String translationId, boolean successful, String errorMessage,
                           BaseMessage sourceMessage, BaseMessage translatedMessage,
                           String sourceFormat, String targetFormat, Instant translatedAt,
                           long translationTimeMillis, Map<String, Object> metadata, Map<String, String> warnings) {
        this.translationId = Objects.requireNonNull(translationId, "Translation ID cannot be null");
        this.successful = successful;
        this.errorMessage = errorMessage;
        this.sourceMessage = Objects.requireNonNull(sourceMessage, "Source message cannot be null");
        this.translatedMessage = translatedMessage;
        this.sourceFormat = Objects.requireNonNull(sourceFormat, "Source format cannot be null");
        this.targetFormat = Objects.requireNonNull(targetFormat, "Target format cannot be null");
        this.translatedAt = Objects.requireNonNull(translatedAt, "Translated timestamp cannot be null");
        this.translationTimeMillis = translationTimeMillis;
        this.metadata = metadata;
        this.warnings = warnings;
    }

    /**
     * Creates a successful translation result.
     *
     * @param translationId the translation identifier
     * @param sourceMessage the source message
     * @param translatedMessage the translated message
     * @param sourceFormat the source format
     * @param targetFormat the target format
     * @param translatedAt when the translation was performed
     * @param translationTimeMillis the translation time in milliseconds
     * @param metadata additional metadata about the translation
     * @return a successful translation result
     */
    public static TranslationResult success(String translationId, BaseMessage sourceMessage, BaseMessage translatedMessage,
                                          String sourceFormat, String targetFormat, Instant translatedAt,
                                          long translationTimeMillis, Map<String, Object> metadata) {
        return new TranslationResult(translationId, true, null, sourceMessage, translatedMessage,
            sourceFormat, targetFormat, translatedAt, translationTimeMillis, metadata, Map.of());
    }

    /**
     * Creates a failed translation result.
     *
     * @param translationId the translation identifier
     * @param sourceMessage the source message
     * @param errorMessage the error message
     * @param sourceFormat the source format
     * @param targetFormat the target format
     * @param translatedAt when the translation was attempted
     * @param translationTimeMillis the translation time in milliseconds
     * @return a failed translation result
     */
    public static TranslationResult failure(String translationId, BaseMessage sourceMessage, String errorMessage,
                                          String sourceFormat, String targetFormat, Instant translatedAt,
                                          long translationTimeMillis) {
        return new TranslationResult(translationId, false, errorMessage, sourceMessage, null,
            sourceFormat, targetFormat, translatedAt, translationTimeMillis, Map.of(), Map.of());
    }

    /**
     * Gets the translation identifier.
     *
     * @return the translation identifier
     */
    public String getTranslationId() {
        return translationId;
    }

    /**
     * Checks if the translation was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Gets the error message.
     *
     * @return the error message, or null if translation was successful
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the source message.
     *
     * @return the source message
     */
    public BaseMessage getSourceMessage() {
        return sourceMessage;
    }

    /**
     * Gets the translated message.
     *
     * @return the translated message, or null if translation failed
     */
    public BaseMessage getTranslatedMessage() {
        return translatedMessage;
    }

    /**
     * Gets the source format.
     *
     * @return the source format
     */
    public String getSourceFormat() {
        return sourceFormat;
    }

    /**
     * Gets the target format.
     *
     * @return the target format
     */
    public String getTargetFormat() {
        return targetFormat;
    }

    /**
     * Gets when the translation was performed.
     *
     * @return the translation timestamp
     */
    public Instant getTranslatedAt() {
        return translatedAt;
    }

    /**
     * Gets the translation time in milliseconds.
     *
     * @return the translation time in milliseconds
     */
    public long getTranslationTimeMillis() {
        return translationTimeMillis;
    }

    /**
     * Gets additional metadata about the translation.
     *
     * @return the metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Gets translation warnings.
     *
     * @return the warnings map
     */
    public Map<String, String> getWarnings() {
        return warnings;
    }

    /**
     * Checks if there are any warnings.
     *
     * @return true if there are warnings, false otherwise
     */
    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }

    /**
     * Gets the number of warnings.
     *
     * @return the number of warnings
     */
    public int getWarningCount() {
        return warnings != null ? warnings.size() : 0;
    }

    @Override
    public String toString() {
        return "TranslationResult{" +
                "translationId='" + translationId + '\'' +
                ", successful=" + successful +
                ", errorMessage='" + errorMessage + '\'' +
                ", sourceFormat='" + sourceFormat + '\'' +
                ", targetFormat='" + targetFormat + '\'' +
                ", translatedAt=" + translatedAt +
                ", translationTimeMillis=" + translationTimeMillis +
                ", warningCount=" + getWarningCount() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationResult that = (TranslationResult) o;
        return successful == that.successful &&
                translationTimeMillis == that.translationTimeMillis &&
                Objects.equals(translationId, that.translationId) &&
                Objects.equals(errorMessage, that.errorMessage) &&
                Objects.equals(sourceFormat, that.sourceFormat) &&
                Objects.equals(targetFormat, that.targetFormat) &&
                Objects.equals(translatedAt, that.translatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(translationId, successful, errorMessage, sourceFormat, targetFormat, translatedAt, translationTimeMillis);
    }
}
