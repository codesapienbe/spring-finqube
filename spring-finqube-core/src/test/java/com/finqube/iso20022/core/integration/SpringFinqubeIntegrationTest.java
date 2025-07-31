package com.finqube.iso20022.core.integration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import com.finqube.iso20022.core.async.AsyncMessageProcessor;
import com.finqube.iso20022.core.async.ProcessingResult;
import com.finqube.iso20022.core.async.impl.DefaultAsyncMessageProcessor;
import com.finqube.iso20022.core.message.MessagePriority;
import com.finqube.iso20022.core.message.pain.Pain001Message;
import com.finqube.iso20022.core.monitoring.MonitoringManager;
import com.finqube.iso20022.core.monitoring.SystemHealth;
import com.finqube.iso20022.core.monitoring.impl.DefaultMonitoringManager;
import com.finqube.iso20022.core.security.SecurityManager;
import com.finqube.iso20022.core.security.SignedMessage;
import com.finqube.iso20022.core.security.impl.DefaultSecurityManager;
import com.finqube.iso20022.core.template.Iso20022Template;
import com.finqube.iso20022.core.translation.TranslationManager;
import com.finqube.iso20022.core.translation.TranslationOptions;
import com.finqube.iso20022.core.translation.TranslationResult;
import com.finqube.iso20022.core.translation.impl.DefaultTranslationManager;
import com.finqube.iso20022.core.transport.Transport;
import com.finqube.iso20022.core.transport.TransportFactory;
import com.finqube.iso20022.core.transport.TransportResponse;
import com.finqube.iso20022.core.transport.TransportStatus;
import com.finqube.iso20022.core.validation.MessageValidator;
import com.finqube.iso20022.core.validation.ValidationResult;
import com.finqube.iso20022.core.validation.impl.SimpleMessageValidator;

