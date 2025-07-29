package com.finqube.iso20022.autoconfigure.properties;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for SpringFinqubeProperties.
 *
 * <p>This test class validates the configuration properties class,
 * including default values, property binding, and nested configuration objects.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@DisplayName("SpringFinqubeProperties Tests")
class SpringFinqubePropertiesTest {

    private SpringFinqubeProperties properties;

    @BeforeEach
    void setUp() {
        properties = new SpringFinqubeProperties();
    }

    @Test
    @DisplayName("Should have correct default values")
    void shouldHaveCorrectDefaultValues() {
        // Main properties
        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.getTransport()).isEqualTo("none");

        // Transport configuration
        SpringFinqubeProperties.Transport transport = properties.getTransportConfig();
        assertThat(transport.getConnectionTimeout()).isEqualTo(30000);
        assertThat(transport.getReadTimeout()).isEqualTo(60000);
        assertThat(transport.getMaxRetries()).isEqualTo(3);
        assertThat(transport.getRetryDelay()).isEqualTo(1000);
        assertThat(transport.isConnectionPooling()).isTrue();
        assertThat(transport.getMaxConnections()).isEqualTo(10);

        // Validation configuration
        SpringFinqubeProperties.Validation validation = properties.getValidation();
        assertThat(validation.isEnabled()).isTrue();
        assertThat(validation.isFailFast()).isTrue();

        // Security configuration
        SpringFinqubeProperties.Security security = properties.getSecurity();
        SpringFinqubeProperties.Security.Keystore keystore = security.getKeystore();
        assertThat(keystore.getLocation()).isNull();
        assertThat(keystore.getPassword()).isNull();
        assertThat(keystore.getType()).isEqualTo("PKCS12");

        // Monitoring configuration
        SpringFinqubeProperties.Monitoring monitoring = properties.getMonitoring();
        assertThat(monitoring.isEnabled()).isTrue();
        assertThat(monitoring.getCollectionInterval()).isEqualTo(60);
        assertThat(monitoring.isHealthChecks()).isTrue();

