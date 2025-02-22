package net.ow.shared.commonlog.provider.trace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

import jakarta.servlet.http.HttpServletRequest;
import net.ow.shared.commonlog.utils.TraceIdUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultTraceIdProviderTest {
    @InjectMocks private DefaultTraceIdProvider traceIdProvider;

    @Mock private HttpServletRequest request;

    @Test
    public void getTraceIdTest_OK() {
        String expectedTraceId = "generatedTraceId";

        try (var mockedStatic = mockStatic(TraceIdUtils.class)) {
            mockedStatic.when(TraceIdUtils::generateTraceId).thenReturn(expectedTraceId);

            String traceId = traceIdProvider.getTraceId(request);

            assertEquals(expectedTraceId, traceId);
        }
    }
}
