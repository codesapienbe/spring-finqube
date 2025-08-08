# Spring Finqube Admin Dashboard

A Vaadin Flow-based admin dashboard for real-time monitoring of ISO 20022 financial messages.

## Overview

The Spring Finqube Admin Dashboard provides a comprehensive web interface for monitoring and administering the Spring Finqube ISO 20022 financial messaging system. Built with Vaadin Flow and Spring Boot, it offers real-time monitoring capabilities with a modern, responsive user interface.

## Features

### Real-time Monitoring
- **Message Statistics**: Track total messages, daily counts, pending messages, and error rates
- **System Health**: Monitor CPU, memory, and disk utilization
- **Performance Metrics**: View processing times, throughput, and error rates
- **Component Status**: Check health status of all system components

### Dashboard Components
- **Main Dashboard**: Overview with key metrics and system status
- **Message Monitoring**: Detailed message flow tracking and statistics
- **Security Management**: Certificate management, audit logs, and security status
- **System Monitoring**: Health checks, performance metrics, and resource utilization
- **Configuration Management**: System configuration and settings interface

### Technical Features
- **Real-time Updates**: WebSocket-based live updates for dashboard metrics
- **Responsive Design**: Mobile-friendly interface that adapts to different screen sizes
- **Security**: Role-based access control and secure authentication
- **REST API**: Backend API endpoints for external integrations
- **Monitoring Integration**: Integration with Spring Boot Actuator and Prometheus

## Architecture

### Core Components

#### Services
- `MessageMonitoringService`: Real-time message statistics and monitoring
- `SystemHealthService`: System health and performance monitoring
- `ProcessingPerformanceMetrics`: Performance data encapsulation

#### Controllers
- `AdminDashboardController`: REST API endpoints for dashboard data
- `MainDashboardView`: Main Vaadin Flow dashboard interface

#### Configuration
- Spring Boot auto-configuration for admin features
- Vaadin Flow configuration for web interface
- Actuator endpoints for monitoring integration

### Integration Points

The admin dashboard integrates with the core Spring Finqube modules:

- **Core Module**: Access to message processing and monitoring components
- **Security Module**: Certificate management and audit logging
- **Transport Module**: Connection monitoring and status
- **Validation Module**: Message validation statistics

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.8 or higher
- Spring Boot 3.2.0

### Running the Application

1. **Build the project**:
   ```bash
   mvn clean install
   ```

2. **Run the admin dashboard**:
   ```bash
   mvn spring-boot:run -pl spring-finqube-admin
   ```

3. **Access the dashboard**:
   - URL: `http://localhost:8080/admin`
   - API Endpoints: `http://localhost:8080/admin/api/admin/*`

### Configuration

The admin dashboard can be configured via `application.yml`:

```yaml
finqube:
  admin:
    refresh-interval: 30
    real-time-updates: true
    security:
      basic-auth-enabled: true
      session-timeout: 30
    monitoring:
      system-health-enabled: true
      message-monitoring-enabled: true
      metrics-interval: 30
```

## API Endpoints

### Dashboard Overview
- `GET /api/admin/overview` - Dashboard overview with key metrics

### Message Statistics
- `GET /api/admin/messages/stats` - Detailed message statistics

### System Health
- `GET /api/admin/system/health` - System health information

### Application Info
- `GET /api/admin/info` - Application information

## Development

### Project Structure
```
spring-finqube-admin/
├── src/main/java/com/finqube/iso20022/admin/
│   ├── SpringFinqubeAdminApplication.java
│   ├── controller/
│   │   └── AdminDashboardController.java
│   ├── dashboard/
│   │   ├── MainDashboardView.java
│   │   └── SimpleDashboardView.java
│   └── service/
│       ├── MessageMonitoringService.java
│       ├── SystemHealthService.java
│       ├── ProcessingPerformanceMetrics.java
│       └── impl/
│           ├── DefaultMessageMonitoringService.java
│           └── DefaultSystemHealthService.java
├── src/main/resources/
│   └── application.yml
└── pom.xml
```

### Adding New Features

1. **Create Service Interface**: Define the service contract in the `service` package
2. **Implement Service**: Create implementation in the `service/impl` package
3. **Add Controller Endpoints**: Create REST endpoints in the `controller` package
4. **Create Vaadin Views**: Add UI components in the `dashboard` package
5. **Update Configuration**: Add any new configuration properties

### Testing

Run the tests with:
```bash
mvn test -pl spring-finqube-admin
```

## Security Considerations

- All admin endpoints require authentication
- Session management with configurable timeouts
- Audit logging for all administrative actions
- Role-based access control for different admin functions

## Monitoring and Observability

The admin dashboard provides comprehensive monitoring capabilities:

- **Health Checks**: System and component health status
- **Metrics**: Performance and throughput metrics
- **Logging**: Structured logging with correlation IDs
- **Tracing**: Distributed tracing for request flows

## Future Enhancements

- **Advanced Analytics**: Historical data analysis and trend reporting
- **Alert Management**: Configurable alerts and notifications
- **User Management**: Admin user and role management interface
- **Integration APIs**: REST APIs for external monitoring systems
- **Custom Dashboards**: User-configurable dashboard layouts

## Contributing

Please refer to the main project's CONTRIBUTING.md for development guidelines and coding standards.

## License

This project is licensed under the same license as the main Spring Finqube project. 
