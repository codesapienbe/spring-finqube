package com.finqube.iso20022.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.finqube.iso20022.autoconfigure.properties.SpringFinqubeProperties;

/**
 * Integration tests for SpringFinqubeAutoConfiguration.
 *
 * <p>This test class verifies that the autoconfiguration works correctly
 * under different conditions and configuration scenarios.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
class SpringFinqubeAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SpringFinqubeAutoConfiguration.class));

    @Test
    void shouldLoadAutoConfigurationWhenEnabled() {
        contextRunner
                .withPropertyValues("iso20022.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(SpringFinqubeProperties.class);
                    assertThat(context).hasSingleBean(String.class);
                    assertThat(context.getBean(String.class)).isEqualTo("Spring Finqube ISO 20022 Starter is ready! 🚀");
                });
    }

    @Test
    void shouldLoadAutoConfigurationByDefault() {
        contextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(SpringFinqubeProperties.class);
                    assertThat(context).hasSingleBean(String.class);
                });
    }

    @Test
    void shouldNotLoadAutoConfigurationWhenDisabled() {
        contextRunner
                .withPropertyValues("iso20022.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(SpringFinqubeProperties.class);
                    assertThat(context).doesNotHaveBean(String.class);
                });
    }

    @Test
    void shouldBindConfigurationProperties() {
        contextRunner
                .withPropertyValues(
                        "iso20022.transport=swiftnet",
                        "iso20022.validation.enabled=false",
                        "iso20022.validation.fail-fast=false",
                        "iso20022.security.keystore.location=classpath:test-keystore.p12",
                        "iso20022.security.keystore.password=test-password",
                        "iso20022.security.keystore.type=JKS"
                )
                .run(context -> {
                    SpringFinqubeProperties properties = context.getBean(SpringFinqubeProperties.class);

                    assertThat(properties.isEnabled()).isTrue();
                    assertThat(properties.getTransport()).isEqualTo("swiftnet");
                    assertThat(properties.getValidation().isEnabled()).isFalse();
                    assertThat(properties.getValidation().isFailFast()).isFalse();
                    assertThat(properties.getSecurity().getKeystore().getLocation()).isEqualTo("classpath:test-keystore.p12");
                    assertThat(properties.getSecurity().getKeystore().getPassword()).isEqualTo("test-password");
                    assertThat(properties.getSecurity().getKeystore().getType()).isEqualTo("JKS");
                });
    }

    @Test
    void shouldUseDefaultValuesWhenNoPropertiesSet() {
        contextRunner
                .run(context -> {
                    SpringFinqubeProperties properties = context.getBean(SpringFinqubeProperties.class);

                    assertThat(properties.isEnabled()).isTrue();
                    assertThat(properties.getTransport()).isEqualTo("none");
                    assertThat(properties.getValidation().isEnabled()).isTrue();
                    assertThat(properties.getValidation().isFailFast()).isTrue();
                    assertThat(properties.getSecurity().getKeystore().getType()).isEqualTo("PKCS12");
                });
    }

    @Test
    void shouldWorkWithCustomConfiguration() {
        contextRunner
                .withUserConfiguration(CustomConfiguration.class)
                .withPropertyValues("iso20022.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(SpringFinqubeProperties.class);
                    assertThat(context).hasSingleBean(String.class);
                    assertThat(context).hasSingleBean(CustomBean.class);
                });
    }

    @Configuration
    static class CustomConfiguration {

        @Bean
        public CustomBean customBean() {
            return new CustomBean();
        }
    }

    static class CustomBean {
        // Test bean
    }
}
