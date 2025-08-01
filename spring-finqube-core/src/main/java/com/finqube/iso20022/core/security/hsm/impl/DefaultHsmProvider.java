package com.finqube.iso20022.core.security.hsm.impl;

import com.finqube.iso20022.core.security.hsm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default HSM provider implementation for development and testing.
 *
 * <p>This implementation provides a basic HSM functionality using Java's built-in
 * cryptographic providers. It is suitable for development, testing, and non-production
 * environments where a physical HSM is not available.</p>
 *
 * <p><strong>Warning:</strong> This implementation is NOT suitable for production use
 * as it stores keys in memory and does not provide the same level of security as
 * a physical HSM.</p>
 *
 * @author Spring Finqube Team
 * @version 1.0.0
 * @since 0.1.0
 */
public class DefaultHsmProvider implements HsmProvider {

    private static final Logger log = LoggerFactory.getLogger(DefaultHsmProvider.class);

    private final Map<String, Key> keyStore = new ConcurrentHashMap<>();
    private final Map<String, X509Certificate> certificateStore = new ConcurrentHashMap<>();
    private final Map<String, KeyInfo> keyInfoStore = new ConcurrentHashMap<>();
    private final List<HsmAuditLogEntry> auditLog = Collections.synchronizedList(new ArrayList<>());

