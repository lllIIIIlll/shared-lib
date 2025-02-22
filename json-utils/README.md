# JSON Utils Module

Utility functions for working with JSON data in lllIIIIlll applications.

## Features

- `serializer`: A pack of utilities for serializing an object to a JSON string.
  - `DateInstantSerializer`: Converts Instant objects to date strings in "yyyy-MM-dd" format using UTC timezone.
- `deserializer`: A pack of utilities for deserializing a JSON string to an object.
  - `DateInstantDeserializer`: Parses date strings into Instant objects, assuming UTC timezone.

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>net.ow.shared</groupId>
    <artifactId>json-utils</artifactId>
    <version>version</version>
</dependency>
```

## Usage

### Date/Instant Serialization

```java
// Configure ObjectMapper with custom serializer
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new SimpleModule()
    .addSerializer(Instant.class, new DateInstantSerializer())
    .addDeserializer(Instant.class, new DateInstantDeserializer()));

// Example usage
class Event {
    private Instant date;
    // getters and setters
}

// Serialization
Event event = new Event();
event.setDate(Instant.now());
String json = mapper.writeValueAsString(event);
// Output: {"date":"2024-01-10"}

// Deserialization
Event parsed = mapper.readValue(json, Event.class);
Instant date = parsed.getDate(); // UTC date at start of day
```