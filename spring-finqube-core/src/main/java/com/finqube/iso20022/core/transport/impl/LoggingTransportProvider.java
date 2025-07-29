package com.finqube.iso20022.core.transport.impl;

import com.finqube.iso20022.core.transport.Transport;
import com.finqube.iso20022.core.transport.TransportProvider;

/**
 * Provider for LoggingTransport implementation.
 *
 * <p>This provider registers the LoggingTransport with the transport SPI
 * for automatic discovery and instantiation.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class LoggingTransportProvider implements TransportProvider {

    @Override
    public String getTransportId() {
        return "logging";
    }

    @Override
    public String getDisplayName() {
        return "Logging Transport";
    }

    @Override
    public String getProtocolVersion() {
        return "1.0";
    }

    @Override
    public String getDescription() {
        return "Simple transport that logs messages instead of sending them. Useful for development and testing.";
    }

    @Override
    public Transport createTransport() throws Exception {
        return new LoggingTransport();
    }
}
