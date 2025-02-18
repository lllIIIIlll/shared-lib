package net.ow.shared.common.utils;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;
import java.text.ParseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JWTUtils {
    @Nullable
    public static Object getClaim(@NonNull String jwt, @NonNull String key) throws ParseException {
        if (jwt.isEmpty()) {
            return null;
        }

        JWTClaimsSet claims = JWTParser.parse(jwt).getJWTClaimsSet();
        return claims.getClaim(key);
    }
}
