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
 * ISO 20022 pacs.018 Direct Debit Mandate Cancellation Response message implementation.
 *
 * <p>This message is used to respond to mandate cancellation requests. It allows banks
 * and financial institutions to provide approval, rejection, or modification responses
 * to cancellation requests with detailed reasoning and conditions.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Mandate cancellation responses with approval/rejection status</li>
 *   <li>Support for multiple response types and statuses</li>
 *   <li>Comprehensive validation and business logic</li>
 *   <li>Response tracking and cancellation implementation</li>
 * </ul>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class Pacs018Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Pacs018Message.class);

    /**
     * Cancellation response type enumeration for pacs.018 messages.
     */
    public enum CancellationResponseType {
        /** Cancellation request approved */
        APPROVED("APPR"),
        /** Cancellation request rejected */
        REJECTED("REJT"),
        /** Cancellation request modified */
        MODIFIED("MODF"),
        /** Cancellation request pending additional information */
        PENDING_INFO("PEND"),
        /** Cancellation request under review */
        UNDER_REVIEW("REVW"),
        /** Cancellation request cancelled */
        CANCELLED("CANC");

        private final String code;

        CancellationResponseType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Cancellation response status enumeration for pacs.018 messages.
     */
    public enum CancellationResponseStatus {
        /** Cancellation response accepted */
        ACCEPTED("ACPT"),
        /** Cancellation response rejected */
        REJECTED("REJT"),
        /** Cancellation response pending */
        PENDING("PEND"),
        /** Cancellation response completed */
        COMPLETED("COMP"),
        /** Cancellation response failed */
        FAILED("FAIL"),
        /** Cancellation response cancelled */
        CANCELLED("CANC");

        private final String code;

        CancellationResponseStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Cancellation response details for pacs.018 messages.
     */
    public static class CancellationResponse {
        private final String responseId;
        private final String originalCancellationRequestId;
        private final CancellationResponseType responseType;
        private final String reason;
        private final LocalDate effectiveDate;
        private final String respondingPartyId;
        private final String requestingPartyId;
        private final String debtorId;
        private final String creditorId;
        private final String mandateId;
        private final String conditions;
        private final boolean requiresDebtorConfirmation;
        private final boolean requiresCreditorConfirmation;
        private final boolean immediateCancellation;
        private final String documentationReference;
        private final String additionalInformation;

        public CancellationResponse(String responseId, String originalCancellationRequestId, CancellationResponseType responseType,
                                  String reason, LocalDate effectiveDate, String respondingPartyId, String requestingPartyId,
                                  String debtorId, String creditorId, String mandateId, String conditions,
                                  boolean requiresDebtorConfirmation, boolean requiresCreditorConfirmation,
                                  boolean immediateCancellation, String documentationReference, String additionalInformation) {
            this.responseId = responseId;
            this.originalCancellationRequestId = originalCancellationRequestId;
            this.responseType = responseType;
            this.reason = reason;
            this.effectiveDate = effectiveDate;
            this.respondingPartyId = respondingPartyId;
            this.requestingPartyId = requestingPartyId;
            this.debtorId = debtorId;
            this.creditorId = creditorId;
            this.mandateId = mandateId;
            this.conditions = conditions;
            this.requiresDebtorConfirmation = requiresDebtorConfirmation;
            this.requiresCreditorConfirmation = requiresCreditorConfirmation;
            this.immediateCancellation = immediateCancellation;
            this.documentationReference = documentationReference;
            this.additionalInformation = additionalInformation;
        }

        public String getResponseId() {
            return responseId;
        }

        public String getOriginalCancellationRequestId() {
            return originalCancellationRequestId;
        }

        public CancellationResponseType getResponseType() {
            return responseType;
        }

        public String getReason() {
            return reason;
        }

        public LocalDate getEffectiveDate() {
            return effectiveDate;
        }

        public String getRespondingPartyId() {
            return respondingPartyId;
        }

        public String getRequestingPartyId() {
            return requestingPartyId;
        }

        public String getDebtorId() {
            return debtorId;
        }

        public String getCreditorId() {
            return creditorId;
        }

        public String getMandateId() {
            return mandateId;
        }

        public String getConditions() {
            return conditions;
        }

        public boolean isRequiresDebtorConfirmation() {
            return requiresDebtorConfirmation;
        }

        public boolean isRequiresCreditorConfirmation() {
            return requiresCreditorConfirmation;
        }

        public boolean isImmediateCancellation() {
            return immediateCancellation;
        }

        public String getDocumentationReference() {
            return documentationReference;
        }

        public String getAdditionalInformation() {
            return additionalInformation;
        }

        @Override
        public String toString() {
            return String.format("CancellationResponse{responseId='%s', originalCancellationRequestId='%s', responseType=%s, " +
                               "reason='%s', effectiveDate=%s}",
                               responseId, originalCancellationRequestId, responseType, reason, effectiveDate);
        }
    }

    @NotNull
    @Size(min = 1, max = 35)
    private String messageId;

    @NotNull
    @Size(min = 1, max = 35)
    private String cancellationResponseId;

    @NotNull
    private CancellationResponseType cancellationResponseType;

    @NotNull
    private CancellationResponseStatus cancellationResponseStatus;

    @NotNull
    private LocalDateTime creationDateTime;

    private LocalDateTime responseDateTime;

    @Size(max = 35)
    private String respondingPartyId;

    @Size(max = 35)
    private String requestingPartyId;

    @Size(max = 35)
    private String debtorId;

    @Size(max = 35)
    private String creditorId;

    @Size(max = 35)
    private String originalCancellationRequestId;

    @Size(max = 500)
    private String responseReason;

    @Size(max = 500)
    private String additionalInformation;

    private final List<CancellationResponse> cancellationResponses = new java.util.ArrayList<>();

    /**
     * Default constructor initializes required fields.
     */
    public Pacs018Message() {
        this.messageId = "PACS-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.cancellationResponseType = CancellationResponseType.APPROVED;
        this.cancellationResponseStatus = CancellationResponseStatus.ACCEPTED;
        log.debug("Created new Pacs018Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param cancellationResponseId the cancellation response identifier
     * @param cancellationResponseType the type of cancellation response
     * @param originalCancellationRequestId the original cancellation request identifier
     * @param debtorId the debtor identifier
     * @param creditorId the creditor identifier
     */
    public Pacs018Message(String cancellationResponseId, CancellationResponseType cancellationResponseType,
                         String originalCancellationRequestId, String debtorId, String creditorId) {
        this();
        this.cancellationResponseId = cancellationResponseId;
        this.cancellationResponseType = cancellationResponseType;
        this.originalCancellationRequestId = originalCancellationRequestId;
        this.debtorId = debtorId;
        this.creditorId = creditorId;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getMessageType() {
        return "pacs.018";
    }

    @Override
    public MessagePriority getPriority() {
        return MessagePriority.HIGH; // Cancellation responses are high priority
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
        if (cancellationResponseId == null || cancellationResponseId.trim().isEmpty()) {
            throw new MessageValidationException("Cancellation response ID is required", messageId, getMessageType());
        }
        if (cancellationResponseType == null) {
            throw new MessageValidationException("Cancellation response type is required", messageId, getMessageType());
        }
        if (cancellationResponseStatus == null) {
            throw new MessageValidationException("Cancellation response status is required", messageId, getMessageType());
        }
        if (creationDateTime == null) {
            throw new MessageValidationException("Creation date time is required", messageId, getMessageType());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("Direct Debit Mandate Cancellation Response %s with type %s and status %s",
                           cancellationResponseId, cancellationResponseType, cancellationResponseStatus);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return true; // Cancellation responses require acknowledgment
    }

    @Override
    public String getSchemaVersion() {
        return "001.001.11";
    }

    @Override
    public List<String> getTransactions() {
        return cancellationResponses.stream()
                .map(CancellationResponse::getResponseId)
                .toList();
    }

    @Override
    public int getTransactionCount() {
        return cancellationResponses.size();
    }

    @Override
    public double getTotalAmount() {
        return 0.0; // Cancellation responses don't have amounts
    }

    /**
     * Sets the unique identifier for this cancellation response message.
     *
     * @param messageId the message identifier (1-35 characters)
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the cancellation response identifier.
     *
     * @return the cancellation response ID
     */
    public String getCancellationResponseId() {
        return cancellationResponseId;
    }

    /**
     * Sets the cancellation response identifier.
     *
     * @param cancellationResponseId the cancellation response ID (1-35 characters)
     */
    public void setCancellationResponseId(String cancellationResponseId) {
        this.cancellationResponseId = cancellationResponseId;
    }

    /**
     * Gets the cancellation response type.
     *
     * @return the cancellation response type
     */
    public CancellationResponseType getCancellationResponseType() {
        return cancellationResponseType;
    }

    /**
     * Sets the cancellation response type.
     *
     * @param cancellationResponseType the cancellation response type
     */
    public void setCancellationResponseType(CancellationResponseType cancellationResponseType) {
        this.cancellationResponseType = cancellationResponseType;
    }

    /**
     * Gets the cancellation response status.
     *
     * @return the cancellation response status
     */
    public CancellationResponseStatus getCancellationResponseStatus() {
        return cancellationResponseStatus;
    }

    /**
     * Sets the cancellation response status.
     *
     * @param cancellationResponseStatus the cancellation response status
     */
    public void setCancellationResponseStatus(CancellationResponseStatus cancellationResponseStatus) {
        this.cancellationResponseStatus = cancellationResponseStatus;
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
     * Gets the original cancellation request identifier.
     *
     * @return the original cancellation request ID
     */
    public String getOriginalCancellationRequestId() {
        return originalCancellationRequestId;
    }

    /**
     * Sets the original cancellation request identifier.
     *
     * @param originalCancellationRequestId the original cancellation request ID (max 35 characters)
     */
    public void setOriginalCancellationRequestId(String originalCancellationRequestId) {
        this.originalCancellationRequestId = originalCancellationRequestId;
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
     * Gets the additional information.
     *
     * @return the additional information
     */
    public String getAdditionalInformation() {
        return additionalInformation;
    }

    /**
     * Sets the additional information.
     *
     * @param additionalInformation the additional information (max 500 characters)
     */
    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    /**
     * Gets the list of cancellation responses.
     *
     * @return immutable list of cancellation responses
     */
    public List<CancellationResponse> getCancellationResponses() {
        return List.copyOf(cancellationResponses);
    }

    /**
     * Adds a cancellation response to the message.
     *
     * @param cancellationResponse the cancellation response to add
     */
    public void addCancellationResponse(CancellationResponse cancellationResponse) {
        cancellationResponses.add(cancellationResponse);
        log.debug("Added cancellation response: {}", cancellationResponse.getResponseId());
    }

    /**
     * Gets cancellation response by response ID.
     *
     * @param responseId the response ID
     * @return the cancellation response, or null if not found
     */
    public CancellationResponse getCancellationResponse(String responseId) {
        return cancellationResponses.stream()
                .filter(resp -> Objects.equals(resp.getResponseId(), responseId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if this is an approved cancellation response.
     *
     * @return true if the cancellation response type is APPROVED
     */
    public boolean isApproved() {
        return CancellationResponseType.APPROVED.equals(cancellationResponseType);
    }

    /**
     * Checks if this is a rejected cancellation response.
     *
     * @return true if the cancellation response type is REJECTED
     */
    public boolean isRejected() {
        return CancellationResponseType.REJECTED.equals(cancellationResponseType);
    }

    /**
     * Checks if this is a modified cancellation response.
     *
     * @return true if the cancellation response type is MODIFIED
     */
    public boolean isModified() {
        return CancellationResponseType.MODIFIED.equals(cancellationResponseType);
    }

    /**
     * Checks if this is a pending information cancellation response.
     *
     * @return true if the cancellation response type is PENDING_INFO
     */
    public boolean isPendingInfo() {
        return CancellationResponseType.PENDING_INFO.equals(cancellationResponseType);
    }

    /**
     * Checks if this is an under review cancellation response.
     *
     * @return true if the cancellation response type is UNDER_REVIEW
     */
    public boolean isUnderReview() {
        return CancellationResponseType.UNDER_REVIEW.equals(cancellationResponseType);
    }

    /**
     * Checks if this is a cancelled cancellation response.
     *
     * @return true if the cancellation response type is CANCELLED
     */
    public boolean isCancelled() {
        return CancellationResponseType.CANCELLED.equals(cancellationResponseType);
    }

    /**
     * Checks if the cancellation response is accepted.
     *
     * @return true if the cancellation response status is ACCEPTED
     */
    public boolean isAccepted() {
        return CancellationResponseStatus.ACCEPTED.equals(cancellationResponseStatus);
    }

    /**
     * Checks if the cancellation response is rejected.
     *
     * @return true if the cancellation response status is REJECTED
     */
    public boolean isResponseRejected() {
        return CancellationResponseStatus.REJECTED.equals(cancellationResponseStatus);
    }

    /**
     * Checks if the cancellation response is pending.
     *
     * @return true if the cancellation response status is PENDING
     */
    public boolean isResponsePending() {
        return CancellationResponseStatus.PENDING.equals(cancellationResponseStatus);
    }

    /**
     * Checks if the cancellation response is completed.
     *
     * @return true if the cancellation response status is COMPLETED
     */
    public boolean isResponseCompleted() {
        return CancellationResponseStatus.COMPLETED.equals(cancellationResponseStatus);
    }

    /**
     * Checks if the cancellation response is failed.
     *
     * @return true if the cancellation response status is FAILED
     */
    public boolean isResponseFailed() {
        return CancellationResponseStatus.FAILED.equals(cancellationResponseStatus);
    }

    /**
     * Checks if the cancellation response is cancelled.
     *
     * @return true if the cancellation response status is CANCELLED
     */
    public boolean isResponseCancelled() {
        return CancellationResponseStatus.CANCELLED.equals(cancellationResponseStatus);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pacs018Message that = (Pacs018Message) obj;
        return Objects.equals(messageId, that.messageId) &&
               Objects.equals(cancellationResponseId, that.cancellationResponseId) &&
               cancellationResponseType == that.cancellationResponseType &&
               Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, cancellationResponseId, cancellationResponseType, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Pacs018Message{messageId='%s', cancellationResponseId='%s', cancellationResponseType=%s, " +
                           "cancellationResponseStatus=%s, originalCancellationRequestId='%s', debtorId='%s', creditorId='%s', creationDateTime=%s}",
                           messageId, cancellationResponseId, cancellationResponseType, cancellationResponseStatus,
                           originalCancellationRequestId, debtorId, creditorId, creationDateTime);
    }
}
