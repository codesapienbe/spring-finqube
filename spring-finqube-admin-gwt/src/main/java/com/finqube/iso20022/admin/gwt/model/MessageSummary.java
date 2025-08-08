package com.finqube.iso20022.admin.gwt.model;

import java.util.Map;

/**
 * Summary aggregates used by the admin dashboard overview.
 */
public record MessageSummary(
        long totalIncoming,
        long totalOutgoing,
        Map<String, Long> byStatusIncoming,
        Map<String, Long> byStatusOutgoing) {
}
