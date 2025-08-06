package com.finqube.iso20022.core.security.routing;

/**
 * Exception thrown when message routing fails.
 *
 * <p>This exception is thrown when there are problems with message routing,
 * such as no suitable routes available, filtering failures, or transport errors.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class RoutingException extends Exception {

    private final String messageId;
    private final String routeId;
    private final RoutingErrorType errorType;

    /**
     * Creates a new routing exception.
     *
     * @param message the error message
     * @param cause the cause of the exception
     * @param messageId the ID of the message that failed to route
     * @param routeId the ID of the route that failed (if applicable)
     * @param errorType the type of routing error
     */
    public RoutingException(String message, Throwable cause, String messageId, String routeId, RoutingErrorType errorType) {
        super(message, cause);
        this.messageId = messageId;
        this.routeId = routeId;
        this.errorType = errorType;
    }

    /**
     * Creates a new routing exception.
     *
     * @param message the error message
     * @param messageId the ID of the message that failed to route
     * @param routeId the ID of the route that failed (if applicable)
     * @param errorType the type of routing error
     */
    public RoutingException(String message, String messageId, String routeId, RoutingErrorType errorType) {
        super(message);
        this.messageId = messageId;
        this.routeId = routeId;
        this.errorType = errorType;
    }

    /**
     * Gets the ID of the message that failed to route.
     *
     * @return the message ID
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Gets the ID of the route that failed (if applicable).
     *
     * @return the route ID, or null if not applicable
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * Gets the type of routing error.
     *
     * @return the error type
     */
    public RoutingErrorType getErrorType() {
        return errorType;
    }

    /**
     * Defines the types of routing errors.
     */
    public enum RoutingErrorType {
        /**
         * No suitable route found for the message.
         */
        NO_ROUTE_AVAILABLE,

        /**
         * Message was filtered out by security or business rules.
         */
        MESSAGE_FILTERED,

        /**
         * Route is not available or disabled.
         */
        ROUTE_UNAVAILABLE,

        /**
         * Transport error occurred during routing.
         */
        TRANSPORT_ERROR,

        /**
         * Security validation failed.
         */
        SECURITY_ERROR,

        /**
         * Configuration error in the router.
         */
        CONFIGURATION_ERROR,

        /**
         * Unknown or unexpected error.
         */
        UNKNOWN_ERROR
    }
}
