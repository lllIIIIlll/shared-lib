# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

## [0.2.1] - 2025-03-01

### Added
- Enhanced JWT claims extraction functionality in json-utils module
- Added request logging with trace ID support in log-utils module

## [0.2.0] - 2025-02-22

### Added
- JWT claims extraction functionality in JSON utilities module
- CachedHttpServletRequestWrapper in common module for request body caching
- Request logging with trace ID support in log-utils module

## [0.1.0] - 2025-02-15

### Added
- Error handling module (error-utils) for standardized exception handling [#3]
- YAML property source factory in common module [#14]
- Comprehensive README.md with project overview and guidelines [#29]
- JSON utilities for serialization and deserialization [#30]

### Build
- Set up Maven build configuration for multi-module project
- Configured Java 17 as compilation target
- Implemented dependency management for Spring, Jackson, Log4j, and testing frameworks
- Added build plugins for code formatting (Spotless), code coverage (JaCoCo), and static analysis (Sonar)
- Defined profiles for default and Sonar builds