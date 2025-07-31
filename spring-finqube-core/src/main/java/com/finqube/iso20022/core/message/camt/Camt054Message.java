package com.finqube.iso20022.core.message.camt;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.finqube.iso20022.core.exception.MessageValidationException;
import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * ISO 20022 camt.054 Debit/Credit Notification message implementation.
 *
 * <p>This message is used in the Cash Management (camt) process to notify about
 * debit and credit entries on an account. It provides detailed information about
 * individual transactions that have been posted to an account.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Debit and credit transaction notifications</li>
 *   <li>Detailed transaction information</li>
 *   <li>Account identification and details</li>
 *   <li>Transaction amounts and currencies</li>
 *   <li>Transaction dates and references</li>
 *   <li>Counterparty information</li>
 *   <li>Transaction categorization</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * Camt054Message notification = new Camt054Message();
 * notification.setMessageId("CAMT-" + UUID.randomUUID().toString());
 * notification.setAccountId("ACCOUNT-123");
 * notification.setNotificationType(NotificationType.DEBIT);
 * notification.addTransaction("TXN-001", "DEBIT", "USD", new BigDecimal("100.00"));
 *
 * // Send via template
 * iso20022Template.sendMessage(notification);
 * }</pre>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 * @see BaseMessage
 * @see <a href="https://www.iso20022.org/standardsrepository/type/Camt054">ISO 20022 camt.054 Specification</a>
 */
