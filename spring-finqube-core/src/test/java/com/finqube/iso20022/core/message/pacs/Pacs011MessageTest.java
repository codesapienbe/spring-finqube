package com.finqube.iso20022.core.message.pacs;

import com.finqube.iso20022.core.exception.MessageValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Pacs011Message (Direct Debit Mandate Amendment).
 *
 * <p>Tests cover validation, amendment management, and business logic for
 * ISO 20022 pacs.011 messages.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
@DisplayName("Pacs011Message Tests")
class Pacs011MessageTest {

    private Pacs011Message pacs011Message;
    private static final String ORIGINAL_MANDATE_ID = "MANDATE-" + UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        pacs011Message = new Pacs011Message(ORIGINAL_MANDATE_ID,
                                          Pacs011Message.AmendmentType.AMOUNT_CHANGE,
                                          "DEBTOR-001", "CREDITOR-001");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should initialize required fields")
        void defaultConstructorShouldInitializeRequiredFields() {
            Pacs011Message message = new Pacs011Message();

            assertNotNull(message.getMessageId());
            assertTrue(message.getMessageId().startsWith("PACS-"));
            assertNotNull(message.getCreationTime());
            assertEquals(Pacs011Message.AmendmentType.OTHER, message.getAmendmentType());
            assertEquals(Pacs011Message.AmendmentStatus.PENDING, message.getAmendmentStatus());
            assertEquals("USD", message.getNewCurrency());
        }

        @Test
        @DisplayName("Parameterized constructor should set provided values")
        void parameterizedConstructorShouldSetProvidedValues() {
            String originalMandateId = "MANDATE-123";
            Pacs011Message.AmendmentType amendmentType = Pacs011Message.AmendmentType.FREQUENCY_CHANGE;
            String debtorId = "DEBTOR-002";
            String creditorId = "CREDITOR-002";

            Pacs011Message message = new Pacs011Message(originalMandateId, amendmentType, debtorId, creditorId);

            assertEquals(originalMandateId, message.getOriginalMandateId());
            assertEquals(amendmentType, message.getAmendmentType());
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
            assertEquals("pacs.011", pacs011Message.getMessageType());
        }

        @Test
        @DisplayName("Should return correct business process")
        void shouldReturnCorrectBusinessProcess() {
            assertEquals("pacs", pacs011Message.getBusinessProcess());
        }

        @Test
        @DisplayName("Should return normal priority")
        void shouldReturnNormalPriority() {
            assertEquals(com.finqube.iso20022.core.message.MessagePriority.NORMAL,
                        pacs011Message.getPriority());
        }

        @Test
        @DisplayName("Should return correct schema version")
        void shouldReturnCorrectSchemaVersion() {
            assertEquals("001.001.11", pacs011Message.getSchemaVersion());
        }

        @Test
        @DisplayName("Should require acknowledgment")
        void shouldRequireAcknowledgment() {
            assertTrue(pacs011Message.requiresAcknowledgment());
        }

        @Test
        @DisplayName("Should return correct description")
        void shouldReturnCorrectDescription() {
            String description = pacs011Message.getDescription();
            assertTrue(description.contains("Direct Debit Mandate Amendment"));
            assertTrue(description.contains(ORIGINAL_MANDATE_ID));
            assertTrue(description.contains("AMOUNT_CHANGE"));
            assertTrue(description.contains("PENDING"));
        }

        @Test
        @DisplayName("Should return original mandate ID in transactions list")
        void shouldReturnOriginalMandateIdInTransactionsList() {
            var transactions = pacs011Message.getTransactions();
            assertEquals(1, transactions.size());
            assertEquals(ORIGINAL_MANDATE_ID, transactions.get(0));
        }

        @Test
        @DisplayName("Should return correct transaction count")
        void shouldReturnCorrectTransactionCount() {
            assertEquals(1, pacs011Message.getTransactionCount());
        }

        @Test
        @DisplayName("Should return new max amount as total amount")
        void shouldReturnNewMaxAmountAsTotalAmount() {
            pacs011Message.setNewMaxAmount(new BigDecimal("2000.00"));
            assertEquals(2000.0, pacs011Message.getTotalAmount());
        }

