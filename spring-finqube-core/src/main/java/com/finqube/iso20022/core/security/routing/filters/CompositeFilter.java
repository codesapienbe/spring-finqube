package com.finqube.iso20022.core.security.routing.filters;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.security.routing.MessageFilter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A composite filter that combines multiple filters using logical operations.
 *
 * <p>This filter allows you to create complex filtering rules by combining
 * multiple individual filters. It supports both AND and OR logical operations.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * SecurityLevelFilter securityFilter = new SecurityLevelFilter(SecurityLevel.HIGH);
 * MessageSizeFilter sizeFilter = new MessageSizeFilter(1024 * 1024); // 1MB
 *
 * CompositeFilter compositeFilter = new CompositeFilter(
 *     "composite-filter",
 *     "Composite Security and Size Filter",
 *     List.of(securityFilter, sizeFilter),
 *     CompositeFilter.LogicalOperator.AND,
 *     50,
 *     true
 * );
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class CompositeFilter implements MessageFilter {

    private final String filterId;
    private final String filterName;
    private final List<MessageFilter> filters;
    private final LogicalOperator operator;
    private final int priority;
    private final boolean enabled;

    /**
     * Defines the logical operator used to combine filters.
     */
    public enum LogicalOperator {
        /**
         * All filters must match (logical AND).
         */
        AND,

        /**
         * At least one filter must match (logical OR).
         */
        OR
    }

    /**
     * Creates a new composite filter.
     *
     * @param filterId the unique filter identifier
     * @param filterName the human-readable filter name
     * @param filters the list of filters to combine
     * @param operator the logical operator to use
     * @param priority the filter priority
     * @param enabled whether the filter is enabled
     */
    public CompositeFilter(String filterId, String filterName, List<MessageFilter> filters,
                          LogicalOperator operator, int priority, boolean enabled) {
        this.filterId = filterId;
        this.filterName = filterName;
        this.filters = filters;
        this.operator = operator;
        this.priority = priority;
        this.enabled = enabled;
    }

    @Override
    public boolean matches(BaseMessage message) {
        if (!enabled || filters.isEmpty()) {
            return true; // If disabled or no filters, don't filter
        }

        return switch (operator) {
            case AND -> filters.stream().allMatch(filter -> filter.matches(message));
            case OR -> filters.stream().anyMatch(filter -> filter.matches(message));
        };
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
        String filterDescriptions = filters.stream()
                .map(MessageFilter::getFilterName)
                .collect(Collectors.joining(" " + operator.name() + " "));

        return String.format("Combines filters using %s: %s", operator.name(), filterDescriptions);
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
        return FilterType.COMPOSITE;
    }

    @Override
    public String getFilterReason(BaseMessage message) {
        if (!enabled || filters.isEmpty()) {
            return null; // Not filtered if disabled or no filters
        }

        if (operator == LogicalOperator.AND) {
            // For AND, find the first filter that doesn't match
            for (MessageFilter filter : filters) {
                if (!filter.matches(message)) {
                    return String.format("Composite filter failed: %s", filter.getFilterReason(message));
                }
            }
        } else { // OR
            // For OR, check if any filter matches
            boolean anyMatch = filters.stream().anyMatch(filter -> filter.matches(message));
            if (!anyMatch) {
                String reasons = filters.stream()
                        .map(filter -> filter.getFilterReason(message))
                        .filter(reason -> reason != null)
                        .collect(Collectors.joining("; "));

                return String.format("No filter matched in composite filter: %s", reasons);
            }
        }

        return null;
    }

    /**
     * Gets the list of filters in this composite filter.
     *
     * @return the list of filters
     */
    public List<MessageFilter> getFilters() {
        return filters;
    }

    /**
     * Gets the logical operator used to combine filters.
     *
     * @return the logical operator
     */
    public LogicalOperator getOperator() {
        return operator;
    }

    /**
     * Adds a filter to this composite filter.
     *
     * @param filter the filter to add
     * @return this composite filter for chaining
     */
    public CompositeFilter addFilter(MessageFilter filter) {
        filters.add(filter);
        return this;
    }

    /**
     * Removes a filter from this composite filter.
     *
     * @param filter the filter to remove
     * @return this composite filter for chaining
     */
    public CompositeFilter removeFilter(MessageFilter filter) {
        filters.remove(filter);
        return this;
    }

    /**
     * Gets the number of filters in this composite filter.
     *
     * @return the number of filters
     */
    public int getFilterCount() {
        return filters.size();
    }

    @Override
    public String toString() {
        return String.format("CompositeFilter{filterId='%s', operator=%s, filterCount=%d, enabled=%s}",
                           filterId, operator, filters.size(), enabled);
    }
}
