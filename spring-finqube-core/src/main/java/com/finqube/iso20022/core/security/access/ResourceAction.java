package com.finqube.iso20022.core.security.access;

import java.util.Objects;

/**
 * Immutable pair representing a resource and an action on that resource.
 *
 * <p>This record is used by the RBAC subsystem to express fine-grained
 * permissions such as {@code ("MESSAGE", "SEND")}. Both parts are stored in
 * upper-case to ensure case-insensitive comparisons.</p>
 *
 * @param resource the resource identifier (e.g. {@code "MESSAGE"})
 * @param action   the action identifier   (e.g. {@code "SEND"})
 */
public record ResourceAction(String resource, String action) {

    public ResourceAction {
        Objects.requireNonNull(resource, "Resource cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");

        // Normalise to upper-case for case-insensitive comparisons
        resource = resource.toUpperCase();
        action = action.toUpperCase();
    }

    @Override
    public String toString() {
        return resource + ':' + action;
    }
}