/**
 * Comprehensive integration tests for Spring Finqube ISO 20022 system.
 *
 * <p>This test class validates the complete end-to-end functionality of the Spring Finqube system,
 * including message creation, validation, processing, security, translation, transport, and monitoring.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@SpringBootTest(classes = SpringFinqubeIntegrationTest.TestConfig.class)
@ActiveProfiles("test")
@DisplayName("Spring Finqube Integration Tests")
class SpringFinqubeIntegrationTest {

    @Configuration
    static class TestConfig {
        // Spring Boot test configuration
        @Bean
        public Iso20022Template iso20022Template() {
            return new Iso20022Template();
        }

        @Bean
        public MessageValidator messageValidator() {
            return new SimpleMessageValidator();
        }

        @Bean
        public AsyncMessageProcessor asyncMessageProcessor() {
            return new DefaultAsyncMessageProcessor(
                iso20022Template(),
                transportFactory(),
                messageValidator(),
                2
            );
        }

        @Bean
        public SecurityManager securityManager() {
            return new DefaultSecurityManager();
        }

        @Bean
        public TranslationManager translationManager() {
            return new DefaultTranslationManager();
        }

        @Bean
        public TransportFactory transportFactory() {
            return new TransportFactory();
        }

        @Bean
        public MonitoringManager monitoringManager() {
            return new DefaultMonitoringManager();
        }
    }

    @Autowired
    private Iso20022Template template;

    @Autowired
    private MessageValidator validator;

    @Autowired
    private AsyncMessageProcessor asyncProcessor;

    @Autowired
    private SecurityManager securityManager;

    @Autowired
    private TranslationManager translationManager;

    @Autowired
    private TransportFactory transportFactory;

    @Autowired
    private MonitoringManager monitoringManager;

    private Pain001Message testMessage;

    @BeforeEach
    void setUp() {
        testMessage = createTestMessage();
    }

    @Test
    @DisplayName("Should perform complete end-to-end message processing workflow")
    void shouldPerformCompleteEndToEndMessageProcessingWorkflow() throws Exception {
        // Step 1: Create and validate message
        ValidationResult validationResult = validator.validate(testMessage);
        assertThat(validationResult.isValid()).isTrue();
        assertThat(validationResult.getErrors()).isEmpty();

        // Step 2: Generate XML using template
        String xml = template.generateXml(testMessage);
        assertThat(xml).isNotNull();
        assertThat(xml).contains("pain.001");
        assertThat(xml).contains(testMessage.getMessageId());

        // Step 3: Sign message
        SignedMessage signedMessage = securityManager.sign(testMessage);
        assertThat(signedMessage).isNotNull();
        assertThat(signedMessage.getMessageId()).isEqualTo(testMessage.getMessageId());
        assertThat(signedMessage.getSignature()).isNotNull();

        // Step 4: Verify signature
        var verificationResult = securityManager.verifySignature(signedMessage);
        assertThat(verificationResult.isValid()).isTrue();

        // Step 5: Translate message
        TranslationOptions translationOptions = TranslationOptions.builder()
            .enableCaching(true)
            .enableValidation(true)
            .enableWarnings(true)
            .build();

        TranslationResult translationResult = translationManager.translate(testMessage, "MT103", "MX103", translationOptions);
        assertThat(translationResult.isSuccessful()).isTrue();
        assertThat(translationResult.getTranslatedMessage()).isNotNull();

        // Step 6: Send message via transport
        Transport transport = transportFactory.getTransport("logging");
        assertThat(transport).isNotNull();

        TransportResponse transportResponse = transport.send(testMessage);
        assertThat(transportResponse.getStatus()).isEqualTo(TransportStatus.SUCCESS);

        // Step 7: Verify monitoring metrics
        SystemHealth systemHealth = monitoringManager.getSystemHealth();
        assertThat(systemHealth.isHealthy()).isTrue();
        assertThat(systemHealth.getHealthPercentage()).isGreaterThan(0.0);
    }

    @Test
    @DisplayName("Should handle async message processing workflow")
    void shouldHandleAsyncMessageProcessingWorkflow() throws Exception {
        // Create multiple messages for async processing
        List<Pain001Message> messages = List.of(
            createTestMessage("ASYNC001", 1000.00),
            createTestMessage("ASYNC002", 2000.00),
            createTestMessage("ASYNC003", 3000.00)
        );

        // Submit all messages for async processing
        List<CompletableFuture<ProcessingResult>> futures = messages.stream()
            .map(asyncProcessor::processAsync)
            .toList();

        // Wait for all processing to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .get(30, TimeUnit.SECONDS);

        // Verify all futures completed successfully
        futures.forEach(future -> assertThat(future).isCompleted());

        // Verify processing statistics
        var stats = asyncProcessor.getStatistics();
        assertThat(stats.getTotalMessagesProcessed()).isGreaterThanOrEqualTo(messages.size());
        assertThat(stats.getSuccessfulProcessing()).isGreaterThanOrEqualTo(messages.size());
        assertThat(stats.getSuccessRate()).isGreaterThan(0.0);
    }

    @Test
    @DisplayName("Should handle security operations workflow")
    void shouldHandleSecurityOperationsWorkflow() throws Exception {
        // Test signing workflow
        SignedMessage signedMessage = securityManager.sign(testMessage);
        assertThat(signedMessage).isNotNull();
        assertThat(signedMessage.getSignature()).isNotNull();

        // Test signature verification
        var verificationResult = securityManager.verifySignature(signedMessage);
        assertThat(verificationResult.isValid()).isTrue();

        // Test encryption workflow (simulated)
        byte[] testCertificate = "TEST_CERTIFICATE".getBytes();
        var encryptedMessage = securityManager.encrypt(testMessage, testCertificate);
        assertThat(encryptedMessage).isNotNull();
        assertThat(encryptedMessage.getEncryptedContent()).isNotNull();

        // Test decryption
        var decryptedMessage = securityManager.decrypt(encryptedMessage);
        assertThat(decryptedMessage).isNotNull();
        assertThat(decryptedMessage.getMessageId()).isEqualTo(testMessage.getMessageId());

        // Test combined sign and encrypt
        var secureMessage = securityManager.signAndEncrypt(testMessage, testCertificate);
        assertThat(secureMessage).isNotNull();
        assertThat(secureMessage.getSignature()).isNotNull();
        assertThat(secureMessage.getEncryptedContent()).isNotNull();

        // Test verify and decrypt
        var secureResult = securityManager.verifyAndDecrypt(secureMessage);
        assertThat(secureResult.isSuccessful()).isTrue();
        assertThat(secureResult.getDecryptedMessage()).isNotNull();
        assertThat(secureResult.isSignatureValid()).isTrue();
    }

    @Test
    @DisplayName("Should handle translation workflow with caching")
    void shouldHandleTranslationWorkflowWithCaching() throws Exception {
        // Test translation with caching enabled
        TranslationOptions options = TranslationOptions.builder()
            .enableCaching(true)
            .enableValidation(true)
            .enableWarnings(true)
            .timeoutMillis(30000)
            .build();

        // First translation (cache miss)
        TranslationResult result1 = translationManager.translate(testMessage, "MT103", "MX103", options);
        assertThat(result1.isSuccessful()).isTrue();
        assertThat(result1.getTranslatedMessage()).isNotNull();

        // Second translation (cache hit)
        TranslationResult result2 = translationManager.translate(testMessage, "MT103", "MX103", options);
        assertThat(result2.isSuccessful()).isTrue();
        assertThat(result2.getTranslatedMessage()).isNotNull();

        // Verify cache statistics
        var cacheStats = translationManager.getCacheStatistics();
        assertThat(cacheStats.getCacheHits()).isGreaterThan(0);
        assertThat(cacheStats.getCacheSize()).isGreaterThan(0);

        // Test translation without caching
        TranslationOptions noCacheOptions = TranslationOptions.builder()
            .enableCaching(false)
            .build();

        TranslationResult result3 = translationManager.translate(testMessage, "MT103", "MX103", noCacheOptions);
        assertThat(result3.isSuccessful()).isTrue();

        // Verify translation statistics
        var translationStats = translationManager.getStatistics();
        assertThat(translationStats.getTotalTranslations()).isGreaterThan(0);
        assertThat(translationStats.getSuccessRate()).isGreaterThan(0.0);
    }

    @Test
    @DisplayName("Should handle transport workflow with multiple transports")
    void shouldHandleTransportWorkflowWithMultipleTransports() {
        // Get available transports
        var transports = transportFactory.getAvailableTransports();
        assertThat(transports).isNotEmpty();

        // Test each available transport
        for (var transportInfo : transports) {
            String transportId = transportInfo.getTransportId();
            Transport transport = transportFactory.getTransport(transportId);
            assertThat(transport).isNotNull();

            TransportResponse response = transport.send(testMessage);
            assertThat(response).isNotNull();
            assertThat(response.getStatus()).isNotNull();
            assertThat(response.getResponseTimeMillis()).isGreaterThanOrEqualTo(0);
        }

        // Test specific transport by ID
        Transport loggingTransport = transportFactory.getTransport("logging");
        if (loggingTransport != null) {
            TransportResponse response = loggingTransport.send(testMessage);
            assertThat(response.getStatus()).isEqualTo(TransportStatus.SUCCESS);
        }

        // Verify transport statistics
        var transportStats = transportFactory.getStatistics();
        assertThat(transportStats).isNotNull();
        assertThat(transportStats.getTransportId()).isNotNull();
    }

    @Test
    @DisplayName("Should handle monitoring and metrics workflow")
    void shouldHandleMonitoringAndMetricsWorkflow() {
        // Record various metrics
        monitoringManager.recordMetric("test.metric", 42.5, Map.of("type", "test"));
        monitoringManager.incrementCounter("test.counter", Map.of("type", "test"));
        monitoringManager.recordTiming("test.timing", 100, Map.of("type", "test"));
        monitoringManager.recordEvent("test.event", Map.of("key", "value"), Map.of("type", "test"));

        // Test system health
        SystemHealth systemHealth = monitoringManager.getSystemHealth();
        assertThat(systemHealth).isNotNull();
        assertThat(systemHealth.getSystemId()).isNotNull();
        assertThat(systemHealth.getStatus()).isNotNull();
        assertThat(systemHealth.getComponents()).isNotNull();

        // Test component health
        var componentHealth = monitoringManager.getComponentHealth();
        assertThat(componentHealth).isNotEmpty();
        componentHealth.forEach(component -> {
            assertThat(component.getComponentId()).isNotNull();
            assertThat(component.getComponentName()).isNotNull();
            assertThat(component.getStatus()).isNotNull();
        });

        // Test performance metrics
        var performanceMetrics = monitoringManager.getCurrentPerformanceMetrics();
        assertThat(performanceMetrics).isNotNull();
        assertThat(performanceMetrics.getSystemId()).isNotNull();
        assertThat(performanceMetrics.getTotalRequests()).isGreaterThanOrEqualTo(0);
        assertThat(performanceMetrics.getSuccessRate()).isGreaterThanOrEqualTo(0.0);

        // Test monitoring statistics
        var monitoringStats = monitoringManager.getStatistics();
        assertThat(monitoringStats).isNotNull();
        assertThat(monitoringStats.getMonitoringManagerId()).isNotNull();
        assertThat(monitoringStats.getTotalOperations()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should handle error scenarios gracefully")
    void shouldHandleErrorScenariosGracefully() {
        // Test validation with invalid message
        Pain001Message invalidMessage = createInvalidMessage();
        ValidationResult validationResult = validator.validate(invalidMessage);
        assertThat(validationResult.isValid()).isFalse();
        assertThat(validationResult.getErrors()).isNotEmpty();

        // Test translation with unsupported format
        assertThatThrownBy(() -> {
            translationManager.translate(testMessage, "UNSUPPORTED", "MX103");
        }).isInstanceOf(Exception.class);

        // Test transport with null message
        Transport transport = transportFactory.getTransport("logging");
        if (transport != null) {
            assertThatThrownBy(() -> {
                transport.send(null);
            }).isInstanceOf(Exception.class);
        }

        // Verify error metrics are recorded
        var monitoringStats = monitoringManager.getStatistics();
        assertThat(monitoringStats.getTotalOperations()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should handle concurrent operations")
    void shouldHandleConcurrentOperations() throws Exception {
        // Create multiple concurrent operations
        List<CompletableFuture<ValidationResult>> validationFutures = List.of(
            CompletableFuture.supplyAsync(() -> validator.validate(testMessage)),
            CompletableFuture.supplyAsync(() -> validator.validate(testMessage)),
            CompletableFuture.supplyAsync(() -> validator.validate(testMessage))
        );

        List<CompletableFuture<String>> templateFutures = List.of(
            CompletableFuture.supplyAsync(() -> template.generateXml(testMessage)),
            CompletableFuture.supplyAsync(() -> template.generateXml(testMessage)),
            CompletableFuture.supplyAsync(() -> template.generateXml(testMessage))
        );

        List<CompletableFuture<SignedMessage>> securityFutures = List.of(
            CompletableFuture.supplyAsync(() -> securityManager.sign(testMessage)),
            CompletableFuture.supplyAsync(() -> securityManager.sign(testMessage)),
            CompletableFuture.supplyAsync(() -> securityManager.sign(testMessage))
        );

        // Wait for all operations to complete
        CompletableFuture.allOf(
            Stream.concat(
                Stream.concat(
                    validationFutures.stream(),
                    templateFutures.stream()
                ),
                securityFutures.stream()
            ).toArray(CompletableFuture[]::new)
        ).get(30, TimeUnit.SECONDS);

        // Verify all operations completed successfully
        validationFutures.forEach(future -> {
            assertThat(future).isCompleted();
            assertThat(future.join().isValid()).isTrue();
        });

        templateFutures.forEach(future -> {
            assertThat(future).isCompleted();
            assertThat(future.join()).isNotNull();
            assertThat(future.join()).contains("pain.001");
        });

        securityFutures.forEach(future -> {
            assertThat(future).isCompleted();
            assertThat(future.join()).isNotNull();
            assertThat(future.join().getSignature()).isNotNull();
        });
    }

    @Test
    @DisplayName("Should handle system health and availability")
    void shouldHandleSystemHealthAndAvailability() {
        // Test system health
        SystemHealth systemHealth = monitoringManager.getSystemHealth();
        assertThat(systemHealth.isHealthy()).isTrue();
        assertThat(systemHealth.getHealthPercentage()).isGreaterThan(0.0);
        assertThat(systemHealth.getHealthyComponentCount()).isGreaterThan(0);

        // Test component health
        var componentHealth = monitoringManager.getComponentHealth();
        assertThat(componentHealth).isNotEmpty();

        long healthyComponents = componentHealth.stream()
            .filter(component -> component.isHealthy())
            .count();
        assertThat(healthyComponents).isGreaterThan(0);

        // Test availability
        assertThat(securityManager.isAvailable()).isTrue();
        assertThat(translationManager.isAvailable()).isTrue();
        assertThat(transportFactory.getAvailableTransports()).isNotEmpty();

        // Test health checks
        var securityHealth = securityManager.healthCheck();
        assertThat(securityHealth.isHealthy()).isTrue();

        var translationHealth = translationManager.healthCheck();
        assertThat(translationHealth.isHealthy()).isTrue();
    }

    // Helper methods
    private Pain001Message createTestMessage() {
        return createTestMessage("INTEGRATION001", 1000.00);
    }

    private Pain001Message createTestMessage(String messageId, double amount) {
        List<Pain001Message.PaymentInstruction> instructions = List.of(
            new Pain001Message.PaymentInstruction("TXN" + messageId, amount, "EUR",
                "DE12345678901234567890", "FR98765432109876543210", "Test payment")
        );
        return new Pain001Message(messageId, instructions, 1, amount) {
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
                return "Integration test message for " + messageId;
            }
        };
    }

    private Pain001Message createInvalidMessage() {
        List<Pain001Message.PaymentInstruction> instructions = List.of(
            new Pain001Message.PaymentInstruction("", -1000.00, "EUR", "", "", "")
        );
        return new Pain001Message("", instructions, 0, -1000.00) {
            @Override
            public String getMessageType() {
                return "";
            }

            @Override
            public String getBusinessProcess() {
                return "";
            }

            @Override
            public MessagePriority getPriority() {
                return MessagePriority.NORMAL;
            }

            @Override
            public String getDescription() {
                return "Invalid test message";
            }
        };
    }
}
