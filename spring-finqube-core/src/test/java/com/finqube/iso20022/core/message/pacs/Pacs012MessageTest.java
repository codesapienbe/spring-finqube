package com.finqube.iso20022.core.message.pacs;

import com.finqube.iso20022.core.exception.MessageValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Pacs012Message (Direct Debit Mandate Cancellation).
 *
 * <p>Tests cover validation, cancellation management, and business logic for
 * ISO 20022 pacs.012 messages.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
@DisplayName("Pacs012Message Tests")
class Pacs012MessageTest {

    private Pacs012Message pacs012Message;
    private static final String ORIGINAL_MANDATE_ID = "MANDATE-" + UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        pacs012Message = new Pacs012Message(ORIGINAL_MANDATE_ID,
                                          Pacs012Message.CancellationReason.CUSTOMER_REQUEST,
                                          "DEBTOR-001", "CREDITOR-001");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should initialize required fields")
        void defaultConstructorShouldInitializeRequiredFields() {
            Pacs012Message message = new Pacs012Message();

            assertNotNull(message.getMessageId());
            assertTrue(message.getMessageId().startsWith("PACS-"));
            assertNotNull(message.getCreationTime());
            assertEquals(Pacs012Message.CancellationReason.CUSTOMER_REQUEST, message.getCancellationReason());
            assertEquals(Pacs012Message.CancellationStatus.PENDING, message.getCancellationStatus());
        }

        @Test
        @DisplayName("Parameterized constructor should set provided values")
        void parameterizedConstructorShouldSetProvidedValues() {
            String originalMandateId = "MANDATE-123";
            Pacs012Message.CancellationReason cancellationReason = Pacs012Message.CancellationReason.ACCOUNT_CLOSED;
            String debtorId = "DEBTOR-002";
            String creditorId = "CREDITOR-002";

            Pacs012Message message = new Pacs012Message(originalMandateId, cancellationReason, debtorId, creditorId);

            assertEquals(originalMandateId, message.getOriginalMandateId());
            assertEquals(cancellationReason, message.getCancellationReason());
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
            assertEquals("pacs.012", pacs012Message.getMessageType());
        }

        @Test
        @DisplayName("Should return correct business process")
        void shouldReturnCorrectBusinessProcess() {
            assertEquals("pacs", pacs012Message.getBusinessProcess());
        }

        @Test
        @DisplayName("Should return high priority")
        void shouldReturnHighPriority() {
            assertEquals(com.finqube.iso20022.core.message.MessagePriority.HIGH,
                        pacs012Message.getPriority());
        }

        @Test
        @DisplayName("Should return correct schema version")
        void shouldReturnCorrectSchemaVersion() {
            assertEquals("001.001.11", pacs012Message.getSchemaVersion());
        }

        @Test
        @DisplayName("Should require acknowledgment")
        void shouldRequireAcknowledgment() {
            assertTrue(pacs012Message.requiresAcknowledgment());
        }

        @Test
        @DisplayName("Should return correct description")
        void shouldReturnCorrectDescription() {
            String description = pacs012Message.getDescription();
            assertTrue(description.contains("Direct Debit Mandate Cancellation"));
            assertTrue(description.contains(ORIGINAL_MANDATE_ID));
            assertTrue(description.contains("CUSTOMER_REQUEST"));
            assertTrue(description.contains("PENDING"));
        }

        @Test
        @DisplayName("Should return original mandate ID in transactions list")
        void shouldReturnOriginalMandateIdInTransactionsList() {
            var transactions = pacs012Message.getTransactions();
            assertEquals(1, transactions.size());
            assertEquals(ORIGINAL_MANDATE_ID, transactions.get(0));
        }

        @Test
        @DisplayName("Should return correct transaction count")
        void shouldReturnCorrectTransactionCount() {
            assertEquals(1, pacs012Message.getTransactionCount());
        }

