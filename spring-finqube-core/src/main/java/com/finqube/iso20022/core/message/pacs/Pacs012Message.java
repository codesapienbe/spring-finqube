package com.finqube.iso20022.core.message.pacs;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;
import com.finqube.iso20022.core.exception.MessageValidationException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.List;

/**
 * ISO 20022 pacs.012 Direct Debit Mandate Cancellation message implementation.
 *
 * <p>This message is used in the Payment Clearing and Settlement (pacs) process to
 * cancel existing direct debit mandates. It provides detailed information about
 * the cancellation including the original mandate reference, cancellation reason,
 * and authorization details.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Direct debit mandate cancellation</li>
 *   <li>Original mandate reference</li>
 *   <li>Cancellation reason and details</li>
 *   <li>Debtor and creditor identification</li>
 *   <li>Cancellation authorization</li>
 *   <li>Cancellation effective date</li>
 *   <li>Cancellation status management</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * Pacs012Message cancellation = new Pacs012Message();
 * cancellation.setMessageId("PACS-" + UUID.randomUUID().toString());
 * cancellation.setOriginalMandateId("MANDATE-123");
 * cancellation.setCancellationReason(CancellationReason.CUSTOMER_REQUEST);
 * cancellation.setEffectiveDate(LocalDate.now().plusDays(30));
 *
 * // Send via template
 * iso20022Template.sendMessage(cancellation);
 * }</pre>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 * @see BaseMessage
 * @see <a href="https://www.iso20022.org/standardsrepository/type/Pacs012">ISO 20022 pacs.012 Specification</a>
 */
