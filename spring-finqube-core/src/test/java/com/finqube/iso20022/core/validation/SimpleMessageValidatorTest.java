package com.finqube.iso20022.core.validation;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;
import com.finqube.iso20022.core.message.pain.Pain001Message;
import com.finqube.iso20022.core.validation.impl.SimpleMessageValidator;

/**
 * Unit tests for SimpleMessageValidator.
 *
 * <p>This test class validates the simple message validator functionality,
 * including message validation, XML validation, and error reporting.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@DisplayName("SimpleMessageValidator Tests")
class SimpleMessageValidatorTest {

    private SimpleMessageValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SimpleMessageValidator();
    }

    @Test
    @DisplayName("Should validate valid message successfully")
    void shouldValidateValidMessageSuccessfully() throws ValidationException {
        // Given
        BaseMessage message = createValidMessage();

        // When
        ValidationResult result = validator.validate(message);

        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getErrors()).isEmpty();
        assertThat(result.getWarnings()).isEmpty();
        assertThat(result.getMessageId()).isEqualTo("MSG001");
        assertThat(result.getValidationTimeMillis()).isPositive();
    }

    @Test
    @DisplayName("Should detect missing message ID")
    void shouldDetectMissingMessageId() throws ValidationException {
        // Given
        BaseMessage message = createMessageWithMissingId();

        // When
        ValidationResult result = validator.validate(message);

        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).getCode()).isEqualTo("MISSING_MESSAGE_ID");
        assertThat(result.getErrors().get(0).getMessage()).isEqualTo("Message ID is required");
    }

    @Test
    @DisplayName("Should detect missing message type")
    void shouldDetectMissingMessageType() throws ValidationException {
        // Given
        BaseMessage message = createMessageWithMissingType();

        // When
        ValidationResult result = validator.validate(message);

        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).getCode()).isEqualTo("MISSING_MESSAGE_TYPE");
        assertThat(result.getErrors().get(0).getMessage()).isEqualTo("Message type is required");
    }

    @Test
    @DisplayName("Should detect missing business process")
    void shouldDetectMissingBusinessProcess() throws ValidationException {
        // Given
        BaseMessage message = createMessageWithMissingBusinessProcess();

        // When
        ValidationResult result = validator.validate(message);

        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).getCode()).isEqualTo("MISSING_BUSINESS_PROCESS");
        assertThat(result.getErrors().get(0).getMessage()).isEqualTo("Business process is required");
    }

    @Test
    @DisplayName("Should generate warnings for non-critical issues")
    void shouldGenerateWarningsForNonCriticalIssues() throws ValidationException {
        // Given
        BaseMessage message = createMessageWithWarnings();

        // When
        ValidationResult result = validator.validate(message);

        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getErrors()).isEmpty();
        assertThat(result.getWarnings()).isNotEmpty();
        assertThat(result.getWarnings()).anyMatch(w -> w.getCode().equals("MISSING_PRIORITY"));
    }

    @Test
    @DisplayName("Should validate XML content successfully")
    void shouldValidateXmlContentSuccessfully() throws ValidationException {
        // Given
        String validXml = "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.11\">...</Document>";

        // When
        ValidationResult result = validator.validateXml(validXml);

        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getErrors()).isEmpty();
        assertThat(result.getValidationTimeMillis()).isPositive();
    }

    @Test
    @DisplayName("Should detect empty XML content")
    void shouldDetectEmptyXmlContent() throws ValidationException {
        // Given
        String emptyXml = "";

        // When
        ValidationResult result = validator.validateXml(emptyXml);

        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).getCode()).isEqualTo("XML_EMPTY");
        assertThat(result.getErrors().get(0).getMessage()).isEqualTo("XML content is empty");
    }

    @Test
    @DisplayName("Should detect invalid XML content")
    void shouldDetectInvalidXmlContent() throws ValidationException {
        // Given
        String invalidXml = "<InvalidElement>content</InvalidElement>";

        // When
        ValidationResult result = validator.validateXml(invalidXml);

        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).getCode()).isEqualTo("XML_INVALID");
        assertThat(result.getErrors().get(0).getMessage()).isEqualTo("XML content does not contain Document element");
    }

    @Test
    @DisplayName("Should validate message asynchronously")
    void shouldValidateMessageAsynchronously() throws Exception {
        // Given
        BaseMessage message = createValidMessage();

        // When
        CompletableFuture<ValidationResult> future = validator.validateAsync(message);
        ValidationResult result = future.get();

        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getErrors()).isEmpty();
    }

    @Test
    @DisplayName("Should validate XML asynchronously")
    void shouldValidateXmlAsynchronously() throws Exception {
        // Given
        String validXml = "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.11\">...</Document>";

        // When
        CompletableFuture<ValidationResult> future = validator.validateXmlAsync(validXml);
        ValidationResult result = future.get();

        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getErrors()).isEmpty();
    }

    @Test
    @DisplayName("Should throw exception for null message")
    void shouldThrowExceptionForNullMessage() {
        // When & Then
        assertThatThrownBy(() -> validator.validate(null))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Message cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null XML content")
    void shouldThrowExceptionForNullXmlContent() {
        // When & Then
        assertThatThrownBy(() -> validator.validateXml(null))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("XML content cannot be null");
    }

    @Test
    @DisplayName("Should provide validator information")
    void shouldProvideValidatorInformation() {
        // When & Then
        assertThat(validator.getValidatorId()).isEqualTo("simple");
        assertThat(validator.getDisplayName()).isEqualTo("Simple Message Validator");
        assertThat(validator.getVersion()).isEqualTo("1.0");
        assertThat(validator.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Should provide validation statistics")
    void shouldProvideValidationStatistics() throws ValidationException {
        // Given
        BaseMessage message = createValidMessage();

        // When
        validator.validate(message);
        ValidationStatistics stats = validator.getStatistics();

        // Then
        assertThat(stats.getTotalValidations()).isEqualTo(1);
        assertThat(stats.getSuccessfulValidations()).isEqualTo(1);
        assertThat(stats.getFailedValidations()).isEqualTo(0);
        assertThat(stats.getSuccessRate()).isEqualTo(100.0);
        assertThat(stats.getAverageValidationTimeMillis()).isPositive();
    }

    @Test
    @DisplayName("Should provide health check information")
    void shouldProvideHealthCheckInformation() {
        // When
        ValidationHealthCheck healthCheck = validator.healthCheck();

        // Then
        assertThat(healthCheck.getValidatorId()).isEqualTo("simple");
        assertThat(healthCheck.isHealthy()).isTrue();
        assertThat(healthCheck.getComponents()).isNotEmpty();
        assertThat(healthCheck.getResponseTimeMillis()).isPositive();
    }

    @Test
    @DisplayName("Should handle validator availability changes")
    void shouldHandleValidatorAvailabilityChanges() {
        // Given
        assertThat(validator.isAvailable()).isTrue();

        // When
        validator.setAvailable(false);

        // Then
        assertThat(validator.isAvailable()).isFalse();

        ValidationHealthCheck healthCheck = validator.healthCheck();
        assertThat(healthCheck.isHealthy()).isFalse();
    }

    // Helper methods to create test messages
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

    private BaseMessage createMessageWithMissingId() {
        return new Pain001Message(null, List.of(), 1, 1000.00) {
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

    private BaseMessage createMessageWithMissingType() {
        return new Pain001Message("MSG001", List.of(), 1, 1000.00) {
            @Override
            public String getMessageType() {
                return null;
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

    private BaseMessage createMessageWithMissingBusinessProcess() {
        return new Pain001Message("MSG001", List.of(), 1, 1000.00) {
            @Override
            public String getMessageType() {
                return "pain.001";
            }

            @Override
            public String getBusinessProcess() {
                return null;
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

    private BaseMessage createMessageWithWarnings() {
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
                return null; // This will generate a warning
            }

            @Override
            public String getDescription() {
                return "Test payment message";
            }
        };
    }
}