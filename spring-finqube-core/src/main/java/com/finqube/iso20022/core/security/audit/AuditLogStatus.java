package com.finqube.iso20022.core.security.audit;

/**
 * Audit log status indicating the outcome of audited operations.
 *
 * <p>This enum defines the different status values that can be assigned to
 * audit log entries to indicate the success, failure, or other outcomes
 * of security operations.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public enum AuditLogStatus {

    /**
     * Operation completed successfully.
     */
    SUCCESS("SUCCESS"),

    /**
     * Operation failed.
     */
    FAILED("FAILED"),

    /**
     * Operation was denied due to insufficient permissions.
     */
    DENIED("DENIED"),

    /**
     * Operation was blocked due to security policy.
     */
    BLOCKED("BLOCKED"),

    /**
     * Operation was suspended or paused.
     */
    SUSPENDED("SUSPENDED"),

    /**
     * Operation is in progress.
     */
    IN_PROGRESS("IN_PROGRESS"),

    /**
     * Operation was cancelled.
     */
    CANCELLED("CANCELLED"),

    /**
     * Operation timed out.
     */
    TIMEOUT("TIMEOUT"),

    /**
     * Operation was retried.
     */
    RETRIED("RETRIED"),

    /**
     * Operation was partially completed.
     */
    PARTIAL("PARTIAL"),

    /**
     * Operation status is unknown.
     */
    UNKNOWN("UNKNOWN");

    private final String displayName;

    /**
     * Constructs a new AuditLogStatus instance.
     *
     * @param displayName the display name for the status
     */
    AuditLogStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name for this status.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this status indicates a successful operation.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return SUCCESS.equals(this);
    }

    /**
     * Checks if this status indicates a failed operation.
     *
     * @return true if failed, false otherwise
     */
    public boolean isFailed() {
        return FAILED.equals(this) || DENIED.equals(this) || BLOCKED.equals(this) || TIMEOUT.equals(this);
    }

    /**
     * Checks if this status indicates a security-related failure.
     *
     * @return true if security-related failure, false otherwise
     */
    public boolean isSecurityFailure() {
        return DENIED.equals(this) || BLOCKED.equals(this);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
