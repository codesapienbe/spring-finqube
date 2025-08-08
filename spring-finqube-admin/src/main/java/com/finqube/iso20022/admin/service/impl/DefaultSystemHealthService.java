package com.finqube.iso20022.admin.service.impl;

import com.finqube.iso20022.admin.service.SystemHealthService;
import com.finqube.iso20022.core.monitoring.MonitoringManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default implementation of the SystemHealthService.
 *
 * <p>This service provides real-time system health monitoring by collecting
 * metrics from the JVM and integrating with the core Spring Finqube
 * monitoring components.</p>
 *
 * @author Spring Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Service
public class DefaultSystemHealthService implements SystemHealthService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSystemHealthService.class);

    private final MonitoringManager monitoringManager;
        private final CopyOnWriteArrayList<SystemHealthService.SystemHealthUpdateListener> listeners = new CopyOnWriteArrayList<>();

    private final long startTime = System.currentTimeMillis();
    private volatile SystemHealthService.SystemHealthStatus currentHealthStatus = SystemHealthService.SystemHealthStatus.HEALTHY;
    private volatile SystemHealthService.SystemPerformanceMetrics currentMetrics;

    /**
     * Constructs the service with required dependencies.
     *
     * @param monitoringManager the core monitoring manager
     */
    @Autowired
    public DefaultSystemHealthService(MonitoringManager monitoringManager) {
        this.monitoringManager = monitoringManager;
        updateMetrics();
        logger.info("System health service initialized");
    }

    @Override
    public SystemHealthService.SystemHealthStatus getSystemHealthStatus() {
        return currentHealthStatus;
    }

    @Override
    public Duration getSystemUptime() {
        return Duration.ofMillis(System.currentTimeMillis() - startTime);
    }

    @Override
    public double getCpuUtilization() {
        // TODO: Implement real CPU monitoring
        return 23.5; // Placeholder value
    }

    @Override
    public double getMemoryUtilization() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long used = memoryBean.getHeapMemoryUsage().getUsed();
        long max = memoryBean.getHeapMemoryUsage().getMax();
        return max > 0 ? (double) used / max * 100.0 : 0.0;
    }

    @Override
    public double getDiskUtilization() {
        // TODO: Implement real disk monitoring
        return 45.2; // Placeholder value
    }

    @Override
    public int getActiveThreadCount() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        return threadBean.getThreadCount();
    }

    @Override
    public Map<String, ComponentHealthStatus> getComponentHealthStatus() {
        Map<String, ComponentHealthStatus> status = new HashMap<>();

        // TODO: Integrate with actual component health checks
        status.put("Message Processing", ComponentHealthStatus.UP);
        status.put("Security Manager", ComponentHealthStatus.UP);
        status.put("Transport Layer", ComponentHealthStatus.UP);
        status.put("Database Connection", ComponentHealthStatus.UP);

        return status;
    }

    @Override
    public SystemPerformanceMetrics getSystemPerformanceMetrics() {
        return currentMetrics;
    }

    @Override
    public void subscribeToHealthUpdates(SystemHealthUpdateListener listener) {
        listeners.add(listener);
        logger.debug("Added system health update listener: {}", listener.getClass().getSimpleName());
    }

    @Override
    public void unsubscribeFromHealthUpdates(SystemHealthUpdateListener listener) {
        listeners.remove(listener);
        logger.debug("Removed system health update listener: {}", listener.getClass().getSimpleName());
    }

    /**
     * Scheduled task to update system metrics every 30 seconds.
     */
    @Scheduled(fixedRate = 30000)
    public void updateMetrics() {
        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

            long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
            long heapMax = memoryBean.getHeapMemoryUsage().getMax();

            currentMetrics = new SystemPerformanceMetrics(
                getCpuUtilization(),
                getMemoryUtilization(),
                getDiskUtilization(),
                threadBean.getThreadCount(),
                heapUsed,
                heapMax,
                System.currentTimeMillis()
            );

            // Update health status based on metrics
            SystemHealthStatus newStatus = calculateHealthStatus();
            if (newStatus != currentHealthStatus) {
                SystemHealthStatus oldStatus = currentHealthStatus;
                currentHealthStatus = newStatus;

                // Notify listeners of health status change
                for (SystemHealthUpdateListener listener : listeners) {
                    try {
                        listener.onHealthStatusChanged(newStatus);
                    } catch (Exception e) {
                        logger.warn("Error notifying health status listener: {}", e.getMessage());
                    }
                }

                logger.info("System health status changed from {} to {}", oldStatus, newStatus);
            }

        } catch (Exception e) {
            logger.error("Error updating system metrics: {}", e.getMessage(), e);
        }
    }

    /**
     * Calculates the overall system health status based on current metrics.
     *
     * @return the calculated health status
     */
    private SystemHealthStatus calculateHealthStatus() {
        if (currentMetrics == null) {
            return SystemHealthStatus.UNKNOWN;
        }

        // Simple health calculation based on memory and CPU usage
        if (currentMetrics.getMemoryUtilization() > 90.0 || currentMetrics.getCpuUtilization() > 90.0) {
            return SystemHealthStatus.CRITICAL;
        } else if (currentMetrics.getMemoryUtilization() > 75.0 || currentMetrics.getCpuUtilization() > 75.0) {
            return SystemHealthStatus.WARNING;
        } else {
            return SystemHealthStatus.HEALTHY;
        }
    }
}
