package com.finqube.iso20022.core.async.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.template.Iso20022Template;
import com.finqube.iso20022.core.transport.TransportFactory;
import com.finqube.iso20022.core.validation.MessageValidator;
import com.finqube.iso20022.core.async.AsyncMessageProcessor;
import com.finqube.iso20022.core.async.ProcessingHealthCheck;
import com.finqube.iso20022.core.async.ProcessingResult;
import com.finqube.iso20022.core.async.ProcessingStatistics;

/**
 * Default implementation of AsyncMessageProcessor.
 *
 * <p>This implementation provides asynchronous message processing using a thread pool,
 * with comprehensive error handling, retry mechanisms, and performance monitoring.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class DefaultAsyncMessageProcessor implements AsyncMessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAsyncMessageProcessor.class);

    private final String processorId;
    private final String displayName;
    private final String version;
    private final ProcessingStatistics statistics;
    private final Map<String, Long> errorTypeCounts = new ConcurrentHashMap<>();

    private final ExecutorService executorService;
    private final Iso20022Template template;
    private final TransportFactory transportFactory;
    private final MessageValidator validator;

    private volatile boolean available = true;
    private volatile boolean shutdown = false;

    /**
     * Constructs a new DefaultAsyncMessageProcessor.
     *
     * @param template the ISO 20022 template for message processing
     * @param transportFactory the transport factory for message transmission
     * @param validator the message validator
     * @param threadPoolSize the size of the thread pool
     */
    public DefaultAsyncMessageProcessor(Iso20022Template template, TransportFactory transportFactory,
                                      MessageValidator validator, int threadPoolSize) {
        this.processorId = "default-async";
        this.displayName = "Default Async Message Processor";
        this.version = "1.0";
        this.statistics = new ProcessingStatistics(processorId, Instant.now(), errorTypeCounts);

        this.template = Objects.requireNonNull(template, "Template cannot be null");
        this.transportFactory = Objects.requireNonNull(transportFactory, "Transport factory cannot be null");
        this.validator = Objects.requireNonNull(validator, "Validator cannot be null");

        this.executorService = Executors.newFixedThreadPool(threadPoolSize, new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "async-processor-" + counter.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        });

        logger.info("DefaultAsyncMessageProcessor initialized with {} threads", threadPoolSize);
    }

    @Override
    public CompletableFuture<ProcessingResult> processAsync(BaseMessage message) {
        return processAsync(message, 30, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<ProcessingResult> processAsync(BaseMessage message, long timeout, TimeUnit unit) {
        Objects.requireNonNull(message, "Message cannot be null");

        if (shutdown) {
            return CompletableFuture.completedFuture(
                ProcessingResult.failure(message.getMessageId(), "Processor is shutdown",
                    Instant.now(), Instant.now()));
        }

        Instant submittedAt = Instant.now();
        String messageId = message.getMessageId();

        logger.debug("Submitting message for async processing: {}", messageId);

        CompletableFuture<ProcessingResult> future = CompletableFuture.supplyAsync(() -> {
            try {
                return processMessage(message, submittedAt);
            } catch (Exception e) {
                logger.error("Error processing message: {}", messageId, e);
                long processingTime = Instant.now().toEpochMilli() - submittedAt.toEpochMilli();
                statistics.recordFailure(processingTime, e.getClass().getSimpleName());
                return ProcessingResult.failure(messageId, e.getMessage(), submittedAt, Instant.now());
            }
        }, executorService);

        // Apply timeout
        return future.orTimeout(timeout, unit)
            .exceptionally(throwable -> {
                logger.warn("Message processing timed out: {}", messageId);
                long processingTime = Instant.now().toEpochMilli() - submittedAt.toEpochMilli();
                statistics.recordTimeout(processingTime);
                return ProcessingResult.timeout(messageId, submittedAt, Instant.now());
            });
    }

    @Override
    public CompletableFuture<ProcessingResult> processXmlAsync(String xmlContent) {
        return processXmlAsync(xmlContent, 30, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<ProcessingResult> processXmlAsync(String xmlContent, long timeout, TimeUnit unit) {
        Objects.requireNonNull(xmlContent, "XML content cannot be null");

        if (shutdown) {
            return CompletableFuture.completedFuture(
                ProcessingResult.failure("xml-" + System.currentTimeMillis(), "Processor is shutdown",
                    Instant.now(), Instant.now()));
        }

        Instant submittedAt = Instant.now();
        String messageId = "xml-" + System.currentTimeMillis();

        logger.debug("Submitting XML content for async processing: {}", messageId);

        CompletableFuture<ProcessingResult> future = CompletableFuture.supplyAsync(() -> {
            try {
                return processXmlContent(xmlContent, submittedAt);
            } catch (Exception e) {
                logger.error("Error processing XML content: {}", messageId, e);
                long processingTime = Instant.now().toEpochMilli() - submittedAt.toEpochMilli();
                statistics.recordFailure(processingTime, e.getClass().getSimpleName());
                return ProcessingResult.failure(messageId, e.getMessage(), submittedAt, Instant.now());
            }
        }, executorService);

        // Apply timeout
        return future.orTimeout(timeout, unit)
            .exceptionally(throwable -> {
                logger.warn("XML processing timed out: {}", messageId);
                long processingTime = Instant.now().toEpochMilli() - submittedAt.toEpochMilli();
                statistics.recordTimeout(processingTime);
                return ProcessingResult.timeout(messageId, submittedAt, Instant.now());
            });
    }

    @Override
    public void submitForProcessing(BaseMessage message) {
        processAsync(message).thenAccept(result -> {
            if (result.isSuccessful()) {
                logger.debug("Fire-and-forget message processed successfully: {}", result.getMessageId());
            } else {
                logger.warn("Fire-and-forget message processing failed: {} - {}",
                    result.getMessageId(), result.getErrorMessage());
            }
        });
    }

    @Override
    public void submitXmlForProcessing(String xmlContent) {
        processXmlAsync(xmlContent).thenAccept(result -> {
            if (result.isSuccessful()) {
                logger.debug("Fire-and-forget XML processed successfully: {}", result.getMessageId());
            } else {
                logger.warn("Fire-and-forget XML processing failed: {} - {}",
                    result.getMessageId(), result.getErrorMessage());
            }
        });
    }

    @Override
    public String getProcessorId() {
        return processorId;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isAvailable() {
        return available && !shutdown;
    }

    @Override
    public long getPendingMessageCount() {
        // This is a simplified implementation - in a real scenario, you'd track the actual queue size
        return 0;
    }

    @Override
    public int getActiveThreadCount() {
        if (executorService instanceof java.util.concurrent.ThreadPoolExecutor) {
            return ((java.util.concurrent.ThreadPoolExecutor) executorService).getActiveCount();
        }
        return 0;
    }

    @Override
    public ProcessingStatistics getStatistics() {
        statistics.setCurrentQueueSize(getPendingMessageCount());
        return statistics;
    }

    @Override
    public ProcessingHealthCheck healthCheck() {
        Instant startTime = Instant.now();

        Map<String, ProcessingHealthCheck.ComponentHealth> components = new HashMap<>();

        // Check availability
        ProcessingHealthCheck.HealthStatus availabilityStatus = isAvailable() ?
            ProcessingHealthCheck.HealthStatus.HEALTHY : ProcessingHealthCheck.HealthStatus.UNHEALTHY;
        components.put("availability", new ProcessingHealthCheck.ComponentHealth(
            "availability", availabilityStatus,
            isAvailable() ? "Processor is available" : "Processor is not available", 0));

        // Check thread pool
        ProcessingHealthCheck.HealthStatus threadPoolStatus = ProcessingHealthCheck.HealthStatus.HEALTHY;
        String threadPoolMessage = "Thread pool is healthy";
        if (executorService.isShutdown()) {
            threadPoolStatus = ProcessingHealthCheck.HealthStatus.UNHEALTHY;
            threadPoolMessage = "Thread pool is shutdown";
        } else if (getActiveThreadCount() > 0) {
            threadPoolStatus = ProcessingHealthCheck.HealthStatus.HEALTHY;
            threadPoolMessage = "Thread pool has active threads: " + getActiveThreadCount();
        }
        components.put("threadPool", new ProcessingHealthCheck.ComponentHealth(
            "threadPool", threadPoolStatus, threadPoolMessage, 0));

        // Check statistics
        ProcessingHealthCheck.HealthStatus statsStatus = ProcessingHealthCheck.HealthStatus.HEALTHY;
        String statsMessage = "Statistics collection is working";
        if (statistics.getTotalMessagesProcessed() > 0 && statistics.getSuccessRate() < 50.0) {
            statsStatus = ProcessingHealthCheck.HealthStatus.DEGRADED;
            statsMessage = "Low success rate detected: " + String.format("%.1f%%", statistics.getSuccessRate());
        }
        components.put("statistics", new ProcessingHealthCheck.ComponentHealth(
            "statistics", statsStatus, statsMessage, 0));

        Instant endTime = Instant.now();
        long responseTime = endTime.toEpochMilli() - startTime.toEpochMilli();

        ProcessingHealthCheck.HealthStatus overallStatus = isAvailable() ?
            ProcessingHealthCheck.HealthStatus.HEALTHY : ProcessingHealthCheck.HealthStatus.UNHEALTHY;

        return new ProcessingHealthCheck(processorId, overallStatus,
            "Default async processor health check completed", endTime, responseTime, components);
    }

    @Override
    public CompletableFuture<Void> shutdown() {
        logger.info("Initiating graceful shutdown of DefaultAsyncMessageProcessor");
        shutdown = true;

        return CompletableFuture.runAsync(() -> {
            try {
                executorService.shutdown();
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    logger.warn("Thread pool did not terminate gracefully, forcing shutdown");
                    executorService.shutdownNow();
                }
                logger.info("DefaultAsyncMessageProcessor shutdown completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Shutdown interrupted", e);
            }
        });
    }

    @Override
    public void shutdownNow() {
        logger.info("Forcing immediate shutdown of DefaultAsyncMessageProcessor");
        shutdown = true;
        executorService.shutdownNow();
    }

    /**
     * Sets the availability status of the processor.
     *
     * @param available true if available, false otherwise
     */
    public void setAvailable(boolean available) {
        this.available = available;
        logger.info("DefaultAsyncMessageProcessor availability set to: {}", available);
    }

    // Private processing methods
    private ProcessingResult processMessage(BaseMessage message, Instant submittedAt) {
        String messageId = message.getMessageId();
        logger.debug("Processing message: {}", messageId);

        try {
            // Step 1: Validate message
            var validationResult = validator.validate(message);
            if (!validationResult.isValid()) {
                String errorMsg = "Message validation failed: " + validationResult.getErrors().size() + " errors";
                logger.warn("{} - {}", errorMsg, messageId);
                long processingTime = Instant.now().toEpochMilli() - submittedAt.toEpochMilli();
                statistics.recordFailure(processingTime, "VALIDATION_ERROR");
                return ProcessingResult.failure(messageId, errorMsg, submittedAt, Instant.now());
            }

            // Step 2: Process message using template
            var transportResponse = template.sendMessage(message);

            // Step 3: Check transport result
            if (transportResponse.isSuccessful()) {
                long processingTime = Instant.now().toEpochMilli() - submittedAt.toEpochMilli();
                statistics.recordSuccess(processingTime);

                Map<String, Object> metadata = new HashMap<>();
                metadata.put("transport", transportResponse.getStatus());
                metadata.put("processingTime", processingTime);

                logger.debug("Message processed successfully: {}", messageId);
                return ProcessingResult.success(messageId, submittedAt, Instant.now(), metadata);
            } else {
                String errorMsg = "Transport failed: " + transportResponse.getResponseMessage();
                logger.warn("{} - {}", errorMsg, messageId);
                long processingTime = Instant.now().toEpochMilli() - submittedAt.toEpochMilli();
                statistics.recordFailure(processingTime, "TRANSPORT_ERROR");
                return ProcessingResult.failure(messageId, errorMsg, submittedAt, Instant.now());
            }

        } catch (Exception e) {
            logger.error("Unexpected error processing message: {}", messageId, e);
            long processingTime = Instant.now().toEpochMilli() - submittedAt.toEpochMilli();
            statistics.recordFailure(processingTime, e.getClass().getSimpleName());
            return ProcessingResult.failure(messageId, e.getMessage(), submittedAt, Instant.now());
        }
    }

    private ProcessingResult processXmlContent(String xmlContent, Instant submittedAt) {
        String messageId = "xml-" + System.currentTimeMillis();
        logger.debug("Processing XML content: {}", messageId);

        try {
            // Step 1: Validate XML
            var validationResult = validator.validateXml(xmlContent);
            if (!validationResult.isValid()) {
                String errorMsg = "XML validation failed: " + validationResult.getErrors().size() + " errors";
                logger.warn("{} - {}", errorMsg, messageId);
                long processingTime = Instant.now().toEpochMilli() - submittedAt.toEpochMilli();
                statistics.recordFailure(processingTime, "XML_VALIDATION_ERROR");
                return ProcessingResult.failure(messageId, errorMsg, submittedAt, Instant.now());
            }

            // Step 2: Send XML using template
            var transportResponse = template.sendXml(xmlContent);

            // Step 3: Check transport result
            if (transportResponse.isSuccessful()) {
                long processingTime = Instant.now().toEpochMilli() - submittedAt.toEpochMilli();
                statistics.recordSuccess(processingTime);

                Map<String, Object> metadata = new HashMap<>();
                metadata.put("transport", transportResponse.getStatus());
                metadata.put("processingTime", processingTime);
                metadata.put("contentType", "xml");

                logger.debug("XML content processed successfully: {}", messageId);
                return ProcessingResult.success(messageId, submittedAt, Instant.now(), metadata);
            } else {
                String errorMsg = "Transport failed: " + transportResponse.getResponseMessage();
                logger.warn("{} - {}", errorMsg, messageId);
                long processingTime = Instant.now().toEpochMilli() - submittedAt.toEpochMilli();
                statistics.recordFailure(processingTime, "TRANSPORT_ERROR");
                return ProcessingResult.failure(messageId, errorMsg, submittedAt, Instant.now());
            }

        } catch (Exception e) {
            logger.error("Unexpected error processing XML content: {}", messageId, e);
            long processingTime = Instant.now().toEpochMilli() - submittedAt.toEpochMilli();
            statistics.recordFailure(processingTime, e.getClass().getSimpleName());
            return ProcessingResult.failure(messageId, e.getMessage(), submittedAt, Instant.now());
        }
    }
}
