package com.finqube.iso20022.core.security.signature;

import com.finqube.iso20022.core.security.key.KeyInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

import static org.junit.jupiter.api.Assertions.*;

class DefaultMessageSignatureServiceTest {

    private MessageSignatureService signatureService;

    @BeforeEach
    void setUp() {
        signatureService = new DefaultMessageSignatureService();
    }

    @Test
    void testRsaSha256SignAndVerify() throws Exception {
        String message = "Hello, RSA SHA256!";
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        KeyInfo keyInfo = createKeyInfo("test-rsa-sha256", keyPair);

        // Sign the message
        SignatureResult signResult = signatureService.sign(message, SignatureAlgorithm.RSA_SHA256, keyInfo);
        assertTrue(signResult.isValid());
        assertNotNull(signResult.getSignature());
        assertEquals(SignatureAlgorithm.RSA_SHA256, signResult.getAlgorithm());

        // Verify the signature
        SignatureResult verifyResult = signatureService.verify(message, signResult.getSignature(),
            SignatureAlgorithm.RSA_SHA256, keyInfo);
        assertTrue(verifyResult.isValid());
        assertEquals(SignatureAlgorithm.RSA_SHA256, verifyResult.getAlgorithm());
    }

    @Test
    void testRsaSha512SignAndVerify() throws Exception {
        String message = "Hello, RSA SHA512!";
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        KeyInfo keyInfo = createKeyInfo("test-rsa-sha512", keyPair);

        // Sign the message
        SignatureResult signResult = signatureService.sign(message, SignatureAlgorithm.RSA_SHA512, keyInfo);
        assertTrue(signResult.isValid());
        assertNotNull(signResult.getSignature());
        assertEquals(SignatureAlgorithm.RSA_SHA512, signResult.getAlgorithm());

        // Verify the signature
        SignatureResult verifyResult = signatureService.verify(message, signResult.getSignature(),
            SignatureAlgorithm.RSA_SHA512, keyInfo);
        assertTrue(verifyResult.isValid());
        assertEquals(SignatureAlgorithm.RSA_SHA512, verifyResult.getAlgorithm());
    }

    @Test
    void testEcdsaSha256SignAndVerify() throws Exception {
        String message = "Hello, ECDSA SHA256!";
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC");
        keyPairGen.initialize(new ECGenParameterSpec("secp256r1"));
        KeyPair keyPair = keyPairGen.generateKeyPair();

        KeyInfo keyInfo = createKeyInfo("test-ecdsa-sha256", keyPair);

        // Sign the message
        SignatureResult signResult = signatureService.sign(message, SignatureAlgorithm.ECDSA_SHA256, keyInfo);
        assertTrue(signResult.isValid());
        assertNotNull(signResult.getSignature());
        assertEquals(SignatureAlgorithm.ECDSA_SHA256, signResult.getAlgorithm());

        // Verify the signature
        SignatureResult verifyResult = signatureService.verify(message, signResult.getSignature(),
            SignatureAlgorithm.ECDSA_SHA256, keyInfo);
        assertTrue(verifyResult.isValid());
        assertEquals(SignatureAlgorithm.ECDSA_SHA256, verifyResult.getAlgorithm());
    }

    @Test
    void testEcdsaSha512SignAndVerify() throws Exception {
        String message = "Hello, ECDSA SHA512!";
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC");
        keyPairGen.initialize(new ECGenParameterSpec("secp256r1"));
        KeyPair keyPair = keyPairGen.generateKeyPair();

        KeyInfo keyInfo = createKeyInfo("test-ecdsa-sha512", keyPair);

        // Sign the message
        SignatureResult signResult = signatureService.sign(message, SignatureAlgorithm.ECDSA_SHA512, keyInfo);
        assertTrue(signResult.isValid());
        assertNotNull(signResult.getSignature());
        assertEquals(SignatureAlgorithm.ECDSA_SHA512, signResult.getAlgorithm());

        // Verify the signature
        SignatureResult verifyResult = signatureService.verify(message, signResult.getSignature(),
            SignatureAlgorithm.ECDSA_SHA512, keyInfo);
        assertTrue(verifyResult.isValid());
        assertEquals(SignatureAlgorithm.ECDSA_SHA512, verifyResult.getAlgorithm());
    }

    @Test
    void testEd25519SignAndVerify() throws Exception {
        String message = "Hello, Ed25519!";
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("Ed25519");
        KeyPair keyPair = keyPairGen.generateKeyPair();

        KeyInfo keyInfo = createKeyInfo("test-ed25519", keyPair);

        // Sign the message
        SignatureResult signResult = signatureService.sign(message, SignatureAlgorithm.ED25519, keyInfo);
        assertTrue(signResult.isValid());
        assertNotNull(signResult.getSignature());
        assertEquals(SignatureAlgorithm.ED25519, signResult.getAlgorithm());

        // Verify the signature
        SignatureResult verifyResult = signatureService.verify(message, signResult.getSignature(),
            SignatureAlgorithm.ED25519, keyInfo);
        assertTrue(verifyResult.isValid());
        assertEquals(SignatureAlgorithm.ED25519, verifyResult.getAlgorithm());
    }

