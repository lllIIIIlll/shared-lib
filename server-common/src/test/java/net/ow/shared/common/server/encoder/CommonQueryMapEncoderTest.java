package net.ow.shared.common.server.encoder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import lombok.Data;
import net.ow.shared.common.server.annotation.QueryParameter;
import org.junit.jupiter.api.Test;

class CommonQueryMapEncoderTest {
    private final CommonQueryMapEncoder encoder = new CommonQueryMapEncoder();

    @Test
    void encodeTest_whenFieldWithQueryNameAnnotation_thenUsesAnnotationValueAsParameterName() {
        String value = "value";

        AnnotatedQueryParameter parameter = new AnnotatedQueryParameter();
        parameter.setParameter(value);

        Map<String, Object> result = encoder.encode(parameter);

        assertEquals(1, result.size());
        assertEquals(value, result.get("customName"));
    }

    @Test
    void encodeTest_whenFieldWithoutQueryNameAnnotation_thenUsesFieldNameAsParameterName() {
        String value = "value";

        PlainQueryParameter parameter = new PlainQueryParameter();
        parameter.setParameter(value);

        Map<String, Object> result = encoder.encode(parameter);

        assertEquals(1, result.size());
        assertEquals(value, result.get("parameter"));
    }

    @Test
    void encodeTest_whenFieldWithNullValue_thenStoresNullInMap() {
        AnnotatedQueryParameter parameter = new AnnotatedQueryParameter();
        parameter.setParameter(null);

        Map<String, Object> result = encoder.encode(parameter);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("customName"));
        assertNull(result.get("customName"));
    }

    @Data
    private static class AnnotatedQueryParameter {
        @QueryParameter(name = "customName")
        private String parameter;
    }

    @Data
    private static class PlainQueryParameter {
        private String parameter;
    }
}
