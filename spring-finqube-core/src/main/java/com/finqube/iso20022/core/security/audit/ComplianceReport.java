package com.finqube.iso20022.core.security.audit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Compliance report generated from audit log data.
 *
 * <p>This class represents a structured compliance report that can be generated
 * from audit log data to meet various regulatory and compliance requirements.
 * It provides comprehensive analysis of security events, user activities,
 * and system access patterns.</p>
 *
 * <p>The compliance report includes:</p>
 * <ul>
 *   <li>Executive summary and key findings</li>
 *   <li>Detailed audit log analysis</li>
 *   <li>Security event categorization</li>
 *   <li>Risk assessment and recommendations</li>
 *   <li>Regulatory compliance mapping</li>
 * </ul>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class ComplianceReport {

    private final String reportId;
    private final LocalDateTime generatedAt;
    private final LocalDateTime reportPeriodStart;
    private final LocalDateTime reportPeriodEnd;
    private final String reportType;
    private final String regulatoryFramework;
    private final String complianceCategory;
    private final String executiveSummary;
    private final List<String> keyFindings;
    private final Map<String, Object> statistics;
    private final List<AuditLogEntry> relevantAuditLogs;
    private final List<SecurityIncident> securityIncidents;
    private final List<ComplianceViolation> complianceViolations;
    private final List<Recommendation> recommendations;
    private final RiskAssessment riskAssessment;
    private final String generatedBy;
    private final String reportFormat;

    /**
     * Constructs a new ComplianceReport instance.
     *
     * @param reportId the unique report identifier
     * @param generatedAt the timestamp when the report was generated
     * @param reportPeriodStart the start of the reporting period
     * @param reportPeriodEnd the end of the reporting period
     * @param reportType the type of compliance report
     * @param regulatoryFramework the regulatory framework this report addresses
     * @param complianceCategory the compliance category
     * @param executiveSummary the executive summary
     * @param keyFindings the key findings
     * @param statistics the statistical data
     * @param relevantAuditLogs the relevant audit log entries
     * @param securityIncidents the security incidents identified
     * @param complianceViolations the compliance violations found
     * @param recommendations the recommendations
     * @param riskAssessment the risk assessment
     * @param generatedBy the user who generated the report
     * @param reportFormat the format of the report
     */
    public ComplianceReport(String reportId, LocalDateTime generatedAt, LocalDateTime reportPeriodStart,
                           LocalDateTime reportPeriodEnd, String reportType, String regulatoryFramework,
                           String complianceCategory, String executiveSummary, List<String> keyFindings,
                           Map<String, Object> statistics, List<AuditLogEntry> relevantAuditLogs,
                           List<SecurityIncident> securityIncidents, List<ComplianceViolation> complianceViolations,
                           List<Recommendation> recommendations, RiskAssessment riskAssessment,
                           String generatedBy, String reportFormat) {
        this.reportId = Objects.requireNonNull(reportId, "Report ID cannot be null");
        this.generatedAt = Objects.requireNonNull(generatedAt, "Generated at timestamp cannot be null");
        this.reportPeriodStart = Objects.requireNonNull(reportPeriodStart, "Report period start cannot be null");
        this.reportPeriodEnd = Objects.requireNonNull(reportPeriodEnd, "Report period end cannot be null");
        this.reportType = Objects.requireNonNull(reportType, "Report type cannot be null");
        this.regulatoryFramework = regulatoryFramework;
        this.complianceCategory = complianceCategory;
        this.executiveSummary = Objects.requireNonNull(executiveSummary, "Executive summary cannot be null");
        this.keyFindings = Objects.requireNonNull(keyFindings, "Key findings cannot be null");
        this.statistics = Objects.requireNonNull(statistics, "Statistics cannot be null");
        this.relevantAuditLogs = Objects.requireNonNull(relevantAuditLogs, "Relevant audit logs cannot be null");
        this.securityIncidents = Objects.requireNonNull(securityIncidents, "Security incidents cannot be null");
        this.complianceViolations = Objects.requireNonNull(complianceViolations, "Compliance violations cannot be null");
        this.recommendations = Objects.requireNonNull(recommendations, "Recommendations cannot be null");
        this.riskAssessment = Objects.requireNonNull(riskAssessment, "Risk assessment cannot be null");
        this.generatedBy = generatedBy;
        this.reportFormat = reportFormat;
    }

    /**
     * Gets the unique report identifier.
     *
     * @return the report identifier
     */
    public String getReportId() {
        return reportId;
    }

    /**
     * Gets the timestamp when the report was generated.
     *
     * @return the generation timestamp
     */
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    /**
     * Gets the start of the reporting period.
     *
     * @return the period start timestamp
     */
    public LocalDateTime getReportPeriodStart() {
        return reportPeriodStart;
    }

    /**
     * Gets the end of the reporting period.
     *
     * @return the period end timestamp
     */
    public LocalDateTime getReportPeriodEnd() {
        return reportPeriodEnd;
    }

    /**
     * Gets the type of compliance report.
     *
     * @return the report type
     */
    public String getReportType() {
        return reportType;
    }

    /**
     * Gets the regulatory framework this report addresses.
     *
     * @return the regulatory framework
     */
    public String getRegulatoryFramework() {
        return regulatoryFramework;
    }

    /**
     * Gets the compliance category.
     *
     * @return the compliance category
     */
    public String getComplianceCategory() {
        return complianceCategory;
    }

    /**
     * Gets the executive summary.
     *
     * @return the executive summary
     */
    public String getExecutiveSummary() {
        return executiveSummary;
    }

    /**
     * Gets the key findings.
     *
     * @return the key findings list
     */
    public List<String> getKeyFindings() {
        return keyFindings;
    }

    /**
     * Gets the statistical data.
     *
     * @return the statistics map
     */
    public Map<String, Object> getStatistics() {
        return statistics;
    }

    /**
     * Gets the relevant audit log entries.
     *
     * @return the relevant audit logs
     */
    public List<AuditLogEntry> getRelevantAuditLogs() {
        return relevantAuditLogs;
    }

    /**
     * Gets the security incidents identified.
     *
     * @return the security incidents
     */
    public List<SecurityIncident> getSecurityIncidents() {
        return securityIncidents;
    }

    /**
     * Gets the compliance violations found.
     *
     * @return the compliance violations
     */
    public List<ComplianceViolation> getComplianceViolations() {
        return complianceViolations;
    }

    /**
     * Gets the recommendations.
     *
     * @return the recommendations
     */
    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    /**
     * Gets the risk assessment.
     *
     * @return the risk assessment
     */
    public RiskAssessment getRiskAssessment() {
        return riskAssessment;
    }

    /**
     * Gets the user who generated the report.
     *
     * @return the generated by user
     */
    public String getGeneratedBy() {
        return generatedBy;
    }

    /**
     * Gets the format of the report.
     *
     * @return the report format
     */
    public String getReportFormat() {
        return reportFormat;
    }

    /**
     * Gets a summary of the compliance report.
     *
     * @return the summary string
     */
    public String getSummary() {
        return String.format("ComplianceReport[%s] %s - %s - %d findings - %d incidents",
                reportId, reportType, regulatoryFramework, keyFindings.size(), securityIncidents.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ComplianceReport that = (ComplianceReport) obj;
        return Objects.equals(reportId, that.reportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId);
    }

    @Override
    public String toString() {
        return String.format("ComplianceReport{reportId='%s', generatedAt=%s, reportType='%s', " +
                "regulatoryFramework='%s', keyFindings=%d, securityIncidents=%d, complianceViolations=%d}",
                reportId, generatedAt, reportType, regulatoryFramework, keyFindings.size(),
                securityIncidents.size(), complianceViolations.size());
    }

    /**
     * Creates a new builder for ComplianceReport.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for ComplianceReport.
     */
    public static class Builder {
        private String reportId;
        private LocalDateTime generatedAt = LocalDateTime.now();
        private LocalDateTime reportPeriodStart;
        private LocalDateTime reportPeriodEnd;
        private String reportType;
        private String regulatoryFramework;
        private String complianceCategory;
        private String executiveSummary = "Compliance report generated";
        private List<String> keyFindings = List.of();
        private Map<String, Object> statistics = Map.of();
        private List<AuditLogEntry> relevantAuditLogs = List.of();
        private List<SecurityIncident> securityIncidents = List.of();
        private List<ComplianceViolation> complianceViolations = List.of();
        private List<Recommendation> recommendations = List.of();
        private RiskAssessment riskAssessment;
        private String generatedBy;
        private String reportFormat = "JSON";

        public Builder reportId(String reportId) {
            this.reportId = reportId;
            return this;
        }

        public Builder generatedAt(LocalDateTime generatedAt) {
            this.generatedAt = generatedAt;
            return this;
        }

        public Builder reportPeriodStart(LocalDateTime reportPeriodStart) {
            this.reportPeriodStart = reportPeriodStart;
            return this;
        }

        public Builder reportPeriodEnd(LocalDateTime reportPeriodEnd) {
            this.reportPeriodEnd = reportPeriodEnd;
            return this;
        }

        public Builder reportType(String reportType) {
            this.reportType = reportType;
            return this;
        }

        public Builder regulatoryFramework(String regulatoryFramework) {
            this.regulatoryFramework = regulatoryFramework;
            return this;
        }

        public Builder complianceCategory(String complianceCategory) {
            this.complianceCategory = complianceCategory;
            return this;
        }

        public Builder executiveSummary(String executiveSummary) {
            this.executiveSummary = executiveSummary;
            return this;
        }

        public Builder keyFindings(List<String> keyFindings) {
            this.keyFindings = keyFindings;
            return this;
        }

        public Builder statistics(Map<String, Object> statistics) {
            this.statistics = statistics;
            return this;
        }

        public Builder relevantAuditLogs(List<AuditLogEntry> relevantAuditLogs) {
            this.relevantAuditLogs = relevantAuditLogs;
            return this;
        }

        public Builder securityIncidents(List<SecurityIncident> securityIncidents) {
            this.securityIncidents = securityIncidents;
            return this;
        }

        public Builder complianceViolations(List<ComplianceViolation> complianceViolations) {
            this.complianceViolations = complianceViolations;
            return this;
        }

        public Builder recommendations(List<Recommendation> recommendations) {
            this.recommendations = recommendations;
            return this;
        }

        public Builder riskAssessment(RiskAssessment riskAssessment) {
            this.riskAssessment = riskAssessment;
            return this;
        }

        public Builder generatedBy(String generatedBy) {
            this.generatedBy = generatedBy;
            return this;
        }

        public Builder reportFormat(String reportFormat) {
            this.reportFormat = reportFormat;
            return this;
        }

        public ComplianceReport build() {
            return new ComplianceReport(reportId, generatedAt, reportPeriodStart, reportPeriodEnd,
                    reportType, regulatoryFramework, complianceCategory, executiveSummary, keyFindings,
                    statistics, relevantAuditLogs, securityIncidents, complianceViolations,
                    recommendations, riskAssessment, generatedBy, reportFormat);
        }
    }
}
