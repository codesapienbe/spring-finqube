package com.finqube.iso20022.core.tracing;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * OpenTelemetry tracer for distributed tracing in ISO 20022 message processing.
 *
 * <p>This class provides comprehensive distributed tracing capabilities for tracking
 * ISO 20022 message processing across different services and components.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class OpenTelemetryTracer {

    private final Tracer tracer;
    private final String serviceName;
    private final String serviceVersion;

    // Semantic attribute constants
    private static final String MESSAGING_SYSTEM = "messaging.system";
    private static final String MESSAGING_OPERATION = "messaging.operation";
    private static final String MESSAGING_MESSAGE_PAYLOAD_SIZE_BYTES = "messaging.message.payload_size_bytes";
    private static final String DB_SYSTEM = "db.system";
    private static final String DB_NAME = "db.name";
    private static final String DB_TABLE = "db.table";
    private static final String DB_OPERATION = "db.operation";
    private static final String HTTP_METHOD = "http.method";
    private static final String HTTP_URL = "http.url";

    /**
     * Creates a new OpenTelemetry tracer.
     *
     * @param openTelemetry the OpenTelemetry instance
     * @param serviceName the service name
     * @param serviceVersion the service version
     */
    public OpenTelemetryTracer(OpenTelemetry openTelemetry, String serviceName, String serviceVersion) {
        this.tracer = openTelemetry.getTracer(serviceName, serviceVersion);
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
    }

    /**
     * Creates a new span for ISO 20022 message processing.
     *
     * @param operationName the operation name
     * @param messageId the message ID
     * @param messageType the message type
     * @return the created span
     */
    public Span createMessageSpan(String operationName, String messageId, String messageType) {
        return tracer.spanBuilder(operationName)
                .setSpanKind(SpanKind.INTERNAL)
                .setAttribute(MESSAGING_SYSTEM, "iso20022")
                .setAttribute(MESSAGING_OPERATION, operationName)
                .setAttribute("message.id", messageId)
                .setAttribute("message.type", messageType)
                .setAttribute("service.name", serviceName)
                .setAttribute("service.version", serviceVersion)
                .setAttribute(MESSAGING_MESSAGE_PAYLOAD_SIZE_BYTES, 0L)
                .startSpan();
    }

    /**
     * Creates a new span for database operations.
     *
     * @param operationName the operation name
     * @param tableName the table name
     * @param operation the database operation
     * @return the created span
     */
    public Span createDatabaseSpan(String operationName, String tableName, String operation) {
        return tracer.spanBuilder(operationName)
                .setSpanKind(SpanKind.CLIENT)
                .setAttribute(DB_SYSTEM, "postgresql")
                .setAttribute(DB_NAME, "finqube")
                .setAttribute(DB_TABLE, tableName)
                .setAttribute(DB_OPERATION, operation)
                .setAttribute("service.name", serviceName)
                .setAttribute("service.version", serviceVersion)
                .startSpan();
    }

    /**
     * Creates a new span for HTTP operations.
     *
     * @param operationName the operation name
     * @param method the HTTP method
     * @param url the URL
     * @return the created span
     */
    public Span createHttpSpan(String operationName, String method, String url) {
        return tracer.spanBuilder(operationName)
                .setSpanKind(SpanKind.CLIENT)
                .setAttribute(HTTP_METHOD, method)
                .setAttribute(HTTP_URL, url)
                .setAttribute("service.name", serviceName)
                .setAttribute("service.version", serviceVersion)
                .startSpan();
    }

    /**
     * Creates a new span for security operations.
     *
     * @param operationName the operation name
     * @param securityOperation the security operation type
     * @param target the security target
     * @return the created span
     */
    public Span createSecuritySpan(String operationName, String securityOperation, String target) {
        return tracer.spanBuilder(operationName)
                .setSpanKind(SpanKind.INTERNAL)
                .setAttribute("security.operation", securityOperation)
                .setAttribute("security.target", target)
                .setAttribute("service.name", serviceName)
                .setAttribute("service.version", serviceVersion)
                .startSpan();
    }

    /**
     * Executes a function within a span context.
     *
     * @param span the span to use
     * @param operation the operation to execute
     * @param <T> the return type
     * @return the result of the operation
     */
    public <T> T executeInSpan(Span span, Supplier<T> operation) {
        try (var scope = span.makeCurrent()) {
            span.setAttribute("start.time", Instant.now().toString());

            T result = operation.get();

            span.setAttribute("end.time", Instant.now().toString());
            span.setAttribute("status", "success");

            return result;
        } catch (Exception e) {
            span.setAttribute("status", "error");
            span.setAttribute("error.message", e.getMessage());
            span.setAttribute("error.type", e.getClass().getSimpleName());
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    /**
     * Executes a function within a span context asynchronously.
     *
     * @param span the span to use
     * @param operation the operation to execute
     * @param <T> the return type
     * @return a CompletableFuture with the result
     */
    public <T> CompletableFuture<T> executeInSpanAsync(Span span, Supplier<T> operation) {
        return CompletableFuture.supplyAsync(() -> {
            try (var scope = span.makeCurrent()) {
                span.setAttribute("start.time", Instant.now().toString());

                T result = operation.get();

                span.setAttribute("end.time", Instant.now().toString());
                span.setAttribute("status", "success");

                return result;
            } catch (Exception e) {
                span.setAttribute("status", "error");
                span.setAttribute("error.message", e.getMessage());
                span.setAttribute("error.type", e.getClass().getSimpleName());
                span.recordException(e);
                throw e;
            } finally {
                span.end();
            }
        });
    }

    /**
     * Adds attributes to the current span.
     *
     * @param attributes the attributes to add
     */
    public void addAttributes(Map<String, String> attributes) {
        if (attributes == null) {
            return; // Handle null attributes gracefully
        }
        Span currentSpan = Span.current();
        attributes.forEach(currentSpan::setAttribute);
    }

    /**
     * Adds an event to the current span.
     *
     * @param eventName the event name
     * @param attributes the event attributes
     */
    public void addEvent(String eventName, Map<String, String> attributes) {
        Span currentSpan = Span.current();
        // Create attributes using the correct API
        Attributes eventAttributes = Attributes.empty();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            eventAttributes = eventAttributes.toBuilder()
                    .put(entry.getKey(), entry.getValue())
                    .build();
        }
        currentSpan.addEvent(eventName, eventAttributes);
    }

    /**
     * Creates a child span from the current context.
     *
     * @param operationName the operation name
     * @return the child span
     */
    public Span createChildSpan(String operationName) {
        return tracer.spanBuilder(operationName)
                .setSpanKind(SpanKind.INTERNAL)
                .setAttribute("service.name", serviceName)
                .setAttribute("service.version", serviceVersion)
                .startSpan();
    }

    /**
     * Gets the current trace ID.
     *
     * @return the current trace ID
     */
    public String getCurrentTraceId() {
        return Span.current().getSpanContext().getTraceId();
    }

    /**
     * Gets the current span ID.
     *
     * @return the current span ID
     */
    public String getCurrentSpanId() {
        return Span.current().getSpanContext().getSpanId();
    }

    /**
     * Gets the tracer instance.
     *
     * @return the tracer
     */
    public Tracer getTracer() {
        return tracer;
    }

    /**
     * Gets the service name.
     *
     * @return the service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Gets the service version.
     *
     * @return the service version
     */
    public String getServiceVersion() {
        return serviceVersion;
    }
}
