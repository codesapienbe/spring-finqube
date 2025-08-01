package com.finqube.iso20022.core.message.pacs;

import java.math.BigDecimal;
import java.time.LocalDate;
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
 * Unit tests for Pacs010Message (Direct Debit Mandate).
 *
 * <p>Tests cover validation, mandate management, and business logic for
 * ISO 20022 pacs.010 messages.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
@DisplayName("Pacs010Message Tests")
class Pacs010MessageTest {

    private Pacs010Message pacs010Message;
    private static final String MANDATE_ID = "MANDATE-" + UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        pacs010Message = new Pacs010Message(MANDATE_ID,
                                          Pacs010Message.MandateType.RECURRING,
                                          "DEBTOR-001", "CREDITOR-001");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should initialize required fields")
        void defaultConstructorShouldInitializeRequiredFields() {
            Pacs010Message message = new Pacs010Message();

            assertNotNull(message.getMessageId());
            assertTrue(message.getMessageId().startsWith("PACS-"));
            assertNotNull(message.getCreationTime());
            assertEquals(Pacs010Message.MandateType.RECURRING, message.getMandateType());
            assertEquals(Pacs010Message.MandateStatus.PENDING, message.getMandateStatus());
            assertEquals("USD", message.getCurrency());
        }

        @Test
        @DisplayName("Parameterized constructor should set provided values")
        void parameterizedConstructorShouldSetProvidedValues() {
            String mandateId = "MANDATE-123";
            Pacs010Message.MandateType mandateType = Pacs010Message.MandateType.ONE_OFF;
            String debtorId = "DEBTOR-002";
            String creditorId = "CREDITOR-002";

            Pacs010Message message = new Pacs010Message(mandateId, mandateType, debtorId, creditorId);

            assertEquals(mandateId, message.getMandateId());
            assertEquals(mandateType, message.getMandateType());
            assertEquals(debtorId, message.getDebtorId());
            assertEquals(creditorId, message.getCreditorId());
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
            assertEquals("pacs.010", pacs010Message.getMessageType());
        }

        @Test
        @DisplayName("Should return correct business process")
        void shouldReturnCorrectBusinessProcess() {
            assertEquals("pacs", pacs010Message.getBusinessProcess());
        }

        @Test
        @DisplayName("Should return normal priority")
        void shouldReturnNormalPriority() {
            assertEquals(com.finqube.iso20022.core.message.MessagePriority.NORMAL,
                        pacs010Message.getPriority());
        }

        @Test
        @DisplayName("Should return correct schema version")
        void shouldReturnCorrectSchemaVersion() {
            assertEquals("001.001.11", pacs010Message.getSchemaVersion());
        }

        @Test
        @DisplayName("Should require acknowledgment")
        void shouldRequireAcknowledgment() {
            assertTrue(pacs010Message.requiresAcknowledgment());
        }

        @Test
        @DisplayName("Should return correct description")
        void shouldReturnCorrectDescription() {
            String description = pacs010Message.getDescription();
            assertTrue(description.contains("Direct Debit Mandate"));
            assertTrue(description.contains(MANDATE_ID));
            assertTrue(description.contains("RECURRING"));
            assertTrue(description.contains("PENDING"));
        }

        @Test
        @DisplayName("Should return mandate ID in transactions list")
        void shouldReturnMandateIdInTransactionsList() {
            var transactions = pacs010Message.getTransactions();
            assertEquals(1, transactions.size());
            assertEquals(MANDATE_ID, transactions.get(0));
        }

        @Test
        @DisplayName("Should return correct transaction count")
        void shouldReturnCorrectTransactionCount() {
            assertEquals(1, pacs010Message.getTransactionCount());
        }

        @Test
        @DisplayName("Should return max amount as total amount")
        void shouldReturnMaxAmountAsTotalAmount() {
            pacs010Message.setMaxAmount(new BigDecimal("1000.00"));
            assertEquals(1000.0, pacs010Message.getTotalAmount());
        }

