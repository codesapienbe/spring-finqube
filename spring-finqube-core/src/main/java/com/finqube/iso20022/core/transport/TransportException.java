package com.finqube.iso20022.core.transport;

/**
 * Exception thrown when a transport operation fails.
 *
 * <p>This exception is thrown when there are problems with message transmission,
 * connection issues, authentication failures, or other transport-related errors.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class TransportException extends RuntimeException {

    private final String transportId;
    private final String messageId;
    private final TransportStatus status;

    /**
     * Constructs a new TransportException with the specified detail message.
     *
     * @param message the detail message
     */
    public TransportException(String message) {
        super(message);
        this.transportId = null;
        this.messageId = null;
        this.status = TransportStatus.FAILED;
    }

    /**
     * Constructs a new TransportException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public TransportException(String message, Throwable cause) {
        super(message, cause);
        this.transportId = null;
        this.messageId = null;
        this.status = TransportStatus.FAILED;
    }

    /**
     * Constructs a new TransportException with transport details.
     *
     * @param message the detail message
     * @param transportId the transport identifier
     * @param messageId the message identifier
     * @param status the transport status
     */
    public TransportException(String message, String transportId, String messageId, TransportStatus status) {
        super(message);
        this.transportId = transportId;
        this.messageId = messageId;
        this.status = status;
    }

    /**
     * Constructs a new TransportException with transport details and cause.
     *
     * @param message the detail message
     * @param transportId the transport identifier
     * @param messageId the message identifier
     * @param status the transport status
     * @param cause the cause
     */
    public TransportException(String message, String transportId, String messageId, TransportStatus status, Throwable cause) {
        super(message, cause);
        this.transportId = transportId;
        this.messageId = messageId;
        this.status = status;
    }

    /**
     * Gets the transport identifier.
     *
     * @return the transport identifier, or null if not available
     */
    public String getTransportId() {
        return transportId;
    }

    /**
     * Gets the message identifier.
     *
     * @return the message identifier, or null if not available
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

    @Override
    public String toString() {
        return "TransportException{" +
                "message='" + getMessage() + '\'' +
                ", transportId='" + transportId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", status=" + status +
                '}';
    }
}
