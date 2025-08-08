package com.finqube.iso20022.admin.dashboard;

import org.springframework.stereotype.Component;

/**
 * Simple dashboard view for the Spring Finqube Admin application.
 *
 * <p>This is a placeholder implementation that will be replaced with
 * a full Vaadin Flow dashboard once the dependency issues are resolved.</p>
 *
 * @author Spring Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Component
public class SimpleDashboardView {

    /**
     * Gets the dashboard title.
     *
     * @return dashboard title
     */
    public String getTitle() {
        return "Spring Finqube Admin Dashboard";
    }

    /**
     * Gets the current message count.
     *
     * @return message count
     */
    public long getMessageCount() {
        return 1234; // Placeholder value
    }

    /**
     * Gets the system health status.
     *
     * @return health status
     */
    public String getHealthStatus() {
        return "Healthy";
    }
}
