package com.finqube.iso20022.core.message.pacs;

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
 * Unit tests for Pacs009Message (Direct Debit).
 *
 * <p>Tests cover validation, transaction management, and business logic for
 * ISO 20022 pacs.009 messages.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
@DisplayName("Pacs009Message Tests")
class Pacs009MessageTest {

    private Pacs009Message pacs009Message;
    private static final String MANDATE_ID = "MANDATE-" + UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        BigDecimal amount = new BigDecimal("1000.00");
        pacs009Message = new Pacs009Message(MANDATE_ID,
                                          Pacs009Message.DirectDebitType.FIRST,
                                          Pacs009Message.SequenceType.FIRST,
                                          amount, "USD");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should initialize required fields")
        void defaultConstructorShouldInitializeRequiredFields() {
            Pacs009Message message = new Pacs009Message();

            assertNotNull(message.getMessageId());
            assertTrue(message.getMessageId().startsWith("PACS-"));
            assertNotNull(message.getCreationTime());
            assertEquals(Pacs009Message.DirectDebitType.FIRST, message.getDirectDebitType());
            assertEquals(Pacs009Message.SequenceType.FIRST, message.getSequenceType());
            assertEquals(BigDecimal.ZERO, message.getTotalAmountAsBigDecimal());
            assertEquals("USD", message.getCurrency());
        }

        @Test
        @DisplayName("Parameterized constructor should set provided values")
        void parameterizedConstructorShouldSetProvidedValues() {
            String mandateId = "MANDATE-123";
            Pacs009Message.DirectDebitType directDebitType = Pacs009Message.DirectDebitType.RECURRING;
            Pacs009Message.SequenceType sequenceType = Pacs009Message.SequenceType.RECURRING;
            BigDecimal totalAmount = new BigDecimal("500.00");
            String currency = "EUR";

            Pacs009Message message = new Pacs009Message(mandateId, directDebitType, sequenceType, totalAmount, currency);

            assertEquals(mandateId, message.getMandateId());
            assertEquals(directDebitType, message.getDirectDebitType());
            assertEquals(sequenceType, message.getSequenceType());
            assertEquals(totalAmount, message.getTotalAmountAsBigDecimal());
            assertEquals(currency, message.getCurrency());
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
            assertEquals("pacs.009", pacs009Message.getMessageType());
        }

        @Test
        @DisplayName("Should return correct business process")
        void shouldReturnCorrectBusinessProcess() {
            assertEquals("pacs", pacs009Message.getBusinessProcess());
        }

        @Test
        @DisplayName("Should return high priority")
        void shouldReturnHighPriority() {
            assertEquals(com.finqube.iso20022.core.message.MessagePriority.HIGH,
                        pacs009Message.getPriority());
        }

        @Test
        @DisplayName("Should return correct schema version")
        void shouldReturnCorrectSchemaVersion() {
            assertEquals("001.001.11", pacs009Message.getSchemaVersion());
        }

        @Test
        @DisplayName("Should require acknowledgment")
        void shouldRequireAcknowledgment() {
            assertTrue(pacs009Message.requiresAcknowledgment());
        }

        @Test
        @DisplayName("Should return correct description")
        void shouldReturnCorrectDescription() {
            String description = pacs009Message.getDescription();
            assertTrue(description.contains("Direct Debit"));
            assertTrue(description.contains(MANDATE_ID));
            assertTrue(description.contains("FIRST"));
            assertTrue(description.contains("1000.00"));
            assertTrue(description.contains("USD"));
        }

        @Test
        @DisplayName("Should return empty transactions list initially")
        void shouldReturnEmptyTransactionsListInitially() {
            var transactions = pacs009Message.getTransactions();
            assertTrue(transactions.isEmpty());
        }

        @Test
        @DisplayName("Should return correct transaction count")
        void shouldReturnCorrectTransactionCount() {
            assertEquals(0, pacs009Message.getTransactionCount());
        }

