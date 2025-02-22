package net.ow.shared.commonlog.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import net.ow.shared.commonlog.context.TraceIdContext;
import net.ow.shared.commonlog.provider.trace.TraceIdProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraceIdRequestLoggingFilterTest {
    @InjectMocks private TraceIdRequestLoggingFilter traceIdRequestLoggingFilter;

    @Mock private HttpServletRequest request;

    @Mock private TraceIdProvider traceIdProvider;

    @BeforeEach
    void setUp() {
        traceIdRequestLoggingFilter.setTraceIdProvider(traceIdProvider);
    }

    @Test
    void beforeRequestTest_shouldGenerateTraceId() {
        String traceId = "trace-id";
        when(traceIdProvider.getTraceId(request)).thenReturn(traceId);

        traceIdRequestLoggingFilter.beforeRequest(request, "message");

        assertEquals(traceId, TraceIdContext.getTraceId());
    }

    @Test
    void afterRequest_shouldClearTraceId() {
        TraceIdContext.setTraceId("existingTraceId");

        traceIdRequestLoggingFilter.afterRequest(request, "message");

        assertTrue(TraceIdContext.getTraceId().isEmpty());
    }
}
