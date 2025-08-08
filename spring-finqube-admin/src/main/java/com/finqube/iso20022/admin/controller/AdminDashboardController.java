package com.finqube.iso20022.admin.controller;

import com.finqube.iso20022.admin.service.MessageMonitoringService;
import com.finqube.iso20022.admin.service.SystemHealthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for the Spring Finqube Admin Dashboard.
 *
 * <p>This controller provides REST API endpoints for accessing monitoring
 * data and system health information. It serves as a backend for the
 * Vaadin Flow frontend dashboard.</p>
 *
 * @author Spring Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);

    private final MessageMonitoringService messageMonitoringService;
    private final SystemHealthService systemHealthService;

    /**
     * Constructs the controller with required services.
     *
     * @param messageMonitoringService service for message monitoring
     * @param systemHealthService service for system health monitoring
     */
    @Autowired
    public AdminDashboardController(MessageMonitoringService messageMonitoringService,
                                  SystemHealthService systemHealthService) {
        this.messageMonitoringService = messageMonitoringService;
        this.systemHealthService = systemHealthService;
    }

    /**
     * Gets the dashboard overview with key metrics.
     *
     * @return dashboard overview data
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getDashboardOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();

            // Message statistics
            overview.put("totalMessages", messageMonitoringService.getTotalMessageCount());
            overview.put("todayMessages", messageMonitoringService.getTodayMessageCount());
            overview.put("pendingMessages", messageMonitoringService.getPendingMessageCount());
            overview.put("errorMessages", messageMonitoringService.getErrorMessageCount());

            // System health
            overview.put("systemHealth", systemHealthService.getSystemHealthStatus().getDisplayName());
            overview.put("uptime", systemHealthService.getSystemUptime().toString());
            overview.put("cpuUtilization", systemHealthService.getCpuUtilization());
            overview.put("memoryUtilization", systemHealthService.getMemoryUtilization());

            // Timestamp
            overview.put("timestamp", LocalDateTime.now());

            logger.debug("Dashboard overview requested");
            return ResponseEntity.ok(overview);

        } catch (Exception e) {
            logger.error("Error retrieving dashboard overview: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Gets detailed message statistics.
     *
     * @return message statistics data
     */
    @GetMapping("/messages/stats")
    public ResponseEntity<Map<String, Object>> getMessageStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();

            stats.put("byPriority", messageMonitoringService.getMessageCountByPriority());
            stats.put("byType", messageMonitoringService.getMessageCountByType());
            stats.put("performance", messageMonitoringService.getProcessingPerformanceMetrics());

            logger.debug("Message statistics requested");
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            logger.error("Error retrieving message statistics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Gets system health information.
     *
     * @return system health data
     */
    @GetMapping("/system/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        try {
            Map<String, Object> health = new HashMap<>();

            health.put("status", systemHealthService.getSystemHealthStatus().getDisplayName());
            health.put("uptime", systemHealthService.getSystemUptime().toString());
            health.put("cpuUtilization", systemHealthService.getCpuUtilization());
            health.put("memoryUtilization", systemHealthService.getMemoryUtilization());
            health.put("diskUtilization", systemHealthService.getDiskUtilization());
            health.put("activeThreads", systemHealthService.getActiveThreadCount());
            health.put("componentHealth", systemHealthService.getComponentHealthStatus());
            health.put("performanceMetrics", systemHealthService.getSystemPerformanceMetrics());

            logger.debug("System health requested");
            return ResponseEntity.ok(health);

        } catch (Exception e) {
            logger.error("Error retrieving system health: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Gets application information.
     *
     * @return application info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getApplicationInfo() {
        try {
            Map<String, Object> info = new HashMap<>();

            info.put("name", "Spring Finqube Admin Dashboard");
            info.put("version", "0.1.0-SNAPSHOT");
            info.put("description", "Real-time monitoring dashboard for ISO 20022 financial messages");
            info.put("startupTime", LocalDateTime.now());

            logger.debug("Application info requested");
            return ResponseEntity.ok(info);

        } catch (Exception e) {
            logger.error("Error retrieving application info: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
