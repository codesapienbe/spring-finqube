package com.finqube.iso20022.core.security.routing.impl;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.security.routing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Default implementation of the secure message router.
 *
 * <p>This implementation provides comprehensive message routing with filtering
 * capabilities, security validation, and performance monitoring.</p>
 *
 * <p>The router maintains:</p>
 * <ul>
 *   <li>A registry of available routes</li>
 *   <li>A collection of filters for security and business rules</li>
 *   <li>Statistics for monitoring and performance analysis</li>
 *   <li>Health check capabilities</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class DefaultSecureMessageRouter implements SecureMessageRouter {

    private static final Logger log = LoggerFactory.getLogger(DefaultSecureMessageRouter.class);

    private final String routerId;
    private final String displayName;
    private final String version;
    private final Map<String, MessageRoute> routes;
    private final Map<String, MessageFilter> filters;
    private final AtomicLong totalMessagesRouted;
    private final AtomicLong successfulRoutings;
    private final AtomicLong failedRoutings;
    private final AtomicLong filteredMessages;
    private final AtomicLong totalRoutingTime;
    private final Map<String, AtomicLong> routeUsageCounters;
    private final Map<String, AtomicLong> filterUsageCounters;

    /**
     * Creates a new default secure message router.
     *
     * @param routerId the unique router identifier
     * @param displayName the human-readable display name
     * @param version the router version
     */
    public DefaultSecureMessageRouter(String routerId, String displayName, String version) {
        this.routerId = routerId;
        this.displayName = displayName;
        this.version = version;
        this.routes = new ConcurrentHashMap<>();
        this.filters = new ConcurrentHashMap<>();
        this.totalMessagesRouted = new AtomicLong(0);
        this.successfulRoutings = new AtomicLong(0);
        this.failedRoutings = new AtomicLong(0);
        this.filteredMessages = new AtomicLong(0);
        this.totalRoutingTime = new AtomicLong(0);
        this.routeUsageCounters = new ConcurrentHashMap<>();
        this.filterUsageCounters = new ConcurrentHashMap<>();
    }

    @Override
    public RoutingResult routeMessage(BaseMessage message, String userId) throws RoutingException {
        long startTime = System.currentTimeMillis();
        String messageId = message.getMessageId();

        log.debug("Starting message routing for message ID: {}", messageId);
        log.debug("Router has {} routes and {} filters", routes.size(), filters.size());

        try {
            // Step 1: Evaluate filters
            FilteringResult filteringResult = evaluateFilters(message);
            log.debug("Filter evaluation result for message {}: passed={}, appliedFilters={}, failedFilters={}",
                     messageId, filteringResult.isPassed(), filteringResult.getAppliedFilters(), filteringResult.getFailedFilters());

            if (!filteringResult.isPassed()) {
                filteredMessages.incrementAndGet();
                log.warn("Message {} was filtered out: {}", messageId, filteringResult.getFailedFilters());
                return RoutingResult.failure(messageId, "Message filtered out by security or business rules",
                        RoutingException.RoutingErrorType.MESSAGE_FILTERED,
                        filteringResult.getFailedFilters(), System.currentTimeMillis() - startTime);
            }

            // Step 2: Find suitable routes
            List<MessageRoute> suitableRoutes = findSuitableRoutes(message);
            log.debug("Found {} suitable routes for message {} with type {}", suitableRoutes.size(), messageId, message.getMessageType());
            for (MessageRoute route : suitableRoutes) {
                log.debug("Suitable route: {} (enabled={}, priority={})", route.getRouteId(), route.isEnabled(), route.getPriority());
            }

            if (suitableRoutes.isEmpty()) {
                failedRoutings.incrementAndGet();
                log.error("No suitable route found for message ID: {}", messageId);
                throw new RoutingException("No suitable route found for message type: " + message.getMessageType(),
                        messageId, null, RoutingException.RoutingErrorType.NO_ROUTE_AVAILABLE);
            }

            // Step 3: Select the best route
            MessageRoute selectedRoute = selectBestRoute(suitableRoutes, message);
            log.debug("Selected route for message {}: {}", messageId, selectedRoute != null ? selectedRoute.getRouteId() : "null");

            if (selectedRoute == null) {
                failedRoutings.incrementAndGet();
                log.error("No route could be selected for message ID: {}", messageId);
                throw new RoutingException("No route could be selected for message",
                        messageId, null, RoutingException.RoutingErrorType.ROUTE_UNAVAILABLE);
            }

            // Step 4: Update statistics
            totalMessagesRouted.incrementAndGet();
            successfulRoutings.incrementAndGet();
            routeUsageCounters.computeIfAbsent(selectedRoute.getRouteId(), k -> new AtomicLong(0)).incrementAndGet();

            long routingDuration = System.currentTimeMillis() - startTime;
            if (routingDuration <= 0) {
                routingDuration = 1;
            }
            totalRoutingTime.addAndGet(routingDuration);

            log.info("Message {} successfully routed to {} in {}ms", messageId, selectedRoute.getRouteId(), routingDuration);

            return RoutingResult.success(messageId, selectedRoute, filteringResult.getAppliedFilters(), routingDuration);

        } catch (RoutingException e) {
            failedRoutings.incrementAndGet();
            long routingDuration = System.currentTimeMillis() - startTime;
            log.error("Message routing failed for message ID: {} - {}", messageId, e.getMessage());
            throw e;
        } catch (Exception e) {
            failedRoutings.incrementAndGet();
            long routingDuration = System.currentTimeMillis() - startTime;
            log.error("Unexpected error during message routing for message ID: {}", messageId, e);
            throw new RoutingException("Unexpected error during routing: " + e.getMessage(),
                    messageId, null, RoutingException.RoutingErrorType.UNKNOWN_ERROR);
        }
    }

    @Override
    public CompletableFuture<RoutingResult> routeMessageAsync(BaseMessage message, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return routeMessage(message, userId);
            } catch (RoutingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void addRoute(MessageRoute route) throws RoutingException {
        if (route == null) {
            throw new RoutingException("Route cannot be null", null, null, RoutingException.RoutingErrorType.CONFIGURATION_ERROR);
        }

        String routeId = route.getRouteId();
        if (routes.containsKey(routeId)) {
            throw new RoutingException("Route with ID " + routeId + " already exists", null, routeId, RoutingException.RoutingErrorType.CONFIGURATION_ERROR);
        }

        routes.put(routeId, route);
        log.info("Added route: {} ({}) with message types: {}", routeId, route.getDisplayName(), route.getMessageTypes());
        log.debug("Router now has {} routes", routes.size());
    }

    @Override
    public boolean removeRoute(String routeId) {
        MessageRoute removed = routes.remove(routeId);
        if (removed != null) {
            log.info("Removed route: {} ({})", routeId, removed.getDisplayName());
            return true;
        }
        return false;
    }

    @Override
    public MessageRoute getRoute(String routeId) {
        return routes.get(routeId);
    }

    @Override
    public List<MessageRoute> getAllRoutes() {
        return new ArrayList<>(routes.values());
    }

    @Override
    public List<MessageRoute> getRoutesForMessageType(String messageType) {
        return routes.values().stream()
                .filter(route -> route.supportsMessageType(messageType))
                .collect(Collectors.toList());
    }

    @Override
    public void addFilter(MessageFilter filter) throws RoutingException {
        if (filter == null) {
            throw new RoutingException("Filter cannot be null", null, null, RoutingException.RoutingErrorType.CONFIGURATION_ERROR);
        }

        String filterId = filter.getFilterId();
        if (filters.containsKey(filterId)) {
            throw new RoutingException("Filter with ID " + filterId + " already exists", null, null, RoutingException.RoutingErrorType.CONFIGURATION_ERROR);
        }

        filters.put(filterId, filter);
        log.info("Added filter: {} ({})", filterId, filter.getFilterName());
    }

    @Override
    public boolean removeFilter(String filterId) {
        MessageFilter removed = filters.remove(filterId);
        if (removed != null) {
            log.info("Removed filter: {} ({})", filterId, removed.getFilterName());
            return true;
        }
        return false;
    }

    @Override
    public MessageFilter getFilter(String filterId) {
        return filters.get(filterId);
    }

    @Override
    public List<MessageFilter> getAllFilters() {
        return new ArrayList<>(filters.values());
    }

    @Override
    public List<MessageFilter> getFiltersByType(MessageFilter.FilterType filterType) {
        return filters.values().stream()
                .filter(filter -> filter.getFilterType() == filterType)
                .collect(Collectors.toList());
    }

    @Override
    public FilteringResult evaluateFilters(BaseMessage message) {
        List<String> appliedFilters = new ArrayList<>();
        List<String> failedFilters = new ArrayList<>();

        // Sort filters by priority (higher priority first)
        List<MessageFilter> sortedFilters = filters.values().stream()
                .filter(MessageFilter::isEnabled)
                .sorted(Comparator.comparing(MessageFilter::getPriority).reversed())
                .collect(Collectors.toList());

        for (MessageFilter filter : sortedFilters) {
            try {
                if (filter.matches(message)) {
                    appliedFilters.add(filter.getFilterId());
                    filterUsageCounters.computeIfAbsent(filter.getFilterId(), k -> new AtomicLong(0)).incrementAndGet();
                } else {
                    failedFilters.add(filter.getFilterId());
                    log.debug("Filter {} failed for message {}: {}", filter.getFilterId(), message.getMessageId(), filter.getFilterReason(message));
                }
            } catch (Exception e) {
                log.error("Error evaluating filter {} for message {}", filter.getFilterId(), message.getMessageId(), e);
                failedFilters.add(filter.getFilterId());
            }
        }

        boolean passed = failedFilters.isEmpty();
        return new FilteringResult(message.getMessageId(), passed, appliedFilters, failedFilters, 0);
    }

    @Override
    public String getRouterId() {
        return routerId;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isAvailable() {
        return !routes.isEmpty();
    }

    @Override
    public RouterHealthCheck healthCheck() {
        boolean healthy = isAvailable();
        String status = healthy ? "HEALTHY" : "UNHEALTHY";
        String details = String.format("Router has %d routes and %d filters", routes.size(), filters.size());

        return new RouterHealthCheck(healthy, status, routes.size(), filters.size(), details);
    }

    @Override
    public RoutingStatistics getStatistics() {
        long total = totalMessagesRouted.get();
        long successful = successfulRoutings.get();
        long failed = failedRoutings.get();
        long filtered = filteredMessages.get();

        double averageRoutingTime = total > 0 ? (double) totalRoutingTime.get() / total : 0.0;

        Map<String, Long> routesUsed = routeUsageCounters.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

        Map<String, Long> filtersApplied = filterUsageCounters.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

        return new RoutingStatistics(total, successful, failed, filtered, averageRoutingTime, routesUsed, filtersApplied);
    }

    /**
     * Finds suitable routes for a given message.
     *
     * @param message the message to find routes for
     * @return the list of suitable routes
     */
    private List<MessageRoute> findSuitableRoutes(BaseMessage message) {
        log.debug("Finding suitable routes for message {} with type {}", message.getMessageId(), message.getMessageType());
        log.debug("Available routes: {}", routes.keySet());

        List<MessageRoute> suitableRoutes = routes.values().stream()
                .filter(route -> {
                    boolean enabled = route.isEnabled();
                    boolean supportsType = route.supportsMessageType(message.getMessageType());
                    log.debug("Route {}: enabled={}, supportsType={}, messageTypes={}",
                             route.getRouteId(), enabled, supportsType, route.getMessageTypes());
                    return enabled && supportsType;
                })
                .collect(Collectors.toList());

        log.debug("Found {} suitable routes out of {} total routes", suitableRoutes.size(), routes.size());
        return suitableRoutes;
    }

    /**
     * Selects the best route from a list of suitable routes.
     *
     * @param suitableRoutes the list of suitable routes
     * @param message the message to route
     * @return the best route, or null if none can be selected
     */
    private MessageRoute selectBestRoute(List<MessageRoute> suitableRoutes, BaseMessage message) {
        // Sort by priority (higher priority first)
        return suitableRoutes.stream()
                .sorted(Comparator.comparing(MessageRoute::getPriority).reversed())
                .findFirst()
                .orElse(null);
    }
}
