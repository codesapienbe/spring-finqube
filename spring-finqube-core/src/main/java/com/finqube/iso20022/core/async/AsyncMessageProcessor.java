package com.finqube.iso20022.core.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.finqube.iso20022.core.message.BaseMessage;

/**
 * Asynchronous message processor for ISO 20022 messages.
 *
 * <p>This interface defines the contract for processing ISO 20022 messages asynchronously,
 * including validation, transport, and error handling in a non-blocking manner.</p>
 *
 * <p>The async processor supports:</p>
 * <ul>
 *   <li>Non-blocking message processing</li>
 *   <li>Background validation and transport operations</li>
 *   <li>Async error handling and retry mechanisms</li>
 *   <li>Performance monitoring and metrics</li>
 *   <li>Connection pooling and resource management</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * @Autowired
 * private AsyncMessageProcessor asyncProcessor;
 *
 * CompletableFuture<ProcessingResult> future = asyncProcessor.processAsync(message);
 * future.thenAccept(result -> {
 *     if (result.isSuccessful()) {
 *         logger.info("Message processed successfully: {}", result.getMessageId());
 *     } else {
 *         logger.error("Message processing failed: {}", result.getErrorMessage());
 *     }
 * });
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface AsyncMessageProcessor {

    /**
     * Processes an ISO 20022 message asynchronously.
     *
     * <p>This method starts the processing pipeline asynchronously and returns
     * immediately with a CompletableFuture that will complete when processing is finished.</p>
     *
     * @param message the message to process
     * @return a CompletableFuture that will complete with the processing result
     */
    CompletableFuture<ProcessingResult> processAsync(BaseMessage message);

    /**
     * Processes an ISO 20022 message asynchronously with a timeout.
     *
     * @param message the message to process
     * @param timeout the timeout value
     * @param unit the timeout unit
     * @return a CompletableFuture that will complete with the processing result
     */
    CompletableFuture<ProcessingResult> processAsync(BaseMessage message, long timeout, TimeUnit unit);

    /**
     * Processes raw XML content asynchronously.
     *
     * @param xmlContent the XML content to process
     * @return a CompletableFuture that will complete with the processing result
     */
    CompletableFuture<ProcessingResult> processXmlAsync(String xmlContent);

    /**
     * Processes raw XML content asynchronously with a timeout.
     *
     * @param xmlContent the XML content to process
     * @param timeout the timeout value
     * @param unit the timeout unit
     * @return a CompletableFuture that will complete with the processing result
     */
    CompletableFuture<ProcessingResult> processXmlAsync(String xmlContent, long timeout, TimeUnit unit);

    /**
     * Submits a message for processing and returns immediately.
     *
     * <p>This method is useful for fire-and-forget scenarios where you don't
     * need to wait for the result.</p>
     *
     * @param message the message to process
     */
    void submitForProcessing(BaseMessage message);

    /**
     * Submits XML content for processing and returns immediately.
     *
     * @param xmlContent the XML content to process
     */
    void submitXmlForProcessing(String xmlContent);

    /**
     * Gets the processor identifier.
     *
     * @return the processor identifier
     */
    String getProcessorId();

    /**
     * Gets the processor display name.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Gets the processor version.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Checks if the processor is available.
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();

    /**
     * Gets the number of pending messages.
     *
     * @return the number of pending messages
     */
    long getPendingMessageCount();

    /**
     * Gets the number of active processing threads.
     *
     * @return the number of active threads
     */
    int getActiveThreadCount();

    /**
     * Gets the processor statistics.
     *
     * @return the processor statistics
     */
    ProcessingStatistics getStatistics();

    /**
     * Performs a health check on the processor.
     *
     * @return the health check result
     */
    ProcessingHealthCheck healthCheck();

    /**
     * Shuts down the processor gracefully.
     *
     * <p>This method initiates a graceful shutdown, allowing pending messages
     * to complete processing before shutting down.</p>
     *
     * @return a CompletableFuture that completes when shutdown is finished
     */
    CompletableFuture<Void> shutdown();

    /**
     * Shuts down the processor immediately.
     *
     * <p>This method shuts down the processor immediately, potentially
     * losing pending messages.</p>
     */
    void shutdownNow();
}
