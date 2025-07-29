package com.finqube.iso20022.core.translation;

/**
 * Exception thrown when translation operations fail.
 *
 * <p>This exception is thrown when there are problems with translation operations
 * such as format conversion, message transformation, or translation validation.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class TranslationException extends RuntimeException {

    private final String translationManagerId;
    private final String translationId;
    private final String sourceFormat;
    private final String targetFormat;
    private final TranslationErrorType errorType;

    /**
     * Constructs a new TranslationException with the specified detail message.
     *
     * @param message the detail message
     */
    public TranslationException(String message) {
        super(message);
        this.translationManagerId = null;
        this.translationId = null;
        this.sourceFormat = null;
        this.targetFormat = null;
        this.errorType = TranslationErrorType.GENERAL;
    }

    /**
     * Constructs a new TranslationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public TranslationException(String message, Throwable cause) {
        super(message, cause);
        this.translationManagerId = null;
        this.translationId = null;
        this.sourceFormat = null;
        this.targetFormat = null;
        this.errorType = TranslationErrorType.GENERAL;
    }

    /**
     * Constructs a new TranslationException with translation details.
     *
     * @param message the detail message
     * @param translationManagerId the translation manager identifier
     * @param translationId the translation identifier
     * @param sourceFormat the source format
     * @param targetFormat the target format
     * @param errorType the type of translation error
     */
    public TranslationException(String message, String translationManagerId, String translationId,
                              String sourceFormat, String targetFormat, TranslationErrorType errorType) {
        super(message);
        this.translationManagerId = translationManagerId;
        this.translationId = translationId;
        this.sourceFormat = sourceFormat;
        this.targetFormat = targetFormat;
        this.errorType = errorType;
    }

    /**
     * Constructs a new TranslationException with translation details and cause.
     *
     * @param message the detail message
     * @param translationManagerId the translation manager identifier
     * @param translationId the translation identifier
     * @param sourceFormat the source format
     * @param targetFormat the target format
     * @param errorType the type of translation error
     * @param cause the cause
     */
    public TranslationException(String message, String translationManagerId, String translationId,
                              String sourceFormat, String targetFormat, TranslationErrorType errorType, Throwable cause) {
        super(message, cause);
        this.translationManagerId = translationManagerId;
        this.translationId = translationId;
        this.sourceFormat = sourceFormat;
        this.targetFormat = targetFormat;
        this.errorType = errorType;
    }

    /**
     * Gets the translation manager identifier.
     *
     * @return the translation manager identifier, or null if not available
     */
    public String getTranslationManagerId() {
        return translationManagerId;
    }

    /**
     * Gets the translation identifier.
     *
     * @return the translation identifier, or null if not available
     */
    public String getTranslationId() {
        return translationId;
    }

    /**
     * Gets the source format.
     *
     * @return the source format, or null if not available
     */
    public String getSourceFormat() {
        return sourceFormat;
    }

    /**
     * Gets the target format.
     *
     * @return the target format, or null if not available
     */
    public String getTargetFormat() {
        return targetFormat;
    }

    /**
     * Gets the type of translation error.
     *
     * @return the error type
     */
    public TranslationErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return "TranslationException{" +
                "message='" + getMessage() + '\'' +
                ", translationManagerId='" + translationManagerId + '\'' +
                ", translationId='" + translationId + '\'' +
                ", sourceFormat='" + sourceFormat + '\'' +
                ", targetFormat='" + targetFormat + '\'' +
                ", errorType=" + errorType +
                '}';
    }

    /**
     * Translation error types.
     */
    public enum TranslationErrorType {
        GENERAL("General", "General translation error"),
        FORMAT_NOT_SUPPORTED("Format Not Supported", "Translation format is not supported"),
        VALIDATION_FAILED("Validation Failed", "Translation validation failed"),
        TIMEOUT("Timeout", "Translation operation timed out"),
        CACHE_ERROR("Cache Error", "Translation cache error"),
        MAPPING_ERROR("Mapping Error", "Translation mapping error"),
        TRANSFORMATION_ERROR("Transformation Error", "Message transformation error");

        private final String displayName;
        private final String description;

        TranslationErrorType(String displayName, String description) {
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