        @Test
        @DisplayName("Should return zero when max amount is null")
        void shouldReturnZeroWhenMaxAmountIsNull() {
            assertEquals(0.0, pacs010Message.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid message should pass validation")
        void validMessageShouldPassValidation() {
            assertTrue(pacs010Message.validate());
        }

        @Test
        @DisplayName("Should throw exception when message ID is null")
        void shouldThrowExceptionWhenMessageIdIsNull() {
            pacs010Message.setMessageId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs010Message.validate()
            );

            assertEquals("Message ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when mandate ID is null")
        void shouldThrowExceptionWhenMandateIdIsNull() {
            pacs010Message.setMandateId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs010Message.validate()
            );

            assertEquals("Mandate ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when mandate type is null")
        void shouldThrowExceptionWhenMandateTypeIsNull() {
            pacs010Message.setMandateType(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs010Message.validate()
            );

            assertEquals("Mandate type is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when mandate status is null")
        void shouldThrowExceptionWhenMandateStatusIsNull() {
            pacs010Message.setMandateStatus(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs010Message.validate()
            );

            assertEquals("Mandate status is required", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Mandate Status Tests")
    class MandateStatusTests {

        @Test
        @DisplayName("Should correctly identify active mandate")
        void shouldCorrectlyIdentifyActiveMandate() {
            pacs010Message.setMandateStatus(Pacs010Message.MandateStatus.ACTIVE);
            assertTrue(pacs010Message.isActive());
            assertFalse(pacs010Message.isSuspended());
            assertFalse(pacs010Message.isCancelled());
            assertFalse(pacs010Message.isExpired());
            assertFalse(pacs010Message.isPending());
            assertFalse(pacs010Message.isRejected());
        }

        @Test
        @DisplayName("Should correctly identify suspended mandate")
        void shouldCorrectlyIdentifySuspendedMandate() {
            pacs010Message.setMandateStatus(Pacs010Message.MandateStatus.SUSPENDED);
            assertTrue(pacs010Message.isSuspended());
            assertFalse(pacs010Message.isActive());
            assertFalse(pacs010Message.isCancelled());
            assertFalse(pacs010Message.isExpired());
            assertFalse(pacs010Message.isPending());
            assertFalse(pacs010Message.isRejected());
        }

        @Test
        @DisplayName("Should correctly identify cancelled mandate")
        void shouldCorrectlyIdentifyCancelledMandate() {
            pacs010Message.setMandateStatus(Pacs010Message.MandateStatus.CANCELLED);
            assertTrue(pacs010Message.isCancelled());
            assertFalse(pacs010Message.isActive());
            assertFalse(pacs010Message.isSuspended());
            assertFalse(pacs010Message.isExpired());
            assertFalse(pacs010Message.isPending());
            assertFalse(pacs010Message.isRejected());
        }

        @Test
        @DisplayName("Should correctly identify expired mandate")
        void shouldCorrectlyIdentifyExpiredMandate() {
            pacs010Message.setMandateStatus(Pacs010Message.MandateStatus.EXPIRED);
            assertTrue(pacs010Message.isExpired());
            assertFalse(pacs010Message.isActive());
            assertFalse(pacs010Message.isSuspended());
            assertFalse(pacs010Message.isCancelled());
            assertFalse(pacs010Message.isPending());
            assertFalse(pacs010Message.isRejected());
        }

        @Test
        @DisplayName("Should correctly identify pending mandate")
        void shouldCorrectlyIdentifyPendingMandate() {
            pacs010Message.setMandateStatus(Pacs010Message.MandateStatus.PENDING);
            assertTrue(pacs010Message.isPending());
            assertFalse(pacs010Message.isActive());
            assertFalse(pacs010Message.isSuspended());
            assertFalse(pacs010Message.isCancelled());
            assertFalse(pacs010Message.isExpired());
            assertFalse(pacs010Message.isRejected());
        }

        @Test
        @DisplayName("Should correctly identify rejected mandate")
        void shouldCorrectlyIdentifyRejectedMandate() {
            pacs010Message.setMandateStatus(Pacs010Message.MandateStatus.REJECTED);
            assertTrue(pacs010Message.isRejected());
            assertFalse(pacs010Message.isActive());
            assertFalse(pacs010Message.isSuspended());
            assertFalse(pacs010Message.isCancelled());
            assertFalse(pacs010Message.isExpired());
            assertFalse(pacs010Message.isPending());
        }
    }

    @Nested
    @DisplayName("Mandate Type Tests")
    class MandateTypeTests {

        @Test
        @DisplayName("Should correctly identify recurring mandate")
        void shouldCorrectlyIdentifyRecurringMandate() {
            pacs010Message.setMandateType(Pacs010Message.MandateType.RECURRING);
            assertTrue(pacs010Message.isRecurring());
            assertFalse(pacs010Message.isOneOff());
            assertFalse(pacs010Message.isFixedAmount());
            assertFalse(pacs010Message.isVariableAmount());
        }

        @Test
        @DisplayName("Should correctly identify one-off mandate")
        void shouldCorrectlyIdentifyOneOffMandate() {
            pacs010Message.setMandateType(Pacs010Message.MandateType.ONE_OFF);
            assertTrue(pacs010Message.isOneOff());
            assertFalse(pacs010Message.isRecurring());
            assertFalse(pacs010Message.isFixedAmount());
            assertFalse(pacs010Message.isVariableAmount());
        }

        @Test
        @DisplayName("Should correctly identify fixed amount mandate")
        void shouldCorrectlyIdentifyFixedAmountMandate() {
            pacs010Message.setMandateType(Pacs010Message.MandateType.FIXED_AMOUNT);
            assertTrue(pacs010Message.isFixedAmount());
            assertFalse(pacs010Message.isRecurring());
            assertFalse(pacs010Message.isOneOff());
            assertFalse(pacs010Message.isVariableAmount());
        }

        @Test
        @DisplayName("Should correctly identify variable amount mandate")
        void shouldCorrectlyIdentifyVariableAmountMandate() {
            pacs010Message.setMandateType(Pacs010Message.MandateType.VARIABLE_AMOUNT);
            assertTrue(pacs010Message.isVariableAmount());
            assertFalse(pacs010Message.isRecurring());
            assertFalse(pacs010Message.isOneOff());
            assertFalse(pacs010Message.isFixedAmount());
        }
    }

    @Nested
    @DisplayName("Mandate Validity Tests")
    class MandateValidityTests {

        @Test
        @DisplayName("Should be valid when active and no end date")
        void shouldBeValidWhenActiveAndNoEndDate() {
            pacs010Message.setMandateStatus(Pacs010Message.MandateStatus.ACTIVE);
            pacs010Message.setMandateEndDate(null);
            assertTrue(pacs010Message.isValid());
        }

        @Test
        @DisplayName("Should be valid when active and end date in future")
        void shouldBeValidWhenActiveAndEndDateInFuture() {
            pacs010Message.setMandateStatus(Pacs010Message.MandateStatus.ACTIVE);
            pacs010Message.setMandateEndDate(LocalDate.now().plusDays(30));
            assertTrue(pacs010Message.isValid());
        }

        @Test
        @DisplayName("Should not be valid when active but end date in past")
        void shouldNotBeValidWhenActiveButEndDateInPast() {
            pacs010Message.setMandateStatus(Pacs010Message.MandateStatus.ACTIVE);
            pacs010Message.setMandateEndDate(LocalDate.now().minusDays(1));
            assertFalse(pacs010Message.isValid());
        }

        @Test
        @DisplayName("Should not be valid when not active")
        void shouldNotBeValidWhenNotActive() {
            pacs010Message.setMandateStatus(Pacs010Message.MandateStatus.SUSPENDED);
            assertFalse(pacs010Message.isValid());
        }
    }

    @Nested
    @DisplayName("Enum Tests")
    class EnumTests {

        @Test
        @DisplayName("Should get mandate type code")
        void shouldGetMandateTypeCode() {
            assertEquals("RCUR", Pacs010Message.MandateType.RECURRING.getCode());
            assertEquals("OOFF", Pacs010Message.MandateType.ONE_OFF.getCode());
            assertEquals("FIXD", Pacs010Message.MandateType.FIXED_AMOUNT.getCode());
            assertEquals("VARB", Pacs010Message.MandateType.VARIABLE_AMOUNT.getCode());
        }

        @Test
        @DisplayName("Should get mandate status code")
        void shouldGetMandateStatusCode() {
            assertEquals("ACTV", Pacs010Message.MandateStatus.ACTIVE.getCode());
            assertEquals("SUSP", Pacs010Message.MandateStatus.SUSPENDED.getCode());
            assertEquals("CANC", Pacs010Message.MandateStatus.CANCELLED.getCode());
            assertEquals("EXPR", Pacs010Message.MandateStatus.EXPIRED.getCode());
            assertEquals("PEND", Pacs010Message.MandateStatus.PENDING.getCode());
            assertEquals("REJT", Pacs010Message.MandateStatus.REJECTED.getCode());
        }

        @Test
        @DisplayName("Should get frequency code")
        void shouldGetFrequencyCode() {
            assertEquals("DAIL", Pacs010Message.Frequency.DAILY.getCode());
            assertEquals("WEEK", Pacs010Message.Frequency.WEEKLY.getCode());
            assertEquals("MNTH", Pacs010Message.Frequency.MONTHLY.getCode());
            assertEquals("QURT", Pacs010Message.Frequency.QUARTERLY.getCode());
            assertEquals("SEMI", Pacs010Message.Frequency.SEMI_ANNUALLY.getCode());
            assertEquals("YEAR", Pacs010Message.Frequency.ANNUALLY.getCode());
            assertEquals("ADHO", Pacs010Message.Frequency.ON_DEMAND.getCode());
            assertEquals("IRRE", Pacs010Message.Frequency.IRREGULAR.getCode());
        }
    }

    @Nested
    @DisplayName("Setter and Getter Tests")
    class SetterAndGetterTests {

        @Test
        @DisplayName("Should set and get signature date")
        void shouldSetAndGetSignatureDate() {
            LocalDate signatureDate = LocalDate.now();
            pacs010Message.setSignatureDate(signatureDate);
            assertEquals(signatureDate, pacs010Message.getSignatureDate());
        }

        @Test
        @DisplayName("Should set and get mandate start date")
        void shouldSetAndGetMandateStartDate() {
            LocalDate startDate = LocalDate.now();
            pacs010Message.setMandateStartDate(startDate);
            assertEquals(startDate, pacs010Message.getMandateStartDate());
        }

        @Test
        @DisplayName("Should set and get mandate end date")
        void shouldSetAndGetMandateEndDate() {
            LocalDate endDate = LocalDate.now().plusYears(1);
            pacs010Message.setMandateEndDate(endDate);
            assertEquals(endDate, pacs010Message.getMandateEndDate());
        }

        @Test
        @DisplayName("Should set and get debtor account ID")
        void shouldSetAndGetDebtorAccountId() {
            String debtorAccountId = "DEBTOR-ACCOUNT-001";
            pacs010Message.setDebtorAccountId(debtorAccountId);
            assertEquals(debtorAccountId, pacs010Message.getDebtorAccountId());
        }

        @Test
        @DisplayName("Should set and get creditor account ID")
        void shouldSetAndGetCreditorAccountId() {
            String creditorAccountId = "CREDITOR-ACCOUNT-001";
            pacs010Message.setCreditorAccountId(creditorAccountId);
            assertEquals(creditorAccountId, pacs010Message.getCreditorAccountId());
        }

        @Test
        @DisplayName("Should set and get mandate description")
        void shouldSetAndGetMandateDescription() {
            String description = "Monthly utility payment mandate";
            pacs010Message.setMandateDescription(description);
            assertEquals(description, pacs010Message.getMandateDescription());
        }

        @Test
        @DisplayName("Should set and get frequency")
        void shouldSetAndGetFrequency() {
            Pacs010Message.Frequency frequency = Pacs010Message.Frequency.MONTHLY;
            pacs010Message.setFrequency(frequency);
            assertEquals(frequency, pacs010Message.getFrequency());
        }

        @Test
        @DisplayName("Should set and get max amount")
        void shouldSetAndGetMaxAmount() {
            BigDecimal maxAmount = new BigDecimal("5000.00");
            pacs010Message.setMaxAmount(maxAmount);
            assertEquals(maxAmount, pacs010Message.getMaxAmount());
        }

        @Test
        @DisplayName("Should set and get currency")
        void shouldSetAndGetCurrency() {
            String currency = "EUR";
            pacs010Message.setCurrency(currency);
            assertEquals(currency, pacs010Message.getCurrency());
        }

        @Test
        @DisplayName("Should set and get signature method")
        void shouldSetAndGetSignatureMethod() {
            String signatureMethod = "ELECTRONIC";
            pacs010Message.setSignatureMethod(signatureMethod);
            assertEquals(signatureMethod, pacs010Message.getSignatureMethod());
        }

        @Test
        @DisplayName("Should set and get clearing system ID")
        void shouldSetAndGetClearingSystemId() {
            String clearingSystemId = "CLEARING-001";
            pacs010Message.setClearingSystemId(clearingSystemId);
            assertEquals(clearingSystemId, pacs010Message.getClearingSystemId());
        }

        @Test
        @DisplayName("Should set and get settlement system ID")
        void shouldSetAndGetSettlementSystemId() {
            String settlementSystemId = "SETTLEMENT-001";
            pacs010Message.setSettlementSystemId(settlementSystemId);
            assertEquals(settlementSystemId, pacs010Message.getSettlementSystemId());
        }

        @Test
        @DisplayName("Should set and get electronic signature flag")
        void shouldSetAndGetElectronicSignatureFlag() {
            pacs010Message.setElectronicSignature(true);
            assertTrue(pacs010Message.isElectronicSignature());

            pacs010Message.setElectronicSignature(false);
            assertFalse(pacs010Message.isElectronicSignature());
        }

        @Test
        @DisplayName("Should set and get paper signature flag")
        void shouldSetAndGetPaperSignatureFlag() {
            pacs010Message.setPaperSignature(true);
            assertTrue(pacs010Message.isPaperSignature());

            pacs010Message.setPaperSignature(false);
            assertFalse(pacs010Message.isPaperSignature());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(pacs010Message, pacs010Message);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, pacs010Message);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", pacs010Message);
        }

        @Test
        @DisplayName("Should not be equal to message with different creation time")
        void shouldNotBeEqualToMessageWithDifferentCreationTime() {
            Pacs010Message other = new Pacs010Message(MANDATE_ID,
                                                    Pacs010Message.MandateType.RECURRING,
                                                    "DEBTOR-001", "CREDITOR-001");

            // Should be different due to different creation times
            assertNotEquals(pacs010Message, other);
            assertNotEquals(pacs010Message.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to message with different message ID")
        void shouldNotBeEqualToMessageWithDifferentMessageId() {
            Pacs010Message other = new Pacs010Message(MANDATE_ID,
                                                    Pacs010Message.MandateType.RECURRING,
                                                    "DEBTOR-001", "CREDITOR-001");
            other.setMessageId("DIFFERENT-ID");

            assertNotEquals(pacs010Message, other);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void shouldIncludeKeyFieldsInToString() {
            String toString = pacs010Message.toString();

            assertTrue(toString.contains("Pacs010Message"));
            assertTrue(toString.contains(pacs010Message.getMessageId()));
            assertTrue(toString.contains(MANDATE_ID));
            assertTrue(toString.contains("RECURRING"));
            assertTrue(toString.contains("PENDING"));
            assertTrue(toString.contains("DEBTOR-001"));
            assertTrue(toString.contains("CREDITOR-001"));
        }

        @Test
        @DisplayName("Should include mandate status in toString")
        void shouldIncludeMandateStatusInToString() {
            pacs010Message.setMandateStatus(Pacs010Message.MandateStatus.ACTIVE);
            String toString = pacs010Message.toString();

            assertTrue(toString.contains("ACTIVE"));
        }
    }
}