    @Test
    void testSignatureTamperingDetection() throws Exception {
        String message = "Original message";
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        KeyInfo keyInfo = createKeyInfo("test-tampering", keyPair);

        // Sign the original message
        SignatureResult signResult = signatureService.sign(message, SignatureAlgorithm.RSA_SHA256, keyInfo);
        assertTrue(signResult.isValid());

        // Try to verify with tampered message
        String tamperedMessage = "Tampered message";
        SignatureResult verifyResult = signatureService.verify(tamperedMessage, signResult.getSignature(),
            SignatureAlgorithm.RSA_SHA256, keyInfo);
        assertFalse(verifyResult.isValid());
        assertTrue(verifyResult.hasErrors());
    }

    @Test
    void testNullInputs() {
        KeyInfo dummyKeyInfo = new KeyInfo.Builder().build();

        // Test null message
        assertThrows(SignatureException.class, () ->
            signatureService.sign((byte[]) null, SignatureAlgorithm.RSA_SHA256, dummyKeyInfo));

        // Test null algorithm
        assertThrows(SignatureException.class, () ->
            signatureService.sign("test".getBytes(), null, dummyKeyInfo));

        // Test null keyInfo
        assertThrows(SignatureException.class, () ->
            signatureService.sign("test".getBytes(), SignatureAlgorithm.RSA_SHA256, null));

        // Test null signature in verify
        assertThrows(SignatureException.class, () ->
            signatureService.verify("test".getBytes(), null, SignatureAlgorithm.RSA_SHA256, dummyKeyInfo));
    }

    @Test
    void testMissingPrivateKey() throws Exception {
        String message = "Test message";
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        // Create KeyInfo with only public key
        KeyInfo keyInfo = new KeyInfo.Builder()
                .keyId("test-missing-private")
                .keyType(null)
                .keyUsages(null)
                .publicKey(keyPair.getPublic())
                .privateKey(null) // No private key
                .creationTime(null)
                .expirationTime(null)
                .lastRotationTime(null)
                .version(null)
                .active(true)
                .description("Test key without private key")
                .algorithm("RSA")
                .keySize(2048)
                .build();

        SignatureResult result = signatureService.sign(message, SignatureAlgorithm.RSA_SHA256, keyInfo);
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().get(0).contains("private key"));
    }

    @Test
    void testMissingPublicKey() throws Exception {
        String message = "Test message";
        String signature = "dummy-signature";
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        // Create KeyInfo with only private key
        KeyInfo keyInfo = new KeyInfo.Builder()
                .keyId("test-missing-public")
                .keyType(null)
                .keyUsages(null)
                .publicKey(null) // No public key
                .privateKey(keyPair.getPrivate())
                .creationTime(null)
                .expirationTime(null)
                .lastRotationTime(null)
                .version(null)
                .active(true)
                .description("Test key without public key")
                .algorithm("RSA")
                .keySize(2048)
                .build();

        SignatureResult result = signatureService.verify(message, signature, SignatureAlgorithm.RSA_SHA256, keyInfo);
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().get(0).contains("public key"));
    }

    @Test
    void testLargeMessageSignAndVerify() throws Exception {
        // Create a large message (1MB)
        StringBuilder largeMessage = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            largeMessage.append("This is a large message for testing signature performance. ");
        }
        String message = largeMessage.toString();

        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        KeyInfo keyInfo = createKeyInfo("test-large-message", keyPair);

        // Sign the large message
        SignatureResult signResult = signatureService.sign(message, SignatureAlgorithm.RSA_SHA256, keyInfo);
        assertTrue(signResult.isValid());
        assertNotNull(signResult.getSignature());

        // Verify the large message
        SignatureResult verifyResult = signatureService.verify(message, signResult.getSignature(),
            SignatureAlgorithm.RSA_SHA256, keyInfo);
        assertTrue(verifyResult.isValid());
    }

    private KeyInfo createKeyInfo(String keyId, KeyPair keyPair) {
        return new KeyInfo.Builder()
                .keyId(keyId)
                .keyType(null)
                .keyUsages(null)
                .publicKey(keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .creationTime(null)
                .expirationTime(null)
                .lastRotationTime(null)
                .version(null)
                .active(true)
                .description("Test key for " + keyId)
                .algorithm(keyPair.getPublic().getAlgorithm())
                .keySize(keyPair.getPublic().getEncoded().length * 8)
                .build();
    }
}
