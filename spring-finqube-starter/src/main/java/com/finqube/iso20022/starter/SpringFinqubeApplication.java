package com.finqube.iso20022.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for Spring Finqube ISO 20022 Starter.
 *
 * <p>This class serves as the entry point for Spring Boot applications that want to
 * use ISO 20022 financial messaging capabilities. It automatically configures
 * all necessary beans and components for ISO 20022 message processing.</p>
 *
 * <p>Key features automatically configured:</p>
 * <ul>
 *   <li>ISO 20022 message templates and facades</li>
 *   <li>Message validation and processing pipelines</li>
 *   <li>Transport layer abstractions</li>
 *   <li>Security and monitoring components</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * @SpringBootApplication
 * public class MyApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApplication.class, args);
 *     }
 * }
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@SpringBootApplication
public class SpringFinqubeApplication {

    /**
     * Main method to start the Spring Finqube application.
     *
     * <p>This method initializes the Spring Boot application context and starts
     * all configured components for ISO 20022 message processing.</p>
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringFinqubeApplication.class, args);
    }
}
