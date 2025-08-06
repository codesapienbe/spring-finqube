package com.finqube.iso20022.core.security.access;

import java.util.Set;

/**
 * Role-based access-control (RBAC) manager.
 *
 * <p>This interface defines the contract for evaluating and managing access
 * permissions based on user roles. Implementations <strong>must</strong>
 * guarantee thread-safety.</p>
 */
public interface AccessControlManager {

    /**
     * Evaluates whether a user may perform the given action on the resource.
     *
     * @param userId   user identifier (nullable for anonymous)
     * @param resource resource identifier (e.g. {@code "MESSAGE"})
     * @param action   action identifier   (e.g. {@code "SEND"})
     * @return decision ({@link AccessDecision#GRANTED} or {@link AccessDecision#DENIED})
     */
    AccessDecision decide(String userId, String resource, String action);

    /**
     * Convenience wrapper returning a boolean.
     */
    default boolean isAccessAllowed(String userId, String resource, String action) {
        return decide(userId, resource, action) == AccessDecision.GRANTED;
    }

    /**
     * Assigns a role to a user.
     */
    void grantRole(String userId, Role role);

    /**
     * Revokes a role from a user.
     */
    void revokeRole(String userId, Role role);

    /**
     * Returns all roles assigned to the user.
     */
    Set<Role> getUserRoles(String userId);

    /** Unique identifier for this manager instance. */
    String getAccessControlManagerId();

    /** Display name suitable for logs or UI. */
    String getDisplayName();

    /** Semantic version. */
    String getVersion();

    /** Whether the manager is ready for use. */
    boolean isAvailable();

    /** Performs a health-check. */
    AccessControlHealthCheck healthCheck();
}