        // Logging configuration
        SpringFinqubeProperties.Logging logging = properties.getLogging();
        assertThat(logging.getLevel()).isEqualTo("INFO");
        assertThat(logging.isLogMessageContent()).isFalse();
        assertThat(logging.isLogSensitiveData()).isFalse();
    }

    @Test
    @DisplayName("Should set and get main properties")
    void shouldSetAndGetMainProperties() {
        // Given
        properties.setEnabled(false);
        properties.setTransport("swiftnet");

        // When & Then
        assertThat(properties.isEnabled()).isFalse();
        assertThat(properties.getTransport()).isEqualTo("swiftnet");
    }

    @Test
    @DisplayName("Should set and get transport configuration")
    void shouldSetAndGetTransportConfiguration() {
        // Given
        SpringFinqubeProperties.Transport transport = properties.getTransportConfig();
        transport.setConnectionTimeout(45000);
        transport.setReadTimeout(90000);
        transport.setMaxRetries(5);
        transport.setRetryDelay(2000);
        transport.setConnectionPooling(false);
        transport.setMaxConnections(20);

        // When & Then
        assertThat(transport.getConnectionTimeout()).isEqualTo(45000);
        assertThat(transport.getReadTimeout()).isEqualTo(90000);
        assertThat(transport.getMaxRetries()).isEqualTo(5);
        assertThat(transport.getRetryDelay()).isEqualTo(2000);
        assertThat(transport.isConnectionPooling()).isFalse();
        assertThat(transport.getMaxConnections()).isEqualTo(20);
    }

    @Test
    @DisplayName("Should set and get validation configuration")
    void shouldSetAndGetValidationConfiguration() {
        // Given
        SpringFinqubeProperties.Validation validation = properties.getValidation();
        validation.setEnabled(false);
        validation.setFailFast(false);

        // When & Then
        assertThat(validation.isEnabled()).isFalse();
        assertThat(validation.isFailFast()).isFalse();
    }

    @Test
    @DisplayName("Should set and get security configuration")
    void shouldSetAndGetSecurityConfiguration() {
        // Given
        SpringFinqubeProperties.Security security = properties.getSecurity();
        SpringFinqubeProperties.Security.Keystore keystore = security.getKeystore();
        keystore.setLocation("classpath:keystore.p12");
        keystore.setPassword("secret-password");
        keystore.setType("JKS");

        // When & Then
        assertThat(keystore.getLocation()).isEqualTo("classpath:keystore.p12");
        assertThat(keystore.getPassword()).isEqualTo("secret-password");
        assertThat(keystore.getType()).isEqualTo("JKS");
    }

    @Test
    @DisplayName("Should set and get monitoring configuration")
    void shouldSetAndGetMonitoringConfiguration() {
        // Given
        SpringFinqubeProperties.Monitoring monitoring = properties.getMonitoring();
        monitoring.setEnabled(false);
        monitoring.setCollectionInterval(120);
        monitoring.setHealthChecks(false);

        // When & Then
        assertThat(monitoring.isEnabled()).isFalse();
        assertThat(monitoring.getCollectionInterval()).isEqualTo(120);
        assertThat(monitoring.isHealthChecks()).isFalse();
    }

    @Test
    @DisplayName("Should set and get logging configuration")
    void shouldSetAndGetLoggingConfiguration() {
        // Given
        SpringFinqubeProperties.Logging logging = properties.getLogging();
        logging.setLevel("DEBUG");
        logging.setLogMessageContent(true);
        logging.setLogSensitiveData(true);

        // When & Then
        assertThat(logging.getLevel()).isEqualTo("DEBUG");
        assertThat(logging.isLogMessageContent()).isTrue();
        assertThat(logging.isLogSensitiveData()).isTrue();
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValuesGracefully() {
        // Given
        SpringFinqubeProperties.Security.Keystore keystore = properties.getSecurity().getKeystore();
        SpringFinqubeProperties.Logging logging = properties.getLogging();

        // When
        keystore.setLocation(null);
        keystore.setPassword(null);
        keystore.setType(null);
        logging.setLevel(null);

        // Then
        assertThat(keystore.getLocation()).isNull();
        assertThat(keystore.getPassword()).isNull();
        assertThat(keystore.getType()).isNull();
        assertThat(logging.getLevel()).isNull();
    }

    @Test
    @DisplayName("Should handle empty string values gracefully")
    void shouldHandleEmptyStringValuesGracefully() {
        // Given
        SpringFinqubeProperties.Security.Keystore keystore = properties.getSecurity().getKeystore();
        SpringFinqubeProperties.Logging logging = properties.getLogging();

        // When
        keystore.setLocation("");
        keystore.setPassword("");
        keystore.setType("");
        logging.setLevel("");

        // Then
        assertThat(keystore.getLocation()).isEqualTo("");
        assertThat(keystore.getPassword()).isEqualTo("");
        assertThat(keystore.getType()).isEqualTo("");
        assertThat(logging.getLevel()).isEqualTo("");
    }

    @Test
    @DisplayName("Should create new configuration objects when set")
    void shouldCreateNewConfigurationObjectsWhenSet() {
        // Given
        SpringFinqubeProperties.Transport newTransport = new SpringFinqubeProperties.Transport();
        SpringFinqubeProperties.Validation newValidation = new SpringFinqubeProperties.Validation();
        SpringFinqubeProperties.Security newSecurity = new SpringFinqubeProperties.Security();
        SpringFinqubeProperties.Monitoring newMonitoring = new SpringFinqubeProperties.Monitoring();
        SpringFinqubeProperties.Logging newLogging = new SpringFinqubeProperties.Logging();

        // When
        properties.setTransportConfig(newTransport);
        properties.setValidation(newValidation);
        properties.setSecurity(newSecurity);
        properties.setMonitoring(newMonitoring);
        properties.setLogging(newLogging);

        // Then
        assertThat(properties.getTransportConfig()).isSameAs(newTransport);
        assertThat(properties.getValidation()).isSameAs(newValidation);
        assertThat(properties.getSecurity()).isSameAs(newSecurity);
        assertThat(properties.getMonitoring()).isSameAs(newMonitoring);
        assertThat(properties.getLogging()).isSameAs(newLogging);
    }
}
