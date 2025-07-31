package com.finqube.iso20022.core.message.pain;

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
 * ISO 20022 pain.002 Payment Status Report message implementation.
 *
 * <p>This message is used to report the status of a payment instruction that was previously
 * sent via a pain.001 message. It provides detailed information about the processing status,
 * including any errors or rejections that occurred during processing.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>References the original payment instruction</li>
 *   <li>Provides detailed status information</li>
 *   <li>Includes error codes and descriptions</li>
 *   <li>Supports multiple payment statuses in a single report</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * Pain002Message statusReport = new Pain002Message();
 * statusReport.setMessageId("MSG-" + UUID.randomUUID().toString());
 * statusReport.setOriginalMessageId("ORIG-MSG-123");
 * statusReport.setStatus(PaymentStatus.REJECTED);
 * statusReport.setRejectionReason("Invalid account number");
 *
 * // Send via template
 * iso20022Template.sendMessage(statusReport);
 * }</pre>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 * @see BaseMessage
 * @see Pain001Message
 * @see <a href="https://www.iso20022.org/standardsrepository/type/Pain002">ISO 20022 pain.002 Specification</a>
 */
public class Pain002Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Pain002Message.class);

    /**
     * Payment status enumeration for pain.002 messages.
     */
    public enum PaymentStatus {
        /** Payment was successfully processed */
        ACCEPTED,
        /** Payment was rejected due to validation errors */
        REJECTED,
        /** Payment is pending further processing */
        PENDING,
        /** Payment was cancelled by the originator */
        CANCELLED,
        /** Payment failed during processing */
        FAILED
    }

    /**
     * Common rejection reason codes for pain.002 messages.
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
        COMPLIANCE_ISSUE("COMP");

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

    private RejectionReason rejectionReason;

    @Size(max = 500)
    private String statusDescription;

    @NotNull
    private LocalDateTime creationDateTime;

    private LocalDateTime statusDateTime;

    @Size(max = 35)
    private String originatorId;

    @Size(max = 35)
    private String debtorId;

    @Size(max = 35)
    private String creditorId;

    /**
     * Default constructor initializes required fields.
     */
    public Pain002Message() {
        this.messageId = "MSG-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
        log.debug("Created new Pain002Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param originalMessageId the ID of the original payment instruction
     * @param status the current status of the payment
     */
    public Pain002Message(String originalMessageId, PaymentStatus status) {
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
        return "pain.002";
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
        return "pain";
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
        return String.format("Payment Status Report for original message %s with status %s",
                           originalMessageId, status);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return false; // Status reports typically don't require acknowledgment
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
     * Checks if the payment was rejected.
     *
     * @return true if the payment status is REJECTED
     */
    public boolean isRejected() {
        return PaymentStatus.REJECTED.equals(status);
    }

    /**
     * Checks if the payment was successfully processed.
     *
     * @return true if the payment status is ACCEPTED
     */
    public boolean isAccepted() {
        return PaymentStatus.ACCEPTED.equals(status);
    }

    /**
     * Checks if the payment is still pending.
     *
     * @return true if the payment status is PENDING
     */
    public boolean isPending() {
        return PaymentStatus.PENDING.equals(status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pain002Message that = (Pain002Message) obj;
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
        return String.format("Pain002Message{messageId='%s', originalMessageId='%s', status=%s, " +
                           "rejectionReason=%s, creationDateTime=%s}",
                           messageId, originalMessageId, status, rejectionReason, creationDateTime);
    }
}
