package com.finqube.iso20022.core.security.key;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Result of a key rotation operation.
 *
 * <p>This class provides detailed information about the result of key rotation
 * operations, including success status, timing information, and any errors or warnings.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class KeyRotationResult {

    private final String rotationId;
    private final String keyId;
    private final KeyRotationManager.RotationStatus status;
    private final boolean successful;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final long durationMs;
    private final String oldKeyId;
    private final String newKeyId;
    private final KeyRotationManager.KeyType newKeyType;
    private final List<String> errors;
    private final List<String> warnings;
    private final String description;

    /**
     * Constructs a new KeyRotationResult with the specified parameters.
     *
     * @param rotationId the rotation identifier
     * @param keyId the key identifier
     * @param status the rotation status
     * @param successful whether the rotation was successful
     * @param startTime the start time
     * @param endTime the end time
     * @param durationMs the duration in milliseconds
     * @param oldKeyId the old key identifier
     * @param newKeyId the new key identifier
     * @param newKeyType the new key type
     * @param errors list of errors
     * @param warnings list of warnings
     * @param description the rotation description
     */
    public KeyRotationResult(String rotationId, String keyId, KeyRotationManager.RotationStatus status,
                           boolean successful, LocalDateTime startTime, LocalDateTime endTime, long durationMs,
                           String oldKeyId, String newKeyId, KeyRotationManager.KeyType newKeyType,
                           List<String> errors, List<String> warnings, String description) {
        this.rotationId = rotationId;
        this.keyId = keyId;
        this.status = status;
        this.successful = successful;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMs = durationMs;
        this.oldKeyId = oldKeyId;
        this.newKeyId = newKeyId;
        this.newKeyType = newKeyType;
        this.errors = errors;
        this.warnings = warnings;
        this.description = description;
    }

    /**
     * Gets the rotation identifier.
     *
     * @return the rotation identifier
     */
    public String getRotationId() {
        return rotationId;
    }

    /**
     * Gets the key identifier.
     *
     * @return the key identifier
     */
    public String getKeyId() {
        return keyId;
    }

    /**
     * Gets the rotation status.
     *
     * @return the rotation status
     */
    public KeyRotationManager.RotationStatus getStatus() {
        return status;
    }

    /**
     * Checks if the rotation was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time.
     *
     * @return the end time
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Gets the duration in milliseconds.
     *
     * @return the duration in milliseconds
     */
    public long getDurationMs() {
        return durationMs;
    }

    /**
     * Gets the formatted duration.
     *
     * @return the formatted duration string
     */
    public String getFormattedDuration() {
        if (durationMs < 1000) {
            return durationMs + "ms";
        } else if (durationMs < 60000) {
            return String.format("%.2fs", durationMs / 1000.0);
        } else {
            long minutes = durationMs / 60000;
            long seconds = (durationMs % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }

    /**
     * Gets the old key identifier.
     *
     * @return the old key identifier
     */
    public String getOldKeyId() {
        return oldKeyId;
    }

    /**
     * Gets the new key identifier.
     *
     * @return the new key identifier
     */
    public String getNewKeyId() {
        return newKeyId;
    }

    /**
     * Gets the new key type.
     *
     * @return the new key type
     */
    public KeyRotationManager.KeyType getNewKeyType() {
        return newKeyType;
    }

    /**
     * Gets the list of errors.
     *
     * @return the list of errors
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Gets the list of warnings.
     *
     * @return the list of warnings
     */
    public List<String> getWarnings() {
        return warnings;
    }

    /**
     * Gets the rotation description.
     *
     * @return the rotation description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if there are any errors.
     *
     * @return true if there are errors, false otherwise
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    /**
     * Checks if there are any warnings.
     *
     * @return true if there are warnings, false otherwise
     */
    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }

    /**
     * Gets a summary of the rotation result.
     *
     * @return the summary
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Key rotation ");
        sb.append(successful ? "completed successfully" : "failed");
        sb.append(" for key '").append(keyId).append("'");

        if (newKeyId != null) {
            sb.append(" -> '").append(newKeyId).append("'");
        }

        sb.append(" in ").append(getFormattedDuration());

        if (hasErrors()) {
            sb.append(" with ").append(errors.size()).append(" error(s)");
        }

        if (hasWarnings()) {
            sb.append(" with ").append(warnings.size()).append(" warning(s)");
        }

        return sb.toString();
    }

    /**
     * Creates a successful rotation result.
     *
     * @param rotationId the rotation identifier
     * @param keyId the key identifier
     * @param startTime the start time
     * @param endTime the end time
     * @param oldKeyId the old key identifier
     * @param newKeyId the new key identifier
     * @param newKeyType the new key type
     * @param description the rotation description
     * @return a successful rotation result
     */
    public static KeyRotationResult success(String rotationId, String keyId, LocalDateTime startTime,
                                          LocalDateTime endTime, String oldKeyId, String newKeyId,
                                          KeyRotationManager.KeyType newKeyType, String description) {
        long durationMs = java.time.Duration.between(startTime, endTime).toMillis();
        return new KeyRotationResult(rotationId, keyId, KeyRotationManager.RotationStatus.COMPLETED, true,
                startTime, endTime, durationMs, oldKeyId, newKeyId, newKeyType, List.of(), List.of(), description);
    }

    /**
     * Creates a failed rotation result.
     *
     * @param rotationId the rotation identifier
     * @param keyId the key identifier
     * @param startTime the start time
     * @param endTime the end time
     * @param errors list of errors
     * @param warnings list of warnings
     * @param description the rotation description
     * @return a failed rotation result
     */
    public static KeyRotationResult failure(String rotationId, String keyId, LocalDateTime startTime,
                                          LocalDateTime endTime, List<String> errors, List<String> warnings,
                                          String description) {
        long durationMs = java.time.Duration.between(startTime, endTime).toMillis();
        return new KeyRotationResult(rotationId, keyId, KeyRotationManager.RotationStatus.FAILED, false,
                startTime, endTime, durationMs, null, null, null, errors, warnings, description);
    }

    /**
     * Creates a pending rotation result.
     *
     * @param rotationId the rotation identifier
     * @param keyId the key identifier
     * @param description the rotation description
     * @return a pending rotation result
     */
    public static KeyRotationResult pending(String rotationId, String keyId, String description) {
        LocalDateTime now = LocalDateTime.now();
        return new KeyRotationResult(rotationId, keyId, KeyRotationManager.RotationStatus.PENDING, false,
                now, now, 0, null, null, null, List.of(), List.of(), description);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        KeyRotationResult that = (KeyRotationResult) obj;
        return Objects.equals(rotationId, that.rotationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rotationId);
    }

    @Override
    public String toString() {
        return String.format("KeyRotationResult{rotationId='%s', keyId='%s', status=%s, successful=%s, duration=%s}",
                rotationId, keyId, status, successful, getFormattedDuration());
    }
}
