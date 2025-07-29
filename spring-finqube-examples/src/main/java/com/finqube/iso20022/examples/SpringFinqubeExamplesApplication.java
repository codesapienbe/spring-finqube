package com.finqube.iso20022.examples;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.finqube.iso20022.core.async.AsyncMessageProcessor;
import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;
import com.finqube.iso20022.core.message.pain.Pain001Message;
import com.finqube.iso20022.core.monitoring.MonitoringManager;
import com.finqube.iso20022.core.security.SecurityManager;
import com.finqube.iso20022.core.security.SignedMessage;
import com.finqube.iso20022.core.template.Iso20022Template;
import com.finqube.iso20022.core.translation.TranslationManager;
import com.finqube.iso20022.core.translation.TranslationOptions;
import com.finqube.iso20022.core.translation.TranslationResult;
import com.finqube.iso20022.core.transport.Transport;
import com.finqube.iso20022.core.transport.TransportFactory;
import com.finqube.iso20022.core.transport.TransportResponse;
import com.finqube.iso20022.core.validation.MessageValidator;
import com.finqube.iso20022.core.validation.ValidationResult;

/**
 * Comprehensive example application demonstrating Spring Finqube ISO 20022 capabilities.
 *
 * <p>This application showcases all major features of the Spring Finqube system including:</p>
 * <ul>
 *   <li>Message creation and processing</li>
 *   <li>Template-based message generation</li>
 *   <li>Message validation and business rules</li>
 *   <li>Asynchronous message processing</li>
 *   <li>Security operations (signing, encryption)</li>
 *   <li>Message translation and format conversion</li>
 *   <li>Transport and delivery</li>
 *   <li>Monitoring and metrics</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@SpringBootApplication
public class SpringFinqubeExamplesApplication {

    private static final Logger logger = LoggerFactory.getLogger(SpringFinqubeExamplesApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringFinqubeExamplesApplication.class, args);
    }

    @Bean
    public CommandLineRunner exampleRunner(Iso20022Template template, MessageValidator validator,
                                         AsyncMessageProcessor asyncProcessor, SecurityManager securityManager,
                                         TranslationManager translationManager, TransportFactory transportFactory,
                                         MonitoringManager monitoringManager) {
        return args -> {
            logger.info("=== Spring Finqube ISO 20022 Examples ===");

            try {
                // Run all examples
                runMessageCreationExample(template);
                runMessageValidationExample(validator);
                runAsyncProcessingExample(asyncProcessor);
                runSecurityExample(securityManager);
                runTranslationExample(translationManager);
                runTransportExample(transportFactory);
                runMonitoringExample(monitoringManager);
                runIntegrationExample(template, validator, securityManager, translationManager, transportFactory, monitoringManager);

                logger.info("=== All examples completed successfully ===");

            } catch (Exception e) {
                logger.error("Error running examples", e);
            }
        };
    }

    /**
     * Example 1: Message Creation and Template Usage
     */
    private void runMessageCreationExample(Iso20022Template template) {
        logger.info("\n--- Example 1: Message Creation and Template Usage ---");

        try {
            // Create a sample PAIN.001 message
            Pain001Message message = new Pain001Message("MSG001", List.of("TXN001"), 1, 1000.00) {
                @Override
                public String getMessageType() {
                    return "pain.001";
                }

                @Override
                public String getBusinessProcess() {
                    return "pain";
                }

                @Override
                public MessagePriority getPriority() {
                    return MessagePriority.HIGH;
                }

                @Override
                public String getDescription() {
                    return "Sample payment instruction";
                }
            };

            logger.info("Created message: {}", message.getMessageId());
            logger.info("Message type: {}", message.getMessageType());
            logger.info("Priority: {}", message.getPriority());
            logger.info("Amount: {}", message.getTotalAmount());
            logger.info("Transaction count: {}", message.getTransactionCount());

            // Generate XML using template
            String xml = template.generateXml(message);
            logger.info("Generated XML length: {} characters", xml.length());
            logger.info("XML preview: {}", xml.substring(0, Math.min(200, xml.length())) + "...");

        } catch (Exception e) {
            logger.error("Error in message creation example", e);
        }
    }

    /**
     * Example 2: Message Validation
     */
    private void runMessageValidationExample(MessageValidator validator) {
        logger.info("\n--- Example 2: Message Validation ---");

        try {
            // Create a message for validation
            Pain001Message message = new Pain001Message("VAL001", List.of("TXN001"), 1, 1000.00) {
                @Override
                public String getMessageType() {
                    return "pain.001";
                }

                @Override
                public String getBusinessProcess() {
                    return "pain";
                }

                @Override
                public MessagePriority getPriority() {
                    return MessagePriority.NORMAL;
                }

                @Override
                public String getDescription() {
                    return "Validation test message";
                }
            };

            // Validate the message
            ValidationResult result = validator.validate(message);

            logger.info("Validation result: {}", result.isValid() ? "VALID" : "INVALID");
            logger.info("Error count: {}", result.getErrors().size());
            logger.info("Warning count: {}", result.getWarnings().size());

            if (!result.getErrors().isEmpty()) {
                result.getErrors().forEach(error ->
                    logger.warn("Validation error: {} - {}", error.getField(), error.getMessage()));
            }

            if (!result.getWarnings().isEmpty()) {
                result.getWarnings().forEach(warning ->
                    logger.info("Validation warning: {} - {}", warning.getField(), warning.getMessage()));
            }

        } catch (Exception e) {
            logger.error("Error in message validation example", e);
        }
    }

    /**
     * Example 3: Asynchronous Message Processing
     */
    private void runAsyncProcessingExample(AsyncMessageProcessor asyncProcessor) {
        logger.info("\n--- Example 3: Asynchronous Message Processing ---");

        try {
            // Create messages for async processing
            Pain001Message message1 = createSampleMessage("ASYNC001", 500.00);
            Pain001Message message2 = createSampleMessage("ASYNC002", 750.00);
            Pain001Message message3 = createSampleMessage("ASYNC003", 1250.00);

            // Submit messages for async processing
            CompletableFuture<Void> future1 = asyncProcessor.processAsync(message1);
            CompletableFuture<Void> future2 = asyncProcessor.processAsync(message2);
            CompletableFuture<Void> future3 = asyncProcessor.processAsync(message3);

            // Wait for completion
            CompletableFuture.allOf(future1, future2, future3).join();

            logger.info("All async processing completed successfully");

            // Get processing statistics
            var stats = asyncProcessor.getStatistics();
            logger.info("Total messages processed: {}", stats.getTotalMessagesProcessed());
            logger.info("Successful processing: {}", stats.getSuccessfulProcessing());
            logger.info("Failed processing: {}", stats.getFailedProcessing());
            logger.info("Average processing time: {}ms", stats.getAverageProcessingTimeMillis());

        } catch (Exception e) {
            logger.error("Error in async processing example", e);
        }
    }

    /**
     * Example 4: Security Operations
     */
    private void runSecurityExample(SecurityManager securityManager) {
        logger.info("\n--- Example 4: Security Operations ---");

        try {
            // Create a message for security operations
            Pain001Message message = createSampleMessage("SEC001", 1000.00);

            // Sign the message
            SignedMessage signedMessage = securityManager.sign(message);
            logger.info("Message signed successfully: {}", signedMessage.getMessageId());
            logger.info("Signature algorithm: {}", signedMessage.getSignatureAlgorithm());
            logger.info("Signed at: {}", signedMessage.getSignedAt());

            // Verify the signature
            var verificationResult = securityManager.verifySignature(signedMessage);
            logger.info("Signature verification: {}", verificationResult.isValid() ? "VALID" : "INVALID");

            // Get security statistics
            var securityStats = securityManager.getStatistics();
            logger.info("Total security operations: {}", securityStats.getTotalOperations());
            logger.info("Successful operations: {}", securityStats.getSuccessfulOperations());
            logger.info("Success rate: {:.2f}%", securityStats.getSuccessRate());

        } catch (Exception e) {
            logger.error("Error in security example", e);
        }
    }

    /**
     * Example 5: Message Translation
     */
    private void runTranslationExample(TranslationManager translationManager) {
        logger.info("\n--- Example 5: Message Translation ---");

        try {
            // Create a message for translation
            Pain001Message message = createSampleMessage("TRANS001", 1000.00);

            // Translate MT103 to MX103
            TranslationOptions options = TranslationOptions.builder()
                .enableCaching(true)
                .enableValidation(true)
                .enableWarnings(true)
                .timeoutMillis(30000)
                .build();

            TranslationResult result = translationManager.translate(message, "MT103", "MX103", options);

            logger.info("Translation completed: {}", result.isSuccessful() ? "SUCCESS" : "FAILED");
            logger.info("Translation ID: {}", result.getTranslationId());
            logger.info("Source format: {} -> Target format: {}", result.getSourceFormat(), result.getTargetFormat());
            logger.info("Translation time: {}ms", result.getTranslationTimeMillis());

            if (result.hasWarnings()) {
                logger.info("Translation warnings: {}", result.getWarningCount());
                result.getWarnings().forEach((key, value) ->
                    logger.info("Warning - {}: {}", key, value));
            }

            // Get translation statistics
            var translationStats = translationManager.getStatistics();
            logger.info("Total translations: {}", translationStats.getTotalTranslations());
            logger.info("Success rate: {:.2f}%", translationStats.getSuccessRate());
            logger.info("Average translation time: {:.2f}ms", translationStats.getAverageTranslationTimeMillis());

        } catch (Exception e) {
            logger.error("Error in translation example", e);
        }
    }

    /**
     * Example 6: Transport and Delivery
     */
    private void runTransportExample(TransportFactory transportFactory) {
        logger.info("\n--- Example 6: Transport and Delivery ---");

        try {
            // Create a message for transport
            Pain001Message message = createSampleMessage("TRANS001", 1000.00);

            // Get available transports
            List<Transport> transports = transportFactory.getAvailableTransports();
            logger.info("Available transports: {}", transports.size());

            transports.forEach(transport ->
                logger.info("Transport: {} - {}", transport.getTransportId(), transport.getDisplayName()));

            // Send message using logging transport (for demonstration)
            Transport loggingTransport = transportFactory.getTransport("logging");
            if (loggingTransport != null) {
                TransportResponse response = loggingTransport.send(message);

                logger.info("Transport response: {}", response.getStatus());
                logger.info("Response time: {}ms", response.getResponseTimeMillis());
                logger.info("Response message: {}", response.getMessage());
            }

            // Get transport statistics
            var transportStats = transportFactory.getStatistics();
            logger.info("Total transport operations: {}", transportStats.getTotalOperations());
            logger.info("Successful operations: {}", transportStats.getSuccessfulOperations());
            logger.info("Success rate: {:.2f}%", transportStats.getSuccessRate());

        } catch (Exception e) {
            logger.error("Error in transport example", e);
        }
    }

    /**
     * Example 7: Monitoring and Metrics
     */
    private void runMonitoringExample(MonitoringManager monitoringManager) {
        logger.info("\n--- Example 7: Monitoring and Metrics ---");

        try {
            // Record some metrics
            monitoringManager.recordMetric("message.processing.time", 150.5, Map.of("type", "pain.001"));
            monitoringManager.incrementCounter("messages.processed", Map.of("type", "pain.001"));
            monitoringManager.recordTiming("validation.time", 25, Map.of("type", "pain.001"));
            monitoringManager.recordEvent("message.sent", Map.of("messageId", "MSG001"), Map.of("type", "pain.001"));

            // Get system health
            var systemHealth = monitoringManager.getSystemHealth();
            logger.info("System health: {}", systemHealth.getStatus());
            logger.info("Health percentage: {:.2f}%", systemHealth.getHealthPercentage());
            logger.info("Healthy components: {}/{}", systemHealth.getHealthyComponentCount(), systemHealth.getComponents().size());

            // Get performance metrics
            var performanceMetrics = monitoringManager.getCurrentPerformanceMetrics();
            logger.info("Total requests: {}", performanceMetrics.getTotalRequests());
            logger.info("Success rate: {:.2f}%", performanceMetrics.getSuccessRate());
            logger.info("Average response time: {:.2f}ms", performanceMetrics.getAverageResponseTimeMillis());
            logger.info("Requests per second: {:.2f}", performanceMetrics.getRequestsPerSecond());

            // Get monitoring statistics
            var monitoringStats = monitoringManager.getStatistics();
            logger.info("Total monitoring operations: {}", monitoringStats.getTotalOperations());
            logger.info("Operations per second: {:.2f}", monitoringStats.getOperationsPerSecond());

        } catch (Exception e) {
            logger.error("Error in monitoring example", e);
        }
    }

    /**
     * Example 8: Integration Example - End-to-End Workflow
     */
    private void runIntegrationExample(Iso20022Template template, MessageValidator validator,
                                     SecurityManager securityManager, TranslationManager translationManager,
                                     TransportFactory transportFactory, MonitoringManager monitoringManager) {
        logger.info("\n--- Example 8: Integration Example - End-to-End Workflow ---");

        try {
            // Step 1: Create message
            Pain001Message originalMessage = createSampleMessage("INTEGRATION001", 2000.00);
            logger.info("Step 1: Created message {}", originalMessage.getMessageId());

            // Step 2: Validate message
            ValidationResult validationResult = validator.validate(originalMessage);
            if (!validationResult.isValid()) {
                logger.error("Message validation failed");
                return;
            }
            logger.info("Step 2: Message validation passed");

            // Step 3: Sign message
            SignedMessage signedMessage = securityManager.sign(originalMessage);
            logger.info("Step 3: Message signed successfully");

            // Step 4: Translate message
            TranslationResult translationResult = translationManager.translate(signedMessage, "MT103", "MX103");
            if (!translationResult.isSuccessful()) {
                logger.error("Message translation failed");
                return;
            }
            logger.info("Step 4: Message translated successfully");

            // Step 5: Send message
            Transport transport = transportFactory.getTransport("logging");
            if (transport != null) {
                TransportResponse response = transport.send(translationResult.getTranslatedMessage());
                logger.info("Step 5: Message sent successfully - Status: {}", response.getStatus());
            }

            // Step 6: Record metrics
            monitoringManager.recordMetric("integration.workflow.completion.time",
                translationResult.getTranslationTimeMillis(), Map.of("workflow", "end-to-end"));
            monitoringManager.incrementCounter("integration.workflows.completed", Map.of("type", "pain.001"));

            logger.info("Integration workflow completed successfully!");

        } catch (Exception e) {
            logger.error("Error in integration example", e);
        }
    }

    /**
     * Helper method to create sample messages
     */
    private Pain001Message createSampleMessage(String messageId, double amount) {
        return new Pain001Message(messageId, List.of("TXN" + messageId), 1, amount) {
            @Override
            public String getMessageType() {
                return "pain.001";
            }

            @Override
            public String getBusinessProcess() {
                return "pain";
            }

            @Override
            public MessagePriority getPriority() {
                return MessagePriority.NORMAL;
            }

            @Override
            public String getDescription() {
                return "Sample message for " + messageId;
            }
        };
    }
}
