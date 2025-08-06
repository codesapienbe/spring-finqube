package com.finqube.iso20022.core.security.routing;

import com.finqube.iso20022.core.message.BaseMessage;

/**
 * Defines filtering criteria for secure message routing.
 *
 * <p>This interface provides a contract for implementing message filtering logic
 * that can be used to determine whether a message should be routed through
 * a specific route or filtered out based on security, business, or technical criteria.</p>
 *
 * <p>Filters can be combined to create complex filtering rules and can be
 * used to implement security policies, compliance requirements, and business rules.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * MessageFilter securityFilter = new SecurityLevelFilter(SecurityLevel.HIGH);
 * MessageFilter sizeFilter = new MessageSizeFilter(1024 * 1024); // 1MB
 * MessageFilter compositeFilter = new CompositeFilter(List.of(securityFilter, sizeFilter));
 *
 * if (compositeFilter.matches(message)) {
 *     // Route the message
 * }
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface MessageFilter {

    /**
     * Determines if the given message matches this filter's criteria.
     *
     * @param message the message to evaluate
     * @return true if the message matches the filter criteria, false otherwise
     */
    boolean matches(BaseMessage message);

    /**
     * Gets the filter identifier.
     *
     * @return the unique filter identifier
     */
    String getFilterId();

    /**
     * Gets the human-readable filter name.
     *
     * @return the filter name
     */
    String getFilterName();

    /**
     * Gets the filter description.
     *
     * @return the filter description
     */
    String getDescription();

    /**
     * Gets the filter priority (higher values = higher priority).
     *
     * @return the filter priority
     */
    int getPriority();

    /**
     * Checks if this filter is enabled.
     *
     * @return true if enabled, false otherwise
     */
    boolean isEnabled();

    /**
     * Gets the filter type.
     *
     * @return the filter type
     */
    FilterType getFilterType();

    /**
     * Gets the reason why a message was filtered out (if applicable).
     *
     * <p>This method should return a meaningful reason when {@link #matches(BaseMessage)}
     * returns false, explaining why the message was filtered out.</p>
     *
     * @param message the message that was evaluated
     * @return the reason for filtering, or null if the message was not filtered
     */
    String getFilterReason(BaseMessage message);

    /**
     * Defines the types of filters available.
     */
    enum FilterType {
        /**
         * Security-based filtering (e.g., security level, encryption requirements).
         */
        SECURITY,

        /**
         * Business rule filtering (e.g., transaction limits, currency restrictions).
         */
        BUSINESS,

        /**
         * Technical filtering (e.g., message size, format validation).
         */
        TECHNICAL,

        /**
         * Compliance filtering (e.g., regulatory requirements, audit trails).
         */
        COMPLIANCE,

        /**
         * Composite filter that combines multiple filters.
         */
        COMPOSITE
    }
}
