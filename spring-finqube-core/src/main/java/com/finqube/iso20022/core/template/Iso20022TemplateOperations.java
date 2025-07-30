package com.finqube.iso20022.core.template;

import com.finqube.iso20022.core.exception.MessageValidationException;
import com.finqube.iso20022.core.message.BaseMessage;

/**
 * Interface for ISO 20022 template operations.
 */
public interface Iso20022TemplateOperations {

    /**
     * Sends an ISO 20022 message as XML string.
     *
     * @param xml the ISO 20022 message as XML string
     * @return the unique message identifier assigned to this message
     * @throws MessageValidationException if the message fails validation
     * @throws IllegalArgumentException if the XML is null or empty
     */
    String sendMessage(String xml) throws MessageValidationException;

    /**
     * Sends an ISO 20022 message object.
     *
     * @param message the ISO 20022 message object
     * @return the unique message identifier assigned to this message
     * @throws MessageValidationException if the message fails validation
     * @throws IllegalArgumentException if the message is null
     */
    String send(BaseMessage message) throws MessageValidationException;

    /**
     * Generates XML from a BaseMessage object.
     *
     * @param message the BaseMessage object
     * @return the generated XML string
     */
    String generateXml(BaseMessage message);

    /**
     * Generates XML from a BaseMessage object with options.
     *
     * @param message the BaseMessage object
     * @param options the template options
     * @return the generated XML string
     */
    String generateXml(BaseMessage message, TemplateOptions options);

    /**
     * Gets the template identifier.
     *
     * @return the template identifier
     */
    String getTemplateId();
}
