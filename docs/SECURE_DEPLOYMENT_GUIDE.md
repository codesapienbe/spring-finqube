# Secure Deployment and Configuration Guide

## Overview

This guide provides comprehensive instructions for securely deploying and configuring the Spring Finqube ISO 20022 system in production environments. It covers all aspects of secure deployment, from infrastructure setup to runtime configuration.

## Table of Contents

1. [Infrastructure Security](#infrastructure-security)
2. [Container Security](#container-security)
3. [Network Security](#network-security)
4. [Application Security Configuration](#application-security-configuration)
5. [Database Security](#database-security)
6. [Key Management](#key-management)
7. [Monitoring and Logging](#monitoring-and-logging)
8. [Backup and Recovery](#backup-and-recovery)
9. [Compliance Configuration](#compliance-configuration)
10. [Deployment Checklists](#deployment-checklists)

## Infrastructure Security

### 1. Server Hardening

#### Operating System Security
```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install security tools
sudo apt install -y fail2ban ufw rkhunter chkrootkit

# Configure firewall
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow ssh
sudo ufw allow 8080/tcp  # Application port
sudo ufw enable

# Configure fail2ban
sudo cp /etc/fail2ban/jail.conf /etc/fail2ban/jail.local
sudo systemctl enable fail2ban
sudo systemctl start fail2ban
```

#### Security Configuration
```bash
# Disable unnecessary services
sudo systemctl disable telnet
sudo systemctl disable rsh
sudo systemctl disable rlogin

# Configure password policy
sudo apt install -y libpam-pwquality
echo "password requisite pam_pwquality.so retry=3 minlen=12 difok=3 ucredit=-1 lcredit=-1 dcredit=-1 ocredit=-1" | sudo tee -a /etc/pam.d/common-password

# Configure SSH security
sudo cp /etc/ssh/sshd_config /etc/ssh/sshd_config.backup
sudo tee -a /etc/ssh/sshd_config << EOF
PermitRootLogin no
PasswordAuthentication no
PubkeyAuthentication yes
AllowUsers deploy
Port 2222
MaxAuthTries 3
ClientAliveInterval 300
ClientAliveCountMax 2
EOF
sudo systemctl restart ssh
```

### 2. Cloud Infrastructure Security

#### AWS Security Configuration
```yaml
# CloudFormation template for secure VPC
AWSTemplateFormatVersion: '2010-09-09'
Description: 'Secure VPC for Spring Finqube ISO 20022'

Parameters:
  Environment:
    Type: String
    Default: production
    AllowedValues: [development, staging, production]

Resources:
  VPC:
    Type: AWS::EC2::VPC
    Properties:
      CidrBlock: 10.0.0.0/16
      EnableDnsHostnames: true
      EnableDnsSupport: true
      Tags:
        - Key: Name
          Value: !Sub '${Environment}-finqube-vpc'

  PublicSubnet:
    Type: AWS::EC2::Subnet
    Properties:
      VpcId: !Ref VPC
      CidrBlock: 10.0.1.0/24
      AvailabilityZone: !Select [0, !GetAZs '']
      MapPublicIpOnLaunch: true

  PrivateSubnet:
    Type: AWS::EC2::Subnet
    Properties:
      VpcId: !Ref VPC
      CidrBlock: 10.0.2.0/24
      AvailabilityZone: !Select [1, !GetAZs '']

  SecurityGroup:
    Type: AWS::EC2::SecurityGroup
    Properties:
      GroupName: !Sub '${Environment}-finqube-sg'
      GroupDescription: Security group for Spring Finqube
      VpcId: !Ref VPC
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 22
          ToPort: 22
          CidrIp: 10.0.0.0/16
        - IpProtocol: tcp
          FromPort: 8080
          ToPort: 8080
          CidrIp: 10.0.0.0/16
        - IpProtocol: tcp
          FromPort: 8443
          ToPort: 8443
          CidrIp: 10.0.0.0/16
```

#### Azure Security Configuration
```yaml
# Azure ARM template for secure deployment
{
  "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentTemplate.json#",
  "contentVersion": "1.0.0.0",
  "parameters": {
    "environment": {
      "type": "string",
      "defaultValue": "production",
      "allowedValues": ["development", "staging", "production"]
    }
  },
  "resources": [
    {
      "type": "Microsoft.Network/virtualNetworks",
      "name": "[concat(parameters('environment'), '-finqube-vnet')]",
      "apiVersion": "2020-11-01",
      "location": "[resourceGroup().location]",
      "properties": {
        "addressSpace": {
          "addressPrefixes": ["10.0.0.0/16"]
        },
        "subnets": [
          {
            "name": "public-subnet",
            "properties": {
              "addressPrefix": "10.0.1.0/24",
              "networkSecurityGroup": {
                "id": "[resourceId('Microsoft.Network/networkSecurityGroups', concat(parameters('environment'), '-finqube-nsg'))]"
              }
            }
          },
          {
            "name": "private-subnet",
            "properties": {
              "addressPrefix": "10.0.2.0/24"
            }
          }
        ]
      }
    }
  ]
}
```

## Container Security

### 1. Docker Security Configuration

#### Secure Dockerfile
```dockerfile
# Multi-stage build for security
FROM openjdk:21-jdk-slim AS builder

# Install security updates
RUN apt-get update && apt-get upgrade -y && \
    apt-get install -y --no-install-recommends \
    ca-certificates \
    curl \
    gnupg \
    && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN groupadd -r finqube && useradd -r -g finqube finqube

# Set working directory
WORKDIR /app

# Copy application files
COPY target/spring-finqube-*.jar app.jar

# Create runtime image
FROM openjdk:21-jre-slim

# Install security updates
RUN apt-get update && apt-get upgrade -y && \
    apt-get install -y --no-install-recommends \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN groupadd -r finqube && useradd -r -g finqube finqube

# Set working directory
WORKDIR /app

# Copy application from builder
COPY --from=builder /app/app.jar app.jar

# Set ownership
RUN chown -R finqube:finqube /app

# Switch to non-root user
USER finqube

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Docker Compose Security
```yaml
version: '3.8'

services:
  finqube-app:
    build: .
    container_name: finqube-iso20022
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - JAVA_OPTS=-Xms512m -Xmx2g -XX:+UseG1GC
    volumes:
      - ./config:/app/config:ro
      - ./logs:/app/logs
      - ./certs:/app/certs:ro
    networks:
      - finqube-network
    security_opt:
      - no-new-privileges:true
    read_only: true
    tmpfs:
      - /tmp
      - /var/tmp
    cap_drop:
      - ALL
    cap_add:
      - CHOWN
      - SETGID
      - SETUID

  finqube-db:
    image: postgres:15-alpine
    container_name: finqube-database
    restart: unless-stopped
    environment:
      - POSTGRES_DB=finqube
      - POSTGRES_USER=finqube_user
      - POSTGRES_PASSWORD_FILE=/run/secrets/db_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init-db.sql:/docker-entrypoint-initdb.d/init-db.sql:ro
    networks:
      - finqube-network
    security_opt:
      - no-new-privileges:true
    read_only: true
    tmpfs:
      - /tmp
      - /var/tmp

networks:
  finqube-network:
    driver: bridge
    internal: true

volumes:
  postgres_data:
    driver: local

secrets:
  db_password:
    file: ./secrets/db_password.txt
```

### 2. Kubernetes Security Configuration

#### Secure Pod Configuration
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: finqube-iso20022
  labels:
    app: finqube-iso20022
spec:
  securityContext:
    runAsNonRoot: true
    runAsUser: 1000
    runAsGroup: 1000
    fsGroup: 1000
    seccompProfile:
      type: RuntimeDefault
    capabilities:
      drop:
        - ALL
  containers:
  - name: finqube-app
    image: finqube/iso20022:latest
    ports:
    - containerPort: 8080
    env:
    - name: SPRING_PROFILES_ACTIVE
      value: "production"
    - name: JAVA_OPTS
      value: "-Xms512m -Xmx2g -XX:+UseG1GC"
    resources:
      requests:
        memory: "1Gi"
        cpu: "500m"
      limits:
        memory: "2Gi"
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
      mountPath: /app/config
      readOnly: true
    - name: certs-volume
      mountPath: /app/certs
      readOnly: true
    - name: logs-volume
      mountPath: /app/logs
  volumes:
  - name: config-volume
    configMap:
      name: finqube-config
  - name: certs-volume
    secret:
      secretName: finqube-certs
  - name: logs-volume
    emptyDir: {}
```

#### Network Policies
```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: finqube-network-policy
spec:
  podSelector:
    matchLabels:
      app: finqube-iso20022
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          name: ingress-nginx
    ports:
    - protocol: TCP
      port: 8080
  egress:
  - to:
    - namespaceSelector:
        matchLabels:
          name: database
    ports:
    - protocol: TCP
      port: 5432
  - to: []
    ports:
    - protocol: TCP
      port: 53
    - protocol: UDP
      port: 53
```

## Network Security

### 1. Load Balancer Configuration

#### Nginx Security Configuration
```nginx
# /etc/nginx/nginx.conf
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
    use epoll;
    multi_accept on;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    add_header Content-Security-Policy "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';" always;
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # Logging
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
    limit_req_zone $binary_remote_addr zone=login:10m rate=1r/s;

    # Upstream configuration
    upstream finqube_backend {
        least_conn;
        server 10.0.1.10:8080 max_fails=3 fail_timeout=30s;
        server 10.0.1.11:8080 max_fails=3 fail_timeout=30s;
        server 10.0.1.12:8080 max_fails=3 fail_timeout=30s;
    }

    server {
        listen 80;
        server_name _;
        return 301 https://$host$request_uri;
    }

    server {
        listen 443 ssl http2;
        server_name finqube.example.com;

        # SSL configuration
        ssl_certificate /etc/ssl/certs/finqube.crt;
        ssl_certificate_key /etc/ssl/private/finqube.key;
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
        ssl_prefer_server_ciphers off;
        ssl_session_cache shared:SSL:10m;
        ssl_session_timeout 10m;

        # Security headers
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-Content-Type-Options "nosniff" always;
        add_header X-XSS-Protection "1; mode=block" always;

        # Rate limiting
        limit_req zone=api burst=20 nodelay;

        location / {
            proxy_pass http://finqube_backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_connect_timeout 30s;
            proxy_send_timeout 30s;
            proxy_read_timeout 30s;
        }

        location /actuator/ {
            # Restrict access to monitoring endpoints
            allow 10.0.0.0/8;
            deny all;
            proxy_pass http://finqube_backend;
        }
    }
}
```

### 2. VPN Configuration

#### OpenVPN Server Configuration
```bash
# /etc/openvpn/server.conf
port 1194
proto udp
dev tun
ca ca.crt
cert server.crt
key server.key
dh dh2048.pem
auth SHA256
cipher AES-256-CBC
server 10.8.0.0 255.255.255.0
ifconfig-pool-persist ipp.txt
push "redirect-gateway def1 bypass-dhcp"
push "dhcp-option DNS 8.8.8.8"
push "dhcp-option DNS 8.8.4.4"
keepalive 10 120
tls-auth ta.key 0
key-direction 0
cipher AES-256-CBC
auth SHA256
user nobody
group nobody
persist-key
persist-tun
status openvpn-status.log
verb 3
explicit-exit-notify 1
```

## Application Security Configuration

### 1. Spring Boot Security Configuration

#### Application Properties
```yaml
# application-production.yml
spring:
  profiles:
    active: production
  
  # Security configuration
  security:
    user:
      name: ${ADMIN_USERNAME:admin}
      password: ${ADMIN_PASSWORD:}
      roles: ADMIN
    
  # Database configuration
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:finqube}
    username: ${DB_USERNAME:finqube_user}
    password: ${DB_PASSWORD:}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  # JPA configuration
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: false

# ISO 20022 configuration
iso20022:
  enabled: true
  security:
    encryption:
      algorithm: AES-256-GCM
      key: ${ENCRYPTION_KEY:}
    hsm:
      enabled: ${HSM_ENABLED:false}
      provider: ${HSM_PROVIDER:}
      endpoint: ${HSM_ENDPOINT:}
    monitoring:
      enabled: true
      alerting:
        enabled: true
        email:
          enabled: ${ALERT_EMAIL_ENABLED:false}
          recipients: ${ALERT_EMAIL_RECIPIENTS:}
    audit:
      enabled: true
      level: SECURITY
      retention-days: 2555

# Logging configuration
logging:
  level:
    com.finqube: INFO
    org.springframework.security: DEBUG
    org.springframework.web: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/finqube.log
    max-size: 100MB
    max-history: 30

# Management endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true

# Server configuration
server:
  port: 8080
  ssl:
    enabled: ${SSL_ENABLED:false}
    key-store: ${SSL_KEYSTORE:}
    key-store-password: ${SSL_KEYSTORE_PASSWORD:}
    key-alias: ${SSL_KEY_ALIAS:}
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain
  error:
    include-message: never
    include-binding-errors: never
    include-stacktrace: never
```

#### Security Configuration Class
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable for API endpoints
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                .requestMatchers("/actuator/metrics/**").hasRole("MONITORING")
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .accessDeniedHandler(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN))
            );
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
```

## Database Security

### 1. PostgreSQL Security Configuration

#### PostgreSQL Configuration
```sql
-- /etc/postgresql/15/main/postgresql.conf
# Connection settings
listen_addresses = 'localhost'
port = 5432
max_connections = 100

# Memory settings
shared_buffers = 256MB
effective_cache_size = 1GB
work_mem = 4MB
maintenance_work_mem = 64MB

# Security settings
ssl = on
ssl_cert_file = '/etc/ssl/certs/postgresql.crt'
ssl_key_file = '/etc/ssl/private/postgresql.key'
ssl_ciphers = 'HIGH:MEDIUM:+3DES:!aNULL'
ssl_prefer_server_ciphers = on

# Logging
log_destination = 'stderr'
logging_collector = on
log_directory = 'log'
log_filename = 'postgresql-%Y-%m-%d_%H%M%S.log'
log_rotation_age = 1d
log_rotation_size = 100MB
log_min_duration_statement = 1000
log_connections = on
log_disconnections = on
log_statement = 'all'

# Performance
random_page_cost = 1.1
effective_io_concurrency = 200
```

#### Database Security Scripts
```sql
-- Create secure database and user
CREATE DATABASE finqube WITH ENCODING 'UTF8' LC_COLLATE='en_US.UTF-8' LC_CTYPE='en_US.UTF-8';

-- Create application user
CREATE USER finqube_user WITH PASSWORD 'secure_password_here';

-- Grant minimal privileges
GRANT CONNECT ON DATABASE finqube TO finqube_user;
GRANT USAGE ON SCHEMA public TO finqube_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO finqube_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO finqube_user;

-- Create tables with proper permissions
CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_id VARCHAR(35) NOT NULL UNIQUE,
    message_type VARCHAR(35) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes
CREATE INDEX idx_messages_message_id ON messages(message_id);
CREATE INDEX idx_messages_message_type ON messages(message_type);
CREATE INDEX idx_messages_created_at ON messages(created_at);

-- Grant table permissions
GRANT SELECT, INSERT, UPDATE, DELETE ON messages TO finqube_user;

-- Enable row level security
ALTER TABLE messages ENABLE ROW LEVEL SECURITY;

-- Create RLS policies
CREATE POLICY messages_select_policy ON messages
    FOR SELECT USING (true);

CREATE POLICY messages_insert_policy ON messages
    FOR INSERT WITH CHECK (true);

CREATE POLICY messages_update_policy ON messages
    FOR UPDATE USING (true);

CREATE POLICY messages_delete_policy ON messages
    FOR DELETE USING (true);
```

## Key Management

### 1. HSM Configuration

#### HSM Integration Configuration
```yaml
# hsm-config.yml
hsm:
  provider: utimaco
  endpoint: tcp://hsm.example.com:3000
  timeout: 30000
  retry-attempts: 3
  keys:
    encryption:
      - name: "encryption-key-1"
        id: "ENC001"
        algorithm: "AES-256"
        usage: "ENCRYPT"
    signing:
      - name: "signing-key-1"
        id: "SIG001"
        algorithm: "RSA-2048"
        usage: "SIGN"
    authentication:
      - name: "auth-key-1"
        id: "AUTH001"
        algorithm: "RSA-2048"
        usage: "AUTHENTICATE"
```

#### Key Rotation Script
```bash
#!/bin/bash
# key-rotation.sh

set -e

# Configuration
HSM_ENDPOINT="tcp://hsm.example.com:3000"
KEY_BACKUP_DIR="/secure/backup/keys"
LOG_FILE="/var/log/key-rotation.log"

# Log function
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

# Check if running as root
if [[ $EUID -ne 0 ]]; then
   log "ERROR: This script must be run as root"
   exit 1
fi

# Create backup directory
mkdir -p "$KEY_BACKUP_DIR"

# Backup current keys
log "INFO: Starting key backup"
java -jar /opt/finqube/key-manager.jar backup --output "$KEY_BACKUP_DIR/keys-$(date +%Y%m%d-%H%M%S).zip"

# Generate new keys
log "INFO: Generating new keys"
java -jar /opt/finqube/key-manager.jar generate --algorithm AES-256 --usage ENCRYPT --name "encryption-key-$(date +%Y%m%d)"
java -jar /opt/finqube/key-manager.jar generate --algorithm RSA-2048 --usage SIGN --name "signing-key-$(date +%Y%m%d)"

# Update application configuration
log "INFO: Updating application configuration"
systemctl stop finqube-iso20022
cp /etc/finqube/application.yml /etc/finqube/application.yml.backup.$(date +%Y%m%d-%H%M%S)
# Update key references in configuration
systemctl start finqube-iso20022

# Verify application health
log "INFO: Verifying application health"
sleep 30
if curl -f http://localhost:8080/actuator/health; then
    log "INFO: Key rotation completed successfully"
else
    log "ERROR: Application health check failed, rolling back"
    systemctl stop finqube-iso20022
    cp /etc/finqube/application.yml.backup.$(date +%Y%m%d-%H%M%S) /etc/finqube/application.yml
    systemctl start finqube-iso20022
    exit 1
fi

log "INFO: Key rotation script completed"
```

## Monitoring and Logging

### 1. Prometheus Configuration

#### Prometheus Configuration
```yaml
# /etc/prometheus/prometheus.yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "finqube-rules.yml"

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093

scrape_configs:
  - job_name: 'finqube-iso20022'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 30s
    scrape_timeout: 10s
    scheme: 'https'
    tls_config:
      ca_file: '/etc/ssl/certs/ca.crt'
      cert_file: '/etc/ssl/certs/prometheus.crt'
      key_file: '/etc/ssl/private/prometheus.key'

  - job_name: 'node-exporter'
    static_configs:
      - targets: ['localhost:9100']
    scrape_interval: 15s

  - job_name: 'postgres-exporter'
    static_configs:
      - targets: ['localhost:9187']
    scrape_interval: 30s
```

#### Alert Rules
```yaml
# /etc/prometheus/finqube-rules.yml
groups:
  - name: finqube-alerts
    rules:
      - alert: HighErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High error rate detected"
          description: "Error rate is {{ $value }} errors per second"

      - alert: HighResponseTime
        expr: histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) > 2
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High response time detected"
          description: "95th percentile response time is {{ $value }} seconds"

      - alert: DatabaseConnectionFailure
        expr: up{job="postgres-exporter"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Database connection failed"
          description: "PostgreSQL exporter is down"

      - alert: ApplicationDown
        expr: up{job="finqube-iso20022"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Application is down"
          description: "Spring Finqube ISO 20022 application is not responding"
```

### 2. Log Aggregation

#### ELK Stack Configuration
```yaml
# docker-compose.logging.yml
version: '3.8'

services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=true
      - ELASTIC_PASSWORD=secure_password_here
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data
    ports:
      - "9200:9200"
    networks:
      - logging

  logstash:
    image: docker.elastic.co/logstash/logstash:8.11.0
    container_name: logstash
    volumes:
      - ./logstash/pipeline:/usr/share/logstash/pipeline
      - ./logstash/config/logstash.yml:/usr/share/logstash/config/logstash.yml
    ports:
      - "5044:5044"
    networks:
      - logging
    depends_on:
      - elasticsearch

  kibana:
    image: docker.elastic.co/kibana/kibana:8.11.0
    container_name: kibana
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
      - ELASTICSEARCH_USERNAME=elastic
      - ELASTICSEARCH_PASSWORD=secure_password_here
    ports:
      - "5601:5601"
    networks:
      - logging
    depends_on:
      - elasticsearch

networks:
  logging:
    driver: bridge

volumes:
  elasticsearch_data:
    driver: local
```

## Backup and Recovery

### 1. Automated Backup Script

#### Database Backup Script
```bash
#!/bin/bash
# backup-database.sh

set -e

# Configuration
DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="finqube"
DB_USER="finqube_user"
BACKUP_DIR="/secure/backup/database"
RETENTION_DAYS=30
LOG_FILE="/var/log/database-backup.log"

# Log function
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Generate backup filename
BACKUP_FILE="$BACKUP_DIR/finqube-db-$(date +%Y%m%d-%H%M%S).sql"

# Perform database backup
log "INFO: Starting database backup"
PGPASSWORD="$DB_PASSWORD" pg_dump -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" \
    --verbose --clean --no-owner --no-privileges > "$BACKUP_FILE"

# Compress backup
log "INFO: Compressing backup"
gzip "$BACKUP_FILE"

# Verify backup
log "INFO: Verifying backup"
if gunzip -t "$BACKUP_FILE.gz"; then
    log "INFO: Backup verification successful"
else
    log "ERROR: Backup verification failed"
    exit 1
fi

# Clean up old backups
log "INFO: Cleaning up old backups"
find "$BACKUP_DIR" -name "finqube-db-*.sql.gz" -mtime +$RETENTION_DAYS -delete

# Upload to secure storage (if configured)
if [[ -n "$BACKUP_S3_BUCKET" ]]; then
    log "INFO: Uploading backup to S3"
    aws s3 cp "$BACKUP_FILE.gz" "s3://$BACKUP_S3_BUCKET/database/"
fi

log "INFO: Database backup completed successfully"
```

#### Application Backup Script
```bash
#!/bin/bash
# backup-application.sh

set -e

# Configuration
APP_DIR="/opt/finqube"
BACKUP_DIR="/secure/backup/application"
CONFIG_DIR="/etc/finqube"
RETENTION_DAYS=30
LOG_FILE="/var/log/application-backup.log"

# Log function
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Generate backup filename
BACKUP_FILE="$BACKUP_DIR/finqube-app-$(date +%Y%m%d-%H%M%S).tar.gz"

# Stop application
log "INFO: Stopping application"
systemctl stop finqube-iso20022

# Create application backup
log "INFO: Creating application backup"
tar -czf "$BACKUP_FILE" \
    --exclude="$APP_DIR/logs" \
    --exclude="$APP_DIR/temp" \
    "$APP_DIR" \
    "$CONFIG_DIR"

# Start application
log "INFO: Starting application"
systemctl start finqube-iso20022

# Verify application health
log "INFO: Verifying application health"
sleep 30
if curl -f http://localhost:8080/actuator/health; then
    log "INFO: Application backup completed successfully"
else
    log "ERROR: Application health check failed after backup"
    exit 1
fi

# Clean up old backups
log "INFO: Cleaning up old backups"
find "$BACKUP_DIR" -name "finqube-app-*.tar.gz" -mtime +$RETENTION_DAYS -delete

log "INFO: Application backup completed successfully"
```

## Compliance Configuration

### 1. ISO 27001 Compliance

#### Security Policy Configuration
```yaml
# iso27001-compliance.yml
compliance:
  iso27001:
    enabled: true
    controls:
      # Access Control
      access_control:
        enabled: true
        password_policy:
          min_length: 12
          require_uppercase: true
          require_lowercase: true
          require_numbers: true
          require_special: true
          max_age_days: 90
        session_management:
          timeout_minutes: 480
          max_concurrent_sessions: 3
        mfa:
          enabled: true
          method: "TOTP"
      
      # Cryptography
      cryptography:
        enabled: true
        algorithms:
          encryption: ["AES-256-GCM", "AES-256-CBC"]
          signing: ["RSA-2048", "RSA-4096"]
          hashing: ["SHA-256", "SHA-512"]
        key_management:
          rotation_days: 365
          backup_enabled: true
          hsm_integration: true
      
      # Audit and Logging
      audit:
        enabled: true
        events:
          - authentication
          - authorization
          - data_access
          - configuration_change
          - system_events
        retention_days: 2555
        encryption: true
      
      # Data Protection
      data_protection:
        enabled: true
        classification:
          - public
          - internal
          - confidential
          - restricted
        encryption:
          at_rest: true
          in_transit: true
        backup:
          enabled: true
          encryption: true
          retention_days: 2555
```

### 2. PCI DSS Compliance

#### PCI DSS Configuration
```yaml
# pci-dss-compliance.yml
compliance:
  pci_dss:
    enabled: true
    version: "4.0"
    controls:
      # Requirement 1: Network Security
      network_security:
        firewall:
          enabled: true
          rules:
            - source: "10.0.0.0/8"
              destination: "10.0.0.0/8"
              protocol: "tcp"
              ports: ["22", "8080", "8443"]
        segmentation:
          enabled: true
          zones:
            - name: "dmz"
              cidr: "10.0.1.0/24"
            - name: "internal"
              cidr: "10.0.2.0/24"
            - name: "database"
              cidr: "10.0.3.0/24"
      
      # Requirement 2: Access Control
      access_control:
        authentication:
          mfa_required: true
          password_complexity: true
          session_timeout: 480
        authorization:
          role_based: true
          least_privilege: true
        monitoring:
          access_logging: true
          failed_attempts: true
      
      # Requirement 3: Data Protection
      data_protection:
        encryption:
          card_data: true
          algorithm: "AES-256"
          key_management: true
        masking:
          pan_masking: true
          cvv_exclusion: true
      
      # Requirement 4: Vulnerability Management
      vulnerability_management:
        scanning:
          automated: true
          frequency: "weekly"
          tools: ["nmap", "nessus", "qualys"]
        patching:
          automated: true
          critical_timeframe: "24h"
          high_timeframe: "7d"
      
      # Requirement 5: Security Monitoring
      security_monitoring:
        logging:
          enabled: true
          events: ["all"]
          retention: "1_year"
        alerting:
          enabled: true
          real_time: true
        incident_response:
          enabled: true
          team_defined: true
```

## Deployment Checklists

### 1. Pre-Deployment Checklist

#### Security Verification
- [ ] **Infrastructure Security**
  - [ ] Server hardening completed
  - [ ] Firewall rules configured
  - [ ] Network segmentation implemented
  - [ ] SSL/TLS certificates installed
  - [ ] VPN access configured

- [ ] **Application Security**
  - [ ] Security configuration reviewed
  - [ ] Authentication mechanisms tested
  - [ ] Authorization controls verified
  - [ ] Input validation implemented
  - [ ] Output encoding configured

- [ ] **Database Security**
  - [ ] Database hardening completed
  - [ ] User permissions configured
  - [ ] SSL connections enabled
  - [ ] Backup procedures tested
  - [ ] Encryption at rest enabled

- [ ] **Monitoring and Logging**
  - [ ] Logging configured
  - [ ] Monitoring tools deployed
  - [ ] Alerting configured
  - [ ] Dashboard access granted
  - [ ] Log retention policies set

### 2. Deployment Checklist

#### Deployment Steps
- [ ] **Environment Preparation**
  - [ ] Infrastructure deployed
  - [ ] Security groups configured
  - [ ] Load balancer configured
  - [ ] SSL certificates installed
  - [ ] DNS records updated

- [ ] **Application Deployment**
  - [ ] Application container built
  - [ ] Security scan completed
  - [ ] Container deployed
  - [ ] Health checks passing
  - [ ] Performance tests passed

- [ ] **Database Setup**
  - [ ] Database deployed
  - [ ] Schema created
  - [ ] Initial data loaded
  - [ ] Backup procedures tested
  - [ ] Monitoring configured

- [ ] **Integration Testing**
  - [ ] API endpoints tested
  - [ ] Authentication verified
  - [ ] Authorization tested
  - [ ] Error handling verified
  - [ ] Performance validated

### 3. Post-Deployment Checklist

#### Verification Steps
- [ ] **Security Verification**
  - [ ] Penetration testing completed
  - [ ] Vulnerability scan passed
  - [ ] Security monitoring active
  - [ ] Incident response ready
  - [ ] Compliance verified

- [ ] **Performance Verification**
  - [ ] Load testing completed
  - [ ] Performance metrics acceptable
  - [ ] Resource utilization normal
  - [ ] Response times within SLA
  - [ ] Scalability verified

- [ ] **Operational Verification**
  - [ ] Backup procedures working
  - [ ] Monitoring alerts configured
  - [ ] Log aggregation active
  - [ ] Documentation updated
  - [ ] Team training completed

---

**Version**: 1.0.0  
**Last Updated**: December 2024  
**Next Review**: March 2025 
