package com.finqube.iso20022.core.security.certificate;

import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Certificate information and metadata.
 *
 * <p>This class provides comprehensive metadata about a certificate including
 * subject, issuer, validity dates, key information, and usage details.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class CertificateInfo {

    private final String certificateId;
    private final String alias;
    private final X509Certificate certificate;
    private final CertificateManager.CertificateType certificateType;
    private final List<CertificateManager.CertificateUsage> usages;
    private final String subject;
    private final String issuer;
    private final LocalDateTime notBefore;
    private final LocalDateTime notAfter;
    private final String serialNumber;
    private final String signatureAlgorithm;
    private final int keySize;
    private final String keyAlgorithm;
    private final LocalDateTime importDate;
    private final boolean trusted;
    private final String description;

    /**
     * Constructs a new certificate info instance.
     *
     * @param certificateId the certificate identifier
     * @param alias the certificate alias
     * @param certificate the certificate
     * @param certificateType the certificate type
     * @param usages the certificate usages
     * @param subject the subject
     * @param issuer the issuer
     * @param notBefore the not before date
     * @param notAfter the not after date
     * @param serialNumber the serial number
     * @param signatureAlgorithm the signature algorithm
     * @param keySize the key size
     * @param keyAlgorithm the key algorithm
     * @param importDate the import date
     * @param trusted whether the certificate is trusted
     * @param description the description
     */
    public CertificateInfo(String certificateId, String alias, X509Certificate certificate,
                          CertificateManager.CertificateType certificateType, List<CertificateManager.CertificateUsage> usages,
                          String subject, String issuer, LocalDateTime notBefore, LocalDateTime notAfter,
                          String serialNumber, String signatureAlgorithm, int keySize, String keyAlgorithm,
                          LocalDateTime importDate, boolean trusted, String description) {
        this.certificateId = certificateId;
        this.alias = alias;
        this.certificate = certificate;
        this.certificateType = certificateType;
        this.usages = usages;
        this.subject = subject;
        this.issuer = issuer;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
        this.serialNumber = serialNumber;
        this.signatureAlgorithm = signatureAlgorithm;
        this.keySize = keySize;
        this.keyAlgorithm = keyAlgorithm;
        this.importDate = importDate;
        this.trusted = trusted;
        this.description = description;
    }

    /**
     * Gets the certificate identifier.
     *
     * @return the certificate ID
     */
    public String getCertificateId() {
        return certificateId;
    }

    /**
     * Gets the certificate alias.
     *
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Gets the certificate.
     *
     * @return the certificate
     */
    public X509Certificate getCertificate() {
        return certificate;
    }

    /**
     * Gets the certificate type.
     *
     * @return the certificate type
     */
    public CertificateManager.CertificateType getCertificateType() {
        return certificateType;
    }

    /**
     * Gets the certificate usages.
     *
     * @return the usages
     */
    public List<CertificateManager.CertificateUsage> getUsages() {
        return usages;
    }

    /**
     * Gets the subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the issuer.
     *
     * @return the issuer
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Gets the not before date.
     *
     * @return the not before date
     */
    public LocalDateTime getNotBefore() {
        return notBefore;
    }

    /**
     * Gets the not after date.
     *
     * @return the not after date
     */
    public LocalDateTime getNotAfter() {
        return notAfter;
    }

    /**
     * Gets the serial number.
     *
     * @return the serial number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Gets the signature algorithm.
     *
     * @return the signature algorithm
     */
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    /**
     * Gets the key size.
     *
     * @return the key size
     */
    public int getKeySize() {
        return keySize;
    }

    /**
     * Gets the key algorithm.
     *
     * @return the key algorithm
     */
    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    /**
     * Gets the import date.
     *
     * @return the import date
     */
    public LocalDateTime getImportDate() {
        return importDate;
    }

    /**
     * Checks if the certificate is trusted.
     *
     * @return true if the certificate is trusted, false otherwise
     */
    public boolean isTrusted() {
        return trusted;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if the certificate has expired.
     *
     * @return true if the certificate has expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(notAfter);
    }

    /**
     * Checks if the certificate is not yet valid.
     *
     * @return true if the certificate is not yet valid, false otherwise
     */
    public boolean isNotYetValid() {
        return LocalDateTime.now().isBefore(notBefore);
    }

    /**
     * Checks if the certificate is currently valid.
     *
     * @return true if the certificate is currently valid, false otherwise
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(notBefore) && now.isBefore(notAfter);
    }

    /**
     * Gets the remaining validity period in days.
     *
     * @return the number of days until expiration, or -1 if already expired
     */
    public long getRemainingValidityDays() {
        if (isExpired()) {
            return -1;
        }
        return java.time.Duration.between(LocalDateTime.now(), notAfter).toDays();
    }

    /**
     * Checks if the certificate will expire soon (within the specified number of days).
     *
     * @param days the number of days to check
     * @return true if the certificate will expire within the specified days, false otherwise
     */
    public boolean isExpiringSoon(int days) {
        if (isExpired()) {
            return false;
        }
        LocalDateTime warningDate = LocalDateTime.now().plusDays(days);
        return notAfter.isBefore(warningDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CertificateInfo that = (CertificateInfo) obj;
        return Objects.equals(certificateId, that.certificateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateId);
    }

    @Override
    public String toString() {
        return String.format("CertificateInfo{certificateId='%s', alias='%s', subject='%s', issuer='%s', " +
                           "notBefore=%s, notAfter=%s, certificateType=%s, trusted=%s}",
                           certificateId, alias, subject, issuer, notBefore, notAfter, certificateType, trusted);
    }
}
