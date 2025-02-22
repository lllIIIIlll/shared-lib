package net.ow.shared.commonlog.filter;

import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import net.ow.shared.commonlog.context.TraceIdContext;
import net.ow.shared.commonlog.provider.trace.DefaultTraceIdProvider;
import net.ow.shared.commonlog.provider.trace.TraceIdProvider;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

@Setter
@Order(1)
@NonNullApi
public class TraceIdRequestLoggingFilter extends AbstractRequestLoggingFilter {
    public static final TraceIdProvider TRACE_ID_PROVIDER = new DefaultTraceIdProvider();

    private TraceIdProvider traceIdProvider;

    public TraceIdRequestLoggingFilter() {
        traceIdProvider = TRACE_ID_PROVIDER;
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        String traceId = traceIdProvider.getTraceId(request);
        TraceIdContext.setTraceId(traceId);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        TraceIdContext.clearTraceId();
    }
}
