package com.finqube.iso20022.core.tracing;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenTelemetry distributed tracing.
 *
 * <p>This configuration sets up OpenTelemetry with OTLP exporter
 * for distributed tracing across the Spring Finqube ISO 20022 system.</p>
 *
 * @author Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@Configuration
public class OpenTelemetryConfiguration {

    @Value("${opentelemetry.service.name:spring-finqube-iso20022}")
    private String serviceName;

    @Value("${opentelemetry.service.version:0.1.0}")
    private String serviceVersion;

    @Value("${opentelemetry.otlp.endpoint:http://localhost:4317}")
    private String otlpEndpoint;

    @Value("${opentelemetry.sampler.probability:1.0}")
    private double samplerProbability;

    @Value("${opentelemetry.enabled:true}")
    private boolean enabled;

    /**
     * Creates the OpenTelemetry SDK instance.
     *
     * @return the OpenTelemetry instance
     */
    @Bean
    public OpenTelemetry openTelemetry() {
        if (!enabled) {
            return OpenTelemetry.noop();
        }

        Resource resource = Resource.getDefault()
                .merge(Resource.create(Attributes.builder()
                        .put("service.name", serviceName)
                        .put("service.version", serviceVersion)
                        .put("deployment.environment", "production")
                        .build()));

        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(otlpExporter()).build())
                .setResource(resource)
                .setSampler(Sampler.traceIdRatioBased(samplerProbability))
                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
    }

    /**
     * Creates the OTLP exporter.
     *
     * @return the OTLP exporter
     */
    @Bean
    public OtlpGrpcSpanExporter otlpExporter() {
        return OtlpGrpcSpanExporter.builder()
                .setEndpoint(otlpEndpoint)
                .build();
    }

    /**
     * Creates the OpenTelemetry tracer.
     *
     * @param openTelemetry the OpenTelemetry instance
     * @return the OpenTelemetry tracer
     */
    @Bean
    public OpenTelemetryTracer openTelemetryTracer(OpenTelemetry openTelemetry) {
        return new OpenTelemetryTracer(openTelemetry, serviceName, serviceVersion);
    }
}
