package com.finqube.iso20022.core.security.certificate;

import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Certificate manager interface for certificate chain validation and management.
 *
 * <p>This interface provides comprehensive certificate lifecycle management including
 * certificate validation, chain verification, revocation checking, and certificate
 * storage operations. It ensures proper certificate handling for secure communications.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Certificate chain validation and verification</li>
 *   <li>Certificate revocation checking (CRL and OCSP)</li>
 *   <li>Certificate storage and retrieval</li>
 *   <li>Certificate expiration monitoring</li>
 *   <li>Trust store management</li>
 *   <li>Certificate path building and validation</li>
 * </ul>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public interface CertificateManager {

    /**
     * Certificate validation status enumeration.
     */
    enum ValidationStatus {
        /** Certificate is valid */
        VALID("VALID"),
        /** Certificate is invalid */
        INVALID("INVAL"),
        /** Certificate is expired */
        EXPIRED("EXPR"),
        /** Certificate is revoked */
        REVOKED("REVK"),
        /** Certificate is not yet valid */
        NOT_YET_VALID("NYV"),
        /** Certificate validation failed */
        VALIDATION_FAILED("FAIL"),
        /** Certificate validation is in progress */
        VALIDATING("VALG"),
        /** Certificate validation is unknown */
        UNKNOWN("UNKN");

        private final String code;

        ValidationStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Certificate type enumeration.
     */
    enum CertificateType {
        /** Root CA certificate */
        ROOT_CA("ROOT"),
        /** Intermediate CA certificate */
        INTERMEDIATE_CA("INT"),
        /** End entity certificate */
        END_ENTITY("END"),
        /** Self-signed certificate */
        SELF_SIGNED("SELF"),
        /** Cross-certificate */
        CROSS_CERT("CROSS");

        private final String code;

        CertificateType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Certificate usage enumeration.
     */
    enum CertificateUsage {
        /** Digital signature */
        DIGITAL_SIGNATURE("SIGN"),
        /** Key encipherment */
        KEY_ENCIPHERMENT("ENC"),
        /** Data encipherment */
        DATA_ENCIPHERMENT("DATA"),
        /** Key agreement */
        KEY_AGREEMENT("AGRE"),
        /** Certificate signing */
        CERTIFICATE_SIGNING("CERT"),
        /** CRL signing */
        CRL_SIGNING("CRL"),
        /** Server authentication */
        SERVER_AUTH("SRV"),
        /** Client authentication */
        CLIENT_AUTH("CLI"),
        /** Code signing */
        CODE_SIGNING("CODE"),
        /** Email protection */
        EMAIL_PROTECTION("EMAIL");

        private final String code;

        CertificateUsage(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Validates a single certificate.
     *
     * @param certificate the certificate to validate
     * @return the validation result
     * @throws CertificateException if validation fails
     */
    CertificateValidationResult validateCertificate(X509Certificate certificate) throws CertificateException;

    /**
     * Validates a certificate chain.
     *
     * @param certificateChain the certificate chain to validate
     * @return the validation result
     * @throws CertificateException if validation fails
     */
    CertificateValidationResult validateCertificateChain(List<X509Certificate> certificateChain) throws CertificateException;

    /**
     * Builds and validates a certificate path.
     *
     * @param targetCertificate the target certificate
     * @param trustAnchors the trust anchors to use
     * @return the validation result
     * @throws CertificateException if path building or validation fails
     */
    CertificateValidationResult buildAndValidatePath(X509Certificate targetCertificate,
                                                    List<X509Certificate> trustAnchors) throws CertificateException;

    /**
     * Checks certificate revocation status using CRL.
     *
     * @param certificate the certificate to check
     * @return the revocation status
     * @throws CertificateException if revocation checking fails
     */
    RevocationStatus checkRevocationStatus(X509Certificate certificate) throws CertificateException;

    /**
     * Checks certificate revocation status using OCSP.
     *
     * @param certificate the certificate to check
     * @param responderUrl the OCSP responder URL
     * @return the revocation status
     * @throws CertificateException if revocation checking fails
     */
    RevocationStatus checkOcspStatus(X509Certificate certificate, String responderUrl) throws CertificateException;

    /**
     * Stores a certificate in the certificate store.
     *
     * @param certificate the certificate to store
     * @param alias the alias for the certificate
     * @param certificateType the type of certificate
     * @return the stored certificate identifier
     * @throws CertificateException if storage fails
     */
    String storeCertificate(X509Certificate certificate, String alias, CertificateType certificateType) throws CertificateException;

    /**
     * Retrieves a certificate from the certificate store.
     *
     * @param certificateId the certificate identifier
     * @return the certificate, or empty if not found
     * @throws CertificateException if retrieval fails
     */
    Optional<X509Certificate> getCertificate(String certificateId) throws CertificateException;

    /**
     * Lists all certificates in the certificate store.
     *
     * @return list of certificate identifiers
     * @throws CertificateException if listing fails
     */
    List<String> listCertificates() throws CertificateException;

    /**
     * Gets certificate information.
     *
     * @param certificateId the certificate identifier
     * @return the certificate information, or empty if not found
     * @throws CertificateException if information retrieval fails
     */
    Optional<CertificateInfo> getCertificateInfo(String certificateId) throws CertificateException;

    /**
     * Deletes a certificate from the certificate store.
     *
     * @param certificateId the certificate identifier
     * @return true if deletion was successful, false otherwise
     * @throws CertificateException if deletion fails
     */
    boolean deleteCertificate(String certificateId) throws CertificateException;

    /**
     * Imports a certificate chain.
     *
     * @param certificateChain the certificate chain to import
     * @param alias the alias for the certificate chain
     * @return the imported certificate chain identifier
     * @throws CertificateException if import fails
     */
    String importCertificateChain(List<X509Certificate> certificateChain, String alias) throws CertificateException;

    /**
     * Exports a certificate chain.
     *
     * @param certificateChainId the certificate chain identifier
     * @return the exported certificate chain
     * @throws CertificateException if export fails
     */
    List<X509Certificate> exportCertificateChain(String certificateChainId) throws CertificateException;

    /**
     * Adds a certificate to the trust store.
     *
     * @param certificate the certificate to add
     * @param alias the alias for the certificate
     * @return true if addition was successful, false otherwise
     * @throws CertificateException if addition fails
     */
    boolean addToTrustStore(X509Certificate certificate, String alias) throws CertificateException;

    /**
     * Removes a certificate from the trust store.
     *
     * @param alias the alias of the certificate to remove
     * @return true if removal was successful, false otherwise
     * @throws CertificateException if removal fails
     */
    boolean removeFromTrustStore(String alias) throws CertificateException;

    /**
     * Lists all certificates in the trust store.
     *
     * @return list of trust store certificate aliases
     * @throws CertificateException if listing fails
     */
    List<String> listTrustStoreCertificates() throws CertificateException;

    /**
     * Checks if a certificate is trusted.
     *
     * @param certificate the certificate to check
     * @return true if the certificate is trusted, false otherwise
     * @throws CertificateException if trust checking fails
     */
    boolean isTrusted(X509Certificate certificate) throws CertificateException;

    /**
     * Gets certificates that are expiring soon.
     *
     * @param days the number of days to check
     * @return list of certificates expiring within the specified days
     * @throws CertificateException if retrieval fails
     */
    List<CertificateInfo> getExpiringCertificates(int days) throws CertificateException;

    /**
     * Gets expired certificates.
     *
     * @return list of expired certificates
     * @throws CertificateException if retrieval fails
     */
    List<CertificateInfo> getExpiredCertificates() throws CertificateException;

    /**
     * Gets certificate statistics.
     *
     * @return certificate statistics
     * @throws CertificateException if statistics retrieval fails
     */
    CertificateStatistics getStatistics() throws CertificateException;

    /**
     * Performs a health check on the certificate manager.
     *
     * @return true if the certificate manager is healthy, false otherwise
     * @throws CertificateException if health check fails
     */
    boolean isHealthy() throws CertificateException;
}
