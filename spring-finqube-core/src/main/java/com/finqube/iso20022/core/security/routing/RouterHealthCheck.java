package com.finqube.iso20022.core.security.routing;

import java.time.LocalDateTime;

/**
 * Health check result for the secure message router.
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class RouterHealthCheck {

    private final boolean healthy;
    private final String status;
    private final LocalDateTime checkTime;
    private final int routeCount;
    private final int filterCount;
    private final String details;

    public RouterHealthCheck(boolean healthy, String status, int routeCount, int filterCount, String details) {
        this.healthy = healthy;
        this.status = status;
        this.checkTime = LocalDateTime.now();
        this.routeCount = routeCount;
        this.filterCount = filterCount;
        this.details = details;
    }

    public boolean isHealthy() { return healthy; }
    public String getStatus() { return status; }
    public LocalDateTime getCheckTime() { return checkTime; }
    public int getRouteCount() { return routeCount; }
    public int getFilterCount() { return filterCount; }
    public String getDetails() { return details; }
}
