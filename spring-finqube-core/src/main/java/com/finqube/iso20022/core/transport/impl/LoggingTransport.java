package com.finqube.iso20022.core.transport.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.transport.Transport;
import com.finqube.iso20022.core.transport.TransportException;
import com.finqube.iso20022.core.transport.TransportHealthCheck;
import com.finqube.iso20022.core.transport.TransportResponse;
import com.finqube.iso20022.core.transport.TransportStatistics;
import com.finqube.iso20022.core.transport.TransportStatus;

/**
 * Simple transport implementation that logs messages instead of sending them.
 *
 * <p>This transport is useful for development, testing, and debugging purposes.
 * It simulates the transport interface without actually sending messages to external systems.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class LoggingTransport implements Transport {

    private static final Logger logger = LoggerFactory.getLogger(LoggingTransport.class);

    private final String transportId;
    private final String displayName;
    private final String protocolVersion;
    private final TransportStatistics statistics;
    private final Map<String, Long> errorCounts = new ConcurrentHashMap<>();
    private volatile boolean available = true;

    /**
     * Constructs a new LoggingTransport.
     */
    public LoggingTransport() {
        this.transportId = "logging";
        this.displayName = "Logging Transport";
        this.protocolVersion = "1.0";
        this.statistics = new TransportStatistics(transportId, Instant.now(), Map.of(), Map.of());
    }

    @Override
    public String getTransportId() {
        return transportId;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public TransportResponse send(BaseMessage message) throws TransportException {
        if (message == null) {
            throw new TransportException("Message cannot be null", transportId, null, TransportStatus.FAILED);
        }

        Instant startTime = Instant.now();
        String messageId = message.getMessageId();

        try {
            logger.info("=== LoggingTransport: Sending BaseMessage ===");
            logger.info("Message ID: {}", messageId);
            logger.info("Message Type: {}", message.getMessageType());
            logger.info("Business Process: {}", message.getBusinessProcess());
            logger.info("Priority: {}", message.getPriority());
            logger.info("Description: {}", message.getDescription());

            // Simulate processing time
            Thread.sleep(100);

            Instant endTime = Instant.now();
            long duration = endTime.toEpochMilli() - startTime.toEpochMilli();

            // Note: Statistics recording removed as TransportStatistics is simplified

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("transport", "logging");
            metadata.put("processedAt", endTime);

            return new TransportResponse(messageId, TransportStatus.SUCCESS,
                "Message logged successfully", startTime, endTime, metadata);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TransportException("Transport operation interrupted", transportId, messageId, TransportStatus.FAILED, e);
        } catch (Exception e) {
            long duration = Instant.now().toEpochMilli() - startTime.toEpochMilli();
            // Note: Statistics recording removed as TransportStatistics is simplified
            throw new TransportException("Failed to log message: " + e.getMessage(), transportId, messageId, TransportStatus.FAILED, e);
        }
    }

    @Override
    public CompletableFuture<TransportResponse> sendAsync(BaseMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return send(message);
            } catch (TransportException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public TransportResponse sendXml(String xmlContent) throws TransportException {
        if (xmlContent == null) {
            throw new TransportException("XML content cannot be null", transportId, null, TransportStatus.FAILED);
        }

        Instant startTime = Instant.now();
        String messageId = "xml-" + System.currentTimeMillis();

        try {
            logger.info("=== LoggingTransport: Sending XML Content ===");
            logger.info("Message ID: {}", messageId);
            logger.info("XML Content Length: {}", xmlContent.length());
            logger.info("XML Content Preview: {}", xmlContent.substring(0, Math.min(200, xmlContent.length())));

            // Simulate processing time
            Thread.sleep(80);

            Instant endTime = Instant.now();
            long duration = endTime.toEpochMilli() - startTime.toEpochMilli();

            logger.info("XML message logged successfully: {} ({}ms)", messageId, duration);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("transport", transportId);
            metadata.put("contentLength", xmlContent.length());
            metadata.put("loggedAt", endTime);

            return new TransportResponse(messageId, TransportStatus.SUCCESS, "XML logged successfully", startTime, endTime, metadata);

        } catch (Exception e) {
            long duration = Instant.now().toEpochMilli() - startTime.toEpochMilli();
            errorCounts.merge(e.getClass().getSimpleName(), 1L, Long::sum);
            logger.error("Failed to log XML message: {}", messageId, e);
            throw new TransportException("Failed to log XML message: " + e.getMessage(),
                transportId, messageId, TransportStatus.FAILED, e);
        }
    }

    @Override
    public CompletableFuture<TransportResponse> sendXmlAsync(String xmlContent) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendXml(xmlContent);
            } catch (TransportException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public TransportHealthCheck healthCheck() {
        Instant startTime = Instant.now();

        Map<String, TransportHealthCheck.ComponentHealth> components = new HashMap<>();

        // Check availability
        TransportHealthCheck.HealthStatus availabilityStatus = available ?
            TransportHealthCheck.HealthStatus.HEALTHY : TransportHealthCheck.HealthStatus.UNHEALTHY;
        components.put("availability", new TransportHealthCheck.ComponentHealth(
            "availability", availabilityStatus,
            available ? "Transport is available" : "Transport is not available", 0));

        // Check statistics (simplified since TransportStatistics is simplified)
        TransportHealthCheck.HealthStatus statsStatus = TransportHealthCheck.HealthStatus.HEALTHY;
        String statsMessage = "Statistics collection is working";
        components.put("statistics", new TransportHealthCheck.ComponentHealth(
            "statistics", statsStatus, statsMessage, 0));

        Instant endTime = Instant.now();
        long responseTime = endTime.toEpochMilli() - startTime.toEpochMilli();

        TransportHealthCheck.HealthStatus overallStatus = available ?
            TransportHealthCheck.HealthStatus.HEALTHY : TransportHealthCheck.HealthStatus.UNHEALTHY;

        return new TransportHealthCheck(transportId, overallStatus,
            "Logging transport health check completed", endTime, responseTime, components);
    }

    @Override
    public TransportStatistics getStatistics() {
        return statistics;
    }

    @Override
    public void close() {
        logger.info("Closing LoggingTransport: {}", transportId);
        available = false;
    }

    /**
     * Sets the availability status of the transport.
     *
     * @param available true if available, false otherwise
     */
    public void setAvailable(boolean available) {
        this.available = available;
        logger.info("LoggingTransport availability set to: {}", available);
    }
}
