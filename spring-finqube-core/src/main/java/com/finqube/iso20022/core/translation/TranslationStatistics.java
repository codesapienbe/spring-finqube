package com.finqube.iso20022.core.translation;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Statistics and metrics for translation operations.
 *
 * <p>This class tracks various performance metrics and statistics for translation operations,
 * including translation counts, success rates, timing, and format-specific metrics.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class TranslationStatistics {

    private final String translationManagerId;
    private final Instant startTime;
    private final AtomicLong totalTranslations;
    private final AtomicLong successfulTranslations;
    private final AtomicLong failedTranslations;
    private final AtomicLong totalTranslationTimeMillis;
    private final AtomicLong minTranslationTimeMillis;
    private final AtomicLong maxTranslationTimeMillis;
    private final Map<String, Long> formatPairCounts;
    private final Map<String, Long> errorTypeCounts;

    /**
     * Constructs a new TranslationStatistics.
     *
     * @param translationManagerId the translation manager identifier
     * @param startTime when statistics collection started
     * @param formatPairCounts map of format pairs to counts
     * @param errorTypeCounts map of error types to counts
     */
    public TranslationStatistics(String translationManagerId, Instant startTime,
                               Map<String, Long> formatPairCounts, Map<String, Long> errorTypeCounts) {
        this.translationManagerId = Objects.requireNonNull(translationManagerId, "Translation manager ID cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.totalTranslations = new AtomicLong(0);
        this.successfulTranslations = new AtomicLong(0);
        this.failedTranslations = new AtomicLong(0);
        this.totalTranslationTimeMillis = new AtomicLong(0);
        this.minTranslationTimeMillis = new AtomicLong(Long.MAX_VALUE);
        this.maxTranslationTimeMillis = new AtomicLong(0);
        this.formatPairCounts = formatPairCounts;
        this.errorTypeCounts = errorTypeCounts;
    }

    /**
     * Records a successful translation.
     *
     * @param sourceFormat the source format
     * @param targetFormat the target format
     * @param translationTimeMillis the translation time in milliseconds
     */
    public void recordSuccess(String sourceFormat, String targetFormat, long translationTimeMillis) {
        totalTranslations.incrementAndGet();
        successfulTranslations.incrementAndGet();
        totalTranslationTimeMillis.addAndGet(translationTimeMillis);
        updateMinMaxTranslationTime(translationTimeMillis);

        String formatPair = sourceFormat + "->" + targetFormat;
        formatPairCounts.merge(formatPair, 1L, Long::sum);
    }

    /**
     * Records a failed translation.
     *
     * @param sourceFormat the source format
     * @param targetFormat the target format
     * @param errorType the type of error
     * @param translationTimeMillis the translation time in milliseconds
     */
    public void recordFailure(String sourceFormat, String targetFormat, String errorType, long translationTimeMillis) {
        totalTranslations.incrementAndGet();
        failedTranslations.incrementAndGet();
        totalTranslationTimeMillis.addAndGet(translationTimeMillis);
        updateMinMaxTranslationTime(translationTimeMillis);

        String formatPair = sourceFormat + "->" + targetFormat;
        formatPairCounts.merge(formatPair, 1L, Long::sum);

        if (errorType != null) {
            errorTypeCounts.merge(errorType, 1L, Long::sum);
        }
    }

    private void updateMinMaxTranslationTime(long translationTimeMillis) {
        minTranslationTimeMillis.updateAndGet(current -> Math.min(current, translationTimeMillis));
        maxTranslationTimeMillis.updateAndGet(current -> Math.max(current, translationTimeMillis));
    }

    /**
     * Gets the translation manager identifier.
     *
     * @return the translation manager identifier
     */
    public String getTranslationManagerId() {
        return translationManagerId;
    }

    /**
     * Gets when statistics collection started.
     *
     * @return the start time
     */
    public Instant getStartTime() {
        return startTime;
    }

    /**
     * Gets the total number of translations.
     *
     * @return the total translations count
     */
    public long getTotalTranslations() {
        return totalTranslations.get();
    }

    /**
     * Gets the number of successful translations.
     *
     * @return the successful translations count
     */
    public long getSuccessfulTranslations() {
        return successfulTranslations.get();
    }

    /**
     * Gets the number of failed translations.
     *
     * @return the failed translations count
     */
    public long getFailedTranslations() {
        return failedTranslations.get();
    }

    /**
     * Gets the success rate as a percentage.
     *
     * @return the success rate percentage
     */
    public double getSuccessRate() {
        long total = totalTranslations.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) successfulTranslations.get() / total * 100.0;
    }

    /**
     * Gets the failure rate as a percentage.
     *
     * @return the failure rate percentage
     */
    public double getFailureRate() {
        long total = totalTranslations.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) failedTranslations.get() / total * 100.0;
    }

    /**
     * Gets the average translation time in milliseconds.
     *
     * @return the average translation time
     */
    public double getAverageTranslationTimeMillis() {
        long total = totalTranslations.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) totalTranslationTimeMillis.get() / total;
    }

    /**
     * Gets the minimum translation time in milliseconds.
     *
     * @return the minimum translation time
     */
    public long getMinTranslationTimeMillis() {
        long min = minTranslationTimeMillis.get();
        return min == Long.MAX_VALUE ? 0 : min;
    }

    /**
     * Gets the maximum translation time in milliseconds.
     *
     * @return the maximum translation time
     */
    public long getMaxTranslationTimeMillis() {
        return maxTranslationTimeMillis.get();
    }

    /**
     * Gets the total translation time in milliseconds.
     *
     * @return the total translation time
     */
    public long getTotalTranslationTimeMillis() {
        return totalTranslationTimeMillis.get();
    }

    /**
     * Gets the format pair counts.
     *
     * @return the format pair counts map
     */
    public Map<String, Long> getFormatPairCounts() {
        return formatPairCounts;
    }

    /**
     * Gets the error type counts.
     *
     * @return the error type counts map
     */
    public Map<String, Long> getErrorTypeCounts() {
        return errorTypeCounts;
    }

    /**
     * Gets the uptime in milliseconds.
     *
     * @return the uptime in milliseconds
     */
    public long getUptimeMillis() {
        return Instant.now().toEpochMilli() - startTime.toEpochMilli();
    }

    /**
     * Gets the translations per second rate.
     *
     * @return the translations per second
     */
    public double getTranslationsPerSecond() {
        long uptimeSeconds = getUptimeMillis() / 1000;
        if (uptimeSeconds == 0) {
            return 0.0;
        }
        return (double) totalTranslations.get() / uptimeSeconds;
    }

    @Override
    public String toString() {
        return "TranslationStatistics{" +
                "translationManagerId='" + translationManagerId + '\'' +
                ", startTime=" + startTime +
                ", totalTranslations=" + totalTranslations.get() +
                ", successfulTranslations=" + successfulTranslations.get() +
                ", failedTranslations=" + failedTranslations.get() +
                ", successRate=" + String.format("%.2f%%", getSuccessRate()) +
                ", averageTranslationTime=" + String.format("%.2fms", getAverageTranslationTimeMillis()) +
                ", translationsPerSecond=" + String.format("%.2f", getTranslationsPerSecond()) +
                '}';
    }
}
