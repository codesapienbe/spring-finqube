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
import java.util.Objects;
import java.util.UUID;
import java.util.List;

/**
 * ISO 20022 pacs.011 Direct Debit Mandate Amendment message implementation.
 *
 * <p>This message is used in the Payment Clearing and Settlement (pacs) process to
 * amend existing direct debit mandates. It provides detailed information about
 * the amendment including the original mandate reference, changes to mandate terms,
 * and authorization details.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Direct debit mandate amendment</li>
 *   <li>Original mandate reference</li>
 *   <li>Amendment details and changes</li>
 *   <li>Debtor and creditor identification</li>
 *   <li>Amendment authorization</li>
 *   <li>Amendment validity period</li>
 *   <li>Amendment status management</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * Pacs011Message amendment = new Pacs011Message();
 * amendment.setMessageId("PACS-" + UUID.randomUUID().toString());
 * amendment.setOriginalMandateId("MANDATE-123");
 * amendment.setAmendmentType(AmendmentType.AMOUNT_CHANGE);
 * amendment.setNewMaxAmount(new BigDecimal("2000.00"));
 *
 * // Send via template
 * iso20022Template.sendMessage(amendment);
 * }</pre>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 * @see BaseMessage
 * @see <a href="https://www.iso20022.org/standardsrepository/type/Pacs011">ISO 20022 pacs.011 Specification</a>
 */
