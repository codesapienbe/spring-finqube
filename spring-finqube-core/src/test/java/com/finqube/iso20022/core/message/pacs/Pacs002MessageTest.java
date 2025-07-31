package com.finqube.iso20022.core.message.pacs;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.finqube.iso20022.core.exception.MessageValidationException;

/**
 * Unit tests for Pacs002Message (Payment Clearing and Settlement Status Report).
 *
 * <p>Tests cover validation, status management, and business logic for
 * ISO 20022 pacs.002 messages.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
@DisplayName("Pacs002Message Tests")
class Pacs002MessageTest {

    private Pacs002Message pacs002Message;
    private static final String ORIGINAL_MESSAGE_ID = "PACS-ORIG-" + UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        pacs002Message = new Pacs002Message(ORIGINAL_MESSAGE_ID, Pacs002Message.PaymentStatus.PENDING_SETTLEMENT);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should initialize required fields")
        void defaultConstructorShouldInitializeRequiredFields() {
            Pacs002Message message = new Pacs002Message();

            assertNotNull(message.getMessageId());
            assertTrue(message.getMessageId().startsWith("PACS-"));
            assertNotNull(message.getCreationTime());
            assertEquals(Pacs002Message.PaymentStatus.PENDING_SETTLEMENT, message.getStatus());
        }

        @Test
        @DisplayName("Parameterized constructor should set provided values")
        void parameterizedConstructorShouldSetProvidedValues() {
            String originalId = "TEST-PACS-ORIG-123";
            Pacs002Message.PaymentStatus status = Pacs002Message.PaymentStatus.SETTLED;

            Pacs002Message message = new Pacs002Message(originalId, status);

            assertEquals(originalId, message.getOriginalMessageId());
            assertEquals(status, message.getStatus());
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
            assertEquals("pacs.002", pacs002Message.getMessageType());
        }

        @Test
        @DisplayName("Should return correct business process")
        void shouldReturnCorrectBusinessProcess() {
            assertEquals("pacs", pacs002Message.getBusinessProcess());
        }

        @Test
        @DisplayName("Should return high priority")
        void shouldReturnHighPriority() {
            assertEquals(com.finqube.iso20022.core.message.MessagePriority.HIGH,
                        pacs002Message.getPriority());
        }

        @Test
        @DisplayName("Should return correct schema version")
        void shouldReturnCorrectSchemaVersion() {
            assertEquals("001.001.11", pacs002Message.getSchemaVersion());
        }

        @Test
        @DisplayName("Should require acknowledgment")
        void shouldRequireAcknowledgment() {
            assertTrue(pacs002Message.requiresAcknowledgment());
        }

        @Test
        @DisplayName("Should return correct description")
        void shouldReturnCorrectDescription() {
            String description = pacs002Message.getDescription();
            assertTrue(description.contains("Payment Clearing and Settlement Status Report"));
            assertTrue(description.contains(ORIGINAL_MESSAGE_ID));
            assertTrue(description.contains("PENDING_SETTLEMENT"));
        }

        @Test
        @DisplayName("Should return original message ID in transactions list")
        void shouldReturnOriginalMessageIdInTransactionsList() {
            var transactions = pacs002Message.getTransactions();
            assertEquals(1, transactions.size());
            assertEquals(ORIGINAL_MESSAGE_ID, transactions.get(0));
        }

        @Test
        @DisplayName("Should return correct transaction count")
        void shouldReturnCorrectTransactionCount() {
            assertEquals(1, pacs002Message.getTransactionCount());
        }

        @Test
        @DisplayName("Should return zero total amount")
        void shouldReturnZeroTotalAmount() {
            assertEquals(0.0, pacs002Message.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid message should pass validation")
        void validMessageShouldPassValidation() {
            assertTrue(pacs002Message.validate());
        }

        @Test
        @DisplayName("Should throw exception when message ID is null")
        void shouldThrowExceptionWhenMessageIdIsNull() {
            pacs002Message.setMessageId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs002Message.validate()
            );

            assertEquals("Message ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when original message ID is null")
        void shouldThrowExceptionWhenOriginalMessageIdIsNull() {
            pacs002Message.setOriginalMessageId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs002Message.validate()
            );

            assertEquals("Original message ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when status is null")
        void shouldThrowExceptionWhenStatusIsNull() {
            pacs002Message.setStatus(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs002Message.validate()
            );

            assertEquals("Payment status is required", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Status Management Tests")
    class StatusManagementTests {

        @Test
        @DisplayName("Should update status and set status date time")
        void shouldUpdateStatusAndSetStatusDateTime() {
            LocalDateTime beforeUpdate = LocalDateTime.now();

            pacs002Message.setStatus(Pacs002Message.PaymentStatus.SETTLED);

            assertEquals(Pacs002Message.PaymentStatus.SETTLED, pacs002Message.getStatus());
            assertNotNull(pacs002Message.getStatusDateTime());
            assertTrue(pacs002Message.getStatusDateTime().isAfter(beforeUpdate) ||
                      pacs002Message.getStatusDateTime().equals(beforeUpdate));
        }

        @Test
        @DisplayName("Should correctly identify rejected status")
        void shouldCorrectlyIdentifyRejectedStatus() {
            pacs002Message.setStatus(Pacs002Message.PaymentStatus.REJECTED);
            assertTrue(pacs002Message.isRejected());
            assertFalse(pacs002Message.isSettled());
            assertFalse(pacs002Message.isPendingSettlement());
        }

        @Test
        @DisplayName("Should correctly identify settled status")
        void shouldCorrectlyIdentifySettledStatus() {
            pacs002Message.setStatus(Pacs002Message.PaymentStatus.SETTLED);
            assertTrue(pacs002Message.isSettled());
            assertFalse(pacs002Message.isRejected());
            assertFalse(pacs002Message.isPendingSettlement());
        }

        @Test
        @DisplayName("Should correctly identify pending settlement status")
        void shouldCorrectlyIdentifyPendingSettlementStatus() {
            pacs002Message.setStatus(Pacs002Message.PaymentStatus.PENDING_SETTLEMENT);
            assertTrue(pacs002Message.isPendingSettlement());
            assertFalse(pacs002Message.isRejected());
            assertFalse(pacs002Message.isSettled());
        }

        @Test
        @DisplayName("Should correctly identify in clearing status")
        void shouldCorrectlyIdentifyInClearingStatus() {
            pacs002Message.setStatus(Pacs002Message.PaymentStatus.IN_CLEARING);
            assertTrue(pacs002Message.isInClearing());
            assertFalse(pacs002Message.isRejected());
            assertFalse(pacs002Message.isSettled());
        }
    }

    @Nested
    @DisplayName("Settlement Method Tests")
    class SettlementMethodTests {

        @Test
        @DisplayName("Should set and get settlement method")
        void shouldSetAndGetSettlementMethod() {
            Pacs002Message.SettlementMethod method = Pacs002Message.SettlementMethod.CLEARING;

            pacs002Message.setSettlementMethod(method);

            assertEquals(method, pacs002Message.getSettlementMethod());
        }

        @Test
        @DisplayName("Should get settlement method code")
        void shouldGetSettlementMethodCode() {
            Pacs002Message.SettlementMethod method = Pacs002Message.SettlementMethod.RTGS;
            assertEquals("RTGS", method.getCode());
        }

        @Test
        @DisplayName("Should handle null settlement method")
        void shouldHandleNullSettlementMethod() {
            pacs002Message.setSettlementMethod(null);
            assertNull(pacs002Message.getSettlementMethod());
        }
    }

    @Nested
    @DisplayName("Rejection Reason Tests")
    class RejectionReasonTests {

        @Test
        @DisplayName("Should set and get rejection reason")
        void shouldSetAndGetRejectionReason() {
            Pacs002Message.RejectionReason reason = Pacs002Message.RejectionReason.CLEARING_ERROR;

            pacs002Message.setRejectionReason(reason);

            assertEquals(reason, pacs002Message.getRejectionReason());
        }

        @Test
        @DisplayName("Should get rejection reason code")
        void shouldGetRejectionReasonCode() {
            Pacs002Message.RejectionReason reason = Pacs002Message.RejectionReason.SETTLEMENT_ERROR;
            assertEquals("STLM", reason.getCode());
        }

        @Test
        @DisplayName("Should handle null rejection reason")
        void shouldHandleNullRejectionReason() {
            pacs002Message.setRejectionReason(null);
            assertNull(pacs002Message.getRejectionReason());
        }
    }

    @Nested
    @DisplayName("Setter and Getter Tests")
    class SetterAndGetterTests {

        @Test
        @DisplayName("Should set and get settlement date time")
        void shouldSetAndGetSettlementDateTime() {
            LocalDateTime settlementTime = LocalDateTime.now();
            pacs002Message.setSettlementDateTime(settlementTime);
            assertEquals(settlementTime, pacs002Message.getSettlementDateTime());
        }

        @Test
        @DisplayName("Should set and get clearing system ID")
        void shouldSetAndGetClearingSystemId() {
            String clearingSystemId = "CLEARING-SYS-001";
            pacs002Message.setClearingSystemId(clearingSystemId);
            assertEquals(clearingSystemId, pacs002Message.getClearingSystemId());
        }

        @Test
        @DisplayName("Should set and get settlement system ID")
        void shouldSetAndGetSettlementSystemId() {
            String settlementSystemId = "SETTLEMENT-SYS-001";
            pacs002Message.setSettlementSystemId(settlementSystemId);
            assertEquals(settlementSystemId, pacs002Message.getSettlementSystemId());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(pacs002Message, pacs002Message);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, pacs002Message);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", pacs002Message);
        }

        @Test
        @DisplayName("Should not be equal to message with different creation time")
        void shouldNotBeEqualToMessageWithDifferentCreationTime() {
            Pacs002Message other = new Pacs002Message(ORIGINAL_MESSAGE_ID, Pacs002Message.PaymentStatus.PENDING_SETTLEMENT);

            // Should be different due to different creation times
            assertNotEquals(pacs002Message, other);
            assertNotEquals(pacs002Message.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to message with different message ID")
        void shouldNotBeEqualToMessageWithDifferentMessageId() {
            Pacs002Message other = new Pacs002Message(ORIGINAL_MESSAGE_ID, Pacs002Message.PaymentStatus.PENDING_SETTLEMENT);
            other.setMessageId("DIFFERENT-ID");

            assertNotEquals(pacs002Message, other);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void shouldIncludeKeyFieldsInToString() {
            String toString = pacs002Message.toString();

            assertTrue(toString.contains("Pacs002Message"));
            assertTrue(toString.contains(pacs002Message.getMessageId()));
            assertTrue(toString.contains(ORIGINAL_MESSAGE_ID));
            assertTrue(toString.contains("PENDING_SETTLEMENT"));
        }

        @Test
        @DisplayName("Should include settlement method in toString when present")
        void shouldIncludeSettlementMethodInToStringWhenPresent() {
            pacs002Message.setSettlementMethod(Pacs002Message.SettlementMethod.RTGS);
            String toString = pacs002Message.toString();

            assertTrue(toString.contains("RTGS"));
        }

        @Test
        @DisplayName("Should include rejection reason in toString when present")
        void shouldIncludeRejectionReasonInToStringWhenPresent() {
            pacs002Message.setRejectionReason(Pacs002Message.RejectionReason.CLEARING_ERROR);
            String toString = pacs002Message.toString();

            assertTrue(toString.contains("CLEARING_ERROR"));
        }
    }
}
