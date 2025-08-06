package com.finqube.iso20022.core.security.audit;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Compliance violation identified from audit log analysis.
 *
 * <p>This class represents a compliance violation that has been identified
 * through analysis of audit log data. It provides detailed information
 * about the violation, its regulatory context, and required remediation.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class ComplianceViolation {

    private final String violationId;
    private final LocalDateTime detectedAt;
    private final LocalDateTime firstOccurrence;
    private final LocalDateTime lastOccurrence;
    private final String regulatoryFramework;
    private final String regulation;
    private final String requirement;
    private final String violationType;
    private final String severity;
    private final String description;
    private final String impact;
    private final String remediation;
    private final String responsibleParty;
    private final String status;
    private final LocalDateTime remediatedAt;
    private final String evidence;
    private final Map<String, Object> context;

    /**
     * Constructs a new ComplianceViolation instance.
     *
     * @param violationId the unique violation identifier
     * @param detectedAt the timestamp when the violation was detected
     * @param firstOccurrence the timestamp of the first occurrence
     * @param lastOccurrence the timestamp of the last occurrence
     * @param regulatoryFramework the regulatory framework
     * @param regulation the specific regulation
     * @param requirement the requirement that was violated
     * @param violationType the type of violation
     * @param severity the severity level
     * @param description the violation description
     * @param impact the impact assessment
     * @param remediation the required remediation
     * @param responsibleParty the party responsible for remediation
     * @param status the violation status
     * @param remediatedAt the timestamp when the violation was remediated
     * @param evidence the evidence of the violation
     * @param context additional context information
     */
    public ComplianceViolation(String violationId, LocalDateTime detectedAt, LocalDateTime firstOccurrence,
                              LocalDateTime lastOccurrence, String regulatoryFramework, String regulation,
                              String requirement, String violationType, String severity, String description,
                              String impact, String remediation, String responsibleParty, String status,
                              LocalDateTime remediatedAt, String evidence, Map<String, Object> context) {
        this.violationId = Objects.requireNonNull(violationId, "Violation ID cannot be null");
        this.detectedAt = Objects.requireNonNull(detectedAt, "Detected at timestamp cannot be null");
        this.firstOccurrence = firstOccurrence;
        this.lastOccurrence = lastOccurrence;
        this.regulatoryFramework = Objects.requireNonNull(regulatoryFramework, "Regulatory framework cannot be null");
        this.regulation = Objects.requireNonNull(regulation, "Regulation cannot be null");
        this.requirement = Objects.requireNonNull(requirement, "Requirement cannot be null");
        this.violationType = Objects.requireNonNull(violationType, "Violation type cannot be null");
        this.severity = Objects.requireNonNull(severity, "Severity cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.impact = impact;
        this.remediation = remediation;
        this.responsibleParty = responsibleParty;
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.remediatedAt = remediatedAt;
        this.evidence = evidence;
        this.context = context;
    }

    /**
     * Gets the unique violation identifier.
     *
     * @return the violation identifier
     */
    public String getViolationId() {
        return violationId;
    }

    /**
     * Gets the timestamp when the violation was detected.
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
     * Gets the regulatory framework.
     *
     * @return the regulatory framework
     */
    public String getRegulatoryFramework() {
        return regulatoryFramework;
    }

    /**
     * Gets the specific regulation.
     *
     * @return the regulation
     */
    public String getRegulation() {
        return regulation;
    }

    /**
     * Gets the requirement that was violated.
     *
     * @return the requirement
     */
    public String getRequirement() {
        return requirement;
    }

    /**
     * Gets the type of violation.
     *
     * @return the violation type
     */
    public String getViolationType() {
        return violationType;
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
     * Gets the violation description.
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
     * Gets the required remediation.
     *
     * @return the remediation
     */
    public String getRemediation() {
        return remediation;
    }

    /**
     * Gets the party responsible for remediation.
     *
     * @return the responsible party
     */
    public String getResponsibleParty() {
        return responsibleParty;
    }

    /**
     * Gets the violation status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the timestamp when the violation was remediated.
     *
     * @return the remediation timestamp
     */
    public LocalDateTime getRemediatedAt() {
        return remediatedAt;
    }

    /**
     * Gets the evidence of the violation.
     *
     * @return the evidence
     */
    public String getEvidence() {
        return evidence;
    }

    /**
     * Gets additional context information.
     *
     * @return the context map
     */
    public Map<String, Object> getContext() {
        return context;
    }

    /**
     * Checks if the violation is remediated.
     *
     * @return true if remediated, false otherwise
     */
    public boolean isRemediated() {
        return "REMEDIATED".equals(status) || "CLOSED".equals(status);
    }

    /**
     * Checks if the violation is high severity.
     *
     * @return true if high severity, false otherwise
     */
    public boolean isHighSeverity() {
        return "HIGH".equals(severity) || "CRITICAL".equals(severity);
    }

    /**
     * Gets a summary of the compliance violation.
     *
     * @return the summary string
     */
    public String getSummary() {
        return String.format("ComplianceViolation[%s] %s - %s - %s",
                violationId, regulatoryFramework, regulation, status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ComplianceViolation that = (ComplianceViolation) obj;
        return Objects.equals(violationId, that.violationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(violationId);
    }

    @Override
    public String toString() {
        return String.format("ComplianceViolation{violationId='%s', detectedAt=%s, regulatoryFramework='%s', " +
                "regulation='%s', violationType='%s', severity='%s', status='%s'}",
                violationId, detectedAt, regulatoryFramework, regulation, violationType, severity, status);
    }
}
