package com.finqube.iso20022.core.security.routing;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;
import com.finqube.iso20022.core.security.routing.*;
import com.finqube.iso20022.core.security.routing.filters.SecurityLevelFilter;
import com.finqube.iso20022.core.security.routing.impl.DefaultSecureMessageRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DefaultSecureMessageRouter.
 *
 * <p>This test class demonstrates the secure message routing and filtering
 * capabilities of the Spring Finqube ISO 20022 system.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@DisplayName("Default Secure Message Router Tests")
class DefaultSecureMessageRouterTest {

    private DefaultSecureMessageRouter router;
    private TestMessage testMessage;

    @BeforeEach
    void setUp() {
        router = new DefaultSecureMessageRouter("test-router", "Test Router", "1.0.0");
        testMessage = new TestMessage("TEST001", "pacs.008.001.08", "pacs", 50000.0);
    }

    @Test
    @DisplayName("Should route message successfully when suitable route exists")
    void shouldRouteMessageSuccessfully() throws Exception {
        // Given
        MessageRoute route = MessageRoute.builder()
                .routeId("SWIFT_PACS_008")
                .displayName("SWIFT PACS.008 Route")
                .destination("swift://fin.primary")
                .messageTypes(Set.of("pacs.008.001.08"))
                .requiredSecurityLevel(SecurityLevel.MEDIUM)
                .priority(100)
                .build();

        router.addRoute(route);

        // When
        RoutingResult result = router.routeMessage(testMessage, "user123");

        // Then
        assertTrue(result.isSuccessful());
        assertEquals("TEST001", result.getMessageId());
        assertEquals("SWIFT_PACS_008", result.getSelectedRoute().getRouteId());
        assertTrue(result.getRoutingDurationMillis() > 0);
    }

    @Test
    @DisplayName("Should filter message when security level is insufficient")
    void shouldFilterMessageWhenSecurityLevelInsufficient() throws Exception {
        // Given
        MessageRoute route = MessageRoute.builder()
                .routeId("SWIFT_PACS_008")
                .displayName("SWIFT PACS.008 Route")
                .destination("swift://fin.primary")
                .messageTypes(Set.of("pacs.008.001.08"))
                .requiredSecurityLevel(SecurityLevel.CRITICAL) // Requires critical security
                .priority(100)
                .build();

        // Add a filter that requires CRITICAL security level
        SecurityLevelFilter securityFilter = new SecurityLevelFilter(SecurityLevel.CRITICAL);
        router.addFilter(securityFilter);
        router.addRoute(route);

        // When
        RoutingResult result = router.routeMessage(testMessage, "user123");

        // Then
        assertFalse(result.isSuccessful());
        assertEquals("TEST001", result.getMessageId());
        assertEquals(RoutingException.RoutingErrorType.MESSAGE_FILTERED, result.getErrorType());
        assertTrue(result.getFailureReason().contains("filtered out"));
    }

    @Test
    @DisplayName("Should fail when no suitable route exists")
    void shouldFailWhenNoSuitableRouteExists() {
        // Given - no routes added

        // When & Then
        RoutingException exception = assertThrows(RoutingException.class, () -> {
            router.routeMessage(testMessage, "user123");
        });

        assertEquals(RoutingException.RoutingErrorType.NO_ROUTE_AVAILABLE, exception.getErrorType());
        assertEquals("TEST001", exception.getMessageId());
    }

    @Test
    @DisplayName("Should apply security level filter correctly")
    void shouldApplySecurityLevelFilterCorrectly() throws RoutingException {
        // Given
        SecurityLevelFilter filter = new SecurityLevelFilter(SecurityLevel.HIGH);
        router.addFilter(filter);

        // When
        FilteringResult result = router.evaluateFilters(testMessage);

        // Then
        assertFalse(result.isPassed()); // Message has MEDIUM security but filter requires HIGH
        assertEquals(1, result.getFailedFilters().size());
        assertEquals("security-level-filter", result.getFailedFilters().get(0));
    }

    @Test
    @DisplayName("Should select route with highest priority")
    void shouldSelectRouteWithHighestPriority() throws Exception {
        // Given
        MessageRoute lowPriorityRoute = MessageRoute.builder()
                .routeId("LOW_PRIORITY")
                .displayName("Low Priority Route")
                .destination("swift://fin.backup")
                .messageTypes(Set.of("pacs.008.001.08"))
                .requiredSecurityLevel(SecurityLevel.MEDIUM)
                .priority(50)
                .build();

        MessageRoute highPriorityRoute = MessageRoute.builder()
                .routeId("HIGH_PRIORITY")
                .displayName("High Priority Route")
                .destination("swift://fin.primary")
                .messageTypes(Set.of("pacs.008.001.08"))
                .requiredSecurityLevel(SecurityLevel.MEDIUM)
                .priority(100)
                .build();

        router.addRoute(lowPriorityRoute);
        router.addRoute(highPriorityRoute);

        // When
        RoutingResult result = router.routeMessage(testMessage, "user123");

        // Then
        assertTrue(result.isSuccessful());
        assertEquals("HIGH_PRIORITY", result.getSelectedRoute().getRouteId());
    }

