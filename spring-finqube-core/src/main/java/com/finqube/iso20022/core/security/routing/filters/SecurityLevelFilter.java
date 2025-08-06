package com.finqube.iso20022.core.security.routing.filters;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.security.routing.MessageFilter;
import com.finqube.iso20022.core.security.routing.SecurityLevel;

/**
 * Filters messages based on their required security level.
 *
 * <p>This filter ensures that messages are only routed through routes that meet
 * or exceed the required security level. It's a critical component for maintaining
 * security compliance in message routing.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * SecurityLevelFilter filter = new SecurityLevelFilter(SecurityLevel.HIGH);
 * if (filter.matches(message)) {
 *     // Message requires HIGH or CRITICAL security level
 * }
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class SecurityLevelFilter implements MessageFilter {

    private final String filterId;
    private final String filterName;
    private final SecurityLevel requiredSecurityLevel;
    private final int priority;
    private final boolean enabled;

    /**
     * Creates a new security level filter.
     *
     * @param requiredSecurityLevel the minimum required security level
     */
    public SecurityLevelFilter(SecurityLevel requiredSecurityLevel) {
        this("security-level-filter", "Security Level Filter", requiredSecurityLevel, 100, true);
    }

    /**
     * Creates a new security level filter with custom parameters.
     *
     * @param filterId the unique filter identifier
     * @param filterName the human-readable filter name
     * @param requiredSecurityLevel the minimum required security level
     * @param priority the filter priority
     * @param enabled whether the filter is enabled
     */
    public SecurityLevelFilter(String filterId, String filterName, SecurityLevel requiredSecurityLevel,
                              int priority, boolean enabled) {
        this.filterId = filterId;
        this.filterName = filterName;
        this.requiredSecurityLevel = requiredSecurityLevel;
        this.priority = priority;
        this.enabled = enabled;
    }

    @Override
    public boolean matches(BaseMessage message) {
        if (!enabled) {
            return true; // If disabled, don't filter
        }

        // For now, we'll use a simple heuristic based on message type and amount
        // In a real implementation, this would be determined by the message content
        SecurityLevel messageSecurityLevel = determineMessageSecurityLevel(message);
        return messageSecurityLevel.isAtLeast(requiredSecurityLevel);
    }

    @Override
    public String getFilterId() {
        return filterId;
    }

    @Override
    public String getFilterName() {
        return filterName;
    }

    @Override
    public String getDescription() {
        return String.format("Filters messages requiring security level %s or higher",
                           requiredSecurityLevel.getDisplayName());
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.SECURITY;
    }

    @Override
    public String getFilterReason(BaseMessage message) {
        if (!enabled) {
            return null; // Not filtered if disabled
        }

        SecurityLevel messageSecurityLevel = determineMessageSecurityLevel(message);
        if (!messageSecurityLevel.isAtLeast(requiredSecurityLevel)) {
            return String.format("Message security level %s is below required level %s",
                               messageSecurityLevel.getDisplayName(),
                               requiredSecurityLevel.getDisplayName());
        }
        return null;
    }

    /**
     * Gets the required security level for this filter.
     *
     * @return the required security level
     */
    public SecurityLevel getRequiredSecurityLevel() {
        return requiredSecurityLevel;
    }

    /**
     * Determines the security level required for a given message.
     *
     * <p>This method implements business logic to determine the appropriate
     * security level based on message characteristics such as type, amount,
     * and business process.</p>
     *
     * @param message the message to evaluate
     * @return the required security level for the message
     */
    private SecurityLevel determineMessageSecurityLevel(BaseMessage message) {
        // High-value transactions require higher security
        if (message.getTotalAmount() > 1000000) { // 1M threshold
            return SecurityLevel.CRITICAL;
        }

        // High-value transactions
        if (message.getTotalAmount() > 100000) { // 100K threshold
            return SecurityLevel.HIGH;
        }

        // Payment messages typically require medium security
        if (message.getBusinessProcess().equalsIgnoreCase("pacs")) {
            return SecurityLevel.MEDIUM;
        }

        // Payment initiation messages
        if (message.getBusinessProcess().equalsIgnoreCase("pain")) {
            return SecurityLevel.MEDIUM;
        }

        // Account reporting messages can use lower security
        if (message.getBusinessProcess().equalsIgnoreCase("camt")) {
            return SecurityLevel.LOW;
        }

        // Default to medium security
        return SecurityLevel.MEDIUM;
    }

    @Override
    public String toString() {
        return String.format("SecurityLevelFilter{filterId='%s', requiredLevel=%s, enabled=%s}",
                           filterId, requiredSecurityLevel, enabled);
    }
}
