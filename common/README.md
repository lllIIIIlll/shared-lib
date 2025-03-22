# Common Module

Core utilities and shared components for lllIIIIlll applications.

## Features

- `YAMLPropertySourceFactory`: A custom YAML property source factory that enables loading YAML configuration files in Spring applications. It provides seamless integration with Spring's property loading mechanism.
- `JWTUtils`: Built-in support for JWT token handling and validation using the Nimbus JWT library. It provides type-safe claim extraction with null-safety handling, supporting various data types through generic type parameters. The utility helps prevent common JWT-related issues like type casting errors and null pointer exceptions.

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>net.ow.shared</groupId>
    <artifactId>common</artifactId>
    <version>version</version>
</dependency>
```

## Usage

### YAML Property Source Factory

To use the YAML property source factory in your Spring application:

```java
@Configuration
@PropertySource(value = "classpath:config.yml", factory = YAMLPropertySourceFactory.class)
public class YourConfig {
    // Your configuration properties
}
```

This enables you to use YAML files for configuration with proper type conversion and validation.

### JWT Integration

The module provides JWT support through Nimbus JWT library integration. The `JWTUtils` class offers a type-safe way to extract claims from JWT tokens:

```java
// Extract a string claim
String subject = JWTUtils.getClaim(jwtToken, "sub", String.class);

// Extract a numeric claim
Long timestamp = JWTUtils.getClaim(jwtToken, "timestamp", Long.class);

// Extract a custom object claim
Map<String, Object> customClaim = JWTUtils.getClaim(jwtToken, "custom", Map.class);
```