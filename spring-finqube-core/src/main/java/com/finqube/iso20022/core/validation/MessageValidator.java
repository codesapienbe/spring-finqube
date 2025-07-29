package com.finqube.iso20022.core.validation;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.finqube.iso20022.core.message.BaseMessage;

/**
 * Main interface for ISO 20022 message validation.
 *
 * <p>This interface defines the contract for validating ISO 20022 messages,
 * including XML schema validation, business rule validation, and custom validation rules.</p>
 *
 * <p>The validation pipeline supports:</p>
 * <ul>
 *   <li>XML schema validation against ISO 20022 XSDs</li>
 *   <li>Business rule validation and compliance checks</li>
 *   <li>Custom validation rules and extensions</li>
 *   <li>Validation error reporting and categorization</li>
 *   <li>Performance monitoring and metrics</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * @Autowired
 * private MessageValidator validator;
 *
 * ValidationResult result = validator.validate(message);
 * if (!result.isValid()) {
 *     List<ValidationError> errors = result.getErrors();
 *     // Handle validation errors
 * }
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface MessageValidator {

    /**
     * Validates an ISO 20022 message.
     *
     * <p>This method performs comprehensive validation including XML schema validation,
     * business rule validation, and any configured custom validation rules.</p>
     *
     * @param message the message to validate
     * @return the validation result
     * @throws ValidationException if validation cannot be performed
     */
    ValidationResult validate(BaseMessage message) throws ValidationException;

    /**
     * Validates an ISO 20022 message asynchronously.
     *
     * <p>This method returns immediately with a CompletableFuture that will
     * complete when validation is finished.</p>
     *
     * @param message the message to validate
     * @return a CompletableFuture that will complete with the validation result
     */
    CompletableFuture<ValidationResult> validateAsync(BaseMessage message);

    /**
     * Validates raw XML content.
     *
     * <p>This method validates XML content without requiring a BaseMessage object.
     * It performs XML schema validation and basic structural validation.</p>
     *
     * @param xmlContent the XML content to validate
     * @return the validation result
     * @throws ValidationException if validation cannot be performed
     */
    ValidationResult validateXml(String xmlContent) throws ValidationException;

    /**
     * Validates raw XML content asynchronously.
     *
     * @param xmlContent the XML content to validate
     * @return a CompletableFuture that will complete with the validation result
     */
    CompletableFuture<ValidationResult> validateXmlAsync(String xmlContent);

    /**
     * Gets validation statistics and metrics.
     *
     * @return validation statistics
     */
    ValidationStatistics getStatistics();

    /**
     * Performs a health check on the validator.
     *
     * @return the health check result
     */
    ValidationHealthCheck healthCheck();

    /**
     * Gets the validator identifier.
     *
     * @return the validator identifier
     */
    String getValidatorId();

    /**
     * Gets the validator display name.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Gets the validator version.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Checks if the validator is available.
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();
}
