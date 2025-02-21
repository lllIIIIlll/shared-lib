package net.ow.shared.common.servlet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.util.StreamUtils;

@ExtendWith(MockitoExtension.class)
class CachedHttpServletRequestWrapperTest {
    @InjectMocks private CachedHttpServletRequestWrapper cachedRequestWrapper;

    @Mock private HttpServletRequest mockRequest;

    @Mock private InputStream mockInputStream;

    @Test
    @SneakyThrows
    void getInputStreamTest_OK() {
        byte[] expectedPayload = "testPayload".getBytes();
        InputStream inputStream = new ByteArrayInputStream(expectedPayload);
        ServletInputStream servletInputStream = new DelegatingServletInputStream(inputStream);
        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        cachedRequestWrapper = new CachedHttpServletRequestWrapper(mockRequest);
        ServletInputStream actualInputStream = cachedRequestWrapper.getInputStream();

        assertNotNull(actualInputStream, "InputStream should not be null");
        byte[] actualPayload = StreamUtils.copyToByteArray(actualInputStream);

        assertEquals(expectedPayload.length, actualPayload.length, "Payload length should match");
        assertEquals(
                new String(expectedPayload),
                new String(actualPayload),
                "Payload content should match");
        assertTrue(actualInputStream.isFinished(), "InputStream should be finished");
    }

    @Test
    @SneakyThrows
    void getReaderTest_OK() {
        byte[] expectedPayload = "testPayload".getBytes();
        InputStream inputStream = new ByteArrayInputStream(expectedPayload);
        ServletInputStream servletInputStream = new DelegatingServletInputStream(inputStream);
        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        cachedRequestWrapper = new CachedHttpServletRequestWrapper(mockRequest);
        Reader reader = cachedRequestWrapper.getReader();

        assertNotNull(reader, "Reader should not be null");
        String actualPayload = IOUtils.toString(reader);

        assertEquals(new String(expectedPayload), actualPayload, "Payload content should match");
    }

    @Test
    @SneakyThrows
    void getReaderTest_whenEmptyPayload_thenReturnsEmptyReader() {
        byte[] expectedPayload = new byte[0];
        InputStream inputStream = new ByteArrayInputStream(expectedPayload);
        ServletInputStream servletInputStream = new DelegatingServletInputStream(inputStream);
        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        cachedRequestWrapper = new CachedHttpServletRequestWrapper(mockRequest);
        Reader reader = cachedRequestWrapper.getReader();

        assertNotNull(reader, "Reader should not be null");
        String actualPayload = IOUtils.toString(reader);

        assertEquals(new String(expectedPayload), actualPayload, "Payload content should be empty");
    }

    @Test
    @SneakyThrows
    void getReaderTest_whenSpecialCharacters_OK() {
        byte[] expectedPayload = "test@#%&*()".getBytes();
        InputStream inputStream = new ByteArrayInputStream(expectedPayload);
        ServletInputStream servletInputStream = new DelegatingServletInputStream(inputStream);
        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        cachedRequestWrapper = new CachedHttpServletRequestWrapper(mockRequest);
        Reader reader = cachedRequestWrapper.getReader();

        assertNotNull(reader, "Reader should not be null");
        String actualPayload = IOUtils.toString(reader);

        assertEquals(
                new String(expectedPayload),
                actualPayload,
                "Payload content should match special characters");
    }

    @Test
    @SneakyThrows
    void isFinishedTest_whenInputStreamHasBytes_thenReturnsFalse() {
        byte[] expectedPayload = "testPayload".getBytes();
        InputStream inputStream = new ByteArrayInputStream(expectedPayload);
        ServletInputStream servletInputStream = new DelegatingServletInputStream(inputStream);
        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        cachedRequestWrapper = new CachedHttpServletRequestWrapper(mockRequest);
        ServletInputStream actualInputStream = cachedRequestWrapper.getInputStream();

        assertFalse(actualInputStream.isFinished(), "InputStream should not be finished");
    }

    @Test
    @SneakyThrows
    void isFinishedTEst_whenInputStreamIsEmpty_thenReturnsTrue() {
        byte[] emptyPayload = new byte[0];
        InputStream inputStream = new ByteArrayInputStream(emptyPayload);
        ServletInputStream servletInputStream = new DelegatingServletInputStream(inputStream);
        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        cachedRequestWrapper = new CachedHttpServletRequestWrapper(mockRequest);
        ServletInputStream actualInputStream = cachedRequestWrapper.getInputStream();

        assertTrue(actualInputStream.isFinished(), "InputStream should be finished");
    }

    @Test
    @SneakyThrows
    void isFinishedTest_whenIOExceptionOccurs_thenReturnsFalse() {
        ServletInputStream actualInputStream = cachedRequestWrapper.getInputStream();

        Field cachedInputStreamField =
                CachedHttpServletRequestWrapper.CachedServletInputStream.class.getDeclaredField(
                        "cachedInputStream");
        cachedInputStreamField.setAccessible(true);

        when(mockInputStream.available()).thenThrow(new IOException("Simulated IOException"));
        cachedInputStreamField.set(actualInputStream, mockInputStream);

        assertFalse(
                actualInputStream.isFinished(),
                "InputStream should not be finished due to IOException");
    }

    @Test
    @SneakyThrows
    void isReadyTest_OK() {
        byte[] expectedPayload = "testPayload".getBytes();
        InputStream inputStream = new ByteArrayInputStream(expectedPayload);
        ServletInputStream servletInputStream = new DelegatingServletInputStream(inputStream);
        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        cachedRequestWrapper = new CachedHttpServletRequestWrapper(mockRequest);
        ServletInputStream actualInputStream = cachedRequestWrapper.getInputStream();

        assertTrue(actualInputStream.isReady(), "isReady should always return true");
    }

    @Test
    @SneakyThrows
    void readTest_whenBytesAvailable_thenReturnByte() {
        byte[] expectedPayload = "testPayload".getBytes();
        InputStream inputStream = new ByteArrayInputStream(expectedPayload);
        ServletInputStream servletInputStream = new DelegatingServletInputStream(inputStream);
        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        cachedRequestWrapper = new CachedHttpServletRequestWrapper(mockRequest);
        ServletInputStream actualInputStream = cachedRequestWrapper.getInputStream();

        int expectedByte = 't';
        int actualByte = actualInputStream.read();
        assertEquals(expectedByte, actualByte, "The read byte should match the expected byte");
    }

    @Test
    @SneakyThrows
    void readTest_whenEndOfStream_thenReturnMinusOne() {
        byte[] expectedPayload = "testPayload".getBytes();
        InputStream inputStream = new ByteArrayInputStream(expectedPayload);
        ServletInputStream servletInputStream = new DelegatingServletInputStream(inputStream);
        when(mockRequest.getInputStream()).thenReturn(servletInputStream);

        cachedRequestWrapper = new CachedHttpServletRequestWrapper(mockRequest);
        ServletInputStream actualInputStream = cachedRequestWrapper.getInputStream();
        while (actualInputStream.read() != -1)
            ;

        int actualByte = actualInputStream.read();
        assertEquals(-1, actualByte, "At the end of the stream, read should return -1");
    }
}
