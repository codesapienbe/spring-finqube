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
 * ISO 20022 camt.052 Account Report message implementation.
 *
 * <p>This message is used in the Cash Management (camt) process to report account
 * information including balances, transactions, and account details. It provides
 * comprehensive account reporting capabilities for financial institutions.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Account balance reporting</li>
 *   <li>Transaction history and details</li>
 *   <li>Account identification and details</li>
 *   <li>Balance types (opening, closing, available, booked)</li>
 *   <li>Currency and amount information</li>
 *   <li>Account status and restrictions</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * Camt052Message accountReport = new Camt052Message();
 * accountReport.setMessageId("CAMT-" + UUID.randomUUID().toString());
 * accountReport.setAccountId("ACCOUNT-123");
 * accountReport.setReportType(ReportType.ACCOUNT_REPORT);
 * accountReport.addBalance(BalanceType.AVAILABLE, "USD", new BigDecimal("10000.00"));
 *
 * // Send via template
 * iso20022Template.sendMessage(accountReport);
 * }</pre>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 * @see BaseMessage
 * @see <a href="https://www.iso20022.org/standardsrepository/type/Camt052">ISO 20022 camt.052 Specification</a>
 */
public class Camt052Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Camt052Message.class);

    /**
     * Report type enumeration for camt.052 messages.
     */
    public enum ReportType {
        /** Standard account report */
        ACCOUNT_REPORT("ACCT"),
        /** Account opening report */
        ACCOUNT_OPENING("OPEN"),
        /** Account closing report */
        ACCOUNT_CLOSING("CLOS"),
        /** Interim account report */
        INTERIM_REPORT("INTM"),
        /** Final account report */
        FINAL_REPORT("FINAL");

        private final String code;

        ReportType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Balance type enumeration for camt.052 messages.
     */
    public enum BalanceType {
        /** Opening balance */
        OPENING("OPBD"),
        /** Closing balance */
        CLOSING("CLBD"),
        /** Available balance */
        AVAILABLE("AVLB"),
        /** Booked balance */
        BOOKED("BKBD"),
        /** Current balance */
        CURRENT("CRRT"),
        /** Forward available balance */
        FORWARD_AVAILABLE("FWAV"),
        /** Blocked balance */
        BLOCKED("BLKD"),
        /** Reserved balance */
        RESERVED("RSVD");

        private final String code;

        BalanceType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Account status enumeration for camt.052 messages.
     */
    public enum AccountStatus {
        /** Account is active */
        ACTIVE("ACTV"),
        /** Account is inactive */
        INACTIVE("INAC"),
        /** Account is suspended */
        SUSPENDED("SUSP"),
        /** Account is closed */
        CLOSED("CLOS"),
        /** Account is blocked */
        BLOCKED("BLKD"),
        /** Account is restricted */
        RESTRICTED("REST");

        private final String code;

        AccountStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Balance information for camt.052 messages.
     */
    public static class Balance {
        private final BalanceType type;
        private final String currency;
        private final BigDecimal amount;
        private final LocalDateTime dateTime;

        public Balance(BalanceType type, String currency, BigDecimal amount, LocalDateTime dateTime) {
            this.type = type;
            this.currency = currency;
            this.amount = amount;
            this.dateTime = dateTime;
        }

        public BalanceType getType() {
            return type;
        }

        public String getCurrency() {
            return currency;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        @Override
        public String toString() {
            return String.format("Balance{type=%s, currency='%s', amount=%s, dateTime=%s}",
                               type, currency, amount, dateTime);
        }
    }

    @NotNull
    @Size(min = 1, max = 35)
    private String messageId;

    @NotNull
    @Size(min = 1, max = 35)
    private String accountId;

    @NotNull
    private ReportType reportType;

    @NotNull
    private LocalDateTime creationDateTime;

    private LocalDateTime reportDateTime;

    @Size(max = 35)
    private String accountOwnerId;

    @Size(max = 35)
    private String accountServicerId;

    private AccountStatus accountStatus;

    @Size(max = 500)
    private String accountDescription;

    private final List<Balance> balances = new ArrayList<>();

    private final List<String> transactionIds = new ArrayList<>();

    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * Default constructor initializes required fields.
     */
    public Camt052Message() {
        this.messageId = "CAMT-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.reportType = ReportType.ACCOUNT_REPORT;
        log.debug("Created new Camt052Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param accountId the account identifier
     * @param reportType the type of report
     */
    public Camt052Message(String accountId, ReportType reportType) {
        this();
        this.accountId = accountId;
        this.reportType = reportType;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getMessageType() {
        return "camt.052";
    }

    @Override
    public MessagePriority getPriority() {
        return MessagePriority.NORMAL;
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
        if (reportType == null) {
            throw new MessageValidationException("Report type is required", messageId, getMessageType());
        }
        if (creationDateTime == null) {
            throw new MessageValidationException("Creation date time is required", messageId, getMessageType());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("Account Report for account %s with report type %s",
                           accountId, reportType);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return false; // Account reports typically don't require acknowledgment
    }

    @Override
    public String getSchemaVersion() {
        return "001.001.11";
    }

    @Override
    public List<String> getTransactions() {
        return new ArrayList<>(transactionIds);
    }

    @Override
    public int getTransactionCount() {
        return transactionIds.size();
    }

    @Override
    public double getTotalAmount() {
        return totalAmount.doubleValue();
    }

    /**
     * Sets the unique identifier for this account report message.
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
     * Gets the report type.
     *
     * @return the report type
     */
    public ReportType getReportType() {
        return reportType;
    }

    /**
     * Sets the report type.
     *
     * @param reportType the report type
     */
    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    /**
     * Gets the timestamp when the report was generated.
     *
     * @return the report timestamp
     */
    public LocalDateTime getReportDateTime() {
        return reportDateTime;
    }

    /**
     * Sets the timestamp when the report was generated.
     *
     * @param reportDateTime the report timestamp
     */
    public void setReportDateTime(LocalDateTime reportDateTime) {
        this.reportDateTime = reportDateTime;
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
     * Gets the account status.
     *
     * @return the account status
     */
    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    /**
     * Sets the account status.
     *
     * @param accountStatus the account status
     */
    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
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
     * Gets the list of balances.
     *
     * @return the balances list (immutable)
     */
    public List<Balance> getBalances() {
        return List.copyOf(balances);
    }

    /**
     * Adds a balance to this account report.
     *
     * @param type the balance type
     * @param currency the currency code
     * @param amount the balance amount
     * @param dateTime the balance date time
     */
    public void addBalance(BalanceType type, String currency, BigDecimal amount, LocalDateTime dateTime) {
        Balance balance = new Balance(type, currency, amount, dateTime);
        balances.add(balance);
        log.debug("Added balance {} for account {}", balance, accountId);
    }

    /**
     * Adds a balance to this account report with current date time.
     *
     * @param type the balance type
     * @param currency the currency code
     * @param amount the balance amount
     */
    public void addBalance(BalanceType type, String currency, BigDecimal amount) {
        addBalance(type, currency, amount, LocalDateTime.now());
    }

    /**
     * Gets the balance for a specific type and currency.
     *
     * @param type the balance type
     * @param currency the currency code
     * @return the balance, or null if not found
     */
    public Balance getBalance(BalanceType type, String currency) {
        return balances.stream()
                .filter(b -> b.getType() == type && b.getCurrency().equals(currency))
                .findFirst()
                .orElse(null);
    }

    /**
     * Adds a transaction ID to this account report.
     *
     * @param transactionId the transaction ID
     */
    public void addTransactionId(String transactionId) {
        transactionIds.add(transactionId);
    }

    /**
     * Sets the total amount for this account report.
     *
     * @param totalAmount the total amount
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Gets the total amount for this account report.
     *
     * @return the total amount
     */
    public BigDecimal getTotalAmountAsBigDecimal() {
        return totalAmount;
    }

    /**
     * Checks if the account is active.
     *
     * @return true if the account status is ACTIVE
     */
    public boolean isActive() {
        return AccountStatus.ACTIVE.equals(accountStatus);
    }

    /**
     * Checks if the account is blocked.
     *
     * @return true if the account status is BLOCKED
     */
    public boolean isBlocked() {
        return AccountStatus.BLOCKED.equals(accountStatus);
    }

    /**
     * Checks if the account is closed.
     *
     * @return true if the account status is CLOSED
     */
    public boolean isClosed() {
        return AccountStatus.CLOSED.equals(accountStatus);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Camt052Message that = (Camt052Message) obj;
        return Objects.equals(messageId, that.messageId) &&
               Objects.equals(accountId, that.accountId) &&
               reportType == that.reportType &&
               Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, accountId, reportType, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Camt052Message{messageId='%s', accountId='%s', reportType=%s, " +
                           "accountStatus=%s, balanceCount=%d, transactionCount=%d, creationDateTime=%s}",
                           messageId, accountId, reportType, accountStatus, balances.size(),
                           transactionIds.size(), creationDateTime);
    }
}
