package com.finqube.iso20022.core.message.pacs;

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
 * ISO 20022 pacs.004 Payment Return message implementation.
 *
 * <p>This message is used in the Payment Clearing and Settlement (pacs) process to
 * return a payment that was previously sent via a pacs.008 message. It provides
 * detailed information about the return, including the reason for the return and
 * any associated charges.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Payment return processing</li>
 *   <li>Return reason codes and descriptions</li>
 *   <li>Original payment reference</li>
 *   <li>Return amount and currency</li>
 *   <li>Return charges and fees</li>
 *   <li>Return date and time information</li>
 *   <li>Return routing information</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * Pacs004Message paymentReturn = new Pacs004Message();
 * paymentReturn.setMessageId("PACS-" + UUID.randomUUID().toString());
 * paymentReturn.setOriginalMessageId("PACS-ORIG-123");
 * paymentReturn.setReturnReason(ReturnReason.ACCOUNT_CLOSED);
 * paymentReturn.setReturnAmount(new BigDecimal("1000.00"));
 *
 * // Send via template
 * iso20022Template.sendMessage(paymentReturn);
 * }</pre>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 * @see BaseMessage
 * @see <a href="https://www.iso20022.org/standardsrepository/type/Pacs004">ISO 20022 pacs.004 Specification</a>
 */
