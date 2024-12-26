package net.ow.shared.errorutils.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import java.io.Serializable;
import java.util.Map;
import lombok.SneakyThrows;
import net.ow.shared.errorutils.dto.Error;
import net.ow.shared.errorutils.fixture.TestMessageSource;
import net.ow.shared.errorutils.fixture.TestServiceError;
import net.ow.shared.errorutils.fixture.TestableErrorMapper;
import net.ow.shared.errorutils.model.APIException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.test.util.ReflectionTestUtils;

public class ErrorMapperTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void shouldConvertApiExceptionToJson() {
        // Given
        var ex = new APIException(TestServiceError.TEST_ONE, 20, "October");
        var mapper = new TestableErrorMapper();
        var messageSource = new TestMessageSource();

        // When
        var error = mapper.toError(ex, messageSource);

        // Then
        assertJson(
                """
				{
				"id": "1234-5678-1234-5678",
				"code": "400-test-one",
				"title": "Error for Test One",
				"detail": "Created on 20 October"
				}""",
                error);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldAddMonitoringData() {
        var mapper = new TestableErrorMapper();
        var spanId = "1234abcd1234abcd";
        var traceId = spanId + spanId;

        var spanContext =
                SpanContext.create(
                        traceId, spanId, TraceFlags.getDefault(), TraceState.getDefault());
        var span = Span.wrap(spanContext);
        span.makeCurrent();

        // When
        var meta =
                (Map<String, Serializable>)
                        ReflectionTestUtils.invokeMethod(mapper, "getMonitoringMetaData");

        // Then
        assertEquals(traceId, meta.get("operation_Id"));
        assertEquals(spanId, meta.get("operationParent_Id"));
    }

    @Test
    void shouldAddMetaData() {
        var error =
                Error.builder()
                        .id("error-1")
                        .meta(Map.of("foo", "bar"))
                        .meta(Map.of("ham", "spam"))
                        .build();

        assertEquals("error-1", error.getId());
        assertEquals("bar", error.getMeta().get("foo"));
        assertEquals("spam", error.getMeta().get("ham"));
    }

    @SneakyThrows
    public static void assertJson(String expected, Object data) {
        var json =
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(data)
                        .replaceAll("\" :", "\":")
                        .replaceAll(System.lineSeparator(), "\n");
        JSONAssert.assertEquals(expected, json, JSONCompareMode.LENIENT);
    }
}
