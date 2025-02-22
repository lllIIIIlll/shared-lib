package net.ow.shared.commonlog.utils;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class TraceIdUtilsTest {
    @Test
    void generateTraceId_ShouldReturnNonNullString() {
        String traceId = TraceIdUtils.generateTraceId();
        assertNotNull(traceId, "TraceId should not be null");
    }

    @Test
    void generateTraceId_ShouldReturnUniqueIdsOnEachCall() {
        String traceId1 = TraceIdUtils.generateTraceId();
        String traceId2 = TraceIdUtils.generateTraceId();
        assertNotEquals(traceId1, traceId2, "TraceIds should be different");
    }
}
