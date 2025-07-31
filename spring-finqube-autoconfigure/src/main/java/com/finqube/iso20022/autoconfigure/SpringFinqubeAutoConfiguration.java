package com.finqube.iso20022.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.finqube.iso20022.autoconfigure.properties.SpringFinqubeProperties;

/**
 * Spring Boot autoconfiguration for Spring Finqube ISO 20022 functionality.
 *
 * <p>This configuration class automatically sets up all necessary beans and components
 * for ISO 20022 message processing when the starter is included in a Spring Boot application.</p>
 *
 * <p>The autoconfiguration is conditional on the {@code iso20022.enabled} property,
 * which defaults to {@code true}. Set to {@code false} to disable ISO 20022 functionality.</p>
 *
 * <p>Automatically configured components:</p>
 * <ul>
 *   <li>ISO 20022 message templates and facades</li>
 *   <li>Message validation and processing pipelines</li>
 *   <li>Transport layer abstractions</li>
 *   <li>Security and monitoring components</li>
 *   <li>Configuration properties binding</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Configuration
@ConditionalOnProperty(prefix = "iso20022", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SpringFinqubeProperties.class)
public class SpringFinqubeAutoConfiguration {

    /**
     * Creates a "hello world" bean to demonstrate that the autoconfiguration is working.
     *
     * <p>This bean serves as a simple indicator that the Spring Finqube autoconfiguration
     * has been successfully loaded and is ready to process ISO 20022 messages.</p>
     *
     * @return a simple string bean indicating successful autoconfiguration
     */
    @Bean
    public String springFinqubeHelloWorld() {
        return "Spring Finqube ISO 20022 Starter is ready! 🚀";
    }



    /**
     * Creates the Iso20022Template bean for message processing.
     *
     * <p>This bean provides the main facade for sending and processing ISO 20022
     * financial messages. It can be autowired into any Spring component.</p>
     *
     * @return the configured Iso20022Template bean
     */
    @Bean
    public com.finqube.iso20022.core.template.Iso20022Template iso20022Template() {
        return new com.finqube.iso20022.core.template.Iso20022Template();
    }
}
