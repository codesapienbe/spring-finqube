package com.finqube.iso20022.core.security.routing;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.security.routing.MessageFilter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Secure message router for ISO 20022 message routing with filtering capabilities.
 *
 * <p>This interface defines the contract for routing messages through secure
 * channels while applying filtering rules and security policies. The router
 * ensures that messages are only sent through routes that meet the required
 * security and business criteria.</p>
 *
 * <p>The router supports:</p>
 * <ul>
 *   <li>Message routing based on type and destination</li>
 *   <li>Security level filtering and validation</li>
 *   <li>Business rule filtering</li>
 *   <li>Compliance and audit requirements</li>
 *   <li>Load balancing and failover</li>
 *   <li>Asynchronous routing operations</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * @Autowired
 * private SecureMessageRouter router;
 *
 * // Route a message with filtering
 * RoutingResult result = router.routeMessage(message, userId);
 *
 * if (result.isSuccessful()) {
 *     // Message was successfully routed
 *     log.info("Message routed to: {}", result.getSelectedRoute());
 * } else {
 *     // Message was filtered out or routing failed
 *     log.warn("Message routing failed: {}", result.getFailureReason());
 * }
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface SecureMessageRouter {

    /**
     * Routes a message through the appropriate secure channel.
     *
     * @param message the message to route
     * @param userId the user ID requesting the routing
     * @return the routing result
     * @throws RoutingException if routing fails
     */
    RoutingResult routeMessage(BaseMessage message, String userId) throws RoutingException;

    /**
     * Routes a message asynchronously.
     *
     * @param message the message to route
     * @param userId the user ID requesting the routing
     * @return a CompletableFuture that will complete with the routing result
     */
    CompletableFuture<RoutingResult> routeMessageAsync(BaseMessage message, String userId);

    /**
     * Adds a route to the router.
     *
     * @param route the route to add
     * @throws RoutingException if the route cannot be added
     */
    void addRoute(MessageRoute route) throws RoutingException;

    /**
     * Removes a route from the router.
     *
     * @param routeId the ID of the route to remove
     * @return true if the route was removed, false if it didn't exist
     */
    boolean removeRoute(String routeId);

    /**
     * Gets a route by its ID.
     *
     * @param routeId the route ID
     * @return the route, or null if not found
     */
    MessageRoute getRoute(String routeId);

    /**
     * Gets all available routes.
     *
     * @return the list of all routes
     */
    List<MessageRoute> getAllRoutes();

    /**
     * Gets routes that support a specific message type.
     *
     * @param messageType the message type to filter by
     * @return the list of supporting routes
     */
    List<MessageRoute> getRoutesForMessageType(String messageType);

    /**
     * Adds a filter to the router.
     *
     * @param filter the filter to add
     * @throws RoutingException if the filter cannot be added
     */
    void addFilter(MessageFilter filter) throws RoutingException;

    /**
     * Removes a filter from the router.
     *
     * @param filterId the ID of the filter to remove
     * @return true if the filter was removed, false if it didn't exist
     */
    boolean removeFilter(String filterId);

    /**
     * Gets a filter by its ID.
     *
     * @param filterId the filter ID
     * @return the filter, or null if not found
     */
    MessageFilter getFilter(String filterId);

    /**
     * Gets all filters.
     *
     * @return the list of all filters
     */
    List<MessageFilter> getAllFilters();

    /**
     * Gets filters of a specific type.
     *
     * @param filterType the filter type to filter by
     * @return the list of filters of the specified type
     */
    List<MessageFilter> getFiltersByType(MessageFilter.FilterType filterType);

    /**
     * Evaluates all filters against a message.
     *
     * @param message the message to evaluate
     * @return the filtering result
     */
    FilteringResult evaluateFilters(BaseMessage message);

    /**
     * Gets the router identifier.
     *
     * @return the router identifier
     */
    String getRouterId();

    /**
     * Gets the router display name.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Gets the router version.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Checks if the router is available.
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();

    /**
     * Performs a health check on the router.
     *
     * @return the health check result
     */
    RouterHealthCheck healthCheck();

    /**
     * Gets routing statistics.
     *
     * @return the routing statistics
     */
    RoutingStatistics getStatistics();
}
