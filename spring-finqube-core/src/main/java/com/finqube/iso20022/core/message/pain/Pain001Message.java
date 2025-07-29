package com.finqube.iso20022.core.message.pain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.finqube.iso20022.core.exception.MessageValidationException;
import com.finqube.iso20022.core.exception.MessageValidationException.ValidationError;
import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;

/**
 * Customer Credit Transfer Initiation (pain.001) message.
 *
 * <p>This message is used by customers to initiate credit transfers to their banks.
 * It supports both single and bulk payment instructions.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Single or multiple payment instructions</li>
 *   <li>Support for various payment types (SEPA, domestic, international)</li>
 *   <li>Detailed party information (debtor and creditor)</li>
 *   <li>Payment purpose and remittance information</li>
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

        this.paymentInstructions = new ArrayList<>(paymentInstructions);
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
        return new ArrayList<>(paymentInstructions);
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
     * Gets the control sum of all payment amounts.
     *
     * @return the control sum
     */
    public double getControlSum() {
        return controlSum;
    }

    @Override
    public boolean validate() throws MessageValidationException {
        List<ValidationError> errors = new ArrayList<>();

        // Validate message ID
        if (messageId == null || messageId.trim().isEmpty()) {
            errors.add(new ValidationError("messageId", "REQUIRED",
                "Message ID is required", "ERROR"));
        }

        // Validate payment instructions
        if (paymentInstructions == null || paymentInstructions.isEmpty()) {
            errors.add(new ValidationError("paymentInstructions", "REQUIRED",
                "At least one payment instruction is required", "ERROR"));
        } else {
            // Validate each payment instruction
            for (int i = 0; i < paymentInstructions.size(); i++) {
                PaymentInstruction instruction = paymentInstructions.get(i);
                if (instruction == null) {
                    errors.add(new ValidationError("paymentInstructions[" + i + "]", "REQUIRED",
                        "Payment instruction cannot be null", "ERROR"));
                } else {
                    try {
                        instruction.validate();
                    } catch (MessageValidationException e) {
                        for (ValidationError error : e.getValidationErrors()) {
                            errors.add(new ValidationError("paymentInstructions[" + i + "]." + error.getField(),
                                error.getCode(), error.getMessage(), error.getSeverity()));
                        }
                    }
                }
            }
        }

        // Validate number of transactions
        if (numberOfTransactions <= 0) {
            errors.add(new ValidationError("numberOfTransactions", "INVALID",
                "Number of transactions must be greater than 0", "ERROR"));
        }

        // Validate control sum
        if (controlSum <= 0) {
            errors.add(new ValidationError("controlSum", "INVALID",
                "Control sum must be greater than 0", "ERROR"));
        }

        // Check if validation failed
        if (!errors.isEmpty()) {
            throw new MessageValidationException("Pain001Message validation failed",
                messageId, messageType, errors);
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
                errors.add(new ValidationError("instructionId", "REQUIRED",
                    "Instruction ID is required", "ERROR"));
            }

            if (amount <= 0) {
                errors.add(new ValidationError("amount", "INVALID",
                    "Amount must be greater than 0", "ERROR"));
            }

            if (currency == null || currency.trim().isEmpty()) {
                errors.add(new ValidationError("currency", "REQUIRED",
                    "Currency is required", "ERROR"));
            }

            if (debtorAccount == null || debtorAccount.trim().isEmpty()) {
                errors.add(new ValidationError("debtorAccount", "REQUIRED",
                    "Debtor account is required", "ERROR"));
            }

            if (creditorAccount == null || creditorAccount.trim().isEmpty()) {
                errors.add(new ValidationError("creditorAccount", "REQUIRED",
                    "Creditor account is required", "ERROR"));
            }

            if (!errors.isEmpty()) {
                throw new MessageValidationException("PaymentInstruction validation failed",
                    instructionId, "pain.001.instruction", errors);
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
