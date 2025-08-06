package com.finqube.iso20022.core.security.routing;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Defines a message routing destination with security requirements and filtering criteria.
 *
 * <p>This class represents a secure route that messages can take, including
 * destination information, security requirements, and filtering rules.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * MessageRoute route = MessageRoute.builder()
 *     .routeId("SWIFT_PACS_008")
 *     .displayName("SWIFT PACS.008 Route")
 *     .destination("swift://fin.primary")
 *     .messageTypes(Set.of("pacs.008.001.08"))
 *     .requiredSecurityLevel(SecurityLevel.HIGH)
 *     .maxMessageSize(10 * 1024 * 1024) // 10MB
 *     .timeout(30, TimeUnit.SECONDS)
 *     .build();
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class MessageRoute {

    private final String routeId;
    private final String displayName;
    private final String destination;
    private final Set<String> messageTypes;
    private final SecurityLevel requiredSecurityLevel;
    private final long maxMessageSize;
    private final long timeoutMillis;
    private final Map<String, String> metadata;
    private final boolean enabled;
    private final int priority;

    /**
     * Private constructor for builder pattern.
     */
    private MessageRoute(Builder builder) {
        this.routeId = builder.routeId;
        this.displayName = builder.displayName;
        this.destination = builder.destination;
        this.messageTypes = builder.messageTypes;
        this.requiredSecurityLevel = builder.requiredSecurityLevel;
        this.maxMessageSize = builder.maxMessageSize;
        this.timeoutMillis = builder.timeoutMillis;
        this.metadata = builder.metadata;
        this.enabled = builder.enabled;
        this.priority = builder.priority;
    }

    /**
     * Gets the unique route identifier.
     *
     * @return the route identifier
     */
    public String getRouteId() {
        return routeId;
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
     * Gets the destination endpoint or transport identifier.
     *
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Gets the set of supported message types.
     *
     * @return the supported message types
     */
    public Set<String> getMessageTypes() {
        return messageTypes;
    }

    /**
     * Gets the required security level for this route.
     *
     * @return the required security level
     */
    public SecurityLevel getRequiredSecurityLevel() {
        return requiredSecurityLevel;
    }

    /**
     * Gets the maximum message size in bytes.
     *
     * @return the maximum message size
     */
    public long getMaxMessageSize() {
        return maxMessageSize;
    }

    /**
     * Gets the timeout in milliseconds.
     *
     * @return the timeout in milliseconds
     */
    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    /**
     * Gets the route metadata.
     *
     * @return the metadata map
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * Checks if this route is enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gets the route priority (higher values = higher priority).
     *
     * @return the priority value
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Checks if this route supports the given message type.
     *
     * @param messageType the message type to check
     * @return true if supported, false otherwise
     */
    public boolean supportsMessageType(String messageType) {
        return messageTypes.contains(messageType);
    }

    /**
     * Gets a metadata value by key.
     *
     * @param key the metadata key
     * @return the metadata value, or null if not found
     */
    public String getMetadata(String key) {
        return metadata.get(key);
    }

    @Override
    public String toString() {
        return String.format("MessageRoute{routeId='%s', destination='%s', enabled=%s, priority=%d}",
                routeId, destination, enabled, priority);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MessageRoute that = (MessageRoute) obj;
        return routeId.equals(that.routeId);
    }

    @Override
    public int hashCode() {
        return routeId.hashCode();
    }

    /**
     * Creates a new builder for MessageRoute instances.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating MessageRoute instances.
     */
    public static class Builder {
        private String routeId;
        private String displayName;
        private String destination;
        private Set<String> messageTypes;
        private SecurityLevel requiredSecurityLevel = SecurityLevel.MEDIUM;
        private long maxMessageSize = 10 * 1024 * 1024; // 10MB default
        private long timeoutMillis = 30000; // 30 seconds default
        private Map<String, String> metadata = Map.of();
        private boolean enabled = true;
        private int priority = 0;

        public Builder routeId(String routeId) {
            this.routeId = routeId;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder messageTypes(Set<String> messageTypes) {
            this.messageTypes = messageTypes;
            return this;
        }

        public Builder requiredSecurityLevel(SecurityLevel requiredSecurityLevel) {
            this.requiredSecurityLevel = requiredSecurityLevel;
            return this;
        }

        public Builder maxMessageSize(long maxMessageSize) {
            this.maxMessageSize = maxMessageSize;
            return this;
        }

        public Builder timeout(long timeout, TimeUnit unit) {
            this.timeoutMillis = unit.toMillis(timeout);
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public MessageRoute build() {
            if (routeId == null || routeId.trim().isEmpty()) {
                throw new IllegalArgumentException("Route ID is required");
            }
            if (destination == null || destination.trim().isEmpty()) {
                throw new IllegalArgumentException("Destination is required");
            }
            if (messageTypes == null || messageTypes.isEmpty()) {
                throw new IllegalArgumentException("At least one message type is required");
            }
            return new MessageRoute(this);
        }
    }
}
