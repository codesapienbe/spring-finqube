package com.finqube.iso20022.core.message.pacs;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;
import com.finqube.iso20022.core.exception.MessageValidationException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * ISO 20022 pacs.009 Direct Debit message implementation.
 *
 * <p>This message is used in the Payment Clearing and Settlement (pacs) process to
 * initiate direct debit transactions. It provides detailed information about the
 * direct debit including mandate references, debtor and creditor details, and
 * transaction amounts.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Direct debit initiation</li>
 *   <li>Mandate reference management</li>
 *   <li>Debtor and creditor identification</li>
 *   <li>Transaction amount and currency</li>
 *   <li>Direct debit type and category</li>
 *   <li>Collection date and frequency</li>
 *   <li>Sequence type and mandate information</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * Pacs009Message directDebit = new Pacs009Message();
 * directDebit.setMessageId("PACS-" + UUID.randomUUID().toString());
 * directDebit.setMandateId("MANDATE-123");
 * directDebit.setDirectDebitType(DirectDebitType.FIRST);
 * directDebit.setAmount(new BigDecimal("500.00"));
 *
 * // Send via template
 * iso20022Template.sendMessage(directDebit);
 * }</pre>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 * @see BaseMessage
 * @see <a href="https://www.iso20022.org/standardsrepository/type/Pacs009">ISO 20022 pacs.009 Specification</a>
 */