        @Test
        @DisplayName("Should return zero when new max amount is null")
        void shouldReturnZeroWhenNewMaxAmountIsNull() {
            assertEquals(0.0, pacs011Message.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid message should pass validation")
        void validMessageShouldPassValidation() {
            assertTrue(pacs011Message.validate());
        }

        @Test
        @DisplayName("Should throw exception when message ID is null")
        void shouldThrowExceptionWhenMessageIdIsNull() {
            pacs011Message.setMessageId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs011Message.validate()
            );

            assertEquals("Message ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when original mandate ID is null")
        void shouldThrowExceptionWhenOriginalMandateIdIsNull() {
            pacs011Message.setOriginalMandateId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs011Message.validate()
            );

            assertEquals("Original mandate ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when amendment type is null")
        void shouldThrowExceptionWhenAmendmentTypeIsNull() {
            pacs011Message.setAmendmentType(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs011Message.validate()
            );

            assertEquals("Amendment type is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when amendment status is null")
        void shouldThrowExceptionWhenAmendmentStatusIsNull() {
            pacs011Message.setAmendmentStatus(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs011Message.validate()
            );

            assertEquals("Amendment status is required", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Amendment Status Tests")
    class AmendmentStatusTests {

        @Test
        @DisplayName("Should correctly identify pending amendment")
        void shouldCorrectlyIdentifyPendingAmendment() {
            pacs011Message.setAmendmentStatus(Pacs011Message.AmendmentStatus.PENDING);
            assertTrue(pacs011Message.isPending());
            assertFalse(pacs011Message.isApproved());
            assertFalse(pacs011Message.isRejected());
            assertFalse(pacs011Message.isCancelled());
            assertFalse(pacs011Message.isCompleted());
        }

        @Test
        @DisplayName("Should correctly identify approved amendment")
        void shouldCorrectlyIdentifyApprovedAmendment() {
            pacs011Message.setAmendmentStatus(Pacs011Message.AmendmentStatus.APPROVED);
            assertTrue(pacs011Message.isApproved());
            assertFalse(pacs011Message.isPending());
            assertFalse(pacs011Message.isRejected());
            assertFalse(pacs011Message.isCancelled());
            assertFalse(pacs011Message.isCompleted());
        }

        @Test
        @DisplayName("Should correctly identify rejected amendment")
        void shouldCorrectlyIdentifyRejectedAmendment() {
            pacs011Message.setAmendmentStatus(Pacs011Message.AmendmentStatus.REJECTED);
            assertTrue(pacs011Message.isRejected());
            assertFalse(pacs011Message.isPending());
            assertFalse(pacs011Message.isApproved());
            assertFalse(pacs011Message.isCancelled());
            assertFalse(pacs011Message.isCompleted());
        }

        @Test
        @DisplayName("Should correctly identify cancelled amendment")
        void shouldCorrectlyIdentifyCancelledAmendment() {
            pacs011Message.setAmendmentStatus(Pacs011Message.AmendmentStatus.CANCELLED);
            assertTrue(pacs011Message.isCancelled());
            assertFalse(pacs011Message.isPending());
            assertFalse(pacs011Message.isApproved());
            assertFalse(pacs011Message.isRejected());
            assertFalse(pacs011Message.isCompleted());
        }

        @Test
        @DisplayName("Should correctly identify completed amendment")
        void shouldCorrectlyIdentifyCompletedAmendment() {
            pacs011Message.setAmendmentStatus(Pacs011Message.AmendmentStatus.COMPLETED);
            assertTrue(pacs011Message.isCompleted());
            assertFalse(pacs011Message.isPending());
            assertFalse(pacs011Message.isApproved());
            assertFalse(pacs011Message.isRejected());
            assertFalse(pacs011Message.isCancelled());
        }
    }

    @Nested
    @DisplayName("Amendment Type Tests")
    class AmendmentTypeTests {

        @Test
        @DisplayName("Should correctly identify amount change")
        void shouldCorrectlyIdentifyAmountChange() {
            pacs011Message.setAmendmentType(Pacs011Message.AmendmentType.AMOUNT_CHANGE);
            assertTrue(pacs011Message.isAmountChange());
            assertFalse(pacs011Message.isFrequencyChange());
            assertFalse(pacs011Message.isDateChange());
            assertFalse(pacs011Message.isAccountChange());
            assertFalse(pacs011Message.isCreditorChange());
            assertFalse(pacs011Message.isDescriptionChange());
            assertFalse(pacs011Message.isCurrencyChange());
            assertFalse(pacs011Message.isMultipleChanges());
            assertFalse(pacs011Message.isOtherChange());
        }

        @Test
        @DisplayName("Should correctly identify frequency change")
        void shouldCorrectlyIdentifyFrequencyChange() {
            pacs011Message.setAmendmentType(Pacs011Message.AmendmentType.FREQUENCY_CHANGE);
            assertTrue(pacs011Message.isFrequencyChange());
            assertFalse(pacs011Message.isAmountChange());
        }

        @Test
        @DisplayName("Should correctly identify date change")
        void shouldCorrectlyIdentifyDateChange() {
            pacs011Message.setAmendmentType(Pacs011Message.AmendmentType.DATE_CHANGE);
            assertTrue(pacs011Message.isDateChange());
            assertFalse(pacs011Message.isAmountChange());
        }

        @Test
        @DisplayName("Should correctly identify account change")
        void shouldCorrectlyIdentifyAccountChange() {
            pacs011Message.setAmendmentType(Pacs011Message.AmendmentType.ACCOUNT_CHANGE);
            assertTrue(pacs011Message.isAccountChange());
            assertFalse(pacs011Message.isAmountChange());
        }

        @Test
        @DisplayName("Should correctly identify creditor change")
        void shouldCorrectlyIdentifyCreditorChange() {
            pacs011Message.setAmendmentType(Pacs011Message.AmendmentType.CREDITOR_CHANGE);
            assertTrue(pacs011Message.isCreditorChange());
            assertFalse(pacs011Message.isAmountChange());
        }

        @Test
        @DisplayName("Should correctly identify description change")
        void shouldCorrectlyIdentifyDescriptionChange() {
            pacs011Message.setAmendmentType(Pacs011Message.AmendmentType.DESCRIPTION_CHANGE);
            assertTrue(pacs011Message.isDescriptionChange());
            assertFalse(pacs011Message.isAmountChange());
        }

        @Test
        @DisplayName("Should correctly identify currency change")
        void shouldCorrectlyIdentifyCurrencyChange() {
            pacs011Message.setAmendmentType(Pacs011Message.AmendmentType.CURRENCY_CHANGE);
            assertTrue(pacs011Message.isCurrencyChange());
            assertFalse(pacs011Message.isAmountChange());
        }

        @Test
        @DisplayName("Should correctly identify multiple changes")
        void shouldCorrectlyIdentifyMultipleChanges() {
            pacs011Message.setAmendmentType(Pacs011Message.AmendmentType.MULTIPLE_CHANGES);
            assertTrue(pacs011Message.isMultipleChanges());
            assertFalse(pacs011Message.isAmountChange());
        }

        @Test
        @DisplayName("Should correctly identify other change")
        void shouldCorrectlyIdentifyOtherChange() {
            pacs011Message.setAmendmentType(Pacs011Message.AmendmentType.OTHER);
            assertTrue(pacs011Message.isOtherChange());
            assertFalse(pacs011Message.isAmountChange());
        }
    }

    @Nested
    @DisplayName("Amendment Effectiveness Tests")
    class AmendmentEffectivenessTests {

        @Test
        @DisplayName("Should be effective when approved and no effective date")
        void shouldBeEffectiveWhenApprovedAndNoEffectiveDate() {
            pacs011Message.setAmendmentStatus(Pacs011Message.AmendmentStatus.APPROVED);
            pacs011Message.setEffectiveDate(null);
            assertTrue(pacs011Message.isEffective());
        }

        @Test
        @DisplayName("Should be effective when approved and effective date in past")
        void shouldBeEffectiveWhenApprovedAndEffectiveDateInPast() {
            pacs011Message.setAmendmentStatus(Pacs011Message.AmendmentStatus.APPROVED);
            pacs011Message.setEffectiveDate(LocalDate.now().minusDays(1));
            assertTrue(pacs011Message.isEffective());
        }

        @Test
        @DisplayName("Should be effective when approved and effective date is today")
        void shouldBeEffectiveWhenApprovedAndEffectiveDateIsToday() {
            pacs011Message.setAmendmentStatus(Pacs011Message.AmendmentStatus.APPROVED);
            pacs011Message.setEffectiveDate(LocalDate.now());
            assertTrue(pacs011Message.isEffective());
        }

        @Test
        @DisplayName("Should not be effective when approved but effective date in future")
        void shouldNotBeEffectiveWhenApprovedButEffectiveDateInFuture() {
            pacs011Message.setAmendmentStatus(Pacs011Message.AmendmentStatus.APPROVED);
            pacs011Message.setEffectiveDate(LocalDate.now().plusDays(1));
            assertFalse(pacs011Message.isEffective());
        }

        @Test
        @DisplayName("Should not be effective when not approved")
        void shouldNotBeEffectiveWhenNotApproved() {
            pacs011Message.setAmendmentStatus(Pacs011Message.AmendmentStatus.PENDING);
            assertFalse(pacs011Message.isEffective());
        }
    }

    @Nested
    @DisplayName("Enum Tests")
    class EnumTests {

        @Test
        @DisplayName("Should get amendment type code")
        void shouldGetAmendmentTypeCode() {
            assertEquals("AMNT", Pacs011Message.AmendmentType.AMOUNT_CHANGE.getCode());
            assertEquals("FREQ", Pacs011Message.AmendmentType.FREQUENCY_CHANGE.getCode());
            assertEquals("DATE", Pacs011Message.AmendmentType.DATE_CHANGE.getCode());
            assertEquals("ACCT", Pacs011Message.AmendmentType.ACCOUNT_CHANGE.getCode());
            assertEquals("CRED", Pacs011Message.AmendmentType.CREDITOR_CHANGE.getCode());
            assertEquals("DESC", Pacs011Message.AmendmentType.DESCRIPTION_CHANGE.getCode());
            assertEquals("CURR", Pacs011Message.AmendmentType.CURRENCY_CHANGE.getCode());
            assertEquals("MULT", Pacs011Message.AmendmentType.MULTIPLE_CHANGES.getCode());
            assertEquals("OTHR", Pacs011Message.AmendmentType.OTHER.getCode());
        }

        @Test
        @DisplayName("Should get amendment status code")
        void shouldGetAmendmentStatusCode() {
            assertEquals("PEND", Pacs011Message.AmendmentStatus.PENDING.getCode());
            assertEquals("APPR", Pacs011Message.AmendmentStatus.APPROVED.getCode());
            assertEquals("REJT", Pacs011Message.AmendmentStatus.REJECTED.getCode());
            assertEquals("CANC", Pacs011Message.AmendmentStatus.CANCELLED.getCode());
            assertEquals("COMP", Pacs011Message.AmendmentStatus.COMPLETED.getCode());
        }
    }

    @Nested
    @DisplayName("Setter and Getter Tests")
    class SetterAndGetterTests {

        @Test
        @DisplayName("Should set and get amendment date")
        void shouldSetAndGetAmendmentDate() {
            LocalDate amendmentDate = LocalDate.now();
            pacs011Message.setAmendmentDate(amendmentDate);
            assertEquals(amendmentDate, pacs011Message.getAmendmentDate());
        }

        @Test
        @DisplayName("Should set and get effective date")
        void shouldSetAndGetEffectiveDate() {
            LocalDate effectiveDate = LocalDate.now().plusDays(30);
            pacs011Message.setEffectiveDate(effectiveDate);
            assertEquals(effectiveDate, pacs011Message.getEffectiveDate());
        }

        @Test
        @DisplayName("Should set and get debtor account ID")
        void shouldSetAndGetDebtorAccountId() {
            String debtorAccountId = "DEBTOR-ACCOUNT-001";
            pacs011Message.setDebtorAccountId(debtorAccountId);
            assertEquals(debtorAccountId, pacs011Message.getDebtorAccountId());
        }

        @Test
        @DisplayName("Should set and get creditor account ID")
        void shouldSetAndGetCreditorAccountId() {
            String creditorAccountId = "CREDITOR-ACCOUNT-001";
            pacs011Message.setCreditorAccountId(creditorAccountId);
            assertEquals(creditorAccountId, pacs011Message.getCreditorAccountId());
        }

        @Test
        @DisplayName("Should set and get amendment description")
        void shouldSetAndGetAmendmentDescription() {
            String description = "Increase maximum amount for utility payments";
            pacs011Message.setAmendmentDescription(description);
            assertEquals(description, pacs011Message.getAmendmentDescription());
        }

        @Test
        @DisplayName("Should set and get new max amount")
        void shouldSetAndGetNewMaxAmount() {
            BigDecimal newMaxAmount = new BigDecimal("3000.00");
            pacs011Message.setNewMaxAmount(newMaxAmount);
            assertEquals(newMaxAmount, pacs011Message.getNewMaxAmount());
        }

        @Test
        @DisplayName("Should set and get new currency")
        void shouldSetAndGetNewCurrency() {
            String newCurrency = "EUR";
            pacs011Message.setNewCurrency(newCurrency);
            assertEquals(newCurrency, pacs011Message.getNewCurrency());
        }

        @Test
        @DisplayName("Should set and get new frequency")
        void shouldSetAndGetNewFrequency() {
            Pacs010Message.Frequency newFrequency = Pacs010Message.Frequency.QUARTERLY;
            pacs011Message.setNewFrequency(newFrequency);
            assertEquals(newFrequency, pacs011Message.getNewFrequency());
        }

        @Test
        @DisplayName("Should set and get amendment reason")
        void shouldSetAndGetAmendmentReason() {
            String amendmentReason = "INCREASE_LIMIT";
            pacs011Message.setAmendmentReason(amendmentReason);
            assertEquals(amendmentReason, pacs011Message.getAmendmentReason());
        }

        @Test
        @DisplayName("Should set and get clearing system ID")
        void shouldSetAndGetClearingSystemId() {
            String clearingSystemId = "CLEARING-001";
            pacs011Message.setClearingSystemId(clearingSystemId);
            assertEquals(clearingSystemId, pacs011Message.getClearingSystemId());
        }

        @Test
        @DisplayName("Should set and get settlement system ID")
        void shouldSetAndGetSettlementSystemId() {
            String settlementSystemId = "SETTLEMENT-001";
            pacs011Message.setSettlementSystemId(settlementSystemId);
            assertEquals(settlementSystemId, pacs011Message.getSettlementSystemId());
        }

        @Test
        @DisplayName("Should set and get debtor approval requirement")
        void shouldSetAndGetDebtorApprovalRequirement() {
            pacs011Message.setRequiresDebtorApproval(true);
            assertTrue(pacs011Message.isRequiresDebtorApproval());

            pacs011Message.setRequiresDebtorApproval(false);
            assertFalse(pacs011Message.isRequiresDebtorApproval());
        }

        @Test
        @DisplayName("Should set and get creditor approval requirement")
        void shouldSetAndGetCreditorApprovalRequirement() {
            pacs011Message.setRequiresCreditorApproval(true);
            assertTrue(pacs011Message.isRequiresCreditorApproval());

            pacs011Message.setRequiresCreditorApproval(false);
            assertFalse(pacs011Message.isRequiresCreditorApproval());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(pacs011Message, pacs011Message);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, pacs011Message);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", pacs011Message);
        }

        @Test
        @DisplayName("Should not be equal to message with different creation time")
        void shouldNotBeEqualToMessageWithDifferentCreationTime() {
            Pacs011Message other = new Pacs011Message(ORIGINAL_MANDATE_ID,
                                                    Pacs011Message.AmendmentType.AMOUNT_CHANGE,
                                                    "DEBTOR-001", "CREDITOR-001");

            // Should be different due to different creation times
            assertNotEquals(pacs011Message, other);
            assertNotEquals(pacs011Message.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to message with different message ID")
        void shouldNotBeEqualToMessageWithDifferentMessageId() {
            Pacs011Message other = new Pacs011Message(ORIGINAL_MANDATE_ID,
                                                    Pacs011Message.AmendmentType.AMOUNT_CHANGE,
                                                    "DEBTOR-001", "CREDITOR-001");
            other.setMessageId("DIFFERENT-ID");

            assertNotEquals(pacs011Message, other);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void shouldIncludeKeyFieldsInToString() {
            String toString = pacs011Message.toString();

            assertTrue(toString.contains("Pacs011Message"));
            assertTrue(toString.contains(pacs011Message.getMessageId()));
            assertTrue(toString.contains(ORIGINAL_MANDATE_ID));
            assertTrue(toString.contains("AMOUNT_CHANGE"));
            assertTrue(toString.contains("PENDING"));
            assertTrue(toString.contains("DEBTOR-001"));
            assertTrue(toString.contains("CREDITOR-001"));
        }

        @Test
        @DisplayName("Should include amendment status in toString")
        void shouldIncludeAmendmentStatusInToString() {
            pacs011Message.setAmendmentStatus(Pacs011Message.AmendmentStatus.APPROVED);
            String toString = pacs011Message.toString();

            assertTrue(toString.contains("APPROVED"));
        }
    }
}
