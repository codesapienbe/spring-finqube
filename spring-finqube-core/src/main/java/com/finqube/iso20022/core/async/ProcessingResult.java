package com.finqube.iso20022.core.async;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Result of an asynchronous processing operation.
 *
 * <p>This class encapsulates the result of processing an ISO 20022 message
 * asynchronously, including status, timing information, and any error details.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class ProcessingResult {

    private final String messageId;
    private final ProcessingStatus status;
    private final String errorMessage;
    private final Instant submittedAt;
    private final Instant completedAt;
    private final long processingTimeMillis;
    private final Map<String, Object> metadata;

    /**
     * Constructs a new ProcessingResult.
     *
     * @param messageId the message identifier
     * @param status the processing status
     * @param errorMessage the error message, if any
     * @param submittedAt when the message was submitted
     * @param completedAt when processing was completed
     * @param metadata additional metadata about the processing
     */
    public ProcessingResult(String messageId, ProcessingStatus status, String errorMessage,
                          Instant submittedAt, Instant completedAt, Map<String, Object> metadata) {
        this.messageId = Objects.requireNonNull(messageId, "Message ID cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.errorMessage = errorMessage;
        this.submittedAt = Objects.requireNonNull(submittedAt, "Submitted timestamp cannot be null");
        this.completedAt = completedAt;
        this.processingTimeMillis = completedAt != null ? completedAt.toEpochMilli() - submittedAt.toEpochMilli() : 0;
        this.metadata = metadata;
    }

    /**
     * Creates a successful processing result.
     *
     * @param messageId the message identifier
     * @param submittedAt when the message was submitted
     * @param completedAt when processing was completed
     * @return a successful processing result
     */
    public static ProcessingResult success(String messageId, Instant submittedAt, Instant completedAt) {
        return new ProcessingResult(messageId, ProcessingStatus.SUCCESS, null, submittedAt, completedAt, Map.of());
    }

    /**
     * Creates a successful processing result with metadata.
     *
     * @param messageId the message identifier
     * @param submittedAt when the message was submitted
     * @param completedAt when processing was completed
     * @param metadata additional metadata
     * @return a successful processing result
     */
    public static ProcessingResult success(String messageId, Instant submittedAt, Instant completedAt, Map<String, Object> metadata) {
        return new ProcessingResult(messageId, ProcessingStatus.SUCCESS, null, submittedAt, completedAt, metadata);
    }

    /**
     * Creates a failed processing result.
     *
     * @param messageId the message identifier
     * @param errorMessage the error message
     * @param submittedAt when the message was submitted
     * @param completedAt when processing was completed
     * @return a failed processing result
     */
    public static ProcessingResult failure(String messageId, String errorMessage, Instant submittedAt, Instant completedAt) {
        return new ProcessingResult(messageId, ProcessingStatus.FAILED, errorMessage, submittedAt, completedAt, Map.of());
    }

    /**
     * Creates a timeout processing result.
     *
     * @param messageId the message identifier
     * @param submittedAt when the message was submitted
     * @param completedAt when processing was completed
     * @return a timeout processing result
     */
    public static ProcessingResult timeout(String messageId, Instant submittedAt, Instant completedAt) {
        return new ProcessingResult(messageId, ProcessingStatus.TIMEOUT, "Processing timed out", submittedAt, completedAt, Map.of());
    }

    /**
     * Gets the message identifier.
     *
     * @return the message identifier
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Gets the processing status.
     *
     * @return the processing status
     */
    public ProcessingStatus getStatus() {
        return status;
    }

    /**
     * Gets the error message.
     *
     * @return the error message, or null if no error
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets when the message was submitted.
     *
     * @return the submitted timestamp
     */
    public Instant getSubmittedAt() {
        return submittedAt;
    }

    /**
     * Gets when processing was completed.
     *
     * @return the completed timestamp, or null if not completed
     */
    public Instant getCompletedAt() {
        return completedAt;
    }

    /**
     * Gets the processing time in milliseconds.
     *
     * @return the processing time in milliseconds
     */
    public long getProcessingTimeMillis() {
        return processingTimeMillis;
    }

    /**
     * Gets additional metadata about the processing.
     *
     * @return the metadata map
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Checks if the processing was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return status == ProcessingStatus.SUCCESS;
    }

    /**
     * Checks if the processing failed.
     *
     * @return true if failed, false otherwise
     */
    public boolean isFailed() {
        return status == ProcessingStatus.FAILED;
    }

    /**
     * Checks if the processing timed out.
     *
     * @return true if timed out, false otherwise
     */
    public boolean isTimeout() {
        return status == ProcessingStatus.TIMEOUT;
    }

    /**
     * Checks if the processing is still in progress.
     *
     * @return true if in progress, false otherwise
     */
    public boolean isInProgress() {
        return status == ProcessingStatus.IN_PROGRESS;
    }

    @Override
    public String toString() {
        return "ProcessingResult{" +
                "messageId='" + messageId + '\'' +
                ", status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                ", submittedAt=" + submittedAt +
                ", completedAt=" + completedAt +
                ", processingTimeMillis=" + processingTimeMillis +
                ", metadata=" + metadata +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessingResult that = (ProcessingResult) o;
        return processingTimeMillis == that.processingTimeMillis &&
                Objects.equals(messageId, that.messageId) &&
                status == that.status &&
                Objects.equals(errorMessage, that.errorMessage) &&
                Objects.equals(submittedAt, that.submittedAt) &&
                Objects.equals(completedAt, that.completedAt) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, status, errorMessage, submittedAt, completedAt, processingTimeMillis, metadata);
    }

    /**
     * Processing status enumeration.
     */
    public enum ProcessingStatus {
        IN_PROGRESS("In Progress", "Message is being processed"),
        SUCCESS("Success", "Message was processed successfully"),
        FAILED("Failed", "Message processing failed"),
        TIMEOUT("Timeout", "Message processing timed out"),
        CANCELLED("Cancelled", "Message processing was cancelled");

        private final String displayName;
        private final String description;

        ProcessingStatus(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
