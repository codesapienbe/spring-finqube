package com.finqube.iso20022.core.template;

import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.finqube.iso20022.core.exception.MessageValidationException;
import com.finqube.iso20022.core.message.BaseMessage;

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
public class Iso20022Template {

    private static final Logger logger = LoggerFactory.getLogger(Iso20022Template.class);

    private final String templateId;

    /**
     * Constructs a new Iso20022Template with a unique identifier.
     */
    public Iso20022Template() {
        this.templateId = "template-" + UUID.randomUUID().toString().substring(0, 8);
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
        Objects.requireNonNull(xml, "XML message cannot be null");

        if (xml.trim().isEmpty()) {
            throw new IllegalArgumentException("XML message cannot be empty");
        }

        String messageId = generateMessageId();
        logger.info("Sending ISO 20022 message with ID: {}", messageId);

        try {
            // TODO: Implement XML validation
            logger.debug("Validating XML message: {}", xml.substring(0, Math.min(100, xml.length())));

            // TODO: Implement transport layer
            logger.info("Message would be sent via transport layer (currently logging only)");

            logger.info("Successfully processed message: {}", messageId);
            return messageId;

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
     * validation and transport pipeline. The message is first validated according
     * to its specific business rules.</p>
     *
     * @param message the ISO 20022 message object
     * @return the unique message identifier assigned to this message
     * @throws MessageValidationException if the message fails validation
     * @throws IllegalArgumentException if the message is null
     */
    public String send(BaseMessage message) throws MessageValidationException {
        Objects.requireNonNull(message, "Message cannot be null");

        String messageId = message.getMessageId();
        if (messageId == null || messageId.trim().isEmpty()) {
            messageId = generateMessageId();
        }

        logger.info("Sending ISO 20022 message: {} (Type: {})", messageId, message.getMessageType());

        try {
            // Validate the message
            if (message.validate()) {
                logger.debug("Message validation passed for: {}", messageId);
            } else {
                throw new MessageValidationException("Message validation failed", messageId, message.getMessageType());
            }

            // TODO: Implement transport layer
            logger.info("Message would be sent via transport layer (currently logging only)");

            logger.info("Successfully processed message: {}", messageId);
            return messageId;

        } catch (MessageValidationException e) {
            logger.error("Message validation failed: {}", messageId, e);
            throw e;
                } catch (Exception e) {
            logger.error("Failed to send message: {}", messageId, e);
            throw new MessageValidationException("Failed to send message: " + e.getMessage(),
                messageId, message.getMessageType());
        }
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