        @Test
        @DisplayName("Should return zero as total amount")
        void shouldReturnZeroAsTotalAmount() {
            assertEquals(0.0, pacs012Message.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid message should pass validation")
        void validMessageShouldPassValidation() {
            assertTrue(pacs012Message.validate());
        }

        @Test
        @DisplayName("Should throw exception when message ID is null")
        void shouldThrowExceptionWhenMessageIdIsNull() {
            pacs012Message.setMessageId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs012Message.validate()
            );

            assertEquals("Message ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when original mandate ID is null")
        void shouldThrowExceptionWhenOriginalMandateIdIsNull() {
            pacs012Message.setOriginalMandateId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs012Message.validate()
            );

            assertEquals("Original mandate ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when cancellation reason is null")
        void shouldThrowExceptionWhenCancellationReasonIsNull() {
            pacs012Message.setCancellationReason(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs012Message.validate()
            );

            assertEquals("Cancellation reason is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when cancellation status is null")
        void shouldThrowExceptionWhenCancellationStatusIsNull() {
            pacs012Message.setCancellationStatus(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs012Message.validate()
            );

            assertEquals("Cancellation status is required", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Cancellation Status Tests")
    class CancellationStatusTests {

        @Test
        @DisplayName("Should correctly identify pending cancellation")
        void shouldCorrectlyIdentifyPendingCancellation() {
            pacs012Message.setCancellationStatus(Pacs012Message.CancellationStatus.PENDING);
            assertTrue(pacs012Message.isPending());
            assertFalse(pacs012Message.isApproved());
            assertFalse(pacs012Message.isRejected());
            assertFalse(pacs012Message.isCancelled());
            assertFalse(pacs012Message.isCompleted());
        }

        @Test
        @DisplayName("Should correctly identify approved cancellation")
        void shouldCorrectlyIdentifyApprovedCancellation() {
            pacs012Message.setCancellationStatus(Pacs012Message.CancellationStatus.APPROVED);
            assertTrue(pacs012Message.isApproved());
            assertFalse(pacs012Message.isPending());
            assertFalse(pacs012Message.isRejected());
            assertFalse(pacs012Message.isCancelled());
            assertFalse(pacs012Message.isCompleted());
        }

        @Test
        @DisplayName("Should correctly identify rejected cancellation")
        void shouldCorrectlyIdentifyRejectedCancellation() {
            pacs012Message.setCancellationStatus(Pacs012Message.CancellationStatus.REJECTED);
            assertTrue(pacs012Message.isRejected());
            assertFalse(pacs012Message.isPending());
            assertFalse(pacs012Message.isApproved());
            assertFalse(pacs012Message.isCancelled());
            assertFalse(pacs012Message.isCompleted());
        }

        @Test
        @DisplayName("Should correctly identify cancelled cancellation")
        void shouldCorrectlyIdentifyCancelledCancellation() {
            pacs012Message.setCancellationStatus(Pacs012Message.CancellationStatus.CANCELLED);
            assertTrue(pacs012Message.isCancelled());
            assertFalse(pacs012Message.isPending());
            assertFalse(pacs012Message.isApproved());
            assertFalse(pacs012Message.isRejected());
            assertFalse(pacs012Message.isCompleted());
        }

        @Test
        @DisplayName("Should correctly identify completed cancellation")
        void shouldCorrectlyIdentifyCompletedCancellation() {
            pacs012Message.setCancellationStatus(Pacs012Message.CancellationStatus.COMPLETED);
            assertTrue(pacs012Message.isCompleted());
            assertFalse(pacs012Message.isPending());
            assertFalse(pacs012Message.isApproved());
            assertFalse(pacs012Message.isRejected());
            assertFalse(pacs012Message.isCancelled());
        }
    }

    @Nested
    @DisplayName("Cancellation Reason Tests")
    class CancellationReasonTests {

        @Test
        @DisplayName("Should correctly identify customer request")
        void shouldCorrectlyIdentifyCustomerRequest() {
            pacs012Message.setCancellationReason(Pacs012Message.CancellationReason.CUSTOMER_REQUEST);
            assertTrue(pacs012Message.isCustomerRequest());
            assertFalse(pacs012Message.isAccountClosed());
            assertFalse(pacs012Message.isInsufficientFunds());
            assertFalse(pacs012Message.isUnauthorized());
            assertFalse(pacs012Message.isDuplicateMandate());
            assertFalse(pacs012Message.isTechnicalError());
            assertFalse(pacs012Message.isComplianceIssue());
            assertFalse(pacs012Message.isMandateExpired());
            assertFalse(pacs012Message.isCreditorRequest());
            assertFalse(pacs012Message.isBankRequest());
            assertFalse(pacs012Message.isOtherReason());
        }

        @Test
        @DisplayName("Should correctly identify account closed")
        void shouldCorrectlyIdentifyAccountClosed() {
            pacs012Message.setCancellationReason(Pacs012Message.CancellationReason.ACCOUNT_CLOSED);
            assertTrue(pacs012Message.isAccountClosed());
            assertFalse(pacs012Message.isCustomerRequest());
        }

        @Test
        @DisplayName("Should correctly identify insufficient funds")
        void shouldCorrectlyIdentifyInsufficientFunds() {
            pacs012Message.setCancellationReason(Pacs012Message.CancellationReason.INSUFFICIENT_FUNDS);
            assertTrue(pacs012Message.isInsufficientFunds());
            assertFalse(pacs012Message.isCustomerRequest());
        }

        @Test
        @DisplayName("Should correctly identify unauthorized")
        void shouldCorrectlyIdentifyUnauthorized() {
            pacs012Message.setCancellationReason(Pacs012Message.CancellationReason.UNAUTHORIZED);
            assertTrue(pacs012Message.isUnauthorized());
            assertFalse(pacs012Message.isCustomerRequest());
        }

        @Test
        @DisplayName("Should correctly identify duplicate mandate")
        void shouldCorrectlyIdentifyDuplicateMandate() {
            pacs012Message.setCancellationReason(Pacs012Message.CancellationReason.DUPLICATE_MANDATE);
            assertTrue(pacs012Message.isDuplicateMandate());
            assertFalse(pacs012Message.isCustomerRequest());
        }

        @Test
        @DisplayName("Should correctly identify technical error")
        void shouldCorrectlyIdentifyTechnicalError() {
            pacs012Message.setCancellationReason(Pacs012Message.CancellationReason.TECHNICAL_ERROR);
            assertTrue(pacs012Message.isTechnicalError());
            assertFalse(pacs012Message.isCustomerRequest());
        }

        @Test
        @DisplayName("Should correctly identify compliance issue")
        void shouldCorrectlyIdentifyComplianceIssue() {
            pacs012Message.setCancellationReason(Pacs012Message.CancellationReason.COMPLIANCE_ISSUE);
            assertTrue(pacs012Message.isComplianceIssue());
            assertFalse(pacs012Message.isCustomerRequest());
        }

        @Test
        @DisplayName("Should correctly identify mandate expired")
        void shouldCorrectlyIdentifyMandateExpired() {
            pacs012Message.setCancellationReason(Pacs012Message.CancellationReason.MANDATE_EXPIRED);
            assertTrue(pacs012Message.isMandateExpired());
            assertFalse(pacs012Message.isCustomerRequest());
        }

        @Test
        @DisplayName("Should correctly identify creditor request")
        void shouldCorrectlyIdentifyCreditorRequest() {
            pacs012Message.setCancellationReason(Pacs012Message.CancellationReason.CREDITOR_REQUEST);
            assertTrue(pacs012Message.isCreditorRequest());
            assertFalse(pacs012Message.isCustomerRequest());
        }

        @Test
        @DisplayName("Should correctly identify bank request")
        void shouldCorrectlyIdentifyBankRequest() {
            pacs012Message.setCancellationReason(Pacs012Message.CancellationReason.BANK_REQUEST);
            assertTrue(pacs012Message.isBankRequest());
            assertFalse(pacs012Message.isCustomerRequest());
        }

        @Test
        @DisplayName("Should correctly identify other reason")
        void shouldCorrectlyIdentifyOtherReason() {
            pacs012Message.setCancellationReason(Pacs012Message.CancellationReason.OTHER);
            assertTrue(pacs012Message.isOtherReason());
            assertFalse(pacs012Message.isCustomerRequest());
        }
    }

    @Nested
    @DisplayName("Cancellation Effectiveness Tests")
    class CancellationEffectivenessTests {

        @Test
        @DisplayName("Should be effective when approved and no effective date")
        void shouldBeEffectiveWhenApprovedAndNoEffectiveDate() {
            pacs012Message.setCancellationStatus(Pacs012Message.CancellationStatus.APPROVED);
            pacs012Message.setEffectiveDate(null);
            assertTrue(pacs012Message.isEffective());
        }

        @Test
        @DisplayName("Should be effective when approved and effective date in past")
        void shouldBeEffectiveWhenApprovedAndEffectiveDateInPast() {
            pacs012Message.setCancellationStatus(Pacs012Message.CancellationStatus.APPROVED);
            pacs012Message.setEffectiveDate(LocalDate.now().minusDays(1));
            assertTrue(pacs012Message.isEffective());
        }

        @Test
        @DisplayName("Should be effective when approved and effective date is today")
        void shouldBeEffectiveWhenApprovedAndEffectiveDateIsToday() {
            pacs012Message.setCancellationStatus(Pacs012Message.CancellationStatus.APPROVED);
            pacs012Message.setEffectiveDate(LocalDate.now());
            assertTrue(pacs012Message.isEffective());
        }

        @Test
        @DisplayName("Should not be effective when approved but effective date in future")
        void shouldNotBeEffectiveWhenApprovedButEffectiveDateInFuture() {
            pacs012Message.setCancellationStatus(Pacs012Message.CancellationStatus.APPROVED);
            pacs012Message.setEffectiveDate(LocalDate.now().plusDays(1));
            assertFalse(pacs012Message.isEffective());
        }

        @Test
        @DisplayName("Should not be effective when not approved")
        void shouldNotBeEffectiveWhenNotApproved() {
            pacs012Message.setCancellationStatus(Pacs012Message.CancellationStatus.PENDING);
            assertFalse(pacs012Message.isEffective());
        }
    }

    @Nested
    @DisplayName("Enum Tests")
    class EnumTests {

        @Test
        @DisplayName("Should get cancellation reason code")
        void shouldGetCancellationReasonCode() {
            assertEquals("CUST", Pacs012Message.CancellationReason.CUSTOMER_REQUEST.getCode());
            assertEquals("ACCT", Pacs012Message.CancellationReason.ACCOUNT_CLOSED.getCode());
            assertEquals("FUND", Pacs012Message.CancellationReason.INSUFFICIENT_FUNDS.getCode());
            assertEquals("AUTH", Pacs012Message.CancellationReason.UNAUTHORIZED.getCode());
            assertEquals("DUPL", Pacs012Message.CancellationReason.DUPLICATE_MANDATE.getCode());
            assertEquals("TECH", Pacs012Message.CancellationReason.TECHNICAL_ERROR.getCode());
            assertEquals("COMP", Pacs012Message.CancellationReason.COMPLIANCE_ISSUE.getCode());
            assertEquals("EXPR", Pacs012Message.CancellationReason.MANDATE_EXPIRED.getCode());
            assertEquals("CRED", Pacs012Message.CancellationReason.CREDITOR_REQUEST.getCode());
            assertEquals("BANK", Pacs012Message.CancellationReason.BANK_REQUEST.getCode());
            assertEquals("OTHR", Pacs012Message.CancellationReason.OTHER.getCode());
        }

        @Test
        @DisplayName("Should get cancellation status code")
        void shouldGetCancellationStatusCode() {
            assertEquals("PEND", Pacs012Message.CancellationStatus.PENDING.getCode());
            assertEquals("APPR", Pacs012Message.CancellationStatus.APPROVED.getCode());
            assertEquals("REJT", Pacs012Message.CancellationStatus.REJECTED.getCode());
            assertEquals("CANC", Pacs012Message.CancellationStatus.CANCELLED.getCode());
            assertEquals("COMP", Pacs012Message.CancellationStatus.COMPLETED.getCode());
        }
    }

    @Nested
    @DisplayName("Setter and Getter Tests")
    class SetterAndGetterTests {

        @Test
        @DisplayName("Should set and get cancellation date")
        void shouldSetAndGetCancellationDate() {
            LocalDate cancellationDate = LocalDate.now();
            pacs012Message.setCancellationDate(cancellationDate);
            assertEquals(cancellationDate, pacs012Message.getCancellationDate());
        }

        @Test
        @DisplayName("Should set and get effective date")
        void shouldSetAndGetEffectiveDate() {
            LocalDate effectiveDate = LocalDate.now().plusDays(30);
            pacs012Message.setEffectiveDate(effectiveDate);
            assertEquals(effectiveDate, pacs012Message.getEffectiveDate());
        }

        @Test
        @DisplayName("Should set and get debtor account ID")
        void shouldSetAndGetDebtorAccountId() {
            String debtorAccountId = "DEBTOR-ACCOUNT-001";
            pacs012Message.setDebtorAccountId(debtorAccountId);
            assertEquals(debtorAccountId, pacs012Message.getDebtorAccountId());
        }

        @Test
        @DisplayName("Should set and get creditor account ID")
        void shouldSetAndGetCreditorAccountId() {
            String creditorAccountId = "CREDITOR-ACCOUNT-001";
            pacs012Message.setCreditorAccountId(creditorAccountId);
            assertEquals(creditorAccountId, pacs012Message.getCreditorAccountId());
        }

        @Test
        @DisplayName("Should set and get cancellation description")
        void shouldSetAndGetCancellationDescription() {
            String description = "Customer requested mandate cancellation";
            pacs012Message.setCancellationDescription(description);
            assertEquals(description, pacs012Message.getCancellationDescription());
        }

        @Test
        @DisplayName("Should set and get cancellation reference")
        void shouldSetAndGetCancellationReference() {
            String reference = "CANCEL-REF-001";
            pacs012Message.setCancellationReference(reference);
            assertEquals(reference, pacs012Message.getCancellationReference());
        }

        @Test
        @DisplayName("Should set and get clearing system ID")
        void shouldSetAndGetClearingSystemId() {
            String clearingSystemId = "CLEARING-001";
            pacs012Message.setClearingSystemId(clearingSystemId);
            assertEquals(clearingSystemId, pacs012Message.getClearingSystemId());
        }

        @Test
        @DisplayName("Should set and get settlement system ID")
        void shouldSetAndGetSettlementSystemId() {
            String settlementSystemId = "SETTLEMENT-001";
            pacs012Message.setSettlementSystemId(settlementSystemId);
            assertEquals(settlementSystemId, pacs012Message.getSettlementSystemId());
        }

        @Test
        @DisplayName("Should set and get debtor approval requirement")
        void shouldSetAndGetDebtorApprovalRequirement() {
            pacs012Message.setRequiresDebtorApproval(true);
            assertTrue(pacs012Message.isRequiresDebtorApproval());

            pacs012Message.setRequiresDebtorApproval(false);
            assertFalse(pacs012Message.isRequiresDebtorApproval());
        }

        @Test
        @DisplayName("Should set and get creditor approval requirement")
        void shouldSetAndGetCreditorApprovalRequirement() {
            pacs012Message.setRequiresCreditorApproval(true);
            assertTrue(pacs012Message.isRequiresCreditorApproval());

            pacs012Message.setRequiresCreditorApproval(false);
            assertFalse(pacs012Message.isRequiresCreditorApproval());
        }

        @Test
        @DisplayName("Should set and get immediate cancellation flag")
        void shouldSetAndGetImmediateCancellationFlag() {
            pacs012Message.setImmediateCancellation(true);
            assertTrue(pacs012Message.isImmediateCancellation());

            pacs012Message.setImmediateCancellation(false);
            assertFalse(pacs012Message.isImmediateCancellation());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(pacs012Message, pacs012Message);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, pacs012Message);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", pacs012Message);
        }

        @Test
        @DisplayName("Should not be equal to message with different creation time")
        void shouldNotBeEqualToMessageWithDifferentCreationTime() {
            Pacs012Message other = new Pacs012Message(ORIGINAL_MANDATE_ID,
                                                    Pacs012Message.CancellationReason.CUSTOMER_REQUEST,
                                                    "DEBTOR-001", "CREDITOR-001");

            // Should be different due to different creation times
            assertNotEquals(pacs012Message, other);
            assertNotEquals(pacs012Message.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to message with different message ID")
        void shouldNotBeEqualToMessageWithDifferentMessageId() {
            Pacs012Message other = new Pacs012Message(ORIGINAL_MANDATE_ID,
                                                    Pacs012Message.CancellationReason.CUSTOMER_REQUEST,
                                                    "DEBTOR-001", "CREDITOR-001");
            other.setMessageId("DIFFERENT-ID");

            assertNotEquals(pacs012Message, other);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void shouldIncludeKeyFieldsInToString() {
            String toString = pacs012Message.toString();

            assertTrue(toString.contains("Pacs012Message"));
            assertTrue(toString.contains(pacs012Message.getMessageId()));
            assertTrue(toString.contains(ORIGINAL_MANDATE_ID));
            assertTrue(toString.contains("CUSTOMER_REQUEST"));
            assertTrue(toString.contains("PENDING"));
            assertTrue(toString.contains("DEBTOR-001"));
            assertTrue(toString.contains("CREDITOR-001"));
        }

        @Test
        @DisplayName("Should include cancellation status in toString")
        void shouldIncludeCancellationStatusInToString() {
            pacs012Message.setCancellationStatus(Pacs012Message.CancellationStatus.APPROVED);
            String toString = pacs012Message.toString();

            assertTrue(toString.contains("APPROVED"));
        }
    }
}
