package com.finqube.iso20022.core.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.finqube.iso20022.core.message.BaseMessage;
import com.finqube.iso20022.core.message.MessagePriority;
import com.finqube.iso20022.core.message.pain.Pain001Message;
import com.finqube.iso20022.core.security.impl.DefaultSecurityManager;

/**
 * Unit tests for DefaultSecurityManager.
 *
 * <p>This test class validates the security manager functionality,
 * including signing, encryption, verification, and decryption operations.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@DisplayName("DefaultSecurityManager Tests")
class DefaultSecurityManagerTest {

    private DefaultSecurityManager securityManager;
    private BaseMessage testMessage;
    private byte[] testCertificate;

    @BeforeEach
    void setUp() {
        securityManager = new DefaultSecurityManager();
        testMessage = createValidMessage();
        testCertificate = "TEST_CERTIFICATE_DATA".getBytes();
    }

    @Test
    @DisplayName("Should sign message successfully")
    void shouldSignMessageSuccessfully() throws SecurityException {
        // When
        SignedMessage signedMessage = securityManager.sign(testMessage);

        // Then
        assertThat(signedMessage).isNotNull();
        assertThat(signedMessage.getMessageId()).isEqualTo(testMessage.getMessageId());
        assertThat(signedMessage.getSignature()).isNotNull();
        assertThat(signedMessage.getSignatureAlgorithm()).isEqualTo("SHA256withRSA");
        assertThat(signedMessage.hasSignerCertificate()).isTrue();
        assertThat(signedMessage.getSignedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should sign message asynchronously successfully")
    void shouldSignMessageAsynchronouslySuccessfully() throws Exception {
        // When
        CompletableFuture<SignedMessage> future = securityManager.signAsync(testMessage);
        SignedMessage signedMessage = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(signedMessage).isNotNull();
        assertThat(signedMessage.getMessageId()).isEqualTo(testMessage.getMessageId());
        assertThat(signedMessage.getSignature()).isNotNull();
    }

    @Test
    @DisplayName("Should verify signature successfully")
    void shouldVerifySignatureSuccessfully() throws SecurityException {
        // Given
        SignedMessage signedMessage = securityManager.sign(testMessage);

        // When
        SignatureVerificationResult result = securityManager.verifySignature(signedMessage);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isTrue();
        assertThat(result.getMessageId()).isEqualTo(testMessage.getMessageId());
        assertThat(result.getVerifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should verify signature asynchronously successfully")
    void shouldVerifySignatureAsynchronouslySuccessfully() throws Exception {
        // Given
        SignedMessage signedMessage = securityManager.sign(testMessage);

        // When
        CompletableFuture<SignatureVerificationResult> future = securityManager.verifySignatureAsync(signedMessage);
        SignatureVerificationResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isTrue();
    }

    @Test
    @DisplayName("Should encrypt message successfully")
    void shouldEncryptMessageSuccessfully() throws SecurityException {
        // When
        EncryptedMessage encryptedMessage = securityManager.encrypt(testMessage, testCertificate);

        // Then
        assertThat(encryptedMessage).isNotNull();
        assertThat(encryptedMessage.getMessageId()).isEqualTo(testMessage.getMessageId());
        assertThat(encryptedMessage.getEncryptedContent()).isNotNull();
        assertThat(encryptedMessage.getEncryptedKey()).isNotNull();
        assertThat(encryptedMessage.getEncryptionAlgorithm()).isEqualTo("AES-256");
        assertThat(encryptedMessage.getKeyEncryptionAlgorithm()).isEqualTo("RSA");
        assertThat(encryptedMessage.getEncryptedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should encrypt message asynchronously successfully")
    void shouldEncryptMessageAsynchronouslySuccessfully() throws Exception {
        // When
        CompletableFuture<EncryptedMessage> future = securityManager.encryptAsync(testMessage, testCertificate);
        EncryptedMessage encryptedMessage = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(encryptedMessage).isNotNull();
        assertThat(encryptedMessage.getMessageId()).isEqualTo(testMessage.getMessageId());
    }

    @Test
    @DisplayName("Should decrypt message successfully")
    void shouldDecryptMessageSuccessfully() throws SecurityException {
        // Given
        EncryptedMessage encryptedMessage = securityManager.encrypt(testMessage, testCertificate);

        // When
        BaseMessage decryptedMessage = securityManager.decrypt(encryptedMessage);

        // Then
        assertThat(decryptedMessage).isNotNull();
        assertThat(decryptedMessage.getMessageId()).isEqualTo(testMessage.getMessageId());
        assertThat(decryptedMessage.getMessageType()).isEqualTo("decrypted");
    }

    @Test
    @DisplayName("Should decrypt message asynchronously successfully")
    void shouldDecryptMessageAsynchronouslySuccessfully() throws Exception {
        // Given
        EncryptedMessage encryptedMessage = securityManager.encrypt(testMessage, testCertificate);

        // When
        CompletableFuture<BaseMessage> future = securityManager.decryptAsync(encryptedMessage);
        BaseMessage decryptedMessage = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(decryptedMessage).isNotNull();
        assertThat(decryptedMessage.getMessageId()).isEqualTo(testMessage.getMessageId());
    }

    @Test
    @DisplayName("Should sign and encrypt message successfully")
    void shouldSignAndEncryptMessageSuccessfully() throws SecurityException {
        // When
        SecureMessage secureMessage = securityManager.signAndEncrypt(testMessage, testCertificate);

        // Then
        assertThat(secureMessage).isNotNull();
        assertThat(secureMessage.getMessageId()).isEqualTo(testMessage.getMessageId());
        assertThat(secureMessage.getEncryptedContent()).isNotNull();
        assertThat(secureMessage.getEncryptedKey()).isNotNull();
        assertThat(secureMessage.getSignature()).isNotNull();
        assertThat(secureMessage.getEncryptionAlgorithm()).isEqualTo("AES-256");
        assertThat(secureMessage.getKeyEncryptionAlgorithm()).isEqualTo("RSA");
        assertThat(secureMessage.getSignatureAlgorithm()).isEqualTo("SHA256withRSA");
        assertThat(secureMessage.getSecuredAt()).isNotNull();
    }

    @Test
    @DisplayName("Should sign and encrypt message asynchronously successfully")
    void shouldSignAndEncryptMessageAsynchronouslySuccessfully() throws Exception {
        // When
        CompletableFuture<SecureMessage> future = securityManager.signAndEncryptAsync(testMessage, testCertificate);
        SecureMessage secureMessage = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(secureMessage).isNotNull();
        assertThat(secureMessage.getMessageId()).isEqualTo(testMessage.getMessageId());
    }

    @Test
    @DisplayName("Should verify and decrypt message successfully")
    void shouldVerifyAndDecryptMessageSuccessfully() throws SecurityException {
        // Given
        SecureMessage secureMessage = securityManager.signAndEncrypt(testMessage, testCertificate);

        // When
        SecureMessageResult result = securityManager.verifyAndDecrypt(secureMessage);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getMessageId()).isEqualTo(testMessage.getMessageId());
        assertThat(result.getDecryptedMessage()).isNotNull();
        assertThat(result.getSignatureVerification()).isNotNull();
        assertThat(result.isSignatureValid()).isTrue();
        assertThat(result.getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should verify and decrypt message asynchronously successfully")
    void shouldVerifyAndDecryptMessageAsynchronouslySuccessfully() throws Exception {
        // Given
        SecureMessage secureMessage = securityManager.signAndEncrypt(testMessage, testCertificate);

        // When
        CompletableFuture<SecureMessageResult> future = securityManager.verifyAndDecryptAsync(secureMessage);
        SecureMessageResult result = future.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccessful()).isTrue();
    }

    @Test
    @DisplayName("Should provide security manager information")
    void shouldProvideSecurityManagerInformation() {
        // When & Then
        assertThat(securityManager.getSecurityManagerId()).isEqualTo("default-security");
        assertThat(securityManager.getDisplayName()).isEqualTo("Default Security Manager");
        assertThat(securityManager.getVersion()).isEqualTo("1.0");
        assertThat(securityManager.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Should provide security statistics")
    void shouldProvideSecurityStatistics() throws SecurityException {
        // Given
        securityManager.sign(testMessage);
        securityManager.encrypt(testMessage, testCertificate);

        // When
        SecurityStatistics stats = securityManager.getStatistics();

        // Then
        assertThat(stats.getTotalOperations()).isEqualTo(2);
        assertThat(stats.getSuccessfulOperations()).isEqualTo(2);
        assertThat(stats.getFailedOperations()).isEqualTo(0);
        assertThat(stats.getSuccessRate()).isEqualTo(100.0);
        assertThat(stats.getAverageProcessingTimeMillis()).isPositive();
        assertThat(stats.getOperationTypeCounts()).containsKeys("SIGN", "ENCRYPT");
    }

    @Test
    @DisplayName("Should provide health check information")
    void shouldProvideHealthCheckInformation() {
        // When
        SecurityHealthCheck healthCheck = securityManager.healthCheck();

        // Then
        assertThat(healthCheck.getSecurityManagerId()).isEqualTo("default-security");
        assertThat(healthCheck.isHealthy()).isTrue();
        assertThat(healthCheck.getComponents()).isNotEmpty();
        assertThat(healthCheck.getResponseTimeMillis()).isPositive();
    }

    @Test
    @DisplayName("Should throw exception for null message in sign")
    void shouldThrowExceptionForNullMessageInSign() {
        // When & Then
        assertThatThrownBy(() -> securityManager.sign(null))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("Message cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null message in encrypt")
    void shouldThrowExceptionForNullMessageInEncrypt() {
        // When & Then
        assertThatThrownBy(() -> securityManager.encrypt(null, testCertificate))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("Message cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null certificate in encrypt")
    void shouldThrowExceptionForNullCertificateInEncrypt() {
        // When & Then
        assertThatThrownBy(() -> securityManager.encrypt(testMessage, null))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("Recipient certificate cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null signed message in verify")
    void shouldThrowExceptionForNullSignedMessageInVerify() {
        // When & Then
        assertThatThrownBy(() -> securityManager.verifySignature(null))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("Signed message cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null encrypted message in decrypt")
    void shouldThrowExceptionForNullEncryptedMessageInDecrypt() {
        // When & Then
        assertThatThrownBy(() -> securityManager.decrypt(null))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("Encrypted message cannot be null");
    }

    @Test
    @DisplayName("Should handle availability changes")
    void shouldHandleAvailabilityChanges() {
        // Given
        assertThat(securityManager.isAvailable()).isTrue();

        // When
        securityManager.setAvailable(false);

        // Then
        assertThat(securityManager.isAvailable()).isFalse();

        SecurityHealthCheck healthCheck = securityManager.healthCheck();
        assertThat(healthCheck.isHealthy()).isFalse();
    }

    @Test
    @DisplayName("Should return null key store for default implementation")
    void shouldReturnNullKeyStoreForDefaultImplementation() {
        // When & Then
        assertThat(securityManager.getKeyStore()).isNull();
    }

    // Helper methods
    private BaseMessage createValidMessage() {
        return new Pain001Message("SEC001", List.of(), 1, 1000.00) {
            @Override
            public String getMessageType() {
                return "pain.001";
            }

            @Override
            public String getBusinessProcess() {
                return "pain";
            }

            @Override
            public MessagePriority getPriority() {
                return MessagePriority.NORMAL;
            }

            @Override
            public String getDescription() {
                return "Test secure payment message";
            }
        };
    }
}
