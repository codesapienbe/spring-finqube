package com.finqube.iso20022.autoconfigure.validation;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import com.finqube.iso20022.autoconfigure.properties.SpringFinqubeProperties;

/**
 * Unit tests for SpringFinqubePropertiesValidator.
 *
 * <p>This test class validates the configuration validator,
 * ensuring that all validation rules are properly enforced.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@DisplayName("SpringFinqubePropertiesValidator Tests")
class SpringFinqubePropertiesValidatorTest {

    private SpringFinqubePropertiesValidator validator;
    private SpringFinqubeProperties properties;
    private Errors errors;

    @BeforeEach
    void setUp() {
        validator = new SpringFinqubePropertiesValidator();
        properties = new SpringFinqubeProperties();
        errors = new BeanPropertyBindingResult(properties, "properties");
    }

    @Test
    @DisplayName("Should support SpringFinqubeProperties class")
    void shouldSupportSpringFinqubePropertiesClass() {
        assertThat(validator.supports(SpringFinqubeProperties.class)).isTrue();
        assertThat(validator.supports(String.class)).isFalse();
    }

    @Test
    @DisplayName("Should validate valid configuration without errors")
    void shouldValidateValidConfigurationWithoutErrors() {
        // Given - Valid configuration
        properties.setTransport("none");
        properties.getTransportConfig().setConnectionTimeout(30000);
        properties.getTransportConfig().setReadTimeout(60000);
        properties.getMonitoring().setCollectionInterval(60);

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    @DisplayName("Should reject invalid connection timeout")
    void shouldRejectInvalidConnectionTimeout() {
        // Given
        properties.getTransportConfig().setConnectionTimeout(0);

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("transportConfig.connectionTimeout")).isNotNull();
        assertThat(errors.getFieldError("transportConfig.connectionTimeout").getDefaultMessage())
            .contains("must be positive");
    }

    @Test
    @DisplayName("Should reject invalid read timeout")
    void shouldRejectInvalidReadTimeout() {
        // Given
        properties.getTransportConfig().setReadTimeout(-1);

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("transportConfig.readTimeout")).isNotNull();
        assertThat(errors.getFieldError("transportConfig.readTimeout").getDefaultMessage())
            .contains("must be positive");
    }

    @Test
    @DisplayName("Should reject negative max retries")
    void shouldRejectNegativeMaxRetries() {
        // Given
        properties.getTransportConfig().setMaxRetries(-1);

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("transportConfig.maxRetries")).isNotNull();
        assertThat(errors.getFieldError("transportConfig.maxRetries").getDefaultMessage())
            .contains("must be non-negative");
    }

    @Test
    @DisplayName("Should reject negative retry delay")
    void shouldRejectNegativeRetryDelay() {
        // Given
        properties.getTransportConfig().setRetryDelay(-100);

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("transportConfig.retryDelay")).isNotNull();
        assertThat(errors.getFieldError("transportConfig.retryDelay").getDefaultMessage())
            .contains("must be non-negative");
    }

    @Test
    @DisplayName("Should reject invalid max connections")
    void shouldRejectInvalidMaxConnections() {
        // Given
        properties.getTransportConfig().setMaxConnections(0);

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("transportConfig.maxConnections")).isNotNull();
        assertThat(errors.getFieldError("transportConfig.maxConnections").getDefaultMessage())
            .contains("must be positive");
    }

    @Test
    @DisplayName("Should reject read timeout less than connection timeout")
    void shouldRejectReadTimeoutLessThanConnectionTimeout() {
        // Given
        properties.getTransportConfig().setConnectionTimeout(60000);
        properties.getTransportConfig().setReadTimeout(30000);

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("transportConfig.readTimeout")).isNotNull();
        assertThat(errors.getFieldError("transportConfig.readTimeout").getDefaultMessage())
            .contains("must be greater than connection timeout");
    }

    @Test
    @DisplayName("Should reject invalid keystore type")
    void shouldRejectInvalidKeystoreType() {
        // Given
        properties.getSecurity().getKeystore().setType("INVALID");

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("security.keystore.type")).isNotNull();
        assertThat(errors.getFieldError("security.keystore.type").getDefaultMessage())
            .contains("must be one of: PKCS12, JKS, JCEKS");
    }

    @Test
    @DisplayName("Should accept valid keystore types")
    void shouldAcceptValidKeystoreTypes() {
        // Given
        String[] validTypes = {"PKCS12", "JKS", "JCEKS"};

        for (String type : validTypes) {
            properties = new SpringFinqubeProperties();
            errors = new BeanPropertyBindingResult(properties, "properties");
            properties.getSecurity().getKeystore().setType(type);

            // When
            validator.validate(properties, errors);

            // Then
            assertThat(errors.hasErrors()).isFalse();
        }
    }

    @Test
    @DisplayName("Should reject missing keystore password when location is provided")
    void shouldRejectMissingKeystorePasswordWhenLocationIsProvided() {
        // Given
        properties.getSecurity().getKeystore().setLocation("classpath:keystore.p12");
        properties.getSecurity().getKeystore().setPassword(null);

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("security.keystore.password")).isNotNull();
        assertThat(errors.getFieldError("security.keystore.password").getDefaultMessage())
            .contains("Keystore password is required");
    }

    @Test
    @DisplayName("Should reject invalid collection interval")
    void shouldRejectInvalidCollectionInterval() {
        // Given
        properties.getMonitoring().setCollectionInterval(0);

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("monitoring.collectionInterval")).isNotNull();
        assertThat(errors.getFieldError("monitoring.collectionInterval").getDefaultMessage())
            .contains("must be positive");
    }

    @Test
    @DisplayName("Should reject collection interval exceeding maximum")
    void shouldRejectCollectionIntervalExceedingMaximum() {
        // Given
        properties.getMonitoring().setCollectionInterval(4000); // > 3600

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("monitoring.collectionInterval")).isNotNull();
        assertThat(errors.getFieldError("monitoring.collectionInterval").getDefaultMessage())
            .contains("must not exceed 3600 seconds");
    }

    @Test
    @DisplayName("Should reject invalid log level")
    void shouldRejectInvalidLogLevel() {
        // Given
        properties.getLogging().setLevel("INVALID");

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("logging.level")).isNotNull();
        assertThat(errors.getFieldError("logging.level").getDefaultMessage())
            .contains("must be one of: TRACE, DEBUG, INFO, WARN, ERROR");
    }

    @Test
    @DisplayName("Should accept valid log levels")
    void shouldAcceptValidLogLevels() {
        // Given
        String[] validLevels = {"TRACE", "DEBUG", "INFO", "WARN", "ERROR"};

        for (String level : validLevels) {
            properties = new SpringFinqubeProperties();
            errors = new BeanPropertyBindingResult(properties, "properties");
            properties.getLogging().setLevel(level);

            // When
            validator.validate(properties, errors);

            // Then
            assertThat(errors.hasErrors()).isFalse();
        }
    }

    @Test
    @DisplayName("Should warn about sensitive data logging")
    void shouldWarnAboutSensitiveDataLogging() {
        // Given
        properties.getLogging().setLogSensitiveData(true);

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("logging.logSensitiveData")).isNotNull();
        assertThat(errors.getFieldError("logging.logSensitiveData").getDefaultMessage())
            .contains("not recommended for production");
    }

    @Test
    @DisplayName("Should reject missing keystore for secure transport")
    void shouldRejectMissingKeystoreForSecureTransport() {
        // Given
        properties.setTransport("swiftnet");
        properties.getSecurity().getKeystore().setLocation(null);

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("security.keystore.location")).isNotNull();
        assertThat(errors.getFieldError("security.keystore.location").getDefaultMessage())
            .contains("Keystore location is required for transport: swiftnet");
    }

    @Test
    @DisplayName("Should reject health checks when monitoring is disabled")
    void shouldRejectHealthChecksWhenMonitoringIsDisabled() {
        // Given
        properties.getMonitoring().setEnabled(false);
        properties.getMonitoring().setHealthChecks(true);

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("monitoring.healthChecks")).isNotNull();
        assertThat(errors.getFieldError("monitoring.healthChecks").getDefaultMessage())
            .contains("cannot be enabled when monitoring is disabled");
    }

    @Test
    @DisplayName("Should handle multiple validation errors")
    void shouldHandleMultipleValidationErrors() {
        // Given
        properties.getTransportConfig().setConnectionTimeout(0);
        properties.getTransportConfig().setReadTimeout(-1);
        properties.getLogging().setLevel("INVALID");

        // When
        validator.validate(properties, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getErrorCount()).isEqualTo(3);
        assertThat(errors.getFieldError("transportConfig.connectionTimeout")).isNotNull();
        assertThat(errors.getFieldError("transportConfig.readTimeout")).isNotNull();
        assertThat(errors.getFieldError("logging.level")).isNotNull();
    }
}
