package net.ow.shared.jsonutils.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

public class DateInstantDeserializer extends JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, DateTimeParseException {
        String text = jsonParser.getText();
        if (null == text || text.isBlank()) {
            return null;
        }

        return LocalDate.parse(text).atStartOfDay(ZoneOffset.UTC).toInstant();
    }
}
