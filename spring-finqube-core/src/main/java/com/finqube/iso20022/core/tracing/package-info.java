/**
 * Distributed tracing package for Spring Finqube ISO 20022.
 *
 * <p>This package provides OpenTelemetry-based distributed tracing capabilities
 * for tracking ISO 20022 message processing across different services and components.</p>
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li><strong>OpenTelemetryTracer</strong>: Main tracer class for creating and managing spans</li>
 *   <li><strong>OpenTelemetryConfiguration</strong>: Spring configuration for OpenTelemetry setup</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * @Autowired
 * private OpenTelemetryTracer tracer;
 *
 * public void processMessage(String messageId, String messageType) {
 *     Span span = tracer.createMessageSpan("process-message", messageId, messageType);
 *     try (var scope = span.makeCurrent()) {
 *         // Process the message
 *         span.setAttribute("message.size", messageContent.length());
 *     } finally {
 *         span.end();
 *     }
 * }
 * }</pre>
 *
 * <h2>Configuration</h2>
 * <p>The tracing can be configured using the following properties:</p>
 * <ul>
 *   <li><code>opentelemetry.enabled</code>: Enable/disable tracing (default: true)</li>
 *   <li><code>opentelemetry.service.name</code>: Service name (default: spring-finqube-iso20022)</li>
 *   <li><code>opentelemetry.service.version</code>: Service version (default: 0.1.0)</li>
 *   <li><code>opentelemetry.otlp.endpoint</code>: OTLP exporter endpoint (default: http://localhost:4317)</li>
 *   <li><code>opentelemetry.sampler.probability</code>: Sampling probability (default: 1.0)</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
package com.finqube.iso20022.core.tracing;
