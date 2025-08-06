package com.finqube.iso20022.core.security.audit;

/**
 * Risk levels for assessing the security risk associated with audited operations.
 *
 * <p>This enum defines the different risk levels that can be assigned to
 * audit log entries to indicate the potential security risk associated
 * with the operation being audited.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public enum RiskLevel {

    /**
     * No risk - normal operation.
     */
    NONE(0, "NONE"),

    /**
     * Low risk - minor security considerations.
     */
    LOW(1, "LOW"),

    /**
     * Medium risk - moderate security considerations.
     */
    MEDIUM(2, "MEDIUM"),

    /**
     * High risk - significant security considerations.
     */
    HIGH(3, "HIGH"),

    /**
     * Critical risk - immediate security threat.
     */
    CRITICAL(4, "CRITICAL");

    private final int level;
    private final String displayName;

    /**
     * Constructs a new RiskLevel instance.
     *
     * @param level the numeric risk level (higher = more risky)
     * @param displayName the display name for the risk level
     */
    RiskLevel(int level, String displayName) {
        this.level = level;
        this.displayName = displayName;
    }

    /**
     * Gets the numeric risk level.
     *
     * @return the risk level value
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets the display name for this risk level.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this risk level is at least as high as the given level.
     *
     * @param other the level to compare against
     * @return true if this level is at least as high, false otherwise
     */
    public boolean isAtLeast(RiskLevel other) {
        return this.level >= other.level;
    }

    /**
     * Checks if this risk level is higher than the given level.
     *
     * @param other the level to compare against
     * @return true if this level is higher, false otherwise
     */
    public boolean isHigherThan(RiskLevel other) {
        return this.level > other.level;
    }

    /**
     * Checks if this is a high-risk level.
     *
     * @return true if high or critical risk, false otherwise
     */
    public boolean isHighRisk() {
        return HIGH.equals(this) || CRITICAL.equals(this);
    }

    /**
     * Checks if this is a critical risk level.
     *
     * @return true if critical risk, false otherwise
     */
    public boolean isCriticalRisk() {
        return CRITICAL.equals(this);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
