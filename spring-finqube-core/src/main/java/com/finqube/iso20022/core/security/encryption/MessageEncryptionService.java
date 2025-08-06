package com.finqube.iso20022.core.security.encryption;

import com.finqube.iso20022.core.security.key.KeyInfo;

/**
 * Service for encrypting and decrypting ISO 20022 messages using multiple algorithms.
 *
 * @author Spring Finqube Team
 */
public interface MessageEncryptionService {

    /**
     * Encrypts the given plaintext using the specified algorithm and key.
     *
     * @param plaintext the data to encrypt
     * @param algorithm the encryption algorithm
     * @param keyInfo the key information
     * @return the encrypted data
     * @throws EncryptionException if encryption fails
     */
    byte[] encrypt(byte[] plaintext, EncryptionAlgorithm algorithm, KeyInfo keyInfo) throws EncryptionException;

    /**
     * Decrypts the given ciphertext using the specified algorithm and key.
     *
     * @param ciphertext the data to decrypt
     * @param algorithm the encryption algorithm
     * @param keyInfo the key information
     * @return the decrypted data
     * @throws EncryptionException if decryption fails
     */
    byte[] decrypt(byte[] ciphertext, EncryptionAlgorithm algorithm, KeyInfo keyInfo) throws EncryptionException;

    /**
     * Encrypts a string message (UTF-8) using the specified algorithm and key.
     *
     * @param message the message to encrypt
     * @param algorithm the encryption algorithm
     * @param keyInfo the key information
     * @return the encrypted data
     * @throws EncryptionException if encryption fails
     */
    default byte[] encrypt(String message, EncryptionAlgorithm algorithm, KeyInfo keyInfo) throws EncryptionException {
        return encrypt(message.getBytes(java.nio.charset.StandardCharsets.UTF_8), algorithm, keyInfo);
    }

    /**
     * Decrypts the given ciphertext to a string (UTF-8) using the specified algorithm and key.
     *
     * @param ciphertext the data to decrypt
     * @param algorithm the encryption algorithm
     * @param keyInfo the key information
     * @return the decrypted message as a string
     * @throws EncryptionException if decryption fails
     */
    default String decryptToString(byte[] ciphertext, EncryptionAlgorithm algorithm, KeyInfo keyInfo) throws EncryptionException {
        return new String(decrypt(ciphertext, algorithm, keyInfo), java.nio.charset.StandardCharsets.UTF_8);
    }
}
