package net.ow.shared.jsonutils.serializer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.TimeZone;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DateInstantSerializerTest {
    @InjectMocks private DateInstantSerializer dateInstantSerializer;

    @Mock private JsonGenerator jsonGenerator;

    @Mock private SerializerProvider serializerProvider;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeAll
    static void setUp() {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Test
    @SneakyThrows
    void serializeTest_OK() {
        String expectedDate = "2024-01-01";
        Instant instantToConvert = SIMPLE_DATE_FORMAT.parse(expectedDate).toInstant();

        dateInstantSerializer.serialize(instantToConvert, jsonGenerator, serializerProvider);

        verify(jsonGenerator, times(1)).writeString(expectedDate);
    }

    @Test
    @SneakyThrows
    void serializeTest_whenInstantIsNull_OK() {
        dateInstantSerializer.serialize(null, jsonGenerator, serializerProvider);

        verify(jsonGenerator, times(1)).writeString("");
    }
}
