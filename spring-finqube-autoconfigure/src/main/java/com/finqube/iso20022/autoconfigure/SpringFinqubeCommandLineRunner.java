package com.finqube.iso20022.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.finqube.iso20022.autoconfigure.properties.SpringFinqubeProperties;

/**
 * Command-line runner that demonstrates Spring Finqube ISO 20022 starter functionality.
 *
 * <p>This runner is automatically executed when the application starts and provides
 * feedback about the ISO 20022 configuration and readiness status.</p>
 *
 * <p>The runner is only active when ISO 20022 functionality is enabled
 * ({@code iso20022.enabled=true}).</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Component
@ConditionalOnProperty(prefix = "iso20022", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SpringFinqubeCommandLineRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SpringFinqubeCommandLineRunner.class);

    private final SpringFinqubeProperties properties;
    private final String helloWorldBean;

    /**
     * Constructs a new SpringFinqubeCommandLineRunner.
     *
     * @param properties the ISO 20022 configuration properties
     * @param helloWorldBean the hello world bean from autoconfiguration
     */
    public SpringFinqubeCommandLineRunner(SpringFinqubeProperties properties, String helloWorldBean) {
        this.properties = properties;
        this.helloWorldBean = helloWorldBean;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== Spring Finqube ISO 20022 Starter Initialization ===");
        logger.info(helloWorldBean);

        // Log configuration details
        logger.info("Configuration:");
        logger.info("  - Enabled: {}", properties.isEnabled());
        logger.info("  - Transport: {}", properties.getTransport());
        logger.info("  - Validation enabled: {}", properties.getValidation().isEnabled());
        logger.info("  - Validation fail-fast: {}", properties.getValidation().isFailFast());

        if (properties.getSecurity().getKeystore().getLocation() != null) {
            logger.info("  - Keystore location: {}", properties.getSecurity().getKeystore().getLocation());
            logger.info("  - Keystore type: {}", properties.getSecurity().getKeystore().getType());
        } else {
            logger.info("  - Keystore: Not configured");
        }

        logger.info("=== Spring Finqube ISO 20022 Starter Ready ===");
        logger.info("Ready to process ISO 20022 financial messages!");
    }
}