public class Camt054Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Camt054Message.class);

    /**
     * Notification type enumeration for camt.054 messages.
     */
    public enum NotificationType {
        /** Debit transaction notification */
        DEBIT("DBIT"),
        /** Credit transaction notification */
        CREDIT("CRDT"),
        /** Both debit and credit notifications */
        BOTH("BOTH");

        private final String code;

        NotificationType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Transaction type enumeration for camt.054 messages.
     */
    public enum TransactionType {
        /** Debit transaction */
        DEBIT("DBIT"),
        /** Credit transaction */
        CREDIT("CRDT"),
        /** Reversal transaction */
        REVERSAL("REVR"),
        /** Correction transaction */
        CORRECTION("CORR");

        private final String code;

        TransactionType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Transaction information for camt.054 messages.
     */
    public static class Transaction {
        private final String transactionId;
        private final TransactionType type;
        private final String currency;
        private final BigDecimal amount;
        private final LocalDateTime valueDate;
        private final LocalDateTime bookingDate;
        private final String description;
        private final String counterpartyId;
        private final String reference;

        public Transaction(String transactionId, TransactionType type, String currency,
                         BigDecimal amount, LocalDateTime valueDate, LocalDateTime bookingDate,
                         String description, String counterpartyId, String reference) {
            this.transactionId = transactionId;
            this.type = type;
            this.currency = currency;
            this.amount = amount;
            this.valueDate = valueDate;
            this.bookingDate = bookingDate;
            this.description = description;
            this.counterpartyId = counterpartyId;
            this.reference = reference;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public TransactionType getType() {
            return type;
        }

        public String getCurrency() {
            return currency;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public LocalDateTime getValueDate() {
            return valueDate;
        }

        public LocalDateTime getBookingDate() {
            return bookingDate;
        }

        public String getDescription() {
            return description;
        }

        public String getCounterpartyId() {
            return counterpartyId;
        }

        public String getReference() {
            return reference;
        }

        @Override
        public String toString() {
            return String.format("Transaction{transactionId='%s', type=%s, currency='%s', " +
                               "amount=%s, valueDate=%s, bookingDate=%s}",
                               transactionId, type, currency, amount, valueDate, bookingDate);
        }
    }

    @NotNull
    @Size(min = 1, max = 35)
    private String messageId;

    @NotNull
    @Size(min = 1, max = 35)
    private String accountId;

    @NotNull
    private NotificationType notificationType;

    @NotNull
    private LocalDateTime creationDateTime;

    private LocalDateTime notificationDateTime;

    @Size(max = 35)
    private String accountOwnerId;

    @Size(max = 35)
    private String accountServicerId;

    @Size(max = 500)
    private String accountDescription;

    private final List<Transaction> transactions = new ArrayList<>();

    private BigDecimal totalDebitAmount = BigDecimal.ZERO;

    private BigDecimal totalCreditAmount = BigDecimal.ZERO;

    /**
     * Default constructor initializes required fields.
     */
    public Camt054Message() {
        this.messageId = "CAMT-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.notificationType = NotificationType.BOTH;
        log.debug("Created new Camt054Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param accountId the account identifier
     * @param notificationType the type of notification
     */
    public Camt054Message(String accountId, NotificationType notificationType) {
        this();
        this.accountId = accountId;
        this.notificationType = notificationType;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getMessageType() {
        return "camt.054";
    }

    @Override
    public MessagePriority getPriority() {
        return MessagePriority.HIGH; // Debit/Credit notifications are high priority
    }

    @Override
    public LocalDateTime getCreationTime() {
        return creationDateTime;
    }

    @Override
    public String getBusinessProcess() {
        return "camt";
    }

    @Override
    public boolean validate() throws MessageValidationException {
        if (messageId == null || messageId.trim().isEmpty()) {
            throw new MessageValidationException("Message ID is required", messageId, getMessageType());
        }
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new MessageValidationException("Account ID is required", messageId, getMessageType());
        }
        if (notificationType == null) {
            throw new MessageValidationException("Notification type is required", messageId, getMessageType());
        }
        if (creationDateTime == null) {
            throw new MessageValidationException("Creation date time is required", messageId, getMessageType());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("Debit/Credit Notification for account %s with notification type %s",
                           accountId, notificationType);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return true; // Debit/Credit notifications typically require acknowledgment
    }

    @Override
    public String getSchemaVersion() {
        return "001.001.11";
    }

    @Override
    public List<String> getTransactions() {
        return transactions.stream()
                .map(Transaction::getTransactionId)
                .toList();
    }

    @Override
    public int getTransactionCount() {
        return transactions.size();
    }

    @Override
    public double getTotalAmount() {
        return totalDebitAmount.add(totalCreditAmount).doubleValue();
    }

    /**
     * Sets the unique identifier for this notification message.
     *
     * @param messageId the message identifier (1-35 characters)
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the account identifier.
     *
     * @return the account ID
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Sets the account identifier.
     *
     * @param accountId the account ID (1-35 characters)
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * Gets the notification type.
     *
     * @return the notification type
     */
    public NotificationType getNotificationType() {
        return notificationType;
    }

    /**
     * Sets the notification type.
     *
     * @param notificationType the notification type
     */
    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    /**
     * Gets the timestamp when the notification was generated.
     *
     * @return the notification timestamp
     */
    public LocalDateTime getNotificationDateTime() {
        return notificationDateTime;
    }

    /**
     * Sets the timestamp when the notification was generated.
     *
     * @param notificationDateTime the notification timestamp
     */
    public void setNotificationDateTime(LocalDateTime notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }

    /**
     * Gets the account owner identifier.
     *
     * @return the account owner ID
     */
    public String getAccountOwnerId() {
        return accountOwnerId;
    }

    /**
     * Sets the account owner identifier.
     *
     * @param accountOwnerId the account owner ID (max 35 characters)
     */
    public void setAccountOwnerId(String accountOwnerId) {
        this.accountOwnerId = accountOwnerId;
    }

    /**
     * Gets the account servicer identifier.
     *
     * @return the account servicer ID
     */
    public String getAccountServicerId() {
        return accountServicerId;
    }

    /**
     * Sets the account servicer identifier.
     *
     * @param accountServicerId the account servicer ID (max 35 characters)
     */
    public void setAccountServicerId(String accountServicerId) {
        this.accountServicerId = accountServicerId;
    }

    /**
     * Gets the account description.
     *
     * @return the account description
     */
    public String getAccountDescription() {
        return accountDescription;
    }

    /**
     * Sets the account description.
     *
     * @param accountDescription the account description (max 500 characters)
     */
    public void setAccountDescription(String accountDescription) {
        this.accountDescription = accountDescription;
    }

    /**
     * Gets the list of transactions.
     *
     * @return the transactions list (immutable)
     */
    public List<Transaction> getTransactionsList() {
        return List.copyOf(transactions);
    }

    /**
     * Adds a transaction to this notification.
     *
     * @param transactionId the transaction ID
     * @param type the transaction type
     * @param currency the currency code
     * @param amount the transaction amount
     * @param valueDate the value date
     * @param bookingDate the booking date
     * @param description the transaction description
     * @param counterpartyId the counterparty ID
     * @param reference the transaction reference
     */
    public void addTransaction(String transactionId, TransactionType type, String currency,
                             BigDecimal amount, LocalDateTime valueDate, LocalDateTime bookingDate,
                             String description, String counterpartyId, String reference) {
        Transaction transaction = new Transaction(transactionId, type, currency, amount,
                                                valueDate, bookingDate, description, counterpartyId, reference);
        transactions.add(transaction);

        // Update totals
        if (TransactionType.DEBIT.equals(type)) {
            totalDebitAmount = totalDebitAmount.add(amount);
        } else if (TransactionType.CREDIT.equals(type)) {
            totalCreditAmount = totalCreditAmount.add(amount);
        }

        log.debug("Added transaction {} for account {}", transaction, accountId);
    }

    /**
     * Adds a transaction to this notification with current dates.
     *
     * @param transactionId the transaction ID
     * @param type the transaction type
     * @param currency the currency code
     * @param amount the transaction amount
     * @param description the transaction description
     * @param counterpartyId the counterparty ID
     * @param reference the transaction reference
     */
    public void addTransaction(String transactionId, TransactionType type, String currency,
                             BigDecimal amount, String description, String counterpartyId, String reference) {
        LocalDateTime now = LocalDateTime.now();
        addTransaction(transactionId, type, currency, amount, now, now, description, counterpartyId, reference);
    }

    /**
     * Gets the transaction for a specific ID.
     *
     * @param transactionId the transaction ID
     * @return the transaction, or null if not found
     */
    public Transaction getTransaction(String transactionId) {
        return transactions.stream()
                .filter(t -> t.getTransactionId().equals(transactionId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the total debit amount.
     *
     * @return the total debit amount
     */
    public BigDecimal getTotalDebitAmount() {
        return totalDebitAmount;
    }

    /**
     * Gets the total credit amount.
     *
     * @return the total credit amount
     */
    public BigDecimal getTotalCreditAmount() {
        return totalCreditAmount;
    }

    /**
     * Gets the net amount (credit - debit).
     *
     * @return the net amount
     */
    public BigDecimal getNetAmount() {
        return totalCreditAmount.subtract(totalDebitAmount);
    }

    /**
     * Checks if this notification contains debit transactions.
     *
     * @return true if there are debit transactions
     */
    public boolean hasDebitTransactions() {
        return transactions.stream().anyMatch(t -> TransactionType.DEBIT.equals(t.getType()));
    }

    /**
     * Checks if this notification contains credit transactions.
     *
     * @return true if there are credit transactions
     */
    public boolean hasCreditTransactions() {
        return transactions.stream().anyMatch(t -> TransactionType.CREDIT.equals(t.getType()));
    }

    /**
     * Gets the count of debit transactions.
     *
     * @return the debit transaction count
     */
    public long getDebitTransactionCount() {
        return transactions.stream().filter(t -> TransactionType.DEBIT.equals(t.getType())).count();
    }

    /**
     * Gets the count of credit transactions.
     *
     * @return the credit transaction count
     */
    public long getCreditTransactionCount() {
        return transactions.stream().filter(t -> TransactionType.CREDIT.equals(t.getType())).count();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Camt054Message that = (Camt054Message) obj;
        return Objects.equals(messageId, that.messageId) &&
               Objects.equals(accountId, that.accountId) &&
               notificationType == that.notificationType &&
               Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, accountId, notificationType, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Camt054Message{messageId='%s', accountId='%s', notificationType=%s, " +
                           "transactionCount=%d, totalDebitAmount=%s, totalCreditAmount=%s, creationDateTime=%s}",
                           messageId, accountId, notificationType, transactions.size(),
                           totalDebitAmount, totalCreditAmount, creationDateTime);
    }
}
