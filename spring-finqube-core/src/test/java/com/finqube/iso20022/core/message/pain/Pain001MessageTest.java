package com.finqube.iso20022.core.message.pain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.finqube.iso20022.core.exception.MessageValidationException;

/**
 * Unit tests for Pain001Message.
 *
 * <p>This test class validates the functionality of the Pain001Message class,
 * including message creation, validation, and business logic.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@DisplayName("Pain001Message Tests")
class Pain001MessageTest {

    private Pain001Message validMessage;
    private List<Pain001Message.PaymentInstruction> validInstructions;

    @BeforeEach
    void setUp() {
        // Create valid payment instructions
        validInstructions = new ArrayList<>();
        validInstructions.add(new Pain001Message.PaymentInstruction(
            "INSTR001", 1000.00, "EUR", "DE89370400440532013000", "FR1420041010050500013M02606", "Salary payment"));
        validInstructions.add(new Pain001Message.PaymentInstruction(
            "INSTR002", 500.00, "EUR", "DE89370400440532013000", "IT60X0542811101000000123456", "Invoice payment"));

        // Create valid message
        validMessage = new Pain001Message("MSG001", validInstructions, 2, 1500.00);
    }

    @Test
    @DisplayName("Should create valid Pain001Message with correct properties")
    void shouldCreateValidMessage() {
        // Assert basic properties
        assertEquals("MSG001", validMessage.getMessageId());
        assertEquals("pain.001.001.11", validMessage.getMessageType());
        assertEquals("pain", validMessage.getBusinessProcess());
        assertEquals("2019-09-01", validMessage.getSchemaVersion());
        assertEquals("Customer Credit Transfer Initiation", validMessage.getDescription());
        assertTrue(validMessage.requiresAcknowledgment());
        assertEquals(2, validMessage.getNumberOfTransactions());
        assertEquals(1500.00, validMessage.getControlSum(), 0.01);

        // Assert creation time is recent
        LocalDateTime now = LocalDateTime.now();
        assertTrue(validMessage.getCreationTime().isBefore(now.plusSeconds(1)));
        assertTrue(validMessage.getCreationTime().isAfter(now.minusSeconds(1)));
    }

    @Test
    @DisplayName("Should validate successfully for valid message")
    void shouldValidateSuccessfully() {
        // Should not throw exception
        assertDoesNotThrow(() -> validMessage.validate());

        // Should return true
        assertTrue(validMessage.validate());
    }

    @Test
    @DisplayName("Should fail validation when message ID is null")
    void shouldFailValidationWhenMessageIdIsNull() {
        Pain001Message invalidMessage = new Pain001Message(null, validInstructions, 2, 1500.00);

        MessageValidationException exception = assertThrows(MessageValidationException.class,
            () -> invalidMessage.validate());

        assertEquals("Pain001Message validation failed [Message ID: null] [Message Type: pain.001.001.11] [Validation Errors: 1]",
            exception.getMessage());
        assertEquals(1, exception.getValidationErrorCount());
    }

    @Test
    @DisplayName("Should fail validation when message ID is empty")
    void shouldFailValidationWhenMessageIdIsEmpty() {
        Pain001Message invalidMessage = new Pain001Message("", validInstructions, 2, 1500.00);

        MessageValidationException exception = assertThrows(MessageValidationException.class,
            () -> invalidMessage.validate());

        assertEquals(1, exception.getValidationErrorCount());
    }

    @Test
    @DisplayName("Should fail validation when payment instructions are null")
    void shouldFailValidationWhenPaymentInstructionsAreNull() {
        Pain001Message invalidMessage = new Pain001Message("MSG001", null, 1, 1000.00);

        MessageValidationException exception = assertThrows(MessageValidationException.class,
            () -> invalidMessage.validate());

        assertEquals(1, exception.getValidationErrorCount());
    }

    @Test
    @DisplayName("Should fail validation when payment instructions are empty")
    void shouldFailValidationWhenPaymentInstructionsAreEmpty() {
        Pain001Message invalidMessage = new Pain001Message("MSG001", new ArrayList<>(), 1, 1000.00);

        MessageValidationException exception = assertThrows(MessageValidationException.class,
            () -> invalidMessage.validate());

        assertEquals(1, exception.getValidationErrorCount());
    }

    @Test
    @DisplayName("Should fail validation when number of transactions is invalid")
    void shouldFailValidationWhenNumberOfTransactionsIsInvalid() {
        Pain001Message invalidMessage = new Pain001Message("MSG001", validInstructions, 0, 1500.00);

        MessageValidationException exception = assertThrows(MessageValidationException.class,
            () -> invalidMessage.validate());

        assertEquals(1, exception.getValidationErrorCount());
    }

    @Test
    @DisplayName("Should fail validation when control sum is invalid")
    void shouldFailValidationWhenControlSumIsInvalid() {
        Pain001Message invalidMessage = new Pain001Message("MSG001", validInstructions, 2, 0.0);

        MessageValidationException exception = assertThrows(MessageValidationException.class,
            () -> invalidMessage.validate());

        assertEquals(1, exception.getValidationErrorCount());
    }

    @Test
    @DisplayName("Should return immutable list of payment instructions")
    void shouldReturnImmutableListOfPaymentInstructions() {
        List<Pain001Message.PaymentInstruction> instructions = validMessage.getPaymentInstructions();

        // Should not be able to modify the returned list
        assertThrows(UnsupportedOperationException.class, () -> instructions.add(null));

        // Original list should remain unchanged
        assertEquals(2, validMessage.getPaymentInstructions().size());
    }

    @Test
    @DisplayName("Should validate payment instruction successfully")
    void shouldValidatePaymentInstructionSuccessfully() {
        Pain001Message.PaymentInstruction instruction = validInstructions.get(0);

        assertDoesNotThrow(() -> instruction.validate());
    }

    @Test
    @DisplayName("Should fail payment instruction validation when instruction ID is null")
    void shouldFailPaymentInstructionValidationWhenInstructionIdIsNull() {
        Pain001Message.PaymentInstruction invalidInstruction = new Pain001Message.PaymentInstruction(
            null, 1000.00, "EUR", "DE89370400440532013000", "FR1420041010050500013M02606", "Salary payment");

        MessageValidationException exception = assertThrows(MessageValidationException.class,
            () -> invalidInstruction.validate());

        assertEquals("PaymentInstruction validation failed [Message ID: null] [Message Type: pain.001.instruction] [Validation Errors: 1]",
            exception.getMessage());
    }

    @Test
    @DisplayName("Should fail payment instruction validation when amount is invalid")
    void shouldFailPaymentInstructionValidationWhenAmountIsInvalid() {
        Pain001Message.PaymentInstruction invalidInstruction = new Pain001Message.PaymentInstruction(
            "INSTR001", 0.0, "EUR", "DE89370400440532013000", "FR1420041010050500013M02606", "Salary payment");

        MessageValidationException exception = assertThrows(MessageValidationException.class,
            () -> invalidInstruction.validate());

        assertEquals(1, exception.getValidationErrorCount());
    }

    @Test
    @DisplayName("Should fail payment instruction validation when currency is null")
    void shouldFailPaymentInstructionValidationWhenCurrencyIsNull() {
        Pain001Message.PaymentInstruction invalidInstruction = new Pain001Message.PaymentInstruction(
            "INSTR001", 1000.00, null, "DE89370400440532013000", "FR1420041010050500013M02606", "Salary payment");

        MessageValidationException exception = assertThrows(MessageValidationException.class,
            () -> invalidInstruction.validate());

        assertEquals(1, exception.getValidationErrorCount());
    }

    @Test
    @DisplayName("Should fail payment instruction validation when debtor account is null")
    void shouldFailPaymentInstructionValidationWhenDebtorAccountIsNull() {
        Pain001Message.PaymentInstruction invalidInstruction = new Pain001Message.PaymentInstruction(
            "INSTR001", 1000.00, "EUR", null, "FR1420041010050500013M02606", "Salary payment");

        MessageValidationException exception = assertThrows(MessageValidationException.class,
            () -> invalidInstruction.validate());

        assertEquals(1, exception.getValidationErrorCount());
    }

    @Test
    @DisplayName("Should fail payment instruction validation when creditor account is null")
    void shouldFailPaymentInstructionValidationWhenCreditorAccountIsNull() {
        Pain001Message.PaymentInstruction invalidInstruction = new Pain001Message.PaymentInstruction(
            "INSTR001", 1000.00, "EUR", "DE89370400440532013000", null, "Salary payment");

        MessageValidationException exception = assertThrows(MessageValidationException.class,
            () -> invalidInstruction.validate());

        assertEquals(1, exception.getValidationErrorCount());
    }

    @Test
    @DisplayName("Should get payment instruction properties correctly")
    void shouldGetPaymentInstructionPropertiesCorrectly() {
        Pain001Message.PaymentInstruction instruction = validInstructions.get(0);

        assertEquals("INSTR001", instruction.getInstructionId());
        assertEquals(1000.00, instruction.getAmount(), 0.01);
        assertEquals("EUR", instruction.getCurrency());
        assertEquals("DE89370400440532013000", instruction.getDebtorAccount());
        assertEquals("FR1420041010050500013M02606", instruction.getCreditorAccount());
        assertEquals("Salary payment", instruction.getPurpose());
    }
}