public class Pacs011Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Pacs011Message.class);

    /**
     * Amendment type enumeration for pacs.011 messages.
     */
    public enum AmendmentType {
        /** Amount change */
        AMOUNT_CHANGE("AMNT"),
        /** Frequency change */
        FREQUENCY_CHANGE("FREQ"),
        /** Date change */
        DATE_CHANGE("DATE"),
        /** Account change */
        ACCOUNT_CHANGE("ACCT"),
        /** Creditor change */
        CREDITOR_CHANGE("CRED"),
        /** Description change */
        DESCRIPTION_CHANGE("DESC"),
        /** Currency change */
        CURRENCY_CHANGE("CURR"),
        /** Multiple changes */
        MULTIPLE_CHANGES("MULT"),
        /** Other change */
        OTHER("OTHR");

        private final String code;

        AmendmentType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Amendment status enumeration for pacs.011 messages.
     */
    public enum AmendmentStatus {
        /** Amendment is pending */
        PENDING("PEND"),
        /** Amendment is approved */
        APPROVED("APPR"),
        /** Amendment is rejected */
        REJECTED("REJT"),
        /** Amendment is cancelled */
        CANCELLED("CANC"),
        /** Amendment is completed */
        COMPLETED("COMP");

        private final String code;

        AmendmentStatus(String code) {
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
    private String originalMandateId;

    @NotNull
    private AmendmentType amendmentType;

    @NotNull
    private AmendmentStatus amendmentStatus;

    @NotNull
    private LocalDateTime creationDateTime;

    private LocalDate amendmentDate;

    private LocalDate effectiveDate;

    @Size(max = 35)
    private String debtorId;

    @Size(max = 35)
    private String creditorId;

    @Size(max = 35)
    private String debtorAccountId;

    @Size(max = 35)
    private String creditorAccountId;

    @Size(max = 500)
    private String amendmentDescription;

    private BigDecimal newMaxAmount;

    private String newCurrency;

    private Pacs010Message.Frequency newFrequency;

    @Size(max = 35)
    private String amendmentReason;

    @Size(max = 35)
    private String clearingSystemId;

    @Size(max = 35)
    private String settlementSystemId;

    private boolean requiresDebtorApproval;

    private boolean requiresCreditorApproval;

    /**
     * Default constructor initializes required fields.
     */
    public Pacs011Message() {
        this.messageId = "PACS-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.amendmentType = AmendmentType.OTHER;
        this.amendmentStatus = AmendmentStatus.PENDING;
        this.newCurrency = "USD";
        log.debug("Created new Pacs011Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param originalMandateId the original mandate identifier
     * @param amendmentType the type of amendment
     * @param debtorId the debtor identifier
     * @param creditorId the creditor identifier
     */
    public Pacs011Message(String originalMandateId, AmendmentType amendmentType,
                         String debtorId, String creditorId) {
        this();
        this.originalMandateId = originalMandateId;
        this.amendmentType = amendmentType;
        this.debtorId = debtorId;
        this.creditorId = creditorId;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getMessageType() {
        return "pacs.011";
    }

    @Override
    public MessagePriority getPriority() {
        return MessagePriority.NORMAL; // Amendments are normal priority
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
        if (originalMandateId == null || originalMandateId.trim().isEmpty()) {
            throw new MessageValidationException("Original mandate ID is required", messageId, getMessageType());
        }
        if (amendmentType == null) {
            throw new MessageValidationException("Amendment type is required", messageId, getMessageType());
        }
        if (amendmentStatus == null) {
            throw new MessageValidationException("Amendment status is required", messageId, getMessageType());
        }
        if (creationDateTime == null) {
            throw new MessageValidationException("Creation date time is required", messageId, getMessageType());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("Direct Debit Mandate Amendment for mandate %s with type %s and status %s",
                           originalMandateId, amendmentType, amendmentStatus);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return true; // Amendments typically require acknowledgment
    }

    @Override
    public String getSchemaVersion() {
        return "001.001.11";
    }

    @Override
    public List<String> getTransactions() {
        return List.of(originalMandateId); // Amendment references original mandate
    }

    @Override
    public int getTransactionCount() {
        return 1; // Amendment is one transaction
    }

    @Override
    public double getTotalAmount() {
        return newMaxAmount != null ? newMaxAmount.doubleValue() : 0.0;
    }

    /**
     * Sets the unique identifier for this amendment message.
     *
     * @param messageId the message identifier (1-35 characters)
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the original mandate identifier.
     *
     * @return the original mandate ID
     */
    public String getOriginalMandateId() {
        return originalMandateId;
    }

    /**
     * Sets the original mandate identifier.
     *
     * @param originalMandateId the original mandate ID (1-35 characters)
     */
    public void setOriginalMandateId(String originalMandateId) {
        this.originalMandateId = originalMandateId;
    }

    /**
     * Gets the amendment type.
     *
     * @return the amendment type
     */
    public AmendmentType getAmendmentType() {
        return amendmentType;
    }

    /**
     * Sets the amendment type.
     *
     * @param amendmentType the amendment type
     */
    public void setAmendmentType(AmendmentType amendmentType) {
        this.amendmentType = amendmentType;
    }

    /**
     * Gets the amendment status.
     *
     * @return the amendment status
     */
    public AmendmentStatus getAmendmentStatus() {
        return amendmentStatus;
    }

    /**
     * Sets the amendment status.
     *
     * @param amendmentStatus the amendment status
     */
    public void setAmendmentStatus(AmendmentStatus amendmentStatus) {
        this.amendmentStatus = amendmentStatus;
        log.debug("Amendment status updated to {} for mandate {}", amendmentStatus, originalMandateId);
    }

    /**
     * Gets the amendment date.
     *
     * @return the amendment date
     */
    public LocalDate getAmendmentDate() {
        return amendmentDate;
    }

    /**
     * Sets the amendment date.
     *
     * @param amendmentDate the amendment date
     */
    public void setAmendmentDate(LocalDate amendmentDate) {
        this.amendmentDate = amendmentDate;
    }

    /**
     * Gets the effective date.
     *
     * @return the effective date
     */
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the effective date.
     *
     * @param effectiveDate the effective date
     */
    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
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
     * Gets the amendment description.
     *
     * @return the amendment description
     */
    public String getAmendmentDescription() {
        return amendmentDescription;
    }

    /**
     * Sets the amendment description.
     *
     * @param amendmentDescription the amendment description (max 500 characters)
     */
    public void setAmendmentDescription(String amendmentDescription) {
        this.amendmentDescription = amendmentDescription;
    }

    /**
     * Gets the new maximum amount.
     *
     * @return the new maximum amount
     */
    public BigDecimal getNewMaxAmount() {
        return newMaxAmount;
    }

    /**
     * Sets the new maximum amount.
     *
     * @param newMaxAmount the new maximum amount
     */
    public void setNewMaxAmount(BigDecimal newMaxAmount) {
        this.newMaxAmount = newMaxAmount;
    }

    /**
     * Gets the new currency.
     *
     * @return the new currency
     */
    public String getNewCurrency() {
        return newCurrency;
    }

    /**
     * Sets the new currency.
     *
     * @param newCurrency the new currency
     */
    public void setNewCurrency(String newCurrency) {
        this.newCurrency = newCurrency;
    }

    /**
     * Gets the new frequency.
     *
     * @return the new frequency
     */
    public Pacs010Message.Frequency getNewFrequency() {
        return newFrequency;
    }

    /**
     * Sets the new frequency.
     *
     * @param newFrequency the new frequency
     */
    public void setNewFrequency(Pacs010Message.Frequency newFrequency) {
        this.newFrequency = newFrequency;
    }

    /**
     * Gets the amendment reason.
     *
     * @return the amendment reason
     */
    public String getAmendmentReason() {
        return amendmentReason;
    }

    /**
     * Sets the amendment reason.
     *
     * @param amendmentReason the amendment reason (max 35 characters)
     */
    public void setAmendmentReason(String amendmentReason) {
        this.amendmentReason = amendmentReason;
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
     * Checks if debtor approval is required.
     *
     * @return true if debtor approval is required
     */
    public boolean isRequiresDebtorApproval() {
        return requiresDebtorApproval;
    }

    /**
     * Sets whether debtor approval is required.
     *
     * @param requiresDebtorApproval true if debtor approval is required
     */
    public void setRequiresDebtorApproval(boolean requiresDebtorApproval) {
        this.requiresDebtorApproval = requiresDebtorApproval;
    }

    /**
     * Checks if creditor approval is required.
     *
     * @return true if creditor approval is required
     */
    public boolean isRequiresCreditorApproval() {
        return requiresCreditorApproval;
    }

    /**
     * Sets whether creditor approval is required.
     *
     * @param requiresCreditorApproval true if creditor approval is required
     */
    public void setRequiresCreditorApproval(boolean requiresCreditorApproval) {
        this.requiresCreditorApproval = requiresCreditorApproval;
    }

    /**
     * Checks if the amendment is pending.
     *
     * @return true if the amendment status is PENDING
     */
    public boolean isPending() {
        return AmendmentStatus.PENDING.equals(amendmentStatus);
    }

    /**
     * Checks if the amendment is approved.
     *
     * @return true if the amendment status is APPROVED
     */
    public boolean isApproved() {
        return AmendmentStatus.APPROVED.equals(amendmentStatus);
    }

    /**
     * Checks if the amendment is rejected.
     *
     * @return true if the amendment status is REJECTED
     */
    public boolean isRejected() {
        return AmendmentStatus.REJECTED.equals(amendmentStatus);
    }

    /**
     * Checks if the amendment is cancelled.
     *
     * @return true if the amendment status is CANCELLED
     */
    public boolean isCancelled() {
        return AmendmentStatus.CANCELLED.equals(amendmentStatus);
    }

    /**
     * Checks if the amendment is completed.
     *
     * @return true if the amendment status is COMPLETED
     */
    public boolean isCompleted() {
        return AmendmentStatus.COMPLETED.equals(amendmentStatus);
    }

    /**
     * Checks if the amendment is an amount change.
     *
     * @return true if the amendment type is AMOUNT_CHANGE
     */
    public boolean isAmountChange() {
        return AmendmentType.AMOUNT_CHANGE.equals(amendmentType);
    }

    /**
     * Checks if the amendment is a frequency change.
     *
     * @return true if the amendment type is FREQUENCY_CHANGE
     */
    public boolean isFrequencyChange() {
        return AmendmentType.FREQUENCY_CHANGE.equals(amendmentType);
    }

    /**
     * Checks if the amendment is a date change.
     *
     * @return true if the amendment type is DATE_CHANGE
     */
    public boolean isDateChange() {
        return AmendmentType.DATE_CHANGE.equals(amendmentType);
    }

    /**
     * Checks if the amendment is an account change.
     *
     * @return true if the amendment type is ACCOUNT_CHANGE
     */
    public boolean isAccountChange() {
        return AmendmentType.ACCOUNT_CHANGE.equals(amendmentType);
    }

    /**
     * Checks if the amendment is a creditor change.
     *
     * @return true if the amendment type is CREDITOR_CHANGE
     */
    public boolean isCreditorChange() {
        return AmendmentType.CREDITOR_CHANGE.equals(amendmentType);
    }

    /**
     * Checks if the amendment is a description change.
     *
     * @return true if the amendment type is DESCRIPTION_CHANGE
     */
    public boolean isDescriptionChange() {
        return AmendmentType.DESCRIPTION_CHANGE.equals(amendmentType);
    }

    /**
     * Checks if the amendment is a currency change.
     *
     * @return true if the amendment type is CURRENCY_CHANGE
     */
    public boolean isCurrencyChange() {
        return AmendmentType.CURRENCY_CHANGE.equals(amendmentType);
    }

    /**
     * Checks if the amendment involves multiple changes.
     *
     * @return true if the amendment type is MULTIPLE_CHANGES
     */
    public boolean isMultipleChanges() {
        return AmendmentType.MULTIPLE_CHANGES.equals(amendmentType);
    }

    /**
     * Checks if the amendment is other type.
     *
     * @return true if the amendment type is OTHER
     */
    public boolean isOtherChange() {
        return AmendmentType.OTHER.equals(amendmentType);
    }

    /**
     * Checks if the amendment is effective (approved and effective date is in the past or today).
     *
     * @return true if the amendment is effective
     */
    public boolean isEffective() {
        return isApproved() && (effectiveDate == null || !effectiveDate.isAfter(LocalDate.now()));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pacs011Message that = (Pacs011Message) obj;
        return Objects.equals(messageId, that.messageId) &&
               Objects.equals(originalMandateId, that.originalMandateId) &&
               amendmentType == that.amendmentType &&
               Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, originalMandateId, amendmentType, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Pacs011Message{messageId='%s', originalMandateId='%s', amendmentType=%s, " +
                           "amendmentStatus=%s, debtorId='%s', creditorId='%s', creationDateTime=%s}",
                           messageId, originalMandateId, amendmentType, amendmentStatus, debtorId, creditorId, creationDateTime);
    }
}
