package net.ow.shared.jsonutils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateInstantSerializer extends JsonSerializer<Instant> {
    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);

    @Override
    public void serialize(
            Instant instantToConvert,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider)
            throws IOException {
        if (null == instantToConvert) {
            jsonGenerator.writeString("");
            return;
        }

        String result = dateTimeFormatter.format(instantToConvert);
        jsonGenerator.writeString(result);
    }
}
