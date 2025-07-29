package com.finqube.iso20022.core.template;

import java.util.Map;
import java.util.Objects;

/**
 * Configuration options for ISO 20022 template operations.
 *
 * <p>This class provides configuration options for template operations such as
 * XML generation, formatting, and validation settings.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class TemplateOptions {

    private final boolean enableValidation;
    private final boolean enableFormatting;
    private final boolean enableIndentation;
    private final String encoding;
    private final Map<String, Object> customOptions;

    /**
     * Constructs a new TemplateOptions with default settings.
     */
    public TemplateOptions() {
        this(true, true, true, "UTF-8", Map.of());
    }

    /**
     * Constructs a new TemplateOptions with specified settings.
     *
     * @param enableValidation whether to enable validation
     * @param enableFormatting whether to enable formatting
     * @param enableIndentation whether to enable indentation
     * @param encoding the encoding to use
     * @param customOptions custom options
     */
    public TemplateOptions(boolean enableValidation, boolean enableFormatting, boolean enableIndentation,
                          String encoding, Map<String, Object> customOptions) {
        this.enableValidation = enableValidation;
        this.enableFormatting = enableFormatting;
        this.enableIndentation = enableIndentation;
        this.encoding = Objects.requireNonNull(encoding, "Encoding cannot be null");
        this.customOptions = Objects.requireNonNull(customOptions, "Custom options cannot be null");
    }

    /**
     * Gets whether validation is enabled.
     *
     * @return true if validation is enabled
     */
    public boolean isEnableValidation() {
        return enableValidation;
    }

    /**
     * Gets whether formatting is enabled.
     *
     * @return true if formatting is enabled
     */
    public boolean isEnableFormatting() {
        return enableFormatting;
    }

    /**
     * Gets whether indentation is enabled.
     *
     * @return true if indentation is enabled
     */
    public boolean isEnableIndentation() {
        return enableIndentation;
    }

    /**
     * Gets the encoding.
     *
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Gets custom options.
     *
     * @return custom options
     */
    public Map<String, Object> getCustomOptions() {
        return customOptions;
    }

    /**
     * Builder for TemplateOptions.
     */
    public static class Builder {
        private boolean enableValidation = true;
        private boolean enableFormatting = true;
        private boolean enableIndentation = true;
        private String encoding = "UTF-8";
        private Map<String, Object> customOptions = Map.of();

        public Builder enableValidation(boolean enableValidation) {
            this.enableValidation = enableValidation;
            return this;
        }

        public Builder enableFormatting(boolean enableFormatting) {
            this.enableFormatting = enableFormatting;
            return this;
        }

        public Builder enableIndentation(boolean enableIndentation) {
            this.enableIndentation = enableIndentation;
            return this;
        }

        public Builder encoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        public Builder customOptions(Map<String, Object> customOptions) {
            this.customOptions = customOptions;
            return this;
        }

        public TemplateOptions build() {
            return new TemplateOptions(enableValidation, enableFormatting, enableIndentation, encoding, customOptions);
        }
    }

    /**
     * Creates a new builder.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }
}
