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
 * Unit tests for Camt052Message (Account Report).
 *
 * <p>Tests cover validation, balance management, and business logic for
 * ISO 20022 camt.052 messages.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
@DisplayName("Camt052Message Tests")
class Camt052MessageTest {

    private Camt052Message camt052Message;
    private static final String ACCOUNT_ID = "ACCOUNT-" + UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        camt052Message = new Camt052Message(ACCOUNT_ID, Camt052Message.ReportType.ACCOUNT_REPORT);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should initialize required fields")
        void defaultConstructorShouldInitializeRequiredFields() {
            Camt052Message message = new Camt052Message();

            assertNotNull(message.getMessageId());
            assertTrue(message.getMessageId().startsWith("CAMT-"));
            assertNotNull(message.getCreationTime());
            assertEquals(Camt052Message.ReportType.ACCOUNT_REPORT, message.getReportType());
        }

        @Test
        @DisplayName("Parameterized constructor should set provided values")
        void parameterizedConstructorShouldSetProvidedValues() {
            String accountId = "TEST-ACCOUNT-123";
            Camt052Message.ReportType reportType = Camt052Message.ReportType.ACCOUNT_OPENING;

            Camt052Message message = new Camt052Message(accountId, reportType);

            assertEquals(accountId, message.getAccountId());
            assertEquals(reportType, message.getReportType());
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
            assertEquals("camt.052", camt052Message.getMessageType());
        }

        @Test
        @DisplayName("Should return correct business process")
        void shouldReturnCorrectBusinessProcess() {
            assertEquals("camt", camt052Message.getBusinessProcess());
        }

        @Test
        @DisplayName("Should return normal priority")
        void shouldReturnNormalPriority() {
            assertEquals(com.finqube.iso20022.core.message.MessagePriority.NORMAL,
                        camt052Message.getPriority());
        }

        @Test
        @DisplayName("Should return correct schema version")
        void shouldReturnCorrectSchemaVersion() {
            assertEquals("001.001.11", camt052Message.getSchemaVersion());
        }

        @Test
        @DisplayName("Should not require acknowledgment")
        void shouldNotRequireAcknowledgment() {
            assertFalse(camt052Message.requiresAcknowledgment());
        }

        @Test
        @DisplayName("Should return correct description")
        void shouldReturnCorrectDescription() {
            String description = camt052Message.getDescription();
            assertTrue(description.contains("Account Report"));
            assertTrue(description.contains(ACCOUNT_ID));
            assertTrue(description.contains("ACCOUNT_REPORT"));
        }

        @Test
        @DisplayName("Should return empty transactions list initially")
        void shouldReturnEmptyTransactionsListInitially() {
            var transactions = camt052Message.getTransactions();
            assertTrue(transactions.isEmpty());
        }

        @Test
        @DisplayName("Should return correct transaction count")
        void shouldReturnCorrectTransactionCount() {
            assertEquals(0, camt052Message.getTransactionCount());
        }

