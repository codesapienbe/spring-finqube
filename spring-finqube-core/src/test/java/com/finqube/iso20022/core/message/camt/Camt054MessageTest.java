package com.finqube.iso20022.core.message.camt;

import java.math.BigDecimal;
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
 * Unit tests for Camt054Message (Debit/Credit Notification).
 *
 * <p>Tests cover validation, transaction management, and business logic for
 * ISO 20022 camt.054 messages.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
@DisplayName("Camt054Message Tests")
class Camt054MessageTest {

    private Camt054Message camt054Message;
    private static final String ACCOUNT_ID = "ACCOUNT-" + UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        camt054Message = new Camt054Message(ACCOUNT_ID, Camt054Message.NotificationType.BOTH);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should initialize required fields")
        void defaultConstructorShouldInitializeRequiredFields() {
            Camt054Message message = new Camt054Message();

            assertNotNull(message.getMessageId());
            assertTrue(message.getMessageId().startsWith("CAMT-"));
            assertNotNull(message.getCreationTime());
            assertEquals(Camt054Message.NotificationType.BOTH, message.getNotificationType());
        }

        @Test
        @DisplayName("Parameterized constructor should set provided values")
        void parameterizedConstructorShouldSetProvidedValues() {
            String accountId = "TEST-ACCOUNT-123";
            Camt054Message.NotificationType notificationType = Camt054Message.NotificationType.DEBIT;

            Camt054Message message = new Camt054Message(accountId, notificationType);

            assertEquals(accountId, message.getAccountId());
            assertEquals(notificationType, message.getNotificationType());
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
            assertEquals("camt.054", camt054Message.getMessageType());
        }

        @Test
        @DisplayName("Should return correct business process")
        void shouldReturnCorrectBusinessProcess() {
            assertEquals("camt", camt054Message.getBusinessProcess());
        }

        @Test
        @DisplayName("Should return high priority")
        void shouldReturnHighPriority() {
            assertEquals(com.finqube.iso20022.core.message.MessagePriority.HIGH,
                        camt054Message.getPriority());
        }

        @Test
        @DisplayName("Should return correct schema version")
        void shouldReturnCorrectSchemaVersion() {
            assertEquals("001.001.11", camt054Message.getSchemaVersion());
        }

        @Test
        @DisplayName("Should require acknowledgment")
        void shouldRequireAcknowledgment() {
            assertTrue(camt054Message.requiresAcknowledgment());
        }

        @Test
        @DisplayName("Should return correct description")
        void shouldReturnCorrectDescription() {
            String description = camt054Message.getDescription();
            assertTrue(description.contains("Debit/Credit Notification"));
            assertTrue(description.contains(ACCOUNT_ID));
            assertTrue(description.contains("BOTH"));
        }

        @Test
        @DisplayName("Should return empty transactions list initially")
        void shouldReturnEmptyTransactionsListInitially() {
            var transactions = camt054Message.getTransactions();
            assertTrue(transactions.isEmpty());
        }

        @Test
        @DisplayName("Should return correct transaction count")
        void shouldReturnCorrectTransactionCount() {
            assertEquals(0, camt054Message.getTransactionCount());
        }

