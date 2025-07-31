package com.finqube.iso20022.core.message.pacs;

import java.time.LocalDateTime;
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
 * ISO 20022 pacs.002 Payment Status Report message implementation.
 *
 * <p>This message is used in the Payment Clearing and Settlement (pacs) process to report
 * the status of a payment instruction that was previously sent via a pacs.008 message.
 * It provides detailed information about the processing status, including any errors or
 * rejections that occurred during clearing and settlement.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>References the original payment instruction</li>
 *   <li>Provides detailed clearing and settlement status</li>
 *   <li>Includes error codes and descriptions</li>
 *   <li>Supports multiple payment statuses in a single report</li>
 *   <li>Used in interbank clearing and settlement processes</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * Pacs002Message statusReport = new Pacs002Message();
 * statusReport.setMessageId("PACS-" + UUID.randomUUID().toString());
 * statusReport.setOriginalMessageId("PACS-ORIG-123");
 * statusReport.setStatus(PaymentStatus.SETTLED);
 * statusReport.setSettlementMethod(SettlementMethod.CLEARING);
 *
 * // Send via template
 * iso20022Template.sendMessage(statusReport);
 * }</pre>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 * @see BaseMessage
 * @see <a href="https://www.iso20022.org/standardsrepository/type/Pacs002">ISO 20022 pacs.002 Specification</a>
 */
