/**
 * Secure message routing and filtering for ISO 20022 messages.
 *
 * <p>This package provides comprehensive message routing capabilities with integrated
 * security filtering, business rule validation, and compliance monitoring. The routing
 * system ensures that messages are only sent through secure channels that meet the
 * required security and business criteria.</p>
 *
 * <h2>Key Components</h2>
 *
 * <h3>Core Routing</h3>
 * <ul>
 *   <li>{@link com.finqube.iso20022.core.security.routing.SecureMessageRouter} - Main routing interface</li>
 *   <li>{@link com.finqube.iso20022.core.security.routing.MessageRoute} - Route definition and configuration</li>
 *   <li>{@link com.finqube.iso20022.core.security.routing.SecurityLevel} - Security level enumeration</li>
 * </ul>
 *
 * <h3>Filtering System</h3>
 * <ul>
 *   <li>{@link com.finqube.iso20022.core.security.routing.MessageFilter} - Filter interface</li>
 *   <li>{@link com.finqube.iso20022.core.security.routing.filters.SecurityLevelFilter} - Security level filtering</li>
 *   <li>{@link com.finqube.iso20022.core.security.routing.filters.CompositeFilter} - Composite filtering logic</li>
 * </ul>
 *
 * <h3>Results and Monitoring</h3>
 * <ul>
 *   <li>{@link com.finqube.iso20022.core.security.routing.RoutingResult} - Routing operation results</li>
 *   <li>{@link com.finqube.iso20022.core.security.routing.FilteringResult} - Filtering operation results</li>
 *   <li>{@link com.finqube.iso20022.core.security.routing.RoutingStatistics} - Performance statistics</li>
 *   <li>{@link com.finqube.iso20022.core.security.routing.RouterHealthCheck} - Health monitoring</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 *
 * <pre>{@code
 * @Autowired
 * private SecureMessageRouter router;
 *
 * // Configure routes
 * MessageRoute swiftRoute = MessageRoute.builder()
 *     .routeId("SWIFT_PACS_008")
 *     .displayName("SWIFT PACS.008 Route")
 *     .destination("swift://fin.primary")
 *     .messageTypes(Set.of("pacs.008.001.08"))
 *     .requiredSecurityLevel(SecurityLevel.HIGH)
 *     .priority(100)
 *     .build();
 *
 * router.addRoute(swiftRoute);
 *
 * // Add security filters
 * SecurityLevelFilter securityFilter = new SecurityLevelFilter(SecurityLevel.HIGH);
 * router.addFilter(securityFilter);
 *
 * // Route a message
 * RoutingResult result = router.routeMessage(message, userId);
 *
 * if (result.isSuccessful()) {
 *     log.info("Message routed to: {}", result.getSelectedRoute().getRouteId());
 * } else {
 *     log.warn("Routing failed: {}", result.getFailureReason());
 * }
 * }</pre>
 *
 * <h2>Security Features</h2>
 *
 * <ul>
 *   <li><strong>Security Level Validation</strong> - Ensures messages are routed through channels with appropriate security</li>
 *   <li><strong>Business Rule Filtering</strong> - Applies business logic and compliance rules</li>
 *   <li><strong>Audit Trail</strong> - Comprehensive logging of all routing decisions</li>
 *   <li><strong>Performance Monitoring</strong> - Real-time statistics and health checks</li>
 *   <li><strong>Failover Support</strong> - Automatic route selection based on priority and availability</li>
 * </ul>
 *
 * <h2>Configuration</h2>
 *
 * <p>The routing system can be configured through Spring Boot properties:</p>
 *
 * <pre>{@code
 * # Enable secure routing
 * iso20022.routing.enabled=true
 *
 * # Default security level
 * iso20022.routing.default-security-level=MEDIUM
 *
 * # Route configuration
 * iso20022.routing.routes[0].id=SWIFT_PACS_008
 * iso20022.routing.routes[0].destination=swift://fin.primary
 * iso20022.routing.routes[0].message-types=pacs.008.001.08
 * iso20022.routing.routes[0].security-level=HIGH
 * iso20022.routing.routes[0].priority=100
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
package com.finqube.iso20022.core.security.routing;