public class Pacs009Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Pacs009Message.class);

    /**
     * Direct debit type enumeration for pacs.009 messages.
     */
    public enum DirectDebitType {
        /** First direct debit */
        FIRST("FRST"),
        /** Recurring direct debit */
        RECURRING("RCUR"),
        /** Final direct debit */
        FINAL("FNAL"),
        /** One-off direct debit */
        ONE_OFF("OOFF");

        private final String code;

        DirectDebitType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Sequence type enumeration for pacs.009 messages.
     */
    public enum SequenceType {
        /** First collection */
        FIRST("FRST"),
        /** Recurring collection */
        RECURRING("RCUR"),
        /** Final collection */
        FINAL("FNAL"),
        /** One-off collection */
        ONE_OFF("OOFF");

        private final String code;

        SequenceType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Category purpose enumeration for pacs.009 messages.
     */
    public enum CategoryPurpose {
        /** Cash management */
        CASH_MANAGEMENT("CASH"),
        /** Trade services */
        TRADE_SERVICES("TRAD"),
        /** Treasury operations */
        TREASURY("TREA"),
        /** Securities */
        SECURITIES("SECU"),
        /** Loan */
        LOAN("LOAN"),
        /** Insurance */
        INSURANCE("INSU"),
        /** Utility payment */
        UTILITY("UTIL"),
        /** Government payment */
        GOVERNMENT("GOVT"),
        /** Salary payment */
        SALARY("SALA"),
        /** Pension payment */
        PENSION("PENS"),
        /** Tax payment */
        TAX("TAX"),
        /** Subscription payment */
        SUBSCRIPTION("SUBS"),
        /** Membership fee */
        MEMBERSHIP("MEMB"),
        /** Service charge */
        SERVICE_CHARGE("SERV"),
        /** Maintenance fee */
        MAINTENANCE("MAIN"),
        /** Other */
        OTHER("OTHR");

        private final String code;

        CategoryPurpose(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Direct debit transaction information for pacs.009 messages.
     */
    public static class DirectDebitTransaction {
        private final String transactionId;
        private final BigDecimal amount;
        private final String currency;
        private final LocalDateTime collectionDate;
        private final String mandateId;
        private final DirectDebitType directDebitType;
        private final SequenceType sequenceType;
        private final String debtorId;
        private final String creditorId;
        private final String description;
        private final CategoryPurpose categoryPurpose;

        public DirectDebitTransaction(String transactionId, BigDecimal amount, String currency,
                                   LocalDateTime collectionDate, String mandateId, DirectDebitType directDebitType,
                                   SequenceType sequenceType, String debtorId, String creditorId,
                                   String description, CategoryPurpose categoryPurpose) {
            this.transactionId = transactionId;
            this.amount = amount;
            this.currency = currency;
            this.collectionDate = collectionDate;
            this.mandateId = mandateId;
            this.directDebitType = directDebitType;
            this.sequenceType = sequenceType;
            this.debtorId = debtorId;
            this.creditorId = creditorId;
            this.description = description;
            this.categoryPurpose = categoryPurpose;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public String getCurrency() {
            return currency;
        }

        public LocalDateTime getCollectionDate() {
            return collectionDate;
        }

        public String getMandateId() {
            return mandateId;
        }

        public DirectDebitType getDirectDebitType() {
            return directDebitType;
        }

        public SequenceType getSequenceType() {
            return sequenceType;
        }

        public String getDebtorId() {
            return debtorId;
        }

        public String getCreditorId() {
            return creditorId;
        }

        public String getDescription() {
            return description;
        }

        public CategoryPurpose getCategoryPurpose() {
            return categoryPurpose;
        }

        @Override
        public String toString() {
            return String.format("DirectDebitTransaction{transactionId='%s', amount=%s, currency='%s', " +
                               "mandateId='%s', directDebitType=%s, sequenceType=%s}",
                               transactionId, amount, currency, mandateId, directDebitType, sequenceType);
        }
    }

    @NotNull
    @Size(min = 1, max = 35)
    private String messageId;

    @NotNull
    @Size(min = 1, max = 35)
    private String mandateId;

    @NotNull
    private DirectDebitType directDebitType;

    @NotNull
    private SequenceType sequenceType;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private String currency;

    @Size(max = 500)
    private String description;

    @NotNull
    private LocalDateTime creationDateTime;

    private LocalDateTime collectionDate;

    @Size(max = 35)
    private String debtorId;

    @Size(max = 35)
    private String creditorId;

    private CategoryPurpose categoryPurpose;

    @Size(max = 35)
    private String clearingSystemId;

    @Size(max = 35)
    private String settlementSystemId;

    private final List<DirectDebitTransaction> transactions = new ArrayList<>();

    private BigDecimal totalTransactionAmount = BigDecimal.ZERO;

    /**
     * Default constructor initializes required fields.
     */
    public Pacs009Message() {
        this.messageId = "PACS-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.directDebitType = DirectDebitType.FIRST;
        this.sequenceType = SequenceType.FIRST;
        this.totalAmount = BigDecimal.ZERO;
        this.currency = "USD";
        log.debug("Created new Pacs009Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param mandateId the mandate identifier
     * @param directDebitType the type of direct debit
     * @param sequenceType the sequence type
     * @param totalAmount the total amount
     * @param currency the currency code
     */
    public Pacs009Message(String mandateId, DirectDebitType directDebitType,
                         SequenceType sequenceType, BigDecimal totalAmount, String currency) {
        this();
        this.mandateId = mandateId;
        this.directDebitType = directDebitType;
        this.sequenceType = sequenceType;
        this.totalAmount = totalAmount;
        this.currency = currency;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getMessageType() {
        return "pacs.009";
    }

    @Override
    public MessagePriority getPriority() {
        return MessagePriority.HIGH; // Direct debits are high priority
    }

    @Override
    public LocalDateTime getCreationTime() {
        return creationDateTime;
    }

    @Override
    public String getBusinessProcess() {
        return "pacs";
    }

    @Override
    public boolean validate() throws MessageValidationException {
        if (messageId == null || messageId.trim().isEmpty()) {
            throw new MessageValidationException("Message ID is required", messageId, getMessageType());
        }
        if (mandateId == null || mandateId.trim().isEmpty()) {
            throw new MessageValidationException("Mandate ID is required", messageId, getMessageType());
        }
        if (directDebitType == null) {
            throw new MessageValidationException("Direct debit type is required", messageId, getMessageType());
        }
        if (sequenceType == null) {
            throw new MessageValidationException("Sequence type is required", messageId, getMessageType());
        }
        if (totalAmount == null) {
            throw new MessageValidationException("Total amount is required", messageId, getMessageType());
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new MessageValidationException("Currency is required", messageId, getMessageType());
        }
        if (creationDateTime == null) {
            throw new MessageValidationException("Creation date time is required", messageId, getMessageType());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("Direct Debit for mandate %s with type %s and amount %s %s",
                           mandateId, directDebitType, totalAmount, currency);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return true; // Direct debits typically require acknowledgment
    }

    @Override
    public String getSchemaVersion() {
        return "001.001.11";
    }

    @Override
    public List<String> getTransactions() {
        return transactions.stream()
                .map(DirectDebitTransaction::getTransactionId)
                .toList();
    }

    @Override
    public int getTransactionCount() {
        return transactions.size();
    }

    @Override
    public double getTotalAmount() {
        return totalTransactionAmount.doubleValue();
    }

    /**
     * Sets the unique identifier for this direct debit message.
     *
     * @param messageId the message identifier (1-35 characters)
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the mandate identifier.
     *
     * @return the mandate ID
     */
    public String getMandateId() {
        return mandateId;
    }

    /**
     * Sets the mandate identifier.
     *
     * @param mandateId the mandate ID (1-35 characters)
     */
    public void setMandateId(String mandateId) {
        this.mandateId = mandateId;
    }

    /**
     * Gets the direct debit type.
     *
     * @return the direct debit type
     */
    public DirectDebitType getDirectDebitType() {
        return directDebitType;
    }

    /**
     * Sets the direct debit type.
     *
     * @param directDebitType the direct debit type
     */
    public void setDirectDebitType(DirectDebitType directDebitType) {
        this.directDebitType = directDebitType;
    }

    /**
     * Gets the sequence type.
     *
     * @return the sequence type
     */
    public SequenceType getSequenceType() {
        return sequenceType;
    }

    /**
     * Sets the sequence type.
     *
     * @param sequenceType the sequence type
     */
    public void setSequenceType(SequenceType sequenceType) {
        this.sequenceType = sequenceType;
    }

    /**
     * Gets the total amount.
     *
     * @return the total amount
     */
    public BigDecimal getTotalAmountAsBigDecimal() {
        return totalAmount;
    }

    /**
     * Sets the total amount.
     *
     * @param totalAmount the total amount
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Gets the currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency.
     *
     * @param currency the currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Sets the description.
     *
     * @param description the description (max 500 characters)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the collection date.
     *
     * @return the collection date
     */
    public LocalDateTime getCollectionDate() {
        return collectionDate;
    }

    /**
     * Sets the collection date.
     *
     * @param collectionDate the collection date
     */
    public void setCollectionDate(LocalDateTime collectionDate) {
        this.collectionDate = collectionDate;
    }

    /**
     * Gets the debtor identifier.
     *
     * @return the debtor ID
     */
    public String getDebtorId() {
        return debtorId;
    }

    /**
     * Sets the debtor identifier.
     *
     * @param debtorId the debtor ID (max 35 characters)
     */
    public void setDebtorId(String debtorId) {
        this.debtorId = debtorId;
    }

    /**
     * Gets the creditor identifier.
     *
     * @return the creditor ID
     */
    public String getCreditorId() {
        return creditorId;
    }

    /**
     * Sets the creditor identifier.
     *
     * @param creditorId the creditor ID (max 35 characters)
     */
    public void setCreditorId(String creditorId) {
        this.creditorId = creditorId;
    }

    /**
     * Gets the category purpose.
     *
     * @return the category purpose
     */
    public CategoryPurpose getCategoryPurpose() {
        return categoryPurpose;
    }

    /**
     * Sets the category purpose.
     *
     * @param categoryPurpose the category purpose
     */
    public void setCategoryPurpose(CategoryPurpose categoryPurpose) {
        this.categoryPurpose = categoryPurpose;
    }

    /**
     * Gets the clearing system identifier.
     *
     * @return the clearing system ID
     */
    public String getClearingSystemId() {
        return clearingSystemId;
    }

    /**
     * Sets the clearing system identifier.
     *
     * @param clearingSystemId the clearing system ID (max 35 characters)
     */
    public void setClearingSystemId(String clearingSystemId) {
        this.clearingSystemId = clearingSystemId;
    }

    /**
     * Gets the settlement system identifier.
     *
     * @return the settlement system ID
     */
    public String getSettlementSystemId() {
        return settlementSystemId;
    }

    /**
     * Sets the settlement system identifier.
     *
     * @param settlementSystemId the settlement system ID (max 35 characters)
     */
    public void setSettlementSystemId(String settlementSystemId) {
        this.settlementSystemId = settlementSystemId;
    }

    /**
     * Gets the list of direct debit transactions.
     *
     * @return the transactions list (immutable)
     */
    public List<DirectDebitTransaction> getTransactionsList() {
        return List.copyOf(transactions);
    }

    /**
     * Adds a direct debit transaction to this message.
     *
     * @param transactionId the transaction ID
     * @param amount the transaction amount
     * @param currency the currency code
     * @param collectionDate the collection date
     * @param mandateId the mandate ID
     * @param directDebitType the direct debit type
     * @param sequenceType the sequence type
     * @param debtorId the debtor ID
     * @param creditorId the creditor ID
     * @param description the transaction description
     * @param categoryPurpose the category purpose
     */
    public void addTransaction(String transactionId, BigDecimal amount, String currency,
                             LocalDateTime collectionDate, String mandateId, DirectDebitType directDebitType,
                             SequenceType sequenceType, String debtorId, String creditorId,
                             String description, CategoryPurpose categoryPurpose) {
        DirectDebitTransaction transaction = new DirectDebitTransaction(transactionId, amount, currency,
                                                                      collectionDate, mandateId, directDebitType,
                                                                      sequenceType, debtorId, creditorId,
                                                                      description, categoryPurpose);
        transactions.add(transaction);
        totalTransactionAmount = totalTransactionAmount.add(amount);
        log.debug("Added direct debit transaction {} for mandate {}", transaction, mandateId);
    }

    /**
     * Adds a direct debit transaction with current collection date.
     *
     * @param transactionId the transaction ID
     * @param amount the transaction amount
     * @param currency the currency code
     * @param mandateId the mandate ID
     * @param directDebitType the direct debit type
     * @param sequenceType the sequence type
     * @param debtorId the debtor ID
     * @param creditorId the creditor ID
     * @param description the transaction description
     * @param categoryPurpose the category purpose
     */
    public void addTransaction(String transactionId, BigDecimal amount, String currency,
                             String mandateId, DirectDebitType directDebitType, SequenceType sequenceType,
                             String debtorId, String creditorId, String description, CategoryPurpose categoryPurpose) {
        addTransaction(transactionId, amount, currency, LocalDateTime.now(), mandateId, directDebitType,
                      sequenceType, debtorId, creditorId, description, categoryPurpose);
    }

    /**
     * Gets the transaction for a specific ID.
     *
     * @param transactionId the transaction ID
     * @return the transaction, or null if not found
     */
    public DirectDebitTransaction getTransaction(String transactionId) {
        return transactions.stream()
                .filter(t -> t.getTransactionId().equals(transactionId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the total transaction amount.
     *
     * @return the total transaction amount
     */
    public BigDecimal getTotalTransactionAmount() {
        return totalTransactionAmount;
    }

    /**
     * Checks if this is a first direct debit.
     *
     * @return true if the direct debit type is FIRST
     */
    public boolean isFirstDirectDebit() {
        return DirectDebitType.FIRST.equals(directDebitType);
    }

    /**
     * Checks if this is a recurring direct debit.
     *
     * @return true if the direct debit type is RECURRING
     */
    public boolean isRecurringDirectDebit() {
        return DirectDebitType.RECURRING.equals(directDebitType);
    }

    /**
     * Checks if this is a final direct debit.
     *
     * @return true if the direct debit type is FINAL
     */
    public boolean isFinalDirectDebit() {
        return DirectDebitType.FINAL.equals(directDebitType);
    }

    /**
     * Checks if this is a one-off direct debit.
     *
     * @return true if the direct debit type is ONE_OFF
     */
    public boolean isOneOffDirectDebit() {
        return DirectDebitType.ONE_OFF.equals(directDebitType);
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pacs009Message that = (Pacs009Message) obj;
        return Objects.equals(messageId, that.messageId) &&
               Objects.equals(mandateId, that.mandateId) &&
               directDebitType == that.directDebitType &&
               Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, mandateId, directDebitType, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Pacs009Message{messageId='%s', mandateId='%s', directDebitType=%s, " +
                           "sequenceType=%s, totalAmount=%s, currency='%s', transactionCount=%d, creationDateTime=%s}",
                           messageId, mandateId, directDebitType, sequenceType, totalAmount,
                           currency, transactions.size(), creationDateTime);
    }
}