public class Pacs012Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Pacs012Message.class);

    /**
     * Cancellation reason enumeration for pacs.012 messages.
     */
    public enum CancellationReason {
        /** Customer request */
        CUSTOMER_REQUEST("CUST"),
        /** Account closed */
        ACCOUNT_CLOSED("ACCT"),
        /** Insufficient funds */
        INSUFFICIENT_FUNDS("FUND"),
        /** Unauthorized transaction */
        UNAUTHORIZED("AUTH"),
        /** Duplicate mandate */
        DUPLICATE_MANDATE("DUPL"),
        /** Technical error */
        TECHNICAL_ERROR("TECH"),
        /** Regulatory compliance issue */
        COMPLIANCE_ISSUE("COMP"),
        /** Mandate expired */
        MANDATE_EXPIRED("EXPR"),
        /** Creditor request */
        CREDITOR_REQUEST("CRED"),
        /** Bank request */
        BANK_REQUEST("BANK"),
        /** Other reason */
        OTHER("OTHR");

        private final String code;

        CancellationReason(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Cancellation status enumeration for pacs.012 messages.
     */
    public enum CancellationStatus {
        /** Cancellation is pending */
        PENDING("PEND"),
        /** Cancellation is approved */
        APPROVED("APPR"),
        /** Cancellation is rejected */
        REJECTED("REJT"),
        /** Cancellation is cancelled */
        CANCELLED("CANC"),
        /** Cancellation is completed */
        COMPLETED("COMP");

        private final String code;

        CancellationStatus(String code) {
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
    private CancellationReason cancellationReason;

    @NotNull
    private CancellationStatus cancellationStatus;

    @NotNull
    private LocalDateTime creationDateTime;

    private LocalDate cancellationDate;

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
    private String cancellationDescription;

    @Size(max = 35)
    private String cancellationReference;

    @Size(max = 35)
    private String clearingSystemId;

    @Size(max = 35)
    private String settlementSystemId;

    private boolean requiresDebtorApproval;

    private boolean requiresCreditorApproval;

    private boolean immediateCancellation;

    /**
     * Default constructor initializes required fields.
     */
    public Pacs012Message() {
        this.messageId = "PACS-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.cancellationReason = CancellationReason.CUSTOMER_REQUEST;
        this.cancellationStatus = CancellationStatus.PENDING;
        log.debug("Created new Pacs012Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param originalMandateId the original mandate identifier
     * @param cancellationReason the reason for cancellation
     * @param debtorId the debtor identifier
     * @param creditorId the creditor identifier
     */
    public Pacs012Message(String originalMandateId, CancellationReason cancellationReason,
                         String debtorId, String creditorId) {
        this();
        this.originalMandateId = originalMandateId;
        this.cancellationReason = cancellationReason;
        this.debtorId = debtorId;
        this.creditorId = creditorId;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getMessageType() {
        return "pacs.012";
    }

    @Override
    public MessagePriority getPriority() {
        return MessagePriority.HIGH; // Cancellations are high priority
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
        if (cancellationReason == null) {
            throw new MessageValidationException("Cancellation reason is required", messageId, getMessageType());
        }
        if (cancellationStatus == null) {
            throw new MessageValidationException("Cancellation status is required", messageId, getMessageType());
        }
        if (creationDateTime == null) {
            throw new MessageValidationException("Creation date time is required", messageId, getMessageType());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("Direct Debit Mandate Cancellation for mandate %s with reason %s and status %s",
                           originalMandateId, cancellationReason, cancellationStatus);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return true; // Cancellations typically require acknowledgment
    }

    @Override
    public String getSchemaVersion() {
        return "001.001.11";
    }

    @Override
    public List<String> getTransactions() {
        return List.of(originalMandateId); // Cancellation references original mandate
    }

    @Override
    public int getTransactionCount() {
        return 1; // Cancellation is one transaction
    }

    @Override
    public double getTotalAmount() {
        return 0.0; // Cancellations have no amount
    }

    /**
     * Sets the unique identifier for this cancellation message.
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
     * Gets the cancellation reason.
     *
     * @return the cancellation reason
     */
    public CancellationReason getCancellationReason() {
        return cancellationReason;
    }

    /**
     * Sets the cancellation reason.
     *
     * @param cancellationReason the cancellation reason
     */
    public void setCancellationReason(CancellationReason cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    /**
     * Gets the cancellation status.
     *
     * @return the cancellation status
     */
    public CancellationStatus getCancellationStatus() {
        return cancellationStatus;
    }

    /**
     * Sets the cancellation status.
     *
     * @param cancellationStatus the cancellation status
     */
    public void setCancellationStatus(CancellationStatus cancellationStatus) {
        this.cancellationStatus = cancellationStatus;
        log.debug("Cancellation status updated to {} for mandate {}", cancellationStatus, originalMandateId);
    }

    /**
     * Gets the cancellation date.
     *
     * @return the cancellation date
     */
    public LocalDate getCancellationDate() {
        return cancellationDate;
    }

    /**
     * Sets the cancellation date.
     *
     * @param cancellationDate the cancellation date
     */
    public void setCancellationDate(LocalDate cancellationDate) {
        this.cancellationDate = cancellationDate;
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
     * Gets the cancellation description.
     *
     * @return the cancellation description
     */
    public String getCancellationDescription() {
        return cancellationDescription;
    }

    /**
     * Sets the cancellation description.
     *
     * @param cancellationDescription the cancellation description (max 500 characters)
     */
    public void setCancellationDescription(String cancellationDescription) {
        this.cancellationDescription = cancellationDescription;
    }

    /**
     * Gets the cancellation reference.
     *
     * @return the cancellation reference
     */
    public String getCancellationReference() {
        return cancellationReference;
    }

    /**
     * Sets the cancellation reference.
     *
     * @param cancellationReference the cancellation reference (max 35 characters)
     */
    public void setCancellationReference(String cancellationReference) {
        this.cancellationReference = cancellationReference;
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
     * Checks if immediate cancellation is requested.
     *
     * @return true if immediate cancellation is requested
     */
    public boolean isImmediateCancellation() {
        return immediateCancellation;
    }

    /**
     * Sets whether immediate cancellation is requested.
     *
     * @param immediateCancellation true if immediate cancellation is requested
     */
    public void setImmediateCancellation(boolean immediateCancellation) {
        this.immediateCancellation = immediateCancellation;
    }

    /**
     * Checks if the cancellation is pending.
     *
     * @return true if the cancellation status is PENDING
     */
    public boolean isPending() {
        return CancellationStatus.PENDING.equals(cancellationStatus);
    }

    /**
     * Checks if the cancellation is approved.
     *
     * @return true if the cancellation status is APPROVED
     */
    public boolean isApproved() {
        return CancellationStatus.APPROVED.equals(cancellationStatus);
    }

    /**
     * Checks if the cancellation is rejected.
     *
     * @return true if the cancellation status is REJECTED
     */
    public boolean isRejected() {
        return CancellationStatus.REJECTED.equals(cancellationStatus);
    }

    /**
     * Checks if the cancellation is cancelled.
     *
     * @return true if the cancellation status is CANCELLED
     */
    public boolean isCancelled() {
        return CancellationStatus.CANCELLED.equals(cancellationStatus);
    }

    /**
     * Checks if the cancellation is completed.
     *
     * @return true if the cancellation status is COMPLETED
     */
    public boolean isCompleted() {
        return CancellationStatus.COMPLETED.equals(cancellationStatus);
    }

    /**
     * Checks if the cancellation is due to customer request.
     *
     * @return true if the cancellation reason is CUSTOMER_REQUEST
     */
    public boolean isCustomerRequest() {
        return CancellationReason.CUSTOMER_REQUEST.equals(cancellationReason);
    }

    /**
     * Checks if the cancellation is due to account closure.
     *
     * @return true if the cancellation reason is ACCOUNT_CLOSED
     */
    public boolean isAccountClosed() {
        return CancellationReason.ACCOUNT_CLOSED.equals(cancellationReason);
    }

    /**
     * Checks if the cancellation is due to insufficient funds.
     *
     * @return true if the cancellation reason is INSUFFICIENT_FUNDS
     */
    public boolean isInsufficientFunds() {
        return CancellationReason.INSUFFICIENT_FUNDS.equals(cancellationReason);
    }

    /**
     * Checks if the cancellation is due to unauthorized transaction.
     *
     * @return true if the cancellation reason is UNAUTHORIZED
     */
    public boolean isUnauthorized() {
        return CancellationReason.UNAUTHORIZED.equals(cancellationReason);
    }

    /**
     * Checks if the cancellation is due to duplicate mandate.
     *
     * @return true if the cancellation reason is DUPLICATE_MANDATE
     */
    public boolean isDuplicateMandate() {
        return CancellationReason.DUPLICATE_MANDATE.equals(cancellationReason);
    }

    /**
     * Checks if the cancellation is due to technical error.
     *
     * @return true if the cancellation reason is TECHNICAL_ERROR
     */
    public boolean isTechnicalError() {
        return CancellationReason.TECHNICAL_ERROR.equals(cancellationReason);
    }

    /**
     * Checks if the cancellation is due to compliance issue.
     *
     * @return true if the cancellation reason is COMPLIANCE_ISSUE
     */
    public boolean isComplianceIssue() {
        return CancellationReason.COMPLIANCE_ISSUE.equals(cancellationReason);
    }

    /**
     * Checks if the cancellation is due to mandate expiration.
     *
     * @return true if the cancellation reason is MANDATE_EXPIRED
     */
    public boolean isMandateExpired() {
        return CancellationReason.MANDATE_EXPIRED.equals(cancellationReason);
    }

    /**
     * Checks if the cancellation is due to creditor request.
     *
     * @return true if the cancellation reason is CREDITOR_REQUEST
     */
    public boolean isCreditorRequest() {
        return CancellationReason.CREDITOR_REQUEST.equals(cancellationReason);
    }

    /**
     * Checks if the cancellation is due to bank request.
     *
     * @return true if the cancellation reason is BANK_REQUEST
     */
    public boolean isBankRequest() {
        return CancellationReason.BANK_REQUEST.equals(cancellationReason);
    }

    /**
     * Checks if the cancellation is due to other reason.
     *
     * @return true if the cancellation reason is OTHER
     */
    public boolean isOtherReason() {
        return CancellationReason.OTHER.equals(cancellationReason);
    }

    /**
     * Checks if the cancellation is effective (approved and effective date is in the past or today).
     *
     * @return true if the cancellation is effective
     */
    public boolean isEffective() {
        return isApproved() && (effectiveDate == null || !effectiveDate.isAfter(LocalDate.now()));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pacs012Message that = (Pacs012Message) obj;
        return Objects.equals(messageId, that.messageId) &&
               Objects.equals(originalMandateId, that.originalMandateId) &&
               cancellationReason == that.cancellationReason &&
               Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, originalMandateId, cancellationReason, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Pacs012Message{messageId='%s', originalMandateId='%s', cancellationReason=%s, " +
                           "cancellationStatus=%s, debtorId='%s', creditorId='%s', creationDateTime=%s}",
                           messageId, originalMandateId, cancellationReason, cancellationStatus, debtorId, creditorId, creationDateTime);
    }
}
