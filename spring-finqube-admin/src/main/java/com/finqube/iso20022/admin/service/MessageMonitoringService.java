package com.finqube.iso20022.admin.service;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service interface for monitoring ISO 20022 financial messages in real-time.
 *
 * <p>This service provides comprehensive monitoring capabilities including:
 * <ul>
 *   <li>Real-time message statistics and metrics</li>
 *   <li>Message flow tracking and visualization</li>
 *   <li>Performance monitoring and alerting</li>
 *   <li>Historical data analysis</li>
 * </ul></p>
 *
 * <p>The service integrates with the core Spring Finqube modules to provide
 * accurate and up-to-date monitoring information.</p>
 *
 * @author Spring Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface MessageMonitoringService {

    /**
     * Gets the total count of messages processed by the system.
     *
     * @return total message count
     */
    long getTotalMessageCount();

    /**
     * Gets the count of messages processed today.
     *
     * @return today's message count
     */
    long getTodayMessageCount();

    /**
     * Gets the count of pending messages.
     *
     * @return pending message count
     */
    long getPendingMessageCount();

    /**
     * Gets the count of messages with errors.
     *
     * @return error message count
     */
    long getErrorMessageCount();

    /**
     * Gets message statistics by priority.
     *
     * @return map of priority to message count
     */
    Map<MessagePriority, Long> getMessageCountByPriority();

    /**
     * Gets message statistics by message type.
     *
     * @return map of message type to count
     */
    Map<String, Long> getMessageCountByType();

    /**
     * Gets recent messages within the specified time range.
     *
     * @param from start time
     * @param to end time
     * @return list of recent messages
     */
    List<BaseMessage> getRecentMessages(LocalDateTime from, LocalDateTime to);

    /**
     * Gets messages with errors within the specified time range.
     *
     * @param from start time
     * @param to end time
     * @return list of error messages
     */
    List<BaseMessage> getErrorMessages(LocalDateTime from, LocalDateTime to);

    /**
     * Gets processing performance metrics.
     *
     * @return processing performance data
     */
    ProcessingPerformanceMetrics getProcessingPerformanceMetrics();

    /**
     * Subscribes to real-time message updates.
     *
     * @param listener the listener to receive updates
     */
    void subscribeToUpdates(MessageUpdateListener listener);

    /**
     * Unsubscribes from real-time message updates.
     *
     * @param listener the listener to remove
     */
    void unsubscribeFromUpdates(MessageUpdateListener listener);

    /**
     * Listener interface for real-time message updates.
     */
    interface MessageUpdateListener {

        /**
         * Called when a new message is processed.
         *
         * @param message the processed message
         */
        void onMessageProcessed(BaseMessage message);

        /**
         * Called when a message processing error occurs.
         *
         * @param message the message that caused the error
         * @param error the error details
         */
        void onMessageError(BaseMessage message, String error);
    }

}
