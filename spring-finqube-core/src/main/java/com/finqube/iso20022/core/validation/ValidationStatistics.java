package com.finqube.iso20022.core.validation;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Statistics and metrics for validation operations.
 *
 * <p>This class tracks various performance metrics and statistics for validation operations,
 * including validation counts, success rates, error rates, and timing information.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class ValidationStatistics {

    private final String validatorId;
    private final Instant startTime;
    private final AtomicLong totalValidations;
    private final AtomicLong successfulValidations;
    private final AtomicLong failedValidations;
    private final AtomicLong totalValidationTimeMillis;
    private final AtomicLong minValidationTimeMillis;
    private final AtomicLong maxValidationTimeMillis;
    private final Map<String, Long> errorTypeCounts;

    /**
     * Constructs a new ValidationStatistics.
     *
     * @param validatorId the validator identifier
     * @param startTime when statistics collection started
     * @param errorTypeCounts map of error types to counts
     */
    public ValidationStatistics(String validatorId, Instant startTime, Map<String, Long> errorTypeCounts) {
        this.validatorId = Objects.requireNonNull(validatorId, "Validator ID cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.totalValidations = new AtomicLong(0);
        this.successfulValidations = new AtomicLong(0);
        this.failedValidations = new AtomicLong(0);
        this.totalValidationTimeMillis = new AtomicLong(0);
        this.minValidationTimeMillis = new AtomicLong(Long.MAX_VALUE);
        this.maxValidationTimeMillis = new AtomicLong(0);
        this.errorTypeCounts = errorTypeCounts;
    }

    /**
     * Records a successful validation.
     *
     * @param validationTimeMillis the validation time in milliseconds
     */
    public void recordSuccess(long validationTimeMillis) {
        totalValidations.incrementAndGet();
        successfulValidations.incrementAndGet();
        totalValidationTimeMillis.addAndGet(validationTimeMillis);
        updateMinMaxValidationTime(validationTimeMillis);
    }

    /**
     * Records a failed validation.
     *
     * @param validationTimeMillis the validation time in milliseconds
     * @param errorType the type of error
     */
    public void recordFailure(long validationTimeMillis, String errorType) {
        totalValidations.incrementAndGet();
        failedValidations.incrementAndGet();
        totalValidationTimeMillis.addAndGet(validationTimeMillis);
        updateMinMaxValidationTime(validationTimeMillis);

        if (errorType != null) {
            errorTypeCounts.merge(errorType, 1L, Long::sum);
        }
    }

    private void updateMinMaxValidationTime(long validationTimeMillis) {
        minValidationTimeMillis.updateAndGet(current -> Math.min(current, validationTimeMillis));
        maxValidationTimeMillis.updateAndGet(current -> Math.max(current, validationTimeMillis));
    }

    /**
     * Gets the validator identifier.
     *
     * @return the validator identifier
     */
    public String getValidatorId() {
        return validatorId;
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
     * Gets the total number of validations.
     *
     * @return the total validations count
     */
    public long getTotalValidations() {
        return totalValidations.get();
    }

    /**
     * Gets the number of successful validations.
     *
     * @return the successful validations count
     */
    public long getSuccessfulValidations() {
        return successfulValidations.get();
    }

    /**
     * Gets the number of failed validations.
     *
     * @return the failed validations count
     */
    public long getFailedValidations() {
        return failedValidations.get();
    }

    /**
     * Gets the success rate as a percentage.
     *
     * @return the success rate percentage
     */
    public double getSuccessRate() {
        long total = totalValidations.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) successfulValidations.get() / total * 100.0;
    }

    /**
     * Gets the failure rate as a percentage.
     *
     * @return the failure rate percentage
     */
    public double getFailureRate() {
        long total = totalValidations.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) failedValidations.get() / total * 100.0;
    }

    /**
     * Gets the average validation time in milliseconds.
     *
     * @return the average validation time
     */
    public double getAverageValidationTimeMillis() {
        long total = totalValidations.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) totalValidationTimeMillis.get() / total;
    }

    /**
     * Gets the minimum validation time in milliseconds.
     *
     * @return the minimum validation time
     */
    public long getMinValidationTimeMillis() {
        long min = minValidationTimeMillis.get();
        return min == Long.MAX_VALUE ? 0 : min;
    }

    /**
     * Gets the maximum validation time in milliseconds.
     *
     * @return the maximum validation time
     */
    public long getMaxValidationTimeMillis() {
        return maxValidationTimeMillis.get();
    }

    /**
     * Gets the total validation time in milliseconds.
     *
     * @return the total validation time
     */
    public long getTotalValidationTimeMillis() {
        return totalValidationTimeMillis.get();
    }

    /**
     * Gets the error counts by type.
     *
     * @return the error counts map
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
     * Gets the validations per second rate.
     *
     * @return the validations per second
     */
    public double getValidationsPerSecond() {
        long uptimeSeconds = getUptimeMillis() / 1000;
        if (uptimeSeconds == 0) {
            return 0.0;
        }
        return (double) totalValidations.get() / uptimeSeconds;
    }

    @Override
    public String toString() {
        return "ValidationStatistics{" +
                "validatorId='" + validatorId + '\'' +
                ", startTime=" + startTime +
                ", totalValidations=" + totalValidations.get() +
                ", successfulValidations=" + successfulValidations.get() +
                ", failedValidations=" + failedValidations.get() +
                ", successRate=" + String.format("%.2f%%", getSuccessRate()) +
                ", averageValidationTime=" + String.format("%.2fms", getAverageValidationTimeMillis()) +
                ", validationsPerSecond=" + String.format("%.2f", getValidationsPerSecond()) +
                '}';
    }
}
