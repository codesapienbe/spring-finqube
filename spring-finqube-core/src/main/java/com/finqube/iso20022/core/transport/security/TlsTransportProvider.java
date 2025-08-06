package com.finqube.iso20022.core.transport.security;

import com.finqube.iso20022.core.transport.Transport;
import com.finqube.iso20022.core.transport.TransportProvider;
import com.finqube.iso20022.core.transport.security.impl.DefaultTlsTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Provider for TLS transport implementations.
 *
 * <p>This provider registers TLS transport implementations with the transport factory,
 * making them available for use in the ISO 20022 message processing pipeline.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Component
public class TlsTransportProvider implements TransportProvider {

    private final DefaultTlsTransport tlsTransport;

    @Autowired
    public TlsTransportProvider(DefaultTlsTransport tlsTransport) {
        this.tlsTransport = tlsTransport;
    }

    @Override
    public String getTransportId() {
        return "tls";
    }

    @Override
    public String getDisplayName() {
        return "TLS Transport Provider";
    }

    @Override
    public String getProtocolVersion() {
        return "TLS 1.3";
    }

    @Override
    public String getDescription() {
        return "Secure TLS 1.3 transport for ISO 20022 message transmission";
    }

    @Override
    public Transport createTransport() throws Exception {
        return tlsTransport;
    }
}
