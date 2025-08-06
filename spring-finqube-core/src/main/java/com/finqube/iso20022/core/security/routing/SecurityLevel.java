package com.finqube.iso20022.core.security.routing;

/**
 * Defines security levels for message routing and filtering.
 *
 * <p>This enum represents different levels of security requirements that can be
 * applied to message routes and filtering rules.</p>
 *
 * <p>Security levels are ordered from lowest to highest security:</p>
 * <ul>
 *   <li><strong>NONE</strong> - No security requirements</li>
 *   <li><strong>LOW</strong> - Basic security (e.g., TLS transport)</li>
 *   <li><strong>MEDIUM</strong> - Standard security (e.g., TLS + basic authentication)</li>
 *   <li><strong>HIGH</strong> - High security (e.g., TLS + strong authentication + encryption)</li>
 *   <li><strong>CRITICAL</strong> - Maximum security (e.g., HSM + end-to-end encryption + audit)</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public enum SecurityLevel {

    /**
     * No security requirements.
     *
     * <p>Messages can be sent without any security measures. This level should
     * only be used for internal testing or development environments.</p>
     */
    NONE(0, "None", "No security requirements"),

    /**
     * Basic security level.
     *
     * <p>Requires basic transport security such as TLS encryption. Suitable for
     * internal networks or low-risk environments.</p>
     */
    LOW(1, "Low", "Basic transport security (TLS)"),

    /**
     * Standard security level.
     *
     * <p>Requires TLS transport and basic authentication. This is the default
     * level for most production environments.</p>
     */
    MEDIUM(2, "Medium", "TLS + basic authentication"),

    /**
     * High security level.
     *
     * <p>Requires TLS transport, strong authentication, and message encryption.
     * Suitable for high-value transactions and sensitive data.</p>
     */
    HIGH(3, "High", "TLS + strong authentication + encryption"),

    /**
     * Critical security level.
     *
     * <p>Requires maximum security measures including HSM integration, end-to-end
     * encryption, comprehensive audit logging, and strict access controls.
     * Used for the most sensitive financial transactions.</p>
     */
    CRITICAL(4, "Critical", "HSM + end-to-end encryption + comprehensive audit");

    private final int level;
    private final String displayName;
    private final String description;

    /**
     * Constructor for SecurityLevel enum.
     *
     * @param level the numeric security level
     * @param displayName the human-readable display name
     * @param description the detailed description
     */
    SecurityLevel(int level, String displayName, String description) {
        this.level = level;
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Gets the numeric security level.
     *
     * @return the numeric level
     */
    public int getLevel() {
        return level;
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
     * Gets the detailed description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if this security level is at least as secure as the given level.
     *
     * @param other the security level to compare against
     * @return true if this level is at least as secure, false otherwise
     */
    public boolean isAtLeast(SecurityLevel other) {
        return this.level >= other.level;
    }

    /**
     * Checks if this security level is more secure than the given level.
     *
     * @param other the security level to compare against
     * @return true if this level is more secure, false otherwise
     */
    public boolean isMoreSecureThan(SecurityLevel other) {
        return this.level > other.level;
    }

    /**
     * Gets the next higher security level.
     *
     * @return the next higher security level, or this level if already at maximum
     */
    public SecurityLevel getNextLevel() {
        SecurityLevel[] levels = values();
        int nextIndex = Math.min(this.ordinal() + 1, levels.length - 1);
        return levels[nextIndex];
    }

    /**
     * Gets the previous lower security level.
     *
     * @return the previous lower security level, or this level if already at minimum
     */
    public SecurityLevel getPreviousLevel() {
        SecurityLevel[] levels = values();
        int prevIndex = Math.max(this.ordinal() - 1, 0);
        return levels[prevIndex];
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", displayName, description);
    }
}
