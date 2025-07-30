package com.finqube.iso20022.core.translation;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;
import com.finqube.iso20022.core.message.pain.Pain001Message;
import com.finqube.iso20022.core.translation.impl.DefaultTranslationManager;

/**
 * Unit tests for DefaultTranslationManager.
 *
 * <p>This test class validates the translation manager functionality,
 * including format conversion, message transformation, and translation caching.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@DisplayName("DefaultTranslationManager Tests")
class DefaultTranslationManagerTest {

    private DefaultTranslationManager translationManager;
    private BaseMessage testMessage;

    @BeforeEach
    void setUp() {
        translationManager = new DefaultTranslationManager();
        testMessage = createValidMessage();
    }

    @Test
    @DisplayName("Should translate MT103 to MX103 successfully")
    void shouldTranslateMT103ToMX103Successfully() throws TranslationException {
        // When
        TranslationResult result = translationManager.translate(testMessage, "MT103", "MX103");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getSourceFormat()).isEqualTo("MT103");
        assertThat(result.getTargetFormat()).isEqualTo("MX103");
        assertThat(result.getTranslatedMessage()).isNotNull();
        assertThat(result.getTranslatedMessage().getMessageType()).isEqualTo("mx103");
        assertThat(result.getTranslationTimeMillis()).isPositive();
    }

    @Test
    @DisplayName("Should translate MT103 to MX103 asynchronously successfully")
    void shouldTranslateMT103ToMX103AsynchronouslySuccessfully() throws Exception {
        // When
        CompletableFuture<TranslationResult> future = translationManager.translateAsync(testMessage, "MT103", "MX103");
        TranslationResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getSourceFormat()).isEqualTo("MT103");
        assertThat(result.getTargetFormat()).isEqualTo("MX103");
    }

    @Test
    @DisplayName("Should translate MX103 to MT103 successfully")
    void shouldTranslateMX103ToMT103Successfully() throws TranslationException {
        // When
        TranslationResult result = translationManager.translate(testMessage, "MX103", "MT103");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getSourceFormat()).isEqualTo("MX103");
        assertThat(result.getTargetFormat()).isEqualTo("MT103");
        assertThat(result.getTranslatedMessage()).isNotNull();
    }

    @Test
    @DisplayName("Should translate with custom options successfully")
    void shouldTranslateWithCustomOptionsSuccessfully() throws TranslationException {
        // Given
        TranslationOptions options = TranslationOptions.builder()
            .enableCaching(true)
            .enableValidation(true)
            .enableWarnings(true)
            .timeoutMillis(60000)
            .build();

        // When
        TranslationResult result = translationManager.translate(testMessage, "MT103", "MX103", options);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getMetadata()).containsKey("validated");
        assertThat(result.getMetadata().get("validated")).isEqualTo(true);
    }

    @Test
    @DisplayName("Should translate with custom options asynchronously successfully")
    void shouldTranslateWithCustomOptionsAsynchronouslySuccessfully() throws Exception {
        // Given
        TranslationOptions options = TranslationOptions.builder()
            .enableCaching(false)
            .enableValidation(true)
            .build();

        // When
        CompletableFuture<TranslationResult> future = translationManager.translateAsync(testMessage, "MT103", "MX103", options);
        TranslationResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccessful()).isTrue();
    }

    @Test
    @DisplayName("Should check translation support correctly")
    void shouldCheckTranslationSupportCorrectly() {
        // When & Then
        assertThat(translationManager.isTranslationSupported("MT103", "MX103")).isTrue();
        assertThat(translationManager.isTranslationSupported("MX103", "MT103")).isTrue();
        assertThat(translationManager.isTranslationSupported("MT103", "JSON")).isTrue();
        assertThat(translationManager.isTranslationSupported("MX103", "JSON")).isTrue();
        assertThat(translationManager.isTranslationSupported("MT103", "CSV")).isFalse();
        assertThat(translationManager.isTranslationSupported("UNKNOWN", "MX103")).isFalse();
    }

    @Test
    @DisplayName("Should get supported formats successfully")
    void shouldGetSupportedFormatsSuccessfully() {
        // When
        List<TranslationFormat> formats = translationManager.getSupportedFormats();

        // Then
        assertThat(formats).isNotEmpty();
        assertThat(formats).extracting(TranslationFormat::getFormatId)
            .contains("MT103", "MT202", "MX103", "MX202", "JSON");
        assertThat(formats).allMatch(TranslationFormat::isSupported);
    }

    @Test
    @DisplayName("Should provide translation manager information")
    void shouldProvideTranslationManagerInformation() {
        // When & Then
        assertThat(translationManager.getTranslationManagerId()).isEqualTo("default-translation");
        assertThat(translationManager.getDisplayName()).isEqualTo("Default Translation Manager");
        assertThat(translationManager.getVersion()).isEqualTo("1.0");
        assertThat(translationManager.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Should provide translation statistics")
    void shouldProvideTranslationStatistics() throws TranslationException {
        // Given
        translationManager.translate(testMessage, "MT103", "MX103");
        translationManager.translate(testMessage, "MX103", "MT103");

        // When
        TranslationStatistics stats = translationManager.getStatistics();

        // Then
        assertThat(stats.getTranslationManagerId()).isEqualTo("default-translation");
        assertThat(stats.getTotalTranslations()).isEqualTo(2);
        assertThat(stats.getSuccessfulTranslations()).isEqualTo(2);
        assertThat(stats.getFailedTranslations()).isEqualTo(0);
        assertThat(stats.getSuccessRate()).isEqualTo(100.0);
        assertThat(stats.getAverageTranslationTimeMillis()).isPositive();
        assertThat(stats.getFormatPairCounts()).containsKeys("MT103->MX103", "MX103->MT103");
    }

    @Test
    @DisplayName("Should provide translation statistics asynchronously")
    void shouldProvideTranslationStatisticsAsynchronously() throws Exception {
        // When
        CompletableFuture<TranslationStatistics> future = translationManager.getStatisticsAsync();
        TranslationStatistics stats = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(stats).isNotNull();
        assertThat(stats.getTranslationManagerId()).isEqualTo("default-translation");
    }

    @Test
    @DisplayName("Should provide health check information")
    void shouldProvideHealthCheckInformation() {
        // When
        TranslationHealthCheck healthCheck = translationManager.healthCheck();

        // Then
        assertThat(healthCheck.getTranslationManagerId()).isEqualTo("default-translation");
        assertThat(healthCheck.isHealthy()).isTrue();
        assertThat(healthCheck.getResponseTimeMillis()).isPositive();
        assertThat(healthCheck.getMetrics()).isNotEmpty();
    }

    @Test
    @DisplayName("Should provide health check information asynchronously")
    void shouldProvideHealthCheckInformationAsynchronously() throws Exception {
        // When
        CompletableFuture<TranslationHealthCheck> future = translationManager.healthCheckAsync();
        TranslationHealthCheck healthCheck = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(healthCheck).isNotNull();
        assertThat(healthCheck.isHealthy()).isTrue();
    }

    @Test
    @DisplayName("Should clear cache successfully")
    void shouldClearCacheSuccessfully() throws TranslationException {
        // Given
        TranslationOptions options = TranslationOptions.builder().enableCaching(true).build();
        translationManager.translate(testMessage, "MT103", "MX103", options);

        // When
        translationManager.clearCache();

        // Then
        CacheStatistics cacheStats = translationManager.getCacheStatistics();
        assertThat(cacheStats.getCacheSize()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should clear cache asynchronously successfully")
    void shouldClearCacheAsynchronouslySuccessfully() throws Exception {
        // When
        CompletableFuture<Void> future = translationManager.clearCacheAsync();
        future.get(5, TimeUnit.SECONDS);

        // Then
        CacheStatistics cacheStats = translationManager.getCacheStatistics();
        assertThat(cacheStats.getCacheSize()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should provide cache statistics")
    void shouldProvideCacheStatistics() throws TranslationException {
        // Given
        TranslationOptions options = TranslationOptions.builder().enableCaching(true).build();
        translationManager.translate(testMessage, "MT103", "MX103", options);

        // When
        CacheStatistics cacheStats = translationManager.getCacheStatistics();

        // Then
        assertThat(cacheStats.getCacheId()).isEqualTo("translation-cache");
        assertThat(cacheStats.getCacheSize()).isGreaterThan(0);
        assertThat(cacheStats.getMaxCacheSize()).isEqualTo(1000);
        assertThat(cacheStats.getHitRate()).isGreaterThanOrEqualTo(0.0);
        assertThat(cacheStats.getMissRate()).isGreaterThanOrEqualTo(0.0);
    }

    @Test
    @DisplayName("Should provide cache statistics asynchronously")
    void shouldProvideCacheStatisticsAsynchronously() throws Exception {
        // When
        CompletableFuture<CacheStatistics> future = translationManager.getCacheStatisticsAsync();
        CacheStatistics cacheStats = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(cacheStats).isNotNull();
        assertThat(cacheStats.getCacheId()).isEqualTo("translation-cache");
    }

    @Test
    @DisplayName("Should throw exception for null source message")
    void shouldThrowExceptionForNullSourceMessage() {
        // When & Then
        assertThatThrownBy(() -> translationManager.translate(null, "MT103", "MX103"))
            .isInstanceOf(TranslationException.class)
            .hasMessageContaining("Source message cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null source format")
    void shouldThrowExceptionForNullSourceFormat() {
        // When & Then
        assertThatThrownBy(() -> translationManager.translate(testMessage, null, "MX103"))
            .isInstanceOf(TranslationException.class)
            .hasMessageContaining("Source format cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null target format")
    void shouldThrowExceptionForNullTargetFormat() {
        // When & Then
        assertThatThrownBy(() -> translationManager.translate(testMessage, "MT103", null))
            .isInstanceOf(TranslationException.class)
            .hasMessageContaining("Target format cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null translation options")
    void shouldThrowExceptionForNullTranslationOptions() {
        // When & Then
        assertThatThrownBy(() -> translationManager.translate(testMessage, "MT103", "MX103", null))
            .isInstanceOf(TranslationException.class)
            .hasMessageContaining("Translation options cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for unsupported translation")
    void shouldThrowExceptionForUnsupportedTranslation() {
        // When & Then
        assertThatThrownBy(() -> translationManager.translate(testMessage, "MT103", "CSV"))
            .isInstanceOf(TranslationException.class)
            .hasMessageContaining("Translation not supported");
    }

    @Test
    @DisplayName("Should handle availability changes")
    void shouldHandleAvailabilityChanges() {
        // Given
        assertThat(translationManager.isAvailable()).isTrue();

        // When
        translationManager.setAvailable(false);

        // Then
        assertThat(translationManager.isAvailable()).isFalse();

        TranslationHealthCheck healthCheck = translationManager.healthCheck();
        assertThat(healthCheck.isHealthy()).isFalse();
    }

    @Test
    @DisplayName("Should use caching when enabled")
    void shouldUseCachingWhenEnabled() throws TranslationException {
        // Given
        TranslationOptions options = TranslationOptions.builder().enableCaching(true).build();

        // When - First translation
        TranslationResult result1 = translationManager.translate(testMessage, "MT103", "MX103", options);

        // When - Second translation (should be cached)
        TranslationResult result2 = translationManager.translate(testMessage, "MT103", "MX103", options);

        // Then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1.getTranslationId()).isEqualTo(result2.getTranslationId());

        CacheStatistics cacheStats = translationManager.getCacheStatistics();
        assertThat(cacheStats.getCacheHits()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should not use caching when disabled")
    void shouldNotUseCachingWhenDisabled() throws TranslationException {
        // Given
        TranslationOptions options = TranslationOptions.builder().enableCaching(false).build();

        // When - First translation
        TranslationResult result1 = translationManager.translate(testMessage, "MT103", "MX103", options);

        // When - Second translation (should not be cached)
        TranslationResult result2 = translationManager.translate(testMessage, "MT103", "MX103", options);

        // Then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1.getTranslationId()).isNotEqualTo(result2.getTranslationId());

        CacheStatistics cacheStats = translationManager.getCacheStatistics();
        assertThat(cacheStats.getCacheHits()).isEqualTo(0);
    }

    // Helper methods
    private BaseMessage createValidMessage() {
        return new Pain001Message("TRANS001", List.of(), 1, 1000.00) {
            @Override
            public String getMessageType() {
                return "pain.001";
            }

            @Override
            public String getBusinessProcess() {
                return "pain";
            }

            @Override
            public MessagePriority getPriority() {
                return MessagePriority.NORMAL;
            }

            @Override
            public String getDescription() {
                return "Test translation message";
            }
        };
    }
}
