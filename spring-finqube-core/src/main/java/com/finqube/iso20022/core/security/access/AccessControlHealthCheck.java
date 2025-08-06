package com.finqube.iso20022.core.security.access;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Lightweight health-check for the access-control subsystem.
 */
public class AccessControlHealthCheck {

    private final String accessControlManagerId;
    private final LocalDateTime timestamp;
    private final String status;
    private final String message;
    private final boolean healthy;

    public AccessControlHealthCheck(String accessControlManagerId, LocalDateTime timestamp,
                                    String status, String message, boolean healthy) {
        this.accessControlManagerId = Objects.requireNonNull(accessControlManagerId);
        this.timestamp = Objects.requireNonNull(timestamp);
        this.status = Objects.requireNonNull(status);
        this.message = message;
        this.healthy = healthy;
    }

    public String getAccessControlManagerId() {
        return accessControlManagerId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isHealthy() {
        return healthy;
    }

    @Override
    public String toString() {
        return "AccessControlHealthCheck{" +
                "id='" + accessControlManagerId + '\'' +
                ", status='" + status + '\'' +
                ", healthy=" + healthy +
                '}';
    }
}
