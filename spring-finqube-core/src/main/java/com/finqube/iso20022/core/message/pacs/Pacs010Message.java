package com.finqube.iso20022.core.message.pacs;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;
import com.finqube.iso20022.core.exception.MessageValidationException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * ISO 20022 pacs.010 Direct Debit Mandate message implementation.
 *
 * <p>This message is used in the Payment Clearing and Settlement (pacs) process to
 * create and manage direct debit mandates. It provides detailed information about
 * the mandate including debtor and creditor details, mandate terms, and authorization.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Direct debit mandate creation</li>
 *   <li>Mandate terms and conditions</li>
 *   <li>Debtor and creditor identification</li>
 *   <li>Mandate signature and authorization</li>
 *   <li>Mandate validity period</li>
 *   <li>Collection frequency and amounts</li>
 *   <li>Mandate status management</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * Pacs010Message mandate = new Pacs010Message();
 * mandate.setMessageId("PACS-" + UUID.randomUUID().toString());
 * mandate.setMandateId("MANDATE-123");
 * mandate.setMandateType(MandateType.RECURRING);
 * mandate.setDebtorId("DEBTOR-001");
 * mandate.setCreditorId("CREDITOR-001");
 *
 * // Send via template
 * iso20022Template.sendMessage(mandate);
 * }</pre>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 * @see BaseMessage
 * @see <a href="https://www.iso20022.org/standardsrepository/type/Pacs010">ISO 20022 pacs.010 Specification</a>
 */