    private final AtomicLong operationCounter = new AtomicLong(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final AtomicLong successfulOperations = new AtomicLong(0);
    private final AtomicLong failedOperations = new AtomicLong(0);
    private final AtomicLong cancelledOperations = new AtomicLong(0);
    private final AtomicLong timeoutOperations = new AtomicLong(0);

    private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
    private final LocalDateTime startTime = LocalDateTime.now();
    private final Random secureRandom = new SecureRandom();

    @Override
    public boolean initialize() throws HsmException {
        try {
            log.info("Initializing Default HSM Provider");
            connectionStatus = ConnectionStatus.CONNECTING;

            // Simulate initialization delay
            Thread.sleep(100);

            connectionStatus = ConnectionStatus.CONNECTED;
            log.info("Default HSM Provider initialized successfully");
            return true;
        } catch (InterruptedException e) {
            connectionStatus = ConnectionStatus.FAILED;
            Thread.currentThread().interrupt();
            throw new HsmException("HSM initialization interrupted", "INITIALIZE", null, e);
        } catch (Exception e) {
            connectionStatus = ConnectionStatus.FAILED;
            throw new HsmException("HSM initialization failed", "INITIALIZE", null, e);
        }
    }

    @Override
    public void shutdown() throws HsmException {
        try {
            log.info("Shutting down Default HSM Provider");
            connectionStatus = ConnectionStatus.DISCONNECTED;

            // Clear all stored data
            keyStore.clear();
            certificateStore.clear();
            keyInfoStore.clear();
            auditLog.clear();

            log.info("Default HSM Provider shut down successfully");
        } catch (Exception e) {
            throw new HsmException("HSM shutdown failed", "SHUTDOWN", null, e);
        }
    }

    @Override
    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    @Override
    public boolean isHealthy() throws HsmException {
        return ConnectionStatus.CONNECTED.equals(connectionStatus);
    }

    @Override
    public String generateKeyPair(KeyType keyType, int keySize, String keyLabel, KeyUsage keyUsage) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "GENERATE_KEY_PAIR";
        String keyId = null;

        try {
            log.debug("Generating {} key pair with size {} and label '{}'", keyType, keySize, keyLabel);

            KeyPairGenerator keyPairGenerator;
            if (KeyType.RSA.equals(keyType)) {
                keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            } else if (KeyType.EC.equals(keyType)) {
                keyPairGenerator = KeyPairGenerator.getInstance("EC");
            } else {
                throw new HsmException("Unsupported key type: " + keyType, operation, null);
            }

            keyPairGenerator.initialize(keySize, secureRandom);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            keyId = "KEY-" + UUID.randomUUID().toString();
            keyStore.put(keyId, keyPair.getPrivate());

            // Store key information
            KeyInfo keyInfo = new KeyInfo(keyId, keyLabel, keyType, keyUsage, keySize,
                                        LocalDateTime.now(), null, true, false,
                                        keyPair.getPrivate().getAlgorithm(), "Generated key");
            keyInfoStore.put(keyId, keyInfo);

            logAuditEntry(operation, keyId, null, OperationStatus.SUCCESS, "Key pair generated successfully",
                         System.currentTimeMillis() - startTime);

            log.debug("Generated key pair with ID: {}", keyId);
            return keyId;

        } catch (Exception e) {
            logAuditEntry(operation, keyId, null, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("Key pair generation failed", operation, keyId, e);
        }
    }

    @Override
    public String importKey(Key key, String keyLabel, KeyUsage keyUsage) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "IMPORT_KEY";
        String keyId = null;

        try {
            log.debug("Importing key with label '{}'", keyLabel);

            keyId = "KEY-" + UUID.randomUUID().toString();
            keyStore.put(keyId, key);

            // Determine key type and size
            KeyType keyType = determineKeyType(key);
            int keySize = determineKeySize(key);

            // Store key information
            KeyInfo keyInfo = new KeyInfo(keyId, keyLabel, keyType, keyUsage, keySize,
                                        LocalDateTime.now(), null, true, false,
                                        key.getAlgorithm(), "Imported key");
            keyInfoStore.put(keyId, keyInfo);

            logAuditEntry(operation, keyId, null, OperationStatus.SUCCESS, "Key imported successfully",
                         System.currentTimeMillis() - startTime);

            log.debug("Imported key with ID: {}", keyId);
            return keyId;

        } catch (Exception e) {
            logAuditEntry(operation, keyId, null, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("Key import failed", operation, keyId, e);
        }
    }

    @Override
    public PublicKey exportPublicKey(String keyId) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "EXPORT_PUBLIC_KEY";

        try {
            log.debug("Exporting public key for key ID: {}", keyId);

            Key key = keyStore.get(keyId);
            if (key == null) {
                throw new HsmException("Key not found: " + keyId, operation, keyId);
            }

            if (key instanceof PrivateKey) {
                // For demonstration purposes, we'll create a dummy public key
                // In a real implementation, this would extract the public key from the HSM
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048, secureRandom);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();

                logAuditEntry(operation, keyId, null, OperationStatus.SUCCESS, "Public key exported successfully",
                             System.currentTimeMillis() - startTime);

                return keyPair.getPublic();
            } else {
                throw new HsmException("Key is not a private key", operation, keyId);
            }

        } catch (Exception e) {
            logAuditEntry(operation, keyId, null, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("Public key export failed", operation, keyId, e);
        }
    }

    @Override
    public byte[] sign(String keyId, byte[] data, String algorithm) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "SIGN";

        try {
            log.debug("Signing data with key ID: {} using algorithm: {}", keyId, algorithm);

            Key key = keyStore.get(keyId);
            if (key == null) {
                throw new HsmException("Key not found: " + keyId, operation, keyId);
            }

            if (!(key instanceof PrivateKey)) {
                throw new HsmException("Key is not a private key", operation, keyId);
            }

            Signature signature = Signature.getInstance(algorithm);
            signature.initSign((PrivateKey) key);
            signature.update(data);
            byte[] signedData = signature.sign();

            logAuditEntry(operation, keyId, null, OperationStatus.SUCCESS, "Data signed successfully",
                         System.currentTimeMillis() - startTime);

            return signedData;

        } catch (Exception e) {
            logAuditEntry(operation, keyId, null, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("Signing failed", operation, keyId, e);
        }
    }

    @Override
    public boolean verifySignature(PublicKey publicKey, byte[] data, byte[] signature, String algorithm) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "VERIFY_SIGNATURE";

