package com.finqube.iso20022.admin.service;

import java.time.Duration;
import java.util.Map;

/**
 * Service interface for monitoring system health and performance metrics.
 *
 * <p>This service provides comprehensive system monitoring capabilities including:
 * <ul>
 *   <li>System uptime and availability monitoring</li>
 *   <li>Resource utilization tracking (CPU, memory, disk)</li>
 *   <li>Component health status monitoring</li>
 *   <li>Performance metrics collection</li>
 * </ul></p>
 *
 * @author Spring Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface SystemHealthService {

    /**
     * Gets the overall system health status.
     *
     * @return system health status
     */
    SystemHealthStatus getSystemHealthStatus();

    /**
     * Gets the system uptime.
     *
     * @return system uptime duration
     */
    Duration getSystemUptime();

    /**
     * Gets CPU utilization percentage.
     *
     * @return CPU utilization as percentage
     */
    double getCpuUtilization();

    /**
     * Gets memory utilization percentage.
     *
     * @return memory utilization as percentage
     */
    double getMemoryUtilization();

    /**
     * Gets disk utilization percentage.
     *
     * @return disk utilization as percentage
     */
    double getDiskUtilization();

    /**
     * Gets the number of active threads.
     *
     * @return active thread count
     */
    int getActiveThreadCount();

    /**
     * Gets component health status for all monitored components.
     *
     * @return map of component name to health status
     */
    Map<String, ComponentHealthStatus> getComponentHealthStatus();

    /**
     * Gets performance metrics for the system.
     *
     * @return system performance metrics
     */
    SystemPerformanceMetrics getSystemPerformanceMetrics();

    /**
     * Subscribes to system health updates.
     *
     * @param listener the listener to receive updates
     */
    void subscribeToHealthUpdates(SystemHealthUpdateListener listener);

    /**
     * Unsubscribes from system health updates.
     *
     * @param listener the listener to remove
     */
    void unsubscribeFromHealthUpdates(SystemHealthUpdateListener listener);

    /**
     * Enumeration of system health statuses.
     */
    enum SystemHealthStatus {
        HEALTHY("Healthy"),
        WARNING("Warning"),
        CRITICAL("Critical"),
        UNKNOWN("Unknown");

        private final String displayName;

        SystemHealthStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Enumeration of component health statuses.
     */
    enum ComponentHealthStatus {
        UP("Up"),
        DOWN("Down"),
        DEGRADED("Degraded"),
        UNKNOWN("Unknown");

        private final String displayName;

        ComponentHealthStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Listener interface for system health updates.
     */
    interface SystemHealthUpdateListener {

        /**
         * Called when system health status changes.
         *
         * @param status the new health status
         */
        void onHealthStatusChanged(SystemHealthStatus status);

        /**
         * Called when component health status changes.
         *
         * @param componentName the component name
         * @param status the new component health status
         */
        void onComponentHealthChanged(String componentName, ComponentHealthStatus status);
    }

    /**
     * Data class for system performance metrics.
     */
    class SystemPerformanceMetrics {
        private final double cpuUtilization;
        private final double memoryUtilization;
        private final double diskUtilization;
        private final int activeThreads;
        private final long heapMemoryUsed;
        private final long heapMemoryMax;
        private final long timestamp;

        public SystemPerformanceMetrics(double cpuUtilization,
                                      double memoryUtilization,
                                      double diskUtilization,
                                      int activeThreads,
                                      long heapMemoryUsed,
                                      long heapMemoryMax,
                                      long timestamp) {
            this.cpuUtilization = cpuUtilization;
            this.memoryUtilization = memoryUtilization;
            this.diskUtilization = diskUtilization;
            this.activeThreads = activeThreads;
            this.heapMemoryUsed = heapMemoryUsed;
            this.heapMemoryMax = heapMemoryMax;
            this.timestamp = timestamp;
        }

        public double getCpuUtilization() {
            return cpuUtilization;
        }

        public double getMemoryUtilization() {
            return memoryUtilization;
        }

        public double getDiskUtilization() {
            return diskUtilization;
        }

        public int getActiveThreads() {
            return activeThreads;
        }

        public long getHeapMemoryUsed() {
            return heapMemoryUsed;
        }

        public long getHeapMemoryMax() {
            return heapMemoryMax;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
