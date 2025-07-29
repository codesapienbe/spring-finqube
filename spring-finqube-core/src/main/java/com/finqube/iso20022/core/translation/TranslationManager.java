package com.finqube.iso20022.core.translation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.finqube.iso20022.core.message.BaseMessage;

/**
 * Translation manager for ISO 20022 message translation and format conversion.
 *
 * <p>This interface defines the contract for message translation operations,
 * including format conversion, message transformation, and translation caching.</p>
 *
 * <p>The translation manager supports:</p>
 * <ul>
 *   <li>Message format conversion (MT to MX, MX to MT)</li>
 *   <li>Message transformation and mapping</li>
 *   <li>Translation validation and error handling</li>
 *   <li>Translation caching and performance optimization</li>
 *   <li>Translation monitoring and statistics</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * @Autowired
 * private TranslationManager translationManager;
 *
 * // Convert MT message to MX format
 * TranslationResult result = translationManager.translate(mtMessage, "MT103", "MX103");
 *
 * // Get translation statistics
 * TranslationStatistics stats = translationManager.getStatistics();
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface TranslationManager {

    /**
     * Translates a message from one format to another.
     *
     * @param sourceMessage the source message to translate
     * @param sourceFormat the source format (e.g., "MT103", "MX103")
     * @param targetFormat the target format (e.g., "MX103", "MT103")
     * @return the translation result
     * @throws TranslationException if translation fails
     */
    TranslationResult translate(BaseMessage sourceMessage, String sourceFormat, String targetFormat) throws TranslationException;

    /**
     * Translates a message from one format to another asynchronously.
     *
     * @param sourceMessage the source message to translate
     * @param sourceFormat the source format
     * @param targetFormat the target format
     * @return a CompletableFuture that completes with the translation result
     */
    CompletableFuture<TranslationResult> translateAsync(BaseMessage sourceMessage, String sourceFormat, String targetFormat);

    /**
     * Translates a message with custom translation options.
     *
     * @param sourceMessage the source message to translate
     * @param sourceFormat the source format
     * @param targetFormat the target format
     * @param options custom translation options
     * @return the translation result
     * @throws TranslationException if translation fails
     */
    TranslationResult translate(BaseMessage sourceMessage, String sourceFormat, String targetFormat, TranslationOptions options) throws TranslationException;

    /**
     * Translates a message with custom translation options asynchronously.
     *
     * @param sourceMessage the source message to translate
     * @param sourceFormat the source format
     * @param targetFormat the target format
     * @param options custom translation options
     * @return a CompletableFuture that completes with the translation result
     */
    CompletableFuture<TranslationResult> translateAsync(BaseMessage sourceMessage, String sourceFormat, String targetFormat, TranslationOptions options);

    /**
     * Validates if a translation is supported.
     *
     * @param sourceFormat the source format
     * @param targetFormat the target format
     * @return true if translation is supported, false otherwise
     */
    boolean isTranslationSupported(String sourceFormat, String targetFormat);

    /**
     * Gets supported translation formats.
     *
     * @return list of supported translation formats
     */
    List<TranslationFormat> getSupportedFormats();

    /**
     * Gets translation statistics.
     *
     * @return the translation statistics
     */
    TranslationStatistics getStatistics();

    /**
     * Gets translation statistics asynchronously.
     *
     * @return a CompletableFuture that completes with the translation statistics
     */
    CompletableFuture<TranslationStatistics> getStatisticsAsync();

    /**
     * Performs a health check on the translation manager.
     *
     * @return the health check result
     */
    TranslationHealthCheck healthCheck();

    /**
     * Performs a health check on the translation manager asynchronously.
     *
     * @return a CompletableFuture that completes with the health check result
     */
    CompletableFuture<TranslationHealthCheck> healthCheckAsync();

    /**
     * Gets the translation manager identifier.
     *
     * @return the translation manager identifier
     */
    String getTranslationManagerId();

    /**
     * Gets the translation manager display name.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Gets the translation manager version.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Checks if the translation manager is available.
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();

    /**
     * Clears the translation cache.
     */
    void clearCache();

    /**
     * Clears the translation cache asynchronously.
     *
     * @return a CompletableFuture that completes when the cache is cleared
     */
    CompletableFuture<Void> clearCacheAsync();

    /**
     * Gets cache statistics.
     *
     * @return the cache statistics
     */
    CacheStatistics getCacheStatistics();

    /**
     * Gets cache statistics asynchronously.
     *
     * @return a CompletableFuture that completes with the cache statistics
     */
    CompletableFuture<CacheStatistics> getCacheStatisticsAsync();
}
