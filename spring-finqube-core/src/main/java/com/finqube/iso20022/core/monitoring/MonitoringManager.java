package com.finqube.iso20022.core.monitoring;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Monitoring manager for ISO 20022 system observability.
 *
 * <p>This interface defines the contract for comprehensive monitoring and metrics collection,
 * including performance monitoring, health checks, alerting, and operational dashboards.</p>
 *
 * <p>The monitoring manager supports:</p>
 * <ul>
 *   <li>Performance metrics collection and aggregation</li>
 *   <li>System health monitoring and alerting</li>
 *   <li>Operational dashboards and reporting</li>
 *   <li>Integration with external monitoring systems</li>
 *   <li>Real-time metrics and historical data</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * @Autowired
 * private MonitoringManager monitoringManager;
 *
 * // Record a metric
 * monitoringManager.recordMetric("message.processing.time", 150.5, Map.of("type", "pain.001"));
 *
 * // Get system health
 * SystemHealth health = monitoringManager.getSystemHealth();
 *
 * // Get performance metrics
 * PerformanceMetrics metrics = monitoringManager.getPerformanceMetrics();
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface MonitoringManager {

    /**
     * Records a metric value.
     *
     * @param metricName the metric name
     * @param value the metric value
     * @param tags additional tags for the metric
     */
    void recordMetric(String metricName, double value, Map<String, String> tags);

    /**
     * Records a metric value asynchronously.
     *
     * @param metricName the metric name
     * @param value the metric value
     * @param tags additional tags for the metric
     * @return a CompletableFuture that completes when the metric is recorded
     */
    CompletableFuture<Void> recordMetricAsync(String metricName, double value, Map<String, String> tags);

    /**
     * Increments a counter metric.
     *
     * @param metricName the metric name
     * @param tags additional tags for the metric
     */
    void incrementCounter(String metricName, Map<String, String> tags);

    /**
     * Increments a counter metric asynchronously.
     *
     * @param metricName the metric name
     * @param tags additional tags for the metric
     * @return a CompletableFuture that completes when the counter is incremented
     */
    CompletableFuture<Void> incrementCounterAsync(String metricName, Map<String, String> tags);

    /**
     * Records a timing measurement.
     *
     * @param metricName the metric name
     * @param durationMillis the duration in milliseconds
     * @param tags additional tags for the metric
     */
    void recordTiming(String metricName, long durationMillis, Map<String, String> tags);

    /**
     * Records a timing measurement asynchronously.
     *
     * @param metricName the metric name
     * @param durationMillis the duration in milliseconds
     * @param tags additional tags for the metric
     * @return a CompletableFuture that completes when the timing is recorded
     */
    CompletableFuture<Void> recordTimingAsync(String metricName, long durationMillis, Map<String, String> tags);

    /**
     * Records an event.
     *
     * @param eventName the event name
     * @param eventData the event data
     * @param tags additional tags for the event
     */
    void recordEvent(String eventName, Map<String, Object> eventData, Map<String, String> tags);

    /**
     * Records an event asynchronously.
     *
     * @param eventName the event name
     * @param eventData the event data
     * @param tags additional tags for the event
     * @return a CompletableFuture that completes when the event is recorded
     */
    CompletableFuture<Void> recordEventAsync(String eventName, Map<String, Object> eventData, Map<String, String> tags);

    /**
     * Gets the overall system health.
     *
     * @return the system health status
     */
    SystemHealth getSystemHealth();

    /**
     * Gets the overall system health asynchronously.
     *
     * @return a CompletableFuture that completes with the system health status
     */
    CompletableFuture<SystemHealth> getSystemHealthAsync();

    /**
     * Gets performance metrics for a specific time range.
     *
     * @param startTime the start time
     * @param endTime the end time
     * @return the performance metrics
     */
    PerformanceMetrics getPerformanceMetrics(Instant startTime, Instant endTime);

    /**
     * Gets performance metrics for a specific time range asynchronously.
     *
     * @param startTime the start time
     * @param endTime the end time
     * @return a CompletableFuture that completes with the performance metrics
     */
    CompletableFuture<PerformanceMetrics> getPerformanceMetricsAsync(Instant startTime, Instant endTime);

    /**
     * Gets current performance metrics.
     *
     * @return the current performance metrics
     */
    PerformanceMetrics getCurrentPerformanceMetrics();

    /**
     * Gets current performance metrics asynchronously.
     *
     * @return a CompletableFuture that completes with the current performance metrics
     */
    CompletableFuture<PerformanceMetrics> getCurrentPerformanceMetricsAsync();

    /**
     * Gets component health checks.
     *
     * @return list of component health checks
     */
    List<ComponentHealth> getComponentHealth();

    /**
     * Gets component health checks asynchronously.
     *
     * @return a CompletableFuture that completes with the component health checks
     */
    CompletableFuture<List<ComponentHealth>> getComponentHealthAsync();

    /**
     * Gets monitoring statistics.
     *
     * @return the monitoring statistics
     */
    MonitoringStatistics getStatistics();

    /**
     * Gets monitoring statistics asynchronously.
     *
     * @return a CompletableFuture that completes with the monitoring statistics
     */
    CompletableFuture<MonitoringStatistics> getStatisticsAsync();

    /**
     * Gets the monitoring manager identifier.
     *
     * @return the monitoring manager identifier
     */
    String getMonitoringManagerId();

    /**
     * Gets the monitoring manager display name.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Gets the monitoring manager version.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Checks if the monitoring manager is available.
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();

    /**
     * Performs a health check on the monitoring manager.
     *
     * @return the health check result
     */
    MonitoringHealthCheck healthCheck();
}
