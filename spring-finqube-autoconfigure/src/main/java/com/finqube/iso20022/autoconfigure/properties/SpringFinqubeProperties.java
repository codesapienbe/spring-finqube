package com.finqube.iso20022.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Configuration properties for Spring Finqube ISO 20022 functionality.
 *
 * <p>This class provides configuration properties that can be set in application.yml
 * or application.properties files to customize the behavior of the ISO 20022 starter.</p>
 *
 * <p>Example configuration:</p>
 * <pre>{@code
 * iso20022:
 *   enabled: true
 *   transport: swiftnet
 *   validation:
 *     enabled: true
 *     fail-fast: true
 *   security:
 *     keystore:
 *       location: classpath:keystore.p12
 *       password: ${KEYSTORE_PASSWORD}
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@ConfigurationProperties(prefix = "iso20022")
@Validated
public class SpringFinqubeProperties {

    /**
     * Whether ISO 20022 functionality is enabled.
     * Defaults to {@code true}.
     */
    private boolean enabled = true;

    /**
     * Transport protocol to use for message transmission.
     * Defaults to {@code "none"}.
     */
    @NotBlank(message = "Transport protocol must be specified")
    private String transport = "none";

    /**
     * Transport-specific configuration.
     */
    @NotNull
    private Transport transportConfig = new Transport();

    /**
     * Validation configuration.
     */
    @NotNull
    private Validation validation = new Validation();

    /**
     * Security configuration.
     */
    @NotNull
    private Security security = new Security();

    /**
     * Monitoring and metrics configuration.
     */
    @NotNull
    private Monitoring monitoring = new Monitoring();

    /**
     * Logging configuration.
     */
    @NotNull
    private Logging logging = new Logging();

    /**
     * Gets whether ISO 20022 functionality is enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether ISO 20022 functionality is enabled.
     *
     * @param enabled true to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the transport protocol.
     *
     * @return the transport protocol
     */
    public String getTransport() {
        return transport;
    }

    /**
     * Sets the transport protocol.
     *
     * @param transport the transport protocol
     */
    public void setTransport(String transport) {
        this.transport = transport;
    }

    /**
     * Gets the validation configuration.
     *
     * @return the validation configuration
     */
    public Validation getValidation() {
        return validation;
    }

    /**
     * Sets the validation configuration.
     *
     * @param validation the validation configuration
     */
    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    /**
     * Gets the security configuration.
     *
     * @return the security configuration
     */
    public Security getSecurity() {
        return security;
    }

        /**
     * Sets the security configuration.
     *
     * @param security the security configuration
     */
    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * Gets the transport configuration.
     *
     * @return the transport configuration
     */
    public Transport getTransportConfig() {
        return transportConfig;
    }

        /**
     * Sets the transport configuration.
     *
     * @param transportConfig the transport configuration
     */
    public void setTransportConfig(Transport transportConfig) {
        this.transportConfig = transportConfig;
    }

    /**
     * Gets the monitoring configuration.
     *
     * @return the monitoring configuration
     */
    public Monitoring getMonitoring() {
        return monitoring;
    }

    /**
     * Sets the monitoring configuration.
     *
     * @param monitoring the monitoring configuration
     */
    public void setMonitoring(Monitoring monitoring) {
        this.monitoring = monitoring;
    }

    /**
     * Gets the logging configuration.
     *
     * @return the logging configuration
     */
    public Logging getLogging() {
        return logging;
    }

    /**
     * Sets the logging configuration.
     *
     * @param logging the logging configuration
     */
    public void setLogging(Logging logging) {
        this.logging = logging;
    }

    /**
     * Validation configuration properties.
     */
    public static class Validation {

        /**
         * Whether message validation is enabled.
         * Defaults to {@code true}.
         */
        private boolean enabled = true;

        /**
         * Whether to fail fast on validation errors.
         * Defaults to {@code true}.
         */
        private boolean failFast = true;

        /**
         * Gets whether validation is enabled.
         *
         * @return true if enabled, false otherwise
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Sets whether validation is enabled.
         *
         * @param enabled true to enable, false to disable
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * Gets whether to fail fast on validation errors.
         *
         * @return true if fail fast is enabled, false otherwise
         */
        public boolean isFailFast() {
            return failFast;
        }

        /**
         * Sets whether to fail fast on validation errors.
         *
         * @param failFast true to enable fail fast, false to disable
         */
        public void setFailFast(boolean failFast) {
            this.failFast = failFast;
        }
    }

    /**
     * Security configuration properties.
     */
    public static class Security {

        /**
         * Keystore configuration.
         */
        @NotNull
        private Keystore keystore = new Keystore();

        /**
         * Gets the keystore configuration.
         *
         * @return the keystore configuration
         */
        public Keystore getKeystore() {
            return keystore;
        }

        /**
         * Sets the keystore configuration.
         *
         * @param keystore the keystore configuration
         */
        public void setKeystore(Keystore keystore) {
            this.keystore = keystore;
        }

        /**
         * Keystore configuration properties.
         */
        public static class Keystore {

            /**
             * Keystore file location.
             */
            private String location;

            /**
             * Keystore password.
             */
            private String password;

            /**
             * Keystore type.
             * Defaults to {@code "PKCS12"}.
             */
            private String type = "PKCS12";

            /**
             * Gets the keystore location.
             *
             * @return the keystore location
             */
            public String getLocation() {
                return location;
            }

            /**
             * Sets the keystore location.
             *
             * @param location the keystore location
             */
            public void setLocation(String location) {
                this.location = location;
            }

            /**
             * Gets the keystore password.
             *
             * @return the keystore password
             */
            public String getPassword() {
                return password;
            }

            /**
             * Sets the keystore password.
             *
             * @param password the keystore password
             */
            public void setPassword(String password) {
                this.password = password;
            }

            /**
             * Gets the keystore type.
             *
             * @return the keystore type
             */
            public String getType() {
                return type;
            }

            /**
             * Sets the keystore type.
             *
             * @param type the keystore type
             */
            public void setType(String type) {
                this.type = type;
            }
        }
    }

    /**
     * Transport configuration properties.
     */
    public static class Transport {

        /**
         * Connection timeout in milliseconds.
         * Defaults to {@code 30000} (30 seconds).
         */
        private int connectionTimeout = 30000;

        /**
         * Read timeout in milliseconds.
         * Defaults to {@code 60000} (60 seconds).
         */
        private int readTimeout = 60000;

        /**
         * Maximum number of retry attempts.
         * Defaults to {@code 3}.
         */
        private int maxRetries = 3;

        /**
         * Retry delay in milliseconds.
         * Defaults to {@code 1000} (1 second).
         */
        private int retryDelay = 1000;

        /**
         * Whether to enable connection pooling.
         * Defaults to {@code true}.
         */
        private boolean connectionPooling = true;

        /**
         * Maximum number of connections in pool.
         * Defaults to {@code 10}.
         */
        private int maxConnections = 10;

        /**
         * Gets the connection timeout.
         *
         * @return the connection timeout in milliseconds
         */
        public int getConnectionTimeout() {
            return connectionTimeout;
        }

        /**
         * Sets the connection timeout.
         *
         * @param connectionTimeout the connection timeout in milliseconds
         */
        public void setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        /**
         * Gets the read timeout.
         *
         * @return the read timeout in milliseconds
         */
        public int getReadTimeout() {
            return readTimeout;
        }

        /**
         * Sets the read timeout.
         *
         * @param readTimeout the read timeout in milliseconds
         */
        public void setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }

        /**
         * Gets the maximum number of retry attempts.
         *
         * @return the maximum number of retries
         */
        public int getMaxRetries() {
            return maxRetries;
        }

        /**
         * Sets the maximum number of retry attempts.
         *
         * @param maxRetries the maximum number of retries
         */
        public void setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
        }

        /**
         * Gets the retry delay.
         *
         * @return the retry delay in milliseconds
         */
        public int getRetryDelay() {
            return retryDelay;
        }

        /**
         * Sets the retry delay.
         *
         * @param retryDelay the retry delay in milliseconds
         */
        public void setRetryDelay(int retryDelay) {
            this.retryDelay = retryDelay;
        }

        /**
         * Gets whether connection pooling is enabled.
         *
         * @return true if connection pooling is enabled, false otherwise
         */
        public boolean isConnectionPooling() {
            return connectionPooling;
        }

        /**
         * Sets whether connection pooling is enabled.
         *
         * @param connectionPooling true to enable connection pooling, false to disable
         */
        public void setConnectionPooling(boolean connectionPooling) {
            this.connectionPooling = connectionPooling;
        }

        /**
         * Gets the maximum number of connections in pool.
         *
         * @return the maximum number of connections
         */
        public int getMaxConnections() {
            return maxConnections;
        }

        /**
         * Sets the maximum number of connections in pool.
         *
         * @param maxConnections the maximum number of connections
         */
        public void setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
        }
    }

    /**
     * Monitoring and metrics configuration properties.
     */
    public static class Monitoring {

        /**
         * Whether to enable metrics collection.
         * Defaults to {@code true}.
         */
        private boolean enabled = true;

        /**
         * Metrics collection interval in seconds.
         * Defaults to {@code 60}.
         */
        private int collectionInterval = 60;

        /**
         * Whether to enable health checks.
         * Defaults to {@code true}.
         */
        private boolean healthChecks = true;

        /**
         * Gets whether monitoring is enabled.
         *
         * @return true if monitoring is enabled, false otherwise
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Sets whether monitoring is enabled.
         *
         * @param enabled true to enable monitoring, false to disable
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * Gets the metrics collection interval.
         *
         * @return the collection interval in seconds
         */
        public int getCollectionInterval() {
            return collectionInterval;
        }

        /**
         * Sets the metrics collection interval.
         *
         * @param collectionInterval the collection interval in seconds
         */
        public void setCollectionInterval(int collectionInterval) {
            this.collectionInterval = collectionInterval;
        }

        /**
         * Gets whether health checks are enabled.
         *
         * @return true if health checks are enabled, false otherwise
         */
        public boolean isHealthChecks() {
            return healthChecks;
        }

        /**
         * Sets whether health checks are enabled.
         *
         * @param healthChecks true to enable health checks, false to disable
         */
        public void setHealthChecks(boolean healthChecks) {
            this.healthChecks = healthChecks;
        }
    }

    /**
     * Logging configuration properties.
     */
    public static class Logging {

        /**
         * Log level for ISO 20022 operations.
         * Defaults to {@code "INFO"}.
         */
        private String level = "INFO";

        /**
         * Whether to log message content.
         * Defaults to {@code false}.
         */
        private boolean logMessageContent = false;

        /**
         * Whether to log sensitive data.
         * Defaults to {@code false}.
         */
        private boolean logSensitiveData = false;

        /**
         * Gets the log level.
         *
         * @return the log level
         */
        public String getLevel() {
            return level;
        }

        /**
         * Sets the log level.
         *
         * @param level the log level
         */
        public void setLevel(String level) {
            this.level = level;
        }

        /**
         * Gets whether message content should be logged.
         *
         * @return true if message content should be logged, false otherwise
         */
        public boolean isLogMessageContent() {
            return logMessageContent;
        }

        /**
         * Sets whether message content should be logged.
         *
         * @param logMessageContent true to log message content, false to disable
         */
        public void setLogMessageContent(boolean logMessageContent) {
            this.logMessageContent = logMessageContent;
        }

        /**
         * Gets whether sensitive data should be logged.
         *
         * @return true if sensitive data should be logged, false otherwise
         */
        public boolean isLogSensitiveData() {
            return logSensitiveData;
        }

        /**
         * Sets whether sensitive data should be logged.
         *
         * @param logSensitiveData true to log sensitive data, false to disable
         */
        public void setLogSensitiveData(boolean logSensitiveData) {
            this.logSensitiveData = logSensitiveData;
        }
    }
}
