package com.finqube.iso20022.core.security.signature;

/**
 * Supported digital signature algorithms for ISO 20022 message signing.
 *
 * @author Spring Finqube Team
 */
public enum SignatureAlgorithm {
    RSA_SHA256,
    RSA_SHA512,
    ECDSA_SHA256,
    ECDSA_SHA512,
    ED25519
}
