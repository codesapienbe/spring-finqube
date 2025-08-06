package com.finqube.iso20022.core.transport.security;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Configuration for TLS (Transport Layer Security) connections.
 *
 * <p>This class provides comprehensive configuration options for TLS 1.3
 * connections, including protocol versions, cipher suites, certificate
 * validation, and security parameters.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class TlsConfiguration {

    private final String[] enabledProtocols;
    private final String[] enabledCipherSuites;
    private final boolean requireClientAuth;
    private final boolean verifyHostname;
    private final String trustStorePath;
    private final String trustStorePassword;
    private final String trustStoreType;
    private final String keyStorePath;
    private final String keyStorePassword;
    private final String keyStoreType;
    private final String keyAlias;
    private final int connectionTimeout;
    private final int handshakeTimeout;
    private final boolean enableOcsp;
    private final boolean enableCrl;
    private final String[] allowedHostnames;

    /**
     * Constructs a new TlsConfiguration with the specified parameters.
     *
     * @param enabledProtocols the enabled TLS protocols
     * @param enabledCipherSuites the enabled cipher suites
     * @param requireClientAuth whether client authentication is required
     * @param verifyHostname whether to verify hostname
     * @param trustStorePath the trust store path
     * @param trustStorePassword the trust store password
     * @param trustStoreType the trust store type
     * @param keyStorePath the key store path
     * @param keyStorePassword the key store password
     * @param keyStoreType the key store type
     * @param keyAlias the key alias
     * @param connectionTimeout the connection timeout in milliseconds
     * @param handshakeTimeout the handshake timeout in milliseconds
     * @param enableOcsp whether to enable OCSP
     * @param enableCrl whether to enable CRL
     * @param allowedHostnames the allowed hostnames
     */
    public TlsConfiguration(String[] enabledProtocols, String[] enabledCipherSuites,
                           boolean requireClientAuth, boolean verifyHostname,
                           String trustStorePath, String trustStorePassword, String trustStoreType,
                           String keyStorePath, String keyStorePassword, String keyStoreType,
                           String keyAlias, int connectionTimeout, int handshakeTimeout,
                           boolean enableOcsp, boolean enableCrl, String[] allowedHostnames) {
        this.enabledProtocols = Objects.requireNonNull(enabledProtocols, "Enabled protocols cannot be null");
        this.enabledCipherSuites = Objects.requireNonNull(enabledCipherSuites, "Enabled cipher suites cannot be null");
        this.requireClientAuth = requireClientAuth;
        this.verifyHostname = verifyHostname;
        this.trustStorePath = trustStorePath;
        this.trustStorePassword = trustStorePassword;
        this.trustStoreType = trustStoreType;
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
        this.keyStoreType = keyStoreType;
        this.keyAlias = keyAlias;
        this.connectionTimeout = connectionTimeout;
        this.handshakeTimeout = handshakeTimeout;
        this.enableOcsp = enableOcsp;
        this.enableCrl = enableCrl;
        this.allowedHostnames = allowedHostnames;
    }

    /**
     * Gets the enabled TLS protocols.
     *
     * @return the enabled protocols
     */
    public String[] getEnabledProtocols() {
        return enabledProtocols.clone();
    }

    /**
     * Gets the enabled cipher suites.
     *
     * @return the enabled cipher suites
     */
    public String[] getEnabledCipherSuites() {
        return enabledCipherSuites.clone();
    }

    /**
     * Checks if client authentication is required.
     *
     * @return true if client authentication is required, false otherwise
     */
    public boolean isRequireClientAuth() {
        return requireClientAuth;
    }

    /**
     * Checks if hostname verification is enabled.
     *
     * @return true if hostname verification is enabled, false otherwise
     */
    public boolean isVerifyHostname() {
        return verifyHostname;
    }

    /**
     * Gets the trust store path.
     *
     * @return the trust store path
     */
    public String getTrustStorePath() {
        return trustStorePath;
    }

    /**
     * Gets the trust store password.
     *
     * @return the trust store password
     */
    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    /**
     * Gets the trust store type.
     *
     * @return the trust store type
     */
    public String getTrustStoreType() {
        return trustStoreType;
    }

    /**
     * Gets the key store path.
     *
     * @return the key store path
     */
    public String getKeyStorePath() {
        return keyStorePath;
    }

    /**
     * Gets the key store password.
     *
     * @return the key store password
     */
    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    /**
     * Gets the key store type.
     *
     * @return the key store type
     */
    public String getKeyStoreType() {
        return keyStoreType;
    }

    /**
     * Gets the key alias.
     *
     * @return the key alias
     */
    public String getKeyAlias() {
        return keyAlias;
    }

    /**
     * Gets the connection timeout in milliseconds.
     *
     * @return the connection timeout
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Gets the handshake timeout in milliseconds.
     *
     * @return the handshake timeout
     */
    public int getHandshakeTimeout() {
        return handshakeTimeout;
    }

    /**
     * Checks if OCSP is enabled.
     *
     * @return true if OCSP is enabled, false otherwise
     */
    public boolean isEnableOcsp() {
        return enableOcsp;
    }

    /**
     * Checks if CRL is enabled.
     *
     * @return true if CRL is enabled, false otherwise
     */
    public boolean isEnableCrl() {
        return enableCrl;
    }

    /**
     * Gets the allowed hostnames.
     *
     * @return the allowed hostnames
     */
    public String[] getAllowedHostnames() {
        return allowedHostnames != null ? allowedHostnames.clone() : null;
    }

    /**
     * Checks if the configuration supports TLS 1.3.
     *
     * @return true if TLS 1.3 is supported, false otherwise
     */
    public boolean supportsTls13() {
        return Arrays.asList(enabledProtocols).contains("TLSv1.3");
    }

    /**
     * Gets a summary of the TLS configuration.
     *
     * @return the configuration summary
     */
    public String getSummary() {
        return String.format("TLS Configuration - Protocols: %s, Client Auth: %s, Hostname Verify: %s",
                Arrays.toString(enabledProtocols), requireClientAuth, verifyHostname);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TlsConfiguration that = (TlsConfiguration) obj;
        return requireClientAuth == that.requireClientAuth &&
               verifyHostname == that.verifyHostname &&
               connectionTimeout == that.connectionTimeout &&
               handshakeTimeout == that.handshakeTimeout &&
               enableOcsp == that.enableOcsp &&
               enableCrl == that.enableCrl &&
               Arrays.equals(enabledProtocols, that.enabledProtocols) &&
               Arrays.equals(enabledCipherSuites, that.enabledCipherSuites) &&
               Objects.equals(trustStorePath, that.trustStorePath) &&
               Objects.equals(trustStorePassword, that.trustStorePassword) &&
               Objects.equals(trustStoreType, that.trustStoreType) &&
               Objects.equals(keyStorePath, that.keyStorePath) &&
               Objects.equals(keyStorePassword, that.keyStorePassword) &&
               Objects.equals(keyStoreType, that.keyStoreType) &&
               Objects.equals(keyAlias, that.keyAlias) &&
               Arrays.equals(allowedHostnames, that.allowedHostnames);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(requireClientAuth, verifyHostname, trustStorePath, trustStorePassword,
                trustStoreType, keyStorePath, keyStorePassword, keyStoreType, keyAlias,
                connectionTimeout, handshakeTimeout, enableOcsp, enableCrl);
        result = 31 * result + Arrays.hashCode(enabledProtocols);
        result = 31 * result + Arrays.hashCode(enabledCipherSuites);
        result = 31 * result + Arrays.hashCode(allowedHostnames);
        return result;
    }

    @Override
    public String toString() {
        return String.format("TlsConfiguration{enabledProtocols=%s, requireClientAuth=%s, " +
                "verifyHostname=%s, trustStorePath='%s', keyStorePath='%s', connectionTimeout=%d, " +
                "handshakeTimeout=%d, enableOcsp=%s, enableCrl=%s}",
                Arrays.toString(enabledProtocols), requireClientAuth, verifyHostname,
                trustStorePath, keyStorePath, connectionTimeout, handshakeTimeout, enableOcsp, enableCrl);
    }

    /**
     * Creates a new builder for TlsConfiguration.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for TlsConfiguration.
     */
    public static class Builder {
        private String[] enabledProtocols = {"TLSv1.3", "TLSv1.2"};
        private String[] enabledCipherSuites = {};
        private boolean requireClientAuth = false;
        private boolean verifyHostname = true;
        private String trustStorePath;
        private String trustStorePassword;
        private String trustStoreType = "PKCS12";
        private String keyStorePath;
        private String keyStorePassword;
        private String keyStoreType = "PKCS12";
        private String keyAlias;
        private int connectionTimeout = 30000;
        private int handshakeTimeout = 10000;
        private boolean enableOcsp = true;
        private boolean enableCrl = true;
        private String[] allowedHostnames;

        public Builder enabledProtocols(String... enabledProtocols) {
            this.enabledProtocols = enabledProtocols;
            return this;
        }

        public Builder enabledCipherSuites(String... enabledCipherSuites) {
            this.enabledCipherSuites = enabledCipherSuites;
            return this;
        }

        public Builder requireClientAuth(boolean requireClientAuth) {
            this.requireClientAuth = requireClientAuth;
            return this;
        }

        public Builder verifyHostname(boolean verifyHostname) {
            this.verifyHostname = verifyHostname;
            return this;
        }

        public Builder trustStorePath(String trustStorePath) {
            this.trustStorePath = trustStorePath;
            return this;
        }

        public Builder trustStorePassword(String trustStorePassword) {
            this.trustStorePassword = trustStorePassword;
            return this;
        }

        public Builder trustStoreType(String trustStoreType) {
            this.trustStoreType = trustStoreType;
            return this;
        }

        public Builder keyStorePath(String keyStorePath) {
            this.keyStorePath = keyStorePath;
            return this;
        }

        public Builder keyStorePassword(String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
            return this;
        }

        public Builder keyStoreType(String keyStoreType) {
            this.keyStoreType = keyStoreType;
            return this;
        }

        public Builder keyAlias(String keyAlias) {
            this.keyAlias = keyAlias;
            return this;
        }

        public Builder connectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder handshakeTimeout(int handshakeTimeout) {
            this.handshakeTimeout = handshakeTimeout;
            return this;
        }

        public Builder enableOcsp(boolean enableOcsp) {
            this.enableOcsp = enableOcsp;
            return this;
        }

        public Builder enableCrl(boolean enableCrl) {
            this.enableCrl = enableCrl;
            return this;
        }

        public Builder allowedHostnames(String... allowedHostnames) {
            this.allowedHostnames = allowedHostnames;
            return this;
        }

        public TlsConfiguration build() {
            return new TlsConfiguration(enabledProtocols, enabledCipherSuites, requireClientAuth,
                    verifyHostname, trustStorePath, trustStorePassword, trustStoreType,
                    keyStorePath, keyStorePassword, keyStoreType, keyAlias, connectionTimeout,
                    handshakeTimeout, enableOcsp, enableCrl, allowedHostnames);
        }
    }
}
