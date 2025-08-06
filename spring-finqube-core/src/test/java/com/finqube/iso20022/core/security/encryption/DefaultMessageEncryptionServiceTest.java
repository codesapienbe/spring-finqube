package com.finqube.iso20022.core.security.encryption;

import com.finqube.iso20022.core.security.key.KeyInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

import static org.junit.jupiter.api.Assertions.*;

class DefaultMessageEncryptionServiceTest {
    private MessageEncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new DefaultMessageEncryptionService();
    }

    @Test
    void testAesEncryptionDecryption() throws Exception {
        String message = "Hello, ISO 20022!";
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();
        // For AES, store the SecretKey in the privateKey field
        KeyInfo keyInfo = new KeyInfo.Builder()
                .keyId("test-aes-key")
                .keyType(null)
                .keyUsages(null)
                .privateKey(secretKey)
                .creationTime(null)
                .expirationTime(null)
                .lastRotationTime(null)
                .version(null)
                .active(true)
                .description("Test AES key")
                .algorithm("AES")
                .keySize(256)
                .build();
        byte[] encrypted = encryptionService.encrypt(message, EncryptionAlgorithm.AES, keyInfo);
        String decrypted = encryptionService.decryptToString(encrypted, EncryptionAlgorithm.AES, keyInfo);
        assertEquals(message, decrypted);
    }

    @Test
    void testRsaEncryptionDecryption() throws Exception {
        String message = "Hello, ISO 20022!";
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        KeyInfo keyInfo = new KeyInfo.Builder()
                .keyId("test-rsa-key")
                .keyType(null)
                .keyUsages(null)
                .publicKey(keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .creationTime(null)
                .expirationTime(null)
                .lastRotationTime(null)
                .version(null)
                .active(true)
                .description("Test RSA key")
                .algorithm("RSA")
                .keySize(2048)
                .build();
        byte[] encrypted = encryptionService.encrypt(message, EncryptionAlgorithm.RSA, keyInfo);
        String decrypted = encryptionService.decryptToString(encrypted, EncryptionAlgorithm.RSA, keyInfo);
        assertEquals(message, decrypted);
    }

    @Test
    void testNullInputs() {
        KeyInfo dummyKeyInfo = new KeyInfo.Builder().build();
        assertThrows(EncryptionException.class, () -> encryptionService.encrypt((byte[]) null, EncryptionAlgorithm.AES, dummyKeyInfo));
        assertThrows(EncryptionException.class, () -> encryptionService.decrypt((byte[]) null, EncryptionAlgorithm.AES, dummyKeyInfo));
    }

    @Test
    void testChaCha20EncryptionDecryption() throws Exception {
        String message = "Hello, ChaCha20!";
        KeyGenerator keyGen = KeyGenerator.getInstance("ChaCha20");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();
        KeyInfo keyInfo = new KeyInfo.Builder()
                .keyId("test-chacha20-key")
                .keyType(null)
                .keyUsages(null)
                .privateKey(secretKey)
                .creationTime(null)
                .expirationTime(null)
                .lastRotationTime(null)
                .version(null)
                .active(true)
                .description("Test ChaCha20 key")
                .algorithm("ChaCha20")
                .keySize(256)
                .build();
        byte[] encrypted = encryptionService.encrypt(message, EncryptionAlgorithm.CHACHA20, keyInfo);
        String decrypted = encryptionService.decryptToString(encrypted, EncryptionAlgorithm.CHACHA20, keyInfo);
        assertEquals(message, decrypted);
    }

    @Test
    void testEciesEncryptionDecryption() throws Exception {
        String message = "Hello, ECIES!";
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC");
        keyPairGen.initialize(new ECGenParameterSpec("secp256r1"));
        KeyPair keyPair = keyPairGen.generateKeyPair();
        KeyInfo keyInfo = new KeyInfo.Builder()
                .keyId("test-ecies-key")
                .keyType(null)
                .keyUsages(null)
                .publicKey(keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .creationTime(null)
                .expirationTime(null)
                .lastRotationTime(null)
                .version(null)
                .active(true)
                .description("Test ECIES key")
                .algorithm("EC")
                .keySize(256)
                .build();
        byte[] encrypted = encryptionService.encrypt(message, EncryptionAlgorithm.ECIES, keyInfo);
        String decrypted = encryptionService.decryptToString(encrypted, EncryptionAlgorithm.ECIES, keyInfo);
        assertEquals(message, decrypted);
    }

    @Test
    void testUnsupportedAlgorithm() {
        KeyInfo dummyKeyInfo = new KeyInfo.Builder().build();
        assertThrows(EncryptionException.class, () -> encryptionService.encrypt("test", null, dummyKeyInfo));
        assertThrows(EncryptionException.class, () -> encryptionService.decrypt("test".getBytes(), null, dummyKeyInfo));
    }
}
