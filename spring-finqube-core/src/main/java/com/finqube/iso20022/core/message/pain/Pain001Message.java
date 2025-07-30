package com.finqube.iso20022.core.message.pain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.finqube.iso20022.core.exception.MessageValidationException;
import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;
import com.finqube.iso20022.core.validation.ValidationError;

/**
 * Implementation of ISO 20022 pain.001.001.11 message (Customer Credit Transfer Initiation).
 *
 * <p>This class represents a pain.001 message used for initiating customer credit transfers.
 * It contains payment instructions with details about the transfer amounts, accounts, and purposes.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Payment instruction management</li>
 *   <li>Transaction counting and control sum validation</li>
 *   <li>Comprehensive validation rules</li>
 *   <li>ISO 20022 schema compliance</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class Pain001Message implements BaseMessage {

    private final String messageId;
    private final LocalDateTime creationTime;
    private final String messageType;
    private final String businessProcess;
    private final String schemaVersion;
    private final MessagePriority priority;
    private final boolean requiresAcknowledgment;
    private final String description;

    // Payment instruction details
    private final List<PaymentInstruction> paymentInstructions;
    private final int numberOfTransactions;
    private final double controlSum;

    /**
     * Constructs a new Pain001Message with the specified parameters.
     *
     * @param messageId the unique message identifier
     * @param paymentInstructions the list of payment instructions
     * @param numberOfTransactions the total number of transactions
     * @param controlSum the control sum of all amounts
     */
    public Pain001Message(String messageId, List<PaymentInstruction> paymentInstructions,
                         int numberOfTransactions, double controlSum) {
        this.messageId = messageId;
        this.creationTime = LocalDateTime.now();
        this.messageType = "pain.001.001.11";
        this.businessProcess = "pain";
        this.schemaVersion = "2019-09-01";
        this.priority = MessagePriority.NORMAL;
        this.requiresAcknowledgment = true;
        this.description = "Customer Credit Transfer Initiation";

        this.paymentInstructions = paymentInstructions != null ? new ArrayList<>(paymentInstructions) : new ArrayList<>();
        this.numberOfTransactions = numberOfTransactions;
        this.controlSum = controlSum;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    @Override
    public String getMessageType() {
        return messageType;
    }

    @Override
    public String getBusinessProcess() {
        return businessProcess;
    }

    @Override
    public String getSchemaVersion() {
        return schemaVersion;
    }

    @Override
    public MessagePriority getPriority() {
        return priority;
    }

    @Override
    public boolean requiresAcknowledgment() {
        return requiresAcknowledgment;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Gets the list of payment instructions in this message.
     *
     * @return the payment instructions
     */
    public List<PaymentInstruction> getPaymentInstructions() {
        return List.copyOf(paymentInstructions);
    }

    /**
     * Gets the total number of transactions in this message.
     *
     * @return the number of transactions
     */
    public int getNumberOfTransactions() {
        return numberOfTransactions;
    }

    /**
     * Gets the control sum of all amounts in this message.
     *
     * @return the control sum
     */
    public double getControlSum() {
        return controlSum;
    }

    @Override
    public List<String> getTransactions() {
        return paymentInstructions.stream()
                .map(PaymentInstruction::getInstructionId)
                .collect(Collectors.toList());
    }

    @Override
    public int getTransactionCount() {
        return numberOfTransactions;
    }

    @Override
    public double getTotalAmount() {
        return controlSum;
    }

    @Override
    public boolean validate() throws MessageValidationException {
        List<ValidationError> errors = new ArrayList<>();

        // Validate message ID
        if (messageId == null || messageId.trim().isEmpty()) {
            errors.add(new ValidationError("REQUIRED", "Message ID is required",
                ValidationError.ErrorSeverity.ERROR, null, "messageId", messageId, "Pain001Message"));
        }

        // Validate payment instructions
        if (paymentInstructions == null || paymentInstructions.isEmpty()) {
            errors.add(new ValidationError("REQUIRED", "At least one payment instruction is required",
                ValidationError.ErrorSeverity.ERROR, null, "paymentInstructions", null, "Pain001Message"));
        } else {
            // Validate each payment instruction
            for (int i = 0; i < paymentInstructions.size(); i++) {
                PaymentInstruction instruction = paymentInstructions.get(i);
                if (instruction == null) {
                    errors.add(new ValidationError("REQUIRED", "Payment instruction cannot be null",
                        ValidationError.ErrorSeverity.ERROR, null, "paymentInstructions[" + i + "]", null, "Pain001Message"));
                } else {
                    try {
                        instruction.validate();
                    } catch (MessageValidationException e) {
                        // Add nested validation errors
                        errors.add(new ValidationError("NESTED_VALIDATION", "Payment instruction validation failed",
                            ValidationError.ErrorSeverity.ERROR, null, "paymentInstructions[" + i + "]", null, "Pain001Message"));
                    }
                }
            }
        }

        // Validate number of transactions
        if (numberOfTransactions <= 0) {
            errors.add(new ValidationError("INVALID", "Number of transactions must be greater than 0",
                ValidationError.ErrorSeverity.ERROR, null, "numberOfTransactions", String.valueOf(numberOfTransactions), "Pain001Message"));
        }

        // Validate control sum
        if (controlSum <= 0) {
            errors.add(new ValidationError("INVALID", "Control sum must be greater than 0",
                ValidationError.ErrorSeverity.ERROR, null, "controlSum", String.valueOf(controlSum), "Pain001Message"));
        }

        // Check if validation failed
        if (!errors.isEmpty()) {
            String errorMessage = String.format("Pain001Message validation failed [Message ID: %s] [Message Type: %s] [Validation Errors: %d]",
                messageId, messageType, errors.size());
            throw new MessageValidationException(errorMessage, messageId, messageType);
        }

        return true;
    }

    /**
     * Represents a single payment instruction within a pain.001 message.
     */
    public static class PaymentInstruction {
        private final String instructionId;
        private final double amount;
        private final String currency;
        private final String debtorAccount;
        private final String creditorAccount;
        private final String purpose;

        /**
         * Constructs a new PaymentInstruction.
         *
         * @param instructionId the unique instruction identifier
         * @param amount the payment amount
         * @param currency the payment currency
         * @param debtorAccount the debtor account number
         * @param creditorAccount the creditor account number
         * @param purpose the payment purpose
         */
        public PaymentInstruction(String instructionId, double amount, String currency,
                                String debtorAccount, String creditorAccount, String purpose) {
            this.instructionId = instructionId;
            this.amount = amount;
            this.currency = currency;
            this.debtorAccount = debtorAccount;
            this.creditorAccount = creditorAccount;
            this.purpose = purpose;
        }

        /**
         * Validates this payment instruction.
         *
         * @throws MessageValidationException if validation fails
         */
        public void validate() throws MessageValidationException {
            List<ValidationError> errors = new ArrayList<>();

            if (instructionId == null || instructionId.trim().isEmpty()) {
                errors.add(new ValidationError("REQUIRED", "Instruction ID is required",
                    ValidationError.ErrorSeverity.ERROR, null, "instructionId", instructionId, "PaymentInstruction"));
            }

            if (amount <= 0) {
                errors.add(new ValidationError("INVALID", "Amount must be greater than 0",
                    ValidationError.ErrorSeverity.ERROR, null, "amount", String.valueOf(amount), "PaymentInstruction"));
            }

            if (currency == null || currency.trim().isEmpty()) {
                errors.add(new ValidationError("REQUIRED", "Currency is required",
                    ValidationError.ErrorSeverity.ERROR, null, "currency", currency, "PaymentInstruction"));
            }

            if (debtorAccount == null || debtorAccount.trim().isEmpty()) {
                errors.add(new ValidationError("REQUIRED", "Debtor account is required",
                    ValidationError.ErrorSeverity.ERROR, null, "debtorAccount", debtorAccount, "PaymentInstruction"));
            }

            if (creditorAccount == null || creditorAccount.trim().isEmpty()) {
                errors.add(new ValidationError("REQUIRED", "Creditor account is required",
                    ValidationError.ErrorSeverity.ERROR, null, "creditorAccount", creditorAccount, "PaymentInstruction"));
            }

            if (!errors.isEmpty()) {
                String errorMessage = String.format("PaymentInstruction validation failed [Message ID: %s] [Message Type: %s] [Validation Errors: %d]",
                    instructionId, "pain.001.instruction", errors.size());
                throw new MessageValidationException(errorMessage, instructionId, "pain.001.instruction");
            }
        }

        // Getters
        public String getInstructionId() { return instructionId; }
        public double getAmount() { return amount; }
        public String getCurrency() { return currency; }
        public String getDebtorAccount() { return debtorAccount; }
        public String getCreditorAccount() { return creditorAccount; }
        public String getPurpose() { return purpose; }
    }
}
