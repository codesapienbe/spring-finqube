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
 * ISO 20022 pacs.013 Direct Debit Mandate Information message implementation.
 *
 * <p>This message is used to request or provide information about direct debit mandates.
 * It supports both information requests and responses, allowing banks and financial
 * institutions to exchange mandate details.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Mandate information requests and responses</li>
 *   <li>Support for multiple mandate types and statuses</li>
 *   <li>Detailed mandate information including amounts, dates, and parties</li>
 *   <li>Comprehensive validation and business logic</li>
 * </ul>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class Pacs013Message implements BaseMessage {

    private static final Logger log = LoggerFactory.getLogger(Pacs013Message.class);

    /**
     * Information type enumeration for pacs.013 messages.
     */
    public enum InformationType {
        /** Request for mandate information */
        REQUEST("REQ"),
        /** Response with mandate information */
        RESPONSE("RES"),
        /** Notification of mandate changes */
        NOTIFICATION("NOT"),
        /** Confirmation of mandate information */
        CONFIRMATION("CON");

        private final String code;

        InformationType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Information status enumeration for pacs.013 messages.
     */
    public enum InformationStatus {
        /** Information request pending */
        PENDING("PEND"),
        /** Information provided successfully */
        PROVIDED("PROV"),
        /** Information not available */
        NOT_AVAILABLE("NAVA"),
        /** Information request rejected */
        REJECTED("REJT"),
        /** Information request cancelled */
        CANCELLED("CANC"),
        /** Information request completed */
        COMPLETED("COMP");

        private final String code;

        InformationStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Mandate information details for pacs.013 messages.
     */
    public static class MandateInformation {
        private final String mandateId;
        private final String mandateType;
        private final String mandateStatus;
        private final LocalDate mandateStartDate;
        private final LocalDate mandateEndDate;
        private final BigDecimal maxAmount;
        private final String currency;
        private final String frequency;
        private final String debtorId;
        private final String creditorId;
        private final String debtorAccountId;
        private final String creditorAccountId;
        private final String description;
        private final LocalDate lastCollectionDate;
        private final BigDecimal lastCollectionAmount;
        private final int totalCollections;
        private final BigDecimal totalAmountCollected;

        public MandateInformation(String mandateId, String mandateType, String mandateStatus,
                                LocalDate mandateStartDate, LocalDate mandateEndDate, BigDecimal maxAmount,
                                String currency, String frequency, String debtorId, String creditorId,
                                String debtorAccountId, String creditorAccountId, String description,
                                LocalDate lastCollectionDate, BigDecimal lastCollectionAmount,
                                int totalCollections, BigDecimal totalAmountCollected) {
            this.mandateId = mandateId;
            this.mandateType = mandateType;
            this.mandateStatus = mandateStatus;
            this.mandateStartDate = mandateStartDate;
            this.mandateEndDate = mandateEndDate;
            this.maxAmount = maxAmount;
            this.currency = currency;
            this.frequency = frequency;
            this.debtorId = debtorId;
            this.creditorId = creditorId;
            this.debtorAccountId = debtorAccountId;
            this.creditorAccountId = creditorAccountId;
            this.description = description;
            this.lastCollectionDate = lastCollectionDate;
            this.lastCollectionAmount = lastCollectionAmount;
            this.totalCollections = totalCollections;
            this.totalAmountCollected = totalAmountCollected;
        }

        public String getMandateId() {
            return mandateId;
        }

        public String getMandateType() {
            return mandateType;
        }

        public String getMandateStatus() {
            return mandateStatus;
        }

        public LocalDate getMandateStartDate() {
            return mandateStartDate;
        }

        public LocalDate getMandateEndDate() {
            return mandateEndDate;
        }

        public BigDecimal getMaxAmount() {
            return maxAmount;
        }

        public String getCurrency() {
            return currency;
        }

        public String getFrequency() {
            return frequency;
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

        public String getDescription() {
            return description;
        }

        public LocalDate getLastCollectionDate() {
            return lastCollectionDate;
        }

        public BigDecimal getLastCollectionAmount() {
            return lastCollectionAmount;
        }

        public int getTotalCollections() {
            return totalCollections;
        }

        public BigDecimal getTotalAmountCollected() {
            return totalAmountCollected;
        }

        @Override
        public String toString() {
            return String.format("MandateInformation{mandateId='%s', mandateType='%s', mandateStatus='%s', " +
                               "debtorId='%s', creditorId='%s', maxAmount=%s, currency='%s'}",
                               mandateId, mandateType, mandateStatus, debtorId, creditorId, maxAmount, currency);
        }
    }

    @NotNull
    @Size(min = 1, max = 35)
    private String messageId;

    @NotNull
    @Size(min = 1, max = 35)
    private String requestId;

    @NotNull
    private InformationType informationType;

    @NotNull
    private InformationStatus informationStatus;

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

    private final List<MandateInformation> mandateInformationList = new java.util.ArrayList<>();

    private BigDecimal totalAmountRequested = BigDecimal.ZERO;

    /**
     * Default constructor initializes required fields.
     */
    public Pacs013Message() {
        this.messageId = "PACS-" + UUID.randomUUID().toString();
        this.creationDateTime = LocalDateTime.now();
        this.informationType = InformationType.REQUEST;
        this.informationStatus = InformationStatus.PENDING;
        log.debug("Created new Pacs013Message with ID: {}", this.messageId);
    }

    /**
     * Constructor with required fields.
     *
     * @param requestId the request identifier
     * @param informationType the type of information request/response
     * @param debtorId the debtor identifier
     * @param creditorId the creditor identifier
     */
    public Pacs013Message(String requestId, InformationType informationType, String debtorId, String creditorId) {
        this();
        this.requestId = requestId;
        this.informationType = informationType;
        this.debtorId = debtorId;
        this.creditorId = creditorId;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getMessageType() {
        return "pacs.013";
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
        if (requestId == null || requestId.trim().isEmpty()) {
            throw new MessageValidationException("Request ID is required", messageId, getMessageType());
        }
        if (informationType == null) {
            throw new MessageValidationException("Information type is required", messageId, getMessageType());
        }
        if (informationStatus == null) {
            throw new MessageValidationException("Information status is required", messageId, getMessageType());
        }
        if (creationDateTime == null) {
            throw new MessageValidationException("Creation date time is required", messageId, getMessageType());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return String.format("Direct Debit Mandate Information %s with type %s and status %s",
                           requestId, informationType, informationStatus);
    }

    @Override
    public boolean requiresAcknowledgment() {
        return informationType == InformationType.REQUEST; // Requests require acknowledgment
    }

    @Override
    public String getSchemaVersion() {
        return "001.001.11";
    }

    @Override
    public List<String> getTransactions() {
        return mandateInformationList.stream()
                .map(MandateInformation::getMandateId)
                .toList();
    }

    @Override
    public int getTransactionCount() {
        return mandateInformationList.size();
    }

    @Override
    public double getTotalAmount() {
        return totalAmountRequested.doubleValue();
    }

    /**
     * Sets the unique identifier for this mandate information message.
     *
     * @param messageId the message identifier (1-35 characters)
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the request identifier.
     *
     * @return the request ID
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the request identifier.
     *
     * @param requestId the request ID (1-35 characters)
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets the information type.
     *
     * @return the information type
     */
    public InformationType getInformationType() {
        return informationType;
    }

    /**
     * Sets the information type.
     *
     * @param informationType the information type
     */
    public void setInformationType(InformationType informationType) {
        this.informationType = informationType;
    }

    /**
     * Gets the information status.
     *
     * @return the information status
     */
    public InformationStatus getInformationStatus() {
        return informationStatus;
    }

    /**
     * Sets the information status.
     *
     * @param informationStatus the information status
     */
    public void setInformationStatus(InformationStatus informationStatus) {
        this.informationStatus = informationStatus;
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
     * Gets the list of mandate information.
     *
     * @return immutable list of mandate information
     */
    public List<MandateInformation> getMandateInformationList() {
        return List.copyOf(mandateInformationList);
    }

    /**
     * Adds mandate information to the message.
     *
     * @param mandateInformation the mandate information to add
     */
    public void addMandateInformation(MandateInformation mandateInformation) {
        mandateInformationList.add(mandateInformation);
        if (mandateInformation.getMaxAmount() != null) {
            totalAmountRequested = totalAmountRequested.add(mandateInformation.getMaxAmount());
        }
        log.debug("Added mandate information for mandate: {}", mandateInformation.getMandateId());
    }

    /**
     * Gets mandate information by mandate ID.
     *
     * @param mandateId the mandate ID
     * @return the mandate information, or null if not found
     */
    public MandateInformation getMandateInformation(String mandateId) {
        return mandateInformationList.stream()
                .filter(info -> Objects.equals(info.getMandateId(), mandateId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the total amount requested.
     *
     * @return the total amount requested
     */
    public BigDecimal getTotalAmountRequested() {
        return totalAmountRequested;
    }

    /**
     * Checks if this is a request message.
     *
     * @return true if the information type is REQUEST
     */
    public boolean isRequest() {
        return InformationType.REQUEST.equals(informationType);
    }

    /**
     * Checks if this is a response message.
     *
     * @return true if the information type is RESPONSE
     */
    public boolean isResponse() {
        return InformationType.RESPONSE.equals(informationType);
    }

    /**
     * Checks if this is a notification message.
     *
     * @return true if the information type is NOTIFICATION
     */
    public boolean isNotification() {
        return InformationType.NOTIFICATION.equals(informationType);
    }

    /**
     * Checks if this is a confirmation message.
     *
     * @return true if the information type is CONFIRMATION
     */
    public boolean isConfirmation() {
        return InformationType.CONFIRMATION.equals(informationType);
    }

    /**
     * Checks if the information request is pending.
     *
     * @return true if the information status is PENDING
     */
    public boolean isPending() {
        return InformationStatus.PENDING.equals(informationStatus);
    }

    /**
     * Checks if the information has been provided.
     *
     * @return true if the information status is PROVIDED
     */
    public boolean isProvided() {
        return InformationStatus.PROVIDED.equals(informationStatus);
    }

    /**
     * Checks if the information is not available.
     *
     * @return true if the information status is NOT_AVAILABLE
     */
    public boolean isNotAvailable() {
        return InformationStatus.NOT_AVAILABLE.equals(informationStatus);
    }

    /**
     * Checks if the information request was rejected.
     *
     * @return true if the information status is REJECTED
     */
    public boolean isRejected() {
        return InformationStatus.REJECTED.equals(informationStatus);
    }

    /**
     * Checks if the information request was cancelled.
     *
     * @return true if the information status is CANCELLED
     */
    public boolean isCancelled() {
        return InformationStatus.CANCELLED.equals(informationStatus);
    }

    /**
     * Checks if the information request was completed.
     *
     * @return true if the information status is COMPLETED
     */
    public boolean isCompleted() {
        return InformationStatus.COMPLETED.equals(informationStatus);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pacs013Message that = (Pacs013Message) obj;
        return Objects.equals(messageId, that.messageId) &&
               Objects.equals(requestId, that.requestId) &&
               informationType == that.informationType &&
               Objects.equals(creationDateTime, that.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, requestId, informationType, creationDateTime);
    }

    @Override
    public String toString() {
        return String.format("Pacs013Message{messageId='%s', requestId='%s', informationType=%s, " +
                           "informationStatus=%s, debtorId='%s', creditorId='%s', creationDateTime=%s}",
                           messageId, requestId, informationType, informationStatus, debtorId, creditorId, creationDateTime);
    }
}
