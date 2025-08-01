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
 * ISO 20022 pacs.014 Direct Debit Mandate Amendment Request message implementation.
 *
 * <p>This message is used to request amendments to existing direct debit mandates.
 * It allows banks and financial institutions to request changes to mandate details
 * such as amounts, dates, parties, or other parameters.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Mandate amendment requests with detailed change specifications</li>
 *   <li>Support for multiple amendment types and statuses</li>
 *   <li>Comprehensive validation and business logic</li>
 *   <li>Amendment tracking and approval workflows</li>
 * </ul>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class Pacs014Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Pacs014Message.class);

    /**
     * Amendment request type enumeration for pacs.014 messages.
     */
    public enum AmendmentRequestType {
        /** Amount change request */
        AMOUNT_CHANGE("AMNT"),
        /** Frequency change request */
        FREQUENCY_CHANGE("FREQ"),
        /** Date change request */
        DATE_CHANGE("DATE"),
        /** Account change request */
        ACCOUNT_CHANGE("ACCT"),
        /** Creditor change request */
        CREDITOR_CHANGE("CRED"),
        /** Description change request */
        DESCRIPTION_CHANGE("DESC"),
        /** Currency change request */
        CURRENCY_CHANGE("CURR"),
        /** Multiple changes request */
        MULTIPLE_CHANGES("MULT"),
        /** Other change request */
        OTHER("OTHR");

        private final String code;

        AmendmentRequestType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Amendment request status enumeration for pacs.014 messages.
     */
    public enum AmendmentRequestStatus {
        /** Amendment request pending */
        PENDING("PEND"),
        /** Amendment request approved */
        APPROVED("APPR"),
        /** Amendment request rejected */
        REJECTED("REJT"),
        /** Amendment request cancelled */
        CANCELLED("CANC"),
        /** Amendment request completed */
        COMPLETED("COMP"),
        /** Amendment request under review */
        UNDER_REVIEW("REVW");

        private final String code;

        AmendmentRequestStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Amendment request details for pacs.014 messages.
     */
    public static class AmendmentRequest {
        private final String amendmentId;
        private final String originalMandateId;
        private final AmendmentRequestType requestType;
        private final String reason;
        private final LocalDate requestedEffectiveDate;
        private final BigDecimal newAmount;
        private final String newCurrency;
        private final String newFrequency;
        private final LocalDate newStartDate;
        private final LocalDate newEndDate;
        private final String newDebtorId;
        private final String newCreditorId;
        private final String newDebtorAccountId;
        private final String newCreditorAccountId;
        private final String newDescription;

        public AmendmentRequest(String amendmentId, String originalMandateId, AmendmentRequestType requestType,
                              String reason, LocalDate requestedEffectiveDate, BigDecimal newAmount, String newCurrency,
                              String newFrequency, LocalDate newStartDate, LocalDate newEndDate, String newDebtorId,
                              String newCreditorId, String newDebtorAccountId, String newCreditorAccountId,
                              String newDescription) {
            this.amendmentId = amendmentId;
            this.originalMandateId = originalMandateId;
            this.requestType = requestType;
            this.reason = reason;
            this.requestedEffectiveDate = requestedEffectiveDate;
            this.newAmount = newAmount;
            this.newCurrency = newCurrency;
            this.newFrequency = newFrequency;
            this.newStartDate = newStartDate;
            this.newEndDate = newEndDate;
            this.newDebtorId = newDebtorId;
            this.newCreditorId = newCreditorId;
            this.newDebtorAccountId = newDebtorAccountId;
            this.newCreditorAccountId = newCreditorAccountId;
            this.newDescription = newDescription;
        }

        public String getAmendmentId() {
            return amendmentId;
        }

        public String getOriginalMandateId() {
            return originalMandateId;
        }

        public AmendmentRequestType getRequestType() {
            return requestType;
        }

        public String getReason() {
            return reason;
        }

        public LocalDate getRequestedEffectiveDate() {
            return requestedEffectiveDate;
        }

        public BigDecimal getNewAmount() {
            return newAmount;
        }

        public String getNewCurrency() {
            return newCurrency;
        }

        public String getNewFrequency() {
            return newFrequency;
        }

        public LocalDate getNewStartDate() {
            return newStartDate;
        }

        public LocalDate getNewEndDate() {
            return newEndDate;
        }

        public String getNewDebtorId() {
            return newDebtorId;
        }

        public String getNewCreditorId() {
            return newCreditorId;
        }

        public String getNewDebtorAccountId() {
            return newDebtorAccountId;
        }

        public String getNewCreditorAccountId() {
            return newCreditorAccountId;
        }

        public String getNewDescription() {
            return newDescription;
        }

        @Override
        public String toString() {
            return String.format("AmendmentRequest{amendmentId='%s', originalMandateId='%s', requestType=%s, " +
                               "reason='%s', requestedEffectiveDate=%s}",
                               amendmentId, originalMandateId, requestType, reason, requestedEffectiveDate);
        }
    }

    @NotNull
    @Size(min = 1, max = 35)
    private String messageId;

    @NotNull
    @Size(min = 1, max = 35)
    private String amendmentRequestId;

    @NotNull
    private AmendmentRequestType amendmentRequestType;

    @NotNull
    private AmendmentRequestStatus amendmentRequestStatus;

    @NotNull
    private LocalDateTime creationDateTime;

    private LocalDateTime approvalDateTime;

    @Size(max = 35)
    private String requestingPartyId;

    @Size(max = 35)
    private String approvingPartyId;

    @Size(max = 35)
    private String debtorId;

    @Size(max = 35)
    private String creditorId;

    @Size(max = 35)
    private String originalMandateId;

    @Size(max = 500)
    private String amendmentReason;

    @Size(max = 500)
    private String approvalReason;

    private final List<AmendmentRequest> amendmentRequests = new java.util.ArrayList<>();

    private BigDecimal totalAmendmentAmount = BigDecimal.ZERO;

    /**
     * Default constructor initializes required fields.
     */
    public Pacs014Message() {
        this.messageId = "PACS-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.amendmentRequestType = AmendmentRequestType.OTHER;
        this.amendmentRequestStatus = AmendmentRequestStatus.PENDING;
        log.debug("Created new Pacs014Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param amendmentRequestId the amendment request identifier
     * @param amendmentRequestType the type of amendment request
     * @param originalMandateId the original mandate identifier
     * @param debtorId the debtor identifier
     * @param creditorId the creditor identifier
     */
    public Pacs014Message(String amendmentRequestId, AmendmentRequestType amendmentRequestType,
                         String originalMandateId, String debtorId, String creditorId) {
        this();
        this.amendmentRequestId = amendmentRequestId;
        this.amendmentRequestType = amendmentRequestType;
        this.originalMandateId = originalMandateId;
        this.debtorId = debtorId;
        this.creditorId = creditorId;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getMessageType() {
        return "pacs.014";
    }

    @Override
    public MessagePriority getPriority() {
        return MessagePriority.HIGH; // Amendment requests are high priority
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
        if (amendmentRequestId == null || amendmentRequestId.trim().isEmpty()) {
            throw new MessageValidationException("Amendment request ID is required", messageId, getMessageType());
        }
        if (amendmentRequestType == null) {
            throw new MessageValidationException("Amendment request type is required", messageId, getMessageType());
        }
        if (amendmentRequestStatus == null) {
            throw new MessageValidationException("Amendment request status is required", messageId, getMessageType());
        }
        if (creationDateTime == null) {
            throw new MessageValidationException("Creation date time is required", messageId, getMessageType());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("Direct Debit Mandate Amendment Request %s with type %s and status %s",
                           amendmentRequestId, amendmentRequestType, amendmentRequestStatus);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return true; // Amendment requests require acknowledgment
    }

    @Override
    public String getSchemaVersion() {
        return "001.001.11";
    }

    @Override
    public List<String> getTransactions() {
        return amendmentRequests.stream()
                .map(AmendmentRequest::getAmendmentId)
                .toList();
    }

    @Override
    public int getTransactionCount() {
        return amendmentRequests.size();
    }

    @Override
    public double getTotalAmount() {
        return totalAmendmentAmount.doubleValue();
    }

    /**
     * Sets the unique identifier for this amendment request message.
     *
     * @param messageId the message identifier (1-35 characters)
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the amendment request identifier.
     *
     * @return the amendment request ID
     */
    public String getAmendmentRequestId() {
        return amendmentRequestId;
    }

    /**
     * Sets the amendment request identifier.
     *
     * @param amendmentRequestId the amendment request ID (1-35 characters)
     */
    public void setAmendmentRequestId(String amendmentRequestId) {
        this.amendmentRequestId = amendmentRequestId;
    }

    /**
     * Gets the amendment request type.
     *
     * @return the amendment request type
     */
    public AmendmentRequestType getAmendmentRequestType() {
        return amendmentRequestType;
    }

    /**
     * Sets the amendment request type.
     *
     * @param amendmentRequestType the amendment request type
     */
    public void setAmendmentRequestType(AmendmentRequestType amendmentRequestType) {
        this.amendmentRequestType = amendmentRequestType;
    }

    /**
     * Gets the amendment request status.
     *
     * @return the amendment request status
     */
    public AmendmentRequestStatus getAmendmentRequestStatus() {
        return amendmentRequestStatus;
    }

    /**
     * Sets the amendment request status.
     *
     * @param amendmentRequestStatus the amendment request status
     */
    public void setAmendmentRequestStatus(AmendmentRequestStatus amendmentRequestStatus) {
        this.amendmentRequestStatus = amendmentRequestStatus;
    }

    /**
     * Gets the approval date time.
     *
     * @return the approval date time
     */
    public LocalDateTime getApprovalDateTime() {
        return approvalDateTime;
    }

    /**
     * Sets the approval date time.
     *
     * @param approvalDateTime the approval date time
     */
    public void setApprovalDateTime(LocalDateTime approvalDateTime) {
        this.approvalDateTime = approvalDateTime;
    }

    /**
     * Gets the requesting party identifier.
     *
     * @return the requesting party ID
     */
    public String getRequestingPartyId() {
        return requestingPartyId;
    }

    /**
     * Sets the requesting party identifier.
     *
     * @param requestingPartyId the requesting party ID (max 35 characters)
     */
    public void setRequestingPartyId(String requestingPartyId) {
        this.requestingPartyId = requestingPartyId;
    }

    /**
     * Gets the approving party identifier.
     *
     * @return the approving party ID
     */
    public String getApprovingPartyId() {
        return approvingPartyId;
    }

    /**
     * Sets the approving party identifier.
     *
     * @param approvingPartyId the approving party ID (max 35 characters)
     */
    public void setApprovingPartyId(String approvingPartyId) {
        this.approvingPartyId = approvingPartyId;
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
     * @param originalMandateId the original mandate ID (max 35 characters)
     */
    public void setOriginalMandateId(String originalMandateId) {
        this.originalMandateId = originalMandateId;
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
     * @param amendmentReason the amendment reason (max 500 characters)
     */
    public void setAmendmentReason(String amendmentReason) {
        this.amendmentReason = amendmentReason;
    }

    /**
     * Gets the approval reason.
     *
     * @return the approval reason
     */
    public String getApprovalReason() {
        return approvalReason;
    }

    /**
     * Sets the approval reason.
     *
     * @param approvalReason the approval reason (max 500 characters)
     */
    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }

    /**
     * Gets the list of amendment requests.
     *
     * @return immutable list of amendment requests
     */
    public List<AmendmentRequest> getAmendmentRequests() {
        return List.copyOf(amendmentRequests);
    }

    /**
     * Adds an amendment request to the message.
     *
     * @param amendmentRequest the amendment request to add
     */
    public void addAmendmentRequest(AmendmentRequest amendmentRequest) {
        amendmentRequests.add(amendmentRequest);
        if (amendmentRequest.getNewAmount() != null) {
            totalAmendmentAmount = totalAmendmentAmount.add(amendmentRequest.getNewAmount());
        }
        log.debug("Added amendment request: {}", amendmentRequest.getAmendmentId());
    }

    /**
     * Gets amendment request by amendment ID.
     *
     * @param amendmentId the amendment ID
     * @return the amendment request, or null if not found
     */
    public AmendmentRequest getAmendmentRequest(String amendmentId) {
        return amendmentRequests.stream()
                .filter(req -> Objects.equals(req.getAmendmentId(), amendmentId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the total amendment amount.
     *
     * @return the total amendment amount
     */
    public BigDecimal getTotalAmendmentAmount() {
        return totalAmendmentAmount;
    }

    /**
     * Checks if this is an amount change request.
     *
     * @return true if the amendment request type is AMOUNT_CHANGE
     */
    public boolean isAmountChangeRequest() {
        return AmendmentRequestType.AMOUNT_CHANGE.equals(amendmentRequestType);
    }

    /**
     * Checks if this is a frequency change request.
     *
     * @return true if the amendment request type is FREQUENCY_CHANGE
     */
    public boolean isFrequencyChangeRequest() {
        return AmendmentRequestType.FREQUENCY_CHANGE.equals(amendmentRequestType);
    }

    /**
     * Checks if this is a date change request.
     *
     * @return true if the amendment request type is DATE_CHANGE
     */
    public boolean isDateChangeRequest() {
        return AmendmentRequestType.DATE_CHANGE.equals(amendmentRequestType);
    }

    /**
     * Checks if this is an account change request.
     *
     * @return true if the amendment request type is ACCOUNT_CHANGE
     */
    public boolean isAccountChangeRequest() {
        return AmendmentRequestType.ACCOUNT_CHANGE.equals(amendmentRequestType);
    }

    /**
     * Checks if this is a creditor change request.
     *
     * @return true if the amendment request type is CREDITOR_CHANGE
     */
    public boolean isCreditorChangeRequest() {
        return AmendmentRequestType.CREDITOR_CHANGE.equals(amendmentRequestType);
    }

    /**
     * Checks if this is a description change request.
     *
     * @return true if the amendment request type is DESCRIPTION_CHANGE
     */
    public boolean isDescriptionChangeRequest() {
        return AmendmentRequestType.DESCRIPTION_CHANGE.equals(amendmentRequestType);
    }

    /**
     * Checks if this is a currency change request.
     *
     * @return true if the amendment request type is CURRENCY_CHANGE
     */
    public boolean isCurrencyChangeRequest() {
        return AmendmentRequestType.CURRENCY_CHANGE.equals(amendmentRequestType);
    }

    /**
     * Checks if this is a multiple changes request.
     *
     * @return true if the amendment request type is MULTIPLE_CHANGES
     */
    public boolean isMultipleChangesRequest() {
        return AmendmentRequestType.MULTIPLE_CHANGES.equals(amendmentRequestType);
    }

    /**
     * Checks if this is an other change request.
     *
     * @return true if the amendment request type is OTHER
     */
    public boolean isOtherChangeRequest() {
        return AmendmentRequestType.OTHER.equals(amendmentRequestType);
    }

    /**
     * Checks if the amendment request is pending.
     *
     * @return true if the amendment request status is PENDING
     */
    public boolean isPending() {
        return AmendmentRequestStatus.PENDING.equals(amendmentRequestStatus);
    }

    /**
     * Checks if the amendment request is approved.
     *
     * @return true if the amendment request status is APPROVED
     */
    public boolean isApproved() {
        return AmendmentRequestStatus.APPROVED.equals(amendmentRequestStatus);
    }

    /**
     * Checks if the amendment request is rejected.
     *
     * @return true if the amendment request status is REJECTED
     */
    public boolean isRejected() {
        return AmendmentRequestStatus.REJECTED.equals(amendmentRequestStatus);
    }

    /**
     * Checks if the amendment request is cancelled.
     *
     * @return true if the amendment request status is CANCELLED
     */
    public boolean isCancelled() {
        return AmendmentRequestStatus.CANCELLED.equals(amendmentRequestStatus);
    }

    /**
     * Checks if the amendment request is completed.
     *
     * @return true if the amendment request status is COMPLETED
     */
    public boolean isCompleted() {
        return AmendmentRequestStatus.COMPLETED.equals(amendmentRequestStatus);
    }

    /**
     * Checks if the amendment request is under review.
     *
     * @return true if the amendment request status is UNDER_REVIEW
     */
    public boolean isUnderReview() {
        return AmendmentRequestStatus.UNDER_REVIEW.equals(amendmentRequestStatus);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pacs014Message that = (Pacs014Message) obj;
        return Objects.equals(messageId, that.messageId) &&
               Objects.equals(amendmentRequestId, that.amendmentRequestId) &&
               amendmentRequestType == that.amendmentRequestType &&
               Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, amendmentRequestId, amendmentRequestType, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Pacs014Message{messageId='%s', amendmentRequestId='%s', amendmentRequestType=%s, " +
                           "amendmentRequestStatus=%s, originalMandateId='%s', debtorId='%s', creditorId='%s', creationDateTime=%s}",
                           messageId, amendmentRequestId, amendmentRequestType, amendmentRequestStatus,
                           originalMandateId, debtorId, creditorId, creationDateTime);
    }
}