        try {
            log.debug("Verifying signature using algorithm: {}", algorithm);

            Signature sig = Signature.getInstance(algorithm);
            sig.initVerify(publicKey);
            sig.update(data);
            boolean isValid = sig.verify(signature);

            logAuditEntry(operation, null, null, OperationStatus.SUCCESS,
                         "Signature verification " + (isValid ? "succeeded" : "failed"),
                         System.currentTimeMillis() - startTime);

            return isValid;

        } catch (Exception e) {
            logAuditEntry(operation, null, null, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("Signature verification failed", operation, null, e);
        }
    }

    @Override
    public byte[] encrypt(String keyId, byte[] data, String algorithm) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "ENCRYPT";

        try {
            log.debug("Encrypting data with key ID: {} using algorithm: {}", keyId, algorithm);

            Key key = keyStore.get(keyId);
            if (key == null) {
                throw new HsmException("Key not found: " + keyId, operation, keyId);
            }

            // For demonstration purposes, we'll use a simple encryption
            // In a real implementation, this would use the HSM for encryption
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(algorithm);
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
            byte[] encryptedData = cipher.doFinal(data);

            logAuditEntry(operation, keyId, null, OperationStatus.SUCCESS, "Data encrypted successfully",
                         System.currentTimeMillis() - startTime);

            return encryptedData;

        } catch (Exception e) {
            logAuditEntry(operation, keyId, null, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("Encryption failed", operation, keyId, e);
        }
    }

    @Override
    public byte[] decrypt(String keyId, byte[] encryptedData, String algorithm) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "DECRYPT";

        try {
            log.debug("Decrypting data with key ID: {} using algorithm: {}", keyId, algorithm);

            Key key = keyStore.get(keyId);
            if (key == null) {
                throw new HsmException("Key not found: " + keyId, operation, keyId);
            }

            // For demonstration purposes, we'll use a simple decryption
            // In a real implementation, this would use the HSM for decryption
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(algorithm);
            cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key);
            byte[] decryptedData = cipher.doFinal(encryptedData);

            logAuditEntry(operation, keyId, null, OperationStatus.SUCCESS, "Data decrypted successfully",
                         System.currentTimeMillis() - startTime);

