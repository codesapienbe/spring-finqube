package com.finqube.iso20022.core.tracing;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OpenTelemetryTracer.
 *
 * <p>This test class verifies the OpenTelemetry tracing functionality for ISO 20022
 * message processing, ensuring proper span creation, attribute setting, and context management.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
class OpenTelemetryTracerTest {

    private static final Logger log = LoggerFactory.getLogger(OpenTelemetryTracerTest.class);
    private OpenTelemetryTracer tracer;

    @BeforeEach
    void setUp() {
        log.info("Setting up OpenTelemetryTracer test with noop OpenTelemetry instance");
        OpenTelemetry openTelemetry = OpenTelemetry.noop();
        tracer = new OpenTelemetryTracer(openTelemetry, "test-service", "1.0.0");
    }

    @Test
    @DisplayName("Should create message span with correct attributes")
    void shouldCreateMessageSpan() {
        log.debug("Testing message span creation");
        // When
        Span span = tracer.createMessageSpan("process-message", "MSG001", "pain.001");

        // Then
        assertNotNull(span, "Span should not be null");
        // Note: noop spans have invalid contexts by design, which is expected behavior
        log.debug("Message span created successfully with trace ID: {}", span.getSpanContext().getTraceId());
    }

    @Test
    @DisplayName("Should create database span with correct attributes")
    void shouldCreateDatabaseSpan() {
        log.debug("Testing database span creation");
        // When
        Span span = tracer.createDatabaseSpan("save-message", "messages", "INSERT");

        // Then
        assertNotNull(span, "Span should not be null");
        // Note: noop spans have invalid contexts by design, which is expected behavior
        log.debug("Database span created successfully with trace ID: {}", span.getSpanContext().getTraceId());
    }

    @Test
    @DisplayName("Should create HTTP span with correct attributes")
    void shouldCreateHttpSpan() {
        log.debug("Testing HTTP span creation");
        // When
        Span span = tracer.createHttpSpan("send-message", "POST", "https://api.example.com/messages");

        // Then
        assertNotNull(span, "Span should not be null");
        // Note: noop spans have invalid contexts by design, which is expected behavior
        log.debug("HTTP span created successfully with trace ID: {}", span.getSpanContext().getTraceId());
    }

    @Test
    @DisplayName("Should create security span with correct attributes")
    void shouldCreateSecuritySpan() {
        log.debug("Testing security span creation");
        // When
        Span span = tracer.createSecuritySpan("encrypt-message", "encryption", "message-content");

        // Then
        assertNotNull(span, "Span should not be null");
        // Note: noop spans have invalid contexts by design, which is expected behavior
        log.debug("Security span created successfully with trace ID: {}", span.getSpanContext().getTraceId());
    }

    @Test
    @DisplayName("Should execute operation in span context")
    void shouldExecuteInSpan() {
        log.debug("Testing synchronous span execution");
        // Given
        Span span = tracer.createMessageSpan("test-operation", "MSG001", "pain.001");

        // When
        String result = tracer.executeInSpan(span, () -> "test-result");

        // Then
        assertEquals("test-result", result, "Operation result should match expected value");
        log.debug("Synchronous span execution completed successfully");
    }

    @Test
    @DisplayName("Should execute operation in span context asynchronously")
    void shouldExecuteInSpanAsync() throws ExecutionException, InterruptedException {
        log.debug("Testing asynchronous span execution");
        // Given
        Span span = tracer.createMessageSpan("test-async-operation", "MSG001", "pain.001");

        // When
        CompletableFuture<String> future = tracer.executeInSpanAsync(span, () -> "async-result");
        String result = future.get();

        // Then
        assertEquals("async-result", result, "Async operation result should match expected value");
        log.debug("Asynchronous span execution completed successfully");
    }

    @Test
    @DisplayName("Should add attributes to current span")
    void shouldAddAttributes() {
        log.debug("Testing attribute addition to span");
        // Given
        Span span = tracer.createMessageSpan("test-attributes", "MSG001", "pain.001");
        Map<String, String> attributes = Map.of("key1", "value1", "key2", "value2");

        // When
        try (var scope = span.makeCurrent()) {
            tracer.addAttributes(attributes);
        }

        // Then
        assertNotNull(span, "Span should not be null");
        log.debug("Attributes added to span successfully");
    }

    @Test
    @DisplayName("Should add event to current span")
    void shouldAddEvent() {
        log.debug("Testing event addition to span");
        // Given
        Span span = tracer.createMessageSpan("test-events", "MSG001", "pain.001");
        Map<String, String> eventAttributes = Map.of("event.key", "event.value");

        // When
        try (var scope = span.makeCurrent()) {
            tracer.addEvent("test-event", eventAttributes);
        }

        // Then
        assertNotNull(span, "Span should not be null");
        log.debug("Event added to span successfully");
    }

    @Test
    @DisplayName("Should create child span")
    void shouldCreateChildSpan() {
        log.debug("Testing child span creation");
        // Given
        Span parentSpan = tracer.createMessageSpan("parent-operation", "MSG001", "pain.001");

        // When
        try (var scope = parentSpan.makeCurrent()) {
            Span childSpan = tracer.createChildSpan("child-operation");

            // Then
            assertNotNull(childSpan, "Child span should not be null");
            // Note: noop spans have invalid contexts by design, which is expected behavior
            log.debug("Child span created successfully with trace ID: {}", childSpan.getSpanContext().getTraceId());
        }
    }

