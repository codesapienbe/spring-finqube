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
 * Unit tests for Pacs013Message (Direct Debit Mandate Information).
 *
 * <p>Tests cover validation, mandate information management, and business logic for
 * ISO 20022 pacs.013 messages.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
@DisplayName("Pacs013Message Tests")
class Pacs013MessageTest {

    private Pacs013Message pacs013Message;
    private static final String REQUEST_ID = "REQ-" + UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        pacs013Message = new Pacs013Message(REQUEST_ID,
                                          Pacs013Message.InformationType.REQUEST,
                                          "DEBTOR-001", "CREDITOR-001");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should initialize required fields")
        void defaultConstructorShouldInitializeRequiredFields() {
            Pacs013Message message = new Pacs013Message();

            assertNotNull(message.getMessageId());
            assertTrue(message.getMessageId().startsWith("PACS-"));
            assertNotNull(message.getCreationTime());
            assertEquals(Pacs013Message.InformationType.REQUEST, message.getInformationType());
            assertEquals(Pacs013Message.InformationStatus.PENDING, message.getInformationStatus());
        }

        @Test
        @DisplayName("Parameterized constructor should set provided values")
        void parameterizedConstructorShouldSetProvidedValues() {
            String requestId = "REQ-123";
            Pacs013Message.InformationType informationType = Pacs013Message.InformationType.RESPONSE;
            String debtorId = "DEBTOR-002";
            String creditorId = "CREDITOR-002";

            Pacs013Message message = new Pacs013Message(requestId, informationType, debtorId, creditorId);

            assertEquals(requestId, message.getRequestId());
            assertEquals(informationType, message.getInformationType());
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
            assertEquals("pacs.013", pacs013Message.getMessageType());
        }

        @Test
        @DisplayName("Should return correct business process")
        void shouldReturnCorrectBusinessProcess() {
            assertEquals("pacs", pacs013Message.getBusinessProcess());
        }

        @Test
        @DisplayName("Should return normal priority")
        void shouldReturnNormalPriority() {
            assertEquals(com.finqube.iso20022.core.message.MessagePriority.NORMAL,
                        pacs013Message.getPriority());
        }

        @Test
        @DisplayName("Should return correct schema version")
        void shouldReturnCorrectSchemaVersion() {
            assertEquals("001.001.11", pacs013Message.getSchemaVersion());
        }

        @Test
        @DisplayName("Should require acknowledgment for requests")
        void shouldRequireAcknowledgmentForRequests() {
            pacs013Message.setInformationType(Pacs013Message.InformationType.REQUEST);
            assertTrue(pacs013Message.requiresAcknowledgment());
        }

        @Test
        @DisplayName("Should not require acknowledgment for responses")
        void shouldNotRequireAcknowledgmentForResponses() {
            pacs013Message.setInformationType(Pacs013Message.InformationType.RESPONSE);
            assertFalse(pacs013Message.requiresAcknowledgment());
        }

        @Test
        @DisplayName("Should return correct description")
        void shouldReturnCorrectDescription() {
            String description = pacs013Message.getDescription();
            assertTrue(description.contains("Direct Debit Mandate Information"));
            assertTrue(description.contains(REQUEST_ID));
            assertTrue(description.contains("REQUEST"));
            assertTrue(description.contains("PENDING"));
        }

        @Test
        @DisplayName("Should return empty transactions list initially")
        void shouldReturnEmptyTransactionsListInitially() {
            var transactions = pacs013Message.getTransactions();
            assertTrue(transactions.isEmpty());
        }

        @Test
        @DisplayName("Should return correct transaction count")
        void shouldReturnCorrectTransactionCount() {
            assertEquals(0, pacs013Message.getTransactionCount());
        }

