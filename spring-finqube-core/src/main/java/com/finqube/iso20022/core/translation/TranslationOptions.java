package com.finqube.iso20022.core.translation;

import java.util.Map;
import java.util.Objects;

/**
 * Options for translation operations.
 *
 * <p>This class encapsulates configurable options for translation operations,
 * including caching settings, validation options, and custom parameters.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class TranslationOptions {

    private final boolean enableCaching;
    private final boolean enableValidation;
    private final boolean enableWarnings;
    private final long timeoutMillis;
    private final Map<String, Object> customOptions;

    /**
     * Constructs a new TranslationOptions with default values.
     */
    public TranslationOptions() {
        this(true, true, true, 30000, Map.of());
    }

    /**
     * Constructs a new TranslationOptions.
     *
     * @param enableCaching whether to enable caching
     * @param enableValidation whether to enable validation
     * @param enableWarnings whether to enable warnings
     * @param timeoutMillis the timeout in milliseconds
     * @param customOptions custom translation options
     */
    public TranslationOptions(boolean enableCaching, boolean enableValidation, boolean enableWarnings,
                            long timeoutMillis, Map<String, Object> customOptions) {
        this.enableCaching = enableCaching;
        this.enableValidation = enableValidation;
        this.enableWarnings = enableWarnings;
        this.timeoutMillis = timeoutMillis;
        this.customOptions = customOptions;
    }

    /**
     * Creates a builder for TranslationOptions.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Checks if caching is enabled.
     *
     * @return true if caching is enabled, false otherwise
     */
    public boolean isCachingEnabled() {
        return enableCaching;
    }

    /**
     * Checks if validation is enabled.
     *
     * @return true if validation is enabled, false otherwise
     */
    public boolean isValidationEnabled() {
        return enableValidation;
    }

    /**
     * Checks if warnings are enabled.
     *
     * @return true if warnings are enabled, false otherwise
     */
    public boolean isWarningsEnabled() {
        return enableWarnings;
    }

    /**
     * Gets the timeout in milliseconds.
     *
     * @return the timeout in milliseconds
     */
    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    /**
     * Gets custom translation options.
     *
     * @return the custom options map
     */
    public Map<String, Object> getCustomOptions() {
        return customOptions;
    }

    /**
     * Gets a custom option value.
     *
     * @param key the option key
     * @return the option value, or null if not found
     */
    public Object getCustomOption(String key) {
        return customOptions.get(key);
    }

    /**
     * Gets a custom option value with a default.
     *
     * @param key the option key
     * @param defaultValue the default value
     * @return the option value, or the default if not found
     */
    public Object getCustomOption(String key, Object defaultValue) {
        return customOptions.getOrDefault(key, defaultValue);
    }

    @Override
    public String toString() {
        return "TranslationOptions{" +
                "enableCaching=" + enableCaching +
                ", enableValidation=" + enableValidation +
                ", enableWarnings=" + enableWarnings +
                ", timeoutMillis=" + timeoutMillis +
                ", customOptions=" + customOptions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationOptions that = (TranslationOptions) o;
        return enableCaching == that.enableCaching &&
                enableValidation == that.enableValidation &&
                enableWarnings == that.enableWarnings &&
                timeoutMillis == that.timeoutMillis &&
                Objects.equals(customOptions, that.customOptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enableCaching, enableValidation, enableWarnings, timeoutMillis, customOptions);
    }

    /**
     * Builder for TranslationOptions.
     */
    public static class Builder {
        private boolean enableCaching = true;
        private boolean enableValidation = true;
        private boolean enableWarnings = true;
        private long timeoutMillis = 30000;
        private Map<String, Object> customOptions = Map.of();

        public Builder enableCaching(boolean enableCaching) {
            this.enableCaching = enableCaching;
            return this;
        }

        public Builder enableValidation(boolean enableValidation) {
            this.enableValidation = enableValidation;
            return this;
        }

        public Builder enableWarnings(boolean enableWarnings) {
            this.enableWarnings = enableWarnings;
            return this;
        }

        public Builder timeoutMillis(long timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
            return this;
        }

        public Builder customOptions(Map<String, Object> customOptions) {
            this.customOptions = customOptions;
            return this;
        }

        public Builder customOption(String key, Object value) {
            this.customOptions = Map.of(key, value);
            return this;
        }

        public TranslationOptions build() {
            return new TranslationOptions(enableCaching, enableValidation, enableWarnings, timeoutMillis, customOptions);
        }
    }
}
