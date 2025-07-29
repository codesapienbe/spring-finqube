package com.finqube.iso20022.core.security;

import java.security.KeyStore;
import java.util.concurrent.CompletableFuture;

import com.finqube.iso20022.core.message.BaseMessage;

/**
 * Security manager for ISO 20022 message security operations.
 *
 * <p>This interface defines the contract for all security operations including
 * encryption, digital signatures, certificate management, and secure transport.</p>
 *
 * <p>The security manager supports:</p>
 * <ul>
 *   <li>Message encryption and decryption</li>
 *   <li>Digital signature creation and verification</li>
 *   <li>Certificate and key management</li>
 *   <li>Secure transport mechanisms</li>
 *   <li>Security monitoring and auditing</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * @Autowired
 * private SecurityManager securityManager;
 *
 * // Sign and encrypt a message
 * SecureMessage secureMessage = securityManager.signAndEncrypt(message, recipientCertificate);
 *
 * // Verify and decrypt a message
 * BaseMessage decryptedMessage = securityManager.verifyAndDecrypt(secureMessage);
 * }</pre>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
public interface SecurityManager {

    /**
     * Signs an ISO 20022 message.
     *
     * @param message the message to sign
     * @return the signed message
     * @throws SecurityException if signing fails
     */
    SignedMessage sign(BaseMessage message) throws SecurityException;

    /**
     * Signs an ISO 20022 message asynchronously.
     *
     * @param message the message to sign
     * @return a CompletableFuture that will complete with the signed message
     */
    CompletableFuture<SignedMessage> signAsync(BaseMessage message);

    /**
     * Verifies the signature of a signed message.
     *
     * @param signedMessage the signed message to verify
     * @return the verification result
     * @throws SecurityException if verification fails
     */
    SignatureVerificationResult verifySignature(SignedMessage signedMessage) throws SecurityException;

    /**
     * Verifies the signature of a signed message asynchronously.
     *
     * @param signedMessage the signed message to verify
     * @return a CompletableFuture that will complete with the verification result
     */
    CompletableFuture<SignatureVerificationResult> verifySignatureAsync(SignedMessage signedMessage);

    /**
     * Encrypts an ISO 20022 message.
     *
     * @param message the message to encrypt
     * @param recipientCertificate the recipient's certificate
     * @return the encrypted message
     * @throws SecurityException if encryption fails
     */
    EncryptedMessage encrypt(BaseMessage message, byte[] recipientCertificate) throws SecurityException;

    /**
     * Encrypts an ISO 20022 message asynchronously.
     *
     * @param message the message to encrypt
     * @param recipientCertificate the recipient's certificate
     * @return a CompletableFuture that will complete with the encrypted message
     */
    CompletableFuture<EncryptedMessage> encryptAsync(BaseMessage message, byte[] recipientCertificate);

    /**
     * Decrypts an encrypted message.
     *
     * @param encryptedMessage the encrypted message to decrypt
     * @return the decrypted message
     * @throws SecurityException if decryption fails
     */
    BaseMessage decrypt(EncryptedMessage encryptedMessage) throws SecurityException;

    /**
     * Decrypts an encrypted message asynchronously.
     *
     * @param encryptedMessage the encrypted message to decrypt
     * @return a CompletableFuture that will complete with the decrypted message
     */
    CompletableFuture<BaseMessage> decryptAsync(EncryptedMessage encryptedMessage);

    /**
     * Signs and encrypts an ISO 20022 message.
     *
     * @param message the message to sign and encrypt
     * @param recipientCertificate the recipient's certificate
     * @return the secure message
     * @throws SecurityException if signing or encryption fails
     */
    SecureMessage signAndEncrypt(BaseMessage message, byte[] recipientCertificate) throws SecurityException;

    /**
     * Signs and encrypts an ISO 20022 message asynchronously.
     *
     * @param message the message to sign and encrypt
     * @param recipientCertificate the recipient's certificate
     * @return a CompletableFuture that will complete with the secure message
     */
    CompletableFuture<SecureMessage> signAndEncryptAsync(BaseMessage message, byte[] recipientCertificate);

    /**
     * Verifies and decrypts a secure message.
     *
     * @param secureMessage the secure message to verify and decrypt
     * @return the verification and decryption result
     * @throws SecurityException if verification or decryption fails
     */
    SecureMessageResult verifyAndDecrypt(SecureMessage secureMessage) throws SecurityException;

    /**
     * Verifies and decrypts a secure message asynchronously.
     *
     * @param secureMessage the secure message to verify and decrypt
     * @return a CompletableFuture that will complete with the result
     */
    CompletableFuture<SecureMessageResult> verifyAndDecryptAsync(SecureMessage secureMessage);

    /**
     * Gets the security manager identifier.
     *
     * @return the security manager identifier
     */
    String getSecurityManagerId();

    /**
     * Gets the security manager display name.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Gets the security manager version.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Checks if the security manager is available.
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();

    /**
     * Gets the key store.
     *
     * @return the key store, or null if not available
     */
    KeyStore getKeyStore();

    /**
     * Gets security statistics.
     *
     * @return the security statistics
     */
    SecurityStatistics getStatistics();

    /**
     * Performs a health check on the security manager.
     *
     * @return the health check result
     */
    SecurityHealthCheck healthCheck();
}
