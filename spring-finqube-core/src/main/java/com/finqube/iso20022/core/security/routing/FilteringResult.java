package com.finqube.iso20022.core.security.routing;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the result of message filtering operations.
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class FilteringResult {

    private final String messageId;
    private final boolean passed;
    private final List<String> appliedFilters;
    private final List<String> failedFilters;
    private final LocalDateTime filteringTime;
    private final long filteringDurationMillis;

    public FilteringResult(String messageId, boolean passed, List<String> appliedFilters,
                          List<String> failedFilters, long filteringDurationMillis) {
        this.messageId = messageId;
        this.passed = passed;
        this.appliedFilters = appliedFilters;
        this.failedFilters = failedFilters;
        this.filteringTime = LocalDateTime.now();
        this.filteringDurationMillis = filteringDurationMillis;
    }

    public String getMessageId() { return messageId; }
    public boolean isPassed() { return passed; }
    public List<String> getAppliedFilters() { return appliedFilters; }
    public List<String> getFailedFilters() { return failedFilters; }
    public LocalDateTime getFilteringTime() { return filteringTime; }
    public long getFilteringDurationMillis() { return filteringDurationMillis; }
}
