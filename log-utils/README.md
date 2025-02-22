# Log Utils

Log Utils is a lightweight logging utility library focused on providing standardized logging functionality and distributed tracing capabilities across lllIIIIlll applications. This module is designed to be simple, easy to integrate, and effectively improves system observability and problem diagnosis efficiency.

## Features

- Comprehensive HTTP request logging through RequestInformationRequestLoggingFilter
  - Detailed request information logging (URI, method, query parameters)
  - Customizable client information logging
  - Request body content capture and logging
  - Request processing duration measurement
  - MDC integration for request timestamp tracking
- Automatic trace ID generation and management through TraceIdRequestLoggingFilter
  - Thread-safe trace ID context management throughout request lifecycle
  - Flexible trace ID generation strategies via customizable providers
  - Seamless integration with Spring's filter chain

## Core Components

### RequestInformationRequestLoggingFilter

`RequestInformationRequestLoggingFilter` is a Spring-based request logging filter that provides comprehensive HTTP request logging capabilities:

- Records detailed request information including URI, method, and query parameters
- Supports customizable client information logging through `ClientInformationProvider`
- Captures and logs request body content when configured
- Measures and logs request processing duration
- Integrates with MDC for request timestamp tracking

Example configuration:

```java
@Bean
public RequestInformationRequestLoggingFilter requestInformationFilter() {
    RequestInformationRequestLoggingFilter filter = new RequestInformationRequestLoggingFilter();
    
    // Configure client information logging
    filter.setIncludeClientInfo(true);
    filter.setClientInformationProvider(new CustomClientInformationProvider());
    
    // Configure request content logging
    filter.setIncludePayload(true);
    filter.setIncludeQueryString(true);
    
    return filter;
}
```

### ClientInformationProvider

`ClientInformationProvider` is a core interface that defines strategies for customizing and extending client information retrieval:

- Provides flexible mechanisms for obtaining client information
- Supports customization of client information extraction and formatting logic
- Can retrieve various client identification information (such as IP address, User-Agent, etc.)
- Facilitates unified client information handling in distributed systems
- Supports seamless integration with existing logging systems

Example implementations:

1. Default client information provider (IP address only):
```java
public class DefaultClientInformationProvider implements ClientInformationProvider {
    @Override
    public String getClientInformation(HttpServletRequest request) {
        String remoteAddress = request.getRemoteAddr();
        return null == remoteAddress || remoteAddress.isBlank() ? "" : remoteAddress;
    }
}
```

2. Enhanced client information provider (includes more client details):
```java
public class EnhancedClientInformationProvider implements ClientInformationProvider {
    @Override
    public String getClientInformation(HttpServletRequest request) {
        StringBuilder info = new StringBuilder();
        info.append("IP:").append(request.getRemoteAddr());
        info.append(", Agent:").append(request.getHeader("User-Agent"));
        info.append(", Protocol:").append(request.getProtocol());
        return info.toString();
    }
}
```
### TraceIdContext

`TraceIdContext` is a thread-safe utility class that manages trace ID context throughout request processing:

- Provides centralized trace ID storage and retrieval using MDC (Mapped Diagnostic Context)
- Ensures thread isolation for trace IDs in concurrent environments
- Offers simple yet powerful API for trace ID lifecycle management
- Integrates seamlessly with logging frameworks through MDC
- Automatically handles null and empty trace ID cases

Example usage:

```java
// Set trace ID at the beginning of request processing
TraceIdContext.setTraceId("unique-trace-id");

// Retrieve trace ID anywhere in the request chain
String currentTraceId = TraceIdContext.getTraceId();

// Clear trace ID after request completion
TraceIdContext.clearTraceId();
```

### TraceIdRequestLoggingFilter

`TraceIdRequestLoggingFilter` is a Spring-based request logging filter that manages trace IDs throughout the HTTP request lifecycle:

- Automatically generates and assigns trace IDs for incoming requests
- Integrates with customizable `TraceIdProvider` for flexible trace ID generation strategies
- Maintains trace ID context during request processing using `TraceIdContext`
- Automatically cleans up trace ID context after request completion
- Thread-safe implementation suitable for high-concurrency environments

Example configuration:

```java
@Bean
public TraceIdRequestLoggingFilter traceIdFilter() {
    TraceIdRequestLoggingFilter filter = new TraceIdRequestLoggingFilter();
    filter.setTraceIdProvider(new CustomTraceIdProvider());
    return filter;
}
```

### TraceIdProvider

`TraceIdProvider` is a core interface that defines the strategy for trace ID generation and retrieval:

- Provides a flexible abstraction for trace ID generation strategies
- Enables customization of trace ID formats and generation rules
- Supports integration with existing tracing systems
- Allows extraction of trace IDs from request headers or other sources
- Facilitates consistent trace ID generation across distributed systems

Example implementations:

1. UUID-based trace ID provider:
```java
public class UUidTraceIdProvider implements TraceIdProvider {
    @Override
    public String getTraceId(HttpServletRequest request) {
        return UUID.randomUUID().toString();
    }
}
```

2. Request header-based trace ID provider (for distributed tracing):
```java
public class HeaderBasedTraceIdProvider implements TraceIdProvider {
    private static final String TRACE_ID_HEADER = "X-Trace-ID";
    private final TraceIdProvider fallbackProvider;
    
    public HeaderBasedTraceIdProvider() {
        this.fallbackProvider = new UuidTraceIdProvider();
    }
    
    @Override
    public String getTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        return traceId != null ? traceId : fallbackProvider.getTraceId(request);
    }
}
```

## Usage

### Maven Dependency

```xml
<dependency>
    <groupId>net.ow.shared</groupId>
    <artifactId>log-utils</artifactId>
    <version>${log-utils.version}</version>
</dependency>
```