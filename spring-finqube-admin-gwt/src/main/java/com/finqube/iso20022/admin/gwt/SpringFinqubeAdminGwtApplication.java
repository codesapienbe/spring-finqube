package com.finqube.iso20022.admin.gwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application entry point for the GWT-based Admin Dashboard module.
 *
 * <p>This module provides a minimal shell to serve a GWT application host page
 * over Spring Boot's embedded server. The intent is to incrementally port
 * Vaadin views to GWT without impacting the existing Vaadin-based admin module.</p>
 *
 * <p>Static assets (including the GWT host page and compiled JS) are served from
 * {@code src/main/resources/static}.</p>
 */
@SpringBootApplication
public class SpringFinqubeAdminGwtApplication {

    /**
     * Starts the Spring Boot application for the GWT admin module.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringFinqubeAdminGwtApplication.class, args);
    }
}