        @Test
        @DisplayName("Should return zero as total amount")
        void shouldReturnZeroAsTotalAmount() {
            assertEquals(0.0, pacs013Message.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid message should pass validation")
        void validMessageShouldPassValidation() {
            assertTrue(pacs013Message.validate());
        }

        @Test
        @DisplayName("Should throw exception when message ID is null")
        void shouldThrowExceptionWhenMessageIdIsNull() {
            pacs013Message.setMessageId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs013Message.validate()
            );

            assertEquals("Message ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when request ID is null")
        void shouldThrowExceptionWhenRequestIdIsNull() {
            pacs013Message.setRequestId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs013Message.validate()
            );

            assertEquals("Request ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when information type is null")
        void shouldThrowExceptionWhenInformationTypeIsNull() {
            pacs013Message.setInformationType(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs013Message.validate()
            );

            assertEquals("Information type is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when information status is null")
        void shouldThrowExceptionWhenInformationStatusIsNull() {
            pacs013Message.setInformationStatus(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs013Message.validate()
            );

            assertEquals("Information status is required", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Information Type Tests")
    class InformationTypeTests {

        @Test
        @DisplayName("Should correctly identify request")
        void shouldCorrectlyIdentifyRequest() {
            pacs013Message.setInformationType(Pacs013Message.InformationType.REQUEST);
            assertTrue(pacs013Message.isRequest());
            assertFalse(pacs013Message.isResponse());
            assertFalse(pacs013Message.isNotification());
            assertFalse(pacs013Message.isConfirmation());
        }

        @Test
        @DisplayName("Should correctly identify response")
        void shouldCorrectlyIdentifyResponse() {
            pacs013Message.setInformationType(Pacs013Message.InformationType.RESPONSE);
            assertTrue(pacs013Message.isResponse());
            assertFalse(pacs013Message.isRequest());
            assertFalse(pacs013Message.isNotification());
            assertFalse(pacs013Message.isConfirmation());
        }

        @Test
        @DisplayName("Should correctly identify notification")
        void shouldCorrectlyIdentifyNotification() {
            pacs013Message.setInformationType(Pacs013Message.InformationType.NOTIFICATION);
            assertTrue(pacs013Message.isNotification());
            assertFalse(pacs013Message.isRequest());
            assertFalse(pacs013Message.isResponse());
            assertFalse(pacs013Message.isConfirmation());
        }

        @Test
        @DisplayName("Should correctly identify confirmation")
        void shouldCorrectlyIdentifyConfirmation() {
            pacs013Message.setInformationType(Pacs013Message.InformationType.CONFIRMATION);
            assertTrue(pacs013Message.isConfirmation());
            assertFalse(pacs013Message.isRequest());
            assertFalse(pacs013Message.isResponse());
            assertFalse(pacs013Message.isNotification());
        }
    }

    @Nested
    @DisplayName("Information Status Tests")
    class InformationStatusTests {

        @Test
        @DisplayName("Should correctly identify pending status")
        void shouldCorrectlyIdentifyPendingStatus() {
            pacs013Message.setInformationStatus(Pacs013Message.InformationStatus.PENDING);
            assertTrue(pacs013Message.isPending());
            assertFalse(pacs013Message.isProvided());
            assertFalse(pacs013Message.isNotAvailable());
            assertFalse(pacs013Message.isRejected());
            assertFalse(pacs013Message.isCancelled());
            assertFalse(pacs013Message.isCompleted());
        }

        @Test
        @DisplayName("Should correctly identify provided status")
        void shouldCorrectlyIdentifyProvidedStatus() {
            pacs013Message.setInformationStatus(Pacs013Message.InformationStatus.PROVIDED);
            assertTrue(pacs013Message.isProvided());
            assertFalse(pacs013Message.isPending());
            assertFalse(pacs013Message.isNotAvailable());
            assertFalse(pacs013Message.isRejected());
            assertFalse(pacs013Message.isCancelled());
            assertFalse(pacs013Message.isCompleted());
        }

        @Test
        @DisplayName("Should correctly identify not available status")
        void shouldCorrectlyIdentifyNotAvailableStatus() {
            pacs013Message.setInformationStatus(Pacs013Message.InformationStatus.NOT_AVAILABLE);
            assertTrue(pacs013Message.isNotAvailable());
            assertFalse(pacs013Message.isPending());
            assertFalse(pacs013Message.isProvided());
            assertFalse(pacs013Message.isRejected());
            assertFalse(pacs013Message.isCancelled());
            assertFalse(pacs013Message.isCompleted());
        }

        @Test
        @DisplayName("Should correctly identify rejected status")
        void shouldCorrectlyIdentifyRejectedStatus() {
            pacs013Message.setInformationStatus(Pacs013Message.InformationStatus.REJECTED);
            assertTrue(pacs013Message.isRejected());
            assertFalse(pacs013Message.isPending());
            assertFalse(pacs013Message.isProvided());
            assertFalse(pacs013Message.isNotAvailable());
            assertFalse(pacs013Message.isCancelled());
            assertFalse(pacs013Message.isCompleted());
        }

        @Test
        @DisplayName("Should correctly identify cancelled status")
        void shouldCorrectlyIdentifyCancelledStatus() {
            pacs013Message.setInformationStatus(Pacs013Message.InformationStatus.CANCELLED);
            assertTrue(pacs013Message.isCancelled());
            assertFalse(pacs013Message.isPending());
            assertFalse(pacs013Message.isProvided());
            assertFalse(pacs013Message.isNotAvailable());
            assertFalse(pacs013Message.isRejected());
            assertFalse(pacs013Message.isCompleted());
        }

        @Test
        @DisplayName("Should correctly identify completed status")
        void shouldCorrectlyIdentifyCompletedStatus() {
            pacs013Message.setInformationStatus(Pacs013Message.InformationStatus.COMPLETED);
            assertTrue(pacs013Message.isCompleted());
            assertFalse(pacs013Message.isPending());
            assertFalse(pacs013Message.isProvided());
            assertFalse(pacs013Message.isNotAvailable());
            assertFalse(pacs013Message.isRejected());
            assertFalse(pacs013Message.isCancelled());
        }
    }

    @Nested
    @DisplayName("Mandate Information Management Tests")
    class MandateInformationManagementTests {

        @Test
        @DisplayName("Should add mandate information")
        void shouldAddMandateInformation() {
            Pacs013Message.MandateInformation info = new Pacs013Message.MandateInformation(
                "MANDATE-123", "RECURRING", "ACTIVE", LocalDate.now(), LocalDate.now().plusYears(1),
                new BigDecimal("1000.00"), "USD", "MONTHLY", "DEBTOR-001", "CREDITOR-001",
                "DEBTOR-ACC-001", "CREDITOR-ACC-001", "Test mandate", LocalDate.now().minusDays(30),
                new BigDecimal("100.00"), 5, new BigDecimal("500.00")
            );

            pacs013Message.addMandateInformation(info);

            assertEquals(1, pacs013Message.getTransactionCount());
            assertEquals(1, pacs013Message.getTransactions().size());
            assertEquals("MANDATE-123", pacs013Message.getTransactions().get(0));
            assertEquals(new BigDecimal("1000.00"), pacs013Message.getTotalAmountRequested());
        }

        @Test
        @DisplayName("Should get mandate information by ID")
        void shouldGetMandateInformationById() {
            Pacs013Message.MandateInformation info = new Pacs013Message.MandateInformation(
                "MANDATE-123", "RECURRING", "ACTIVE", LocalDate.now(), LocalDate.now().plusYears(1),
                new BigDecimal("1000.00"), "USD", "MONTHLY", "DEBTOR-001", "CREDITOR-001",
                "DEBTOR-ACC-001", "CREDITOR-ACC-001", "Test mandate", LocalDate.now().minusDays(30),
                new BigDecimal("100.00"), 5, new BigDecimal("500.00")
            );

            pacs013Message.addMandateInformation(info);

            Pacs013Message.MandateInformation found = pacs013Message.getMandateInformation("MANDATE-123");
            assertNotNull(found);
            assertEquals("MANDATE-123", found.getMandateId());
            assertEquals("RECURRING", found.getMandateType());
            assertEquals("ACTIVE", found.getMandateStatus());
        }

        @Test
        @DisplayName("Should return null for non-existent mandate information")
        void shouldReturnNullForNonExistentMandateInformation() {
            Pacs013Message.MandateInformation found = pacs013Message.getMandateInformation("NON-EXISTENT");
            assertNull(found);
        }

        @Test
        @DisplayName("Should return immutable mandate information list")
        void shouldReturnImmutableMandateInformationList() {
            Pacs013Message.MandateInformation info = new Pacs013Message.MandateInformation(
                "MANDATE-123", "RECURRING", "ACTIVE", LocalDate.now(), LocalDate.now().plusYears(1),
                new BigDecimal("1000.00"), "USD", "MONTHLY", "DEBTOR-001", "CREDITOR-001",
                "DEBTOR-ACC-001", "CREDITOR-ACC-001", "Test mandate", LocalDate.now().minusDays(30),
                new BigDecimal("100.00"), 5, new BigDecimal("500.00")
            );

            pacs013Message.addMandateInformation(info);

            var list = pacs013Message.getMandateInformationList();
            assertThrows(UnsupportedOperationException.class, () -> list.add(info));
        }

        @Test
        @DisplayName("Should update total amount when mandate information is added")
        void shouldUpdateTotalAmountWhenMandateInformationIsAdded() {
            Pacs013Message.MandateInformation info1 = new Pacs013Message.MandateInformation(
                "MANDATE-123", "RECURRING", "ACTIVE", LocalDate.now(), LocalDate.now().plusYears(1),
                new BigDecimal("1000.00"), "USD", "MONTHLY", "DEBTOR-001", "CREDITOR-001",
                "DEBTOR-ACC-001", "CREDITOR-ACC-001", "Test mandate 1", LocalDate.now().minusDays(30),
                new BigDecimal("100.00"), 5, new BigDecimal("500.00")
            );

            Pacs013Message.MandateInformation info2 = new Pacs013Message.MandateInformation(
                "MANDATE-456", "ONE_OFF", "ACTIVE", LocalDate.now(), LocalDate.now().plusMonths(6),
                new BigDecimal("500.00"), "USD", "ONE_TIME", "DEBTOR-002", "CREDITOR-002",
                "DEBTOR-ACC-002", "CREDITOR-ACC-002", "Test mandate 2", LocalDate.now().minusDays(15),
                new BigDecimal("500.00"), 1, new BigDecimal("500.00")
            );

            pacs013Message.addMandateInformation(info1);
            pacs013Message.addMandateInformation(info2);

            assertEquals(new BigDecimal("1500.00"), pacs013Message.getTotalAmountRequested());
            assertEquals(1500.0, pacs013Message.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Enum Tests")
    class EnumTests {

        @Test
        @DisplayName("Should get information type code")
        void shouldGetInformationTypeCode() {
            assertEquals("REQ", Pacs013Message.InformationType.REQUEST.getCode());
            assertEquals("RES", Pacs013Message.InformationType.RESPONSE.getCode());
            assertEquals("NOT", Pacs013Message.InformationType.NOTIFICATION.getCode());
            assertEquals("CON", Pacs013Message.InformationType.CONFIRMATION.getCode());
        }

        @Test
        @DisplayName("Should get information status code")
        void shouldGetInformationStatusCode() {
            assertEquals("PEND", Pacs013Message.InformationStatus.PENDING.getCode());
            assertEquals("PROV", Pacs013Message.InformationStatus.PROVIDED.getCode());
            assertEquals("NAVA", Pacs013Message.InformationStatus.NOT_AVAILABLE.getCode());
            assertEquals("REJT", Pacs013Message.InformationStatus.REJECTED.getCode());
            assertEquals("CANC", Pacs013Message.InformationStatus.CANCELLED.getCode());
            assertEquals("COMP", Pacs013Message.InformationStatus.COMPLETED.getCode());
        }
    }

    @Nested
    @DisplayName("Setter and Getter Tests")
    class SetterAndGetterTests {

        @Test
        @DisplayName("Should set and get response date time")
        void shouldSetAndGetResponseDateTime() {
            LocalDateTime responseDateTime = LocalDateTime.now();
            pacs013Message.setResponseDateTime(responseDateTime);
            assertEquals(responseDateTime, pacs013Message.getResponseDateTime());
        }

        @Test
        @DisplayName("Should set and get requesting party ID")
        void shouldSetAndGetRequestingPartyId() {
            String requestingPartyId = "REQUESTING-PARTY-001";
            pacs013Message.setRequestingPartyId(requestingPartyId);
            assertEquals(requestingPartyId, pacs013Message.getRequestingPartyId());
        }

        @Test
        @DisplayName("Should set and get responding party ID")
        void shouldSetAndGetRespondingPartyId() {
            String respondingPartyId = "RESPONDING-PARTY-001";
            pacs013Message.setRespondingPartyId(respondingPartyId);
            assertEquals(respondingPartyId, pacs013Message.getRespondingPartyId());
        }

        @Test
        @DisplayName("Should set and get mandate ID")
        void shouldSetAndGetMandateId() {
            String mandateId = "MANDATE-123";
            pacs013Message.setMandateId(mandateId);
            assertEquals(mandateId, pacs013Message.getMandateId());
        }

        @Test
        @DisplayName("Should set and get request reason")
        void shouldSetAndGetRequestReason() {
            String requestReason = "Customer requested mandate information";
            pacs013Message.setRequestReason(requestReason);
            assertEquals(requestReason, pacs013Message.getRequestReason());
        }

        @Test
        @DisplayName("Should set and get response reason")
        void shouldSetAndGetResponseReason() {
            String responseReason = "Mandate information provided as requested";
            pacs013Message.setResponseReason(responseReason);
            assertEquals(responseReason, pacs013Message.getResponseReason());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(pacs013Message, pacs013Message);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, pacs013Message);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", pacs013Message);
        }

        @Test
        @DisplayName("Should not be equal to message with different creation time")
        void shouldNotBeEqualToMessageWithDifferentCreationTime() {
            Pacs013Message other = new Pacs013Message(REQUEST_ID,
                                                    Pacs013Message.InformationType.REQUEST,
                                                    "DEBTOR-001", "CREDITOR-001");

            // Should be different due to different creation times
            assertNotEquals(pacs013Message, other);
            assertNotEquals(pacs013Message.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to message with different message ID")
        void shouldNotBeEqualToMessageWithDifferentMessageId() {
            Pacs013Message other = new Pacs013Message(REQUEST_ID,
                                                    Pacs013Message.InformationType.REQUEST,
                                                    "DEBTOR-001", "CREDITOR-001");
            other.setMessageId("DIFFERENT-ID");

            assertNotEquals(pacs013Message, other);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void shouldIncludeKeyFieldsInToString() {
            String toString = pacs013Message.toString();

            assertTrue(toString.contains("Pacs013Message"));
            assertTrue(toString.contains(pacs013Message.getMessageId()));
            assertTrue(toString.contains(REQUEST_ID));
            assertTrue(toString.contains("REQUEST"));
            assertTrue(toString.contains("PENDING"));
            assertTrue(toString.contains("DEBTOR-001"));
            assertTrue(toString.contains("CREDITOR-001"));
        }

        @Test
        @DisplayName("Should include information status in toString")
        void shouldIncludeInformationStatusInToString() {
            pacs013Message.setInformationStatus(Pacs013Message.InformationStatus.PROVIDED);
            String toString = pacs013Message.toString();

            assertTrue(toString.contains("PROVIDED"));
        }
    }
}
