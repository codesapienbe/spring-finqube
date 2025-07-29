package com.finqube.iso20022.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.finqube.iso20022.core.template.Iso20022Template;

/**
 * Integration tests for Iso20022Template autowiring.
 *
 * <p>This test class verifies that the Iso20022Template can be properly
 * autowired in a Spring Boot application context.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
class Iso20022TemplateIntegrationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SpringFinqubeAutoConfiguration.class));

    @Test
    void shouldAutowireIso20022Template() {
        contextRunner
                .withUserConfiguration(TestConfiguration.class)
                .withPropertyValues("iso20022.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(Iso20022Template.class);

                    Iso20022Template template = context.getBean(Iso20022Template.class);
                    assertThat(template).isNotNull();
                    assertThat(template.getTemplateId()).isNotNull();
                    assertThat(template.getTemplateId()).startsWith("template-");
                });
    }

    @Test
    void shouldNotAutowireIso20022TemplateWhenDisabled() {
        contextRunner
                .withUserConfiguration(TestConfiguration.class)
                .withPropertyValues("iso20022.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(Iso20022Template.class);
                });
    }

    @Test
    void shouldAutowireIso20022TemplateByDefault() {
        contextRunner
                .withUserConfiguration(TestConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(Iso20022Template.class);
                });
    }

    @Test
    void shouldInjectTemplateIntoService() {
        contextRunner
                .withUserConfiguration(TestConfiguration.class)
                .withPropertyValues("iso20022.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(TestService.class);

                    TestService service = context.getBean(TestService.class);
                    assertThat(service.getTemplate()).isNotNull();
                    assertThat(service.getTemplate()).isInstanceOf(Iso20022Template.class);
                });
    }

    @Configuration
    static class TestConfiguration {

        @Bean
        public TestService testService(Iso20022Template template) {
            return new TestService(template);
        }
    }

    static class TestService {
        private final Iso20022Template template;

        public TestService(Iso20022Template template) {
            this.template = template;
        }

        public Iso20022Template getTemplate() {
            return template;
        }
    }
}