public class Pacs010Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Pacs010Message.class);

    /**
     * Mandate type enumeration for pacs.010 messages.
     */
    public enum MandateType {
        /** Recurring mandate */
        RECURRING("RCUR"),
        /** One-off mandate */
        ONE_OFF("OOFF"),
        /** Fixed amount mandate */
        FIXED_AMOUNT("FIXD"),
        /** Variable amount mandate */
        VARIABLE_AMOUNT("VARB");

        private final String code;

        MandateType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Mandate status enumeration for pacs.010 messages.
     */
    public enum MandateStatus {
        /** Mandate is active */
        ACTIVE("ACTV"),
        /** Mandate is suspended */
        SUSPENDED("SUSP"),
        /** Mandate is cancelled */
        CANCELLED("CANC"),
        /** Mandate is expired */
        EXPIRED("EXPR"),
        /** Mandate is pending activation */
        PENDING("PEND"),
        /** Mandate is rejected */
        REJECTED("REJT");

        private final String code;

        MandateStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Frequency enumeration for pacs.010 messages.
     */
    public enum Frequency {
        /** Daily */
        DAILY("DAIL"),
        /** Weekly */
        WEEKLY("WEEK"),
        /** Monthly */
        MONTHLY("MNTH"),
        /** Quarterly */
        QUARTERLY("QURT"),
        /** Semi-annually */
        SEMI_ANNUALLY("SEMI"),
        /** Annually */
        ANNUALLY("YEAR"),
        /** On demand */
        ON_DEMAND("ADHO"),
        /** Irregular */
        IRREGULAR("IRRE");

        private final String code;

        Frequency(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    @NotNull
    @Size(min = 1, max = 35)
    private String messageId;

    @NotNull
    @Size(min = 1, max = 35)
    private String mandateId;

    @NotNull
    private MandateType mandateType;

    @NotNull
    private MandateStatus mandateStatus;

    @NotNull
    private LocalDateTime creationDateTime;

    private LocalDate signatureDate;

    private LocalDate mandateStartDate;

    private LocalDate mandateEndDate;

    @Size(max = 35)
    private String debtorId;

    @Size(max = 35)
    private String creditorId;

    @Size(max = 35)
    private String debtorAccountId;

    @Size(max = 35)
    private String creditorAccountId;

    @Size(max = 500)
    private String mandateDescription;

    private Frequency frequency;

    private BigDecimal maxAmount;

    private String currency;

    @Size(max = 35)
    private String signatureMethod;

    @Size(max = 35)
    private String clearingSystemId;

    @Size(max = 35)
    private String settlementSystemId;

    private boolean electronicSignature;

    private boolean paperSignature;

    /**
     * Default constructor initializes required fields.
     */
    public Pacs010Message() {
        this.messageId = "PACS-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.mandateType = MandateType.RECURRING;
        this.mandateStatus = MandateStatus.PENDING;
        this.currency = "USD";
        log.debug("Created new Pacs010Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param mandateId the mandate identifier
     * @param mandateType the type of mandate
     * @param debtorId the debtor identifier
     * @param creditorId the creditor identifier
     */
    public Pacs010Message(String mandateId, MandateType mandateType, String debtorId, String creditorId) {
        this();
        this.mandateId = mandateId;
        this.mandateType = mandateType;
        this.debtorId = debtorId;
        this.creditorId = creditorId;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getMessageType() {
        return "pacs.010";
    }

    @Override
    public MessagePriority getPriority() {
        return MessagePriority.NORMAL; // Mandates are normal priority
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
        if (mandateType == null) {
            throw new MessageValidationException("Mandate type is required", messageId, getMessageType());
        }
        if (mandateStatus == null) {
            throw new MessageValidationException("Mandate status is required", messageId, getMessageType());
        }
        if (creationDateTime == null) {
            throw new MessageValidationException("Creation date time is required", messageId, getMessageType());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("Direct Debit Mandate %s with type %s and status %s",
                           mandateId, mandateType, mandateStatus);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return true; // Mandates typically require acknowledgment
    }

    @Override
    public String getSchemaVersion() {
        return "001.001.11";
    }

    @Override
    public List<String> getTransactions() {
        return List.of(mandateId); // Mandate references itself
    }

    @Override
    public int getTransactionCount() {
        return 1; // Mandate is one transaction
    }

    @Override
    public double getTotalAmount() {
        return maxAmount != null ? maxAmount.doubleValue() : 0.0;
    }

    /**
     * Sets the unique identifier for this mandate message.
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
     * Gets the mandate type.
     *
     * @return the mandate type
     */
    public MandateType getMandateType() {
        return mandateType;
    }

    /**
     * Sets the mandate type.
     *
     * @param mandateType the mandate type
     */
    public void setMandateType(MandateType mandateType) {
        this.mandateType = mandateType;
    }

    /**
     * Gets the mandate status.
     *
     * @return the mandate status
     */
    public MandateStatus getMandateStatus() {
        return mandateStatus;
    }

    /**
     * Sets the mandate status.
     *
     * @param mandateStatus the mandate status
     */
    public void setMandateStatus(MandateStatus mandateStatus) {
        this.mandateStatus = mandateStatus;
        log.debug("Mandate status updated to {} for mandate {}", mandateStatus, mandateId);
    }

    /**
     * Gets the signature date.
     *
     * @return the signature date
     */
    public LocalDate getSignatureDate() {
        return signatureDate;
    }

    /**
     * Sets the signature date.
     *
     * @param signatureDate the signature date
     */
    public void setSignatureDate(LocalDate signatureDate) {
        this.signatureDate = signatureDate;
    }

    /**
     * Gets the mandate start date.
     *
     * @return the mandate start date
     */
    public LocalDate getMandateStartDate() {
        return mandateStartDate;
    }

    /**
     * Sets the mandate start date.
     *
     * @param mandateStartDate the mandate start date
     */
    public void setMandateStartDate(LocalDate mandateStartDate) {
        this.mandateStartDate = mandateStartDate;
    }

    /**
     * Gets the mandate end date.
     *
     * @return the mandate end date
     */
    public LocalDate getMandateEndDate() {
        return mandateEndDate;
    }

    /**
     * Sets the mandate end date.
     *
     * @param mandateEndDate the mandate end date
     */
    public void setMandateEndDate(LocalDate mandateEndDate) {
        this.mandateEndDate = mandateEndDate;
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
     * Gets the debtor account identifier.
     *
     * @return the debtor account ID
     */
    public String getDebtorAccountId() {
        return debtorAccountId;
    }

    /**
     * Sets the debtor account identifier.
     *
     * @param debtorAccountId the debtor account ID (max 35 characters)
     */
    public void setDebtorAccountId(String debtorAccountId) {
        this.debtorAccountId = debtorAccountId;
    }

    /**
     * Gets the creditor account identifier.
     *
     * @return the creditor account ID
     */
    public String getCreditorAccountId() {
        return creditorAccountId;
    }

    /**
     * Sets the creditor account identifier.
     *
     * @param creditorAccountId the creditor account ID (max 35 characters)
     */
    public void setCreditorAccountId(String creditorAccountId) {
        this.creditorAccountId = creditorAccountId;
    }

    /**
     * Gets the mandate description.
     *
     * @return the mandate description
     */
    public String getMandateDescription() {
        return mandateDescription;
    }

    /**
     * Sets the mandate description.
     *
     * @param mandateDescription the mandate description (max 500 characters)
     */
    public void setMandateDescription(String mandateDescription) {
        this.mandateDescription = mandateDescription;
    }

    /**
     * Gets the collection frequency.
     *
     * @return the frequency
     */
    public Frequency getFrequency() {
        return frequency;
    }

    /**
     * Sets the collection frequency.
     *
     * @param frequency the frequency
     */
    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    /**
     * Gets the maximum amount.
     *
     * @return the maximum amount
     */
    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    /**
     * Sets the maximum amount.
     *
     * @param maxAmount the maximum amount
     */
    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
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
     * Gets the signature method.
     *
     * @return the signature method
     */
    public String getSignatureMethod() {
        return signatureMethod;
    }

    /**
     * Sets the signature method.
     *
     * @param signatureMethod the signature method (max 35 characters)
     */
    public void setSignatureMethod(String signatureMethod) {
        this.signatureMethod = signatureMethod;
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
     * Checks if electronic signature is used.
     *
     * @return true if electronic signature is used
     */
    public boolean isElectronicSignature() {
        return electronicSignature;
    }

    /**
     * Sets whether electronic signature is used.
     *
     * @param electronicSignature true if electronic signature is used
     */
    public void setElectronicSignature(boolean electronicSignature) {
        this.electronicSignature = electronicSignature;
    }

    /**
     * Checks if paper signature is used.
     *
     * @return true if paper signature is used
     */
    public boolean isPaperSignature() {
        return paperSignature;
    }

    /**
     * Sets whether paper signature is used.
     *
     * @param paperSignature true if paper signature is used
     */
    public void setPaperSignature(boolean paperSignature) {
        this.paperSignature = paperSignature;
    }

    /**
     * Checks if the mandate is active.
     *
     * @return true if the mandate status is ACTIVE
     */
    public boolean isActive() {
        return MandateStatus.ACTIVE.equals(mandateStatus);
    }

    /**
     * Checks if the mandate is suspended.
     *
     * @return true if the mandate status is SUSPENDED
     */
    public boolean isSuspended() {
        return MandateStatus.SUSPENDED.equals(mandateStatus);
    }

    /**
     * Checks if the mandate is cancelled.
     *
     * @return true if the mandate status is CANCELLED
     */
    public boolean isCancelled() {
        return MandateStatus.CANCELLED.equals(mandateStatus);
    }

    /**
     * Checks if the mandate is expired.
     *
     * @return true if the mandate status is EXPIRED
     */
    public boolean isExpired() {
        return MandateStatus.EXPIRED.equals(mandateStatus);
    }

    /**
     * Checks if the mandate is pending.
     *
     * @return true if the mandate status is PENDING
     */
    public boolean isPending() {
        return MandateStatus.PENDING.equals(mandateStatus);
    }

    /**
     * Checks if the mandate is rejected.
     *
     * @return true if the mandate status is REJECTED
     */
    public boolean isRejected() {
        return MandateStatus.REJECTED.equals(mandateStatus);
    }

    /**
     * Checks if the mandate is recurring.
     *
     * @return true if the mandate type is RECURRING
     */
    public boolean isRecurring() {
        return MandateType.RECURRING.equals(mandateType);
    }

    /**
     * Checks if the mandate is one-off.
     *
     * @return true if the mandate type is ONE_OFF
     */
    public boolean isOneOff() {
        return MandateType.ONE_OFF.equals(mandateType);
    }

    /**
     * Checks if the mandate is fixed amount.
     *
     * @return true if the mandate type is FIXED_AMOUNT
     */
    public boolean isFixedAmount() {
        return MandateType.FIXED_AMOUNT.equals(mandateType);
    }

    /**
     * Checks if the mandate is variable amount.
     *
     * @return true if the mandate type is VARIABLE_AMOUNT
     */
    public boolean isVariableAmount() {
        return MandateType.VARIABLE_AMOUNT.equals(mandateType);
    }

    /**
     * Checks if the mandate is valid (active and not expired).
     *
     * @return true if the mandate is valid
     */
    public boolean isValid() {
        return isActive() && (mandateEndDate == null || mandateEndDate.isAfter(LocalDate.now()));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pacs010Message that = (Pacs010Message) obj;
        return Objects.equals(messageId, that.messageId) &&
               Objects.equals(mandateId, that.mandateId) &&
               mandateType == that.mandateType &&
               Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, mandateId, mandateType, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Pacs010Message{messageId='%s', mandateId='%s', mandateType=%s, " +
                           "mandateStatus=%s, debtorId='%s', creditorId='%s', creationDateTime=%s}",
                           messageId, mandateId, mandateType, mandateStatus, debtorId, creditorId, creationDateTime);
    }
}
