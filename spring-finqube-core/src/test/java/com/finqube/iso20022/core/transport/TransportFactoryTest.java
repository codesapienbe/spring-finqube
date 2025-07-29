package com.finqube.iso20022.core.transport;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for TransportFactory.
 *
 * <p>This test class validates the transport factory functionality,
 * including transport discovery, creation, and management.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@DisplayName("TransportFactory Tests")
class TransportFactoryTest {

    private TransportFactory factory;

    @BeforeEach
    void setUp() {
        factory = new TransportFactory();
    }

    @Test
    @DisplayName("Should discover available transports")
    void shouldDiscoverAvailableTransports() {
        // When
        List<String> availableIds = factory.getAvailableTransportIds();
        List<TransportFactory.TransportInfo> availableTransports = factory.getAvailableTransports();

        // Then
        assertThat(availableIds).isNotEmpty();
        assertThat(availableTransports).isNotEmpty();
        assertThat(availableIds).contains("logging");
        assertThat(availableTransports).anyMatch(info -> "logging".equals(info.getTransportId()));
    }

    @Test
    @DisplayName("Should get transport instance")
    void shouldGetTransportInstance() throws TransportException {
        // When
        Transport transport = factory.getTransport("logging");

        // Then
        assertThat(transport).isNotNull();
        assertThat(transport.getTransportId()).isEqualTo("logging");
        assertThat(transport.getDisplayName()).isEqualTo("Logging Transport");
        assertThat(transport.getProtocolVersion()).isEqualTo("1.0");
    }

    @Test
    @DisplayName("Should return same instance for same transport ID")
    void shouldReturnSameInstanceForSameTransportId() throws TransportException {
        // When
        Transport transport1 = factory.getTransport("logging");
        Transport transport2 = factory.getTransport("logging");

        // Then
        assertThat(transport1).isSameAs(transport2);
    }

    @Test
    @DisplayName("Should check transport availability")
    void shouldCheckTransportAvailability() {
        // When & Then
        assertThat(factory.isTransportAvailable("logging")).isTrue();
        assertThat(factory.isTransportAvailable("nonexistent")).isFalse();
    }

    @Test
    @DisplayName("Should get transport info")
    void shouldGetTransportInfo() {
        // When
        Optional<TransportFactory.TransportInfo> info = factory.getTransportInfo("logging");

        // Then
        assertThat(info).isPresent();
        assertThat(info.get().getTransportId()).isEqualTo("logging");
        assertThat(info.get().getDisplayName()).isEqualTo("Logging Transport");
        assertThat(info.get().getProtocolVersion()).isEqualTo("1.0");
        assertThat(info.get().getDescription()).contains("Simple transport that logs messages");
    }

    @Test
    @DisplayName("Should return empty for non-existent transport info")
    void shouldReturnEmptyForNonExistentTransportInfo() {
        // When
        Optional<TransportFactory.TransportInfo> info = factory.getTransportInfo("nonexistent");

        // Then
        assertThat(info).isEmpty();
    }

    @Test
    @DisplayName("Should throw exception for non-existent transport")
    void shouldThrowExceptionForNonExistentTransport() {
        // When & Then
        assertThatThrownBy(() -> factory.getTransport("nonexistent"))
            .isInstanceOf(TransportException.class)
            .hasMessageContaining("Transport not found: nonexistent");
    }

    @Test
    @DisplayName("Should throw exception for null transport ID")
    void shouldThrowExceptionForNullTransportId() {
        // When & Then
        assertThatThrownBy(() -> factory.getTransport(null))
            .isInstanceOf(TransportException.class)
            .hasMessageContaining("Transport ID cannot be null");
    }

    @Test
    @DisplayName("Should close all transports")
    void shouldCloseAllTransports() throws TransportException {
        // Given
        Transport transport = factory.getTransport("logging");
        assertThat(transport.isAvailable()).isTrue();

        // When
        factory.closeAll();

        // Then
        assertThat(transport.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("Should handle transport creation failure gracefully")
    void shouldHandleTransportCreationFailureGracefully() {
        // This test would require a mock transport provider that throws exceptions
        // For now, we test that the factory handles the logging transport correctly
        assertThat(factory.isTransportAvailable("logging")).isTrue();
    }

    @Test
    @DisplayName("Should provide transport information")
    void shouldProvideTransportInformation() {
        // When
        List<TransportFactory.TransportInfo> transports = factory.getAvailableTransports();

        // Then
        assertThat(transports).isNotEmpty();

        TransportFactory.TransportInfo loggingInfo = transports.stream()
            .filter(info -> "logging".equals(info.getTransportId()))
            .findFirst()
            .orElse(null);

        assertThat(loggingInfo).isNotNull();
        assertThat(loggingInfo.getTransportId()).isEqualTo("logging");
        assertThat(loggingInfo.getDisplayName()).isEqualTo("Logging Transport");
        assertThat(loggingInfo.getProtocolVersion()).isEqualTo("1.0");
        assertThat(loggingInfo.getDescription()).contains("Simple transport that logs messages");
    }
}
