package com.finqube.iso20022.core.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for configuration encryption functionality.
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@ConfigurationProperties(prefix = "iso20022.security.config.encryption")
@Validated
public class ConfigurationEncryptionProperties {

    /**
     * Whether configuration encryption is enabled.
     * Defaults to {@code true}.
     */
    private boolean enabled = true;

    /**
     * The encryption key (base64 encoded).
     * If not provided, a new key will be generated.
     */
    private String key;

    /**
     * The encryption algorithm to use.
     * Defaults to {@code "AES_256_GCM"}.
     */
    private String algorithm = "AES_256_GCM";

    /**
     * Whether to cache decrypted values.
     * Defaults to {@code true}.
     */
    private boolean cacheEnabled = true;

    /**
     * Maximum cache size for decrypted values.
     * Defaults to {@code 1000}.
     */
    private int maxCacheSize = 1000;

    /**
     * Gets whether configuration encryption is enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether configuration encryption is enabled.
     *
     * @param enabled true to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the encryption key.
     *
     * @return the encryption key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the encryption key.
     *
     * @param key the encryption key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the encryption algorithm.
     *
     * @return the encryption algorithm
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the encryption algorithm.
     *
     * @param algorithm the encryption algorithm
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Gets whether caching is enabled.
     *
     * @return true if caching is enabled, false otherwise
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    /**
     * Sets whether caching is enabled.
     *
     * @param cacheEnabled true to enable caching, false to disable
     */
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    /**
     * Gets the maximum cache size.
     *
     * @return the maximum cache size
     */
    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    /**
     * Sets the maximum cache size.
     *
     * @param maxCacheSize the maximum cache size
     */
    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }
}
