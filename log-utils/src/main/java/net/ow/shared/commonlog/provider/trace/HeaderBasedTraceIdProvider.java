package net.ow.shared.commonlog.provider.trace;

import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@NonNullApi
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HeaderBasedTraceIdProvider implements TraceIdProvider {
    public static final TraceIdProvider DEFAULT_FALLBACK_PROVIDER = new DefaultTraceIdProvider();

    private String traceIdHeader;

    private TraceIdProvider fallbackProvider;

    public HeaderBasedTraceIdProvider(String traceIdHeader) {
        this.fallbackProvider = DEFAULT_FALLBACK_PROVIDER;
        this.traceIdHeader = traceIdHeader;
    }

    @Override
    public String getTraceId(HttpServletRequest request) {
        if (null == traceIdHeader) {
            log.warn("traceIdHeader is not set, using fallback provider.");
            return fallbackProvider.getTraceId(request);
        }

        String traceId = request.getHeader(traceIdHeader);
        return null != traceId ? traceId : fallbackProvider.getTraceId(request);
    }
}
