package net.ow.shared.commonlog.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.MDC;

class TraceIdContextTest {
    private MockedStatic<MDC> mockedMDC;

    @BeforeEach
    public void setUp() {
        mockedMDC = mockStatic(MDC.class);
    }

    @AfterEach
    public void tearDown() {
        if (mockedMDC != null) {
            mockedMDC.close();
        }
    }

    @Test
    void getTraceIdTest_whenTraceIdExists_thenReturnsTraceId() {
        String expectedTraceId = "12345";
        mockedMDC.when(() -> MDC.get(TraceIdContext.TRACE_ID_KEY)).thenReturn(expectedTraceId);

        String actualTraceId = TraceIdContext.getTraceId();

        assertEquals(expectedTraceId, actualTraceId);
    }

    @Test
    void getTraceIdTest_whenTraceIdDoesNotExist_thenReturnsEmptyString() {
        mockedMDC.when(() -> MDC.get(TraceIdContext.TRACE_ID_KEY)).thenReturn(null);

        String actualTraceId = TraceIdContext.getTraceId();

        assertEquals("", actualTraceId);
    }

    @Test
    void setTraceIdTest_whenNullTraceId_thenNoMDCPut() {
        TraceIdContext.setTraceId(null);
        mockedMDC.verify(() -> MDC.put(TraceIdContext.TRACE_ID_KEY, null), never());
    }

    @Test
    void setTraceIdTest_whenEmptyTraceId_thenNoMDCPut() {
        TraceIdContext.setTraceId("");
        mockedMDC.verify(() -> MDC.put(TraceIdContext.TRACE_ID_KEY, ""), never());
    }

    @Test
    void setTraceIdTest_whenValidTraceId_thenMDCPutCalled() {
        String traceId = "12345";
        TraceIdContext.setTraceId(traceId);
        mockedMDC.verify(() -> MDC.put(TraceIdContext.TRACE_ID_KEY, traceId), times(1));
    }

    @Test
    void clearTraceIdTest_OK() {
        TraceIdContext.clearTraceId();

        mockedMDC.verify(() -> MDC.remove(TraceIdContext.TRACE_ID_KEY), times(1));
    }
}