            return decryptedData;

        } catch (Exception e) {
            logAuditEntry(operation, keyId, null, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("Decryption failed", operation, keyId, e);
        }
    }

    @Override
    public byte[] generateRandom(int length) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "GENERATE_RANDOM";

        try {
            log.debug("Generating {} bytes of random data", length);

            byte[] randomData = new byte[length];
            secureRandom.nextBytes(randomData);

            logAuditEntry(operation, null, null, OperationStatus.SUCCESS, "Random data generated successfully",
                         System.currentTimeMillis() - startTime);

            return randomData;

        } catch (Exception e) {
            logAuditEntry(operation, null, null, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("Random generation failed", operation, null, e);
        }
    }

    @Override
    public byte[] computeHash(byte[] data, String algorithm) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "COMPUTE_HASH";

        try {
            log.debug("Computing hash using algorithm: {}", algorithm);

            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(data);

            logAuditEntry(operation, null, null, OperationStatus.SUCCESS, "Hash computed successfully",
                         System.currentTimeMillis() - startTime);

            return hash;

        } catch (Exception e) {
            logAuditEntry(operation, null, null, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("Hash computation failed", operation, null, e);
        }
    }

    @Override
    public byte[] computeHmac(String keyId, byte[] data, String algorithm) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "COMPUTE_HMAC";

        try {
            log.debug("Computing HMAC with key ID: {} using algorithm: {}", keyId, algorithm);

            Key key = keyStore.get(keyId);
            if (key == null) {
                throw new HsmException("Key not found: " + keyId, operation, keyId);
            }

            javax.crypto.Mac mac = javax.crypto.Mac.getInstance(algorithm);
            mac.init(key);
            byte[] hmac = mac.doFinal(data);

            logAuditEntry(operation, keyId, null, OperationStatus.SUCCESS, "HMAC computed successfully",
                         System.currentTimeMillis() - startTime);

            return hmac;

        } catch (Exception e) {
            logAuditEntry(operation, keyId, null, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("HMAC computation failed", operation, keyId, e);
        }
    }

    @Override
    public List<String> listKeys() throws HsmException {
        return new ArrayList<>(keyStore.keySet());
    }

    @Override
    public Optional<KeyInfo> getKeyInfo(String keyId) throws HsmException {
        return Optional.ofNullable(keyInfoStore.get(keyId));
    }

    @Override
    public boolean deleteKey(String keyId) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "DELETE_KEY";

        try {
            log.debug("Deleting key with ID: {}", keyId);

            boolean removed = keyStore.remove(keyId) != null;
            keyInfoStore.remove(keyId);

            logAuditEntry(operation, keyId, null, OperationStatus.SUCCESS, "Key deleted successfully",
                         System.currentTimeMillis() - startTime);

            return removed;

        } catch (Exception e) {
            logAuditEntry(operation, keyId, null, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("Key deletion failed", operation, keyId, e);
        }
    }

    @Override
    public String rotateKey(String keyId, String newKeyLabel) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "ROTATE_KEY";

        try {
            log.debug("Rotating key with ID: {}", keyId);

            KeyInfo oldKeyInfo = keyInfoStore.get(keyId);
            if (oldKeyInfo == null) {
                throw new HsmException("Key not found: " + keyId, operation, keyId);
            }

            // Generate new key
            String newKeyId = generateKeyPair(oldKeyInfo.getKeyType(), oldKeyInfo.getKeySize(),
                                            newKeyLabel, oldKeyInfo.getKeyUsage());

            logAuditEntry(operation, keyId, null, OperationStatus.SUCCESS, "Key rotated successfully",
                         System.currentTimeMillis() - startTime);

            return newKeyId;

        } catch (Exception e) {
            logAuditEntry(operation, keyId, null, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("Key rotation failed", operation, keyId, e);
        }
    }

    @Override
    public String importCertificate(X509Certificate certificate, String certificateLabel) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "IMPORT_CERTIFICATE";
        String certificateId = null;

        try {
            log.debug("Importing certificate with label: {}", certificateLabel);

            certificateId = "CERT-" + UUID.randomUUID().toString();
            certificateStore.put(certificateId, certificate);

            logAuditEntry(operation, null, certificateId, OperationStatus.SUCCESS, "Certificate imported successfully",
                         System.currentTimeMillis() - startTime);

            return certificateId;

        } catch (Exception e) {
            logAuditEntry(operation, null, certificateId, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("Certificate import failed", operation, null, e);
        }
    }

    @Override
    public X509Certificate exportCertificate(String certificateId) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "EXPORT_CERTIFICATE";

        try {
            log.debug("Exporting certificate with ID: {}", certificateId);

            X509Certificate certificate = certificateStore.get(certificateId);
            if (certificate == null) {
                throw new HsmException("Certificate not found: " + certificateId, operation, certificateId);
            }

            logAuditEntry(operation, null, certificateId, OperationStatus.SUCCESS, "Certificate exported successfully",
                         System.currentTimeMillis() - startTime);

            return certificate;

        } catch (Exception e) {
            logAuditEntry(operation, null, certificateId, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("Certificate export failed", operation, certificateId, e);
        }
    }

    @Override
    public boolean validateCertificateChain(List<X509Certificate> certificateChain) throws HsmException {
        long startTime = System.currentTimeMillis();
        String operation = "VALIDATE_CERTIFICATE_CHAIN";

        try {
            log.debug("Validating certificate chain with {} certificates", certificateChain.size());

            // For demonstration purposes, we'll just check if certificates are not expired
            // In a real implementation, this would perform proper certificate chain validation
            boolean isValid = certificateChain.stream()
                    .allMatch(cert -> cert.getNotAfter().after(new java.util.Date()));

            logAuditEntry(operation, null, null, OperationStatus.SUCCESS,
                         "Certificate chain validation " + (isValid ? "succeeded" : "failed"),
                         System.currentTimeMillis() - startTime);

            return isValid;

        } catch (Exception e) {
            logAuditEntry(operation, null, null, OperationStatus.FAILED, e.getMessage(),
                         System.currentTimeMillis() - startTime);
            throw new HsmException("Certificate chain validation failed", operation, null, e);
        }
    }

    @Override
    public HsmStatistics getStatistics() throws HsmException {
        long totalOps = operationCounter.get();
        long successful = successfulOperations.get();
        long failed = failedOperations.get();
        long cancelled = cancelledOperations.get();
        long timeout = timeoutOperations.get();
        long totalTime = totalResponseTime.get();

        double avgResponseTime = totalOps > 0 ? (double) totalTime / totalOps : 0.0;

        return new HsmStatistics(
            LocalDateTime.now(),
            totalOps, successful, failed, cancelled, timeout,
            avgResponseTime, 0.0, 0.0, // min/max response times not tracked in this implementation
            keyStore.size(), keyStore.size(), 0, // key statistics
            certificateStore.size(), certificateStore.size(), 0, // certificate statistics
            connectionStatus,
            java.time.Duration.between(startTime, LocalDateTime.now()).getSeconds(),
            0.0, 0.0, // CPU and memory usage not tracked in this implementation
            0L, 0L // memory statistics not tracked in this implementation
        );
    }

    @Override
    public List<HsmAuditLogEntry> getAuditLogs(long fromTimestamp, long toTimestamp) throws HsmException {
        return new ArrayList<>(auditLog);
    }

    private void logAuditEntry(String operation, String keyId, String certificateId,
                              OperationStatus status, String result, long responseTime) {
        operationCounter.incrementAndGet();
        totalResponseTime.addAndGet(responseTime);

        switch (status) {
            case SUCCESS -> successfulOperations.incrementAndGet();
            case FAILED -> failedOperations.incrementAndGet();
            case CANCELLED -> cancelledOperations.incrementAndGet();
            case TIMEOUT -> timeoutOperations.incrementAndGet();
        }

        HsmAuditLogEntry entry = new HsmAuditLogEntry(
            "ENTRY-" + UUID.randomUUID().toString(),
            LocalDateTime.now(),
            "SYSTEM", // In a real implementation, this would be the actual user ID
            "SESSION-" + UUID.randomUUID().toString(),
            operation,
            keyId,
            certificateId,
            status,
            result,
            status == OperationStatus.FAILED ? result : null,
            "127.0.0.1", // In a real implementation, this would be the actual client IP
            "DefaultHsmProvider/1.0",
            responseTime,
            null
        );

        auditLog.add(entry);

        if (auditLog.size() > 10000) {
            // Keep only the last 10,000 entries
            auditLog.subList(0, auditLog.size() - 10000).clear();
        }
    }

    private KeyType determineKeyType(Key key) {
        String algorithm = key.getAlgorithm();
        if ("RSA".equals(algorithm)) {
            return KeyType.RSA;
        } else if ("EC".equals(algorithm) || "ECDSA".equals(algorithm)) {
            return KeyType.EC;
        } else if ("AES".equals(algorithm)) {
            return KeyType.AES;
        } else if ("DESede".equals(algorithm)) {
            return KeyType.DES3;
        } else if ("HmacSHA256".equals(algorithm) || "HmacSHA512".equals(algorithm)) {
            return KeyType.HMAC;
        } else {
            return KeyType.OTHER;
        }
    }

    private int determineKeySize(Key key) {
        if (key instanceof javax.crypto.SecretKey) {
            return key.getEncoded().length * 8;
        } else if (key instanceof java.security.interfaces.RSAKey) {
            return ((java.security.interfaces.RSAKey) key).getModulus().bitLength();
        } else if (key instanceof java.security.interfaces.ECKey) {
            return ((java.security.interfaces.ECKey) key).getParams().getCurve().getField().getFieldSize();
        } else {
            return 0;
        }
    }
}
