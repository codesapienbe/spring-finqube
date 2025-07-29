package com.finqube.iso20022.core.transport;

/**
 * Provider interface for transport implementations.
 *
 * <p>This interface is used by the Java Service Provider Interface (SPI) to
 * discover and create transport implementations. Each transport implementation
 * should provide a concrete implementation of this interface.</p>
 *
 * <p>To register a transport implementation:</p>
 * <ol>
 *   <li>Implement this interface</li>
 *   <li>Create a file named {@code com.finqube.iso20022.core.transport.TransportProvider}
 *       in {@code META-INF/services/}</li>
 *   <li>Add the fully qualified class name of your implementation to that file</li>
 * </ol>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface TransportProvider {

    /**
     * Gets the transport identifier.
     *
     * <p>This identifier must be unique across all transport implementations.
     * It is used to register and discover the transport.</p>
     *
     * @return the transport identifier
     */
    String getTransportId();

    /**
     * Gets the human-readable display name.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Gets the protocol version.
     *
     * @return the protocol version
     */
    String getProtocolVersion();

    /**
     * Gets a description of the transport.
     *
     * @return the transport description
     */
    String getDescription();

    /**
     * Creates a new transport instance.
     *
     * <p>This method should create and configure a new transport instance
     * with appropriate settings and return it ready for use.</p>
     *
     * @return a new transport instance
     * @throws Exception if the transport cannot be created
     */
    Transport createTransport() throws Exception;
}
