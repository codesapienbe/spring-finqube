package com.finqube.iso20022.core.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import jakarta.annotation.PostConstruct;

/**
 * Auto-configuration for configuration encryption functionality.
 *
 * <p>This configuration class sets up the configuration encryption service
 * and automatically wraps property sources to decrypt encrypted values.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Configuration
@ConditionalOnProperty(name = "iso20022.security.config.encryption.enabled", havingValue = "true", matchIfMissing = true)
public class ConfigurationEncryptionAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationEncryptionAutoConfiguration.class);

    private final ConfigurableEnvironment environment;
    private final ConfigurationEncryptionService encryptionService;

    public ConfigurationEncryptionAutoConfiguration(ConfigurableEnvironment environment,
                                                   ConfigurationEncryptionService encryptionService) {
        this.environment = environment;
        this.encryptionService = encryptionService;
    }

    @PostConstruct
    public void setupEncryptedPropertySources() {
        log.info("Setting up encrypted property sources");

                // Wrap existing property sources with encrypted property sources
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource &&
                !(propertySource instanceof EncryptedPropertySource)) {

                String name = propertySource.getName();
                EncryptedPropertySource encryptedPropertySource =
                    new EncryptedPropertySource("encrypted-" + name, propertySource, encryptionService);

                // Replace the original property source with the encrypted wrapper
                environment.getPropertySources().replace(name, encryptedPropertySource);
                log.debug("Wrapped property source '{}' with encrypted property source", name);
            }
        }

        log.info("Encrypted property sources setup completed");
    }

    @Bean
    public ConfigurationEncryptionProperties configurationEncryptionProperties() {
        return new ConfigurationEncryptionProperties();
    }
}