public class Pacs002Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Pacs002Message.class);

    /**
     * Payment status enumeration for pacs.002 messages.
     */
    public enum PaymentStatus {
        /** Payment was successfully settled */
        SETTLED,
        /** Payment was rejected due to validation errors */
        REJECTED,
        /** Payment is pending settlement */
        PENDING_SETTLEMENT,
        /** Payment was cancelled by the originator */
        CANCELLED,
        /** Payment failed during settlement */
        SETTLEMENT_FAILED,
        /** Payment is in clearing process */
        IN_CLEARING,
        /** Payment is awaiting confirmation */
        AWAITING_CONFIRMATION
    }

    /**
     * Settlement method enumeration for pacs.002 messages.
     */
    public enum SettlementMethod {
        /** Settlement via clearing system */
        CLEARING("CLRG"),
        /** Settlement via correspondent bank */
        CORRESPONDENT("CORR"),
        /** Settlement via central bank */
        CENTRAL_BANK("CENT"),
        /** Settlement via RTGS system */
        RTGS("RTGS"),
        /** Settlement via netting system */
        NETTING("NETT");

        private final String code;

        SettlementMethod(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Rejection reason codes for pacs.002 messages.
     */
    public enum RejectionReason {
        /** Invalid account number */
        INVALID_ACCOUNT("AC01"),
        /** Insufficient funds */
        INSUFFICIENT_FUNDS("AM04"),
        /** Invalid currency */
        INVALID_CURRENCY("CURR"),
        /** Duplicate payment */
        DUPLICATE_PAYMENT("DUPL"),
        /** Invalid amount */
        INVALID_AMOUNT("AM09"),
        /** Unauthorized transaction */
        UNAUTHORIZED("AG01"),
        /** Technical error */
        TECHNICAL_ERROR("TECH"),
        /** Regulatory compliance issue */
        COMPLIANCE_ISSUE("COMP"),
        /** Clearing system error */
        CLEARING_ERROR("CLRG"),
        /** Settlement system error */
        SETTLEMENT_ERROR("STLM");

        private final String code;

        RejectionReason(String code) {
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
    private String originalMessageId;

    @NotNull
    private PaymentStatus status;

    private SettlementMethod settlementMethod;

    private RejectionReason rejectionReason;

    @Size(max = 500)
    private String statusDescription;

    @NotNull
    private LocalDateTime creationDateTime;

    private LocalDateTime statusDateTime;

    private LocalDateTime settlementDateTime;

    @Size(max = 35)
    private String originatorId;

    @Size(max = 35)
    private String debtorId;

    @Size(max = 35)
    private String creditorId;

    @Size(max = 35)
    private String clearingSystemId;

    @Size(max = 35)
    private String settlementSystemId;

    /**
     * Default constructor initializes required fields.
     */
    public Pacs002Message() {
        this.messageId = "PACS-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.status = PaymentStatus.PENDING_SETTLEMENT;
        log.debug("Created new Pacs002Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param originalMessageId the ID of the original payment instruction
     * @param status the current status of the payment
     */
    public Pacs002Message(String originalMessageId, PaymentStatus status) {
        this();
        this.originalMessageId = originalMessageId;
        this.status = status;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getMessageType() {
        return "pacs.002";
    }

    @Override
    public MessagePriority getPriority() {
        return MessagePriority.HIGH; // Settlement status reports are high priority
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
        if (originalMessageId == null || originalMessageId.trim().isEmpty()) {
            throw new MessageValidationException("Original message ID is required", messageId, getMessageType());
        }
        if (status == null) {
            throw new MessageValidationException("Payment status is required", messageId, getMessageType());
        }
        if (creationDateTime == null) {
            throw new MessageValidationException("Creation date time is required", messageId, getMessageType());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("Payment Clearing and Settlement Status Report for original message %s with status %s",
                           originalMessageId, status);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return true; // Settlement status reports typically require acknowledgment
    }

    @Override
    public String getSchemaVersion() {
        return "001.001.11";
    }

    @Override
    public java.util.List<String> getTransactions() {
        return java.util.List.of(originalMessageId); // Status report references original transaction
    }

    @Override
    public int getTransactionCount() {
        return 1; // Status report covers one original transaction
    }

    @Override
    public double getTotalAmount() {
        return 0.0; // Status reports don't carry amounts
    }

    /**
     * Sets the unique identifier for this status report message.
     *
     * @param messageId the message identifier (1-35 characters)
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the ID of the original payment instruction being reported on.
     *
     * @return the original message ID
     */
    public String getOriginalMessageId() {
        return originalMessageId;
    }

    /**
     * Sets the ID of the original payment instruction being reported on.
     *
     * @param originalMessageId the original message ID (1-35 characters)
     */
    public void setOriginalMessageId(String originalMessageId) {
        this.originalMessageId = originalMessageId;
    }

    /**
     * Gets the current status of the payment.
     *
     * @return the payment status
     */
    public PaymentStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of the payment.
     *
     * @param status the payment status
     */
    public void setStatus(PaymentStatus status) {
        this.status = status;
        this.statusDateTime = LocalDateTime.now();
        log.debug("Payment status updated to {} for message {}", status, messageId);
    }

    /**
     * Gets the settlement method used for this payment.
     *
     * @return the settlement method
     */
    public SettlementMethod getSettlementMethod() {
        return settlementMethod;
    }

    /**
     * Sets the settlement method used for this payment.
     *
     * @param settlementMethod the settlement method
     */
    public void setSettlementMethod(SettlementMethod settlementMethod) {
        this.settlementMethod = settlementMethod;
    }

    /**
     * Gets the rejection reason if the payment was rejected.
     *
     * @return the rejection reason, or null if not rejected
     */
    public RejectionReason getRejectionReason() {
        return rejectionReason;
    }

    /**
     * Sets the rejection reason for rejected payments.
     *
     * @param rejectionReason the rejection reason
     */
    public void setRejectionReason(RejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
        if (rejectionReason != null) {
            log.debug("Rejection reason set to {} for message {}", rejectionReason, messageId);
        }
    }

    /**
     * Gets the human-readable status description.
     *
     * @return the status description
     */
    public String getStatusDescription() {
        return statusDescription;
    }

    /**
     * Sets the human-readable status description.
     *
     * @param statusDescription the status description (max 500 characters)
     */
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    /**
     * Gets the timestamp when the status was last updated.
     *
     * @return the status timestamp
     */
    public LocalDateTime getStatusDateTime() {
        return statusDateTime;
    }

    /**
     * Sets the timestamp when the status was last updated.
     *
     * @param statusDateTime the status timestamp
     */
    public void setStatusDateTime(LocalDateTime statusDateTime) {
        this.statusDateTime = statusDateTime;
    }

    /**
     * Gets the timestamp when the payment was settled.
     *
     * @return the settlement timestamp
     */
    public LocalDateTime getSettlementDateTime() {
        return settlementDateTime;
    }

    /**
     * Sets the timestamp when the payment was settled.
     *
     * @param settlementDateTime the settlement timestamp
     */
    public void setSettlementDateTime(LocalDateTime settlementDateTime) {
        this.settlementDateTime = settlementDateTime;
    }

    /**
     * Gets the originator identifier.
     *
     * @return the originator ID
     */
    public String getOriginatorId() {
        return originatorId;
    }

    /**
     * Sets the originator identifier.
     *
     * @param originatorId the originator ID (max 35 characters)
     */
    public void setOriginatorId(String originatorId) {
        this.originatorId = originatorId;
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
     * Checks if the payment was rejected.
     *
     * @return true if the payment status is REJECTED
     */
    public boolean isRejected() {
        return PaymentStatus.REJECTED.equals(status);
    }

    /**
     * Checks if the payment was successfully settled.
     *
     * @return true if the payment status is SETTLED
     */
    public boolean isSettled() {
        return PaymentStatus.SETTLED.equals(status);
    }

    /**
     * Checks if the payment is pending settlement.
     *
     * @return true if the payment status is PENDING_SETTLEMENT
     */
    public boolean isPendingSettlement() {
        return PaymentStatus.PENDING_SETTLEMENT.equals(status);
    }

    /**
     * Checks if the payment is in clearing process.
     *
     * @return true if the payment status is IN_CLEARING
     */
    public boolean isInClearing() {
        return PaymentStatus.IN_CLEARING.equals(status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pacs002Message that = (Pacs002Message) obj;
        return Objects.equals(messageId, that.messageId) &&
               Objects.equals(originalMessageId, that.originalMessageId) &&
               status == that.status &&
               Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, originalMessageId, status, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Pacs002Message{messageId='%s', originalMessageId='%s', status=%s, " +
                           "settlementMethod=%s, rejectionReason=%s, creationDateTime=%s}",
                           messageId, originalMessageId, status, settlementMethod, rejectionReason, creationDateTime);
    }
}
