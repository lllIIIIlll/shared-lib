package net.ow.shared.commonlog.utils;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TraceIdUtils {
    public static String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}