        @Test
        @DisplayName("Should return zero total amount initially")
        void shouldReturnZeroTotalAmountInitially() {
            assertEquals(0.0, camt054Message.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid message should pass validation")
        void validMessageShouldPassValidation() {
            assertTrue(camt054Message.validate());
        }

        @Test
        @DisplayName("Should throw exception when message ID is null")
        void shouldThrowExceptionWhenMessageIdIsNull() {
            camt054Message.setMessageId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> camt054Message.validate()
            );

            assertEquals("Message ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when account ID is null")
        void shouldThrowExceptionWhenAccountIdIsNull() {
            camt054Message.setAccountId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> camt054Message.validate()
            );

            assertEquals("Account ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when notification type is null")
        void shouldThrowExceptionWhenNotificationTypeIsNull() {
            camt054Message.setNotificationType(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> camt054Message.validate()
            );

            assertEquals("Notification type is required", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Transaction Management Tests")
    class TransactionManagementTests {

        @Test
        @DisplayName("Should add transaction with all parameters")
        void shouldAddTransactionWithAllParameters() {
            LocalDateTime valueDate = LocalDateTime.now();
            LocalDateTime bookingDate = LocalDateTime.now().plusDays(1);
            BigDecimal amount = new BigDecimal("1000.00");

            camt054Message.addTransaction("TXN-001", Camt054Message.TransactionType.DEBIT, "USD",
                                        amount, valueDate, bookingDate, "Test debit", "CP-001", "REF-001");

            var transactions = camt054Message.getTransactionsList();
            assertEquals(1, transactions.size());

            var transaction = transactions.get(0);
            assertEquals("TXN-001", transaction.getTransactionId());
            assertEquals(Camt054Message.TransactionType.DEBIT, transaction.getType());
            assertEquals("USD", transaction.getCurrency());
            assertEquals(amount, transaction.getAmount());
            assertEquals(valueDate, transaction.getValueDate());
            assertEquals(bookingDate, transaction.getBookingDate());
            assertEquals("Test debit", transaction.getDescription());
            assertEquals("CP-001", transaction.getCounterpartyId());
            assertEquals("REF-001", transaction.getReference());
        }

        @Test
        @DisplayName("Should add transaction with current dates")
        void shouldAddTransactionWithCurrentDates() {
            BigDecimal amount = new BigDecimal("500.00");

            camt054Message.addTransaction("TXN-002", Camt054Message.TransactionType.CREDIT, "EUR",
                                        amount, "Test credit", "CP-002", "REF-002");

            var transactions = camt054Message.getTransactionsList();
            assertEquals(1, transactions.size());

            var transaction = transactions.get(0);
            assertEquals("TXN-002", transaction.getTransactionId());
            assertEquals(Camt054Message.TransactionType.CREDIT, transaction.getType());
            assertEquals("EUR", transaction.getCurrency());
            assertEquals(amount, transaction.getAmount());
            assertNotNull(transaction.getValueDate());
            assertNotNull(transaction.getBookingDate());
        }

        @Test
        @DisplayName("Should get transaction by ID")
        void shouldGetTransactionById() {
            camt054Message.addTransaction("TXN-003", Camt054Message.TransactionType.DEBIT, "GBP",
                                        new BigDecimal("750.00"), "Test transaction", "CP-003", "REF-003");

            var transaction = camt054Message.getTransaction("TXN-003");

            assertNotNull(transaction);
            assertEquals("TXN-003", transaction.getTransactionId());
            assertEquals(Camt054Message.TransactionType.DEBIT, transaction.getType());
        }

        @Test
        @DisplayName("Should return null for non-existent transaction")
        void shouldReturnNullForNonExistentTransaction() {
            var transaction = camt054Message.getTransaction("NON-EXISTENT");
            assertNull(transaction);
        }

        @Test
        @DisplayName("Should return immutable transaction list")
        void shouldReturnImmutableTransactionList() {
            var transactions = camt054Message.getTransactionsList();

            assertThrows(UnsupportedOperationException.class, () -> {
                transactions.add(null);
            });
        }
    }

    @Nested
    @DisplayName("Amount Management Tests")
    class AmountManagementTests {

        @Test
        @DisplayName("Should calculate total debit amount")
        void shouldCalculateTotalDebitAmount() {
            camt054Message.addTransaction("TXN-001", Camt054Message.TransactionType.DEBIT, "USD",
                                        new BigDecimal("100.00"), "Debit 1", "CP-001", "REF-001");
            camt054Message.addTransaction("TXN-002", Camt054Message.TransactionType.DEBIT, "USD",
                                        new BigDecimal("200.00"), "Debit 2", "CP-002", "REF-002");

            assertEquals(new BigDecimal("300.00"), camt054Message.getTotalDebitAmount());
        }

        @Test
        @DisplayName("Should calculate total credit amount")
        void shouldCalculateTotalCreditAmount() {
            camt054Message.addTransaction("TXN-003", Camt054Message.TransactionType.CREDIT, "EUR",
                                        new BigDecimal("500.00"), "Credit 1", "CP-003", "REF-003");
            camt054Message.addTransaction("TXN-004", Camt054Message.TransactionType.CREDIT, "EUR",
                                        new BigDecimal("300.00"), "Credit 2", "CP-004", "REF-004");

            assertEquals(new BigDecimal("800.00"), camt054Message.getTotalCreditAmount());
        }

        @Test
        @DisplayName("Should calculate net amount")
        void shouldCalculateNetAmount() {
            camt054Message.addTransaction("TXN-001", Camt054Message.TransactionType.DEBIT, "USD",
                                        new BigDecimal("100.00"), "Debit", "CP-001", "REF-001");
            camt054Message.addTransaction("TXN-002", Camt054Message.TransactionType.CREDIT, "USD",
                                        new BigDecimal("300.00"), "Credit", "CP-002", "REF-002");

            assertEquals(new BigDecimal("200.00"), camt054Message.getNetAmount());
        }

        @Test
        @DisplayName("Should update total amount when transactions are added")
        void shouldUpdateTotalAmountWhenTransactionsAreAdded() {
            camt054Message.addTransaction("TXN-001", Camt054Message.TransactionType.DEBIT, "USD",
                                        new BigDecimal("100.00"), "Debit", "CP-001", "REF-001");
            camt054Message.addTransaction("TXN-002", Camt054Message.TransactionType.CREDIT, "USD",
                                        new BigDecimal("300.00"), "Credit", "CP-002", "REF-002");

            assertEquals(400.0, camt054Message.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Transaction Analysis Tests")
    class TransactionAnalysisTests {

        @Test
        @DisplayName("Should detect debit transactions")
        void shouldDetectDebitTransactions() {
            camt054Message.addTransaction("TXN-001", Camt054Message.TransactionType.DEBIT, "USD",
                                        new BigDecimal("100.00"), "Debit", "CP-001", "REF-001");

            assertTrue(camt054Message.hasDebitTransactions());
            assertFalse(camt054Message.hasCreditTransactions());
        }

        @Test
        @DisplayName("Should detect credit transactions")
        void shouldDetectCreditTransactions() {
            camt054Message.addTransaction("TXN-002", Camt054Message.TransactionType.CREDIT, "EUR",
                                        new BigDecimal("200.00"), "Credit", "CP-002", "REF-002");

            assertTrue(camt054Message.hasCreditTransactions());
            assertFalse(camt054Message.hasDebitTransactions());
        }

        @Test
        @DisplayName("Should count debit transactions")
        void shouldCountDebitTransactions() {
            camt054Message.addTransaction("TXN-001", Camt054Message.TransactionType.DEBIT, "USD",
                                        new BigDecimal("100.00"), "Debit 1", "CP-001", "REF-001");
            camt054Message.addTransaction("TXN-002", Camt054Message.TransactionType.DEBIT, "USD",
                                        new BigDecimal("200.00"), "Debit 2", "CP-002", "REF-002");
            camt054Message.addTransaction("TXN-003", Camt054Message.TransactionType.CREDIT, "USD",
                                        new BigDecimal("300.00"), "Credit", "CP-003", "REF-003");

            assertEquals(2, camt054Message.getDebitTransactionCount());
        }

        @Test
        @DisplayName("Should count credit transactions")
        void shouldCountCreditTransactions() {
            camt054Message.addTransaction("TXN-001", Camt054Message.TransactionType.CREDIT, "EUR",
                                        new BigDecimal("100.00"), "Credit 1", "CP-001", "REF-001");
            camt054Message.addTransaction("TXN-002", Camt054Message.TransactionType.CREDIT, "EUR",
                                        new BigDecimal("200.00"), "Credit 2", "CP-002", "REF-002");
            camt054Message.addTransaction("TXN-003", Camt054Message.TransactionType.DEBIT, "EUR",
                                        new BigDecimal("300.00"), "Debit", "CP-003", "REF-003");

            assertEquals(2, camt054Message.getCreditTransactionCount());
        }
    }

    @Nested
    @DisplayName("Enum Tests")
    class EnumTests {

        @Test
        @DisplayName("Should get notification type code")
        void shouldGetNotificationTypeCode() {
            assertEquals("DBIT", Camt054Message.NotificationType.DEBIT.getCode());
            assertEquals("CRDT", Camt054Message.NotificationType.CREDIT.getCode());
            assertEquals("BOTH", Camt054Message.NotificationType.BOTH.getCode());
        }

        @Test
        @DisplayName("Should get transaction type code")
        void shouldGetTransactionTypeCode() {
            assertEquals("DBIT", Camt054Message.TransactionType.DEBIT.getCode());
            assertEquals("CRDT", Camt054Message.TransactionType.CREDIT.getCode());
            assertEquals("REVR", Camt054Message.TransactionType.REVERSAL.getCode());
            assertEquals("CORR", Camt054Message.TransactionType.CORRECTION.getCode());
        }
    }

    @Nested
    @DisplayName("Setter and Getter Tests")
    class SetterAndGetterTests {

        @Test
        @DisplayName("Should set and get notification date time")
        void shouldSetAndGetNotificationDateTime() {
            LocalDateTime notificationTime = LocalDateTime.now();
            camt054Message.setNotificationDateTime(notificationTime);
            assertEquals(notificationTime, camt054Message.getNotificationDateTime());
        }

        @Test
        @DisplayName("Should set and get account owner ID")
        void shouldSetAndGetAccountOwnerId() {
            String ownerId = "OWNER-001";
            camt054Message.setAccountOwnerId(ownerId);
            assertEquals(ownerId, camt054Message.getAccountOwnerId());
        }

        @Test
        @DisplayName("Should set and get account servicer ID")
        void shouldSetAndGetAccountServicerId() {
            String servicerId = "SERVICER-001";
            camt054Message.setAccountServicerId(servicerId);
            assertEquals(servicerId, camt054Message.getAccountServicerId());
        }

        @Test
        @DisplayName("Should set and get account description")
        void shouldSetAndGetAccountDescription() {
            String description = "Test account for notifications";
            camt054Message.setAccountDescription(description);
            assertEquals(description, camt054Message.getAccountDescription());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(camt054Message, camt054Message);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, camt054Message);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", camt054Message);
        }

        @Test
        @DisplayName("Should not be equal to message with different creation time")
        void shouldNotBeEqualToMessageWithDifferentCreationTime() {
            Camt054Message other = new Camt054Message(ACCOUNT_ID, Camt054Message.NotificationType.BOTH);

            // Should be different due to different creation times
            assertNotEquals(camt054Message, other);
            assertNotEquals(camt054Message.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to message with different message ID")
        void shouldNotBeEqualToMessageWithDifferentMessageId() {
            Camt054Message other = new Camt054Message(ACCOUNT_ID, Camt054Message.NotificationType.BOTH);
            other.setMessageId("DIFFERENT-ID");

            assertNotEquals(camt054Message, other);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void shouldIncludeKeyFieldsInToString() {
            String toString = camt054Message.toString();

            assertTrue(toString.contains("Camt054Message"));
            assertTrue(toString.contains(camt054Message.getMessageId()));
            assertTrue(toString.contains(ACCOUNT_ID));
            assertTrue(toString.contains("BOTH"));
        }

        @Test
        @DisplayName("Should include transaction count in toString")
        void shouldIncludeTransactionCountInToString() {
            camt054Message.addTransaction("TXN-001", Camt054Message.TransactionType.DEBIT, "USD",
                                        new BigDecimal("100.00"), "Test", "CP-001", "REF-001");
            String toString = camt054Message.toString();

            assertTrue(toString.contains("transactionCount=1"));
        }

        @Test
        @DisplayName("Should include amount totals in toString")
        void shouldIncludeAmountTotalsInToString() {
            camt054Message.addTransaction("TXN-001", Camt054Message.TransactionType.DEBIT, "USD",
                                        new BigDecimal("100.00"), "Debit", "CP-001", "REF-001");
            camt054Message.addTransaction("TXN-002", Camt054Message.TransactionType.CREDIT, "USD",
                                        new BigDecimal("200.00"), "Credit", "CP-002", "REF-002");
            String toString = camt054Message.toString();

            assertTrue(toString.contains("totalDebitAmount=100"));
            assertTrue(toString.contains("totalCreditAmount=200"));
        }
    }
}
