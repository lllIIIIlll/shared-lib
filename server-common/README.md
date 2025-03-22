# Server Common Module

Server-side utilities and shared components for lllIIIIlll applications.

## Features

- `CachedHttpServletRequestWrapper`: A wrapper for HttpServletRequest that caches the request payload, allowing it to be read multiple times. This is particularly useful for logging, debugging, and request processing scenarios where the request body needs to be accessed more than once.

- `QueryParameter`: An annotation for marking fields in request objects to be used as query parameters in Feign clients. It allows customizing the parameter name used in the HTTP request.

- `CommonQueryMapEncoder`: An implementation of Feign's QueryMapEncoder that converts objects to query parameters for HTTP requests. It supports the QueryParameter annotation for custom parameter naming.

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>net.ow.shared</groupId>
    <artifactId>server-common</artifactId>
    <version>version</version>
</dependency>
```

## Usage

### Cached HTTP Servlet Request

The `CachedHttpServletRequestWrapper` allows you to read the request body multiple times:

```java
// In a filter or interceptor
import net.ow.shared.common.server.servlet.CachedHttpServletRequestWrapper;

@Component
public class RequestLoggingFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        CachedHttpServletRequestWrapper cachedRequest = 
                new CachedHttpServletRequestWrapper((HttpServletRequest) request);
        
        // Read the request body for logging
        String requestBody = new String(cachedRequest.getReader().lines()
                .collect(Collectors.joining(System.lineSeparator())));
        log.info("Request body: {}", requestBody);
        
        // Pass the cached request down the filter chain
        // The request body can still be read by downstream components
        chain.doFilter(cachedRequest, response);
    }
}
```

### Feign Client Query Parameters

Use the `QueryParameter` annotation and `CommonQueryMapEncoder` for Feign clients:

```java
// Define a query object with custom parameter names
import net.ow.shared.common.server.annotation.QueryParameter;

public class SearchQuery {
    @QueryParameter(name = "term")
    private String searchTerm;
    
    @QueryParameter(name = "max_results")
    private Integer maxResults;
}

// Configure Feign client to use the CommonQueryMapEncoder
import net.ow.shared.common.server.encoder.CommonQueryMapEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public QueryMapEncoder queryMapEncoder() {
        return new CommonQueryMapEncoder();
    }
}

// Use in a Feign client
@FeignClient(name = "search-service", configuration = FeignConfig.class)
public interface SearchClient {
    @GetMapping("/search")
    SearchResponse search(@SpringQueryMap SearchQuery query);
}
```

This will generate a request with parameters like: `/search?term=abc&max_results=10`