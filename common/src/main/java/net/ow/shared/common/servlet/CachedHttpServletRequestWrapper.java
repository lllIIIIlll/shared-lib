package net.ow.shared.common.servlet;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

public class CachedHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] cachedPayload;

    public CachedHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        InputStream requestInputStream = request.getInputStream();
        this.cachedPayload = StreamUtils.copyToByteArray(requestInputStream);
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedServletInputStream(this.cachedPayload);
    }

    @Override
    public BufferedReader getReader() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedPayload);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }

    @Slf4j
    @SuppressWarnings("all")
    class CachedServletInputStream extends ServletInputStream {
        private final InputStream cachedInputStream;

        public CachedServletInputStream(byte[] cachedBody) {
            this.cachedInputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            try {
                return cachedInputStream.available() == 0;
            } catch (IOException exp) {
                log.error(exp.getMessage());
            }
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return cachedInputStream.read();
        }
    }
}