    @Test
    @DisplayName("Should provide accurate statistics")
    void shouldProvideAccurateStatistics() throws Exception {
        // Given
        MessageRoute route = MessageRoute.builder()
                .routeId("TEST_ROUTE")
                .displayName("Test Route")
                .destination("test://destination")
                .messageTypes(Set.of("pacs.008.001.08"))
                .requiredSecurityLevel(SecurityLevel.MEDIUM)
                .build();

        router.addRoute(route);

        // When
        router.routeMessage(testMessage, "user123");
        router.routeMessage(testMessage, "user456");

        RoutingStatistics stats = router.getStatistics();

        // Then
        assertEquals(2, stats.getTotalMessagesRouted());
        assertEquals(2, stats.getSuccessfulRoutings());
        assertEquals(0, stats.getFailedRoutings());
        assertEquals(1, stats.getRoutesUsed().size());
        assertEquals(2L, stats.getRoutesUsed().get("TEST_ROUTE"));
        assertTrue(stats.getAverageRoutingTimeMillis() > 0);
    }

    @Test
    @DisplayName("Should perform health check correctly")
    void shouldPerformHealthCheckCorrectly() throws RoutingException {
        // Given - router with no routes

        // When
        RouterHealthCheck healthCheck = router.healthCheck();

        // Then
        assertFalse(healthCheck.isHealthy());
        assertEquals("UNHEALTHY", healthCheck.getStatus());
        assertEquals(0, healthCheck.getRouteCount());
        assertEquals(0, healthCheck.getFilterCount());

        // Add a route and check again
        MessageRoute route = MessageRoute.builder()
                .routeId("TEST_ROUTE")
                .displayName("Test Route")
                .destination("test://destination")
                .messageTypes(Set.of("pacs.008.001.08"))
                .requiredSecurityLevel(SecurityLevel.MEDIUM)
                .build();

        router.addRoute(route);
        healthCheck = router.healthCheck();

        assertTrue(healthCheck.isHealthy());
        assertEquals("HEALTHY", healthCheck.getStatus());
        assertEquals(1, healthCheck.getRouteCount());
    }

    @Test
    @DisplayName("Should handle async routing correctly")
    void shouldHandleAsyncRoutingCorrectly() throws Exception {
        // Given
        MessageRoute route = MessageRoute.builder()
                .routeId("ASYNC_ROUTE")
                .displayName("Async Route")
                .destination("async://destination")
                .messageTypes(Set.of("pacs.008.001.08"))
                .requiredSecurityLevel(SecurityLevel.MEDIUM)
                .build();

        router.addRoute(route);

        // When
        RoutingResult result = router.routeMessageAsync(testMessage, "user123").get();

        // Then
        assertTrue(result.isSuccessful());
        assertEquals("ASYNC_ROUTE", result.getSelectedRoute().getRouteId());
    }

    /**
     * Test implementation of BaseMessage for testing purposes.
     */
    private static class TestMessage implements BaseMessage {
        private final String messageId;
        private final String messageType;
        private final String businessProcess;
        private final double totalAmount;

        public TestMessage(String messageId, String messageType, String businessProcess, double totalAmount) {
            this.messageId = messageId;
            this.messageType = messageType;
            this.businessProcess = businessProcess;
            this.totalAmount = totalAmount;
        }

        @Override
        public String getMessageId() { return messageId; }

        @Override
        public LocalDateTime getCreationTime() { return LocalDateTime.now(); }

        @Override
        public String getMessageType() { return messageType; }

        @Override
        public String getBusinessProcess() { return businessProcess; }

        @Override
        public boolean validate() { return true; }

        @Override
        public String getDescription() { return "Test message for routing"; }

        @Override
        public boolean requiresAcknowledgment() { return false; }

        @Override
        public MessagePriority getPriority() { return MessagePriority.NORMAL; }

        @Override
        public String getSchemaVersion() { return "1.0"; }

        @Override
        public List<String> getTransactions() { return List.of("TXN001"); }

        @Override
        public int getTransactionCount() { return 1; }

        @Override
        public double getTotalAmount() { return totalAmount; }
    }
}