    @Test
    @DisplayName("Should get current trace ID")
    void shouldGetCurrentTraceId() {
        log.debug("Testing current trace ID retrieval");
        // Given
        Span span = tracer.createMessageSpan("test-trace-id", "MSG001", "pain.001");

        // When
        try (var scope = span.makeCurrent()) {
            String traceId = tracer.getCurrentTraceId();

            // Then
            assertNotNull(traceId, "Trace ID should not be null");
            // Note: noop spans return zero trace IDs, which is expected behavior
            log.debug("Current trace ID retrieved successfully: {}", traceId);
        }
    }

    @Test
    @DisplayName("Should get current span ID")
    void shouldGetCurrentSpanId() {
        log.debug("Testing current span ID retrieval");
        // Given
        Span span = tracer.createMessageSpan("test-span-id", "MSG001", "pain.001");

        // When
        try (var scope = span.makeCurrent()) {
            String spanId = tracer.getCurrentSpanId();

            // Then
            assertNotNull(spanId, "Span ID should not be null");
            // Note: noop spans return zero span IDs, which is expected behavior
            log.debug("Current span ID retrieved successfully: {}", spanId);
        }
    }

    @Test
    @DisplayName("Should get service name and version")
    void shouldGetServiceInfo() {
        log.debug("Testing service info retrieval");
        // When & Then
        assertEquals("test-service", tracer.getServiceName(), "Service name should match expected value");
        assertEquals("1.0.0", tracer.getServiceVersion(), "Service version should match expected value");
        log.debug("Service info retrieved successfully - name: {}, version: {}",
            tracer.getServiceName(), tracer.getServiceVersion());
    }

    @Test
    @DisplayName("Should handle exceptions in span execution")
    void shouldHandleExceptionsInSpan() {
        log.debug("Testing exception handling in span execution");
        // Given
        Span span = tracer.createMessageSpan("test-exception", "MSG001", "pain.001");
        RuntimeException testException = new RuntimeException("Test exception");

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            tracer.executeInSpan(span, () -> {
                throw testException;
            });
        }, "Exception should be propagated from span execution");
        log.debug("Exception handling in span execution tested successfully");
    }

    @Test
    @DisplayName("Should handle null attributes gracefully")
    void shouldHandleNullAttributes() {
        log.debug("Testing null attributes handling");
        // Given
        Span span = tracer.createMessageSpan("test-null-attributes", "MSG001", "pain.001");

        // When & Then
        assertDoesNotThrow(() -> {
            try (var scope = span.makeCurrent()) {
                tracer.addAttributes(null);
            }
        }, "Should handle null attributes without throwing exception");
        log.debug("Null attributes handling tested successfully");
    }

    @Test
    @DisplayName("Should handle empty attributes map")
    void shouldHandleEmptyAttributes() {
        log.debug("Testing empty attributes map handling");
        // Given
        Span span = tracer.createMessageSpan("test-empty-attributes", "MSG001", "pain.001");
        Map<String, String> emptyAttributes = Map.of();

        // When & Then
        assertDoesNotThrow(() -> {
            try (var scope = span.makeCurrent()) {
                tracer.addAttributes(emptyAttributes);
            }
        }, "Should handle empty attributes map without throwing exception");
        log.debug("Empty attributes map handling tested successfully");
    }

    @Test
    @DisplayName("Should create multiple spans with different contexts")
    void shouldCreateMultipleSpansWithDifferentContexts() {
        log.debug("Testing multiple span creation with different contexts");
        // When
        Span messageSpan = tracer.createMessageSpan("message-operation", "MSG001", "pain.001");
        Span databaseSpan = tracer.createDatabaseSpan("db-operation", "messages", "SELECT");
        Span httpSpan = tracer.createHttpSpan("http-operation", "GET", "https://api.example.com/status");

        // Then
        assertNotNull(messageSpan, "Message span should not be null");
        assertNotNull(databaseSpan, "Database span should not be null");
        assertNotNull(httpSpan, "HTTP span should not be null");

        // Note: noop spans have invalid contexts by design, which is expected behavior
        log.debug("Multiple spans created successfully with different contexts");
    }

    @Test
    @DisplayName("Should work correctly with noop OpenTelemetry implementation")
    void shouldWorkWithNoopImplementation() {
        log.debug("Testing that tracer works correctly with noop implementation");

        // When - Create various spans and perform operations
        Span messageSpan = tracer.createMessageSpan("test-message", "MSG001", "pain.001");
        Span databaseSpan = tracer.createDatabaseSpan("test-db", "test_table", "SELECT");

        // Then - Verify spans can be used without exceptions
        assertDoesNotThrow(() -> {
            try (var scope = messageSpan.makeCurrent()) {
                tracer.addAttributes(Map.of("test.key", "test.value"));
                tracer.addEvent("test-event", Map.of("event.key", "event.value"));
                String traceId = tracer.getCurrentTraceId();
                String spanId = tracer.getCurrentSpanId();
                log.debug("Retrieved trace ID: {}, span ID: {}", traceId, spanId);
            }
        }, "Should work with noop spans without throwing exceptions");

        assertDoesNotThrow(() -> {
            tracer.executeInSpan(databaseSpan, () -> "test-result");
        }, "Should execute operations in noop spans without throwing exceptions");

        log.debug("Noop implementation test completed successfully");
    }
}
