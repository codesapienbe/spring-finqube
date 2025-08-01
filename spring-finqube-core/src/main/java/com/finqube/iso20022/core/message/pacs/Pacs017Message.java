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
 * ISO 20022 pacs.017 Direct Debit Mandate Amendment Response message implementation.
 *
 * <p>This message is used to respond to mandate amendment requests. It allows banks
 * and financial institutions to provide approval, rejection, or modification responses
 * to amendment requests with detailed reasoning and conditions.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Mandate amendment responses with approval/rejection status</li>
 *   <li>Support for multiple response types and statuses</li>
 *   <li>Comprehensive validation and business logic</li>
 *   <li>Response tracking and amendment implementation</li>
 * </ul>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class Pacs017Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Pacs017Message.class);

    /**
     * Amendment response type enumeration for pacs.017 messages.
     */
    public enum AmendmentResponseType {
        /** Amendment request approved */
        APPROVED("APPR"),
        /** Amendment request rejected */
        REJECTED("REJT"),
        /** Amendment request modified */
        MODIFIED("MODF"),
        /** Amendment request pending additional information */
        PENDING_INFO("PEND"),
        /** Amendment request under review */
        UNDER_REVIEW("REVW"),
        /** Amendment request cancelled */
        CANCELLED("CANC");

        private final String code;

        AmendmentResponseType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Amendment response status enumeration for pacs.017 messages.
     */
    public enum AmendmentResponseStatus {
        /** Amendment response accepted */
        ACCEPTED("ACPT"),
        /** Amendment response rejected */
        REJECTED("REJT"),
        /** Amendment response pending */
        PENDING("PEND"),
        /** Amendment response completed */
        COMPLETED("COMP"),
        /** Amendment response failed */
        FAILED("FAIL"),
        /** Amendment response cancelled */
        CANCELLED("CANC");

        private final String code;

        AmendmentResponseStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Amendment response details for pacs.017 messages.
     */
    public static class AmendmentResponse {
        private final String responseId;
        private final String originalAmendmentRequestId;
        private final AmendmentResponseType responseType;
        private final String reason;
        private final LocalDate effectiveDate;
        private final BigDecimal approvedAmount;
        private final String approvedCurrency;
        private final String approvedFrequency;
        private final LocalDate approvedStartDate;
        private final LocalDate approvedEndDate;
        private final String approvedDebtorId;
        private final String approvedCreditorId;
        private final String approvedDebtorAccountId;
        private final String approvedCreditorAccountId;
        private final String approvedDescription;
        private final String conditions;
        private final boolean requiresDebtorConfirmation;
        private final boolean requiresCreditorConfirmation;

        public AmendmentResponse(String responseId, String originalAmendmentRequestId, AmendmentResponseType responseType,
                               String reason, LocalDate effectiveDate, BigDecimal approvedAmount, String approvedCurrency,
                               String approvedFrequency, LocalDate approvedStartDate, LocalDate approvedEndDate,
                               String approvedDebtorId, String approvedCreditorId, String approvedDebtorAccountId,
                               String approvedCreditorAccountId, String approvedDescription, String conditions,
                               boolean requiresDebtorConfirmation, boolean requiresCreditorConfirmation) {
            this.responseId = responseId;
            this.originalAmendmentRequestId = originalAmendmentRequestId;
            this.responseType = responseType;
            this.reason = reason;
            this.effectiveDate = effectiveDate;
            this.approvedAmount = approvedAmount;
            this.approvedCurrency = approvedCurrency;
            this.approvedFrequency = approvedFrequency;
            this.approvedStartDate = approvedStartDate;
            this.approvedEndDate = approvedEndDate;
            this.approvedDebtorId = approvedDebtorId;
            this.approvedCreditorId = approvedCreditorId;
            this.approvedDebtorAccountId = approvedDebtorAccountId;
            this.approvedCreditorAccountId = approvedCreditorAccountId;
            this.approvedDescription = approvedDescription;
            this.conditions = conditions;
            this.requiresDebtorConfirmation = requiresDebtorConfirmation;
            this.requiresCreditorConfirmation = requiresCreditorConfirmation;
        }

        public String getResponseId() {
            return responseId;
        }

        public String getOriginalAmendmentRequestId() {
            return originalAmendmentRequestId;
        }

        public AmendmentResponseType getResponseType() {
            return responseType;
        }

        public String getReason() {
            return reason;
        }

        public LocalDate getEffectiveDate() {
            return effectiveDate;
        }

        public BigDecimal getApprovedAmount() {
            return approvedAmount;
        }

        public String getApprovedCurrency() {
            return approvedCurrency;
        }

        public String getApprovedFrequency() {
            return approvedFrequency;
        }

        public LocalDate getApprovedStartDate() {
            return approvedStartDate;
        }

        public LocalDate getApprovedEndDate() {
            return approvedEndDate;
        }

        public String getApprovedDebtorId() {
            return approvedDebtorId;
        }

        public String getApprovedCreditorId() {
            return approvedCreditorId;
        }

        public String getApprovedDebtorAccountId() {
            return approvedDebtorAccountId;
        }

        public String getApprovedCreditorAccountId() {
            return approvedCreditorAccountId;
        }

        public String getApprovedDescription() {
            return approvedDescription;
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

        @Override
        public String toString() {
            return String.format("AmendmentResponse{responseId='%s', originalAmendmentRequestId='%s', responseType=%s, " +
                               "reason='%s', effectiveDate=%s}",
                               responseId, originalAmendmentRequestId, responseType, reason, effectiveDate);
        }
    }

    @NotNull
    @Size(min = 1, max = 35)
    private String messageId;

    @NotNull
    @Size(min = 1, max = 35)
    private String amendmentResponseId;

    @NotNull
    private AmendmentResponseType amendmentResponseType;

    @NotNull
    private AmendmentResponseStatus amendmentResponseStatus;

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
    private String originalAmendmentRequestId;

    @Size(max = 500)
    private String responseReason;

    @Size(max = 500)
    private String additionalInformation;

    private final List<AmendmentResponse> amendmentResponses = new java.util.ArrayList<>();

    private BigDecimal totalApprovedAmount = BigDecimal.ZERO;

    /**
     * Default constructor initializes required fields.
     */
    public Pacs017Message() {
        this.messageId = "PACS-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.amendmentResponseType = AmendmentResponseType.APPROVED;
        this.amendmentResponseStatus = AmendmentResponseStatus.ACCEPTED;
        log.debug("Created new Pacs017Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param amendmentResponseId the amendment response identifier
     * @param amendmentResponseType the type of amendment response
     * @param originalAmendmentRequestId the original amendment request identifier
     * @param debtorId the debtor identifier
     * @param creditorId the creditor identifier
     */
    public Pacs017Message(String amendmentResponseId, AmendmentResponseType amendmentResponseType,
                         String originalAmendmentRequestId, String debtorId, String creditorId) {
        this();
        this.amendmentResponseId = amendmentResponseId;
        this.amendmentResponseType = amendmentResponseType;
        this.originalAmendmentRequestId = originalAmendmentRequestId;
        this.debtorId = debtorId;
        this.creditorId = creditorId;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getMessageType() {
        return "pacs.017";
    }

    @Override
    public MessagePriority getPriority() {
        return MessagePriority.HIGH; // Amendment responses are high priority
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
        if (amendmentResponseId == null || amendmentResponseId.trim().isEmpty()) {
            throw new MessageValidationException("Amendment response ID is required", messageId, getMessageType());
        }
        if (amendmentResponseType == null) {
            throw new MessageValidationException("Amendment response type is required", messageId, getMessageType());
        }
        if (amendmentResponseStatus == null) {
            throw new MessageValidationException("Amendment response status is required", messageId, getMessageType());
        }
        if (creationDateTime == null) {
            throw new MessageValidationException("Creation date time is required", messageId, getMessageType());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("Direct Debit Mandate Amendment Response %s with type %s and status %s",
                           amendmentResponseId, amendmentResponseType, amendmentResponseStatus);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return true; // Amendment responses require acknowledgment
    }

    @Override
    public String getSchemaVersion() {
        return "001.001.11";
    }

    @Override
    public List<String> getTransactions() {
        return amendmentResponses.stream()
                .map(AmendmentResponse::getResponseId)
                .toList();
    }

    @Override
    public int getTransactionCount() {
        return amendmentResponses.size();
    }

    @Override
    public double getTotalAmount() {
        return totalApprovedAmount.doubleValue();
    }

    /**
     * Sets the unique identifier for this amendment response message.
     *
     * @param messageId the message identifier (1-35 characters)
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the amendment response identifier.
     *
     * @return the amendment response ID
     */
    public String getAmendmentResponseId() {
        return amendmentResponseId;
    }

    /**
     * Sets the amendment response identifier.
     *
     * @param amendmentResponseId the amendment response ID (1-35 characters)
     */
    public void setAmendmentResponseId(String amendmentResponseId) {
        this.amendmentResponseId = amendmentResponseId;
    }

    /**
     * Gets the amendment response type.
     *
     * @return the amendment response type
     */
    public AmendmentResponseType getAmendmentResponseType() {
        return amendmentResponseType;
    }

    /**
     * Sets the amendment response type.
     *
     * @param amendmentResponseType the amendment response type
     */
    public void setAmendmentResponseType(AmendmentResponseType amendmentResponseType) {
        this.amendmentResponseType = amendmentResponseType;
    }

    /**
     * Gets the amendment response status.
     *
     * @return the amendment response status
     */
    public AmendmentResponseStatus getAmendmentResponseStatus() {
        return amendmentResponseStatus;
    }

    /**
     * Sets the amendment response status.
     *
     * @param amendmentResponseStatus the amendment response status
     */
    public void setAmendmentResponseStatus(AmendmentResponseStatus amendmentResponseStatus) {
        this.amendmentResponseStatus = amendmentResponseStatus;
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
     * Gets the original amendment request identifier.
     *
     * @return the original amendment request ID
     */
    public String getOriginalAmendmentRequestId() {
        return originalAmendmentRequestId;
    }

    /**
     * Sets the original amendment request identifier.
     *
     * @param originalAmendmentRequestId the original amendment request ID (max 35 characters)
     */
    public void setOriginalAmendmentRequestId(String originalAmendmentRequestId) {
        this.originalAmendmentRequestId = originalAmendmentRequestId;
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
     * Gets the list of amendment responses.
     *
     * @return immutable list of amendment responses
     */
    public List<AmendmentResponse> getAmendmentResponses() {
        return List.copyOf(amendmentResponses);
    }

    /**
     * Adds an amendment response to the message.
     *
     * @param amendmentResponse the amendment response to add
     */
    public void addAmendmentResponse(AmendmentResponse amendmentResponse) {
        amendmentResponses.add(amendmentResponse);
        if (amendmentResponse.getApprovedAmount() != null) {
            totalApprovedAmount = totalApprovedAmount.add(amendmentResponse.getApprovedAmount());
        }
        log.debug("Added amendment response: {}", amendmentResponse.getResponseId());
    }

    /**
     * Gets amendment response by response ID.
     *
     * @param responseId the response ID
     * @return the amendment response, or null if not found
     */
    public AmendmentResponse getAmendmentResponse(String responseId) {
        return amendmentResponses.stream()
                .filter(resp -> Objects.equals(resp.getResponseId(), responseId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the total approved amount.
     *
     * @return the total approved amount
     */
    public BigDecimal getTotalApprovedAmount() {
        return totalApprovedAmount;
    }

    /**
     * Checks if this is an approved amendment response.
     *
     * @return true if the amendment response type is APPROVED
     */
    public boolean isApproved() {
        return AmendmentResponseType.APPROVED.equals(amendmentResponseType);
    }

    /**
     * Checks if this is a rejected amendment response.
     *
     * @return true if the amendment response type is REJECTED
     */
    public boolean isRejected() {
        return AmendmentResponseType.REJECTED.equals(amendmentResponseType);
    }

    /**
     * Checks if this is a modified amendment response.
     *
     * @return true if the amendment response type is MODIFIED
     */
    public boolean isModified() {
        return AmendmentResponseType.MODIFIED.equals(amendmentResponseType);
    }

    /**
     * Checks if this is a pending information amendment response.
     *
     * @return true if the amendment response type is PENDING_INFO
     */
    public boolean isPendingInfo() {
        return AmendmentResponseType.PENDING_INFO.equals(amendmentResponseType);
    }

    /**
     * Checks if this is an under review amendment response.
     *
     * @return true if the amendment response type is UNDER_REVIEW
     */
    public boolean isUnderReview() {
        return AmendmentResponseType.UNDER_REVIEW.equals(amendmentResponseType);
    }

    /**
     * Checks if this is a cancelled amendment response.
     *
     * @return true if the amendment response type is CANCELLED
     */
    public boolean isCancelled() {
        return AmendmentResponseType.CANCELLED.equals(amendmentResponseType);
    }

    /**
     * Checks if the amendment response is accepted.
     *
     * @return true if the amendment response status is ACCEPTED
     */
    public boolean isAccepted() {
        return AmendmentResponseStatus.ACCEPTED.equals(amendmentResponseStatus);
    }

    /**
     * Checks if the amendment response is rejected.
     *
     * @return true if the amendment response status is REJECTED
     */
    public boolean isResponseRejected() {
        return AmendmentResponseStatus.REJECTED.equals(amendmentResponseStatus);
    }

    /**
     * Checks if the amendment response is pending.
     *
     * @return true if the amendment response status is PENDING
     */
    public boolean isResponsePending() {
        return AmendmentResponseStatus.PENDING.equals(amendmentResponseStatus);
    }

    /**
     * Checks if the amendment response is completed.
     *
     * @return true if the amendment response status is COMPLETED
     */
    public boolean isResponseCompleted() {
        return AmendmentResponseStatus.COMPLETED.equals(amendmentResponseStatus);
    }

    /**
     * Checks if the amendment response is failed.
     *
     * @return true if the amendment response status is FAILED
     */
    public boolean isResponseFailed() {
        return AmendmentResponseStatus.FAILED.equals(amendmentResponseStatus);
    }

    /**
     * Checks if the amendment response is cancelled.
     *
     * @return true if the amendment response status is CANCELLED
     */
    public boolean isResponseCancelled() {
        return AmendmentResponseStatus.CANCELLED.equals(amendmentResponseStatus);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pacs017Message that = (Pacs017Message) obj;
        return Objects.equals(messageId, that.messageId) &&
               Objects.equals(amendmentResponseId, that.amendmentResponseId) &&
               amendmentResponseType == that.amendmentResponseType &&
               Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, amendmentResponseId, amendmentResponseType, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Pacs017Message{messageId='%s', amendmentResponseId='%s', amendmentResponseType=%s, " +
                           "amendmentResponseStatus=%s, originalAmendmentRequestId='%s', debtorId='%s', creditorId='%s', creationDateTime=%s}",
                           messageId, amendmentResponseId, amendmentResponseType, amendmentResponseStatus,
                           originalAmendmentRequestId, debtorId, creditorId, creationDateTime);
    }
}
