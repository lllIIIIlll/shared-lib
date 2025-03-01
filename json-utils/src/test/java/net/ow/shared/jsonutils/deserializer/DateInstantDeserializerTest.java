package net.ow.shared.jsonutils.deserializer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.TimeZone;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DateInstantDeserializerTest {
    @InjectMocks private DateInstantDeserializer dateInstantDeserializer;

    @Mock private JsonParser jsonParser;

    @Mock private DeserializationContext deserializationContext;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeAll
    static void setUp() {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Test
    @SneakyThrows
    void deserializeTest_OK() {
        String date = "2024-01-01";
        when(jsonParser.getText()).thenReturn(date);

        Instant actualResult =
                dateInstantDeserializer.deserialize(jsonParser, deserializationContext);

        Instant expectResult = SIMPLE_DATE_FORMAT.parse(date).toInstant();

        assertEquals(expectResult, actualResult);
    }

    @Test
    @SneakyThrows
    void deserializeTest_whenNullText_thenReturnsNull() {
        when(jsonParser.getText()).thenReturn(null);

        assertNull(dateInstantDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @Test
    @SneakyThrows
    void deserializeTest_whenBlankText_thenReturnsNull() {
        when(jsonParser.getText()).thenReturn("   ");

        assertNull(dateInstantDeserializer.deserialize(jsonParser, deserializationContext));
    }

    @Test
    @SneakyThrows
    void deserializeTest_whenDateFormatIncorrect_throwException() {
        String date = "2024-01-01 14:00:00";
        when(jsonParser.getText()).thenReturn(date);

        assertThrows(
                DateTimeParseException.class,
                () -> dateInstantDeserializer.deserialize(jsonParser, deserializationContext));
    }
}
