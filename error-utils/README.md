# Error Utils

A utility library for standardized error handling and exception management across lllIIIIlll applications.

## Features

- Standardized error response format
- Common exception types
- Error code management
- Exception translation utilities
- Global exception handling

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>net.ow.shared</groupId>
    <artifactId>error-utils</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Error Response Format

```json lines
{
  "error": {
    "code": "XXX-YYY",        // From ServiceError.getCode() (e.g., "400-INVALID_INPUT")
    "title": "Error Title",    // Localized title from ServiceError.getTitle()
    "message": "Error Message", // Localized message from ServiceError.getMessage()
    "source": {               // Optional, from APIException.source
      // Error source details
    },
    "meta": {                 // Optional, from APIException.meta
      // Additional metadata as key-value pairs
    }
  }
}
```

- `code` is a unique identifier for the error, typically in the format of `XXX-YYY` where `XXX` is the HTTP status code and `YYY` is a suffix specific to the error type.
- `title` is a localized string that provides a brief description of the error.
- `message` is a localized string that provides a detailed description of the error.
- `source` is an optional field that provides additional information about the source of the error, such as a JSON pointer to the location in the request that caused the error.
- `meta` is an optional field that provides additional metadata about the error, such as a stack trace or a list of related entities.

### Example Implementation

Here's an example of implementing the ServiceError interface for common validation errors:

```java
package net.ow.shared.errorutils.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ValidationError implements ServiceError {
    INVALID_INPUT,
    VALUE_TOO_LONG;

    private ErrorProperties errorProperties;

    ValidationError() {
        // Initialize with HTTP 400 BAD_REQUEST status
        initialise(HttpStatus.BAD_REQUEST, this.name());
    }

    @Override
    public HttpStatus getStatus() {
        return errorProperties.getStatus();
    }

    @Override
    public String getCode() {
        return errorProperties.getCode();
    }

    @Override
    public String getTitle() {
        return errorProperties.getTitle();
    }

    @Override
    public String getMessage() {
        return errorProperties.getMessage();
    }

    @Override
    public void setErrorProperties(ErrorProperties errorProperties) {
        this.errorProperties = errorProperties;
    }
}
```

Usage Example:

```java
// Throwing a validation error
throw new APIException(ValidationError.INVALID_INPUT)
    .withSource(new ErrorSource("email"))
    .withMeta(Map.of("attemptedValue", invalidEmail));

// With message parameters
throw new APIException(
    ValidationError.VALUE_TOO_LONG, 
    "username",  // field name
    "50"        // max length
);
```


This will generate error messages like:

```json
{
  "error": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "code": "400-INVALID_INPUT",
    "title": "Invalid Input",
    "message": "The provided input is invalid",
    "source": {
      "field": "email"
    },
    "meta": {
      "attemptedValue": "invalid@email"
    }
  }
}
```

The corresponding message properties file (`messages.properties`) would look like:

```properties
error.400-INVALID_INPUT.title=Invalid Input
error.400-INVALID_INPUT.message=The provided input is invalid

error.400-VALUE_TOO_LONG.title=Value Too Long
error.400-VALUE_TOO_LONG.message=The field {0} must not exceed {1} characters
```

## Best Practices

1. Always use predefined error codes
2. Include meaningful error messages
3. Add relevant error details when possible
4. Log errors appropriately
5. Don't expose sensitive information in error messages