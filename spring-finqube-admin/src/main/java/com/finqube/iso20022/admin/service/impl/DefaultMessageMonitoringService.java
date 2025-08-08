package com.finqube.iso20022.admin.service.impl;

import com.finqube.iso20022.admin.service.MessageMonitoringService;
import com.finqube.iso20022.admin.service.ProcessingPerformanceMetrics;
import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;
import com.finqube.iso20022.core.monitoring.MonitoringManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default implementation of the MessageMonitoringService.
 *
 * <p>This service provides real-time monitoring capabilities by integrating
 * with the core Spring Finqube monitoring components and maintaining
 * in-memory statistics for dashboard display.</p>
 *
 * <p>The service uses scheduled tasks to update metrics and provides
 * real-time updates through listener callbacks.</p>
 *
 * @author Spring Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Service
public class DefaultMessageMonitoringService implements MessageMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMessageMonitoringService.class);

    private final MonitoringManager monitoringManager;
    private final List<MessageMonitoringService.MessageUpdateListener> listeners = new CopyOnWriteArrayList<>();

    // In-memory statistics for real-time dashboard updates
    private final AtomicLong totalMessageCount = new AtomicLong(0);
    private final AtomicLong todayMessageCount = new AtomicLong(0);
    private final AtomicLong pendingMessageCount = new AtomicLong(0);
    private final AtomicLong errorMessageCount = new AtomicLong(0);
    private final Map<MessagePriority, AtomicLong> priorityCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> typeCounts = new ConcurrentHashMap<>();

    /**
     * Constructs the service with required dependencies.
     *
     * @param monitoringManager the core monitoring manager
     */
    @Autowired
    public DefaultMessageMonitoringService(MonitoringManager monitoringManager) {
        this.monitoringManager = monitoringManager;
        initializeCounters();
        logger.info("Message monitoring service initialized");
    }

    @Override
    public long getTotalMessageCount() {
        return totalMessageCount.get();
    }

    @Override
    public long getTodayMessageCount() {
        return todayMessageCount.get();
    }

    @Override
    public long getPendingMessageCount() {
        return pendingMessageCount.get();
    }

    @Override
    public long getErrorMessageCount() {
        return errorMessageCount.get();
    }

    @Override
    public Map<MessagePriority, Long> getMessageCountByPriority() {
        Map<MessagePriority, Long> result = new ConcurrentHashMap<>();
        for (Map.Entry<MessagePriority, AtomicLong> entry : priorityCounts.entrySet()) {
            result.put(entry.getKey(), entry.getValue().get());
        }
        return result;
    }

    @Override
    public Map<String, Long> getMessageCountByType() {
        Map<String, Long> result = new ConcurrentHashMap<>();
        for (Map.Entry<String, AtomicLong> entry : typeCounts.entrySet()) {
            result.put(entry.getKey(), entry.getValue().get());
        }
        return result;
    }

    @Override
    public List<BaseMessage> getRecentMessages(LocalDateTime from, LocalDateTime to) {
        // TODO: Implement integration with message storage/repository
        logger.debug("Retrieving recent messages from {} to {}", from, to);
        return List.of(); // Placeholder implementation
    }

    @Override
    public List<BaseMessage> getErrorMessages(LocalDateTime from, LocalDateTime to) {
        // TODO: Implement integration with error tracking
        logger.debug("Retrieving error messages from {} to {}", from, to);
        return List.of(); // Placeholder implementation
    }

    @Override
    public ProcessingPerformanceMetrics getProcessingPerformanceMetrics() {
        // TODO: Implement real performance metrics calculation
        return new ProcessingPerformanceMetrics(
            150.5, // average processing time in ms
            25.3,  // messages per second
            0.5,   // error rate percentage
            12,    // active connections
            LocalDateTime.now()
        );
    }

    @Override
    public void subscribeToUpdates(MessageMonitoringService.MessageUpdateListener listener) {
        listeners.add(listener);
        logger.debug("Added message update listener: {}", listener.getClass().getSimpleName());
    }

    @Override
    public void unsubscribeFromUpdates(MessageMonitoringService.MessageUpdateListener listener) {
        listeners.remove(listener);
        logger.debug("Removed message update listener: {}", listener.getClass().getSimpleName());
    }

    /**
     * Initializes the message counters with default values.
     */
    private void initializeCounters() {
        // Initialize priority counters
        for (MessagePriority priority : MessagePriority.values()) {
            priorityCounts.put(priority, new AtomicLong(0));
        }

        // Initialize with some sample data for demonstration
        totalMessageCount.set(1234);
        todayMessageCount.set(56);
        pendingMessageCount.set(12);
        errorMessageCount.set(3);

        priorityCounts.get(MessagePriority.HIGH).set(45);
        priorityCounts.get(MessagePriority.NORMAL).set(1189);
        priorityCounts.get(MessagePriority.LOW).set(0);

        typeCounts.put("PAIN.001", new AtomicLong(456));
        typeCounts.put("PAIN.002", new AtomicLong(234));
        typeCounts.put("PACS.002", new AtomicLong(345));
        typeCounts.put("PACS.004", new AtomicLong(199));
    }

    /**
     * Scheduled task to update daily message count at midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDailyCount() {
        todayMessageCount.set(0);
        logger.info("Reset daily message count");
    }

    /**
     * Scheduled task to update metrics every 30 seconds.
     */
    @Scheduled(fixedRate = 30000)
    public void updateMetrics() {
        // TODO: Implement real metrics update from monitoring manager
        logger.debug("Updating message monitoring metrics");

        // Simulate some activity for demonstration
        if (Math.random() < 0.3) { // 30% chance of new message
            totalMessageCount.incrementAndGet();
            todayMessageCount.incrementAndGet();

                    // Notify listeners
        for (MessageMonitoringService.MessageUpdateListener listener : listeners) {
            try {
                // TODO: Create actual message object
                // listener.onMessageProcessed(message);
            } catch (Exception e) {
                logger.warn("Error notifying listener: {}", e.getMessage());
            }
        }
        }
    }

    /**
     * Method to be called when a message is processed (integration point).
     *
     * @param message the processed message
     */
    public void onMessageProcessed(BaseMessage message) {
        totalMessageCount.incrementAndGet();
        todayMessageCount.incrementAndGet();

        // Update priority count
        priorityCounts.computeIfAbsent(message.getPriority(), k -> new AtomicLong(0))
                     .incrementAndGet();

        // Update type count
        String messageType = message.getClass().getSimpleName();
        typeCounts.computeIfAbsent(messageType, k -> new AtomicLong(0))
                  .incrementAndGet();

        // Notify listeners
        for (MessageMonitoringService.MessageUpdateListener listener : listeners) {
            try {
                listener.onMessageProcessed(message);
            } catch (Exception e) {
                logger.warn("Error notifying listener: {}", e.getMessage());
            }
        }

        logger.debug("Message processed: {} (Priority: {})", messageType, message.getPriority());
    }

    /**
     * Method to be called when a message processing error occurs (integration point).
     *
     * @param message the message that caused the error
     * @param error the error details
     */
    public void onMessageError(BaseMessage message, String error) {
        errorMessageCount.incrementAndGet();

        // Notify listeners
        for (MessageMonitoringService.MessageUpdateListener listener : listeners) {
            try {
                listener.onMessageError(message, error);
            } catch (Exception e) {
                logger.warn("Error notifying listener: {}", e.getMessage());
            }
        }

        logger.warn("Message processing error: {} - {}",
                   message.getClass().getSimpleName(), error);
    }
}
