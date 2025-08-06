package com.finqube.iso20022.core.security.routing;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the result of a message routing operation.
 *
 * <p>This class contains information about the success or failure of a routing
 * operation, including the selected route, filtering results, and any error details.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class RoutingResult {

    private final String messageId;
    private final boolean successful;
    private final MessageRoute selectedRoute;
    private final LocalDateTime routingTime;
    private final String failureReason;
    private final RoutingException.RoutingErrorType errorType;
    private final List<String> appliedFilters;
    private final List<String> failedFilters;
    private final long routingDurationMillis;

    /**
     * Private constructor for builder pattern.
     */
    private RoutingResult(Builder builder) {
        this.messageId = builder.messageId;
        this.successful = builder.successful;
        this.selectedRoute = builder.selectedRoute;
        this.routingTime = builder.routingTime;
        this.failureReason = builder.failureReason;
        this.errorType = builder.errorType;
        this.appliedFilters = builder.appliedFilters;
        this.failedFilters = builder.failedFilters;
        this.routingDurationMillis = builder.routingDurationMillis;
    }

    /**
     * Gets the ID of the message that was routed.
     *
     * @return the message ID
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Checks if the routing was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Gets the route that was selected for routing.
     *
     * @return the selected route, or null if routing failed
     */
    public MessageRoute getSelectedRoute() {
        return selectedRoute;
    }

    /**
     * Gets the time when routing was performed.
     *
     * @return the routing time
     */
    public LocalDateTime getRoutingTime() {
        return routingTime;
    }

    /**
     * Gets the reason for routing failure (if applicable).
     *
     * @return the failure reason, or null if routing was successful
     */
    public String getFailureReason() {
        return failureReason;
    }

    /**
     * Gets the type of routing error (if applicable).
     *
     * @return the error type, or null if routing was successful
     */
    public RoutingException.RoutingErrorType getErrorType() {
        return errorType;
    }

    /**
     * Gets the list of filters that were applied during routing.
     *
     * @return the list of applied filter IDs
     */
    public List<String> getAppliedFilters() {
        return appliedFilters;
    }

    /**
     * Gets the list of filters that failed during routing.
     *
     * @return the list of failed filter IDs
     */
    public List<String> getFailedFilters() {
        return failedFilters;
    }

    /**
     * Gets the duration of the routing operation in milliseconds.
     *
     * @return the routing duration in milliseconds
     */
    public long getRoutingDurationMillis() {
        return routingDurationMillis;
    }

    /**
     * Creates a successful routing result.
     *
     * @param messageId the message ID
     * @param selectedRoute the selected route
     * @param appliedFilters the applied filters
     * @param routingDurationMillis the routing duration
     * @return a successful routing result
     */
    public static RoutingResult success(String messageId, MessageRoute selectedRoute,
                                       List<String> appliedFilters, long routingDurationMillis) {
        return new Builder()
                .messageId(messageId)
                .successful(true)
                .selectedRoute(selectedRoute)
                .routingTime(LocalDateTime.now())
                .appliedFilters(appliedFilters)
                .routingDurationMillis(routingDurationMillis)
                .build();
    }

    /**
     * Creates a failed routing result.
     *
     * @param messageId the message ID
     * @param failureReason the failure reason
     * @param errorType the error type
     * @param failedFilters the failed filters
     * @param routingDurationMillis the routing duration
     * @return a failed routing result
     */
    public static RoutingResult failure(String messageId, String failureReason,
                                       RoutingException.RoutingErrorType errorType,
                                       List<String> failedFilters, long routingDurationMillis) {
        return new Builder()
                .messageId(messageId)
                .successful(false)
                .routingTime(LocalDateTime.now())
                .failureReason(failureReason)
                .errorType(errorType)
                .failedFilters(failedFilters)
                .routingDurationMillis(routingDurationMillis)
                .build();
    }

    @Override
    public String toString() {
        if (successful) {
            return String.format("RoutingResult{messageId='%s', successful=true, route='%s', duration=%dms}",
                               messageId, selectedRoute != null ? selectedRoute.getRouteId() : "null", routingDurationMillis);
        } else {
            return String.format("RoutingResult{messageId='%s', successful=false, reason='%s', errorType=%s}",
                               messageId, failureReason, errorType);
        }
    }

    /**
     * Builder for creating RoutingResult instances.
     */
    public static class Builder {
        private String messageId;
        private boolean successful;
        private MessageRoute selectedRoute;
        private LocalDateTime routingTime;
        private String failureReason;
        private RoutingException.RoutingErrorType errorType;
        private List<String> appliedFilters = List.of();
        private List<String> failedFilters = List.of();
        private long routingDurationMillis;

        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder successful(boolean successful) {
            this.successful = successful;
            return this;
        }

        public Builder selectedRoute(MessageRoute selectedRoute) {
            this.selectedRoute = selectedRoute;
            return this;
        }

        public Builder routingTime(LocalDateTime routingTime) {
            this.routingTime = routingTime;
            return this;
        }

        public Builder failureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }

        public Builder errorType(RoutingException.RoutingErrorType errorType) {
            this.errorType = errorType;
            return this;
        }

        public Builder appliedFilters(List<String> appliedFilters) {
            this.appliedFilters = appliedFilters;
            return this;
        }

        public Builder failedFilters(List<String> failedFilters) {
            this.failedFilters = failedFilters;
            return this;
        }

        public Builder routingDurationMillis(long routingDurationMillis) {
            this.routingDurationMillis = routingDurationMillis;
            return this;
        }

        public RoutingResult build() {
            if (messageId == null || messageId.trim().isEmpty()) {
                throw new IllegalArgumentException("Message ID is required");
            }
            if (routingTime == null) {
                routingTime = LocalDateTime.now();
            }
            return new RoutingResult(this);
        }
    }
}
