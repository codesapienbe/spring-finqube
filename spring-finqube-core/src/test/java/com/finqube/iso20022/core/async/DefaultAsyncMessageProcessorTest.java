package com.finqube.iso20022.core.async;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.finqube.iso20022.core.async.impl.DefaultAsyncMessageProcessor;
import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;
import com.finqube.iso20022.core.message.pain.Pain001Message;
import com.finqube.iso20022.core.template.Iso20022TemplateOperations;
import com.finqube.iso20022.core.transport.TransportFactory;
import com.finqube.iso20022.core.transport.TransportResponse;
import com.finqube.iso20022.core.transport.TransportStatus;
import com.finqube.iso20022.core.validation.MessageValidator;
import com.finqube.iso20022.core.validation.ValidationResult;

/**
 * Unit tests for DefaultAsyncMessageProcessor.
 *
 * <p>This test class validates the async message processor functionality,
 * including async processing, timeout handling, and error scenarios.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@DisplayName("DefaultAsyncMessageProcessor Tests")
class DefaultAsyncMessageProcessorTest {

    private DefaultAsyncMessageProcessor processor;
    private Iso20022TemplateOperations template;
    private TransportFactory transportFactory;
    private MessageValidator validator;

    @BeforeEach
    void setUp() {
        template = mock(Iso20022TemplateOperations.class);
        transportFactory = mock(TransportFactory.class);
        validator = mock(MessageValidator.class);

        processor = new DefaultAsyncMessageProcessor(template, transportFactory, validator, 2);
    }

    @Test
    @DisplayName("Should process message asynchronously successfully")
    void shouldProcessMessageAsynchronouslySuccessfully() throws Exception {
        // Given
        BaseMessage message = createValidMessage();
        ValidationResult validationResult = ValidationResult.success(message.getMessageId(), Instant.now(), 100);
        TransportResponse transportResponse = createSuccessfulTransportResponse();

        when(validator.validate(message)).thenReturn(validationResult);
        when(template.send(message)).thenReturn("success");

        // When
        CompletableFuture<ProcessingResult> future = processor.processAsync(message);
        ProcessingResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getMessageId()).isEqualTo(message.getMessageId());
        assertThat(result.getProcessingTimeMillis()).isPositive();
        assertThat(result.getStatus()).isEqualTo(ProcessingResult.ProcessingStatus.SUCCESS);
    }

    @Test
    @DisplayName("Should process XML content asynchronously successfully")
    void shouldProcessXmlContentAsynchronouslySuccessfully() throws Exception {
        // Given
        String xmlContent = "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.11\">...</Document>";
        ValidationResult validationResult = ValidationResult.success("xml-test", Instant.now(), 50);
        TransportResponse transportResponse = createSuccessfulTransportResponse();

        when(template.sendMessage(xmlContent)).thenReturn("success");

        // When
        CompletableFuture<ProcessingResult> future = processor.processXmlAsync(xmlContent);
        ProcessingResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getProcessingTimeMillis()).isPositive();
        assertThat(result.getStatus()).isEqualTo(ProcessingResult.ProcessingStatus.SUCCESS);
    }

    @Test
    @DisplayName("Should handle validation failure")
    void shouldHandleValidationFailure() throws Exception {
        // Given
        BaseMessage message = createValidMessage();
        ValidationResult validationResult = ValidationResult.failure(message.getMessageId(),
            List.of(), Instant.now(), 100);

        when(validator.validate(message)).thenReturn(validationResult);

        // When
        CompletableFuture<ProcessingResult> future = processor.processAsync(message);
        ProcessingResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getStatus()).isEqualTo(ProcessingResult.ProcessingStatus.FAILED);
        assertThat(result.getErrorMessage()).contains("validation failed");
    }

    @Test
    @DisplayName("Should handle transport failure")
    void shouldHandleTransportFailure() throws Exception {
        // Given
        BaseMessage message = createValidMessage();
        ValidationResult validationResult = ValidationResult.success(message.getMessageId(), Instant.now(), 100);
        TransportResponse transportResponse = createFailedTransportResponse();

        when(validator.validate(message)).thenReturn(validationResult);
        when(template.send(message)).thenReturn("success");

        // When
        CompletableFuture<ProcessingResult> future = processor.processAsync(message);
        ProcessingResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getStatus()).isEqualTo(ProcessingResult.ProcessingStatus.FAILED);
        assertThat(result.getErrorMessage()).contains("Transport failed");
    }

    @Test
    @DisplayName("Should handle timeout")
    void shouldHandleTimeout() throws Exception {
        // Given
        BaseMessage message = createValidMessage();
        when(validator.validate(message)).thenAnswer(invocation -> {
            Thread.sleep(2000); // Simulate slow validation
            return ValidationResult.success(message.getMessageId(), Instant.now(), 100);
        });

        // When
        CompletableFuture<ProcessingResult> future = processor.processAsync(message, 1, TimeUnit.SECONDS);
        ProcessingResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getStatus()).isEqualTo(ProcessingResult.ProcessingStatus.TIMEOUT);
    }

    @Test
    @DisplayName("Should handle processor shutdown")
    void shouldHandleProcessorShutdown() throws Exception {
        // Given
        BaseMessage message = createValidMessage();
        processor.shutdownNow();

        // When
        CompletableFuture<ProcessingResult> future = processor.processAsync(message);
        ProcessingResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getStatus()).isEqualTo(ProcessingResult.ProcessingStatus.FAILED);
        assertThat(result.getErrorMessage()).contains("shutdown");
    }

    @Test
    @DisplayName("Should provide processor information")
    void shouldProvideProcessorInformation() {
        // When & Then
        assertThat(processor.getProcessorId()).isEqualTo("default-async");
        assertThat(processor.getDisplayName()).isEqualTo("Default Async Message Processor");
        assertThat(processor.getVersion()).isEqualTo("1.0");
        assertThat(processor.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Should provide processing statistics")
    void shouldProvideProcessingStatistics() throws Exception {
        // Given
        BaseMessage message = createValidMessage();
        ValidationResult validationResult = ValidationResult.success(message.getMessageId(), Instant.now(), 100);
        TransportResponse transportResponse = createSuccessfulTransportResponse();

        when(validator.validate(message)).thenReturn(validationResult);
        when(template.send(message)).thenReturn("success");

        // When
        processor.processAsync(message).get(5, TimeUnit.SECONDS);
        ProcessingStatistics stats = processor.getStatistics();

        // Then
        assertThat(stats.getTotalMessagesProcessed()).isEqualTo(1);
        assertThat(stats.getSuccessfulMessages()).isEqualTo(1);
        assertThat(stats.getFailedMessages()).isEqualTo(0);
        assertThat(stats.getSuccessRate()).isEqualTo(100.0);
        assertThat(stats.getAverageProcessingTimeMillis()).isPositive();
    }

    @Test
    @DisplayName("Should provide health check information")
    void shouldProvideHealthCheckInformation() {
        // When
        ProcessingHealthCheck healthCheck = processor.healthCheck();

        // Then
        assertThat(healthCheck.getProcessorId()).isEqualTo("default-async");
        assertThat(healthCheck.isHealthy()).isTrue();
        assertThat(healthCheck.getComponents()).isNotEmpty();
        assertThat(healthCheck.getResponseTimeMillis()).isPositive();
    }

    @Test
    @DisplayName("Should handle fire-and-forget processing")
    void shouldHandleFireAndForgetProcessing() throws Exception {
        // Given
        BaseMessage message = createValidMessage();
        ValidationResult validationResult = ValidationResult.success(message.getMessageId(), Instant.now(), 100);
        TransportResponse transportResponse = createSuccessfulTransportResponse();

        when(validator.validate(message)).thenReturn(validationResult);
        when(template.send(message)).thenReturn("success");

        // When
        processor.submitForProcessing(message);

        // Wait a bit for processing to complete
        Thread.sleep(1000);

        // Then
        ProcessingStatistics stats = processor.getStatistics();
        assertThat(stats.getTotalMessagesProcessed()).isEqualTo(1);
        assertThat(stats.getSuccessfulMessages()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle fire-and-forget XML processing")
    void shouldHandleFireAndForgetXmlProcessing() throws Exception {
        // Given
        String xmlContent = "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.11\">...</Document>";
        ValidationResult validationResult = ValidationResult.success("xml-test", Instant.now(), 50);
        TransportResponse transportResponse = createSuccessfulTransportResponse();

        when(template.sendMessage(xmlContent)).thenReturn("success");

        // When
        processor.submitXmlForProcessing(xmlContent);

        // Wait a bit for processing to complete
        Thread.sleep(1000);

        // Then
        ProcessingStatistics stats = processor.getStatistics();
        assertThat(stats.getTotalMessagesProcessed()).isEqualTo(1);
        assertThat(stats.getSuccessfulMessages()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw exception for null message")
    void shouldThrowExceptionForNullMessage() {
        // When & Then
        assertThatThrownBy(() -> processor.processAsync(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Message cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null XML content")
    void shouldThrowExceptionForNullXmlContent() {
        // When & Then
        assertThatThrownBy(() -> processor.processXmlAsync(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("XML content cannot be null");
    }

    @Test
    @DisplayName("Should handle availability changes")
    void shouldHandleAvailabilityChanges() {
        // Given
        assertThat(processor.isAvailable()).isTrue();

        // When
        processor.setAvailable(false);

        // Then
        assertThat(processor.isAvailable()).isFalse();

        ProcessingHealthCheck healthCheck = processor.healthCheck();
        assertThat(healthCheck.isHealthy()).isFalse();
    }

    // Helper methods
    private BaseMessage createValidMessage() {
        return new Pain001Message("MSG001", List.of(), 1, 1000.00) {
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
                return "Test payment message";
            }
        };
    }

    private TransportResponse createSuccessfulTransportResponse() {
        return new TransportResponse("MSG001", TransportStatus.SUCCESS, "Message sent successfully",
            Instant.now(), Instant.now(), Map.of());
    }

    private TransportResponse createFailedTransportResponse() {
        return new TransportResponse("MSG001", TransportStatus.FAILED, "Connection failed",
            Instant.now(), Instant.now(), Map.of());
    }
}
