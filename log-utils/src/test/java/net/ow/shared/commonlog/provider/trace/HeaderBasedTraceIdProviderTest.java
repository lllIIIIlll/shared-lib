package net.ow.shared.commonlog.provider.trace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HeaderBasedTraceIdProviderTest {
    @InjectMocks private HeaderBasedTraceIdProvider traceIdProvider;

    @Mock private HttpServletRequest request;

    @Mock private TraceIdProvider fallbackProvider;

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    private static final String TRACE_ID = "trace-id";

    private static final String FALLBACK_TRACE_ID = "fallback-trace-id";

    @Test
    @SuppressWarnings("all")
    void getTraceIdTest_whenTraceIdHeaderIsNull_thenUsesFallbackProvider() {
        when(fallbackProvider.getTraceId(request)).thenReturn(FALLBACK_TRACE_ID);

        traceIdProvider = new HeaderBasedTraceIdProvider(null, fallbackProvider);
        String traceId = traceIdProvider.getTraceId(request);

        assertEquals(FALLBACK_TRACE_ID, traceId);
    }

    @Test
    void getTraceIdTest_whenTraceIdHeaderExists_thenReturnsHeaderValue() {
        when(request.getHeader(TRACE_ID_HEADER)).thenReturn(TRACE_ID);

        traceIdProvider = new HeaderBasedTraceIdProvider(TRACE_ID_HEADER);
        String traceId = traceIdProvider.getTraceId(request);

        assertEquals(TRACE_ID, traceId);
    }

    @Test
    void getTraceIdTest_whenTraceIdHeaderDoesNotExist_thenUsesFallbackProvider() {
        when(request.getHeader(TRACE_ID_HEADER)).thenReturn(null);
        when(fallbackProvider.getTraceId(request)).thenReturn(FALLBACK_TRACE_ID);

        traceIdProvider = new HeaderBasedTraceIdProvider(TRACE_ID_HEADER, fallbackProvider);
        String traceId = traceIdProvider.getTraceId(request);

        assertEquals(FALLBACK_TRACE_ID, traceId);
    }
}
