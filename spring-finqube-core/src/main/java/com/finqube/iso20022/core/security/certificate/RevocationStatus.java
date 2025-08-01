package com.finqube.iso20022.core.security.certificate;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Certificate revocation status information.
 *
 * <p>This class provides detailed information about the revocation status of a
 * certificate, including whether it's revoked, the revocation reason, and
 * when the revocation occurred.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class RevocationStatus {

    /**
     * Revocation status enumeration.
     */
    public enum Status {
        /** Certificate is not revoked */
        NOT_REVOKED("GOOD"),
        /** Certificate is revoked */
        REVOKED("REVOKED"),
        /** Revocation status is unknown */
        UNKNOWN("UNKNOWN"),
        /** Revocation check failed */
        CHECK_FAILED("FAILED");

        private final String code;

        Status(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Revocation reason enumeration.
     */
    public enum RevocationReason {
        /** No reason specified */
        UNSPECIFIED(0),
        /** Key compromise */
        KEY_COMPROMISE(1),
        /** CA compromise */
        CA_COMPROMISE(2),
        /** Affiliation changed */
        AFFILIATION_CHANGED(3),
        /** Superseded */
        SUPERSEDED(4),
        /** Cessation of operation */
        CESSATION_OF_OPERATION(5),
        /** Certificate hold */
        CERTIFICATE_HOLD(6),
        /** Remove from CRL */
        REMOVE_FROM_CRL(8),
        /** Privilege withdrawn */
        PRIVILEGE_WITHDRAWN(9),
        /** AA compromise */
        AA_COMPROMISE(10);

        private final int code;

        RevocationReason(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    private final Status status;
    private final RevocationReason reason;
    private final LocalDateTime revocationDate;
    private final LocalDateTime thisUpdate;
    private final LocalDateTime nextUpdate;
    private final String responderUrl;
    private final String errorMessage;

    /**
     * Constructs a new revocation status.
     *
     * @param status the revocation status
     * @param reason the revocation reason, or null if not revoked
     * @param revocationDate the revocation date, or null if not revoked
     * @param thisUpdate the this update time
     * @param nextUpdate the next update time
     * @param responderUrl the responder URL
     * @param errorMessage the error message, or null if no error
     */
    public RevocationStatus(Status status, RevocationReason reason, LocalDateTime revocationDate,
                          LocalDateTime thisUpdate, LocalDateTime nextUpdate, String responderUrl, String errorMessage) {
        this.status = status;
        this.reason = reason;
        this.revocationDate = revocationDate;
        this.thisUpdate = thisUpdate;
        this.nextUpdate = nextUpdate;
        this.responderUrl = responderUrl;
        this.errorMessage = errorMessage;
    }

    /**
     * Creates a not revoked status.
     *
     * @param thisUpdate the this update time
     * @param nextUpdate the next update time
     * @param responderUrl the responder URL
     * @return a not revoked status
     */
    public static RevocationStatus notRevoked(LocalDateTime thisUpdate, LocalDateTime nextUpdate, String responderUrl) {
        return new RevocationStatus(Status.NOT_REVOKED, null, null, thisUpdate, nextUpdate, responderUrl, null);
    }

    /**
     * Creates a revoked status.
     *
     * @param reason the revocation reason
     * @param revocationDate the revocation date
     * @param thisUpdate the this update time
     * @param nextUpdate the next update time
     * @param responderUrl the responder URL
     * @return a revoked status
     */
    public static RevocationStatus revoked(RevocationReason reason, LocalDateTime revocationDate,
                                         LocalDateTime thisUpdate, LocalDateTime nextUpdate, String responderUrl) {
        return new RevocationStatus(Status.REVOKED, reason, revocationDate, thisUpdate, nextUpdate, responderUrl, null);
    }

    /**
     * Creates an unknown status.
     *
     * @param thisUpdate the this update time
     * @param nextUpdate the next update time
     * @param responderUrl the responder URL
     * @return an unknown status
     */
    public static RevocationStatus unknown(LocalDateTime thisUpdate, LocalDateTime nextUpdate, String responderUrl) {
        return new RevocationStatus(Status.UNKNOWN, null, null, thisUpdate, nextUpdate, responderUrl, null);
    }

    /**
     * Creates a check failed status.
     *
     * @param errorMessage the error message
     * @param responderUrl the responder URL
     * @return a check failed status
     */
    public static RevocationStatus checkFailed(String errorMessage, String responderUrl) {
        return new RevocationStatus(Status.CHECK_FAILED, null, null, null, null, responderUrl, errorMessage);
    }

    /**
     * Gets the revocation status.
     *
     * @return the revocation status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Gets the revocation reason.
     *
     * @return the revocation reason, or null if not revoked
     */
    public RevocationReason getReason() {
        return reason;
    }

    /**
     * Gets the revocation date.
     *
     * @return the revocation date, or null if not revoked
     */
    public LocalDateTime getRevocationDate() {
        return revocationDate;
    }

    /**
     * Gets the this update time.
     *
     * @return the this update time
     */
    public LocalDateTime getThisUpdate() {
        return thisUpdate;
    }

    /**
     * Gets the next update time.
     *
     * @return the next update time
     */
    public LocalDateTime getNextUpdate() {
        return nextUpdate;
    }

    /**
     * Gets the responder URL.
     *
     * @return the responder URL
     */
    public String getResponderUrl() {
        return responderUrl;
    }

    /**
     * Gets the error message.
     *
     * @return the error message, or null if no error
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Checks if the certificate is revoked.
     *
     * @return true if the certificate is revoked, false otherwise
     */
    public boolean isRevoked() {
        return Status.REVOKED.equals(status);
    }

    /**
     * Checks if the certificate is not revoked.
     *
     * @return true if the certificate is not revoked, false otherwise
     */
    public boolean isNotRevoked() {
        return Status.NOT_REVOKED.equals(status);
    }

    /**
     * Checks if the revocation status is unknown.
     *
     * @return true if the revocation status is unknown, false otherwise
     */
    public boolean isUnknown() {
        return Status.UNKNOWN.equals(status);
    }

    /**
     * Checks if the revocation check failed.
     *
     * @return true if the revocation check failed, false otherwise
     */
    public boolean isCheckFailed() {
        return Status.CHECK_FAILED.equals(status);
    }

    /**
     * Gets a summary of the revocation status.
     *
     * @return a summary string
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Revocation status: ").append(status);
        if (isRevoked() && reason != null) {
            summary.append(" (reason: ").append(reason).append(")");
        }
        if (revocationDate != null) {
            summary.append(" (revoked on: ").append(revocationDate).append(")");
        }
        if (errorMessage != null) {
            summary.append(" (error: ").append(errorMessage).append(")");
        }
        return summary.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RevocationStatus that = (RevocationStatus) obj;
        return status == that.status && reason == that.reason && Objects.equals(revocationDate, that.revocationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, reason, revocationDate);
    }

    @Override
    public String toString() {
        return String.format("RevocationStatus{status=%s, reason=%s, revocationDate=%s, thisUpdate=%s, nextUpdate=%s}",
                           status, reason, revocationDate, thisUpdate, nextUpdate);
    }
}
