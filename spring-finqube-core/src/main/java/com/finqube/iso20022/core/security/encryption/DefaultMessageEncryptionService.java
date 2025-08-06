package com.finqube.iso20022.core.security.encryption;

import com.finqube.iso20022.core.security.key.KeyInfo;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

/**
 * Default implementation of MessageEncryptionService supporting AES and RSA.
 *
 * @author Spring Finqube Team
 */
public class DefaultMessageEncryptionService implements MessageEncryptionService {
    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int CHACHA20_NONCE_LENGTH = 12;
    private static final int CHACHA20_TAG_LENGTH = 128;

    @Override
    public byte[] encrypt(byte[] plaintext, EncryptionAlgorithm algorithm, KeyInfo keyInfo) throws EncryptionException {
        if (plaintext == null || algorithm == null || keyInfo == null) {
            throw new EncryptionException("Null input to encrypt");
        }
        try {
            switch (algorithm) {
                case AES -> {
                    SecretKey secretKey = getSecretKey(keyInfo);
                    byte[] iv = new byte[GCM_IV_LENGTH];
                    new SecureRandom().nextBytes(iv);
                    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                    GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
                    byte[] ciphertext = cipher.doFinal(plaintext);
                    byte[] result = new byte[iv.length + ciphertext.length];
                    System.arraycopy(iv, 0, result, 0, iv.length);
                    System.arraycopy(ciphertext, 0, result, iv.length, ciphertext.length);
                    return result;
                }
                case RSA -> {
                    PublicKey publicKey = keyInfo.getPublicKey();
                    if (publicKey == null) throw new EncryptionException("KeyInfo does not contain a public key");
                    Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                    return cipher.doFinal(plaintext);
                }
                case CHACHA20 -> {
                    SecretKey secretKey = getSecretKey(keyInfo);
                    byte[] nonce = new byte[CHACHA20_NONCE_LENGTH];
                    new SecureRandom().nextBytes(nonce);
                    Cipher cipher = Cipher.getInstance("ChaCha20-Poly1305");
                    GCMParameterSpec spec = new GCMParameterSpec(CHACHA20_TAG_LENGTH, nonce);
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
                    byte[] ciphertext = cipher.doFinal(plaintext);
                    byte[] result = new byte[nonce.length + ciphertext.length];
                    System.arraycopy(nonce, 0, result, 0, nonce.length);
                    System.arraycopy(ciphertext, 0, result, nonce.length, ciphertext.length);
                    return result;
                }
                case ECIES -> {
                    PublicKey publicKey = keyInfo.getPublicKey();
                    if (publicKey == null) throw new EncryptionException("KeyInfo does not contain a public key");
                    // Add BouncyCastle provider if not already present
                    if (Security.getProvider("BC") == null) {
                        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
                    }
                    Cipher cipher = Cipher.getInstance("ECIES", "BC");
                    cipher.init(Cipher.ENCRYPT_MODE, publicKey, new SecureRandom());
                    return cipher.doFinal(plaintext);
                }
                default -> throw new EncryptionException("Unsupported algorithm: " + algorithm);
            }
        } catch (Exception e) {
            throw new EncryptionException("Encryption failed", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, EncryptionAlgorithm algorithm, KeyInfo keyInfo) throws EncryptionException {
        if (ciphertext == null || algorithm == null || keyInfo == null) {
            throw new EncryptionException("Null input to decrypt");
        }
        try {
            switch (algorithm) {
                case AES -> {
                    SecretKey secretKey = getSecretKey(keyInfo);
                    byte[] iv = Arrays.copyOfRange(ciphertext, 0, GCM_IV_LENGTH);
                    byte[] actualCiphertext = Arrays.copyOfRange(ciphertext, GCM_IV_LENGTH, ciphertext.length);
                    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                    GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
                    return cipher.doFinal(actualCiphertext);
                }
                case RSA -> {
                    PrivateKey privateKey = keyInfo.getPrivateKey();
                    if (privateKey == null) throw new EncryptionException("KeyInfo does not contain a private key");
                    Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                    cipher.init(Cipher.DECRYPT_MODE, privateKey);
                    return cipher.doFinal(ciphertext);
                }
                case CHACHA20 -> {
                    SecretKey secretKey = getSecretKey(keyInfo);
                    byte[] nonce = Arrays.copyOfRange(ciphertext, 0, CHACHA20_NONCE_LENGTH);
                    byte[] actualCiphertext = Arrays.copyOfRange(ciphertext, CHACHA20_NONCE_LENGTH, ciphertext.length);
                    Cipher cipher = Cipher.getInstance("ChaCha20-Poly1305");
                    GCMParameterSpec spec = new GCMParameterSpec(CHACHA20_TAG_LENGTH, nonce);
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
                    return cipher.doFinal(actualCiphertext);
                }
                case ECIES -> {
                    PrivateKey privateKey = keyInfo.getPrivateKey();
                    if (privateKey == null) throw new EncryptionException("KeyInfo does not contain a private key");
                    // Add BouncyCastle provider if not already present
                    if (Security.getProvider("BC") == null) {
                        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
                    }
                    Cipher cipher = Cipher.getInstance("ECIES", "BC");
                    cipher.init(Cipher.DECRYPT_MODE, privateKey);
                    return cipher.doFinal(ciphertext);
                }
                default -> throw new EncryptionException("Unsupported algorithm: " + algorithm);
            }
        } catch (Exception e) {
            throw new EncryptionException("Decryption failed", e);
        }
    }

    private SecretKey getSecretKey(KeyInfo keyInfo) throws EncryptionException {
        // Check if we have a SecretKey stored in either field
        Key privateKey = keyInfo.getPrivateKeyAsKey();
        Key publicKey = keyInfo.getPublicKeyAsKey();

        if (privateKey instanceof SecretKey) {
            return (SecretKey) privateKey;
        } else if (publicKey instanceof SecretKey) {
            return (SecretKey) publicKey;
        } else {
            // Try to create a SecretKey from encoded key data
            Key key = privateKey != null ? privateKey : publicKey;
            if (key != null && key.getEncoded() != null) {
                return new SecretKeySpec(key.getEncoded(), 0, key.getEncoded().length, "AES");
            } else {
                throw new EncryptionException("KeyInfo does not contain a valid AES key");
            }
        }
    }
}
