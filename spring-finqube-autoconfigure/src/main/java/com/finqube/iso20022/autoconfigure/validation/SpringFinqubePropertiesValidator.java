package com.finqube.iso20022.autoconfigure.validation;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.finqube.iso20022.autoconfigure.properties.SpringFinqubeProperties;

/**
 * Validator for SpringFinqubeProperties configuration.
 *
 * <p>This validator ensures that all configuration properties are valid and consistent.
 * It performs cross-property validation and business rule enforcement.</p>
 *
 * <p>Validation rules:</p>
 * <ul>
 *   <li>Timeouts must be positive values</li>
 *   <li>Retry counts must be non-negative</li>
 *   <li>Connection pool sizes must be positive</li>
 *   <li>Log levels must be valid</li>
 *   <li>Security settings must be consistent</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Component
@ConfigurationPropertiesBinding
public class SpringFinqubePropertiesValidator implements Validator {

    private static final List<String> VALID_LOG_LEVELS = List.of("TRACE", "DEBUG", "INFO", "WARN", "ERROR");

    @Override
    public boolean supports(Class<?> clazz) {
        return SpringFinqubeProperties.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SpringFinqubeProperties properties = (SpringFinqubeProperties) target;

        validateTransport(properties, errors);
        validateSecurity(properties, errors);
        validateMonitoring(properties, errors);
        validateLogging(properties, errors);
        validateCrossProperties(properties, errors);
    }

    private void validateTransport(SpringFinqubeProperties properties, Errors errors) {
        SpringFinqubeProperties.Transport transport = properties.getTransportConfig();

        if (transport.getConnectionTimeout() <= 0) {
            errors.rejectValue("transportConfig.connectionTimeout", "invalid.connectionTimeout",
                "Connection timeout must be positive");
        }

        if (transport.getReadTimeout() <= 0) {
            errors.rejectValue("transportConfig.readTimeout", "invalid.readTimeout",
                "Read timeout must be positive");
        }

        if (transport.getMaxRetries() < 0) {
            errors.rejectValue("transportConfig.maxRetries", "invalid.maxRetries",
                "Maximum retries must be non-negative");
        }

        if (transport.getRetryDelay() < 0) {
            errors.rejectValue("transportConfig.retryDelay", "invalid.retryDelay",
                "Retry delay must be non-negative");
        }

        if (transport.getMaxConnections() <= 0) {
            errors.rejectValue("transportConfig.maxConnections", "invalid.maxConnections",
                "Maximum connections must be positive");
        }

        // Validate that read timeout is greater than connection timeout
        if (transport.getReadTimeout() <= transport.getConnectionTimeout()) {
            errors.rejectValue("transportConfig.readTimeout", "invalid.readTimeout",
                "Read timeout must be greater than connection timeout");
        }
    }

    private void validateSecurity(SpringFinqubeProperties properties, Errors errors) {
        SpringFinqubeProperties.Security security = properties.getSecurity();
        SpringFinqubeProperties.Security.Keystore keystore = security.getKeystore();

        // If keystore location is provided, password must also be provided
        if (keystore.getLocation() != null && !keystore.getLocation().trim().isEmpty()) {
            if (keystore.getPassword() == null || keystore.getPassword().trim().isEmpty()) {
                errors.rejectValue("security.keystore.password", "missing.keystorePassword",
                    "Keystore password is required when keystore location is specified");
            }
        }

        // Validate keystore type
        if (keystore.getType() != null && !keystore.getType().trim().isEmpty()) {
            String type = keystore.getType().toUpperCase();
            if (!List.of("PKCS12", "JKS", "JCEKS").contains(type)) {
                errors.rejectValue("security.keystore.type", "invalid.keystoreType",
                    "Keystore type must be one of: PKCS12, JKS, JCEKS");
            }
        }
    }

    private void validateMonitoring(SpringFinqubeProperties properties, Errors errors) {
        SpringFinqubeProperties.Monitoring monitoring = properties.getMonitoring();

        if (monitoring.getCollectionInterval() <= 0) {
            errors.rejectValue("monitoring.collectionInterval", "invalid.collectionInterval",
                "Metrics collection interval must be positive");
        }

        if (monitoring.getCollectionInterval() > 3600) {
            errors.rejectValue("monitoring.collectionInterval", "invalid.collectionInterval",
                "Metrics collection interval must not exceed 3600 seconds (1 hour)");
        }
    }

    private void validateLogging(SpringFinqubeProperties properties, Errors errors) {
        SpringFinqubeProperties.Logging logging = properties.getLogging();

        if (logging.getLevel() != null && !logging.getLevel().trim().isEmpty()) {
            String level = logging.getLevel().toUpperCase();
            if (!VALID_LOG_LEVELS.contains(level)) {
                errors.rejectValue("logging.level", "invalid.logLevel",
                    "Log level must be one of: " + String.join(", ", VALID_LOG_LEVELS));
            }
        }

        // Security warning for sensitive data logging
        if (logging.isLogSensitiveData()) {
            errors.rejectValue("logging.logSensitiveData", "security.sensitiveDataLogging",
                "Logging sensitive data is not recommended for production environments");
        }
    }

    private void validateCrossProperties(SpringFinqubeProperties properties, Errors errors) {
        // Validate transport protocol consistency
        String transport = properties.getTransport();
        if ("swiftnet".equals(transport) || "amh".equals(transport)) {
            SpringFinqubeProperties.Security security = properties.getSecurity();
            if (security.getKeystore().getLocation() == null || security.getKeystore().getLocation().trim().isEmpty()) {
                errors.rejectValue("security.keystore.location", "missing.keystoreForTransport",
                    "Keystore location is required for transport: " + transport);
            }
        }

        // Validate that monitoring is enabled if health checks are enabled
        SpringFinqubeProperties.Monitoring monitoring = properties.getMonitoring();
        if (monitoring.isHealthChecks() && !monitoring.isEnabled()) {
            errors.rejectValue("monitoring.healthChecks", "inconsistent.healthChecks",
                "Health checks cannot be enabled when monitoring is disabled");
        }
    }
}
