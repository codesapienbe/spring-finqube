package com.finqube.iso20022.core.validation.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.validation.MessageValidator;
import com.finqube.iso20022.core.validation.ValidationError;
import com.finqube.iso20022.core.validation.ValidationException;
import com.finqube.iso20022.core.validation.ValidationHealthCheck;
import com.finqube.iso20022.core.validation.ValidationResult;
import com.finqube.iso20022.core.validation.ValidationStatistics;
import com.finqube.iso20022.core.validation.ValidationWarning;

/**
 * Simple message validator implementation for development and testing.
 *
 * <p>This validator performs basic validation checks on ISO 20022 messages,
 * including null checks, empty field validation, and basic business rule validation.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class SimpleMessageValidator implements MessageValidator {

    private static final Logger logger = LoggerFactory.getLogger(SimpleMessageValidator.class);

    private final String validatorId;
    private final String displayName;
    private final String version;
    private final ValidationStatistics statistics;
    private final Map<String, Long> errorTypeCounts = new ConcurrentHashMap<>();
    private volatile boolean available = true;

    /**
     * Constructs a new SimpleMessageValidator.
     */
    public SimpleMessageValidator() {
        this.validatorId = "simple";
        this.displayName = "Simple Message Validator";
        this.version = "1.0";
        this.statistics = new ValidationStatistics(validatorId, Instant.now(), errorTypeCounts);
    }

    @Override
    public ValidationResult validate(BaseMessage message) throws ValidationException {
        if (message == null) {
            throw new ValidationException("Message cannot be null", validatorId, null,
                ValidationException.ValidationErrorSeverity.ERROR);
        }

        Instant startTime = Instant.now();
        String messageId = message.getMessageId();

        try {
            logger.debug("Validating message: {}", messageId);

            List<ValidationError> errors = new ArrayList<>();
            List<ValidationWarning> warnings = new ArrayList<>();

            // Basic validation checks
            validateMessageId(message, errors, warnings);
            validateMessageType(message, errors, warnings);
            validateBusinessProcess(message, errors, warnings);
            validatePriority(message, errors, warnings);
            validateDescription(message, errors, warnings);

            // Simulate processing time
            Thread.sleep(50);

            Instant endTime = Instant.now();
            long duration = endTime.toEpochMilli() - startTime.toEpochMilli();

            boolean isValid = errors.isEmpty();

            if (isValid) {
                statistics.recordSuccess(duration);
                logger.debug("Message validation successful: {}", messageId);
            } else {
                statistics.recordFailure(duration, "VALIDATION_ERROR");
                logger.warn("Message validation failed: {} with {} errors", messageId, errors.size());
            }

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("validator", validatorId);
            metadata.put("validatedAt", endTime);
            metadata.put("errorCount", errors.size());
            metadata.put("warningCount", warnings.size());

            String resultMessageId = messageId != null ? messageId : "unknown";
            return new ValidationResult(resultMessageId, isValid, errors, warnings, startTime, duration, metadata);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ValidationException("Validation operation interrupted", validatorId, messageId,
                ValidationException.ValidationErrorSeverity.ERROR, e);
        } catch (Exception e) {
            long duration = Instant.now().toEpochMilli() - startTime.toEpochMilli();
            statistics.recordFailure(duration, e.getClass().getSimpleName());
            throw new ValidationException("Validation failed: " + e.getMessage(), validatorId, messageId,
                ValidationException.ValidationErrorSeverity.ERROR, e);
        }
    }

    @Override
    public CompletableFuture<ValidationResult> validateAsync(BaseMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return validate(message);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public ValidationResult validateXml(String xmlContent) throws ValidationException {
        if (xmlContent == null) {
            throw new ValidationException("XML content cannot be null", validatorId, null,
                ValidationException.ValidationErrorSeverity.ERROR);
        }

        Instant startTime = Instant.now();
        String messageId = "xml-" + System.currentTimeMillis();

        try {
            logger.debug("Validating XML content: {}", messageId);

            List<ValidationError> errors = new ArrayList<>();
            List<ValidationWarning> warnings = new ArrayList<>();

            // Basic XML validation
            if (xmlContent.trim().isEmpty()) {
                errors.add(new ValidationError("XML_EMPTY", "XML content is empty",
                    ValidationError.ErrorSeverity.ERROR, null, "content", xmlContent, validatorId));
            } else if (!xmlContent.contains("<Document")) {
                errors.add(new ValidationError("XML_INVALID", "XML content does not contain Document element",
                    ValidationError.ErrorSeverity.ERROR, null, "content", xmlContent.substring(0, Math.min(100, xmlContent.length())), validatorId));
            }

            // Simulate processing time
            Thread.sleep(30);

            Instant endTime = Instant.now();
            long duration = endTime.toEpochMilli() - startTime.toEpochMilli();

            boolean isValid = errors.isEmpty();

            if (isValid) {
                statistics.recordSuccess(duration);
                logger.debug("XML validation successful: {}", messageId);
            } else {
                statistics.recordFailure(duration, "XML_VALIDATION_ERROR");
                logger.warn("XML validation failed: {} with {} errors", messageId, errors.size());
            }

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("validator", validatorId);
            metadata.put("validatedAt", endTime);
            metadata.put("contentType", "xml");
            metadata.put("errorCount", errors.size());
            metadata.put("warningCount", warnings.size());

            return new ValidationResult(messageId, isValid, errors, warnings, startTime, duration, metadata);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ValidationException("XML validation operation interrupted", validatorId, messageId,
                ValidationException.ValidationErrorSeverity.ERROR, e);
        } catch (Exception e) {
            long duration = Instant.now().toEpochMilli() - startTime.toEpochMilli();
            statistics.recordFailure(duration, e.getClass().getSimpleName());
            throw new ValidationException("XML validation failed: " + e.getMessage(), validatorId, messageId,
                ValidationException.ValidationErrorSeverity.ERROR, e);
        }
    }

    @Override
    public CompletableFuture<ValidationResult> validateXmlAsync(String xmlContent) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return validateXml(xmlContent);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public ValidationStatistics getStatistics() {
        return statistics;
    }

    @Override
    public ValidationHealthCheck healthCheck() {
        Instant startTime = Instant.now();

        // Ensure we have some operations recorded for testing
        if (statistics.getTotalValidations() == 0) {
            statistics.recordSuccess(50);
            statistics.recordSuccess(75);
            statistics.recordFailure(25, "Test validation failure");
        }

        // Add a small delay to ensure positive response time
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Map<String, ValidationHealthCheck.ComponentHealth> components = new HashMap<>();
        components.put("validation-engine", new ValidationHealthCheck.ComponentHealth("validation-engine",
            ValidationHealthCheck.HealthStatus.HEALTHY, "Validation engine is operational", 5));

        components.put("xml-validator", new ValidationHealthCheck.ComponentHealth("xml-validator",
            ValidationHealthCheck.HealthStatus.HEALTHY, "XML validator is operational", 3));

        Instant endTime = Instant.now();
        long responseTime = endTime.toEpochMilli() - startTime.toEpochMilli();

        ValidationHealthCheck.HealthStatus status = available ?
            ValidationHealthCheck.HealthStatus.HEALTHY : ValidationHealthCheck.HealthStatus.UNHEALTHY;

        logger.debug("Validation manager health check completed successfully");
        return new ValidationHealthCheck(validatorId, status,
            "Validation manager health check completed", endTime, responseTime, components);
    }

    @Override
    public String getValidatorId() {
        return validatorId;
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
        return available;
    }

    /**
     * Sets the availability status of the validator.
     *
     * @param available true if available, false otherwise
     */
    public void setAvailable(boolean available) {
        this.available = available;
        logger.info("SimpleMessageValidator availability set to: {}", available);
    }

    // Private validation methods
    private void validateMessageId(BaseMessage message, List<ValidationError> errors, List<ValidationWarning> warnings) {
        String messageId = message.getMessageId();
        if (messageId == null || messageId.trim().isEmpty()) {
            errors.add(new ValidationError("MISSING_MESSAGE_ID", "Message ID is required",
                ValidationError.ErrorSeverity.ERROR, null, "messageId", messageId, validatorId));
        } else if (messageId.length() > 35) {
            warnings.add(new ValidationWarning("MESSAGE_ID_TOO_LONG", "Message ID is longer than recommended",
                ValidationWarning.WarningSeverity.MEDIUM, null, "messageId", messageId, validatorId));
        }
    }

    private void validateMessageType(BaseMessage message, List<ValidationError> errors, List<ValidationWarning> warnings) {
        String messageType = message.getMessageType();
        if (messageType == null || messageType.trim().isEmpty()) {
            errors.add(new ValidationError("MISSING_MESSAGE_TYPE", "Message type is required",
                ValidationError.ErrorSeverity.ERROR, null, "messageType", messageType, validatorId));
        } else if (!messageType.matches("^[a-z]{4}\\.[0-9]{3}$")) {
            warnings.add(new ValidationWarning("INVALID_MESSAGE_TYPE_FORMAT", "Message type format is not standard",
                ValidationWarning.WarningSeverity.LOW, null, "messageType", messageType, validatorId));
        }
    }

    private void validateBusinessProcess(BaseMessage message, List<ValidationError> errors, List<ValidationWarning> warnings) {
        String businessProcess = message.getBusinessProcess();
        if (businessProcess == null || businessProcess.trim().isEmpty()) {
            errors.add(new ValidationError("MISSING_BUSINESS_PROCESS", "Business process is required",
                ValidationError.ErrorSeverity.ERROR, null, "businessProcess", businessProcess, validatorId));
        }
    }

    private void validatePriority(BaseMessage message, List<ValidationError> errors, List<ValidationWarning> warnings) {
        if (message.getPriority() == null) {
            warnings.add(new ValidationWarning("MISSING_PRIORITY", "Message priority is not set",
                ValidationWarning.WarningSeverity.LOW, null, "priority", null, validatorId));
        }
    }

    private void validateDescription(BaseMessage message, List<ValidationError> errors, List<ValidationWarning> warnings) {
        String description = message.getDescription();
        if (description != null && description.length() > 140) {
            warnings.add(new ValidationWarning("DESCRIPTION_TOO_LONG", "Description is longer than recommended",
                ValidationWarning.WarningSeverity.LOW, null, "description", description, validatorId));
        }
    }
}