        @Test
        @DisplayName("Should return total amount as total amount")
        void shouldReturnTotalAmountAsTotalAmount() {
            assertEquals(0.0, pacs009Message.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid message should pass validation")
        void validMessageShouldPassValidation() {
            assertTrue(pacs009Message.validate());
        }

        @Test
        @DisplayName("Should throw exception when message ID is null")
        void shouldThrowExceptionWhenMessageIdIsNull() {
            pacs009Message.setMessageId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs009Message.validate()
            );

            assertEquals("Message ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when mandate ID is null")
        void shouldThrowExceptionWhenMandateIdIsNull() {
            pacs009Message.setMandateId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs009Message.validate()
            );

            assertEquals("Mandate ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when direct debit type is null")
        void shouldThrowExceptionWhenDirectDebitTypeIsNull() {
            pacs009Message.setDirectDebitType(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs009Message.validate()
            );

            assertEquals("Direct debit type is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when sequence type is null")
        void shouldThrowExceptionWhenSequenceTypeIsNull() {
            pacs009Message.setSequenceType(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> pacs009Message.validate()
            );

            assertEquals("Sequence type is required", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Transaction Management Tests")
    class TransactionManagementTests {

        @Test
        @DisplayName("Should add transaction with all parameters")
        void shouldAddTransactionWithAllParameters() {
            LocalDateTime collectionDate = LocalDateTime.now();
            BigDecimal amount = new BigDecimal("500.00");

            pacs009Message.addTransaction("TXN-001", amount, "USD", collectionDate, "MANDATE-123",
                                        Pacs009Message.DirectDebitType.FIRST, Pacs009Message.SequenceType.FIRST,
                                        "DEBTOR-001", "CREDITOR-001", "Test transaction",
                                        Pacs009Message.CategoryPurpose.UTILITY);

            var transactions = pacs009Message.getTransactionsList();
            assertEquals(1, transactions.size());

            var transaction = transactions.get(0);
            assertEquals("TXN-001", transaction.getTransactionId());
            assertEquals(amount, transaction.getAmount());
            assertEquals("USD", transaction.getCurrency());
            assertEquals(collectionDate, transaction.getCollectionDate());
            assertEquals("MANDATE-123", transaction.getMandateId());
            assertEquals(Pacs009Message.DirectDebitType.FIRST, transaction.getDirectDebitType());
            assertEquals(Pacs009Message.SequenceType.FIRST, transaction.getSequenceType());
            assertEquals("DEBTOR-001", transaction.getDebtorId());
            assertEquals("CREDITOR-001", transaction.getCreditorId());
            assertEquals("Test transaction", transaction.getDescription());
            assertEquals(Pacs009Message.CategoryPurpose.UTILITY, transaction.getCategoryPurpose());
        }

        @Test
        @DisplayName("Should add transaction with current collection date")
        void shouldAddTransactionWithCurrentCollectionDate() {
            BigDecimal amount = new BigDecimal("300.00");

            pacs009Message.addTransaction("TXN-002", amount, "EUR", "MANDATE-456",
                                        Pacs009Message.DirectDebitType.RECURRING, Pacs009Message.SequenceType.RECURRING,
                                        "DEBTOR-002", "CREDITOR-002", "Recurring transaction",
                                        Pacs009Message.CategoryPurpose.SUBSCRIPTION);

            var transactions = pacs009Message.getTransactionsList();
            assertEquals(1, transactions.size());

            var transaction = transactions.get(0);
            assertEquals("TXN-002", transaction.getTransactionId());
            assertEquals(amount, transaction.getAmount());
            assertEquals("EUR", transaction.getCurrency());
            assertNotNull(transaction.getCollectionDate());
            assertEquals("MANDATE-456", transaction.getMandateId());
            assertEquals(Pacs009Message.DirectDebitType.RECURRING, transaction.getDirectDebitType());
            assertEquals(Pacs009Message.SequenceType.RECURRING, transaction.getSequenceType());
        }

        @Test
        @DisplayName("Should get transaction by ID")
        void shouldGetTransactionById() {
            pacs009Message.addTransaction("TXN-003", new BigDecimal("750.00"), "GBP", "MANDATE-789",
                                        Pacs009Message.DirectDebitType.FINAL, Pacs009Message.SequenceType.FINAL,
                                        "DEBTOR-003", "CREDITOR-003", "Final transaction",
                                        Pacs009Message.CategoryPurpose.LOAN);

            var transaction = pacs009Message.getTransaction("TXN-003");

            assertNotNull(transaction);
            assertEquals("TXN-003", transaction.getTransactionId());
            assertEquals(Pacs009Message.DirectDebitType.FINAL, transaction.getDirectDebitType());
        }

        @Test
        @DisplayName("Should return null for non-existent transaction")
        void shouldReturnNullForNonExistentTransaction() {
            var transaction = pacs009Message.getTransaction("NON-EXISTENT");
            assertNull(transaction);
        }

        @Test
        @DisplayName("Should return immutable transaction list")
        void shouldReturnImmutableTransactionList() {
            var transactions = pacs009Message.getTransactionsList();

            assertThrows(UnsupportedOperationException.class, () -> {
                transactions.add(null);
            });
        }
    }

    @Nested
    @DisplayName("Amount Management Tests")
    class AmountManagementTests {

        @Test
        @DisplayName("Should calculate total transaction amount")
        void shouldCalculateTotalTransactionAmount() {
            pacs009Message.addTransaction("TXN-001", new BigDecimal("100.00"), "USD", "MANDATE-123",
                                        Pacs009Message.DirectDebitType.FIRST, Pacs009Message.SequenceType.FIRST,
                                        "DEBTOR-001", "CREDITOR-001", "Transaction 1",
                                        Pacs009Message.CategoryPurpose.UTILITY);
            pacs009Message.addTransaction("TXN-002", new BigDecimal("200.00"), "USD", "MANDATE-456",
                                        Pacs009Message.DirectDebitType.RECURRING, Pacs009Message.SequenceType.RECURRING,
                                        "DEBTOR-002", "CREDITOR-002", "Transaction 2",
                                        Pacs009Message.CategoryPurpose.SUBSCRIPTION);

            assertEquals(new BigDecimal("300.00"), pacs009Message.getTotalTransactionAmount());
        }

        @Test
        @DisplayName("Should update total amount when transactions are added")
        void shouldUpdateTotalAmountWhenTransactionsAreAdded() {
            pacs009Message.addTransaction("TXN-001", new BigDecimal("100.00"), "USD", "MANDATE-123",
                                        Pacs009Message.DirectDebitType.FIRST, Pacs009Message.SequenceType.FIRST,
                                        "DEBTOR-001", "CREDITOR-001", "Transaction 1",
                                        Pacs009Message.CategoryPurpose.UTILITY);
            pacs009Message.addTransaction("TXN-002", new BigDecimal("200.00"), "USD", "MANDATE-456",
                                        Pacs009Message.DirectDebitType.RECURRING, Pacs009Message.SequenceType.RECURRING,
                                        "DEBTOR-002", "CREDITOR-002", "Transaction 2",
                                        Pacs009Message.CategoryPurpose.SUBSCRIPTION);

            assertEquals(300.0, pacs009Message.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Direct Debit Type Tests")
    class DirectDebitTypeTests {

        @Test
        @DisplayName("Should correctly identify first direct debit")
        void shouldCorrectlyIdentifyFirstDirectDebit() {
            pacs009Message.setDirectDebitType(Pacs009Message.DirectDebitType.FIRST);
            assertTrue(pacs009Message.isFirstDirectDebit());
            assertFalse(pacs009Message.isRecurringDirectDebit());
            assertFalse(pacs009Message.isFinalDirectDebit());
            assertFalse(pacs009Message.isOneOffDirectDebit());
        }

        @Test
        @DisplayName("Should correctly identify recurring direct debit")
        void shouldCorrectlyIdentifyRecurringDirectDebit() {
            pacs009Message.setDirectDebitType(Pacs009Message.DirectDebitType.RECURRING);
            assertTrue(pacs009Message.isRecurringDirectDebit());
            assertFalse(pacs009Message.isFirstDirectDebit());
            assertFalse(pacs009Message.isFinalDirectDebit());
            assertFalse(pacs009Message.isOneOffDirectDebit());
        }

        @Test
        @DisplayName("Should correctly identify final direct debit")
        void shouldCorrectlyIdentifyFinalDirectDebit() {
            pacs009Message.setDirectDebitType(Pacs009Message.DirectDebitType.FINAL);
            assertTrue(pacs009Message.isFinalDirectDebit());
            assertFalse(pacs009Message.isFirstDirectDebit());
            assertFalse(pacs009Message.isRecurringDirectDebit());
            assertFalse(pacs009Message.isOneOffDirectDebit());
        }

        @Test
        @DisplayName("Should correctly identify one-off direct debit")
        void shouldCorrectlyIdentifyOneOffDirectDebit() {
            pacs009Message.setDirectDebitType(Pacs009Message.DirectDebitType.ONE_OFF);
            assertTrue(pacs009Message.isOneOffDirectDebit());
            assertFalse(pacs009Message.isFirstDirectDebit());
            assertFalse(pacs009Message.isRecurringDirectDebit());
            assertFalse(pacs009Message.isFinalDirectDebit());
        }
    }

    @Nested
    @DisplayName("Enum Tests")
    class EnumTests {

        @Test
        @DisplayName("Should get direct debit type code")
        void shouldGetDirectDebitTypeCode() {
            assertEquals("FRST", Pacs009Message.DirectDebitType.FIRST.getCode());
            assertEquals("RCUR", Pacs009Message.DirectDebitType.RECURRING.getCode());
            assertEquals("FNAL", Pacs009Message.DirectDebitType.FINAL.getCode());
            assertEquals("OOFF", Pacs009Message.DirectDebitType.ONE_OFF.getCode());
        }

        @Test
        @DisplayName("Should get sequence type code")
        void shouldGetSequenceTypeCode() {
            assertEquals("FRST", Pacs009Message.SequenceType.FIRST.getCode());
            assertEquals("RCUR", Pacs009Message.SequenceType.RECURRING.getCode());
            assertEquals("FNAL", Pacs009Message.SequenceType.FINAL.getCode());
            assertEquals("OOFF", Pacs009Message.SequenceType.ONE_OFF.getCode());
        }

        @Test
        @DisplayName("Should get category purpose code")
        void shouldGetCategoryPurposeCode() {
            assertEquals("CASH", Pacs009Message.CategoryPurpose.CASH_MANAGEMENT.getCode());
            assertEquals("TRAD", Pacs009Message.CategoryPurpose.TRADE_SERVICES.getCode());
            assertEquals("UTIL", Pacs009Message.CategoryPurpose.UTILITY.getCode());
            assertEquals("SUBS", Pacs009Message.CategoryPurpose.SUBSCRIPTION.getCode());
            assertEquals("OTHR", Pacs009Message.CategoryPurpose.OTHER.getCode());
        }
    }

    @Nested
    @DisplayName("Setter and Getter Tests")
    class SetterAndGetterTests {

        @Test
        @DisplayName("Should set and get collection date")
        void shouldSetAndGetCollectionDate() {
            LocalDateTime collectionDate = LocalDateTime.now();
            pacs009Message.setCollectionDate(collectionDate);
            assertEquals(collectionDate, pacs009Message.getCollectionDate());
        }

        @Test
        @DisplayName("Should set and get debtor ID")
        void shouldSetAndGetDebtorId() {
            String debtorId = "DEBTOR-001";
            pacs009Message.setDebtorId(debtorId);
            assertEquals(debtorId, pacs009Message.getDebtorId());
        }

        @Test
        @DisplayName("Should set and get creditor ID")
        void shouldSetAndGetCreditorId() {
            String creditorId = "CREDITOR-001";
            pacs009Message.setCreditorId(creditorId);
            assertEquals(creditorId, pacs009Message.getCreditorId());
        }

        @Test
        @DisplayName("Should set and get category purpose")
        void shouldSetAndGetCategoryPurpose() {
            Pacs009Message.CategoryPurpose categoryPurpose = Pacs009Message.CategoryPurpose.UTILITY;
            pacs009Message.setCategoryPurpose(categoryPurpose);
            assertEquals(categoryPurpose, pacs009Message.getCategoryPurpose());
        }

        @Test
        @DisplayName("Should set and get clearing system ID")
        void shouldSetAndGetClearingSystemId() {
            String clearingSystemId = "CLEARING-001";
            pacs009Message.setClearingSystemId(clearingSystemId);
            assertEquals(clearingSystemId, pacs009Message.getClearingSystemId());
        }

        @Test
        @DisplayName("Should set and get settlement system ID")
        void shouldSetAndGetSettlementSystemId() {
            String settlementSystemId = "SETTLEMENT-001";
            pacs009Message.setSettlementSystemId(settlementSystemId);
            assertEquals(settlementSystemId, pacs009Message.getSettlementSystemId());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(pacs009Message, pacs009Message);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, pacs009Message);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", pacs009Message);
        }

        @Test
        @DisplayName("Should not be equal to message with different creation time")
        void shouldNotBeEqualToMessageWithDifferentCreationTime() {
            Pacs009Message other = new Pacs009Message(MANDATE_ID,
                                                    Pacs009Message.DirectDebitType.FIRST,
                                                    Pacs009Message.SequenceType.FIRST,
                                                    new BigDecimal("1000.00"), "USD");

            // Should be different due to different creation times
            assertNotEquals(pacs009Message, other);
            assertNotEquals(pacs009Message.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to message with different message ID")
        void shouldNotBeEqualToMessageWithDifferentMessageId() {
            Pacs009Message other = new Pacs009Message(MANDATE_ID,
                                                    Pacs009Message.DirectDebitType.FIRST,
                                                    Pacs009Message.SequenceType.FIRST,
                                                    new BigDecimal("1000.00"), "USD");
            other.setMessageId("DIFFERENT-ID");

            assertNotEquals(pacs009Message, other);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void shouldIncludeKeyFieldsInToString() {
            String toString = pacs009Message.toString();

            assertTrue(toString.contains("Pacs009Message"));
            assertTrue(toString.contains(pacs009Message.getMessageId()));
            assertTrue(toString.contains(MANDATE_ID));
            assertTrue(toString.contains("FIRST"));
        }

        @Test
        @DisplayName("Should include transaction count in toString")
        void shouldIncludeTransactionCountInToString() {
            pacs009Message.addTransaction("TXN-001", new BigDecimal("100.00"), "USD", "MANDATE-123",
                                        Pacs009Message.DirectDebitType.FIRST, Pacs009Message.SequenceType.FIRST,
                                        "DEBTOR-001", "CREDITOR-001", "Test transaction",
                                        Pacs009Message.CategoryPurpose.UTILITY);
            String toString = pacs009Message.toString();

            assertTrue(toString.contains("transactionCount=1"));
        }

        @Test
        @DisplayName("Should include total amount and currency in toString")
        void shouldIncludeTotalAmountAndCurrencyInToString() {
            String toString = pacs009Message.toString();

            assertTrue(toString.contains("totalAmount="));
            assertTrue(toString.contains("currency='USD'"));
        }
    }
}
