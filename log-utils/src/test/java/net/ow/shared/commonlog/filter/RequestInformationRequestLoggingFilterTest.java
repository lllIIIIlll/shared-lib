package net.ow.shared.commonlog.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import lombok.SneakyThrows;
import net.ow.shared.commonlog.provider.client.information.ClientInformationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.test.util.ReflectionTestUtils;

@SuppressWarnings("all")
@ExtendWith({MockitoExtension.class})
class RequestInformationRequestLoggingFilterTest {

    @InjectMocks
    private RequestInformationRequestLoggingFilter requestInformationRequestLoggingFilter;

    @Mock private HttpServletRequest request;

    @Mock private ClientInformationProvider clientInformationProvider;

    @Mock private Logger logger;

    @Mock private Clock clock;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(requestInformationRequestLoggingFilter, "log", logger);
        requestInformationRequestLoggingFilter.setClientInformationProvider(
                clientInformationProvider);
    }

    @Test
    void beforeRequestTest_whenOnlyRequestURILog_OK() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");

        requestInformationRequestLoggingFilter.beforeRequest(request, "Test message");

        verify(logger, times(1)).info("Request Received: {}", "[GET] /test");
        verify(request, times(0)).getQueryString();
        verify(clientInformationProvider, times(0)).getClientInformation(request);
    }

    @Test
    void beforeRequestTest_whenIncludeQueryStringLog_OK() {
        requestInformationRequestLoggingFilter.setIncludeQueryString(true);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getQueryString()).thenReturn("param1=value1");

        requestInformationRequestLoggingFilter.beforeRequest(request, "Test message");

        verify(logger, times(1)).info("Request Received: {}", "[GET] /test?param1=value1");
        verify(clientInformationProvider, times(0)).getClientInformation(request);
    }

    @Test
    void beforeRequestTest_whenIncludeClientInfoLog_OK() {
        requestInformationRequestLoggingFilter.setIncludeClientInfo(true);

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/test");
        when(clientInformationProvider.getClientInformation(request)).thenReturn("Client Info");

        requestInformationRequestLoggingFilter.beforeRequest(request, "Test message");

        verify(logger, times(1)).info("Request Received: {}", "[POST] /test Client Info");
    }

    @Test
    @SneakyThrows
    void beforeRequestTest_whenIncludePayload_OK() {
        requestInformationRequestLoggingFilter.setIncludePayload(true);

        byte[] requestBody = "Request Body".getBytes();
        InputStream inputStream = new ByteArrayInputStream(requestBody);
        ServletInputStream servletInputStream = new DelegatingServletInputStream(inputStream);

        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getInputStream()).thenReturn(servletInputStream);

        requestInformationRequestLoggingFilter.beforeRequest(request, "Test message");

        verify(logger, times(1))
                .info("Request Received: {}", "[PUT] /test\n[Request Body]\nRequest Body");
    }

    @Test
    @SneakyThrows
    void beforeRequestTest_whenIOExceptionDuringGetPayload_OK() {
        requestInformationRequestLoggingFilter.setIncludePayload(true);

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getInputStream()).thenThrow(new IOException("IO Error"));

        requestInformationRequestLoggingFilter.beforeRequest(request, "Test message");

        verify(logger, times(1)).info("Request Received: {}", "[POST] /test");
        verify(logger, times(1)).error("Cannot log request body. Error: {}", "IO Error");
    }

    @Test
    @SneakyThrows
    void beforeRequestTest_whenIncludeAllInformation_OK() {
        requestInformationRequestLoggingFilter.setIncludeQueryString(true);
        requestInformationRequestLoggingFilter.setIncludeClientInfo(true);
        requestInformationRequestLoggingFilter.setIncludePayload(true);

        byte[] requestBody = "Request Body".getBytes();
        InputStream inputStream = new ByteArrayInputStream(requestBody);
        ServletInputStream servletInputStream = new DelegatingServletInputStream(inputStream);

        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getQueryString()).thenReturn("param1=value1");
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(clientInformationProvider.getClientInformation(request)).thenReturn("Client Info");

        requestInformationRequestLoggingFilter.beforeRequest(request, "Test message");

        verify(logger, times(1))
                .info(
                        "Request Received: {}",
                        "[PUT] /test?param1=value1 Client Info\n[Request Body]\nRequest Body");
    }

    @Test
    void beforeRequestTest_whenTimestampAddedToMDC_OK() {
        long requestStartTime = Instant.now().toEpochMilli();

        try (var clockMockedStatic = mockStatic(Clock.class)) {
            clockMockedStatic.when(Clock::systemUTC).thenReturn(clock);
            when(clock.millis()).thenReturn(requestStartTime);

            when(request.getMethod()).thenReturn("GET");
            when(request.getRequestURI()).thenReturn("/test");

            requestInformationRequestLoggingFilter.beforeRequest(request, "Test message");

            String timestamp =
                    MDC.get(RequestInformationRequestLoggingFilter.REQUEST_TIMESTAMP_KEY);
            assertNotNull(timestamp, "Timestamp should be added to MDC");

            assertEquals(requestStartTime, Long.parseLong(timestamp));
        }
    }

    @Test
    @SneakyThrows
    void afterRequest_WhenRequestTimestampExists_LogsDuration() {
        Instant requestStartTime = Instant.now();
        Instant requestEndTime = requestStartTime.plusMillis(1000);
        long duartion = Duration.between(requestStartTime, requestEndTime).toMillis();
        MDC.put(
                RequestInformationRequestLoggingFilter.REQUEST_TIMESTAMP_KEY,
                String.valueOf(requestStartTime.toEpochMilli()));

        try (var clockMockedStatic = mockStatic(Clock.class)) {
            clockMockedStatic.when(Clock::systemUTC).thenReturn(clock);
            when(clock.millis()).thenReturn(requestEndTime.toEpochMilli());

            requestInformationRequestLoggingFilter.afterRequest(request, "Test message");

            verify(logger, times(1)).info("Request processed within {}ms", duartion);
            assertNull(MDC.get(RequestInformationRequestLoggingFilter.REQUEST_TIMESTAMP_KEY));
        }
    }
}
