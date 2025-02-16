# JaCoCo Aggregator

Aggregates code coverage reports from all modules in the shared library project.

## Overview

This module is responsible for:
- Collecting code coverage data from all modules
- Generating consolidated coverage reports
- Enforcing coverage thresholds
- Providing coverage metrics visualization

## Usage

### Generate Coverage Report

In order generate a aggregated coverage report, add all modules except `jacoco-aggregator` under `shared-lib` to `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>net.ow.shared</groupId>
        <artifactId>error-utils</artifactId>
    </dependency>
    <dependency>
        <groupId>net.ow.shared</groupId>
        <artifactId>common</artifactId>
    </dependency>
</dependencies>
```

Run from the root project:
`mvn clean verify`

The aggregated report will be generated at:
```
jacoco-aggregator/target/site/jacoco-aggregate/index.html
```

### Coverage Rules

Current coverage thresholds:
```xml
<limits>
    <limit>
        <counter>LINE</counter>
        <value>COVEREDRATIO</value>
        <minimum>0.80</minimum>
    </limit>
</limits>
```

### Excluding Classes

To exclude classes from coverage:
```xml
<excludes>
    <exclude>**/dto/**/*</exclude>
    <exclude>**/model/**/*</exclude>
    <exclude>**/common/factory/YAMLPropertySourceFactory.*</exclude>
</excludes>
```

## Reports

The aggregated report includes:
- Overall coverage metrics
- Package-level statistics
- Class-level details
- Line coverage visualization
- Branch coverage information

## Best Practices

1. **Regular Monitoring**
   - Run reports before merging
   - Review coverage trends
   - Address coverage drops

2. **Meaningful Tests**
   - Focus on business logic
   - Test edge cases
   - Don't write tests just for coverage

3. **Exclusions**
   - Only exclude when justified
   - Document exclusion reasons
   - Review exclusions periodically

## Integration

### SonarQube Integration
```xml
<properties>
    <sonar.coverage.jacoco.xmlReportPaths>${project.basedir}/jacoco-aggregator/target/site/jacoco-aggregate/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
</properties>
```