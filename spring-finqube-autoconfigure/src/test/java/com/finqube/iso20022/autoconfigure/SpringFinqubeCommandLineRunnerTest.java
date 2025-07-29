package com.finqube.iso20022.autoconfigure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.finqube.iso20022.autoconfigure.properties.SpringFinqubeProperties;
import com.finqube.iso20022.autoconfigure.properties.SpringFinqubeProperties.Security;
import com.finqube.iso20022.autoconfigure.properties.SpringFinqubeProperties.Security.Keystore;
import com.finqube.iso20022.autoconfigure.properties.SpringFinqubeProperties.Validation;

/**
 * Unit tests for SpringFinqubeCommandLineRunner.
 *
 * <p>This test class verifies that the command-line runner correctly logs
 * configuration information and startup messages.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class SpringFinqubeCommandLineRunnerTest {

    @Mock
    private SpringFinqubeProperties properties;

    @Mock
    private Validation validation;

    @Mock
    private Security security;

    @Mock
    private Keystore keystore;

    private SpringFinqubeCommandLineRunner runner;
    private static final String HELLO_WORLD = "Spring Finqube ISO 20022 Starter is ready! 🚀";

    @BeforeEach
    void setUp() {
        // Setup mock behavior
        when(properties.getValidation()).thenReturn(validation);
        when(properties.getSecurity()).thenReturn(security);
        when(security.getKeystore()).thenReturn(keystore);

        runner = new SpringFinqubeCommandLineRunner(properties, HELLO_WORLD);
    }

    @Test
    void shouldRunSuccessfullyWithDefaultConfiguration() throws Exception {
        // Given
        when(properties.isEnabled()).thenReturn(true);
        when(properties.getTransport()).thenReturn("none");
        when(validation.isEnabled()).thenReturn(true);
        when(validation.isFailFast()).thenReturn(true);
        when(keystore.getLocation()).thenReturn(null);

        // When
        runner.run();

        // Then - should not throw any exceptions
        verify(properties).isEnabled();
        verify(properties).getTransport();
        verify(validation).isEnabled();
        verify(validation).isFailFast();
        verify(keystore).getLocation();
    }

    @Test
    void shouldRunSuccessfullyWithCustomConfiguration() throws Exception {
        // Given
        when(properties.isEnabled()).thenReturn(true);
        when(properties.getTransport()).thenReturn("swiftnet");
        when(validation.isEnabled()).thenReturn(false);
        when(validation.isFailFast()).thenReturn(false);
        when(keystore.getLocation()).thenReturn("classpath:keystore.p12");
        when(keystore.getType()).thenReturn("PKCS12");

        // When
        runner.run();

        // Then - should not throw any exceptions
        verify(properties).isEnabled();
        verify(properties).getTransport();
        verify(validation).isEnabled();
        verify(validation).isFailFast();
        verify(keystore).getLocation();
        verify(keystore).getType();
    }

    @Test
    void shouldHandleNullKeystoreLocation() throws Exception {
        // Given
        when(properties.isEnabled()).thenReturn(true);
        when(properties.getTransport()).thenReturn("none");
        when(validation.isEnabled()).thenReturn(true);
        when(validation.isFailFast()).thenReturn(true);
        when(keystore.getLocation()).thenReturn(null);

        // When
        runner.run();

        // Then - should not throw any exceptions
        verify(keystore).getLocation();
        verify(keystore, never()).getType();
    }

    @Test
    void shouldHandleEmptyKeystoreLocation() throws Exception {
        // Given
        when(properties.isEnabled()).thenReturn(true);
        when(properties.getTransport()).thenReturn("none");
        when(validation.isEnabled()).thenReturn(true);
        when(validation.isFailFast()).thenReturn(true);
        when(keystore.getLocation()).thenReturn("");

        // When
        runner.run();

        // Then - should not throw any exceptions
        verify(keystore).getLocation();
        verify(keystore, never()).getType();
    }
}
