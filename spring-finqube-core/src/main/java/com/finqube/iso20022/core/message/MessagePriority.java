package com.finqube.iso20022.core.message;

/**
 * Enumeration of message priority levels for ISO 20022 messages.
 *
 * <p>Message priority affects processing order and routing decisions.
 * Higher priority messages should be processed before lower priority ones.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public enum MessagePriority {

    /**
     * Critical priority - highest level of urgency.
     *
     * <p>Critical messages should be processed immediately and may bypass
     * normal queuing mechanisms. Examples include emergency payment reversals
     * and system alerts.</p>
     */
    CRITICAL(1, "Critical"),

    /**
     * High priority - urgent but not critical.
     *
     * <p>High priority messages should be processed before normal priority
     * messages. Examples include time-sensitive payments and regulatory reports.</p>
     */
    HIGH(2, "High"),

    /**
     * Normal priority - standard processing.
     *
     * <p>Normal priority messages are processed in the order they are received,
     * subject to any higher priority messages in the queue.</p>
     */
    NORMAL(3, "Normal"),

    /**
     * Low priority - non-urgent processing.
     *
     * <p>Low priority messages may be processed during off-peak hours or
     * when system resources are available. Examples include batch reports
     * and non-time-sensitive notifications.</p>
     */
    LOW(4, "Low"),

    /**
     * Background priority - lowest priority.
     *
     * <p>Background priority messages are processed only when system resources
     * are not needed for higher priority messages. Examples include archival
     * operations and maintenance tasks.</p>
     */
    BACKGROUND(5, "Background");

    private final int numericValue;
    private final String displayName;

    /**
     * Constructs a new MessagePriority with the specified numeric value and display name.
     *
     * @param numericValue the numeric priority value (lower = higher priority)
     * @param displayName the human-readable display name
     */
    MessagePriority(int numericValue, String displayName) {
        this.numericValue = numericValue;
        this.displayName = displayName;
    }

    /**
     * Gets the numeric priority value.
     *
     * <p>Lower numeric values indicate higher priority. This allows for
     * easy comparison and sorting of message priorities.</p>
     *
     * @return the numeric priority value
     */
    public int getNumericValue() {
        return numericValue;
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
     * Checks if this priority is higher than the specified priority.
     *
     * @param other the priority to compare against
     * @return true if this priority is higher than the other priority
     */
    public boolean isHigherThan(MessagePriority other) {
        return this.numericValue < other.numericValue;
    }

    /**
     * Checks if this priority is lower than the specified priority.
     *
     * @param other the priority to compare against
     * @return true if this priority is lower than the other priority
     */
    public boolean isLowerThan(MessagePriority other) {
        return this.numericValue > other.numericValue;
    }

    /**
     * Checks if this priority is at least as high as the specified priority.
     *
     * @param other the priority to compare against
     * @return true if this priority is at least as high as the other priority
     */
    public boolean isAtLeast(MessagePriority other) {
        return this.numericValue <= other.numericValue;
    }

    /**
     * Gets the default priority for new messages.
     *
     * @return the default message priority
     */
    public static MessagePriority getDefault() {
        return NORMAL;
    }

    /**
     * Gets the highest priority level.
     *
     * @return the highest priority
     */
    public static MessagePriority getHighest() {
        return CRITICAL;
    }

    /**
     * Gets the lowest priority level.
     *
     * @return the lowest priority
     */
    public static MessagePriority getLowest() {
        return BACKGROUND;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
