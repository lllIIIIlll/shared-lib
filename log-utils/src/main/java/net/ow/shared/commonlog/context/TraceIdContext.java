package net.ow.shared.commonlog.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.MDC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TraceIdContext {
    public static final String TRACE_ID_KEY = "requestId";

    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID_KEY);
        return null == traceId ? Strings.EMPTY : traceId;
    }

    public static void setTraceId(String traceId) {
        if (null == traceId || traceId.isEmpty()) {
            return;
        }

        MDC.put(TRACE_ID_KEY, traceId);
    }

    public static void clearTraceId() {
        MDC.remove(TRACE_ID_KEY);
    }
}
