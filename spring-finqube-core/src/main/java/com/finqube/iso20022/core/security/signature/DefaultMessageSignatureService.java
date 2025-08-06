package com.finqube.iso20022.core.security.signature;

import com.finqube.iso20022.core.security.key.KeyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Default implementation of MessageSignatureService supporting multiple signature algorithms.
 *
 * @author Spring Finqube Team
 */
public class DefaultMessageSignatureService implements MessageSignatureService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMessageSignatureService.class);

    @Override
    public SignatureResult sign(byte[] message, SignatureAlgorithm algorithm, KeyInfo keyInfo) throws SignatureException {
        if (message == null || algorithm == null || keyInfo == null) {
            throw new SignatureException("Null input to sign", "sign", algorithm != null ? algorithm.name() : null);
        }

        long startTime = System.currentTimeMillis();
        logger.debug("Signing message with algorithm: {}", algorithm);

        try {
            String signature = performSigning(message, algorithm, keyInfo);
            long duration = System.currentTimeMillis() - startTime;

            logger.debug("Message signed successfully with algorithm: {} (took {}ms)", algorithm, duration);
            return SignatureResult.success(algorithm, signature, keyInfo.getKeyId(), duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Failed to sign message with algorithm: {}", algorithm, e);

            List<String> errors = new ArrayList<>();
            errors.add("Signing failed: " + e.getMessage());

            return SignatureResult.failure(algorithm, keyInfo.getKeyId(), duration, errors);
        }
    }

    @Override
    public SignatureResult verify(byte[] message, String signature, SignatureAlgorithm algorithm, KeyInfo keyInfo) throws SignatureException {
        if (message == null || signature == null || algorithm == null || keyInfo == null) {
            throw new SignatureException("Null input to verify", "verify", algorithm != null ? algorithm.name() : null);
        }

        long startTime = System.currentTimeMillis();
        logger.debug("Verifying signature with algorithm: {}", algorithm);

        try {
            boolean isValid = performVerification(message, signature, algorithm, keyInfo);
            long duration = System.currentTimeMillis() - startTime;

            if (isValid) {
                logger.debug("Signature verification successful with algorithm: {} (took {}ms)", algorithm, duration);
                return SignatureResult.success(algorithm, signature, keyInfo.getKeyId(), duration);
            } else {
                logger.warn("Signature verification failed with algorithm: {} (took {}ms)", algorithm, duration);
                List<String> errors = new ArrayList<>();
                errors.add("Signature verification failed: Invalid signature");
                return SignatureResult.failure(algorithm, keyInfo.getKeyId(), duration, errors);
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Failed to verify signature with algorithm: {}", algorithm, e);

            List<String> errors = new ArrayList<>();
            errors.add("Verification failed: " + e.getMessage());

            return SignatureResult.failure(algorithm, keyInfo.getKeyId(), duration, errors);
        }
    }

    private String performSigning(byte[] message, SignatureAlgorithm algorithm, KeyInfo keyInfo) throws Exception {
        PrivateKey privateKey = keyInfo.getPrivateKey();
        if (privateKey == null) {
            throw new SignatureException("KeyInfo does not contain a private key", "sign", algorithm.name());
        }

        Signature signature = getSignatureInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(message);
        byte[] signatureBytes = signature.sign();

        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    private boolean performVerification(byte[] message, String signatureString, SignatureAlgorithm algorithm, KeyInfo keyInfo) throws Exception {
        PublicKey publicKey = keyInfo.getPublicKey();
        if (publicKey == null) {
            throw new SignatureException("KeyInfo does not contain a public key", "verify", algorithm.name());
        }

        byte[] signatureBytes = Base64.getDecoder().decode(signatureString);
        Signature signature = getSignatureInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(message);

        return signature.verify(signatureBytes);
    }

    private Signature getSignatureInstance(SignatureAlgorithm algorithm) throws NoSuchAlgorithmException {
        String algorithmName = switch (algorithm) {
            case RSA_SHA256 -> "SHA256withRSA";
            case RSA_SHA512 -> "SHA512withRSA";
            case ECDSA_SHA256 -> "SHA256withECDSA";
            case ECDSA_SHA512 -> "SHA512withECDSA";
            case ED25519 -> "Ed25519";
        };

        return Signature.getInstance(algorithmName);
    }
}
