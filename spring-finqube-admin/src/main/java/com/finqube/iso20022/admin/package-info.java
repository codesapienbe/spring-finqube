/**
 * Spring Finqube Admin Dashboard Module.
 *
 * <p>This package contains the admin dashboard application for the Spring Finqube
 * ISO 20022 financial messaging system. The admin dashboard provides a comprehensive
 * web interface for monitoring and administering the system.</p>
 *
 * <h2>Key Components</h2>
 *
 * <h3>Application</h3>
 * <ul>
 *   <li>{@link com.finqube.iso20022.admin.SpringFinqubeAdminApplication} - Main Spring Boot application</li>
 * </ul>
 *
 * <h3>Services</h3>
 * <ul>
 *   <li>{@link com.finqube.iso20022.admin.service.MessageMonitoringService} - Message monitoring and statistics</li>
 *   <li>{@link com.finqube.iso20022.admin.service.SystemHealthService} - System health monitoring</li>
 *   <li>{@link com.finqube.iso20022.admin.service.ProcessingPerformanceMetrics} - Performance metrics data</li>
 * </ul>
 *
 * <h3>Controllers</h3>
 * <ul>
 *   <li>{@link com.finqube.iso20022.admin.controller.AdminDashboardController} - REST API endpoints</li>
 * </ul>
 *
 * <h3>Dashboard Views</h3>
 * <ul>
 *   <li>{@link com.finqube.iso20022.admin.dashboard.MainDashboardView} - Main Vaadin Flow dashboard</li>
 *   <li>{@link com.finqube.iso20022.admin.dashboard.SimpleDashboardView} - Simple dashboard placeholder</li>
 * </ul>
 *
 * <h2>Features</h2>
 *
 * <ul>
 *   <li><strong>Real-time Monitoring</strong>: Live updates of message statistics and system health</li>
 *   <li><strong>Message Tracking</strong>: Comprehensive message flow monitoring and statistics</li>
 *   <li><strong>System Health</strong>: CPU, memory, and disk utilization monitoring</li>
 *   <li><strong>Performance Metrics</strong>: Processing times, throughput, and error rates</li>
 *   <li><strong>Security Management</strong>: Certificate management and audit logging</li>
 *   <li><strong>REST API</strong>: Backend endpoints for external integrations</li>
 * </ul>
 *
 * <h2>Architecture</h2>
 *
 * <p>The admin dashboard follows a layered architecture:</p>
 *
 * <ul>
 *   <li><strong>Presentation Layer</strong>: Vaadin Flow views and components</li>
 *   <li><strong>Controller Layer</strong>: REST controllers for API endpoints</li>
 *   <li><strong>Service Layer</strong>: Business logic and monitoring services</li>
 *   <li><strong>Integration Layer</strong>: Integration with core Spring Finqube modules</li>
 * </ul>
 *
 * <h2>Configuration</h2>
 *
 * <p>The admin dashboard can be configured via application properties:</p>
 *
 * <ul>
 *   <li><code>finqube.admin.refresh-interval</code> - Dashboard refresh interval</li>
 *   <li><code>finqube.admin.real-time-updates</code> - Enable real-time updates</li>
 *   <li><code>finqube.admin.security.basic-auth-enabled</code> - Enable basic authentication</li>
 *   <li><code>finqube.admin.monitoring.system-health-enabled</code> - Enable system health monitoring</li>
 * </ul>
 *
 * <h2>Security</h2>
 *
 * <p>The admin dashboard implements comprehensive security measures:</p>
 *
 * <ul>
 *   <li>Authentication and authorization</li>
 *   <li>Session management with timeouts</li>
 *   <li>Audit logging for all administrative actions</li>
 *   <li>Role-based access control</li>
 * </ul>
 *
 * <h2>Monitoring and Observability</h2>
 *
 * <p>The admin dashboard provides extensive monitoring capabilities:</p>
 *
 * <ul>
 *   <li>Health checks for system components</li>
 *   <li>Performance metrics collection</li>
 *   <li>Structured logging with correlation IDs</li>
 *   <li>Integration with Spring Boot Actuator</li>
 *   <li>Prometheus metrics export</li>
 * </ul>
 *
 * @author Spring Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
package com.finqube.iso20022.admin;
