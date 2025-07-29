# Spring Finqube ISO 20022 API Documentation

## Overview

Spring Finqube is a comprehensive ISO 20022 message processing framework for Java applications. It provides a complete solution for creating, validating, processing, securing, translating, and transporting ISO 20022 messages.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Core Components](#core-components)
3. [Message Processing](#message-processing)
4. [Security Features](#security-features)
5. [Translation Capabilities](#translation-capabilities)
6. [Transport Layer](#transport-layer)
7. [Monitoring & Metrics](#monitoring--metrics)
8. [Configuration](#configuration)
9. [Examples](#examples)
10. [Best Practices](#best-practices)

## Getting Started

### Maven Dependency

```xml
<dependency>
    <groupId>com.finqube</groupId>
    <artifactId>spring-finqube-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Basic Usage

```java
@SpringBootApplication
public class MyApplication {
    
    @Autowired
    private Iso20022Template template;
    
    @Autowired
    private MessageValidator validator;
    
    public void processMessage() {
        // Create a PAIN.001 message
        Pain001Message message = new Pain001Message("MSG001", List.of("TXN001"), 1, 1000.00);
        
        // Validate the message
        ValidationResult result = validator.validate(message);
        if (result.isValid()) {
            // Generate XML
            String xml = template.generateXml(message);
            // Process the message...
        }
    }
}
```

## Core Components

### 1. Message Models

#### BaseMessage
The foundation class for all ISO 20022 messages.

```java
public abstract class BaseMessage {
    private final String messageId;
    private final List<String> transactions;
    private final int transactionCount;
    private final double totalAmount;
    
    // Abstract methods to be implemented
    public abstract String getMessageType();
    public abstract String getBusinessProcess();
    public abstract MessagePriority getPriority();
    public abstract String getDescription();
}
```

#### Pain001Message
Implementation for PAIN.001 payment initiation messages.

```java
Pain001Message message = new Pain001Message("MSG001", List.of("TXN001"), 1, 1000.00) {
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
        return MessagePriority.HIGH;
    }
    
    @Override
    public String getDescription() {
        return "Payment instruction";
    }
};
```

### 2. Template System

#### Iso20022Template
Generates XML from message objects using templates.

```java
@Autowired
private Iso20022Template template;

// Generate XML
String xml = template.generateXml(message);

// Generate with custom options
TemplateOptions options = TemplateOptions.builder()
    .formatOutput(true)
    .includeSchemaLocation(true)
    .build();
String formattedXml = template.generateXml(message, options);
```

### 3. Validation System

#### MessageValidator
Validates messages against business rules and schemas.

```java
@Autowired
private MessageValidator validator;

// Validate message
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

#### AsyncMessageProcessor
Processes messages asynchronously with thread pool management.

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

```java
public enum MessagePriority {
    LOW,      // Background processing
    NORMAL,   // Standard processing
    HIGH,     // Priority processing
    URGENT    // Immediate processing
}
```

## Security Features

### SecurityManager
Provides digital signatures, encryption, and secure message handling.

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

// Encrypt message
byte[] recipientCertificate = loadCertificate();
EncryptedMessage encryptedMessage = securityManager.encrypt(message, recipientCertificate);

// Decrypt message
BaseMessage decryptedMessage = securityManager.decrypt(encryptedMessage);

// Sign and encrypt
SecureMessage secureMessage = securityManager.signAndEncrypt(message, recipientCertificate);

// Verify and decrypt
SecureMessageResult result = securityManager.verifyAndDecrypt(secureMessage);
```

### Security Options

```java
// Configure security options
SecurityOptions options = SecurityOptions.builder()
    .signatureAlgorithm("SHA256withRSA")
    .encryptionAlgorithm("AES-256")
    .keyEncryptionAlgorithm("RSA")
    .build();
```

## Translation Capabilities

### TranslationManager
Converts messages between different formats (MT, MX, JSON, etc.).

```java
@Autowired
private TranslationManager translationManager;

// Translate MT103 to MX103
TranslationResult result = translationManager.translate(message, "MT103", "MX103");

if (result.isSuccessful()) {
    BaseMessage translatedMessage = result.getTranslatedMessage();
    System.out.println("Translation successful");
}

// Translate with options
TranslationOptions options = TranslationOptions.builder()
    .enableCaching(true)
    .enableValidation(true)
    .timeoutMillis(30000)
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

### TransportFactory
Manages different transport mechanisms for message delivery.

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

## Monitoring & Metrics

### MonitoringManager
Provides comprehensive monitoring and metrics collection.

```java
@Autowired
private MonitoringManager monitoringManager;

// Record metrics
monitoringManager.recordMetric("message.processing.time", 150.5, Map.of("type", "pain.001"));
monitoringManager.incrementCounter("messages.processed", Map.of("type", "pain.001"));
monitoringManager.recordTiming("validation.time", 25, Map.of("type", "pain.001"));

// Get system health
SystemHealth health = monitoringManager.getSystemHealth();
System.out.println("System health: " + health.getStatus());
System.out.println("Health percentage: " + health.getHealthPercentage() + "%");

// Get performance metrics
PerformanceMetrics metrics = monitoringManager.getCurrentPerformanceMetrics();
System.out.println("Success rate: " + metrics.getSuccessRate() + "%");
System.out.println("Average response time: " + metrics.getAverageResponseTimeMillis() + "ms");
```

### Health Checks

```java
// Component health checks
List<ComponentHealth> components = monitoringManager.getComponentHealth();
components.forEach(component -> 
    System.out.println(component.getComponentName() + ": " + component.getStatus()));
```

## Configuration

### Application Properties

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

## Examples

### Complete Workflow Example

```java
@Service
public class PaymentService {
    
    @Autowired
    private Iso20022Template template;
    
    @Autowired
    private MessageValidator validator;
    
    @Autowired
    private SecurityManager securityManager;
    
    @Autowired
    private TranslationManager translationManager;
    
    @Autowired
    private TransportFactory transportFactory;
    
    @Autowired
    private MonitoringManager monitoringManager;
    
    public void processPayment(PaymentRequest request) {
        try {
            // 1. Create message
            Pain001Message message = createPaymentMessage(request);
            
            // 2. Validate message
            ValidationResult validation = validator.validate(message);
            if (!validation.isValid()) {
                throw new ValidationException("Message validation failed");
            }
            
            // 3. Sign message
            SignedMessage signedMessage = securityManager.sign(message);
            
            // 4. Translate to target format
            TranslationResult translation = translationManager.translate(signedMessage, "MT103", "MX103");
            if (!translation.isSuccessful()) {
                throw new TranslationException("Message translation failed");
            }
            
            // 5. Send message
            Transport transport = transportFactory.getTransport("swift");
            TransportResponse response = transport.send(translation.getTranslatedMessage());
            
            if (response.getStatus() == TransportStatus.SUCCESS) {
                // 6. Record success metrics
                monitoringManager.incrementCounter("payments.processed", 
                    Map.of("type", "pain.001", "status", "success"));
                monitoringManager.recordMetric("payment.processing.time", 
                    System.currentTimeMillis() - startTime, Map.of("type", "pain.001"));
            } else {
                // Record failure metrics
                monitoringManager.incrementCounter("payments.failed", 
                    Map.of("type", "pain.001", "status", "failed"));
            }
            
        } catch (Exception e) {
            monitoringManager.recordEvent("payment.error", 
                Map.of("error", e.getMessage()), Map.of("type", "pain.001"));
            throw e;
        }
    }
}
```

### Error Handling Example

```java
@ControllerAdvice
public class FinqubeExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(TranslationException.class)
    public ResponseEntity<ErrorResponse> handleTranslationException(TranslationException e) {
        ErrorResponse error = new ErrorResponse("TRANSLATION_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException e) {
        ErrorResponse error = new ErrorResponse("SECURITY_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
```

## Best Practices

### 1. Message Creation
- Always validate messages before processing
- Use appropriate message priorities
- Include meaningful descriptions and metadata

### 2. Error Handling
- Implement comprehensive error handling
- Log errors with appropriate context
- Use custom exceptions for specific error types

### 3. Performance
- Use async processing for long-running operations
- Enable caching for frequently accessed data
- Monitor performance metrics regularly

### 4. Security
- Always sign important messages
- Use encryption for sensitive data
- Regularly rotate security keys

### 5. Monitoring
- Record metrics for all operations
- Set up alerts for critical failures
- Monitor system health regularly

### 6. Configuration
- Use external configuration for environment-specific settings
- Validate configuration on startup
- Use sensible defaults

## Troubleshooting

### Common Issues

1. **Message Validation Failures**
   - Check message structure and required fields
   - Verify business rules compliance
   - Review validation error messages

2. **Translation Errors**
   - Ensure source and target formats are supported
   - Check message compatibility
   - Verify translation options

3. **Security Issues**
   - Verify certificate validity
   - Check key store configuration
   - Ensure proper algorithm support

4. **Transport Failures**
   - Check network connectivity
   - Verify transport configuration
   - Review transport logs

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

## Support

For additional support and documentation:

- **GitHub**: [https://github.com/finqube/spring-finqube](https://github.com/finqube/spring-finqube)
- **Documentation**: [https://finqube.github.io/spring-finqube](https://finqube.github.io/spring-finqube)
- **Issues**: [https://github.com/finqube/spring-finqube/issues](https://github.com/finqube/spring-finqube/issues)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details. 
