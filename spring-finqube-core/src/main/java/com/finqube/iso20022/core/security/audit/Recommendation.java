package com.finqube.iso20022.core.security.audit;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Recommendation generated from audit log analysis.
 *
 * <p>This class represents a recommendation that has been generated
 * through analysis of audit log data. It provides detailed information
 * about the recommendation, its priority, and implementation guidance.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public class Recommendation {

    private final String recommendationId;
    private final LocalDateTime generatedAt;
    private final String category;
    private final String title;
    private final String description;
    private final String priority;
    private final String impact;
    private final String effort;
    private final String implementation;
    private final String responsibleParty;
    private final LocalDateTime targetDate;
    private final String status;
    private final LocalDateTime implementedAt;
    private final String rationale;
    private final Map<String, Object> context;

    /**
     * Constructs a new Recommendation instance.
     *
     * @param recommendationId the unique recommendation identifier
     * @param generatedAt the timestamp when the recommendation was generated
     * @param category the recommendation category
     * @param title the recommendation title
     * @param description the recommendation description
     * @param priority the priority level
     * @param impact the expected impact
     * @param effort the estimated effort
     * @param implementation the implementation guidance
     * @param responsibleParty the party responsible for implementation
     * @param targetDate the target implementation date
     * @param status the recommendation status
     * @param implementedAt the timestamp when the recommendation was implemented
     * @param rationale the rationale for the recommendation
     * @param context additional context information
     */
    public Recommendation(String recommendationId, LocalDateTime generatedAt, String category,
                         String title, String description, String priority, String impact,
                         String effort, String implementation, String responsibleParty,
                         LocalDateTime targetDate, String status, LocalDateTime implementedAt,
                         String rationale, Map<String, Object> context) {
        this.recommendationId = Objects.requireNonNull(recommendationId, "Recommendation ID cannot be null");
        this.generatedAt = Objects.requireNonNull(generatedAt, "Generated at timestamp cannot be null");
        this.category = Objects.requireNonNull(category, "Category cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.priority = Objects.requireNonNull(priority, "Priority cannot be null");
        this.impact = impact;
        this.effort = effort;
        this.implementation = implementation;
        this.responsibleParty = responsibleParty;
        this.targetDate = targetDate;
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.implementedAt = implementedAt;
        this.rationale = rationale;
        this.context = context;
    }

    /**
     * Gets the unique recommendation identifier.
     *
     * @return the recommendation identifier
     */
    public String getRecommendationId() {
        return recommendationId;
    }

    /**
     * Gets the timestamp when the recommendation was generated.
     *
     * @return the generation timestamp
     */
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    /**
     * Gets the recommendation category.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Gets the recommendation title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the recommendation description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the priority level.
     *
     * @return the priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Gets the expected impact.
     *
     * @return the impact
     */
    public String getImpact() {
        return impact;
    }

    /**
     * Gets the estimated effort.
     *
     * @return the effort
     */
    public String getEffort() {
        return effort;
    }

    /**
     * Gets the implementation guidance.
     *
     * @return the implementation
     */
    public String getImplementation() {
        return implementation;
    }

    /**
     * Gets the party responsible for implementation.
     *
     * @return the responsible party
     */
    public String getResponsibleParty() {
        return responsibleParty;
    }

    /**
     * Gets the target implementation date.
     *
     * @return the target date
     */
    public LocalDateTime getTargetDate() {
        return targetDate;
    }

    /**
     * Gets the recommendation status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the timestamp when the recommendation was implemented.
     *
     * @return the implementation timestamp
     */
    public LocalDateTime getImplementedAt() {
        return implementedAt;
    }

    /**
     * Gets the rationale for the recommendation.
     *
     * @return the rationale
     */
    public String getRationale() {
        return rationale;
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
     * Checks if the recommendation is implemented.
     *
     * @return true if implemented, false otherwise
     */
    public boolean isImplemented() {
        return "IMPLEMENTED".equals(status) || "COMPLETED".equals(status);
    }

    /**
     * Checks if the recommendation is high priority.
     *
     * @return true if high priority, false otherwise
     */
    public boolean isHighPriority() {
        return "HIGH".equals(priority) || "CRITICAL".equals(priority);
    }

    /**
     * Checks if the recommendation is overdue.
     *
     * @return true if overdue, false otherwise
     */
    public boolean isOverdue() {
        return targetDate != null && LocalDateTime.now().isAfter(targetDate) && !isImplemented();
    }

    /**
     * Gets a summary of the recommendation.
     *
     * @return the summary string
     */
    public String getSummary() {
        return String.format("Recommendation[%s] %s - %s - %s",
                recommendationId, category, priority, status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Recommendation that = (Recommendation) obj;
        return Objects.equals(recommendationId, that.recommendationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recommendationId);
    }

    @Override
    public String toString() {
        return String.format("Recommendation{recommendationId='%s', generatedAt=%s, category='%s', " +
                "title='%s', priority='%s', status='%s'}",
                recommendationId, generatedAt, category, title, priority, status);
    }
}
