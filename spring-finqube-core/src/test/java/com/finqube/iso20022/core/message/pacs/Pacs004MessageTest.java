package com.finqube.iso20022.core.message.pacs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.finqube.iso20022.core.exception.MessageValidationException;

/**
 * Unit tests for Pacs004Message (Payment Return).
 *
 * <p>Tests cover validation, return charge management, and business logic for
 * ISO 20022 pacs.004 messages.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
@DisplayName("Pacs004Message Tests")
class Pacs004MessageTest {

    private Pacs004Message pacs004Message;
    private static final String ORIGINAL_MESSAGE_ID = "PACS-ORIG-" + UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        pacs004Message = new Pacs004Message(ORIGINAL_MESSAGE_ID,
                                          Pacs004Message.ReturnReason.ACCOUNT_CLOSED,
                                          new BigDecimal("1000.00"), "USD");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should initialize required fields")
        void defaultConstructorShouldInitializeRequiredFields() {
            Pacs004Message message = new Pacs004Message();

            assertNotNull(message.getMessageId());
            assertTrue(message.getMessageId().startsWith("PACS-"));
            assertNotNull(message.getCreationTime());
            assertEquals(Pacs004Message.ReturnStatus.PENDING, message.getReturnStatus());
            assertEquals(BigDecimal.ZERO, message.getReturnAmount());
            assertEquals("USD", message.getReturnCurrency());
        }

        @Test
        @DisplayName("Parameterized constructor should set provided values")
        void parameterizedConstructorShouldSetProvidedValues() {
            String originalMessageId = "PACS-ORIG-123";
            Pacs004Message.ReturnReason returnReason = Pacs004Message.ReturnReason.INSUFFICIENT_FUNDS;
            BigDecimal returnAmount = new BigDecimal("500.00");
            String returnCurrency = "EUR";

            Pacs004Message message = new Pacs004Message(originalMessageId, returnReason, returnAmount, returnCurrency);

            assertEquals(originalMessageId, message.getOriginalMessageId());
            assertEquals(returnReason, message.getReturnReason());
            assertEquals(returnAmount, message.getReturnAmount());
            assertEquals(returnCurrency, message.getReturnCurrency());
            assertNotNull(message.getMessageId());
            assertNotNull(message.getCreationTime());
        }
    }

    @Nested
    @DisplayName("BaseMessage Interface Tests")
    class BaseMessageInterfaceTests {

        @Test
        @DisplayName("Should return correct message type")
        void shouldReturnCorrectMessageType() {
            assertEquals("pacs.004", pacs004Message.getMessageType());
        }

        @Test
        @DisplayName("Should return correct business process")
        void shouldReturnCorrectBusinessProcess() {
            assertEquals("pacs", pacs004Message.getBusinessProcess());
        }

        @Test
        @DisplayName("Should return high priority")
        void shouldReturnHighPriority() {
            assertEquals(com.finqube.iso20022.core.message.MessagePriority.HIGH,
                        pacs004Message.getPriority());
        }

        @Test
        @DisplayName("Should return correct schema version")
        void shouldReturnCorrectSchemaVersion() {
            assertEquals("001.001.11", pacs004Message.getSchemaVersion());
        }

        @Test
        @DisplayName("Should require acknowledgment")
        void shouldRequireAcknowledgment() {
            assertTrue(pacs004Message.requiresAcknowledgment());
        }

        @Test
        @DisplayName("Should return correct description")
        void shouldReturnCorrectDescription() {
            String description = pacs004Message.getDescription();
            assertTrue(description.contains("Payment Return"));
            assertTrue(description.contains(ORIGINAL_MESSAGE_ID));
            assertTrue(description.contains("ACCOUNT_CLOSED"));
            assertTrue(description.contains("1000.00"));
            assertTrue(description.contains("USD"));
        }

        @Test
        @DisplayName("Should return original message ID in transactions list")
        void shouldReturnOriginalMessageIdInTransactionsList() {
            var transactions = pacs004Message.getTransactions();
            assertEquals(1, transactions.size());
            assertEquals(ORIGINAL_MESSAGE_ID, transactions.get(0));
        }

        @Test
        @DisplayName("Should return correct transaction count")
        void shouldReturnCorrectTransactionCount() {
            assertEquals(1, pacs004Message.getTransactionCount());
        }

        @Test
        @DisplayName("Should return return amount as total amount")
        void shouldReturnReturnAmountAsTotalAmount() {
            assertEquals(1000.0, pacs004Message.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid message should pass validation")
        void validMessageShouldPassValidation() {
            assertTrue(pacs004Message.validate());
        }

        @Test
        @DisplayName("Should throw exception when message ID is null")
        void shouldThrowExceptionWhenMessageIdIsNull() {
            pacs004Message.setMessageId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs004Message.validate()
            );

            assertEquals("Message ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when original message ID is null")
        void shouldThrowExceptionWhenOriginalMessageIdIsNull() {
            pacs004Message.setOriginalMessageId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs004Message.validate()
            );

            assertEquals("Original message ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when return reason is null")
        void shouldThrowExceptionWhenReturnReasonIsNull() {
            pacs004Message.setReturnReason(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs004Message.validate()
            );

            assertEquals("Return reason is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when return status is null")
        void shouldThrowExceptionWhenReturnStatusIsNull() {
            pacs004Message.setReturnStatus(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs004Message.validate()
            );

            assertEquals("Return status is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when return amount is null")
        void shouldThrowExceptionWhenReturnAmountIsNull() {
            pacs004Message.setReturnAmount(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs004Message.validate()
            );

            assertEquals("Return amount is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when return currency is null")
        void shouldThrowExceptionWhenReturnCurrencyIsNull() {
            pacs004Message.setReturnCurrency(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs004Message.validate()
            );

            assertEquals("Return currency is required", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Return Status Management Tests")
    class ReturnStatusManagementTests {

        @Test
        @DisplayName("Should update status and set processing date time")
        void shouldUpdateStatusAndSetProcessingDateTime() {
            LocalDateTime beforeUpdate = LocalDateTime.now();

            pacs004Message.setReturnStatus(Pacs004Message.ReturnStatus.PROCESSED);

            assertEquals(Pacs004Message.ReturnStatus.PROCESSED, pacs004Message.getReturnStatus());
            assertNotNull(pacs004Message.getProcessingDateTime());
            assertTrue(pacs004Message.getProcessingDateTime().isAfter(beforeUpdate) ||
                      pacs004Message.getProcessingDateTime().equals(beforeUpdate));
        }

        @Test
        @DisplayName("Should correctly identify pending status")
        void shouldCorrectlyIdentifyPendingStatus() {
            pacs004Message.setReturnStatus(Pacs004Message.ReturnStatus.PENDING);
            assertTrue(pacs004Message.isPending());
            assertFalse(pacs004Message.isProcessed());
            assertFalse(pacs004Message.isCompleted());
            assertFalse(pacs004Message.isRejected());
        }

        @Test
        @DisplayName("Should correctly identify processed status")
        void shouldCorrectlyIdentifyProcessedStatus() {
            pacs004Message.setReturnStatus(Pacs004Message.ReturnStatus.PROCESSED);
            assertTrue(pacs004Message.isProcessed());
            assertFalse(pacs004Message.isPending());
            assertFalse(pacs004Message.isCompleted());
            assertFalse(pacs004Message.isRejected());
        }

        @Test
        @DisplayName("Should correctly identify completed status")
        void shouldCorrectlyIdentifyCompletedStatus() {
            pacs004Message.setReturnStatus(Pacs004Message.ReturnStatus.COMPLETED);
            assertTrue(pacs004Message.isCompleted());
            assertFalse(pacs004Message.isPending());
            assertFalse(pacs004Message.isProcessed());
            assertFalse(pacs004Message.isRejected());
        }

        @Test
        @DisplayName("Should correctly identify rejected status")
        void shouldCorrectlyIdentifyRejectedStatus() {
            pacs004Message.setReturnStatus(Pacs004Message.ReturnStatus.REJECTED);
            assertTrue(pacs004Message.isRejected());
            assertFalse(pacs004Message.isPending());
            assertFalse(pacs004Message.isProcessed());
            assertFalse(pacs004Message.isCompleted());
        }
    }

    @Nested
    @DisplayName("Return Charge Management Tests")
    class ReturnChargeManagementTests {

        @Test
        @DisplayName("Should add return charge")
        void shouldAddReturnCharge() {
            pacs004Message.addReturnCharge("PROCESSING_FEE", new BigDecimal("25.00"), "USD", "Processing fee for return");

            var charges = pacs004Message.getReturnCharges();
            assertEquals(1, charges.size());

            var charge = charges.get(0);
            assertEquals("PROCESSING_FEE", charge.getChargeType());
            assertEquals(new BigDecimal("25.00"), charge.getAmount());
            assertEquals("USD", charge.getCurrency());
            assertEquals("Processing fee for return", charge.getDescription());
        }

        @Test
        @DisplayName("Should calculate total charges")
        void shouldCalculateTotalCharges() {
            pacs004Message.addReturnCharge("PROCESSING_FEE", new BigDecimal("25.00"), "USD", "Processing fee");
            pacs004Message.addReturnCharge("ADMIN_FEE", new BigDecimal("15.00"), "USD", "Administrative fee");

            assertEquals(new BigDecimal("40.00"), pacs004Message.getTotalCharges());
        }

        @Test
        @DisplayName("Should calculate net return amount")
        void shouldCalculateNetReturnAmount() {
            pacs004Message.addReturnCharge("PROCESSING_FEE", new BigDecimal("25.00"), "USD", "Processing fee");
            pacs004Message.addReturnCharge("ADMIN_FEE", new BigDecimal("15.00"), "USD", "Administrative fee");

            // Return amount is 1000.00, charges are 40.00, net should be 960.00
            assertEquals(new BigDecimal("960.00"), pacs004Message.getNetReturnAmount());
        }

        @Test
        @DisplayName("Should detect presence of charges")
        void shouldDetectPresenceOfCharges() {
            assertFalse(pacs004Message.hasCharges());

            pacs004Message.addReturnCharge("PROCESSING_FEE", new BigDecimal("25.00"), "USD", "Processing fee");
            assertTrue(pacs004Message.hasCharges());
        }

        @Test
        @DisplayName("Should count return charges")
        void shouldCountReturnCharges() {
            assertEquals(0, pacs004Message.getChargeCount());

            pacs004Message.addReturnCharge("PROCESSING_FEE", new BigDecimal("25.00"), "USD", "Processing fee");
            pacs004Message.addReturnCharge("ADMIN_FEE", new BigDecimal("15.00"), "USD", "Administrative fee");

            assertEquals(2, pacs004Message.getChargeCount());
        }

        @Test
        @DisplayName("Should return immutable charge list")
        void shouldReturnImmutableChargeList() {
            var charges = pacs004Message.getReturnCharges();

            assertThrows(UnsupportedOperationException.class, () -> {
                charges.add(null);
            });
        }
    }

    @Nested
    @DisplayName("Enum Tests")
    class EnumTests {

        @Test
        @DisplayName("Should get return reason code")
        void shouldGetReturnReasonCode() {
            assertEquals("AC01", Pacs004Message.ReturnReason.ACCOUNT_CLOSED.getCode());
            assertEquals("AM04", Pacs004Message.ReturnReason.INSUFFICIENT_FUNDS.getCode());
            assertEquals("DUPL", Pacs004Message.ReturnReason.DUPLICATE_PAYMENT.getCode());
            assertEquals("AG01", Pacs004Message.ReturnReason.UNAUTHORIZED.getCode());
        }

        @Test
        @DisplayName("Should get return status code")
        void shouldGetReturnStatusCode() {
            assertEquals("PEND", Pacs004Message.ReturnStatus.PENDING.getCode());
            assertEquals("PROC", Pacs004Message.ReturnStatus.PROCESSED.getCode());
            assertEquals("REJT", Pacs004Message.ReturnStatus.REJECTED.getCode());
            assertEquals("CANC", Pacs004Message.ReturnStatus.CANCELLED.getCode());
            assertEquals("COMP", Pacs004Message.ReturnStatus.COMPLETED.getCode());
        }
    }

    @Nested
    @DisplayName("Setter and Getter Tests")
    class SetterAndGetterTests {

        @Test
        @DisplayName("Should set and get return description")
        void shouldSetAndGetReturnDescription() {
            String description = "Payment returned due to account closure";
            pacs004Message.setReturnDescription(description);
            assertEquals(description, pacs004Message.getReturnDescription());
        }

        @Test
        @DisplayName("Should set and get return date time")
        void shouldSetAndGetReturnDateTime() {
            LocalDateTime returnTime = LocalDateTime.now();
            pacs004Message.setReturnDateTime(returnTime);
            assertEquals(returnTime, pacs004Message.getReturnDateTime());
        }

        @Test
        @DisplayName("Should set and get processing date time")
        void shouldSetAndGetProcessingDateTime() {
            LocalDateTime processingTime = LocalDateTime.now();
            pacs004Message.setProcessingDateTime(processingTime);
            assertEquals(processingTime, pacs004Message.getProcessingDateTime());
        }

        @Test
        @DisplayName("Should set and get originator ID")
        void shouldSetAndGetOriginatorId() {
            String originatorId = "ORIG-001";
            pacs004Message.setOriginatorId(originatorId);
            assertEquals(originatorId, pacs004Message.getOriginatorId());
        }

        @Test
        @DisplayName("Should set and get debtor ID")
        void shouldSetAndGetDebtorId() {
            String debtorId = "DEBTOR-001";
            pacs004Message.setDebtorId(debtorId);
            assertEquals(debtorId, pacs004Message.getDebtorId());
        }

        @Test
        @DisplayName("Should set and get creditor ID")
        void shouldSetAndGetCreditorId() {
            String creditorId = "CREDITOR-001";
            pacs004Message.setCreditorId(creditorId);
            assertEquals(creditorId, pacs004Message.getCreditorId());
        }

        @Test
        @DisplayName("Should set and get clearing system ID")
        void shouldSetAndGetClearingSystemId() {
            String clearingSystemId = "CLEARING-001";
            pacs004Message.setClearingSystemId(clearingSystemId);
            assertEquals(clearingSystemId, pacs004Message.getClearingSystemId());
        }

        @Test
        @DisplayName("Should set and get settlement system ID")
        void shouldSetAndGetSettlementSystemId() {
            String settlementSystemId = "SETTLEMENT-001";
            pacs004Message.setSettlementSystemId(settlementSystemId);
            assertEquals(settlementSystemId, pacs004Message.getSettlementSystemId());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(pacs004Message, pacs004Message);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, pacs004Message);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", pacs004Message);
        }

        @Test
        @DisplayName("Should not be equal to message with different creation time")
        void shouldNotBeEqualToMessageWithDifferentCreationTime() {
            Pacs004Message other = new Pacs004Message(ORIGINAL_MESSAGE_ID,
                                                    Pacs004Message.ReturnReason.ACCOUNT_CLOSED,
                                                    new BigDecimal("1000.00"), "USD");

            // Should be different due to different creation times
            assertNotEquals(pacs004Message, other);
            assertNotEquals(pacs004Message.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to message with different message ID")
        void shouldNotBeEqualToMessageWithDifferentMessageId() {
            Pacs004Message other = new Pacs004Message(ORIGINAL_MESSAGE_ID,
                                                    Pacs004Message.ReturnReason.ACCOUNT_CLOSED,
                                                    new BigDecimal("1000.00"), "USD");
            other.setMessageId("DIFFERENT-ID");

            assertNotEquals(pacs004Message, other);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void shouldIncludeKeyFieldsInToString() {
            String toString = pacs004Message.toString();

            assertTrue(toString.contains("Pacs004Message"));
            assertTrue(toString.contains(pacs004Message.getMessageId()));
            assertTrue(toString.contains(ORIGINAL_MESSAGE_ID));
            assertTrue(toString.contains("ACCOUNT_CLOSED"));
            assertTrue(toString.contains("PENDING"));
        }

        @Test
        @DisplayName("Should include charge count in toString")
        void shouldIncludeChargeCountInToString() {
            pacs004Message.addReturnCharge("PROCESSING_FEE", new BigDecimal("25.00"), "USD", "Processing fee");
            String toString = pacs004Message.toString();

            assertTrue(toString.contains("chargeCount=1"));
        }

        @Test
        @DisplayName("Should include return amount and currency in toString")
        void shouldIncludeReturnAmountAndCurrencyInToString() {
            String toString = pacs004Message.toString();

            assertTrue(toString.contains("returnAmount="));
            assertTrue(toString.contains("returnCurrency='USD'"));
        }
    }
}