public class Pacs004Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Pacs004Message.class);

    /**
     * Return reason enumeration for pacs.004 messages.
     */
    public enum ReturnReason {
        /** Account closed */
        ACCOUNT_CLOSED("AC01"),
        /** Insufficient funds */
        INSUFFICIENT_FUNDS("AM04"),
        /** Invalid account number */
        INVALID_ACCOUNT("AC01"),
        /** Invalid currency */
        INVALID_CURRENCY("CURR"),
        /** Duplicate payment */
        DUPLICATE_PAYMENT("DUPL"),
        /** Unauthorized transaction */
        UNAUTHORIZED("AG01"),
        /** Technical error */
        TECHNICAL_ERROR("TECH"),
        /** Regulatory compliance issue */
        COMPLIANCE_ISSUE("COMP"),
        /** Payment stopped by originator */
        STOPPED_BY_ORIGINATOR("STOP"),
        /** Payment cancelled by originator */
        CANCELLED_BY_ORIGINATOR("CANC"),
        /** Invalid amount */
        INVALID_AMOUNT("AM09"),
        /** Invalid beneficiary */
        INVALID_BENEFICIARY("BE01"),
        /** Invalid clearing code */
        INVALID_CLEARING_CODE("CL01"),
        /** Invalid settlement method */
        INVALID_SETTLEMENT_METHOD("SM01");

        private final String code;

        ReturnReason(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Return status enumeration for pacs.004 messages.
     */
    public enum ReturnStatus {
        /** Return is pending */
        PENDING("PEND"),
        /** Return has been processed */
        PROCESSED("PROC"),
        /** Return has been rejected */
        REJECTED("REJT"),
        /** Return has been cancelled */
        CANCELLED("CANC"),
        /** Return has been completed */
        COMPLETED("COMP");

        private final String code;

        ReturnStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Return charge information for pacs.004 messages.
     */
    public static class ReturnCharge {
        private final String chargeType;
        private final BigDecimal amount;
        private final String currency;
        private final String description;

        public ReturnCharge(String chargeType, BigDecimal amount, String currency, String description) {
            this.chargeType = chargeType;
            this.amount = amount;
            this.currency = currency;
            this.description = description;
        }

        public String getChargeType() {
            return chargeType;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public String getCurrency() {
            return currency;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return String.format("ReturnCharge{chargeType='%s', amount=%s, currency='%s', description='%s'}",
                               chargeType, amount, currency, description);
        }
    }

    @NotNull
    @Size(min = 1, max = 35)
    private String messageId;

    @NotNull
    @Size(min = 1, max = 35)
    private String originalMessageId;

    @NotNull
    private ReturnReason returnReason;

    @NotNull
    private ReturnStatus returnStatus;

    @NotNull
    private BigDecimal returnAmount;

    @NotNull
    private String returnCurrency;

    @Size(max = 500)
    private String returnDescription;

    @NotNull
    private LocalDateTime creationDateTime;

    private LocalDateTime returnDateTime;

    private LocalDateTime processingDateTime;

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

    private final List<ReturnCharge> returnCharges = new ArrayList<>();

    private BigDecimal totalCharges = BigDecimal.ZERO;

    /**
     * Default constructor initializes required fields.
     */
    public Pacs004Message() {
        this.messageId = "PACS-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.returnStatus = ReturnStatus.PENDING;
        this.returnAmount = BigDecimal.ZERO;
        this.returnCurrency = "USD";
        log.debug("Created new Pacs004Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param originalMessageId the ID of the original payment message
     * @param returnReason the reason for the return
     * @param returnAmount the amount being returned
     * @param returnCurrency the currency of the return
     */
    public Pacs004Message(String originalMessageId, ReturnReason returnReason,
                         BigDecimal returnAmount, String returnCurrency) {
        this();
        this.originalMessageId = originalMessageId;
        this.returnReason = returnReason;
        this.returnAmount = returnAmount;
        this.returnCurrency = returnCurrency;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getMessageType() {
        return "pacs.004";
    }

    @Override
    public MessagePriority getPriority() {
        return MessagePriority.HIGH; // Payment returns are high priority
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
        if (returnReason == null) {
            throw new MessageValidationException("Return reason is required", messageId, getMessageType());
        }
        if (returnStatus == null) {
            throw new MessageValidationException("Return status is required", messageId, getMessageType());
        }
        if (returnAmount == null) {
            throw new MessageValidationException("Return amount is required", messageId, getMessageType());
        }
        if (returnCurrency == null || returnCurrency.trim().isEmpty()) {
            throw new MessageValidationException("Return currency is required", messageId, getMessageType());
        }
        if (creationDateTime == null) {
            throw new MessageValidationException("Creation date time is required", messageId, getMessageType());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("Payment Return for original message %s with reason %s and amount %s %s",
                           originalMessageId, returnReason, returnAmount, returnCurrency);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return true; // Payment returns typically require acknowledgment
    }

    @Override
    public String getSchemaVersion() {
        return "001.001.11";
    }

    @Override
    public List<String> getTransactions() {
        return List.of(originalMessageId); // Return references original transaction
    }

    @Override
    public int getTransactionCount() {
        return 1; // Return covers one original transaction
    }

    @Override
    public double getTotalAmount() {
        return returnAmount.doubleValue();
    }

    /**
     * Sets the unique identifier for this payment return message.
     *
     * @param messageId the message identifier (1-35 characters)
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the ID of the original payment message being returned.
     *
     * @return the original message ID
     */
    public String getOriginalMessageId() {
        return originalMessageId;
    }

    /**
     * Sets the ID of the original payment message being returned.
     *
     * @param originalMessageId the original message ID (1-35 characters)
     */
    public void setOriginalMessageId(String originalMessageId) {
        this.originalMessageId = originalMessageId;
    }

    /**
     * Gets the reason for the return.
     *
     * @return the return reason
     */
    public ReturnReason getReturnReason() {
        return returnReason;
    }

    /**
     * Sets the reason for the return.
     *
     * @param returnReason the return reason
     */
    public void setReturnReason(ReturnReason returnReason) {
        this.returnReason = returnReason;
    }

    /**
     * Gets the current status of the return.
     *
     * @return the return status
     */
    public ReturnStatus getReturnStatus() {
        return returnStatus;
    }

    /**
     * Sets the current status of the return.
     *
     * @param returnStatus the return status
     */
    public void setReturnStatus(ReturnStatus returnStatus) {
        this.returnStatus = returnStatus;
        if (ReturnStatus.PROCESSED.equals(returnStatus)) {
            this.processingDateTime = LocalDateTime.now();
        }
        log.debug("Return status updated to {} for message {}", returnStatus, messageId);
    }

    /**
     * Gets the amount being returned.
     *
     * @return the return amount
     */
    public BigDecimal getReturnAmount() {
        return returnAmount;
    }

    /**
     * Sets the amount being returned.
     *
     * @param returnAmount the return amount
     */
    public void setReturnAmount(BigDecimal returnAmount) {
        this.returnAmount = returnAmount;
    }

    /**
     * Gets the currency of the return.
     *
     * @return the return currency
     */
    public String getReturnCurrency() {
        return returnCurrency;
    }

    /**
     * Sets the currency of the return.
     *
     * @param returnCurrency the return currency
     */
    public void setReturnCurrency(String returnCurrency) {
        this.returnCurrency = returnCurrency;
    }

    /**
     * Gets the description of the return.
     *
     * @return the return description
     */
    public String getReturnDescription() {
        return returnDescription;
    }

    /**
     * Sets the description of the return.
     *
     * @param returnDescription the return description (max 500 characters)
     */
    public void setReturnDescription(String returnDescription) {
        this.returnDescription = returnDescription;
    }

    /**
     * Gets the timestamp when the return was processed.
     *
     * @return the return timestamp
     */
    public LocalDateTime getReturnDateTime() {
        return returnDateTime;
    }

    /**
     * Sets the timestamp when the return was processed.
     *
     * @param returnDateTime the return timestamp
     */
    public void setReturnDateTime(LocalDateTime returnDateTime) {
        this.returnDateTime = returnDateTime;
    }

    /**
     * Gets the timestamp when the return was processed.
     *
     * @return the processing timestamp
     */
    public LocalDateTime getProcessingDateTime() {
        return processingDateTime;
    }

    /**
     * Sets the timestamp when the return was processed.
     *
     * @param processingDateTime the processing timestamp
     */
    public void setProcessingDateTime(LocalDateTime processingDateTime) {
        this.processingDateTime = processingDateTime;
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
     * Gets the list of return charges.
     *
     * @return the return charges list (immutable)
     */
    public List<ReturnCharge> getReturnCharges() {
        return List.copyOf(returnCharges);
    }

    /**
     * Adds a return charge to this payment return.
     *
     * @param chargeType the type of charge
     * @param amount the charge amount
     * @param currency the charge currency
     * @param description the charge description
     */
    public void addReturnCharge(String chargeType, BigDecimal amount, String currency, String description) {
        ReturnCharge charge = new ReturnCharge(chargeType, amount, currency, description);
        returnCharges.add(charge);
        totalCharges = totalCharges.add(amount);
        log.debug("Added return charge {} for message {}", charge, messageId);
    }

    /**
     * Gets the total amount of return charges.
     *
     * @return the total charges amount
     */
    public BigDecimal getTotalCharges() {
        return totalCharges;
    }

    /**
     * Gets the net return amount (return amount - total charges).
     *
     * @return the net return amount
     */
    public BigDecimal getNetReturnAmount() {
        return returnAmount.subtract(totalCharges);
    }

    /**
     * Checks if the return is pending.
     *
     * @return true if the return status is PENDING
     */
    public boolean isPending() {
        return ReturnStatus.PENDING.equals(returnStatus);
    }

    /**
     * Checks if the return has been processed.
     *
     * @return true if the return status is PROCESSED
     */
    public boolean isProcessed() {
        return ReturnStatus.PROCESSED.equals(returnStatus);
    }

    /**
     * Checks if the return has been completed.
     *
     * @return true if the return status is COMPLETED
     */
    public boolean isCompleted() {
        return ReturnStatus.COMPLETED.equals(returnStatus);
    }

    /**
     * Checks if the return has been rejected.
     *
     * @return true if the return status is REJECTED
     */
    public boolean isRejected() {
        return ReturnStatus.REJECTED.equals(returnStatus);
    }

    /**
     * Checks if the return has charges.
     *
     * @return true if there are return charges
     */
    public boolean hasCharges() {
        return !returnCharges.isEmpty();
    }

    /**
     * Gets the count of return charges.
     *
     * @return the return charge count
     */
    public int getChargeCount() {
        return returnCharges.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pacs004Message that = (Pacs004Message) obj;
        return Objects.equals(messageId, that.messageId) &&
               Objects.equals(originalMessageId, that.originalMessageId) &&
               returnReason == that.returnReason &&
               Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, originalMessageId, returnReason, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Pacs004Message{messageId='%s', originalMessageId='%s', returnReason=%s, " +
                           "returnStatus=%s, returnAmount=%s, returnCurrency='%s', chargeCount=%d, creationDateTime=%s}",
                           messageId, originalMessageId, returnReason, returnStatus, returnAmount,
                           returnCurrency, returnCharges.size(), creationDateTime);
    }
}
