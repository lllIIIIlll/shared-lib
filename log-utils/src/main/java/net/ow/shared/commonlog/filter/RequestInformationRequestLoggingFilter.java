package net.ow.shared.commonlog.filter;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.core.instrument.util.IOUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ow.shared.common.servlet.CachedHttpServletRequestWrapper;
import net.ow.shared.commonlog.provider.client.information.ClientInformationProvider;
import net.ow.shared.commonlog.provider.client.information.DefaultClientInformationProvider;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

@Setter
@Order(2)
@NonNullApi
@NoArgsConstructor
public class RequestInformationRequestLoggingFilter extends AbstractRequestLoggingFilter {
    private static Logger log =
            LoggerFactory.getLogger(RequestInformationRequestLoggingFilter.class);

    public static final String REQUEST_TIMESTAMP_KEY = "requestTimestamp";

    public static final ClientInformationProvider DEFAULT_CLIENT_INFORMATION_PROVIDER =
            new DefaultClientInformationProvider();

    private ClientInformationProvider clientInformationProvider;

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        log.info("Request Received: {}", getRequestInformation(request));
        MDC.put(REQUEST_TIMESTAMP_KEY, String.valueOf(Clock.systemUTC().millis()));
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        long requestTimestamp = Long.parseLong(MDC.get(REQUEST_TIMESTAMP_KEY));
        MDC.remove(REQUEST_TIMESTAMP_KEY);

        long requestEndTime = Clock.systemUTC().millis();
        long duration = requestEndTime - requestTimestamp;

        log.info("Request processed within {}ms", duration);
    }

    private String getRequestInformation(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();

        String requestURI = getRequestURI(request);
        stringBuilder.append(requestURI);

        String clientInformation = getClientInformation(request);
        if (!clientInformation.isBlank()) {
            stringBuilder.append(" ").append(clientInformation);
        }

        String requestBody = getRequestBody(request);
        if (!requestBody.isBlank()) {
            stringBuilder.append("\n");
            stringBuilder.append(requestBody);
        }

        return stringBuilder.toString();
    }

    private String getClientInformation(HttpServletRequest request) {
        if (!isIncludeClientInfo()) {
            return Strings.EMPTY;
        }

        return clientInformationProvider.getClientInformation(request);
    }

    private String getRequestURI(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
                .append("[")
                .append(request.getMethod())
                .append("] ")
                .append(request.getRequestURI());

        if (isIncludeQueryString()) {
            String queryString = request.getQueryString();
            if (queryString != null) {
                stringBuilder.append('?').append(queryString);
            }
        }

        return stringBuilder.toString();
    }

    private String getRequestBody(HttpServletRequest request) {
        if (!isIncludePayload()) {
            return Strings.EMPTY;
        }

        StringBuilder stringBuilder = new StringBuilder();

        try {
            CachedHttpServletRequestWrapper cachedHttpServletRequestWrapper =
                    new CachedHttpServletRequestWrapper(request);
            String requestBody =
                    IOUtils.toString(
                            cachedHttpServletRequestWrapper.getInputStream(),
                            StandardCharsets.UTF_8);
            if (!requestBody.isEmpty()) {
                stringBuilder.append("[Request Body]\n").append(requestBody);
            }
        } catch (IOException e) {
            log.error("Cannot log request body. Error: {}", e.getMessage());
        }

        return stringBuilder.toString();
    }
}
