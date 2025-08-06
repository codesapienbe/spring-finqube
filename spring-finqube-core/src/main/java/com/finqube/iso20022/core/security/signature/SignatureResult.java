package com.finqube.iso20022.core.security.signature;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Result of a digital signature operation.
 *
 * @author Spring Finqube Team
 */
public class SignatureResult {
    private final boolean valid;
    private final SignatureAlgorithm algorithm;
    private final String signature;
    private final String publicKeyId;
    private final LocalDateTime timestamp;
    private final long durationMs;
    private final List<String> errors;
    private final List<String> warnings;
    private final String description;

    public SignatureResult(boolean valid, SignatureAlgorithm algorithm, String signature,
                          String publicKeyId, LocalDateTime timestamp, long durationMs,
                          List<String> errors, List<String> warnings, String description) {
        this.valid = valid;
        this.algorithm = algorithm;
        this.signature = signature;
        this.publicKeyId = publicKeyId;
        this.timestamp = timestamp;
        this.durationMs = durationMs;
        this.errors = errors != null ? List.copyOf(errors) : List.of();
        this.warnings = warnings != null ? List.copyOf(warnings) : List.of();
        this.description = description;
    }

    public boolean isValid() {
        return valid;
    }

    public SignatureAlgorithm getAlgorithm() {
        return algorithm;
    }

    public String getSignature() {
        return signature;
    }

    public String getPublicKeyId() {
        return publicKeyId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    public String getFormattedDuration() {
        if (durationMs < 1000) {
            return durationMs + "ms";
        } else {
            return String.format("%.2fs", durationMs / 1000.0);
        }
    }

    public String getSummary() {
        return String.format("Signature %s using %s (took %s)",
            valid ? "valid" : "invalid", algorithm, getFormattedDuration());
    }

    public static SignatureResult success(SignatureAlgorithm algorithm, String signature,
                                        String publicKeyId, long durationMs) {
        return new SignatureResult(true, algorithm, signature, publicKeyId,
            LocalDateTime.now(), durationMs, List.of(), List.of(), "Signature verification successful");
    }

    public static SignatureResult failure(SignatureAlgorithm algorithm, String publicKeyId,
                                        long durationMs, List<String> errors) {
        return new SignatureResult(false, algorithm, null, publicKeyId,
            LocalDateTime.now(), durationMs, errors, List.of(), "Signature verification failed");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SignatureResult that = (SignatureResult) obj;
        return valid == that.valid && durationMs == that.durationMs &&
               Objects.equals(algorithm, that.algorithm) &&
               Objects.equals(signature, that.signature) &&
               Objects.equals(publicKeyId, that.publicKeyId) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(errors, that.errors) &&
               Objects.equals(warnings, that.warnings) &&
               Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, algorithm, signature, publicKeyId, timestamp,
                           durationMs, errors, warnings, description);
    }

    @Override
    public String toString() {
        return "SignatureResult{" +
                "valid=" + valid +
                ", algorithm=" + algorithm +
                ", signature='" + signature + '\'' +
                ", publicKeyId='" + publicKeyId + '\'' +
                ", timestamp=" + timestamp +
                ", durationMs=" + durationMs +
                ", errors=" + errors +
                ", warnings=" + warnings +
                ", description='" + description + '\'' +
                '}';
    }
}
