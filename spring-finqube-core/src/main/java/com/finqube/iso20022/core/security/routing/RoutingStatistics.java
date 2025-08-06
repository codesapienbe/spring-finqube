package com.finqube.iso20022.core.security.routing;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Statistics for message routing operations.
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class RoutingStatistics {

    private final long totalMessagesRouted;
    private final long successfulRoutings;
    private final long failedRoutings;
    private final long filteredMessages;
    private final double averageRoutingTimeMillis;
    private final Map<String, Long> routesUsed;
    private final Map<String, Long> filtersApplied;
    private final LocalDateTime lastResetTime;

    public RoutingStatistics(long totalMessagesRouted, long successfulRoutings, long failedRoutings,
                           long filteredMessages, double averageRoutingTimeMillis,
                           Map<String, Long> routesUsed, Map<String, Long> filtersApplied) {
        this.totalMessagesRouted = totalMessagesRouted;
        this.successfulRoutings = successfulRoutings;
        this.failedRoutings = failedRoutings;
        this.filteredMessages = filteredMessages;
        this.averageRoutingTimeMillis = averageRoutingTimeMillis;
        this.routesUsed = routesUsed;
        this.filtersApplied = filtersApplied;
        this.lastResetTime = LocalDateTime.now();
    }

    public long getTotalMessagesRouted() { return totalMessagesRouted; }
    public long getSuccessfulRoutings() { return successfulRoutings; }
    public long getFailedRoutings() { return failedRoutings; }
    public long getFilteredMessages() { return filteredMessages; }
    public double getAverageRoutingTimeMillis() { return averageRoutingTimeMillis; }
    public Map<String, Long> getRoutesUsed() { return routesUsed; }
    public Map<String, Long> getFiltersApplied() { return filtersApplied; }
    public LocalDateTime getLastResetTime() { return lastResetTime; }
}
