package com.finqube.iso20022.core.message;

import java.time.LocalDateTime;

import com.finqube.iso20022.core.exception.MessageValidationException;

/**
 * Base interface for all ISO 20022 message types.
 *
 * <p>This interface provides common functionality that all ISO 20022 messages
 * should implement, including message identification, timestamps, and validation.</p>
 *
 * <p>Implementations should provide:</p>
 * <ul>
 *   <li>Unique message identification</li>
 *   <li>Message creation timestamp</li>
 *   <li>Message type classification</li>
 *   <li>Validation capabilities</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface BaseMessage {

    /**
     * Gets the unique identifier for this message.
     *
     * <p>The message ID should be globally unique and follow ISO 20022
     * naming conventions. Typically includes a timestamp and sequence number.</p>
     *
     * @return the unique message identifier
     */
    String getMessageId();

    /**
     * Gets the creation timestamp of this message.
     *
     * <p>The creation time should be set when the message is first created
     * and should not be modified during the message lifecycle.</p>
     *
     * @return the message creation timestamp
     */
    LocalDateTime getCreationTime();

    /**
     * Gets the ISO 20022 message type code.
     *
     * <p>The message type identifies the specific ISO 20022 message format,
     * such as "pain.001.001.11" for Customer Credit Transfer Initiation.</p>
     *
     * @return the ISO 20022 message type code
     */
    String getMessageType();

    /**
     * Gets the business process associated with this message.
     *
     * <p>The business process identifies the high-level workflow this message
     * participates in, such as "pain" for Payment Initiation or "pacs" for
     * Payment Clearing and Settlement.</p>
     *
     * @return the business process identifier
     */
    String getBusinessProcess();

    /**
     * Validates this message according to ISO 20022 rules.
     *
     * <p>This method should perform both structural validation (XML schema)
     * and business rule validation specific to the message type.</p>
     *
     * @return true if the message is valid, false otherwise
     * @throws MessageValidationException if validation fails with details
     */
    boolean validate() throws MessageValidationException;

    /**
     * Gets a human-readable description of this message.
     *
     * <p>The description should provide context about what this message
     * represents in business terms.</p>
     *
     * @return a human-readable description of the message
     */
    String getDescription();

    /**
     * Checks if this message requires acknowledgment.
     *
     * <p>Some ISO 20022 messages require explicit acknowledgment from
     * the receiving system, while others are fire-and-forget.</p>
     *
     * @return true if acknowledgment is required, false otherwise
     */
    boolean requiresAcknowledgment();

    /**
     * Gets the priority level of this message.
     *
     * <p>Message priority affects processing order and routing decisions.
     * Higher priority messages should be processed before lower priority ones.</p>
     *
     * @return the message priority level
     */
    MessagePriority getPriority();

    /**
     * Gets the version of the ISO 20022 schema used for this message.
     *
     * <p>The schema version helps ensure compatibility and proper validation
     * when processing messages across different ISO 20022 implementations.</p>
     *
     * @return the ISO 20022 schema version
     */
    String getSchemaVersion();

    /**
     * Gets the list of transaction identifiers associated with this message.
     *
     * <p>For payment messages, this typically contains the unique identifiers
     * of the individual transactions being processed.</p>
     *
     * @return the list of transaction identifiers
     */
    java.util.List<String> getTransactions();

    /**
     * Gets the total number of transactions in this message.
     *
     * <p>This count should match the size of the transactions list and
     * represents the number of individual operations being processed.</p>
     *
     * @return the number of transactions
     */
    int getTransactionCount();

    /**
     * Gets the total amount of all transactions in this message.
     *
     * <p>For payment messages, this represents the sum of all individual
     * transaction amounts being processed.</p>
     *
     * @return the total amount of all transactions
     */
    double getTotalAmount();
}
