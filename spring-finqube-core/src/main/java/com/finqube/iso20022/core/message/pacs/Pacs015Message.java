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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * ISO 20022 pacs.015 Direct Debit Mandate Cancellation Request message implementation.
 *
 * <p>This message is used to request cancellation of existing direct debit mandates.
 * It allows banks and financial institutions to request the termination of mandate
 * agreements with proper authorization and documentation.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Mandate cancellation requests with detailed cancellation specifications</li>
 *   <li>Support for multiple cancellation types and statuses</li>
 *   <li>Comprehensive validation and business logic</li>
 *   <li>Cancellation tracking and approval workflows</li>
 * </ul>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class Pacs015Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Pacs015Message.class);

    /**
     * Cancellation request type enumeration for pacs.015 messages.
     */
    public enum CancellationRequestType {
        /** Customer request for cancellation */
        CUSTOMER_REQUEST("CUST"),
        /** Account closed cancellation */
        ACCOUNT_CLOSED("ACCT"),
        /** Insufficient funds cancellation */
        INSUFFICIENT_FUNDS("FUND"),
        /** Unauthorized transaction cancellation */
        UNAUTHORIZED("AUTH"),
        /** Duplicate mandate cancellation */
        DUPLICATE_MANDATE("DUPL"),
        /** Technical error cancellation */
        TECHNICAL_ERROR("TECH"),
        /** Regulatory compliance issue cancellation */
        COMPLIANCE_ISSUE("COMP"),
        /** Mandate expired cancellation */
        MANDATE_EXPIRED("EXPR"),
        /** Creditor request cancellation */
        CREDITOR_REQUEST("CRED"),
        /** Bank request cancellation */
        BANK_REQUEST("BANK"),
        /** Other reason cancellation */
        OTHER("OTHR");

        private final String code;

        CancellationRequestType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Cancellation request status enumeration for pacs.015 messages.
     */
    public enum CancellationRequestStatus {
        /** Cancellation request pending */
        PENDING("PEND"),
        /** Cancellation request approved */
        APPROVED("APPR"),
        /** Cancellation request rejected */
        REJECTED("REJT"),
        /** Cancellation request cancelled */
        CANCELLED("CANC"),
        /** Cancellation request completed */
        COMPLETED("COMP"),
        /** Cancellation request under review */
        UNDER_REVIEW("REVW");

        private final String code;

        CancellationRequestStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Cancellation request details for pacs.015 messages.
     */
    public static class CancellationRequest {
        private final String cancellationId;
        private final String originalMandateId;
        private final CancellationRequestType requestType;
        private final String reason;
        private final LocalDate requestedEffectiveDate;
        private final String requestingPartyId;
        private final String approvingPartyId;
        private final String debtorId;
        private final String creditorId;
        private final String debtorAccountId;
        private final String creditorAccountId;
        private final String documentationReference;
        private final boolean requiresDebtorApproval;
        private final boolean requiresCreditorApproval;
        private final boolean immediateCancellation;

        public CancellationRequest(String cancellationId, String originalMandateId, CancellationRequestType requestType,
                                 String reason, LocalDate requestedEffectiveDate, String requestingPartyId,
                                 String approvingPartyId, String debtorId, String creditorId, String debtorAccountId,
                                 String creditorAccountId, String documentationReference, boolean requiresDebtorApproval,
                                 boolean requiresCreditorApproval, boolean immediateCancellation) {
            this.cancellationId = cancellationId;
            this.originalMandateId = originalMandateId;
            this.requestType = requestType;
            this.reason = reason;
            this.requestedEffectiveDate = requestedEffectiveDate;
            this.requestingPartyId = requestingPartyId;
            this.approvingPartyId = approvingPartyId;
            this.debtorId = debtorId;
            this.creditorId = creditorId;
            this.debtorAccountId = debtorAccountId;
            this.creditorAccountId = creditorAccountId;
            this.documentationReference = documentationReference;
            this.requiresDebtorApproval = requiresDebtorApproval;
            this.requiresCreditorApproval = requiresCreditorApproval;
            this.immediateCancellation = immediateCancellation;
        }

        public String getCancellationId() {
            return cancellationId;
        }

        public String getOriginalMandateId() {
            return originalMandateId;
        }

        public CancellationRequestType getRequestType() {
            return requestType;
        }

        public String getReason() {
            return reason;
        }

        public LocalDate getRequestedEffectiveDate() {
            return requestedEffectiveDate;
        }

        public String getRequestingPartyId() {
            return requestingPartyId;
        }

        public String getApprovingPartyId() {
            return approvingPartyId;
        }

        public String getDebtorId() {
            return debtorId;
        }

        public String getCreditorId() {
            return creditorId;
        }

        public String getDebtorAccountId() {
            return debtorAccountId;
        }

        public String getCreditorAccountId() {
            return creditorAccountId;
        }

        public String getDocumentationReference() {
            return documentationReference;
        }

        public boolean isRequiresDebtorApproval() {
            return requiresDebtorApproval;
        }

        public boolean isRequiresCreditorApproval() {
            return requiresCreditorApproval;
        }

        public boolean isImmediateCancellation() {
            return immediateCancellation;
        }

        @Override
        public String toString() {
            return String.format("CancellationRequest{cancellationId='%s', originalMandateId='%s', requestType=%s, " +
                               "reason='%s', requestedEffectiveDate=%s}",
                               cancellationId, originalMandateId, requestType, reason, requestedEffectiveDate);
        }
    }

    @NotNull
    @Size(min = 1, max = 35)
    private String messageId;

    @NotNull
    @Size(min = 1, max = 35)
    private String cancellationRequestId;

    @NotNull
    private CancellationRequestType cancellationRequestType;

    @NotNull
    private CancellationRequestStatus cancellationRequestStatus;

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
    private String cancellationReason;

    @Size(max = 500)
    private String approvalReason;

    private final List<CancellationRequest> cancellationRequests = new java.util.ArrayList<>();

    /**
     * Default constructor initializes required fields.
     */
    public Pacs015Message() {
        this.messageId = "PACS-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.cancellationRequestType = CancellationRequestType.CUSTOMER_REQUEST;
        this.cancellationRequestStatus = CancellationRequestStatus.PENDING;
        log.debug("Created new Pacs015Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param cancellationRequestId the cancellation request identifier
     * @param cancellationRequestType the type of cancellation request
     * @param originalMandateId the original mandate identifier
     * @param debtorId the debtor identifier
     * @param creditorId the creditor identifier
     */
    public Pacs015Message(String cancellationRequestId, CancellationRequestType cancellationRequestType,
                         String originalMandateId, String debtorId, String creditorId) {
        this();
        this.cancellationRequestId = cancellationRequestId;
        this.cancellationRequestType = cancellationRequestType;
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
        return "pacs.015";
    }

    @Override
    public MessagePriority getPriority() {
        return MessagePriority.HIGH; // Cancellation requests are high priority
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
        if (cancellationRequestId == null || cancellationRequestId.trim().isEmpty()) {
            throw new MessageValidationException("Cancellation request ID is required", messageId, getMessageType());
        }
        if (cancellationRequestType == null) {
            throw new MessageValidationException("Cancellation request type is required", messageId, getMessageType());
        }
        if (cancellationRequestStatus == null) {
            throw new MessageValidationException("Cancellation request status is required", messageId, getMessageType());
        }
        if (creationDateTime == null) {
            throw new MessageValidationException("Creation date time is required", messageId, getMessageType());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("Direct Debit Mandate Cancellation Request %s with type %s and status %s",
                           cancellationRequestId, cancellationRequestType, cancellationRequestStatus);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return true; // Cancellation requests require acknowledgment
    }

    @Override
    public String getSchemaVersion() {
        return "001.001.11";
    }

    @Override
    public List<String> getTransactions() {
        return cancellationRequests.stream()
                .map(CancellationRequest::getCancellationId)
                .toList();
    }

    @Override
    public int getTransactionCount() {
        return cancellationRequests.size();
    }

    @Override
    public double getTotalAmount() {
        return 0.0; // Cancellation requests don't have amounts
    }

    /**
     * Sets the unique identifier for this cancellation request message.
     *
     * @param messageId the message identifier (1-35 characters)
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the cancellation request identifier.
     *
     * @return the cancellation request ID
     */
    public String getCancellationRequestId() {
        return cancellationRequestId;
    }

    /**
     * Sets the cancellation request identifier.
     *
     * @param cancellationRequestId the cancellation request ID (1-35 characters)
     */
    public void setCancellationRequestId(String cancellationRequestId) {
        this.cancellationRequestId = cancellationRequestId;
    }

    /**
     * Gets the cancellation request type.
     *
     * @return the cancellation request type
     */
    public CancellationRequestType getCancellationRequestType() {
        return cancellationRequestType;
    }

    /**
     * Sets the cancellation request type.
     *
     * @param cancellationRequestType the cancellation request type
     */
    public void setCancellationRequestType(CancellationRequestType cancellationRequestType) {
        this.cancellationRequestType = cancellationRequestType;
    }

    /**
     * Gets the cancellation request status.
     *
     * @return the cancellation request status
     */
    public CancellationRequestStatus getCancellationRequestStatus() {
        return cancellationRequestStatus;
    }

    /**
     * Sets the cancellation request status.
     *
     * @param cancellationRequestStatus the cancellation request status
     */
    public void setCancellationRequestStatus(CancellationRequestStatus cancellationRequestStatus) {
        this.cancellationRequestStatus = cancellationRequestStatus;
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
     * Gets the cancellation reason.
     *
     * @return the cancellation reason
     */
    public String getCancellationReason() {
        return cancellationReason;
    }

    /**
     * Sets the cancellation reason.
     *
     * @param cancellationReason the cancellation reason (max 500 characters)
     */
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
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
     * Gets the list of cancellation requests.
     *
     * @return immutable list of cancellation requests
     */
    public List<CancellationRequest> getCancellationRequests() {
        return List.copyOf(cancellationRequests);
    }

    /**
     * Adds a cancellation request to the message.
     *
     * @param cancellationRequest the cancellation request to add
     */
    public void addCancellationRequest(CancellationRequest cancellationRequest) {
        cancellationRequests.add(cancellationRequest);
        log.debug("Added cancellation request: {}", cancellationRequest.getCancellationId());
    }

    /**
     * Gets cancellation request by cancellation ID.
     *
     * @param cancellationId the cancellation ID
     * @return the cancellation request, or null if not found
     */
    public CancellationRequest getCancellationRequest(String cancellationId) {
        return cancellationRequests.stream()
                .filter(req -> Objects.equals(req.getCancellationId(), cancellationId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if this is a customer request cancellation.
     *
     * @return true if the cancellation request type is CUSTOMER_REQUEST
     */
    public boolean isCustomerRequestCancellation() {
        return CancellationRequestType.CUSTOMER_REQUEST.equals(cancellationRequestType);
    }

    /**
     * Checks if this is an account closed cancellation.
     *
     * @return true if the cancellation request type is ACCOUNT_CLOSED
     */
    public boolean isAccountClosedCancellation() {
        return CancellationRequestType.ACCOUNT_CLOSED.equals(cancellationRequestType);
    }

    /**
     * Checks if this is an insufficient funds cancellation.
     *
     * @return true if the cancellation request type is INSUFFICIENT_FUNDS
     */
    public boolean isInsufficientFundsCancellation() {
        return CancellationRequestType.INSUFFICIENT_FUNDS.equals(cancellationRequestType);
    }

    /**
     * Checks if this is an unauthorized cancellation.
     *
     * @return true if the cancellation request type is UNAUTHORIZED
     */
    public boolean isUnauthorizedCancellation() {
        return CancellationRequestType.UNAUTHORIZED.equals(cancellationRequestType);
    }

    /**
     * Checks if this is a duplicate mandate cancellation.
     *
     * @return true if the cancellation request type is DUPLICATE_MANDATE
     */
    public boolean isDuplicateMandateCancellation() {
        return CancellationRequestType.DUPLICATE_MANDATE.equals(cancellationRequestType);
    }

    /**
     * Checks if this is a technical error cancellation.
     *
     * @return true if the cancellation request type is TECHNICAL_ERROR
     */
    public boolean isTechnicalErrorCancellation() {
        return CancellationRequestType.TECHNICAL_ERROR.equals(cancellationRequestType);
    }

    /**
     * Checks if this is a compliance issue cancellation.
     *
     * @return true if the cancellation request type is COMPLIANCE_ISSUE
     */
    public boolean isComplianceIssueCancellation() {
        return CancellationRequestType.COMPLIANCE_ISSUE.equals(cancellationRequestType);
    }

    /**
     * Checks if this is a mandate expired cancellation.
     *
     * @return true if the cancellation request type is MANDATE_EXPIRED
     */
    public boolean isMandateExpiredCancellation() {
        return CancellationRequestType.MANDATE_EXPIRED.equals(cancellationRequestType);
    }

    /**
     * Checks if this is a creditor request cancellation.
     *
     * @return true if the cancellation request type is CREDITOR_REQUEST
     */
    public boolean isCreditorRequestCancellation() {
        return CancellationRequestType.CREDITOR_REQUEST.equals(cancellationRequestType);
    }

    /**
     * Checks if this is a bank request cancellation.
     *
     * @return true if the cancellation request type is BANK_REQUEST
     */
    public boolean isBankRequestCancellation() {
        return CancellationRequestType.BANK_REQUEST.equals(cancellationRequestType);
    }

    /**
     * Checks if this is an other reason cancellation.
     *
     * @return true if the cancellation request type is OTHER
     */
    public boolean isOtherReasonCancellation() {
        return CancellationRequestType.OTHER.equals(cancellationRequestType);
    }

    /**
     * Checks if the cancellation request is pending.
     *
     * @return true if the cancellation request status is PENDING
     */
    public boolean isPending() {
        return CancellationRequestStatus.PENDING.equals(cancellationRequestStatus);
    }

    /**
     * Checks if the cancellation request is approved.
     *
     * @return true if the cancellation request status is APPROVED
     */
    public boolean isApproved() {
        return CancellationRequestStatus.APPROVED.equals(cancellationRequestStatus);
    }

    /**
     * Checks if the cancellation request is rejected.
     *
     * @return true if the cancellation request status is REJECTED
     */
    public boolean isRejected() {
        return CancellationRequestStatus.REJECTED.equals(cancellationRequestStatus);
    }

    /**
     * Checks if the cancellation request is cancelled.
     *
     * @return true if the cancellation request status is CANCELLED
     */
    public boolean isCancelled() {
        return CancellationRequestStatus.CANCELLED.equals(cancellationRequestStatus);
    }

    /**
     * Checks if the cancellation request is completed.
     *
     * @return true if the cancellation request status is COMPLETED
     */
    public boolean isCompleted() {
        return CancellationRequestStatus.COMPLETED.equals(cancellationRequestStatus);
    }

    /**
     * Checks if the cancellation request is under review.
     *
     * @return true if the cancellation request status is UNDER_REVIEW
     */
    public boolean isUnderReview() {
        return CancellationRequestStatus.UNDER_REVIEW.equals(cancellationRequestStatus);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pacs015Message that = (Pacs015Message) obj;
        return Objects.equals(messageId, that.messageId) &&
               Objects.equals(cancellationRequestId, that.cancellationRequestId) &&
               cancellationRequestType == that.cancellationRequestType &&
               Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, cancellationRequestId, cancellationRequestType, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Pacs015Message{messageId='%s', cancellationRequestId='%s', cancellationRequestType=%s, " +
                           "cancellationRequestStatus=%s, originalMandateId='%s', debtorId='%s', creditorId='%s', creationDateTime=%s}",
                           messageId, cancellationRequestId, cancellationRequestType, cancellationRequestStatus,
                           originalMandateId, debtorId, creditorId, creationDateTime);
    }
}
