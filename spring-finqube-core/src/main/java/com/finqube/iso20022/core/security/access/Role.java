package com.finqube.iso20022.core.security.access;

/**
 * System-wide roles used for role-based access control (RBAC).
 *
 * <p>This enum defines the standard roles recognised by the Spring Finqube
 * ISO 20022 system. Additional roles can be added by custom extensions if
 * needed, but these provide a sensible baseline for most deployments.</p>
 *
 * <ul>
 *   <li>{@code ADMIN} – Full administrative privileges across the system.</li>
 *   <li>{@code USER} – Standard user capabilities for day-to-day operations.</li>
 *   <li>{@code AUDITOR} – Read-only access to data and audit logs.</li>
 *   <li>{@code SYSTEM} – Internal service account with technical privileges.</li>
 *   <li>{@code GUEST} – Limited, read-only access for trial or demonstration.</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public enum Role {

    /** Administrator with full privileges. */
    ADMIN("Administrator with full privileges"),

    /** Standard business user. */
    USER("Standard user"),

    /** Read-only auditor role. */
    AUDITOR("Auditor with read-only access"),

    /** Internal system/service account. */
    SYSTEM("System internal service"),

    /** Guest or trial user with minimal privileges. */
    GUEST("Guest read-only access");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    /**
     * Gets the human-readable role description.
     *
     * @return the role description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name();
    }
}