        @Test
        @DisplayName("Should return zero total amount initially")
        void shouldReturnZeroTotalAmountInitially() {
            assertEquals(0.0, camt052Message.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid message should pass validation")
        void validMessageShouldPassValidation() {
            assertTrue(camt052Message.validate());
        }

        @Test
        @DisplayName("Should throw exception when message ID is null")
        void shouldThrowExceptionWhenMessageIdIsNull() {
            camt052Message.setMessageId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> camt052Message.validate()
            );

            assertEquals("Message ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when account ID is null")
        void shouldThrowExceptionWhenAccountIdIsNull() {
            camt052Message.setAccountId(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> camt052Message.validate()
            );

            assertEquals("Account ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when report type is null")
        void shouldThrowExceptionWhenReportTypeIsNull() {
            camt052Message.setReportType(null);

            MessageValidationException exception = assertThrows(
                MessageValidationException.class,
                () -> camt052Message.validate()
            );

            assertEquals("Report type is required", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Balance Management Tests")
    class BalanceManagementTests {

        @Test
        @DisplayName("Should add balance with date time")
        void shouldAddBalanceWithDateTime() {
            LocalDateTime balanceTime = LocalDateTime.now();
            BigDecimal amount = new BigDecimal("1000.00");

            camt052Message.addBalance(Camt052Message.BalanceType.AVAILABLE, "USD", amount, balanceTime);

            var balances = camt052Message.getBalances();
            assertEquals(1, balances.size());

            var balance = balances.get(0);
            assertEquals(Camt052Message.BalanceType.AVAILABLE, balance.getType());
            assertEquals("USD", balance.getCurrency());
            assertEquals(amount, balance.getAmount());
            assertEquals(balanceTime, balance.getDateTime());
        }

        @Test
        @DisplayName("Should add balance without date time")
        void shouldAddBalanceWithoutDateTime() {
            BigDecimal amount = new BigDecimal("2000.00");

            camt052Message.addBalance(Camt052Message.BalanceType.BOOKED, "EUR", amount);

            var balances = camt052Message.getBalances();
            assertEquals(1, balances.size());

            var balance = balances.get(0);
            assertEquals(Camt052Message.BalanceType.BOOKED, balance.getType());
            assertEquals("EUR", balance.getCurrency());
            assertEquals(amount, balance.getAmount());
            assertNotNull(balance.getDateTime());
        }

        @Test
        @DisplayName("Should get balance for specific type and currency")
        void shouldGetBalanceForSpecificTypeAndCurrency() {
            BigDecimal amount = new BigDecimal("1500.00");
            camt052Message.addBalance(Camt052Message.BalanceType.AVAILABLE, "USD", amount);

            var balance = camt052Message.getBalance(Camt052Message.BalanceType.AVAILABLE, "USD");

            assertNotNull(balance);
            assertEquals(amount, balance.getAmount());
        }

        @Test
        @DisplayName("Should return null for non-existent balance")
        void shouldReturnNullForNonExistentBalance() {
            var balance = camt052Message.getBalance(Camt052Message.BalanceType.AVAILABLE, "USD");
            assertNull(balance);
        }

        @Test
        @DisplayName("Should return immutable balance list")
        void shouldReturnImmutableBalanceList() {
            var balances = camt052Message.getBalances();

            assertThrows(UnsupportedOperationException.class, () -> {
                balances.add(null);
            });
        }
    }

    @Nested
    @DisplayName("Transaction Management Tests")
    class TransactionManagementTests {

        @Test
        @DisplayName("Should add transaction ID")
        void shouldAddTransactionId() {
            String transactionId = "TXN-001";
            camt052Message.addTransactionId(transactionId);

            var transactions = camt052Message.getTransactions();
            assertEquals(1, transactions.size());
            assertEquals(transactionId, transactions.get(0));
            assertEquals(1, camt052Message.getTransactionCount());
        }

        @Test
        @DisplayName("Should add multiple transaction IDs")
        void shouldAddMultipleTransactionIds() {
            camt052Message.addTransactionId("TXN-001");
            camt052Message.addTransactionId("TXN-002");
            camt052Message.addTransactionId("TXN-003");

            assertEquals(3, camt052Message.getTransactionCount());
            var transactions = camt052Message.getTransactions();
            assertEquals(3, transactions.size());
        }
    }

    @Nested
    @DisplayName("Amount Management Tests")
    class AmountManagementTests {

        @Test
        @DisplayName("Should set and get total amount")
        void shouldSetAndGetTotalAmount() {
            BigDecimal totalAmount = new BigDecimal("5000.00");
            camt052Message.setTotalAmount(totalAmount);

            assertEquals(totalAmount, camt052Message.getTotalAmountAsBigDecimal());
            assertEquals(5000.0, camt052Message.getTotalAmount());
        }

        @Test
        @DisplayName("Should handle zero total amount")
        void shouldHandleZeroTotalAmount() {
            camt052Message.setTotalAmount(BigDecimal.ZERO);

            assertEquals(BigDecimal.ZERO, camt052Message.getTotalAmountAsBigDecimal());
            assertEquals(0.0, camt052Message.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("Account Status Tests")
    class AccountStatusTests {

        @Test
        @DisplayName("Should set and get account status")
        void shouldSetAndGetAccountStatus() {
            camt052Message.setAccountStatus(Camt052Message.AccountStatus.ACTIVE);
            assertEquals(Camt052Message.AccountStatus.ACTIVE, camt052Message.getAccountStatus());
        }

        @Test
        @DisplayName("Should correctly identify active account")
        void shouldCorrectlyIdentifyActiveAccount() {
            camt052Message.setAccountStatus(Camt052Message.AccountStatus.ACTIVE);
            assertTrue(camt052Message.isActive());
            assertFalse(camt052Message.isBlocked());
            assertFalse(camt052Message.isClosed());
        }

        @Test
        @DisplayName("Should correctly identify blocked account")
        void shouldCorrectlyIdentifyBlockedAccount() {
            camt052Message.setAccountStatus(Camt052Message.AccountStatus.BLOCKED);
            assertTrue(camt052Message.isBlocked());
            assertFalse(camt052Message.isActive());
            assertFalse(camt052Message.isClosed());
        }

        @Test
        @DisplayName("Should correctly identify closed account")
        void shouldCorrectlyIdentifyClosedAccount() {
            camt052Message.setAccountStatus(Camt052Message.AccountStatus.CLOSED);
            assertTrue(camt052Message.isClosed());
            assertFalse(camt052Message.isActive());
            assertFalse(camt052Message.isBlocked());
        }
    }

    @Nested
    @DisplayName("Enum Tests")
    class EnumTests {

        @Test
        @DisplayName("Should get report type code")
        void shouldGetReportTypeCode() {
            assertEquals("ACCT", Camt052Message.ReportType.ACCOUNT_REPORT.getCode());
            assertEquals("OPEN", Camt052Message.ReportType.ACCOUNT_OPENING.getCode());
            assertEquals("CLOS", Camt052Message.ReportType.ACCOUNT_CLOSING.getCode());
        }

        @Test
        @DisplayName("Should get balance type code")
        void shouldGetBalanceTypeCode() {
            assertEquals("AVLB", Camt052Message.BalanceType.AVAILABLE.getCode());
            assertEquals("BKBD", Camt052Message.BalanceType.BOOKED.getCode());
            assertEquals("OPBD", Camt052Message.BalanceType.OPENING.getCode());
        }

        @Test
        @DisplayName("Should get account status code")
        void shouldGetAccountStatusCode() {
            assertEquals("ACTV", Camt052Message.AccountStatus.ACTIVE.getCode());
            assertEquals("BLKD", Camt052Message.AccountStatus.BLOCKED.getCode());
            assertEquals("CLOS", Camt052Message.AccountStatus.CLOSED.getCode());
        }
    }

    @Nested
    @DisplayName("Setter and Getter Tests")
    class SetterAndGetterTests {

        @Test
        @DisplayName("Should set and get report date time")
        void shouldSetAndGetReportDateTime() {
            LocalDateTime reportTime = LocalDateTime.now();
            camt052Message.setReportDateTime(reportTime);
            assertEquals(reportTime, camt052Message.getReportDateTime());
        }

        @Test
        @DisplayName("Should set and get account owner ID")
        void shouldSetAndGetAccountOwnerId() {
            String ownerId = "OWNER-001";
            camt052Message.setAccountOwnerId(ownerId);
            assertEquals(ownerId, camt052Message.getAccountOwnerId());
        }

        @Test
        @DisplayName("Should set and get account servicer ID")
        void shouldSetAndGetAccountServicerId() {
            String servicerId = "SERVICER-001";
            camt052Message.setAccountServicerId(servicerId);
            assertEquals(servicerId, camt052Message.getAccountServicerId());
        }

        @Test
        @DisplayName("Should set and get account description")
        void shouldSetAndGetAccountDescription() {
            String description = "Test account for unit testing";
            camt052Message.setAccountDescription(description);
            assertEquals(description, camt052Message.getAccountDescription());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(camt052Message, camt052Message);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, camt052Message);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", camt052Message);
        }

        @Test
        @DisplayName("Should not be equal to message with different creation time")
        void shouldNotBeEqualToMessageWithDifferentCreationTime() {
            Camt052Message other = new Camt052Message(ACCOUNT_ID, Camt052Message.ReportType.ACCOUNT_REPORT);

            // Should be different due to different creation times
            assertNotEquals(camt052Message, other);
            assertNotEquals(camt052Message.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to message with different message ID")
        void shouldNotBeEqualToMessageWithDifferentMessageId() {
            Camt052Message other = new Camt052Message(ACCOUNT_ID, Camt052Message.ReportType.ACCOUNT_REPORT);
            other.setMessageId("DIFFERENT-ID");

            assertNotEquals(camt052Message, other);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void shouldIncludeKeyFieldsInToString() {
            String toString = camt052Message.toString();

            assertTrue(toString.contains("Camt052Message"));
            assertTrue(toString.contains(camt052Message.getMessageId()));
            assertTrue(toString.contains(ACCOUNT_ID));
            assertTrue(toString.contains("ACCOUNT_REPORT"));
        }

        @Test
        @DisplayName("Should include balance count in toString")
        void shouldIncludeBalanceCountInToString() {
            camt052Message.addBalance(Camt052Message.BalanceType.AVAILABLE, "USD", new BigDecimal("1000.00"));
            String toString = camt052Message.toString();

            assertTrue(toString.contains("balanceCount=1"));
        }

        @Test
        @DisplayName("Should include transaction count in toString")
        void shouldIncludeTransactionCountInToString() {
            camt052Message.addTransactionId("TXN-001");
            camt052Message.addTransactionId("TXN-002");
            String toString = camt052Message.toString();

            assertTrue(toString.contains("transactionCount=2"));
        }
    }
}
