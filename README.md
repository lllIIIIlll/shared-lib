# lllIIIIlll Shared Library

Backend shared module for lllIIIIlll.  All modules under this repository are deployed to [Nexus](http://www.oliverw.dns-dynamic.net:8081)

## Overview

This repository contains shared libraries and common utilities used across lllIIIIlll projects. By centralizing these shared components, we ensure consistency and reduce code duplication across our applications.

## Modules

### Common Module

Contains core utilities and shared functionality including:
- Utility functions
- Base configurations

### Error Utils Module

Contains utility functions for handling errors and exceptions.

### Jacoco Aggregator Module

Aggregates Jacoco reports from all modules.

### JSON Utils Module

Utility functions for working with JSON data in lllIIIIlll applications.

### Log Utils Module

Lightweight logging utility library focused on providing standardized logging functionality and distributed tracing capabilities across lllIIIIlll applications.

## Documentation

For detailed documentation of each module, please refer to the respective module directories.

## Installation

To include any module from this shared library in your project:

```xml
<dependency>
    <groupId>net.ow.shared</groupId>
    <artifactId>module-name</artifactId>
    <version>version</version>
</dependency>
```

## Development

### Prerequisites
- Java 17
- Maven 3.6+

### Maven Configuration

To use the Nexus repository, you need to configure your Maven settings.xml file.

```xml
<settings>
    <servers>
        <server>
            <id>nexus</id>
            <username>your-username</username>
            <password>your-password</password>
        </server>
    </servers>
</settings>
```

### Project Structure

```
shared-lib/
├── common/
├── error-utils/
├── jacoco-aggregator/
├── json-utils/
├── log-utils/
```

### Branch Strategy

- `main` - Production branch
- `dev` - Development branch
- `feat-*` - New features
- `fix-*` - Bug fixes
- `release-*` - Release preparation
- `docs-*` - Documentation
- `test-*` - Add or update code related to testing
- `style-*` - Feature and updates related to styling
- `rft-*` - Refactor a specific section of the codebase
- `chore-*` - Regular code maintenance 

### Code Style

- [Google Java Format](https://github.com/google/google-java-format)
- [Spotless](https://github.com/diffplug/spotless)

### Testing

- [JUnit 5](https://junit.org/junit5/)
- [Mockito](https://site.mockito.org/)
- [Spring Test](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/junit/jupiter/SpringJUnitJupiterConfig.html)

### Building

#### Local Build
Open this project in your favorite terminal.

##### Build all modules
`mvn clean install`

##### Skip tests
`mvn clean install -DskipTests`

##### Build specific module
`mvn clean install -pl <module-name>`