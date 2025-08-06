package com.finqube.iso20022.core.security.signature;

import com.finqube.iso20022.core.security.key.KeyInfo;

/**
 * Service for digital signature operations on ISO 20022 messages.
 *
 * @author Spring Finqube Team
 */
public interface MessageSignatureService {

    /**
     * Signs a message using the specified algorithm and private key.
     *
     * @param message the message to sign
     * @param algorithm the signature algorithm to use
     * @param keyInfo the key information containing the private key
     * @return the signature result
     * @throws SignatureException if signing fails
     */
    SignatureResult sign(byte[] message, SignatureAlgorithm algorithm, KeyInfo keyInfo) throws SignatureException;

    /**
     * Signs a string message using the specified algorithm and private key.
     *
     * @param message the message to sign
     * @param algorithm the signature algorithm to use
     * @param keyInfo the key information containing the private key
     * @return the signature result
     * @throws SignatureException if signing fails
     */
    default SignatureResult sign(String message, SignatureAlgorithm algorithm, KeyInfo keyInfo) throws SignatureException {
        return sign(message.getBytes(java.nio.charset.StandardCharsets.UTF_8), algorithm, keyInfo);
    }

    /**
     * Verifies a signature using the specified algorithm and public key.
     *
     * @param message the original message
     * @param signature the signature to verify
     * @param algorithm the signature algorithm used
     * @param keyInfo the key information containing the public key
     * @return the verification result
     * @throws SignatureException if verification fails
     */
    SignatureResult verify(byte[] message, String signature, SignatureAlgorithm algorithm, KeyInfo keyInfo) throws SignatureException;

    /**
     * Verifies a signature for a string message using the specified algorithm and public key.
     *
     * @param message the original message
     * @param signature the signature to verify
     * @param algorithm the signature algorithm used
     * @param keyInfo the key information containing the public key
     * @return the verification result
     * @throws SignatureException if verification fails
     */
    default SignatureResult verify(String message, String signature, SignatureAlgorithm algorithm, KeyInfo keyInfo) throws SignatureException {
        return verify(message.getBytes(java.nio.charset.StandardCharsets.UTF_8), signature, algorithm, keyInfo);
    }
}
