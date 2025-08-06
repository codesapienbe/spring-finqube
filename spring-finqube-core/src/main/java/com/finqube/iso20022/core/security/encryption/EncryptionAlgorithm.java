package com.finqube.iso20022.core.security.encryption;

/**
 * Supported encryption algorithms for ISO 20022 message encryption.
 *
 * @author Spring Finqube Team
 */
public enum EncryptionAlgorithm {
    AES,
    AES_256_GCM,
    RSA,
    CHACHA20,
    ECIES
}
