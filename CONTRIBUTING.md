# Contributing to Spring Finqube

Thank you for your interest in contributing to Spring Finqube! This document provides guidelines and information for contributors.

## 🎯 How to Contribute

We welcome contributions from the community! Here are the main ways you can contribute:

- 🐛 **Bug Reports**: Report bugs and issues
- 💡 **Feature Requests**: Suggest new features and improvements
- 📝 **Documentation**: Improve or add documentation
- 🔧 **Code Contributions**: Submit pull requests with code changes
- 🧪 **Testing**: Help with testing and quality assurance

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8+ or Gradle 8+
- Git
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

### Development Setup

1. **Fork the repository**
   ```bash
   git clone https://github.com/your-username/spring-finqube.git
   cd spring-finqube
   ```

2. **Set up your development environment**
   ```bash
   mvn clean install
   ```

3. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

4. **Make your changes and test them**
   ```bash
   mvn clean test
   ```

5. **Commit your changes**
   ```bash
   git commit -m "feat: add new ISO 20022 message type support"
   ```

6. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Create a Pull Request**

## 📋 Development Guidelines

### Code Style

We follow the Google Java Style Guide with some modifications:

- **Indentation**: 4 spaces (no tabs)
- **Line length**: 120 characters maximum
- **Naming conventions**: Follow Java conventions
- **Documentation**: Javadoc for all public APIs

### Commit Message Format

We use [Conventional Commits](https://www.conventionalcommits.org/) format:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Examples:**
```
feat(core): add support for pain.001 message validation
fix(autoconfig): resolve bean creation issue in starter
docs(readme): update installation instructions
test(core): add unit tests for message parser
```

### Testing Requirements

- **Unit Tests**: All new code must have unit tests
- **Integration Tests**: New features should include integration tests
- **Test Coverage**: Aim for at least 80% code coverage
- **Test Naming**: Use descriptive test names that explain the scenario

### Security Guidelines

- **No hardcoded secrets**: Use environment variables or configuration properties
- **Input validation**: Validate all external inputs
- **Secure defaults**: Use secure configurations by default
- **Dependency updates**: Keep dependencies up to date

## 🔧 Development Workflow

### 1. Issue Creation

Before starting work, create an issue to:
- Describe the problem or feature
- Discuss the proposed solution
- Get feedback from maintainers

### 2. Branch Strategy

- `main`: Production-ready code
- `develop`: Integration branch for features
- `feature/*`: Feature branches
- `bugfix/*`: Bug fix branches
- `hotfix/*`: Critical bug fixes

### 3. Pull Request Process

1. **Create a descriptive PR title**
2. **Fill out the PR template**
3. **Link related issues**
4. **Ensure all tests pass**
5. **Update documentation if needed**
6. **Request reviews from maintainers**

### 4. Code Review

All code changes require review:
- At least one maintainer approval
- All CI checks must pass
- Code style and quality standards met

## 📚 Documentation

### Code Documentation

- **Javadoc**: Required for all public APIs
- **Inline comments**: For complex business logic
- **README updates**: When adding new features

### User Documentation

- **Getting Started**: Clear setup instructions
- **Configuration**: Document all properties
- **Examples**: Provide working code examples
- **Troubleshooting**: Common issues and solutions

## 🧪 Testing

### Test Structure

```
src/
├── main/java/
└── test/java/
    ├── unit/           # Unit tests
    ├── integration/    # Integration tests
    └── performance/    # Performance tests
```

### Running Tests

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=MessageValidatorTest

# Run with coverage
mvn clean test jacoco:report

# Run integration tests
mvn verify
```

### Test Guidelines

- **Test isolation**: Each test should be independent
- **Descriptive names**: Test names should explain the scenario
- **Arrange-Act-Assert**: Follow AAA pattern
- **Mock external dependencies**: Don't rely on external services

## 🔍 Quality Assurance

### Code Quality Tools

- **Checkstyle**: Code style enforcement
- **Spotless**: Code formatting
- **JaCoCo**: Code coverage
- **Maven Enforcer**: Dependency and version management

### Pre-commit Checks

```bash
# Format code
mvn spotless:apply

# Check style
mvn checkstyle:check

# Run tests
mvn clean test

# Build project
mvn clean install
```

## 🚨 Reporting Issues

### Bug Reports

When reporting bugs, please include:

1. **Environment details**:
   - Java version
   - Spring Boot version
   - Operating system
   - Maven/Gradle version

2. **Steps to reproduce**:
   - Clear, step-by-step instructions
   - Sample code or configuration

3. **Expected vs actual behavior**:
   - What you expected to happen
   - What actually happened

4. **Additional context**:
   - Error messages and stack traces
   - Logs (with sensitive data removed)
   - Screenshots if applicable

### Feature Requests

For feature requests, please include:

1. **Problem description**: What problem does this solve?
2. **Proposed solution**: How should it work?
3. **Use cases**: Real-world scenarios
4. **Alternatives considered**: Other approaches you've considered

## 🤝 Community Guidelines

### Code of Conduct

We are committed to providing a welcoming and inclusive environment:

- **Be respectful**: Treat everyone with respect
- **Be inclusive**: Welcome people from all backgrounds
- **Be collaborative**: Work together constructively
- **Be professional**: Maintain professional behavior

### Communication

- **GitHub Issues**: For bug reports and feature requests
- **GitHub Discussions**: For questions and general discussion
- **Pull Requests**: For code contributions
- **Email**: For security issues (support@finqube.com)

## 📋 Checklist for Contributors

Before submitting your contribution, ensure:

- [ ] Code follows style guidelines
- [ ] All tests pass
- [ ] Documentation is updated
- [ ] Commit messages follow conventions
- [ ] No sensitive data is included
- [ ] Security considerations are addressed
- [ ] Performance impact is considered

## 🏆 Recognition

Contributors will be recognized in:

- **Contributors list**: GitHub contributors page
- **Release notes**: Mentioned in release announcements
- **Documentation**: Listed in contributors section
- **Hall of Fame**: Special recognition for significant contributions

## 📞 Getting Help

If you need help:

1. **Check existing issues**: Search for similar problems
2. **Read documentation**: Check README and docs
3. **Ask in discussions**: Use GitHub Discussions
4. **Contact maintainers**: For urgent or private matters

## 🎉 Thank You

Thank you for contributing to Spring Finqube! Your contributions help make financial messaging more accessible and secure for everyone.

---

**Happy coding! 🚀** 
