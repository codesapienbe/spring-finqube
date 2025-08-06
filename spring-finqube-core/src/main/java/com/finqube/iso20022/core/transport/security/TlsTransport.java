package com.finqube.iso20022.core.transport.security;

import com.finqube.iso20022.core.transport.Transport;

/**
 * Transport interface for TLS-secured communication channels.
 *
 * <p>This interface extends the base Transport interface to provide
 * TLS-specific functionality for secure communication channels.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface TlsTransport extends Transport {

    /**
     * Gets the TLS configuration for this transport.
     *
     * @return the TLS configuration
     */
    TlsConfiguration getTlsConfiguration();

    /**
     * Gets the TLS protocol version being used.
     *
     * @return the TLS protocol version
     */
    String getTlsProtocolVersion();

    /**
     * Gets the cipher suite being used.
     *
     * @return the cipher suite
     */
    String getCipherSuite();

    /**
     * Gets the peer certificate information.
     *
     * @return the peer certificate information, or null if not available
     */
    String getPeerCertificateInfo();

    /**
     * Performs a TLS-specific health check.
     *
     * @return the TLS health check result
     */
    TlsHealthCheck tlsHealthCheck();
}
