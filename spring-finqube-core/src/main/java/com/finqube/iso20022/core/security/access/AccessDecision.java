package com.finqube.iso20022.core.security.access;

/**
 * Outcome of a role-based access-control decision.
 */
public enum AccessDecision {
    /** Access granted. */
    GRANTED,
    /** Access explicitly denied. */
    DENIED
}
