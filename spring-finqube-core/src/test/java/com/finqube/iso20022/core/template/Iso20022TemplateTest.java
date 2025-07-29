package com.finqube.iso20022.core.template;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.finqube.iso20022.core.exception.MessageValidationException;
import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.pain.Pain001Message;

/**
 * Unit tests for Iso20022Template.
 *
 * <p>This test class validates the functionality of the Iso20022Template class,
 * including message sending, validation, and error handling.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@DisplayName("Iso20022Template Tests")
class Iso20022TemplateTest {

    private Iso20022Template template;

    @BeforeEach
    void setUp() {
        template = new Iso20022Template();
    }

    @Test
    @DisplayName("Should create template with unique ID")
    void shouldCreateTemplateWithUniqueId() {
        // Assert template has an ID
        assertNotNull(template.getTemplateId());
        assertTrue(template.getTemplateId().startsWith("template-"));
        assertEquals(16, template.getTemplateId().length()); // "template-" + 8 chars
    }

    @Test
    @DisplayName("Should send XML message successfully")
    void shouldSendXmlMessageSuccessfully() throws MessageValidationException {
        // Given
        String xmlMessage = "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.11\">" +
                "<CstmrCdtTrfInitn>" +
                "<GrpHdr>" +
                "<MsgId>MSG001</MsgId>" +
                "<CreDtTm>2024-12-19T10:00:00</CreDtTm>" +
                "<NbOfTxs>1</NbOfTxs>" +
                "<CtrlSum>1000.00</CtrlSum>" +
                "</GrpHdr>" +
                "</CstmrCdtTrfInitn>" +
                "</Document>";

        // When
        String messageId = template.sendMessage(xmlMessage);

        // Then
        assertNotNull(messageId);
        assertTrue(messageId.startsWith("msg-"));
        assertEquals(20, messageId.length()); // "msg-" + 16 chars
    }

    @Test
    @DisplayName("Should send BaseMessage successfully")
    void shouldSendBaseMessageSuccessfully() throws MessageValidationException {
        // Given
        List<Pain001Message.PaymentInstruction> instructions = new ArrayList<>();
        instructions.add(new Pain001Message.PaymentInstruction(
            "INSTR001", 1000.00, "EUR", "DE89370400440532013000", "FR1420041010050500013M02606", "Test payment"));

        Pain001Message message = new Pain001Message("MSG001", instructions, 1, 1000.00);

        // When
        String messageId = template.send(message);

        // Then
        assertNotNull(messageId);
        assertEquals("MSG001", messageId); // Should use the message's own ID
    }

    @Test
    @DisplayName("Should generate message ID for BaseMessage without ID")
    void shouldGenerateMessageIdForBaseMessageWithoutId() throws MessageValidationException {
        // Given
        List<Pain001Message.PaymentInstruction> instructions = new ArrayList<>();
        instructions.add(new Pain001Message.PaymentInstruction(
            "INSTR001", 1000.00, "EUR", "DE89370400440532013000", "FR1420041010050500013M02606", "Test payment"));

        Pain001Message message = new Pain001Message("", instructions, 1, 1000.00);

        // When
        String messageId = template.send(message);

        // Then
        assertNotNull(messageId);
        assertTrue(messageId.startsWith("msg-"));
        assertEquals(20, messageId.length());
    }

    @Test
    @DisplayName("Should throw exception when XML is null")
    void shouldThrowExceptionWhenXmlIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> template.sendMessage(null));
    }

    @Test
    @DisplayName("Should throw exception when XML is empty")
    void shouldThrowExceptionWhenXmlIsEmpty() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> template.sendMessage(""));
    }

    @Test
    @DisplayName("Should throw exception when XML is whitespace only")
    void shouldThrowExceptionWhenXmlIsWhitespaceOnly() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> template.sendMessage("   "));
    }

    @Test
    @DisplayName("Should throw exception when BaseMessage is null")
    void shouldThrowExceptionWhenBaseMessageIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> template.send((BaseMessage) null));
    }

    @Test
    @DisplayName("Should have correct toString representation")
    void shouldHaveCorrectToStringRepresentation() {
        // When
        String toString = template.toString();

        // Then
        assertTrue(toString.startsWith("Iso20022Template{"));
        assertTrue(toString.contains("templateId='"));
        assertTrue(toString.endsWith("}"));
    }

    @Test
    @DisplayName("Should have correct equals and hashCode")
    void shouldHaveCorrectEqualsAndHashCode() {
        // Given
        Iso20022Template template1 = new Iso20022Template();
        Iso20022Template template2 = new Iso20022Template();

        // When & Then
        assertNotEquals(template1, template2); // Different IDs
        assertNotEquals(template1.hashCode(), template2.hashCode());

        assertEquals(template1, template1); // Same instance
        assertEquals(template1.hashCode(), template1.hashCode());

        assertNotEquals(template1, null); // Not equal to null
        assertNotEquals(template1, "string"); // Not equal to different type
    }

    @Test
    @DisplayName("Should handle BaseMessage validation failure")
    void shouldHandleBaseMessageValidationFailure() {
        // Given - Create an invalid message (empty instructions)
        List<Pain001Message.PaymentInstruction> emptyInstructions = new ArrayList<>();
        Pain001Message invalidMessage = new Pain001Message("MSG001", emptyInstructions, 0, 0.0);

        // When & Then
        assertThrows(MessageValidationException.class, () -> template.send(invalidMessage));
    }

    @Test
    @DisplayName("Should generate different message IDs for different calls")
    void shouldGenerateDifferentMessageIdsForDifferentCalls() throws MessageValidationException {
        // Given
        String xml1 = "<Document><Test>Message 1</Test></Document>";
        String xml2 = "<Document><Test>Message 2</Test></Document>";

        // When
        String messageId1 = template.sendMessage(xml1);
        String messageId2 = template.sendMessage(xml2);

        // Then
        assertNotNull(messageId1);
        assertNotNull(messageId2);
        assertNotEquals(messageId1, messageId2);
    }
}
