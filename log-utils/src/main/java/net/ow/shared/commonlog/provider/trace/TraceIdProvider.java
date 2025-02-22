package net.ow.shared.commonlog.provider.trace;

import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.http.HttpServletRequest;

@NonNullApi
@FunctionalInterface
public interface TraceIdProvider {
    /**
     * Retrieves trace id from the HTTP request.
     *
     * <p>This method generates and returns a trace ID used to track the request's flow through the
     * system.
     *
     * @param request HTTP request object, this parameter is currently unused but may be used for
     *     future extensions
     * @return Generated trace ID, guaranteed to be non-null
     */
    String getTraceId(HttpServletRequest request);
}
