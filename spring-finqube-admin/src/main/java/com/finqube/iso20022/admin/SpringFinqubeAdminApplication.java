package com.finqube.iso20022.admin;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Spring Boot application class for the Spring Finqube Admin Dashboard.
 *
 * <p>This application provides a Vaadin Flow-based web interface for real-time monitoring
 * and administration of ISO 20022 financial messages. It integrates with the core
 * Spring Finqube modules to provide comprehensive monitoring capabilities.</p>
 *
 * <p>The dashboard includes:
 * <ul>
 *   <li>Real-time message monitoring and statistics</li>
 *   <li>Message flow visualization and tracking</li>
 *   <li>Security and audit log management</li>
 *   <li>System health and performance metrics</li>
 *   <li>Configuration management interface</li>
 * </ul></p>
 *
 * @author Spring Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.finqube.iso20022.admin",
    "com.finqube.iso20022.core"
})
@org.springframework.scheduling.annotation.EnableScheduling
@Theme(variant = Lumo.DARK)
@PWA(
    name = "Spring Finqube Admin Dashboard",
    shortName = "Finqube Admin",
    description = "Real-time monitoring dashboard for ISO 20022 financial messages",
    themeColor = "#1976d2",
    backgroundColor = "#ffffff"
)
public class SpringFinqubeAdminApplication implements AppShellConfigurator {

    /**
     * Main application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringFinqubeAdminApplication.class, args);
    }
}
