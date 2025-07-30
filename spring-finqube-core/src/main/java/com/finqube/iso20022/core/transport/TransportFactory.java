package com.finqube.iso20022.core.transport;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Factory for creating and managing transport instances.
 *
 * <p>This factory provides a centralized way to create, configure, and manage
 * transport instances. It supports automatic discovery of transport implementations
 * using the Java Service Provider Interface (SPI).</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * @Autowired
 * private TransportFactory transportFactory;
 *
 * Transport transport = transportFactory.getTransport("swiftnet");
 * TransportResponse response = transport.send(message);
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Component
public class TransportFactory implements TransportFactoryOperations {

    private static final Logger logger = LoggerFactory.getLogger(TransportFactory.class);

    private final Map<String, Transport> transportInstances = new ConcurrentHashMap<>();
    private final Map<String, TransportProvider> transportProviders = new ConcurrentHashMap<>();

    /**
     * Constructs a new TransportFactory and discovers available transports.
     */
    public TransportFactory() {
        discoverTransports();
    }

    /**
     * Discovers available transport implementations using SPI.
     */
    private void discoverTransports() {
        logger.info("Discovering transport implementations...");

        ServiceLoader<TransportProvider> loader = ServiceLoader.load(TransportProvider.class);

        StreamSupport.stream(loader.spliterator(), false)
                .forEach(provider -> {
                    String transportId = provider.getTransportId();
                    transportProviders.put(transportId, provider);
                    logger.info("Discovered transport: {} ({})", transportId, provider.getDisplayName());
                });

        logger.info("Discovered {} transport implementations", transportProviders.size());
    }

    /**
     * Gets a transport instance by ID.
     *
     * <p>This method returns a cached instance if available, or creates a new one
     * using the appropriate transport provider.</p>
     *
     * @param transportId the transport identifier
     * @return the transport instance
     * @throws TransportException if the transport is not available or cannot be created
     */
    public Transport getTransport(String transportId) throws TransportException {
        if (transportId == null) {
            throw new TransportException("Transport ID cannot be null", null, null, TransportStatus.UNAVAILABLE);
        }

        return transportInstances.computeIfAbsent(transportId, this::createTransport);
    }

    /**
     * Creates a new transport instance.
     *
     * @param transportId the transport identifier
     * @return the new transport instance
     * @throws TransportException if the transport cannot be created
     */
    private Transport createTransport(String transportId) throws TransportException {
        TransportProvider provider = transportProviders.get(transportId);

        if (provider == null) {
            throw new TransportException("Transport not found: " + transportId,
                transportId, null, TransportStatus.UNAVAILABLE);
        }

        try {
            logger.debug("Creating transport instance: {}", transportId);
            Transport transport = provider.createTransport();
            logger.info("Created transport instance: {} ({})", transportId, transport.getDisplayName());
            return transport;
        } catch (Exception e) {
            logger.error("Failed to create transport: {}", transportId, e);
            throw new TransportException("Failed to create transport: " + e.getMessage(),
                transportId, null, TransportStatus.FAILED, e);
        }
    }

    /**
     * Gets all available transport IDs.
     *
     * @return list of available transport IDs
     */
    public List<String> getAvailableTransportIds() {
        return List.copyOf(transportProviders.keySet());
    }

    /**
     * Gets information about all available transports.
     *
     * @return list of transport information
     */
    public List<TransportInfo> getAvailableTransports() {
        return transportProviders.values().stream()
                .map(provider -> new TransportInfo(
                    provider.getTransportId(),
                    provider.getDisplayName(),
                    provider.getProtocolVersion(),
                    provider.getDescription()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Checks if a transport is available.
     *
     * @param transportId the transport identifier
     * @return true if available, false otherwise
     */
    public boolean isTransportAvailable(String transportId) {
        return transportProviders.containsKey(transportId);
    }

    /**
     * Gets information about a specific transport.
     *
     * @param transportId the transport identifier
     * @return optional transport information
     */
    public Optional<TransportInfo> getTransportInfo(String transportId) {
        if (transportId == null) {
            throw new IllegalArgumentException("Transport ID cannot be null");
        }

        TransportProvider provider = transportProviders.get(transportId);
        if (provider == null) {
            return Optional.empty();
        }

        TransportInfo info = new TransportInfo(
            provider.getTransportId(),
            provider.getDisplayName(),
            provider.getProtocolVersion(),
            provider.getDescription()
        );

        return Optional.of(info);
    }

    /**
     * Gets transport statistics.
     *
     * @return transport statistics
     */
    public TransportStatistics getStatistics() {
        // TODO: Implement comprehensive transport statistics
        return new TransportStatistics("transport-factory", Instant.now(), Map.of(), Map.of());
    }

    /**
     * Closes all transport instances and releases resources.
     */
    public void closeAll() {
        logger.info("Closing all transport instances...");

        transportInstances.values().forEach(transport -> {
            try {
                transport.close();
            } catch (Exception e) {
                logger.error("Error closing transport: {}", transport.getTransportId(), e);
            }
        });

        transportInstances.clear();
        logger.info("All transport instances closed");
    }

    /**
     * Information about a transport implementation.
     */
    public static class TransportInfo {
        private final String transportId;
        private final String displayName;
        private final String protocolVersion;
        private final String description;

        public TransportInfo(String transportId, String displayName, String protocolVersion, String description) {
            this.transportId = transportId;
            this.displayName = displayName;
            this.protocolVersion = protocolVersion;
            this.description = description;
        }

        public String getTransportId() {
            return transportId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getProtocolVersion() {
            return protocolVersion;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "TransportInfo{" +
                    "transportId='" + transportId + '\'' +
                    ", displayName='" + displayName + '\'' +
                    ", protocolVersion='" + protocolVersion + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
}
