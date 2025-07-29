package com.finqube.iso20022.core.transport;

/**
 * Status of a transport operation.
 *
 * <p>This enum represents the possible outcomes of sending a message
 * through a transport layer.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public enum TransportStatus {

    /**
     * The message was successfully sent and acknowledged.
     */
    SUCCESS("Success", "Message sent successfully"),

    /**
     * The message was sent but no acknowledgment was received.
     */
    SENT("Sent", "Message sent but no acknowledgment received"),

    /**
     * The transport operation failed.
     */
    FAILED("Failed", "Transport operation failed"),

    /**
     * The transport operation timed out.
     */
    TIMEOUT("Timeout", "Transport operation timed out"),

    /**
     * The transport is not available.
     */
    UNAVAILABLE("Unavailable", "Transport is not available"),

    /**
     * The transport operation was rejected.
     */
    REJECTED("Rejected", "Transport operation was rejected"),

    /**
     * The transport operation is in progress.
     */
    IN_PROGRESS("In Progress", "Transport operation is in progress");

    private final String displayName;
    private final String description;

    /**
     * Constructs a new TransportStatus.
     *
     * @param displayName the human-readable display name
     * @param description the detailed description
     */
    TransportStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Gets the human-readable display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the detailed description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if this status represents a successful operation.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return this == SUCCESS || this == SENT;
    }

    /**
     * Checks if this status represents a failed operation.
     *
     * @return true if failed, false otherwise
     */
    public boolean isFailed() {
        return this == FAILED || this == TIMEOUT || this == REJECTED;
    }

    /**
     * Checks if this status represents a terminal state.
     *
     * @return true if terminal, false otherwise
     */
    public boolean isTerminal() {
        return this != IN_PROGRESS;
    }

    @Override
    public String toString() {
        return displayName;
    }
}