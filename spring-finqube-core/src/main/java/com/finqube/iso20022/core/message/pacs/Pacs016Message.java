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
 * ISO 20022 pacs.016 Direct Debit Mandate Information Request message implementation.
 *
 * <p>This message is used to request specific information about direct debit mandates.
 * It allows banks and financial institutions to query mandate details, status, and
 * related information for compliance, audit, or operational purposes.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Mandate information requests with specific query criteria</li>
 *   <li>Support for multiple request types and statuses</li>
 *   <li>Comprehensive validation and business logic</li>
 *   <li>Request tracking and response management</li>
 * </ul>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class Pacs016Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Pacs016Message.class);

    /**
     * Information request type enumeration for pacs.016 messages.
     */
    public enum InformationRequestType {
        /** Request for mandate details */
        MANDATE_DETAILS("DETL"),
        /** Request for mandate status */
        MANDATE_STATUS("STAT"),
        /** Request for mandate history */
        MANDATE_HISTORY("HIST"),
        /** Request for mandate transactions */
        MANDATE_TRANSACTIONS("TRAN"),
        /** Request for mandate parties */
        MANDATE_PARTIES("PART"),
        /** Request for mandate documents */
        MANDATE_DOCUMENTS("DOCS"),
        /** Request for mandate compliance */
        MANDATE_COMPLIANCE("COMP"),
        /** Request for mandate audit trail */
        MANDATE_AUDIT("AUDT"),
        /** Request for mandate summary */
        MANDATE_SUMMARY("SUMM"),
        /** Request for all mandate information */
        ALL_INFORMATION("ALL");

        private final String code;

        InformationRequestType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Information request status enumeration for pacs.016 messages.
     */
    public enum InformationRequestStatus {
        /** Information request pending */
        PENDING("PEND"),
        /** Information request processed */
        PROCESSED("PROC"),
        /** Information request completed */
        COMPLETED("COMP"),
        /** Information request rejected */
        REJECTED("REJT"),
        /** Information request cancelled */
        CANCELLED("CANC"),
        /** Information request under review */
        UNDER_REVIEW("REVW");

        private final String code;

        InformationRequestStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Information request details for pacs.016 messages.
     */
    public static class InformationRequest {
        private final String requestId;
        private final InformationRequestType requestType;
        private final String mandateId;
        private final String debtorId;
        private final String creditorId;
        private final LocalDate fromDate;
        private final LocalDate toDate;
        private final String requestReason;
        private final boolean includeHistory;
        private final boolean includeTransactions;
        private final boolean includeDocuments;
        private final String responseFormat;
        private final String priority;

        public InformationRequest(String requestId, InformationRequestType requestType, String mandateId,
                                String debtorId, String creditorId, LocalDate fromDate, LocalDate toDate,
                                String requestReason, boolean includeHistory, boolean includeTransactions,
                                boolean includeDocuments, String responseFormat, String priority) {
            this.requestId = requestId;
            this.requestType = requestType;
            this.mandateId = mandateId;
            this.debtorId = debtorId;
            this.creditorId = creditorId;
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.requestReason = requestReason;
            this.includeHistory = includeHistory;
            this.includeTransactions = includeTransactions;
            this.includeDocuments = includeDocuments;
            this.responseFormat = responseFormat;
            this.priority = priority;
        }

        public String getRequestId() {
            return requestId;
        }

        public InformationRequestType getRequestType() {
            return requestType;
        }

        public String getMandateId() {
            return mandateId;
        }

        public String getDebtorId() {
            return debtorId;
        }

        public String getCreditorId() {
            return creditorId;
        }

        public LocalDate getFromDate() {
            return fromDate;
        }

        public LocalDate getToDate() {
            return toDate;
        }

        public String getRequestReason() {
            return requestReason;
        }

        public boolean isIncludeHistory() {
            return includeHistory;
        }

        public boolean isIncludeTransactions() {
            return includeTransactions;
        }

        public boolean isIncludeDocuments() {
            return includeDocuments;
        }

        public String getResponseFormat() {
            return responseFormat;
        }

        public String getPriority() {
            return priority;
        }

        @Override
        public String toString() {
            return String.format("InformationRequest{requestId='%s', requestType=%s, mandateId='%s', " +
                               "debtorId='%s', creditorId='%s', fromDate=%s, toDate=%s}",
                               requestId, requestType, mandateId, debtorId, creditorId, fromDate, toDate);
        }
    }

    @NotNull
    @Size(min = 1, max = 35)
    private String messageId;

    @NotNull
    @Size(min = 1, max = 35)
    private String informationRequestId;

    @NotNull
    private InformationRequestType informationRequestType;

    @NotNull
    private InformationRequestStatus informationRequestStatus;

    @NotNull
    private LocalDateTime creationDateTime;

    private LocalDateTime responseDateTime;

    @Size(max = 35)
    private String requestingPartyId;

    @Size(max = 35)
    private String respondingPartyId;

    @Size(max = 35)
    private String debtorId;

    @Size(max = 35)
    private String creditorId;

    @Size(max = 35)
    private String mandateId;

    @Size(max = 500)
    private String requestReason;

    @Size(max = 500)
    private String responseReason;

    private final List<InformationRequest> informationRequests = new java.util.ArrayList<>();

    /**
     * Default constructor initializes required fields.
     */
    public Pacs016Message() {
        this.messageId = "PACS-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.informationRequestType = InformationRequestType.MANDATE_DETAILS;
        this.informationRequestStatus = InformationRequestStatus.PENDING;
        log.debug("Created new Pacs016Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param informationRequestId the information request identifier
     * @param informationRequestType the type of information request
     * @param mandateId the mandate identifier
     * @param debtorId the debtor identifier
     * @param creditorId the creditor identifier
     */
    public Pacs016Message(String informationRequestId, InformationRequestType informationRequestType,
                         String mandateId, String debtorId, String creditorId) {
        this();
        this.informationRequestId = informationRequestId;
        this.informationRequestType = informationRequestType;
        this.mandateId = mandateId;
        this.debtorId = debtorId;
        this.creditorId = creditorId;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getMessageType() {
        return "pacs.016";
    }

    @Override
    public MessagePriority getPriority() {
        return MessagePriority.NORMAL; // Information requests are normal priority
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
        if (informationRequestId == null || informationRequestId.trim().isEmpty()) {
            throw new MessageValidationException("Information request ID is required", messageId, getMessageType());
        }
        if (informationRequestType == null) {
            throw new MessageValidationException("Information request type is required", messageId, getMessageType());
        }
        if (informationRequestStatus == null) {
            throw new MessageValidationException("Information request status is required", messageId, getMessageType());
        }
        if (creationDateTime == null) {
            throw new MessageValidationException("Creation date time is required", messageId, getMessageType());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("Direct Debit Mandate Information Request %s with type %s and status %s",
                           informationRequestId, informationRequestType, informationRequestStatus);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return true; // Information requests require acknowledgment
    }

    @Override
    public String getSchemaVersion() {
        return "001.001.11";
    }

    @Override
    public List<String> getTransactions() {
        return informationRequests.stream()
                .map(InformationRequest::getRequestId)
                .toList();
    }

    @Override
    public int getTransactionCount() {
        return informationRequests.size();
    }

    @Override
    public double getTotalAmount() {
        return 0.0; // Information requests don't have amounts
    }

    /**
     * Sets the unique identifier for this information request message.
     *
     * @param messageId the message identifier (1-35 characters)
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the information request identifier.
     *
     * @return the information request ID
     */
    public String getInformationRequestId() {
        return informationRequestId;
    }

    /**
     * Sets the information request identifier.
     *
     * @param informationRequestId the information request ID (1-35 characters)
     */
    public void setInformationRequestId(String informationRequestId) {
        this.informationRequestId = informationRequestId;
    }

    /**
     * Gets the information request type.
     *
     * @return the information request type
     */
    public InformationRequestType getInformationRequestType() {
        return informationRequestType;
    }

    /**
     * Sets the information request type.
     *
     * @param informationRequestType the information request type
     */
    public void setInformationRequestType(InformationRequestType informationRequestType) {
        this.informationRequestType = informationRequestType;
    }

    /**
     * Gets the information request status.
     *
     * @return the information request status
     */
    public InformationRequestStatus getInformationRequestStatus() {
        return informationRequestStatus;
    }

    /**
     * Sets the information request status.
     *
     * @param informationRequestStatus the information request status
     */
    public void setInformationRequestStatus(InformationRequestStatus informationRequestStatus) {
        this.informationRequestStatus = informationRequestStatus;
    }

    /**
     * Gets the response date time.
     *
     * @return the response date time
     */
    public LocalDateTime getResponseDateTime() {
        return responseDateTime;
    }

    /**
     * Sets the response date time.
     *
     * @param responseDateTime the response date time
     */
    public void setResponseDateTime(LocalDateTime responseDateTime) {
        this.responseDateTime = responseDateTime;
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
     * Gets the responding party identifier.
     *
     * @return the responding party ID
     */
    public String getRespondingPartyId() {
        return respondingPartyId;
    }

    /**
     * Sets the responding party identifier.
     *
     * @param respondingPartyId the responding party ID (max 35 characters)
     */
    public void setRespondingPartyId(String respondingPartyId) {
        this.respondingPartyId = respondingPartyId;
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
     * @param mandateId the mandate ID (max 35 characters)
     */
    public void setMandateId(String mandateId) {
        this.mandateId = mandateId;
    }

    /**
     * Gets the request reason.
     *
     * @return the request reason
     */
    public String getRequestReason() {
        return requestReason;
    }

    /**
     * Sets the request reason.
     *
     * @param requestReason the request reason (max 500 characters)
     */
    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    /**
     * Gets the response reason.
     *
     * @return the response reason
     */
    public String getResponseReason() {
        return responseReason;
    }

    /**
     * Sets the response reason.
     *
     * @param responseReason the response reason (max 500 characters)
     */
    public void setResponseReason(String responseReason) {
        this.responseReason = responseReason;
    }

    /**
     * Gets the list of information requests.
     *
     * @return immutable list of information requests
     */
    public List<InformationRequest> getInformationRequests() {
        return List.copyOf(informationRequests);
    }

    /**
     * Adds an information request to the message.
     *
     * @param informationRequest the information request to add
     */
    public void addInformationRequest(InformationRequest informationRequest) {
        informationRequests.add(informationRequest);
        log.debug("Added information request: {}", informationRequest.getRequestId());
    }

    /**
     * Gets information request by request ID.
     *
     * @param requestId the request ID
     * @return the information request, or null if not found
     */
    public InformationRequest getInformationRequest(String requestId) {
        return informationRequests.stream()
                .filter(req -> Objects.equals(req.getRequestId(), requestId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if this is a mandate details request.
     *
     * @return true if the information request type is MANDATE_DETAILS
     */
    public boolean isMandateDetailsRequest() {
        return InformationRequestType.MANDATE_DETAILS.equals(informationRequestType);
    }

    /**
     * Checks if this is a mandate status request.
     *
     * @return true if the information request type is MANDATE_STATUS
     */
    public boolean isMandateStatusRequest() {
        return InformationRequestType.MANDATE_STATUS.equals(informationRequestType);
    }

    /**
     * Checks if this is a mandate history request.
     *
     * @return true if the information request type is MANDATE_HISTORY
     */
    public boolean isMandateHistoryRequest() {
        return InformationRequestType.MANDATE_HISTORY.equals(informationRequestType);
    }

    /**
     * Checks if this is a mandate transactions request.
     *
     * @return true if the information request type is MANDATE_TRANSACTIONS
     */
    public boolean isMandateTransactionsRequest() {
        return InformationRequestType.MANDATE_TRANSACTIONS.equals(informationRequestType);
    }

    /**
     * Checks if this is a mandate parties request.
     *
     * @return true if the information request type is MANDATE_PARTIES
     */
    public boolean isMandatePartiesRequest() {
        return InformationRequestType.MANDATE_PARTIES.equals(informationRequestType);
    }

    /**
     * Checks if this is a mandate documents request.
     *
     * @return true if the information request type is MANDATE_DOCUMENTS
     */
    public boolean isMandateDocumentsRequest() {
        return InformationRequestType.MANDATE_DOCUMENTS.equals(informationRequestType);
    }

    /**
     * Checks if this is a mandate compliance request.
     *
     * @return true if the information request type is MANDATE_COMPLIANCE
     */
    public boolean isMandateComplianceRequest() {
        return InformationRequestType.MANDATE_COMPLIANCE.equals(informationRequestType);
    }

    /**
     * Checks if this is a mandate audit request.
     *
     * @return true if the information request type is MANDATE_AUDIT
     */
    public boolean isMandateAuditRequest() {
        return InformationRequestType.MANDATE_AUDIT.equals(informationRequestType);
    }

    /**
     * Checks if this is a mandate summary request.
     *
     * @return true if the information request type is MANDATE_SUMMARY
     */
    public boolean isMandateSummaryRequest() {
        return InformationRequestType.MANDATE_SUMMARY.equals(informationRequestType);
    }

    /**
     * Checks if this is an all information request.
     *
     * @return true if the information request type is ALL_INFORMATION
     */
    public boolean isAllInformationRequest() {
        return InformationRequestType.ALL_INFORMATION.equals(informationRequestType);
    }

    /**
     * Checks if the information request is pending.
     *
     * @return true if the information request status is PENDING
     */
    public boolean isPending() {
        return InformationRequestStatus.PENDING.equals(informationRequestStatus);
    }

    /**
     * Checks if the information request is processed.
     *
     * @return true if the information request status is PROCESSED
     */
    public boolean isProcessed() {
        return InformationRequestStatus.PROCESSED.equals(informationRequestStatus);
    }

    /**
     * Checks if the information request is completed.
     *
     * @return true if the information request status is COMPLETED
     */
    public boolean isCompleted() {
        return InformationRequestStatus.COMPLETED.equals(informationRequestStatus);
    }

    /**
     * Checks if the information request is rejected.
     *
     * @return true if the information request status is REJECTED
     */
    public boolean isRejected() {
        return InformationRequestStatus.REJECTED.equals(informationRequestStatus);
    }

    /**
     * Checks if the information request is cancelled.
     *
     * @return true if the information request status is CANCELLED
     */
    public boolean isCancelled() {
        return InformationRequestStatus.CANCELLED.equals(informationRequestStatus);
    }

    /**
     * Checks if the information request is under review.
     *
     * @return true if the information request status is UNDER_REVIEW
     */
    public boolean isUnderReview() {
        return InformationRequestStatus.UNDER_REVIEW.equals(informationRequestStatus);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pacs016Message that = (Pacs016Message) obj;
        return Objects.equals(messageId, that.messageId) &&
               Objects.equals(informationRequestId, that.informationRequestId) &&
               informationRequestType == that.informationRequestType &&
               Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, informationRequestId, informationRequestType, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Pacs016Message{messageId='%s', informationRequestId='%s', informationRequestType=%s, " +
                           "informationRequestStatus=%s, mandateId='%s', debtorId='%s', creditorId='%s', creationDateTime=%s}",
                           messageId, informationRequestId, informationRequestType, informationRequestStatus,
                           mandateId, debtorId, creditorId, creationDateTime);
    }
}
