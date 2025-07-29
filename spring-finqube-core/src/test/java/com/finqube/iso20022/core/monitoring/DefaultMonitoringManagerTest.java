package com.finqube.iso20022.core.monitoring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.finqube.iso20022.core.monitoring.impl.DefaultMonitoringManager;

/**
 * Unit tests for DefaultMonitoringManager.
 *
 * <p>This test class validates the monitoring manager functionality,
 * including metrics recording, health checks, and performance monitoring.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@DisplayName("DefaultMonitoringManager Tests")
class DefaultMonitoringManagerTest {

    private DefaultMonitoringManager monitoringManager;

    @BeforeEach
    void setUp() {
        monitoringManager = new DefaultMonitoringManager();
    }

    @Test
    @DisplayName("Should record metric successfully")
    void shouldRecordMetricSuccessfully() {
        // When
        monitoringManager.recordMetric("test.metric", 42.5, Map.of("tag1", "value1"));

        // Then
        assertThat(monitoringManager.getCurrentMetric("test.metric")).isEqualTo(42.5);
    }

    @Test
    @DisplayName("Should record metric asynchronously successfully")
    void shouldRecordMetricAsynchronouslySuccessfully() throws Exception {
        // When
        CompletableFuture<Void> future = monitoringManager.recordMetricAsync("async.metric", 100.0, Map.of());
        future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(monitoringManager.getCurrentMetric("async.metric")).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Should increment counter successfully")
    void shouldIncrementCounterSuccessfully() {
        // When
        monitoringManager.incrementCounter("test.counter", Map.of("tag1", "value1"));
        monitoringManager.incrementCounter("test.counter", Map.of("tag1", "value1"));

        // Then
        assertThat(monitoringManager.getCurrentCounter("test.counter")).isEqualTo(2);
    }

    @Test
    @DisplayName("Should increment counter asynchronously successfully")
    void shouldIncrementCounterAsynchronouslySuccessfully() throws Exception {
        // When
        CompletableFuture<Void> future1 = monitoringManager.incrementCounterAsync("async.counter", Map.of());
        CompletableFuture<Void> future2 = monitoringManager.incrementCounterAsync("async.counter", Map.of());

        future1.get(5, TimeUnit.SECONDS);
        future2.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(monitoringManager.getCurrentCounter("async.counter")).isEqualTo(2);
    }

    @Test
    @DisplayName("Should record timing successfully")
    void shouldRecordTimingSuccessfully() {
        // When
        monitoringManager.recordTiming("test.timing", 150, Map.of("tag1", "value1"));

        // Then
        assertThat(monitoringManager.getCurrentMetric("test.timing")).isEqualTo(150.0);
    }

    @Test
    @DisplayName("Should record timing asynchronously successfully")
    void shouldRecordTimingAsynchronouslySuccessfully() throws Exception {
        // When
        CompletableFuture<Void> future = monitoringManager.recordTimingAsync("async.timing", 200, Map.of());
        future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(monitoringManager.getCurrentMetric("async.timing")).isEqualTo(200.0);
    }

    @Test
    @DisplayName("Should record event successfully")
    void shouldRecordEventSuccessfully() {
        // When
        Map<String, Object> eventData = Map.of("key1", "value1", "key2", 42);
        monitoringManager.recordEvent("test.event", eventData, Map.of("tag1", "value1"));

        // Then - Event recording should not throw exceptions
        assertThat(monitoringManager.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Should record event asynchronously successfully")
    void shouldRecordEventAsynchronouslySuccessfully() throws Exception {
        // When
        Map<String, Object> eventData = Map.of("key1", "value1");
        CompletableFuture<Void> future = monitoringManager.recordEventAsync("async.event", eventData, Map.of());
        future.get(5, TimeUnit.SECONDS);

        // Then - Event recording should not throw exceptions
        assertThat(monitoringManager.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Should get system health successfully")
    void shouldGetSystemHealthSuccessfully() {
        // When
        SystemHealth health = monitoringManager.getSystemHealth();

        // Then
        assertThat(health).isNotNull();
        assertThat(health.getSystemId()).isEqualTo("default-monitoring");
        assertThat(health.isHealthy()).isTrue();
        assertThat(health.getComponents()).isNotEmpty();
        assertThat(health.getResponseTimeMillis()).isPositive();
        assertThat(health.getHealthPercentage()).isGreaterThan(0.0);
    }

    @Test
    @DisplayName("Should get system health asynchronously successfully")
    void shouldGetSystemHealthAsynchronouslySuccessfully() throws Exception {
        // When
        CompletableFuture<SystemHealth> future = monitoringManager.getSystemHealthAsync();
        SystemHealth health = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(health).isNotNull();
        assertThat(health.isHealthy()).isTrue();
    }

    @Test
    @DisplayName("Should get performance metrics successfully")
    void shouldGetPerformanceMetricsSuccessfully() {
        // Given
        Instant startTime = Instant.now().minusSeconds(300);
        Instant endTime = Instant.now();

        // When
        PerformanceMetrics metrics = monitoringManager.getPerformanceMetrics(startTime, endTime);

        // Then
        assertThat(metrics).isNotNull();
        assertThat(metrics.getSystemId()).isEqualTo("default-monitoring");
        assertThat(metrics.getStartTime()).isEqualTo(startTime);
        assertThat(metrics.getEndTime()).isEqualTo(endTime);
        assertThat(metrics.getTotalRequests()).isGreaterThan(0);
        assertThat(metrics.getSuccessRate()).isGreaterThan(0.0);
        assertThat(metrics.getErrorRate()).isGreaterThanOrEqualTo(0.0);
    }

    @Test
    @DisplayName("Should get performance metrics asynchronously successfully")
    void shouldGetPerformanceMetricsAsynchronouslySuccessfully() throws Exception {
        // Given
        Instant startTime = Instant.now().minusSeconds(300);
        Instant endTime = Instant.now();

        // When
        CompletableFuture<PerformanceMetrics> future = monitoringManager.getPerformanceMetricsAsync(startTime, endTime);
        PerformanceMetrics metrics = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(metrics).isNotNull();
        assertThat(metrics.getSystemId()).isEqualTo("default-monitoring");
    }

    @Test
    @DisplayName("Should get current performance metrics successfully")
    void shouldGetCurrentPerformanceMetricsSuccessfully() {
        // When
        PerformanceMetrics metrics = monitoringManager.getCurrentPerformanceMetrics();

        // Then
        assertThat(metrics).isNotNull();
        assertThat(metrics.getSystemId()).isEqualTo("default-monitoring");
        assertThat(metrics.getStartTime()).isBefore(metrics.getEndTime());
    }

    @Test
    @DisplayName("Should get current performance metrics asynchronously successfully")
    void shouldGetCurrentPerformanceMetricsAsynchronouslySuccessfully() throws Exception {
        // When
        CompletableFuture<PerformanceMetrics> future = monitoringManager.getCurrentPerformanceMetricsAsync();
        PerformanceMetrics metrics = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(metrics).isNotNull();
        assertThat(metrics.getSystemId()).isEqualTo("default-monitoring");
    }

    @Test
    @DisplayName("Should get component health successfully")
    void shouldGetComponentHealthSuccessfully() {
        // When
        List<ComponentHealth> components = monitoringManager.getComponentHealth();

        // Then
        assertThat(components).isNotEmpty();
        assertThat(components).allMatch(ComponentHealth::isHealthy);
        assertThat(components).extracting(ComponentHealth::getComponentId)
            .contains("monitoring-manager", "metrics-collection", "health-checks");
    }

    @Test
    @DisplayName("Should get component health asynchronously successfully")
    void shouldGetComponentHealthAsynchronouslySuccessfully() throws Exception {
        // When
        CompletableFuture<List<ComponentHealth>> future = monitoringManager.getComponentHealthAsync();
        List<ComponentHealth> components = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(components).isNotEmpty();
        assertThat(components).allMatch(ComponentHealth::isHealthy);
    }

    @Test
    @DisplayName("Should provide monitoring manager information")
    void shouldProvideMonitoringManagerInformation() {
        // When & Then
        assertThat(monitoringManager.getMonitoringManagerId()).isEqualTo("default-monitoring");
        assertThat(monitoringManager.getDisplayName()).isEqualTo("Default Monitoring Manager");
        assertThat(monitoringManager.getVersion()).isEqualTo("1.0");
        assertThat(monitoringManager.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Should provide monitoring statistics")
    void shouldProvideMonitoringStatistics() {
        // Given
        monitoringManager.recordMetric("test.metric", 42.5, Map.of());
        monitoringManager.incrementCounter("test.counter", Map.of());
        monitoringManager.recordTiming("test.timing", 150, Map.of());

        // When
        MonitoringStatistics stats = monitoringManager.getStatistics();

        // Then
        assertThat(stats.getMonitoringManagerId()).isEqualTo("default-monitoring");
        assertThat(stats.getTotalMetricsRecorded()).isGreaterThan(0);
        assertThat(stats.getTotalCountersIncremented()).isGreaterThan(0);
        assertThat(stats.getTotalTimingsRecorded()).isGreaterThan(0);
        assertThat(stats.getAverageProcessingTimeMillis()).isPositive();
        assertThat(stats.getOperationsPerSecond()).isGreaterThan(0.0);
    }

    @Test
    @DisplayName("Should provide monitoring statistics asynchronously")
    void shouldProvideMonitoringStatisticsAsynchronously() throws Exception {
        // When
        CompletableFuture<MonitoringStatistics> future = monitoringManager.getStatisticsAsync();
        MonitoringStatistics stats = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(stats).isNotNull();
        assertThat(stats.getMonitoringManagerId()).isEqualTo("default-monitoring");
    }

    @Test
    @DisplayName("Should provide health check information")
    void shouldProvideHealthCheckInformation() {
        // When
        MonitoringHealthCheck healthCheck = monitoringManager.healthCheck();

        // Then
        assertThat(healthCheck.getMonitoringManagerId()).isEqualTo("default-monitoring");
        assertThat(healthCheck.isHealthy()).isTrue();
        assertThat(healthCheck.getResponseTimeMillis()).isPositive();
        assertThat(healthCheck.getMetrics()).isNotEmpty();
    }

    @Test
    @DisplayName("Should throw exception for null metric name in recordMetric")
    void shouldThrowExceptionForNullMetricNameInRecordMetric() {
        // When & Then
        assertThatThrownBy(() -> monitoringManager.recordMetric(null, 42.5, Map.of()))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Metric name cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null metric name in incrementCounter")
    void shouldThrowExceptionForNullMetricNameInIncrementCounter() {
        // When & Then
        assertThatThrownBy(() -> monitoringManager.incrementCounter(null, Map.of()))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Metric name cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null metric name in recordTiming")
    void shouldThrowExceptionForNullMetricNameInRecordTiming() {
        // When & Then
        assertThatThrownBy(() -> monitoringManager.recordTiming(null, 150, Map.of()))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Metric name cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null event name in recordEvent")
    void shouldThrowExceptionForNullEventNameInRecordEvent() {
        // When & Then
        assertThatThrownBy(() -> monitoringManager.recordEvent(null, Map.of(), Map.of()))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Event name cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null start time in getPerformanceMetrics")
    void shouldThrowExceptionForNullStartTimeInGetPerformanceMetrics() {
        // When & Then
        assertThatThrownBy(() -> monitoringManager.getPerformanceMetrics(null, Instant.now()))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Start time cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null end time in getPerformanceMetrics")
    void shouldThrowExceptionForNullEndTimeInGetPerformanceMetrics() {
        // When & Then
        assertThatThrownBy(() -> monitoringManager.getPerformanceMetrics(Instant.now(), null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("End time cannot be null");
    }

    @Test
    @DisplayName("Should handle availability changes")
    void shouldHandleAvailabilityChanges() {
        // Given
        assertThat(monitoringManager.isAvailable()).isTrue();

        // When
        monitoringManager.setAvailable(false);

        // Then
        assertThat(monitoringManager.isAvailable()).isFalse();

        SystemHealth health = monitoringManager.getSystemHealth();
        assertThat(health.isHealthy()).isFalse();
    }

    @Test
    @DisplayName("Should get all current metrics")
    void shouldGetAllCurrentMetrics() {
        // Given
        monitoringManager.recordMetric("metric1", 10.0, Map.of());
        monitoringManager.recordMetric("metric2", 20.0, Map.of());

        // When
        Map<String, Double> allMetrics = monitoringManager.getAllCurrentMetrics();

        // Then
        assertThat(allMetrics).containsKeys("metric1", "metric2");
        assertThat(allMetrics.get("metric1")).isEqualTo(10.0);
        assertThat(allMetrics.get("metric2")).isEqualTo(20.0);
    }

    @Test
    @DisplayName("Should get all current counters")
    void shouldGetAllCurrentCounters() {
        // Given
        monitoringManager.incrementCounter("counter1", Map.of());
        monitoringManager.incrementCounter("counter1", Map.of());
        monitoringManager.incrementCounter("counter2", Map.of());

        // When
        Map<String, Long> allCounters = monitoringManager.getAllCurrentCounters();

        // Then
        assertThat(allCounters).containsKeys("counter1", "counter2");
        assertThat(allCounters.get("counter1")).isEqualTo(2);
        assertThat(allCounters.get("counter2")).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return zero for non-existent counter")
    void shouldReturnZeroForNonExistentCounter() {
        // When
        Long counterValue = monitoringManager.getCurrentCounter("non.existent");

        // Then
        assertThat(counterValue).isEqualTo(0);
    }

    @Test
    @DisplayName("Should return null for non-existent metric")
    void shouldReturnNullForNonExistentMetric() {
        // When
        Double metricValue = monitoringManager.getCurrentMetric("non.existent");

        // Then
        assertThat(metricValue).isNull();
    }
}
