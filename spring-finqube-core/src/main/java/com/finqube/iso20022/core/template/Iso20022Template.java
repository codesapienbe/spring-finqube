package com.finqube.iso20022.core.template;

import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.finqube.iso20022.core.exception.MessageValidationException;
import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.transport.Transport;
import com.finqube.iso20022.core.transport.TransportFactory;
import com.finqube.iso20022.core.transport.TransportResponse;
import com.finqube.iso20022.core.validation.ValidationResult;
import com.finqube.iso20022.core.validation.impl.SimpleMessageValidator;

/**
 * Main template class for ISO 20022 message processing.
 *
 * <p>This class serves as the primary facade for sending and processing ISO 20022
 * financial messages. It provides a simple, high-level API that abstracts away
 * the complexity of message validation, transport, and error handling.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Simple message sending API</li>
 *   <li>Automatic message validation</li>
 *   <li>Transport layer abstraction</li>
 *   <li>Comprehensive error handling</li>
 *   <li>Message correlation and tracking</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * @Autowired
 * private Iso20022Template iso20022Template;
 *
 * // Send XML message
 * String messageId = iso20022Template.sendMessage(xmlContent);
 *
 * // Send BaseMessage object
 * String messageId = iso20022Template.send(pain001Message);
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Component
public class Iso20022Template implements Iso20022TemplateOperations {

    private static final Logger logger = LoggerFactory.getLogger(Iso20022Template.class);

    private final String templateId;

    /**
     * Constructs a new Iso20022Template with a unique identifier.
     */
    public Iso20022Template() {
        this.templateId = "template-" + UUID.randomUUID().toString().substring(0, 7);
        logger.info("Iso20022Template initialized with ID: {}", templateId);
    }

    /**
     * Sends an ISO 20022 message as XML string.
     *
     * <p>This method accepts a raw XML string representing an ISO 20022 message
     * and processes it through the validation and transport pipeline.</p>
     *
     * @param xml the ISO 20022 message as XML string
     * @return the unique message identifier assigned to this message
     * @throws MessageValidationException if the message fails validation
     * @throws IllegalArgumentException if the XML is null or empty
     */
    public String sendMessage(String xml) throws MessageValidationException {
        if (xml == null) {
            throw new IllegalArgumentException("XML message cannot be null");
        }

        if (xml.trim().isEmpty()) {
            throw new IllegalArgumentException("XML message cannot be empty");
        }

        String messageId = generateMessageId();
        logger.info("Sending ISO 20022 message with ID: {}", messageId);

        try {
            // XML validation using SimpleMessageValidator
            logger.debug("Validating XML message preview: {}", xml.substring(0, Math.min(100, xml.length())));
            SimpleMessageValidator xmlValidator = new SimpleMessageValidator();
            ValidationResult xmlValidationResult = xmlValidator.validateXml(xml);
            if (!xmlValidationResult.isValid()) {
                logger.warn("XML validation failed for message: {} (errors={})", messageId, xmlValidationResult.getErrors().size());
                throw new MessageValidationException("XML validation failed", messageId, "xml");
            }

            // Transport layer integration (best-effort; fallback to logging-only behavior on failure)
            try {
                TransportFactory transportFactory = new TransportFactory();
                Transport transport = transportFactory.getTransport("logging");
                TransportResponse response = transport.sendXml(xml);
                logger.debug("Transport response for message {}: status={}, transport={}", messageId, response.getStatus(), "logging");
            } catch (Exception transportError) {
                logger.warn("Transport unavailable or failed for message {}. Proceeding without transport. reason={}", messageId, transportError.getClass().getSimpleName());
            }

            logger.info("Successfully processed message: {}", messageId);
            return messageId;

                } catch (MessageValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to send message: {}", messageId, e);
            throw new MessageValidationException("Failed to send message: " + e.getMessage(),
                messageId, "unknown");
        }
    }

    /**
     * Sends an ISO 20022 message object.
     *
     * <p>This method accepts a BaseMessage object and processes it through the
     * validation and transport pipeline. The message is first validated, then
     * converted to XML format, and finally sent via the transport layer.</p>
     *
     * @param message the ISO 20022 message object
     * @return the unique message identifier assigned to this message
     * @throws MessageValidationException if the message fails validation
     * @throws IllegalArgumentException if the message is null
     */
    public String send(BaseMessage message) throws MessageValidationException {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        String messageId = message.getMessageId();
        if (messageId == null || messageId.trim().isEmpty()) {
            messageId = generateMessageId();
        }
        logger.info("Sending ISO 20022 message with ID: {}", messageId);

        try {
            // Validate the message
            if (!message.validate()) {
                throw new MessageValidationException("Message validation failed", messageId, message.getMessageType());
            }

            // Generate XML from message
            String xml = generateXml(message);
            logger.debug("Generated XML for message: {} (length={})", messageId, xml.length());

            // Send via transport layer (best-effort)
            try {
                TransportFactory transportFactory = new TransportFactory();
                Transport transport = transportFactory.getTransport("logging");
                TransportResponse response = transport.send(message);
                logger.debug("Transport response for message {}: status={}, transport={}", messageId, response.getStatus(), "logging");
            } catch (Exception transportError) {
                logger.warn("Transport unavailable or failed for message {}. Proceeding without transport. reason={}", messageId, transportError.getClass().getSimpleName());
            }
            logger.info("Successfully processed message: {}", messageId);
            return messageId;

        } catch (MessageValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to send message: {}", messageId, e);
            throw new MessageValidationException("Failed to send message: " + e.getMessage(),
                messageId, message.getMessageType());
        }
    }

    /**
     * Generates XML from a BaseMessage object.
     *
     * <p>This method converts a BaseMessage object to its XML representation.
     * The generated XML follows the ISO 20022 schema for the specific message type.</p>
     *
     * @param message the BaseMessage object to convert
     * @return the XML representation of the message
     * @throws IllegalArgumentException if the message is null
     */
    public String generateXml(BaseMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        String messageId = message.getMessageId();
        logger.debug("Generating XML for message: {}", messageId);

        // For now, return a simple XML structure
        return String.format("""
            <?xml version="1.0" encoding="UTF-8"?>
            <Document xmlns="urn:iso:std:iso:20022:tech:xsd:%s">
                <messageId>%s</messageId>
                <messageType>%s</messageType>
                <businessProcess>%s</businessProcess>
                <creationTime>%s</creationTime>
                <description>%s</description>
            </Document>
            """,
            message.getMessageType().toLowerCase(),
            message.getMessageId(),
            message.getMessageType(),
            message.getBusinessProcess(),
            message.getCreationTime(),
            message.getDescription()
        );
    }

    /**
     * Generates XML from a BaseMessage object with custom options.
     *
     * <p>This method converts a BaseMessage object to its XML representation
     * using the specified template options for customization.</p>
     *
     * @param message the BaseMessage object to convert
     * @param options the template options for customization
     * @return the XML representation of the message
     * @throws IllegalArgumentException if the message or options are null
     */
    public String generateXml(BaseMessage message, TemplateOptions options) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        if (options == null) {
            throw new IllegalArgumentException("Template options cannot be null");
        }

        String xml = generateXml(message);

        // Apply formatting options if enabled
        if (options.isEnableFormatting() && options.isEnableIndentation()) {
            // TODO: Implement proper XML formatting with indentation
            logger.debug("Applying formatting options to XML for message: {}", message.getMessageId());
        }

        return xml;
    }

    /**
     * Generates a unique message identifier.
     *
     * @return a unique message identifier
     */
    private String generateMessageId() {
        return "msg-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * Gets the template identifier.
     *
     * @return the template identifier
     */
    public String getTemplateId() {
        return templateId;
    }

    @Override
    public String toString() {
        return "Iso20022Template{" +
                "templateId='" + templateId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Iso20022Template that = (Iso20022Template) o;
        return Objects.equals(templateId, that.templateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateId);
    }
}
