package com.finqube.iso20022.core.security.audit;

/**
 * Audit log levels for categorizing the severity and importance of audit events.
 *
 * <p>This enum defines the different levels of audit logging that can be used
 * to categorize security events based on their severity and compliance requirements.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public enum AuditLogLevel {

    /**
     * Debug level for detailed diagnostic information.
     * Used for troubleshooting and development purposes.
     */
    DEBUG(0, "DEBUG"),

    /**
     * Info level for general operational information.
     * Used for normal operations and informational events.
     */
    INFO(1, "INFO"),

    /**
     * Warning level for concerning but non-critical events.
     * Used for events that may indicate potential issues.
     */
    WARN(2, "WARN"),

    /**
     * Error level for error conditions that need attention.
     * Used for failed operations and error conditions.
     */
    ERROR(3, "ERROR"),

    /**
     * Critical level for critical security events.
     * Used for security violations and critical failures.
     */
    CRITICAL(4, "CRITICAL"),

    /**
     * Compliance level for regulatory compliance events.
     * Used for events that must be logged for compliance purposes.
     */
    COMPLIANCE(5, "COMPLIANCE"),

    /**
     * Security level for security-related events.
     * Used for authentication, authorization, and security operations.
     */
    SECURITY(6, "SECURITY");

    private final int priority;
    private final String displayName;

    /**
     * Constructs a new AuditLogLevel instance.
     *
     * @param priority the numeric priority (higher = more important)
     * @param displayName the display name for the level
     */
    AuditLogLevel(int priority, String displayName) {
        this.priority = priority;
        this.displayName = displayName;
    }

    /**
     * Gets the numeric priority of this level.
     *
     * @return the priority value
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Gets the display name for this level.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this level is at least as important as the given level.
     *
     * @param other the level to compare against
     * @return true if this level is at least as important, false otherwise
     */
    public boolean isAtLeast(AuditLogLevel other) {
        return this.priority >= other.priority;
    }

    /**
     * Checks if this level is more important than the given level.
     *
     * @param other the level to compare against
     * @return true if this level is more important, false otherwise
     */
    public boolean isMoreImportantThan(AuditLogLevel other) {
        return this.priority > other.priority;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
