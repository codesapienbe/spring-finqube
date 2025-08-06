package com.finqube.iso20022.core.security.audit;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Security incident identified from audit log analysis.
 *
 * <p>This class represents a security incident that has been identified
 * through analysis of audit log data. It provides detailed information
 * about the incident, its severity, impact, and recommended actions.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class SecurityIncident {

    private final String incidentId;
    private final LocalDateTime detectedAt;
    private final LocalDateTime firstOccurrence;
    private final LocalDateTime lastOccurrence;
    private final String incidentType;
    private final String severity;
    private final String description;
    private final String impact;
    private final String rootCause;
    private final String affectedUsers;
    private final String affectedSystems;
    private final Map<String, Object> evidence;
    private final String status;
    private final String assignedTo;
    private final String resolution;
    private final LocalDateTime resolvedAt;
    private final String recommendations;

    /**
     * Constructs a new SecurityIncident instance.
     *
     * @param incidentId the unique incident identifier
     * @param detectedAt the timestamp when the incident was detected
     * @param firstOccurrence the timestamp of the first occurrence
     * @param lastOccurrence the timestamp of the last occurrence
     * @param incidentType the type of security incident
     * @param severity the severity level
     * @param description the incident description
     * @param impact the impact assessment
     * @param rootCause the root cause analysis
     * @param affectedUsers the affected users
     * @param affectedSystems the affected systems
     * @param evidence the evidence collected
     * @param status the incident status
     * @param assignedTo the person assigned to handle the incident
     * @param resolution the resolution details
     * @param resolvedAt the timestamp when the incident was resolved
     * @param recommendations the recommendations
     */
    public SecurityIncident(String incidentId, LocalDateTime detectedAt, LocalDateTime firstOccurrence,
                           LocalDateTime lastOccurrence, String incidentType, String severity,
                           String description, String impact, String rootCause, String affectedUsers,
                           String affectedSystems, Map<String, Object> evidence, String status,
                           String assignedTo, String resolution, LocalDateTime resolvedAt, String recommendations) {
        this.incidentId = Objects.requireNonNull(incidentId, "Incident ID cannot be null");
        this.detectedAt = Objects.requireNonNull(detectedAt, "Detected at timestamp cannot be null");
        this.firstOccurrence = firstOccurrence;
        this.lastOccurrence = lastOccurrence;
        this.incidentType = Objects.requireNonNull(incidentType, "Incident type cannot be null");
        this.severity = Objects.requireNonNull(severity, "Severity cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.impact = impact;
        this.rootCause = rootCause;
        this.affectedUsers = affectedUsers;
        this.affectedSystems = affectedSystems;
        this.evidence = evidence;
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.assignedTo = assignedTo;
        this.resolution = resolution;
        this.resolvedAt = resolvedAt;
        this.recommendations = recommendations;
    }

    /**
     * Gets the unique incident identifier.
     *
     * @return the incident identifier
     */
    public String getIncidentId() {
        return incidentId;
    }

    /**
     * Gets the timestamp when the incident was detected.
     *
     * @return the detection timestamp
     */
    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    /**
     * Gets the timestamp of the first occurrence.
     *
     * @return the first occurrence timestamp
     */
    public LocalDateTime getFirstOccurrence() {
        return firstOccurrence;
    }

    /**
     * Gets the timestamp of the last occurrence.
     *
     * @return the last occurrence timestamp
     */
    public LocalDateTime getLastOccurrence() {
        return lastOccurrence;
    }

    /**
     * Gets the type of security incident.
     *
     * @return the incident type
     */
    public String getIncidentType() {
        return incidentType;
    }

    /**
     * Gets the severity level.
     *
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Gets the incident description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the impact assessment.
     *
     * @return the impact
     */
    public String getImpact() {
        return impact;
    }

    /**
     * Gets the root cause analysis.
     *
     * @return the root cause
     */
    public String getRootCause() {
        return rootCause;
    }

    /**
     * Gets the affected users.
     *
     * @return the affected users
     */
    public String getAffectedUsers() {
        return affectedUsers;
    }

    /**
     * Gets the affected systems.
     *
     * @return the affected systems
     */
    public String getAffectedSystems() {
        return affectedSystems;
    }

    /**
     * Gets the evidence collected.
     *
     * @return the evidence map
     */
    public Map<String, Object> getEvidence() {
        return evidence;
    }

    /**
     * Gets the incident status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the person assigned to handle the incident.
     *
     * @return the assigned person
     */
    public String getAssignedTo() {
        return assignedTo;
    }

    /**
     * Gets the resolution details.
     *
     * @return the resolution
     */
    public String getResolution() {
        return resolution;
    }

    /**
     * Gets the timestamp when the incident was resolved.
     *
     * @return the resolution timestamp
     */
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    /**
     * Gets the recommendations.
     *
     * @return the recommendations
     */
    public String getRecommendations() {
        return recommendations;
    }

    /**
     * Checks if the incident is resolved.
     *
     * @return true if resolved, false otherwise
     */
    public boolean isResolved() {
        return "RESOLVED".equals(status) || "CLOSED".equals(status);
    }

    /**
     * Checks if the incident is high severity.
     *
     * @return true if high severity, false otherwise
     */
    public boolean isHighSeverity() {
        return "HIGH".equals(severity) || "CRITICAL".equals(severity);
    }

    /**
     * Gets a summary of the security incident.
     *
     * @return the summary string
     */
    public String getSummary() {
        return String.format("SecurityIncident[%s] %s - %s - %s",
                incidentId, incidentType, severity, status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SecurityIncident that = (SecurityIncident) obj;
        return Objects.equals(incidentId, that.incidentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(incidentId);
    }

    @Override
    public String toString() {
        return String.format("SecurityIncident{incidentId='%s', detectedAt=%s, incidentType='%s', " +
                "severity='%s', status='%s', description='%s'}",
                incidentId, detectedAt, incidentType, severity, status, description);
    }
}
