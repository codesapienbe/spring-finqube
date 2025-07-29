# Spring Finqube ISO 20022 Deployment Guide

## Overview

This guide provides comprehensive instructions for deploying the Spring Finqube ISO 20022 system in various environments, from development to production.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Development Environment](#development-environment)
3. [Testing Environment](#testing-environment)
4. [Staging Environment](#staging-environment)
5. [Production Environment](#production-environment)
6. [Docker Deployment](#docker-deployment)
7. [Kubernetes Deployment](#kubernetes-deployment)
8. [Monitoring & Observability](#monitoring--observability)
9. [Security Considerations](#security-considerations)
10. [Troubleshooting](#troubleshooting)

## Prerequisites

### System Requirements

- **Java**: OpenJDK 17 or higher
- **Memory**: Minimum 2GB RAM, recommended 4GB+
- **Storage**: Minimum 10GB available space
- **Network**: Internet access for dependencies

### Software Dependencies

- **Spring Boot**: 3.x
- **Maven**: 3.6+ or Gradle 7+
- **Docker**: 20.10+ (for containerized deployment)
- **Kubernetes**: 1.24+ (for K8s deployment)

## Development Environment

### Local Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/finqube/spring-finqube.git
   cd spring-finqube
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run -pl spring-finqube-starter
   ```

### Development Configuration

Create `application-dev.yml`:

```yaml
spring:
  profiles:
    active: dev
  finqube:
    enabled: true
    template:
      enabled: true
    validation:
      enabled: true
    async:
      enabled: true
      core-pool-size: 5
      max-pool-size: 10
    security:
      enabled: false  # Disable for development
    translation:
      enabled: true
      cache-enabled: true
      cache-size: 100
    transport:
      enabled: true
      default-transport: logging
    monitoring:
      enabled: true
      metrics-enabled: true
      health-check-enabled: true

logging:
  level:
    com.finqube: DEBUG
    com.finqube.core: DEBUG
    org.springframework: INFO
```

### IDE Configuration

#### IntelliJ IDEA

1. **Import project** as Maven project
2. **Set JDK**: File → Project Structure → Project → SDK → 17
3. **Enable annotation processing**: Settings → Build Tools → Compiler → Annotation Processors → Enable
4. **Run configuration**: Create Spring Boot configuration for `SpringFinqubeApplication`

#### Eclipse

1. **Import project**: File → Import → Maven → Existing Maven Projects
2. **Set JDK**: Project Properties → Java Build Path → Libraries → Add Library → JRE System Library
3. **Run configuration**: Run → Run Configurations → Spring Boot App

## Testing Environment

### Test Configuration

Create `application-test.yml`:

```yaml
spring:
  profiles:
    active: test
  finqube:
    enabled: true
    template:
      enabled: true
    validation:
      enabled: true
    async:
      enabled: true
      core-pool-size: 10
      max-pool-size: 20
    security:
      enabled: true
      signature-algorithm: SHA256withRSA
      encryption-algorithm: AES-256
    translation:
      enabled: true
      cache-enabled: true
      cache-size: 500
    transport:
      enabled: true
      default-transport: logging
    monitoring:
      enabled: true
      metrics-enabled: true
      health-check-enabled: true

# Test-specific settings
test:
  security:
    keystore-path: classpath:test-keystore.jks
    keystore-password: testpass
    key-password: testpass
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test category
mvn test -Dtest=*IntegrationTest

# Run with coverage
mvn test jacoco:report

# Run performance tests
mvn test -Dtest=*PerformanceTest
```

### Test Data Setup

Create test data scripts:

```sql
-- Test database setup
CREATE TABLE IF NOT EXISTS test_messages (
    id VARCHAR(36) PRIMARY KEY,
    message_id VARCHAR(50) NOT NULL,
    message_type VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert test data
INSERT INTO test_messages (id, message_id, message_type, content) VALUES
('test-001', 'MSG001', 'pain.001', '<xml>...</xml>'),
('test-002', 'MSG002', 'pain.001', '<xml>...</xml>');
```

## Staging Environment

### Staging Configuration

Create `application-staging.yml`:

```yaml
spring:
  profiles:
    active: staging
  finqube:
    enabled: true
    template:
      enabled: true
    validation:
      enabled: true
    async:
      enabled: true
      core-pool-size: 20
      max-pool-size: 50
      queue-capacity: 200
    security:
      enabled: true
      signature-algorithm: SHA256withRSA
      encryption-algorithm: AES-256
      keystore-path: ${SECURITY_KEYSTORE_PATH}
      keystore-password: ${SECURITY_KEYSTORE_PASSWORD}
    translation:
      enabled: true
      cache-enabled: true
      cache-size: 1000
    transport:
      enabled: true
      default-transport: ${TRANSPORT_TYPE:logging}
    monitoring:
      enabled: true
      metrics-enabled: true
      health-check-enabled: true

# Staging-specific settings
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
  endpoint:
    health:
      show-details: when-authorized
```

### Environment Variables

Set staging environment variables:

```bash
export SECURITY_KEYSTORE_PATH=/opt/finqube/keystore.jks
export SECURITY_KEYSTORE_PASSWORD=staging-pass
export TRANSPORT_TYPE=swift
export LOG_LEVEL=INFO
export METRICS_ENABLED=true
```

### Deployment Script

Create `deploy-staging.sh`:

```bash
#!/bin/bash

# Staging deployment script
set -e

echo "Deploying to staging environment..."

# Build application
mvn clean package -DskipTests

# Create staging directory
mkdir -p /opt/finqube/staging

# Copy application
cp spring-finqube-starter/target/spring-finqube-starter-*.jar /opt/finqube/staging/

# Copy configuration
cp config/application-staging.yml /opt/finqube/staging/

# Copy keystore
cp security/keystore.jks /opt/finqube/staging/

# Set permissions
chmod 755 /opt/finqube/staging/*.jar
chmod 600 /opt/finqube/staging/keystore.jks

# Start application
cd /opt/finqube/staging
java -jar spring-finqube-starter-*.jar --spring.profiles.active=staging

echo "Staging deployment completed"
```

## Production Environment

### Production Configuration

Create `application-prod.yml`:

```yaml
spring:
  profiles:
    active: prod
  finqube:
    enabled: true
    template:
      enabled: true
    validation:
      enabled: true
    async:
      enabled: true
      core-pool-size: 50
      max-pool-size: 100
      queue-capacity: 500
    security:
      enabled: true
      signature-algorithm: SHA256withRSA
      encryption-algorithm: AES-256
      keystore-path: ${SECURITY_KEYSTORE_PATH}
      keystore-password: ${SECURITY_KEYSTORE_PASSWORD}
      key-password: ${SECURITY_KEY_PASSWORD}
    translation:
      enabled: true
      cache-enabled: true
      cache-size: 2000
    transport:
      enabled: true
      default-transport: ${TRANSPORT_TYPE:swift}
    monitoring:
      enabled: true
      metrics-enabled: true
      health-check-enabled: true

# Production-specific settings
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
  endpoint:
    health:
      show-details: never
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    com.finqube: INFO
    org.springframework: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/finqube/application.log
    max-size: 100MB
    max-history: 30
```

### Production Environment Variables

```bash
# Security
export SECURITY_KEYSTORE_PATH=/opt/finqube/security/keystore.jks
export SECURITY_KEYSTORE_PASSWORD=prod-keystore-pass
export SECURITY_KEY_PASSWORD=prod-key-pass

# Transport
export TRANSPORT_TYPE=swift
export SWIFT_BIC_CODE=YOURBIC
export SWIFT_CERTIFICATE_PATH=/opt/finqube/security/swift-cert.pem

# Monitoring
export METRICS_ENABLED=true
export HEALTH_CHECK_ENABLED=true
export LOG_LEVEL=INFO

# Performance
export JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### Production Deployment Script

Create `deploy-prod.sh`:

```bash
#!/bin/bash

# Production deployment script
set -e

echo "Deploying to production environment..."

# Validate environment
if [ -z "$SECURITY_KEYSTORE_PASSWORD" ]; then
    echo "ERROR: SECURITY_KEYSTORE_PASSWORD not set"
    exit 1
fi

# Build application
mvn clean package -DskipTests

# Create production directory
sudo mkdir -p /opt/finqube/production
sudo mkdir -p /var/log/finqube
sudo mkdir -p /opt/finqube/security

# Copy application
sudo cp spring-finqube-starter/target/spring-finqube-starter-*.jar /opt/finqube/production/

# Copy configuration
sudo cp config/application-prod.yml /opt/finqube/production/

# Copy security files
sudo cp security/keystore.jks /opt/finqube/security/
sudo cp security/swift-cert.pem /opt/finqube/security/

# Set permissions
sudo chown -R finqube:finqube /opt/finqube
sudo chmod 755 /opt/finqube/production/*.jar
sudo chmod 600 /opt/finqube/security/*

# Create systemd service
sudo tee /etc/systemd/system/finqube.service > /dev/null <<EOF
[Unit]
Description=Spring Finqube ISO 20022 Application
After=network.target

[Service]
Type=simple
User=finqube
Group=finqube
WorkingDirectory=/opt/finqube/production
ExecStart=/usr/bin/java \$JAVA_OPTS -jar spring-finqube-starter-*.jar --spring.profiles.active=prod
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# Reload systemd and start service
sudo systemctl daemon-reload
sudo systemctl enable finqube
sudo systemctl start finqube

echo "Production deployment completed"
echo "Check status: sudo systemctl status finqube"
```

## Docker Deployment

### Dockerfile

Create `Dockerfile`:

```dockerfile
FROM openjdk:17-jre-slim

# Create application user
RUN groupadd -r finqube && useradd -r -g finqube finqube

# Create application directories
RUN mkdir -p /opt/finqube/app /opt/finqube/config /opt/finqube/security /var/log/finqube

# Copy application
COPY spring-finqube-starter/target/spring-finqube-starter-*.jar /opt/finqube/app/app.jar

# Copy configuration
COPY config/ /opt/finqube/config/

# Set permissions
RUN chown -R finqube:finqube /opt/finqube /var/log/finqube

# Switch to application user
USER finqube

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start application
ENTRYPOINT ["java", "-jar", "/opt/finqube/app/app.jar"]
```

### Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  finqube:
    build: .
    container_name: finqube-app
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SECURITY_KEYSTORE_PASSWORD=${SECURITY_KEYSTORE_PASSWORD}
      - TRANSPORT_TYPE=${TRANSPORT_TYPE:-logging}
      - METRICS_ENABLED=true
    volumes:
      - ./config:/opt/finqube/config
      - ./security:/opt/finqube/security
      - finqube-logs:/var/log/finqube
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

  prometheus:
    image: prom/prometheus:latest
    container_name: finqube-prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus-data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--storage.tsdb.retention.time=200h'
      - '--web.enable-lifecycle'

  grafana:
    image: grafana/grafana:latest
    container_name: finqube-grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_PASSWORD:-admin}
    volumes:
      - grafana-data:/var/lib/grafana
      - ./monitoring/grafana/dashboards:/etc/grafana/provisioning/dashboards
      - ./monitoring/grafana/datasources:/etc/grafana/provisioning/datasources

volumes:
  finqube-logs:
  prometheus-data:
  grafana-data:
```

### Docker Deployment Commands

```bash
# Build and run with Docker Compose
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f finqube

# Scale application
docker-compose up -d --scale finqube=3

# Stop services
docker-compose down
```

## Kubernetes Deployment

### Kubernetes Manifests

Create `k8s/namespace.yml`:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: finqube
  labels:
    name: finqube
```

Create `k8s/configmap.yml`:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: finqube-config
  namespace: finqube
data:
  application-prod.yml: |
    spring:
      profiles:
        active: prod
      finqube:
        enabled: true
        template:
          enabled: true
        validation:
          enabled: true
        async:
          enabled: true
          core-pool-size: 50
          max-pool-size: 100
        security:
          enabled: true
          signature-algorithm: SHA256withRSA
        translation:
          enabled: true
          cache-enabled: true
        transport:
          enabled: true
          default-transport: swift
        monitoring:
          enabled: true
          metrics-enabled: true
```

Create `k8s/secret.yml`:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: finqube-secrets
  namespace: finqube
type: Opaque
data:
  keystore-password: <base64-encoded-password>
  key-password: <base64-encoded-password>
  swift-certificate: <base64-encoded-certificate>
```

Create `k8s/deployment.yml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: finqube-app
  namespace: finqube
spec:
  replicas: 3
  selector:
    matchLabels:
      app: finqube
  template:
    metadata:
      labels:
        app: finqube
    spec:
      containers:
      - name: finqube
        image: finqube/spring-finqube:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: SECURITY_KEYSTORE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: finqube-secrets
              key: keystore-password
        - name: SECURITY_KEY_PASSWORD
          valueFrom:
            secretKeyRef:
              name: finqube-secrets
              key: key-password
        - name: TRANSPORT_TYPE
          value: "swift"
        - name: METRICS_ENABLED
          value: "true"
        resources:
          requests:
            memory: "2Gi"
            cpu: "500m"
          limits:
            memory: "4Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        volumeMounts:
        - name: config-volume
          mountPath: /opt/finqube/config
        - name: security-volume
          mountPath: /opt/finqube/security
          readOnly: true
      volumes:
      - name: config-volume
        configMap:
          name: finqube-config
      - name: security-volume
        secret:
          secretName: finqube-secrets
```

Create `k8s/service.yml`:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: finqube-service
  namespace: finqube
spec:
  selector:
    app: finqube
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: ClusterIP
```

Create `k8s/ingress.yml`:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: finqube-ingress
  namespace: finqube
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: finqube.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: finqube-service
            port:
              number: 80
```

### Kubernetes Deployment Commands

```bash
# Create namespace
kubectl apply -f k8s/namespace.yml

# Create secrets and configmaps
kubectl apply -f k8s/secret.yml
kubectl apply -f k8s/configmap.yml

# Deploy application
kubectl apply -f k8s/deployment.yml
kubectl apply -f k8s/service.yml
kubectl apply -f k8s/ingress.yml

# Check deployment status
kubectl get pods -n finqube
kubectl get services -n finqube
kubectl get ingress -n finqube

# View logs
kubectl logs -f deployment/finqube-app -n finqube

# Scale deployment
kubectl scale deployment finqube-app --replicas=5 -n finqube
```

## Monitoring & Observability

### Prometheus Configuration

Create `monitoring/prometheus.yml`:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "finqube_rules.yml"

scrape_configs:
  - job_name: 'finqube'
    static_configs:
      - targets: ['finqube:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 30s

  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']
```

### Grafana Dashboards

Create monitoring dashboards for:
- Application metrics
- System health
- Performance metrics
- Error rates
- Security events

### Alerting Rules

Create `monitoring/finqube_rules.yml`:

```yaml
groups:
  - name: finqube_alerts
    rules:
      - alert: HighErrorRate
        expr: rate(finqube_errors_total[5m]) > 0.1
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High error rate detected"
          description: "Error rate is {{ $value }} errors per second"

      - alert: SystemUnhealthy
        expr: finqube_system_health == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "System is unhealthy"
          description: "System health check is failing"

      - alert: HighResponseTime
        expr: histogram_quantile(0.95, rate(finqube_response_time_seconds_bucket[5m])) > 2
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High response time detected"
          description: "95th percentile response time is {{ $value }} seconds"
```

## Security Considerations

### Security Checklist

- [ ] Use strong passwords for keystores
- [ ] Enable TLS/SSL for all communications
- [ ] Implement proper access controls
- [ ] Regular security updates
- [ ] Monitor security events
- [ ] Encrypt sensitive data at rest
- [ ] Implement audit logging
- [ ] Regular security assessments

### Security Configuration

```yaml
# Security configuration
spring:
  finqube:
    security:
      enabled: true
      signature-algorithm: SHA256withRSA
      encryption-algorithm: AES-256
      keystore-path: /opt/finqube/security/keystore.jks
      keystore-password: ${SECURITY_KEYSTORE_PASSWORD}
      key-password: ${SECURITY_KEY_PASSWORD}
      certificate-validation: true
      audit-logging: true
```

## Troubleshooting

### Common Issues

1. **Application won't start**
   - Check Java version (requires 17+)
   - Verify configuration files
   - Check log files for errors

2. **Memory issues**
   - Increase heap size: `-Xmx4g`
   - Monitor memory usage
   - Check for memory leaks

3. **Performance issues**
   - Monitor CPU and memory usage
   - Check async processing configuration
   - Review cache settings

4. **Security issues**
   - Verify keystore configuration
   - Check certificate validity
   - Review security logs

### Debug Commands

```bash
# Check application status
curl -f http://localhost:8080/actuator/health

# View application metrics
curl http://localhost:8080/actuator/metrics

# Check system resources
top -p $(pgrep -f spring-finqube)

# View application logs
tail -f /var/log/finqube/application.log

# Check network connectivity
netstat -tulpn | grep 8080
```

### Support

For deployment support:

- **Documentation**: [https://finqube.github.io/spring-finqube/deployment](https://finqube.github.io/spring-finqube/deployment)
- **Issues**: [https://github.com/finqube/spring-finqube/issues](https://github.com/finqube/spring-finqube/issues)
- **Community**: [https://github.com/finqube/spring-finqube/discussions](https://github.com/finqube/spring-finqube/discussions) 