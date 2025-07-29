package com.finqube.iso20022.core.transport;

import java.util.concurrent.CompletableFuture;

import com.finqube.iso20022.core.message.BaseMessage;

/**
 * Main transport interface for ISO 20022 message transmission.
 *
 * <p>This interface defines the contract for all transport implementations that
 * handle the actual transmission of ISO 20022 messages to external systems.</p>
 *
 * <p>Transport implementations are responsible for:</p>
 * <ul>
 *   <li>Message serialization and formatting</li>
 *   <li>Connection management and pooling</li>
 *   <li>Security and authentication</li>
 *   <li>Error handling and retry logic</li>
 *   <li>Performance monitoring and metrics</li>
 * </ul>
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
public interface Transport {

    /**
     * Gets the transport identifier.
     *
     * <p>This identifier is used to register and discover transport implementations.
     * It should be unique across all transport types.</p>
     *
     * @return the transport identifier
     */
    String getTransportId();

    /**
     * Gets the transport display name.
     *
     * @return the human-readable transport name
     */
    String getDisplayName();

    /**
     * Gets the transport protocol version.
     *
     * @return the protocol version
     */
    String getProtocolVersion();

    /**
     * Checks if the transport is available and ready to send messages.
     *
     * <p>This method should perform a lightweight health check to verify
     * that the transport can accept new messages.</p>
     *
     * @return true if the transport is available, false otherwise
     */
    boolean isAvailable();

    /**
     * Sends an ISO 20022 message synchronously.
     *
     * <p>This method blocks until the message is sent and a response is received.
     * For long-running operations, consider using {@link #sendAsync(BaseMessage)}.</p>
     *
     * @param message the ISO 20022 message to send
     * @return the transport response containing status and details
     * @throws TransportException if the message cannot be sent
     */
    TransportResponse send(BaseMessage message) throws TransportException;

    /**
     * Sends an ISO 20022 message asynchronously.
     *
     * <p>This method returns immediately with a CompletableFuture that will
     * complete when the message is sent and a response is received.</p>
     *
     * @param message the ISO 20022 message to send
     * @return a CompletableFuture that will complete with the transport response
     */
    CompletableFuture<TransportResponse> sendAsync(BaseMessage message);

    /**
     * Sends raw XML content synchronously.
     *
     * <p>This method is useful for sending pre-formatted XML messages
     * without going through the BaseMessage validation.</p>
     *
     * @param xmlContent the XML content to send
     * @return the transport response containing status and details
     * @throws TransportException if the message cannot be sent
     */
    TransportResponse sendXml(String xmlContent) throws TransportException;

    /**
     * Sends raw XML content asynchronously.
     *
     * @param xmlContent the XML content to send
     * @return a CompletableFuture that will complete with the transport response
     */
    CompletableFuture<TransportResponse> sendXmlAsync(String xmlContent);

    /**
     * Performs a health check on the transport.
     *
     * <p>This method should perform a comprehensive health check including
     * connection verification, authentication, and basic functionality tests.</p>
     *
     * @return the health check result
     */
    TransportHealthCheck healthCheck();

    /**
     * Gets transport statistics and metrics.
     *
     * @return the transport statistics
     */
    TransportStatistics getStatistics();

    /**
     * Closes the transport and releases all resources.
     *
     * <p>This method should be called when the transport is no longer needed
     * to properly clean up connections, threads, and other resources.</p>
     */
    void close();
}
