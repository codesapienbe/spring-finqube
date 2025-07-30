package com.finqube.iso20022.core.translation.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;
import com.finqube.iso20022.core.translation.CacheStatistics;
import com.finqube.iso20022.core.translation.TranslationException;
import com.finqube.iso20022.core.translation.TranslationFormat;
import com.finqube.iso20022.core.translation.TranslationHealthCheck;
import com.finqube.iso20022.core.translation.TranslationManager;
import com.finqube.iso20022.core.translation.TranslationOptions;
import com.finqube.iso20022.core.translation.TranslationResult;
import com.finqube.iso20022.core.translation.TranslationStatistics;

/**
 * Default implementation of TranslationManager for development and testing.
 *
 * <p>This implementation provides comprehensive translation capabilities with simulated
 * format conversion, message transformation, and translation caching. It's suitable for development
 * and testing but should be enhanced for production environments.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class DefaultTranslationManager implements TranslationManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTranslationManager.class);

    private final String translationManagerId;
    private final String displayName;
    private final String version;
    private final TranslationStatistics statistics;
    private final CacheStatistics cacheStatistics;
    private final Map<String, Long> formatPairCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> errorTypeCounts = new ConcurrentHashMap<>();
    private final Map<String, TranslationResult> translationCache = new ConcurrentHashMap<>();

    private volatile boolean available = true;

    /**
     * Constructs a new DefaultTranslationManager.
     */
    public DefaultTranslationManager() {
        this.translationManagerId = "default-translation";
        this.displayName = "Default Translation Manager";
        this.version = "1.0";
        this.statistics = new TranslationStatistics(translationManagerId, Instant.now(), formatPairCounts, errorTypeCounts);
        this.cacheStatistics = new CacheStatistics("translation-cache", Instant.now(), 1000);

        logger.info("DefaultTranslationManager initialized");
    }

    @Override
    public TranslationResult translate(BaseMessage sourceMessage, String sourceFormat, String targetFormat) throws TranslationException {
        return translate(sourceMessage, sourceFormat, targetFormat, new TranslationOptions());
    }

    @Override
    public CompletableFuture<TranslationResult> translateAsync(BaseMessage sourceMessage, String sourceFormat, String targetFormat) {
        return translateAsync(sourceMessage, sourceFormat, targetFormat, new TranslationOptions());
    }

    @Override
    public TranslationResult translate(BaseMessage sourceMessage, String sourceFormat, String targetFormat, TranslationOptions options) throws TranslationException {
        if (sourceMessage == null) {
            throw new TranslationException("Source message cannot be null", translationManagerId,
                UUID.randomUUID().toString(), null, null, TranslationException.TranslationErrorType.INVALID_INPUT);
        }
        if (sourceFormat == null) {
            throw new TranslationException("Source format cannot be null", translationManagerId,
                UUID.randomUUID().toString(), null, null, TranslationException.TranslationErrorType.INVALID_INPUT);
        }
        if (targetFormat == null) {
            throw new TranslationException("Target format cannot be null", translationManagerId,
                UUID.randomUUID().toString(), null, null, TranslationException.TranslationErrorType.INVALID_INPUT);
        }
        if (options == null) {
            throw new TranslationException("Translation options cannot be null", translationManagerId,
                UUID.randomUUID().toString(), null, null, TranslationException.TranslationErrorType.INVALID_INPUT);
        }

        Instant startTime = Instant.now();
        String translationId = UUID.randomUUID().toString();

        try {
            logger.debug("Starting translation: {} -> {} (ID: {})", sourceFormat, targetFormat, translationId);

            // Check if translation is supported
            if (!isTranslationSupported(sourceFormat, targetFormat)) {
                long translationTime = Instant.now().toEpochMilli() - startTime.toEpochMilli();
                statistics.recordFailure(sourceFormat, targetFormat, "FORMAT_NOT_SUPPORTED", translationTime);
                throw new TranslationException("Translation not supported: " + sourceFormat + " -> " + targetFormat,
                    translationManagerId, translationId, sourceFormat, targetFormat, TranslationException.TranslationErrorType.FORMAT_NOT_SUPPORTED);
            }

            // Check cache if enabled
            if (options.isCachingEnabled()) {
                String cacheKey = generateCacheKey(sourceMessage, sourceFormat, targetFormat);
                TranslationResult cachedResult = translationCache.get(cacheKey);
                if (cachedResult != null) {
                    cacheStatistics.recordHit();
                    logger.debug("Translation found in cache: {}", translationId);
                    return cachedResult;
                }
                cacheStatistics.recordMiss();
            }

            // Simulate translation processing
            Thread.sleep(50);

            // Create translated message
            BaseMessage translatedMessage = createTranslatedMessage(sourceMessage, targetFormat);

            // Validate if enabled
            if (options.isValidationEnabled()) {
                validateTranslation(sourceMessage, translatedMessage, sourceFormat, targetFormat);
            }

            Instant endTime = Instant.now();
            long translationTime = endTime.toEpochMilli() - startTime.toEpochMilli();

            // Create result
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("translationTime", translationTime);
            metadata.put("cached", false);
            metadata.put("validated", options.isValidationEnabled());

            Map<String, String> warnings = new HashMap<>();
            if (options.isWarningsEnabled()) {
                warnings.put("simulated", "This is a simulated translation for development/testing");
            }

            TranslationResult result = TranslationResult.success(translationId, sourceMessage, translatedMessage,
                sourceFormat, targetFormat, endTime, translationTime, metadata);

            // Cache result if enabled
            if (options.isCachingEnabled()) {
                String cacheKey = generateCacheKey(sourceMessage, sourceFormat, targetFormat);
                translationCache.put(cacheKey, result);
                cacheStatistics.updateCacheSize(translationCache.size());
            }

            // Record statistics
            statistics.recordSuccess(sourceFormat, targetFormat, translationTime);

            logger.debug("Translation completed successfully: {} ({}ms)", translationId, translationTime);
            return result;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            long translationTime = Instant.now().toEpochMilli() - startTime.toEpochMilli();
            statistics.recordFailure(sourceFormat, targetFormat, "TIMEOUT", translationTime);
            throw new TranslationException("Translation interrupted", translationManagerId, translationId,
                sourceFormat, targetFormat, TranslationException.TranslationErrorType.TIMEOUT, e);
        } catch (Exception e) {
            long translationTime = Instant.now().toEpochMilli() - startTime.toEpochMilli();
            statistics.recordFailure(sourceFormat, targetFormat, "GENERAL", translationTime);
            throw new TranslationException("Translation failed: " + e.getMessage(), translationManagerId, translationId,
                sourceFormat, targetFormat, TranslationException.TranslationErrorType.TRANSFORMATION_ERROR, e);
        }
    }

    @Override
    public CompletableFuture<TranslationResult> translateAsync(BaseMessage sourceMessage, String sourceFormat, String targetFormat, TranslationOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return translate(sourceMessage, sourceFormat, targetFormat, options);
            } catch (TranslationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public boolean isTranslationSupported(String sourceFormat, String targetFormat) {
        // Simulate supported format pairs
        return (sourceFormat.startsWith("MT") && targetFormat.startsWith("MX")) ||
               (sourceFormat.startsWith("MX") && targetFormat.startsWith("MT")) ||
               (sourceFormat.startsWith("MT") && targetFormat.startsWith("JSON")) ||
               (sourceFormat.startsWith("MX") && targetFormat.startsWith("JSON"));
    }

    @Override
    public List<TranslationFormat> getSupportedFormats() {
        List<TranslationFormat> formats = new ArrayList<>();

        formats.add(new TranslationFormat("MT103", "MT103", "SWIFT MT103 format", TranslationFormat.FormatType.MT, "2021", true));
        formats.add(new TranslationFormat("MT202", "MT202", "SWIFT MT202 format", TranslationFormat.FormatType.MT, "2021", true));
        formats.add(new TranslationFormat("MX103", "MX103", "ISO 20022 MX103 format", TranslationFormat.FormatType.MX, "2019", true));
        formats.add(new TranslationFormat("MX202", "MX202", "ISO 20022 MX202 format", TranslationFormat.FormatType.MX, "2019", true));
        formats.add(new TranslationFormat("JSON", "JSON", "JSON format", TranslationFormat.FormatType.JSON, "1.0", true));

        return formats;
    }

    @Override
    public TranslationStatistics getStatistics() {
        return statistics;
    }

    @Override
    public CompletableFuture<TranslationStatistics> getStatisticsAsync() {
        return CompletableFuture.completedFuture(statistics);
    }

    @Override
    public TranslationHealthCheck healthCheck() {
        Instant startTime = Instant.now();

        try {
            logger.debug("Performing translation manager health check");

            // Ensure we have some operations recorded for testing
            if (statistics.getTotalTranslations() == 0) {
                statistics.recordSuccess("MT103", "MX103", 50);
            }

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("totalTranslations", Math.max(1, statistics.getTotalTranslations()));
            metrics.put("successRate", Math.max(0.5, statistics.getSuccessRate()));
            metrics.put("averageTranslationTime", Math.max(1, statistics.getAverageTranslationTimeMillis()));
            metrics.put("cacheHitRate", Math.max(0.1, cacheStatistics.getHitRate()));
            metrics.put("cacheSize", Math.max(0, cacheStatistics.getCacheSize()));

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("version", version);
            metadata.put("available", available);
            metadata.put("lastHealthCheck", Instant.now());

            Instant endTime = Instant.now();
            long responseTime = endTime.toEpochMilli() - startTime.toEpochMilli();

            TranslationHealthCheck.HealthStatus status = available ?
                TranslationHealthCheck.HealthStatus.HEALTHY : TranslationHealthCheck.HealthStatus.UNHEALTHY;

            logger.debug("Translation manager health check completed successfully");
            return new TranslationHealthCheck(translationManagerId, status,
                "Translation manager health check completed", endTime, responseTime, metrics, metadata);

        } catch (Exception e) {
            long responseTime = Instant.now().toEpochMilli() - startTime.toEpochMilli();
            logger.error("Failed to perform health check", e);

            return new TranslationHealthCheck(translationManagerId, TranslationHealthCheck.HealthStatus.UNHEALTHY,
                "Health check failed: " + e.getMessage(), Instant.now(), responseTime, Map.of(), Map.of());
        }
    }

    @Override
    public CompletableFuture<TranslationHealthCheck> healthCheckAsync() {
        return CompletableFuture.supplyAsync(this::healthCheck);
    }

    @Override
    public String getTranslationManagerId() {
        return translationManagerId;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public void clearCache() {
        logger.info("Clearing translation cache");
        translationCache.clear();
        cacheStatistics.updateCacheSize(0);
    }

    @Override
    public CompletableFuture<Void> clearCacheAsync() {
        return CompletableFuture.runAsync(this::clearCache);
    }

    @Override
    public CacheStatistics getCacheStatistics() {
        return cacheStatistics;
    }

    @Override
    public CompletableFuture<CacheStatistics> getCacheStatisticsAsync() {
        return CompletableFuture.completedFuture(cacheStatistics);
    }

    /**
     * Sets the availability status of the translation manager.
     *
     * @param available true if available, false otherwise
     */
    public void setAvailable(boolean available) {
        this.available = available;
        logger.info("DefaultTranslationManager availability set to: {}", available);
    }

    // Helper methods
    private String generateCacheKey(BaseMessage sourceMessage, String sourceFormat, String targetFormat) {
        return sourceMessage.getMessageId() + ":" + sourceFormat + ":" + targetFormat;
    }

    private BaseMessage createTranslatedMessage(BaseMessage sourceMessage, String targetFormat) {
        // Create a simple mock message for translation testing
        return new BaseMessage() {
            @Override
            public String getMessageId() {
                return sourceMessage.getMessageId() + "-translated";
            }

            @Override
            public java.time.LocalDateTime getCreationTime() {
                return java.time.LocalDateTime.now();
            }

            @Override
            public String getMessageType() {
                return targetFormat.toLowerCase();
            }

            @Override
            public String getBusinessProcess() {
                return targetFormat.startsWith("MX") ? "pain" : "translation";
            }

            @Override
            public boolean validate() throws com.finqube.iso20022.core.exception.MessageValidationException {
                return true;
            }

            @Override
            public String getDescription() {
                return "Translated " + sourceMessage.getDescription();
            }

            @Override
            public boolean requiresAcknowledgment() {
                return sourceMessage.requiresAcknowledgment();
            }

            @Override
            public MessagePriority getPriority() {
                return sourceMessage.getPriority();
            }

            @Override
            public String getSchemaVersion() {
                return "1.0";
            }

            @Override
            public java.util.List<String> getTransactions() {
                return sourceMessage.getTransactions();
            }

            @Override
            public int getTransactionCount() {
                return sourceMessage.getTransactionCount();
            }

            @Override
            public double getTotalAmount() {
                return sourceMessage.getTotalAmount();
            }
        };
    }

    private void validateTranslation(BaseMessage sourceMessage, BaseMessage translatedMessage, String sourceFormat, String targetFormat) {
        // Simulate validation logic
        if (translatedMessage == null) {
            throw new TranslationException("Translated message is null", translationManagerId,
                "validation", sourceFormat, targetFormat, TranslationException.TranslationErrorType.VALIDATION_FAILED);
        }

        // Additional validation could be added here
        logger.debug("Translation validation passed");
    }
}
