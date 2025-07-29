package com.finqube.iso20022.core.transport;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Response from a transport operation.
 *
 * <p>This class encapsulates the result of sending a message through a transport,
 * including status, timing information, and any response data.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class TransportResponse {

    private final String messageId;
    private final TransportStatus status;
    private final String responseMessage;
    private final Instant sentAt;
    private final Instant receivedAt;
    private final long durationMillis;
    private final Map<String, Object> metadata;

    /**
     * Constructs a new TransportResponse.
     *
     * @param messageId the unique message identifier
     * @param status the transport status
     * @param responseMessage the response message or error details
     * @param sentAt when the message was sent
     * @param receivedAt when the response was received
     * @param metadata additional metadata about the transport operation
     */
    public TransportResponse(String messageId, TransportStatus status, String responseMessage,
                           Instant sentAt, Instant receivedAt, Map<String, Object> metadata) {
        this.messageId = Objects.requireNonNull(messageId, "Message ID cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.responseMessage = responseMessage;
        this.sentAt = Objects.requireNonNull(sentAt, "Sent timestamp cannot be null");
        this.receivedAt = receivedAt;
        this.durationMillis = receivedAt != null ? receivedAt.toEpochMilli() - sentAt.toEpochMilli() : 0;
        this.metadata = metadata;
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
     * Gets the transport status.
     *
     * @return the transport status
     */
    public TransportStatus getStatus() {
        return status;
    }

    /**
     * Gets the response message.
     *
     * @return the response message or error details
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * Gets when the message was sent.
     *
     * @return the sent timestamp
     */
    public Instant getSentAt() {
        return sentAt;
    }

    /**
     * Gets when the response was received.
     *
     * @return the received timestamp, or null if no response was received
     */
    public Instant getReceivedAt() {
        return receivedAt;
    }

    /**
     * Gets the duration of the transport operation in milliseconds.
     *
     * @return the duration in milliseconds
     */
    public long getDurationMillis() {
        return durationMillis;
    }

    /**
     * Gets additional metadata about the transport operation.
     *
     * @return the metadata map
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Checks if the transport operation was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return status == TransportStatus.SUCCESS;
    }

    /**
     * Checks if the transport operation failed.
     *
     * @return true if failed, false otherwise
     */
    public boolean isFailed() {
        return status == TransportStatus.FAILED;
    }

    /**
     * Checks if the transport operation timed out.
     *
     * @return true if timed out, false otherwise
     */
    public boolean isTimeout() {
        return status == TransportStatus.TIMEOUT;
    }

    @Override
    public String toString() {
        return "TransportResponse{" +
                "messageId='" + messageId + '\'' +
                ", status=" + status +
                ", responseMessage='" + responseMessage + '\'' +
                ", sentAt=" + sentAt +
                ", receivedAt=" + receivedAt +
                ", durationMillis=" + durationMillis +
                ", metadata=" + metadata +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportResponse that = (TransportResponse) o;
        return durationMillis == that.durationMillis &&
                Objects.equals(messageId, that.messageId) &&
                status == that.status &&
                Objects.equals(responseMessage, that.responseMessage) &&
                Objects.equals(sentAt, that.sentAt) &&
                Objects.equals(receivedAt, that.receivedAt) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, status, responseMessage, sentAt, receivedAt, durationMillis, metadata);
    }
}
