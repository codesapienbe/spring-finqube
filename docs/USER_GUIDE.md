# Spring Finqube ISO 20022 User Guide

## Introduction

Welcome to the Spring Finqube ISO 20022 User Guide! This guide will help you understand and use the Spring Finqube framework to process ISO 20022 messages in your Java applications.

## Table of Contents

1. [Quick Start](#quick-start)
2. [Core Concepts](#core-concepts)
3. [Message Processing](#message-processing)
4. [Security Features](#security-features)
5. [Translation Capabilities](#translation-capabilities)
6. [Transport Layer](#transport-layer)
7. [Monitoring & Observability](#monitoring--observability)
8. [Configuration Guide](#configuration-guide)
9. [Troubleshooting](#troubleshooting)
10. [Best Practices](#best-practices)

## Quick Start

### Prerequisites

- Java 17 or higher
- Spring Boot 3.x
- Maven or Gradle

### Installation

Add the Spring Finqube dependency to your project:

```xml
<dependency>
    <groupId>com.finqube</groupId>
    <artifactId>spring-finqube-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Basic Example

```java
@SpringBootApplication
public class MyApplication {
    
    @Autowired
    private Iso20022Template template;
    
    @Autowired
    private MessageValidator validator;
    
    public void processPayment() {
        // Create a payment message
        Pain001Message message = new Pain001Message("MSG001", List.of("TXN001"), 1, 1000.00) {
            @Override
            public String getMessageType() { return "pain.001"; }
            @Override
            public String getBusinessProcess() { return "pain"; }
            @Override
            public MessagePriority getPriority() { return MessagePriority.HIGH; }
            @Override
            public String getDescription() { return "Payment instruction"; }
        };
        
        // Validate the message
        ValidationResult result = validator.validate(message);
        if (result.isValid()) {
            // Generate XML
            String xml = template.generateXml(message);
            System.out.println("Generated XML: " + xml);
        }
    }
}
```

## Core Concepts

### Message Models

Spring Finqube provides a flexible message model system:

- **BaseMessage**: Abstract base class for all ISO 20022 messages
- **Pain001Message**: Implementation for PAIN.001 payment initiation messages
- **MessagePriority**: Enumeration for message priority levels (LOW, NORMAL, HIGH, URGENT)

### Template System

The template system generates XML from message objects:

```java
@Autowired
private Iso20022Template template;

// Basic XML generation
String xml = template.generateXml(message);

// XML generation with options
TemplateOptions options = TemplateOptions.builder()
    .formatOutput(true)
    .includeSchemaLocation(true)
    .build();
String formattedXml = template.generateXml(message, options);
```

### Validation System

The validation system ensures message compliance:

```java
@Autowired
private MessageValidator validator;

ValidationResult result = validator.validate(message);
if (result.isValid()) {
    System.out.println("Message is valid");
} else {
    result.getErrors().forEach(error -> 
        System.err.println("Error: " + error.getMessage()));
}
```

## Message Processing

### Asynchronous Processing

For high-performance message processing:

```java
@Autowired
private AsyncMessageProcessor asyncProcessor;

// Process message asynchronously
CompletableFuture<Void> future = asyncProcessor.processAsync(message);

// Wait for completion
future.get(30, TimeUnit.SECONDS);

// Get processing statistics
ProcessingStatistics stats = asyncProcessor.getStatistics();
System.out.println("Success rate: " + stats.getSuccessRate() + "%");
```

### Message Priority

Configure message processing priority:

```java
public enum MessagePriority {
    LOW,      // Background processing
    NORMAL,   // Standard processing
    HIGH,     // Priority processing
    URGENT    // Immediate processing
}

// Use in message creation
Pain001Message message = new Pain001Message("MSG001", List.of("TXN001"), 1, 1000.00) {
    @Override
    public MessagePriority getPriority() {
        return MessagePriority.HIGH;
    }
    // ... other methods
};
```

## Security Features

### Digital Signatures

Sign and verify messages:

```java
@Autowired
private SecurityManager securityManager;

// Sign a message
SignedMessage signedMessage = securityManager.sign(message);

// Verify signature
SignatureVerificationResult result = securityManager.verifySignature(signedMessage);
if (result.isValid()) {
    System.out.println("Signature is valid");
}
```

### Message Encryption

Encrypt and decrypt messages:

```java
// Encrypt message
byte[] recipientCertificate = loadCertificate();
EncryptedMessage encryptedMessage = securityManager.encrypt(message, recipientCertificate);

// Decrypt message
BaseMessage decryptedMessage = securityManager.decrypt(encryptedMessage);
```

### Combined Operations

Sign and encrypt in one operation:

```java
// Sign and encrypt
SecureMessage secureMessage = securityManager.signAndEncrypt(message, recipientCertificate);

// Verify and decrypt
SecureMessageResult result = securityManager.verifyAndDecrypt(secureMessage);
if (result.isSuccessful() && result.isSignatureValid()) {
    BaseMessage decryptedMessage = result.getDecryptedMessage();
}
```

## Translation Capabilities

### Format Conversion

Convert messages between different formats:

```java
@Autowired
private TranslationManager translationManager;

// Translate MT103 to MX103
TranslationResult result = translationManager.translate(message, "MT103", "MX103");

if (result.isSuccessful()) {
    BaseMessage translatedMessage = result.getTranslatedMessage();
    System.out.println("Translation successful");
}
```

### Translation Options

Configure translation behavior:

```java
TranslationOptions options = TranslationOptions.builder()
    .enableCaching(true)        // Enable result caching
    .enableValidation(true)     // Enable validation
    .enableWarnings(true)       // Enable warnings
    .timeoutMillis(30000)       // Set timeout
    .build();

TranslationResult result = translationManager.translate(message, "MT103", "MX103", options);
```

### Supported Formats

- **MT103**: SWIFT MT103 format
- **MT202**: SWIFT MT202 format
- **MX103**: ISO 20022 MX103 format
- **MX202**: ISO 20022 MX202 format
- **JSON**: JSON format
- **CSV**: CSV format

## Transport Layer

### Available Transports

Spring Finqube supports multiple transport mechanisms:

```java
@Autowired
private TransportFactory transportFactory;

// Get available transports
List<Transport> transports = transportFactory.getAvailableTransports();

// Send message using specific transport
Transport transport = transportFactory.getTransport("swift");
TransportResponse response = transport.send(message);

if (response.getStatus() == TransportStatus.SUCCESS) {
    System.out.println("Message sent successfully");
} else {
    System.err.println("Send failed: " + response.getMessage());
}
```

### Transport Types

- **SWIFT**: SWIFT network transport
- **SEPA**: SEPA network transport
- **FTP**: File transfer protocol
- **HTTP**: HTTP/REST transport
- **Logging**: Logging transport (for development)

## Monitoring & Observability

### Metrics Collection

Record application metrics:

```java
@Autowired
private MonitoringManager monitoringManager;

// Record various metrics
monitoringManager.recordMetric("message.processing.time", 150.5, Map.of("type", "pain.001"));
monitoringManager.incrementCounter("messages.processed", Map.of("type", "pain.001"));
monitoringManager.recordTiming("validation.time", 25, Map.of("type", "pain.001"));
monitoringManager.recordEvent("message.sent", Map.of("messageId", "MSG001"), Map.of("type", "pain.001"));
```

### System Health

Monitor system health:

```java
// Get system health
SystemHealth health = monitoringManager.getSystemHealth();
System.out.println("System health: " + health.getStatus());
System.out.println("Health percentage: " + health.getHealthPercentage() + "%");

// Get component health
List<ComponentHealth> components = monitoringManager.getComponentHealth();
components.forEach(component -> 
    System.out.println(component.getComponentName() + ": " + component.getStatus()));
```

### Performance Metrics

Monitor performance:

```java
// Get current performance metrics
PerformanceMetrics metrics = monitoringManager.getCurrentPerformanceMetrics();
System.out.println("Success rate: " + metrics.getSuccessRate() + "%");
System.out.println("Average response time: " + metrics.getAverageResponseTimeMillis() + "ms");
System.out.println("Requests per second: " + metrics.getRequestsPerSecond());
```

## Configuration Guide

### Application Properties

Configure Spring Finqube using application properties:

```properties
# Core configuration
spring.finqube.enabled=true
spring.finqube.template.enabled=true
spring.finqube.validation.enabled=true

# Async processing
spring.finqube.async.enabled=true
spring.finqube.async.core-pool-size=10
spring.finqube.async.max-pool-size=50
spring.finqube.async.queue-capacity=100

# Security
spring.finqube.security.enabled=true
spring.finqube.security.signature-algorithm=SHA256withRSA
spring.finqube.security.encryption-algorithm=AES-256

# Translation
spring.finqube.translation.enabled=true
spring.finqube.translation.cache-enabled=true
spring.finqube.translation.cache-size=1000

# Transport
spring.finqube.transport.enabled=true
spring.finqube.transport.default-transport=logging

# Monitoring
spring.finqube.monitoring.enabled=true
spring.finqube.monitoring.metrics-enabled=true
spring.finqube.monitoring.health-check-enabled=true
```

### Configuration Classes

Create custom configuration:

```java
@Configuration
@EnableConfigurationProperties(SpringFinqubeProperties.class)
public class FinqubeConfiguration {
    
    @Bean
    @ConditionalOnProperty(name = "spring.finqube.async.enabled", havingValue = "true")
    public AsyncMessageProcessor asyncMessageProcessor(SpringFinqubeProperties properties) {
        return new DefaultAsyncMessageProcessor(properties.getAsync());
    }
    
    @Bean
    @ConditionalOnProperty(name = "spring.finqube.security.enabled", havingValue = "true")
    public SecurityManager securityManager(SpringFinqubeProperties properties) {
        return new DefaultSecurityManager(properties.getSecurity());
    }
}
```

## Troubleshooting

### Common Issues

#### Message Validation Failures

**Problem**: Messages fail validation
**Solution**: 
- Check message structure and required fields
- Verify business rules compliance
- Review validation error messages

```java
ValidationResult result = validator.validate(message);
if (!result.isValid()) {
    result.getErrors().forEach(error -> 
        System.err.println("Field: " + error.getField() + ", Error: " + error.getMessage()));
}
```

#### Translation Errors

**Problem**: Message translation fails
**Solution**:
- Ensure source and target formats are supported
- Check message compatibility
- Verify translation options

```java
// Check if translation is supported
boolean supported = translationManager.isTranslationSupported("MT103", "MX103");
if (!supported) {
    System.err.println("Translation not supported");
    return;
}
```

#### Security Issues

**Problem**: Security operations fail
**Solution**:
- Verify certificate validity
- Check key store configuration
- Ensure proper algorithm support

```java
// Check security manager health
var health = securityManager.healthCheck();
if (!health.isHealthy()) {
    System.err.println("Security manager is not healthy: " + health.getMessage());
}
```

#### Transport Failures

**Problem**: Message transport fails
**Solution**:
- Check network connectivity
- Verify transport configuration
- Review transport logs

```java
// Check transport availability
List<Transport> transports = transportFactory.getAvailableTransports();
if (transports.isEmpty()) {
    System.err.println("No transports available");
    return;
}
```

### Debugging

Enable debug logging:

```properties
logging.level.com.finqube=DEBUG
logging.level.com.finqube.core=DEBUG
```

### Health Checks

Access health check endpoints:

```bash
# System health
curl http://localhost:8080/actuator/health

# Component health
curl http://localhost:8080/actuator/health/finqube

# Metrics
curl http://localhost:8080/actuator/metrics
```

## Best Practices

### Message Creation

1. **Always validate messages** before processing
2. **Use appropriate message priorities** for different use cases
3. **Include meaningful descriptions** and metadata
4. **Handle validation errors** gracefully

```java
// Good practice: Validate before processing
ValidationResult result = validator.validate(message);
if (!result.isValid()) {
    throw new ValidationException("Message validation failed: " + result.getErrors());
}
```

### Error Handling

1. **Implement comprehensive error handling**
2. **Log errors with appropriate context**
3. **Use custom exceptions** for specific error types
4. **Provide meaningful error messages**

```java
@ControllerAdvice
public class FinqubeExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
```

### Performance

1. **Use async processing** for long-running operations
2. **Enable caching** for frequently accessed data
3. **Monitor performance metrics** regularly
4. **Optimize resource usage**

```java
// Use async processing for better performance
CompletableFuture<Void> future = asyncProcessor.processAsync(message);
future.get(30, TimeUnit.SECONDS);
```

### Security

1. **Always sign important messages**
2. **Use encryption for sensitive data**
3. **Regularly rotate security keys**
4. **Monitor security events**

```java
// Sign important messages
SignedMessage signedMessage = securityManager.sign(message);

// Encrypt sensitive data
EncryptedMessage encryptedMessage = securityManager.encrypt(message, recipientCertificate);
```

### Monitoring

1. **Record metrics for all operations**
2. **Set up alerts for critical failures**
3. **Monitor system health regularly**
4. **Track performance trends**

```java
// Record operation metrics
monitoringManager.recordMetric("operation.time", duration, Map.of("operation", "payment"));
monitoringManager.incrementCounter("operations.completed", Map.of("type", "payment"));
```

### Configuration

1. **Use external configuration** for environment-specific settings
2. **Validate configuration on startup**
3. **Use sensible defaults**
4. **Document configuration options**

```properties
# Environment-specific configuration
spring.finqube.transport.default-transport=${FINQUBE_TRANSPORT:logging}
spring.finqube.security.signature-algorithm=${FINQUBE_SIGNATURE_ALGORITHM:SHA256withRSA}
```

## Support

For additional support and documentation:

- **GitHub**: [https://github.com/finqube/spring-finqube](https://github.com/finqube/spring-finqube)
- **Documentation**: [https://finqube.github.io/spring-finqube](https://finqube.github.io/spring-finqube)
- **Issues**: [https://github.com/finqube/spring-finqube/issues](https://github.com/finqube/spring-finqube/issues)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details. 
