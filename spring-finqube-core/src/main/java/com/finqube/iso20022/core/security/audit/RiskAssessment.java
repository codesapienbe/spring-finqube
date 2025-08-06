package com.finqube.iso20022.core.security.audit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Risk assessment generated from audit log analysis.
 *
 * <p>This class represents a comprehensive risk assessment that has been
 * generated through analysis of audit log data. It provides detailed
 * information about identified risks, their likelihood, impact, and
 * mitigation strategies.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class RiskAssessment {

    private final String assessmentId;
    private final LocalDateTime conductedAt;
    private final LocalDateTime assessmentPeriodStart;
    private final LocalDateTime assessmentPeriodEnd;
    private final String assessor;
    private final String methodology;
    private final String overallRiskLevel;
    private final String summary;
    private final List<Risk> identifiedRisks;
    private final Map<String, Object> riskMetrics;
    private final List<String> riskFactors;
    private final List<String> mitigationStrategies;
    private final String status;
    private final LocalDateTime nextReviewDate;
    private final String recommendations;

    /**
     * Constructs a new RiskAssessment instance.
     *
     * @param assessmentId the unique assessment identifier
     * @param conductedAt the timestamp when the assessment was conducted
     * @param assessmentPeriodStart the start of the assessment period
     * @param assessmentPeriodEnd the end of the assessment period
     * @param assessor the person who conducted the assessment
     * @param methodology the methodology used for the assessment
     * @param overallRiskLevel the overall risk level
     * @param summary the assessment summary
     * @param identifiedRisks the list of identified risks
     * @param riskMetrics the risk metrics
     * @param riskFactors the risk factors
     * @param mitigationStrategies the mitigation strategies
     * @param status the assessment status
     * @param nextReviewDate the next review date
     * @param recommendations the recommendations
     */
    public RiskAssessment(String assessmentId, LocalDateTime conductedAt, LocalDateTime assessmentPeriodStart,
                         LocalDateTime assessmentPeriodEnd, String assessor, String methodology,
                         String overallRiskLevel, String summary, List<Risk> identifiedRisks,
                         Map<String, Object> riskMetrics, List<String> riskFactors,
                         List<String> mitigationStrategies, String status, LocalDateTime nextReviewDate,
                         String recommendations) {
        this.assessmentId = Objects.requireNonNull(assessmentId, "Assessment ID cannot be null");
        this.conductedAt = Objects.requireNonNull(conductedAt, "Conducted at timestamp cannot be null");
        this.assessmentPeriodStart = Objects.requireNonNull(assessmentPeriodStart, "Assessment period start cannot be null");
        this.assessmentPeriodEnd = Objects.requireNonNull(assessmentPeriodEnd, "Assessment period end cannot be null");
        this.assessor = Objects.requireNonNull(assessor, "Assessor cannot be null");
        this.methodology = Objects.requireNonNull(methodology, "Methodology cannot be null");
        this.overallRiskLevel = Objects.requireNonNull(overallRiskLevel, "Overall risk level cannot be null");
        this.summary = Objects.requireNonNull(summary, "Summary cannot be null");
        this.identifiedRisks = Objects.requireNonNull(identifiedRisks, "Identified risks cannot be null");
        this.riskMetrics = Objects.requireNonNull(riskMetrics, "Risk metrics cannot be null");
        this.riskFactors = Objects.requireNonNull(riskFactors, "Risk factors cannot be null");
        this.mitigationStrategies = Objects.requireNonNull(mitigationStrategies, "Mitigation strategies cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.nextReviewDate = nextReviewDate;
        this.recommendations = recommendations;
    }

    /**
     * Gets the unique assessment identifier.
     *
     * @return the assessment identifier
     */
    public String getAssessmentId() {
        return assessmentId;
    }

    /**
     * Gets the timestamp when the assessment was conducted.
     *
     * @return the conduction timestamp
     */
    public LocalDateTime getConductedAt() {
        return conductedAt;
    }

    /**
     * Gets the start of the assessment period.
     *
     * @return the period start timestamp
     */
    public LocalDateTime getAssessmentPeriodStart() {
        return assessmentPeriodStart;
    }

    /**
     * Gets the end of the assessment period.
     *
     * @return the period end timestamp
     */
    public LocalDateTime getAssessmentPeriodEnd() {
        return assessmentPeriodEnd;
    }

    /**
     * Gets the person who conducted the assessment.
     *
     * @return the assessor
     */
    public String getAssessor() {
        return assessor;
    }

    /**
     * Gets the methodology used for the assessment.
     *
     * @return the methodology
     */
    public String getMethodology() {
        return methodology;
    }

    /**
     * Gets the overall risk level.
     *
     * @return the overall risk level
     */
    public String getOverallRiskLevel() {
        return overallRiskLevel;
    }

    /**
     * Gets the assessment summary.
     *
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Gets the list of identified risks.
     *
     * @return the identified risks
     */
    public List<Risk> getIdentifiedRisks() {
        return identifiedRisks;
    }

    /**
     * Gets the risk metrics.
     *
     * @return the risk metrics map
     */
    public Map<String, Object> getRiskMetrics() {
        return riskMetrics;
    }

    /**
     * Gets the risk factors.
     *
     * @return the risk factors list
     */
    public List<String> getRiskFactors() {
        return riskFactors;
    }

    /**
     * Gets the mitigation strategies.
     *
     * @return the mitigation strategies list
     */
    public List<String> getMitigationStrategies() {
        return mitigationStrategies;
    }

    /**
     * Gets the assessment status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the next review date.
     *
     * @return the next review date
     */
    public LocalDateTime getNextReviewDate() {
        return nextReviewDate;
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
     * Checks if the assessment indicates high risk.
     *
     * @return true if high risk, false otherwise
     */
    public boolean isHighRisk() {
        return "HIGH".equals(overallRiskLevel) || "CRITICAL".equals(overallRiskLevel);
    }

    /**
     * Gets the number of high-risk items.
     *
     * @return the count of high-risk items
     */
    public long getHighRiskCount() {
        return identifiedRisks.stream()
                .filter(risk -> "HIGH".equals(risk.getRiskLevel()) || "CRITICAL".equals(risk.getRiskLevel()))
                .count();
    }

    /**
     * Gets a summary of the risk assessment.
     *
     * @return the summary string
     */
    public String getAssessmentSummary() {
        return String.format("RiskAssessment[%s] %s - %s - %d risks - %d high-risk",
                assessmentId, overallRiskLevel, status, identifiedRisks.size(), getHighRiskCount());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RiskAssessment that = (RiskAssessment) obj;
        return Objects.equals(assessmentId, that.assessmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assessmentId);
    }

    @Override
    public String toString() {
        return String.format("RiskAssessment{assessmentId='%s', conductedAt=%s, overallRiskLevel='%s', " +
                "status='%s', identifiedRisks=%d, riskFactors=%d}",
                assessmentId, conductedAt, overallRiskLevel, status, identifiedRisks.size(), riskFactors.size());
    }

    /**
     * Inner class representing an individual risk.
     */
    public static class Risk {
        private final String riskId;
        private final String riskType;
        private final String description;
        private final String likelihood;
        private final String impact;
        private final String riskLevel;
        private final String mitigation;
        private final String owner;

        public Risk(String riskId, String riskType, String description, String likelihood,
                   String impact, String riskLevel, String mitigation, String owner) {
            this.riskId = Objects.requireNonNull(riskId, "Risk ID cannot be null");
            this.riskType = Objects.requireNonNull(riskType, "Risk type cannot be null");
            this.description = Objects.requireNonNull(description, "Description cannot be null");
            this.likelihood = Objects.requireNonNull(likelihood, "Likelihood cannot be null");
            this.impact = Objects.requireNonNull(impact, "Impact cannot be null");
            this.riskLevel = Objects.requireNonNull(riskLevel, "Risk level cannot be null");
            this.mitigation = mitigation;
            this.owner = owner;
        }

        public String getRiskId() {
            return riskId;
        }

        public String getRiskType() {
            return riskType;
        }

        public String getDescription() {
            return description;
        }

        public String getLikelihood() {
            return likelihood;
        }

        public String getImpact() {
            return impact;
        }

        public String getRiskLevel() {
            return riskLevel;
        }

        public String getMitigation() {
            return mitigation;
        }

        public String getOwner() {
            return owner;
        }

        public boolean isHighRisk() {
            return "HIGH".equals(riskLevel) || "CRITICAL".equals(riskLevel);
        }

        @Override
        public String toString() {
            return String.format("Risk{riskId='%s', riskType='%s', riskLevel='%s', description='%s'}",
                    riskId, riskType, riskLevel, description);
        }
    }
}
