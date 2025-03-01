# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [0.2.1] - 2025-03-01

### Added
- Enhanced JWT claims extraction in json-utils module
  - Improved type-safe claim extraction with comprehensive null-safety handling
  - Added support for custom claim types with built-in validation
  - Introduced fluent API for claim access and validation
- Improved request logging capabilities in log-utils module
  - Enhanced trace ID logging with header-based extraction and propagation
  - Added customizable client information logging with MDC integration
  - Optimized request information capture for better performance

## [0.2.0] - 2025-02-22

### Added
- Introduced JWT claims extraction functionality in json-utils module
  - Type-safe claim extraction with null handling
  - Support for standard JWT claim types
- Added CachedHttpServletRequestWrapper in common module
  - Efficient request body caching mechanism
  - Thread-safe implementation for concurrent access
- Implemented request logging with trace ID in log-utils module
  - Automatic trace ID generation and propagation
  - Integration with Spring's filter chain

## [0.1.0] - 2025-02-15

### Added
- Introduced error-utils module for standardized exception handling [#3]
  - Centralized error handling framework
  - Custom exception types and handlers
- Added YAML property source factory in common module [#14]
  - Support for YAML configuration loading
  - Integration with Spring property management
- Created comprehensive project documentation [#29]
  - Detailed README with setup instructions
  - Module-specific documentation
- Implemented JSON utilities for data handling [#30]
  - Serialization and deserialization support
  - Custom type adapters and converters

### Build
- Established Maven multi-module project structure
- Configured Java 17 as compilation target
- Implemented dependency management
  - Spring Framework integration
  - Jackson JSON processing
  - Log4j logging framework
  - Testing frameworks and utilities
- Added development tools and plugins
  - Spotless for code formatting
  - JaCoCo for code coverage analysis
  - Sonar for static code analysis
- Defined build profiles
  - Default development profile
  - Sonar analysis profile