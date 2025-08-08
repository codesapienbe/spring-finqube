package com.finqube.iso20022.examples.cli;

import java.util.List;
import java.util.concurrent.Callable;

import com.finqube.iso20022.core.message.pain.Pain001Message;
import com.finqube.iso20022.core.message.pain.Pain001Message.PaymentInstruction;
import com.finqube.iso20022.core.template.Iso20022Template;
import com.finqube.iso20022.core.validation.MessageValidator;
import com.finqube.iso20022.core.validation.ValidationResult;
import com.finqube.iso20022.core.validation.impl.SimpleMessageValidator;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Picocli-based command line example for generating and validating ISO 20022 messages.
 */
@Command(name = "iso20022-cli",
        mixinStandardHelpOptions = true,
        version = "0.1.0",
        description = "Generate and validate ISO 20022 messages from the command line.")
public class Iso20022Cli implements Callable<Integer> {

    @Option(names = {"-i", "--id"}, description = "Message ID", required = true)
    private String messageId;

    @Option(names = {"-a", "--amount"}, description = "Payment amount", required = true)
    private double amount;

    /**
     * Executes the CLI command to build, validate, and print a sample PAIN.001 message.
     *
     * @return exit code compatible with {@link CommandLine.ExitCode}
     * @throws Exception if message processing fails unexpectedly
     */
    @Override
    public Integer call() throws Exception {
        // Secure-by-default: use local instances without external state, no I/O
        Iso20022Template template = new Iso20022Template();
        MessageValidator validator = new SimpleMessageValidator();

        PaymentInstruction instruction = new PaymentInstruction(
                "PI-" + messageId,
                amount,
                "EUR",
                "DE00CLI-DEBTOR",
                "DE00CLI-CREDITOR",
                "CLI payment");

        Pain001Message message = new Pain001Message(messageId, List.of(instruction), 1, amount);

        ValidationResult result = validator.validate(message);
        if (!result.isValid()) {
            System.err.printf("Validation failed with %d errors.%n", result.getErrors().size());
            result.getErrors().forEach(e -> System.err.printf("- %s: %s%n", e.getField(), e.getMessage()));
            return CommandLine.ExitCode.SOFTWARE;
        }

        String xml = template.generateXml(message);
        System.out.printf("Generated XML (%d chars):%n%s%n", xml.length(), xml.substring(0, Math.min(xml.length(), 400)));
        return CommandLine.ExitCode.OK;
    }

    /**
     * Main entry point for the Picocli example.
     *
     * @param args CLI arguments
     */
    public static void main(String[] args) {
        int exit = new CommandLine(new Iso20022Cli()).execute(args);
        System.exit(exit);
    }
}
