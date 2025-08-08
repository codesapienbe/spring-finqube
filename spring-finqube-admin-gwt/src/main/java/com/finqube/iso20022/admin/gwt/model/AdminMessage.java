package com.finqube.iso20022.admin.gwt.model;

import java.time.Instant;

/**
 * Immutable view model representing a single financial message for the admin dashboard.
 *
 * <p>This is a read-only projection for UI/REST purposes and does not affect core
 * business processing. Sensitive payload content is intentionally omitted to avoid
 * leaking data into logs or the UI.</p>
 */
public record AdminMessage(
        String id,
        Instant timestamp,
        Direction direction,
        String messageType,
        String status,
        String summary) {
}
