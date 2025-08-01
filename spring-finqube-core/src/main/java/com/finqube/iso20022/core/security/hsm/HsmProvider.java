package com.finqube.iso20022.core.security.hsm;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;

/**
 * Hardware Security Module (HSM) provider interface for secure cryptographic operations.
 *
 * <p>This interface defines the contract for HSM integration, providing secure key management,
 * digital signing, encryption, and certificate operations. HSM providers ensure that sensitive
 * cryptographic operations are performed in a secure, tamper-resistant environment.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Secure key generation and storage</li>
 *   <li>Digital signature operations</li>
 *   <li>Message encryption and decryption</li>
 *   <li>Certificate management and validation</li>
 *   <li>Key rotation and lifecycle management</li>
 *   <li>Audit logging and compliance</li>
 * </ul>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public interface HsmProvider {

    /**
     * HSM connection status enumeration.
     */
    enum ConnectionStatus {
        /** HSM is connected and operational */
        CONNECTED("CONN"),
        /** HSM is disconnected */
        DISCONNECTED("DISC"),
        /** HSM connection is in progress */
        CONNECTING("CONG"),
        /** HSM connection failed */
        FAILED("FAIL"),
        /** HSM is in maintenance mode */
        MAINTENANCE("MAIN");

        private final String code;

        ConnectionStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * HSM operation status enumeration.
     */
    enum OperationStatus {
        /** Operation completed successfully */
        SUCCESS("SUCC"),
        /** Operation failed */
        FAILED("FAIL"),
        /** Operation is in progress */
        IN_PROGRESS("PROG"),
        /** Operation was cancelled */
        CANCELLED("CANC"),
        /** Operation timed out */
        TIMEOUT("TIME");

        private final String code;

        OperationStatus(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * HSM key type enumeration.
     */
    enum KeyType {
        /** RSA key pair */
        RSA("RSA"),
        /** Elliptic Curve key pair */
        EC("EC"),
        /** AES symmetric key */
        AES("AES"),
        /** Triple DES symmetric key */
        DES3("DES3"),
        /** HMAC key */
        HMAC("HMAC");

        private final String code;

        KeyType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * HSM key usage enumeration.
     */
    enum KeyUsage {
        /** Key used for digital signatures */
        SIGNATURE("SIGN"),
        /** Key used for encryption */
        ENCRYPTION("ENCR"),
        /** Key used for key exchange */
        KEY_EXCHANGE("KEX"),
        /** Key used for authentication */
        AUTHENTICATION("AUTH"),
        /** Key used for key wrapping */
        KEY_WRAPPING("WRAP");

        private final String code;

        KeyUsage(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Initializes the HSM connection and performs health checks.
     *
     * @return true if initialization was successful, false otherwise
     * @throws HsmException if initialization fails
     */
    boolean initialize() throws HsmException;

    /**
     * Closes the HSM connection and releases resources.
     *
     * @throws HsmException if cleanup fails
     */
    void shutdown() throws HsmException;

    /**
     * Gets the current connection status of the HSM.
     *
     * @return the connection status
     */
    ConnectionStatus getConnectionStatus();

    /**
     * Performs a health check on the HSM.
     *
     * @return true if the HSM is healthy, false otherwise
     * @throws HsmException if health check fails
     */
    boolean isHealthy() throws HsmException;

    /**
     * Generates a new key pair in the HSM.
     *
     * @param keyType the type of key to generate
     * @param keySize the size of the key in bits
     * @param keyLabel the label for the key
     * @param keyUsage the intended usage of the key
     * @return the generated key pair identifier
     * @throws HsmException if key generation fails
     */
    String generateKeyPair(KeyType keyType, int keySize, String keyLabel, KeyUsage keyUsage) throws HsmException;

    /**
     * Imports a key into the HSM.
     *
     * @param key the key to import
     * @param keyLabel the label for the key
     * @param keyUsage the intended usage of the key
     * @return the imported key identifier
     * @throws HsmException if key import fails
     */
    String importKey(Key key, String keyLabel, KeyUsage keyUsage) throws HsmException;

    /**
     * Exports a public key from the HSM.
     *
     * @param keyId the key identifier
     * @return the exported public key
     * @throws HsmException if key export fails
     */
    PublicKey exportPublicKey(String keyId) throws HsmException;

    /**
     * Signs data using a private key stored in the HSM.
     *
     * @param keyId the key identifier
     * @param data the data to sign
     * @param algorithm the signing algorithm to use
     * @return the digital signature
     * @throws HsmException if signing fails
     */
    byte[] sign(String keyId, byte[] data, String algorithm) throws HsmException;

    /**
     * Verifies a digital signature using a public key.
     *
     * @param publicKey the public key for verification
     * @param data the original data
     * @param signature the signature to verify
     * @param algorithm the signing algorithm used
     * @return true if the signature is valid, false otherwise
     * @throws HsmException if verification fails
     */
    boolean verifySignature(PublicKey publicKey, byte[] data, byte[] signature, String algorithm) throws HsmException;

    /**
     * Encrypts data using a key stored in the HSM.
     *
     * @param keyId the key identifier
     * @param data the data to encrypt
     * @param algorithm the encryption algorithm to use
     * @return the encrypted data
     * @throws HsmException if encryption fails
     */
    byte[] encrypt(String keyId, byte[] data, String algorithm) throws HsmException;

    /**
     * Decrypts data using a key stored in the HSM.
     *
     * @param keyId the key identifier
     * @param encryptedData the encrypted data
     * @param algorithm the encryption algorithm used
     * @return the decrypted data
     * @throws HsmException if decryption fails
     */
    byte[] decrypt(String keyId, byte[] encryptedData, String algorithm) throws HsmException;

    /**
     * Generates a random number using the HSM.
     *
     * @param length the length of the random number in bytes
     * @return the generated random number
     * @throws HsmException if random number generation fails
     */
    byte[] generateRandom(int length) throws HsmException;

    /**
     * Computes a hash of the provided data.
     *
     * @param data the data to hash
     * @param algorithm the hash algorithm to use
     * @return the computed hash
     * @throws HsmException if hashing fails
     */
    byte[] computeHash(byte[] data, String algorithm) throws HsmException;

    /**
     * Computes an HMAC using a key stored in the HSM.
     *
     * @param keyId the key identifier
     * @param data the data to compute HMAC for
     * @param algorithm the HMAC algorithm to use
     * @return the computed HMAC
     * @throws HsmException if HMAC computation fails
     */
    byte[] computeHmac(String keyId, byte[] data, String algorithm) throws HsmException;

    /**
     * Lists all keys stored in the HSM.
     *
     * @return list of key identifiers
     * @throws HsmException if listing fails
     */
    List<String> listKeys() throws HsmException;

    /**
     * Gets information about a specific key.
     *
     * @param keyId the key identifier
     * @return optional key information
     * @throws HsmException if key information retrieval fails
     */
    Optional<KeyInfo> getKeyInfo(String keyId) throws HsmException;

    /**
     * Deletes a key from the HSM.
     *
     * @param keyId the key identifier
     * @return true if deletion was successful, false otherwise
     * @throws HsmException if key deletion fails
     */
    boolean deleteKey(String keyId) throws HsmException;

    /**
     * Rotates a key in the HSM.
     *
     * @param keyId the key identifier to rotate
     * @param newKeyLabel the label for the new key
     * @return the new key identifier
     * @throws HsmException if key rotation fails
     */
    String rotateKey(String keyId, String newKeyLabel) throws HsmException;

    /**
     * Imports a certificate into the HSM.
     *
     * @param certificate the certificate to import
     * @param certificateLabel the label for the certificate
     * @return the imported certificate identifier
     * @throws HsmException if certificate import fails
     */
    String importCertificate(X509Certificate certificate, String certificateLabel) throws HsmException;

    /**
     * Exports a certificate from the HSM.
     *
     * @param certificateId the certificate identifier
     * @return the exported certificate
     * @throws HsmException if certificate export fails
     */
    X509Certificate exportCertificate(String certificateId) throws HsmException;

    /**
     * Validates a certificate chain.
     *
     * @param certificateChain the certificate chain to validate
     * @return true if the certificate chain is valid, false otherwise
     * @throws HsmException if certificate validation fails
     */
    boolean validateCertificateChain(List<X509Certificate> certificateChain) throws HsmException;

    /**
     * Gets HSM statistics and performance metrics.
     *
     * @return HSM statistics
     * @throws HsmException if statistics retrieval fails
     */
    HsmStatistics getStatistics() throws HsmException;

    /**
     * Gets HSM audit logs.
     *
     * @param fromTimestamp the start timestamp for the audit log
     * @param toTimestamp the end timestamp for the audit log
     * @return list of audit log entries
     * @throws HsmException if audit log retrieval fails
     */
    List<HsmAuditLogEntry> getAuditLogs(long fromTimestamp, long toTimestamp) throws HsmException;
}
