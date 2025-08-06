package com.finqube.iso20022.core.transport.security;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Health check result for TLS connections.
 *
 * <p>This class provides detailed information about the health and security
 * status of TLS connections, including protocol version, cipher suite,
 * certificate validation, and connection status.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class TlsHealthCheck {

    private final String transportId;
    private final LocalDateTime timestamp;
    private final String status;
    private final String message;
    private final boolean healthy;
    private final String tlsProtocolVersion;
    private final String cipherSuite;
    private final boolean certificateValid;
    private final String certificateInfo;
    private final boolean hostnameVerified;
    private final long connectionCount;
    private final long failedConnectionCount;
    private final double averageHandshakeTimeMs;
    private final String securityLevel;

    /**
     * Constructs a new TlsHealthCheck instance.
     *
     * @param transportId the transport identifier
     * @param timestamp the timestamp of the health check
     * @param status the health status
     * @param message the health check message
     * @param healthy whether the TLS connection is healthy
     * @param tlsProtocolVersion the TLS protocol version
     * @param cipherSuite the cipher suite being used
     * @param certificateValid whether the certificate is valid
     * @param certificateInfo the certificate information
     * @param hostnameVerified whether the hostname was verified
     * @param connectionCount the number of successful connections
     * @param failedConnectionCount the number of failed connections
     * @param averageHandshakeTimeMs the average handshake time in milliseconds
     * @param securityLevel the security level assessment
     */
    public TlsHealthCheck(String transportId, LocalDateTime timestamp, String status, String message,
                         boolean healthy, String tlsProtocolVersion, String cipherSuite,
                         boolean certificateValid, String certificateInfo, boolean hostnameVerified,
                         long connectionCount, long failedConnectionCount, double averageHandshakeTimeMs,
                         String securityLevel) {
        this.transportId = Objects.requireNonNull(transportId, "Transport ID cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.message = message;
        this.healthy = healthy;
        this.tlsProtocolVersion = tlsProtocolVersion;
        this.cipherSuite = cipherSuite;
        this.certificateValid = certificateValid;
        this.certificateInfo = certificateInfo;
        this.hostnameVerified = hostnameVerified;
        this.connectionCount = connectionCount;
        this.failedConnectionCount = failedConnectionCount;
        this.averageHandshakeTimeMs = averageHandshakeTimeMs;
        this.securityLevel = securityLevel;
    }

    /**
     * Gets the transport identifier.
     *
     * @return the transport identifier
     */
    public String getTransportId() {
        return transportId;
    }

    /**
     * Gets the timestamp of the health check.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the health status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the health check message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Checks if the TLS connection is healthy.
     *
     * @return true if healthy, false otherwise
     */
    public boolean isHealthy() {
        return healthy;
    }

    /**
     * Gets the TLS protocol version.
     *
     * @return the TLS protocol version
     */
    public String getTlsProtocolVersion() {
        return tlsProtocolVersion;
    }

    /**
     * Gets the cipher suite being used.
     *
     * @return the cipher suite
     */
    public String getCipherSuite() {
        return cipherSuite;
    }

    /**
     * Checks if the certificate is valid.
     *
     * @return true if certificate is valid, false otherwise
     */
    public boolean isCertificateValid() {
        return certificateValid;
    }

    /**
     * Gets the certificate information.
     *
     * @return the certificate information
     */
    public String getCertificateInfo() {
        return certificateInfo;
    }

    /**
     * Checks if the hostname was verified.
     *
     * @return true if hostname was verified, false otherwise
     */
    public boolean isHostnameVerified() {
        return hostnameVerified;
    }

    /**
     * Gets the number of successful connections.
     *
     * @return the connection count
     */
    public long getConnectionCount() {
        return connectionCount;
    }

    /**
     * Gets the number of failed connections.
     *
     * @return the failed connection count
     */
    public long getFailedConnectionCount() {
        return failedConnectionCount;
    }

    /**
     * Gets the average handshake time in milliseconds.
     *
     * @return the average handshake time
     */
    public double getAverageHandshakeTimeMs() {
        return averageHandshakeTimeMs;
    }

    /**
     * Gets the security level assessment.
     *
     * @return the security level
     */
    public String getSecurityLevel() {
        return securityLevel;
    }

    /**
     * Checks if the TLS connection is unhealthy.
     *
     * @return true if unhealthy, false otherwise
     */
    public boolean isUnhealthy() {
        return !healthy;
    }

    /**
     * Checks if TLS 1.3 is being used.
     *
     * @return true if TLS 1.3 is being used, false otherwise
     */
    public boolean isTls13() {
        return "TLSv1.3".equals(tlsProtocolVersion);
    }

    /**
     * Gets the connection success rate.
     *
     * @return the success rate as a percentage
     */
    public double getSuccessRate() {
        long total = connectionCount + failedConnectionCount;
        return total > 0 ? (double) connectionCount / total * 100.0 : 0.0;
    }

    /**
     * Gets a summary of the TLS health check.
     *
     * @return the summary string
     */
    public String getSummary() {
        return String.format("TLS Health[%s] %s - %s - %s - Success Rate: %.1f%%",
                transportId, status, tlsProtocolVersion, securityLevel, getSuccessRate());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TlsHealthCheck that = (TlsHealthCheck) obj;
        return Objects.equals(transportId, that.transportId) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transportId, timestamp);
    }

    @Override
    public String toString() {
        return String.format("TlsHealthCheck{transportId='%s', timestamp=%s, status='%s', " +
                "message='%s', healthy=%s, tlsProtocolVersion='%s', cipherSuite='%s', " +
                "certificateValid=%s, hostnameVerified=%s, connectionCount=%d, " +
                "failedConnectionCount=%d, averageHandshakeTimeMs=%.2f, securityLevel='%s'}",
                transportId, timestamp, status, message, healthy, tlsProtocolVersion, cipherSuite,
                certificateValid, hostnameVerified, connectionCount, failedConnectionCount,
                averageHandshakeTimeMs, securityLevel);
    }
}
