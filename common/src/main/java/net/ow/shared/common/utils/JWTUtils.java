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
    public static <T> T getClaim(@NonNull String jwt, @NonNull String key, @NonNull Class<T> type)
            throws ParseException {
        if (jwt.isEmpty()) {
            return null;
        }

        JWTClaimsSet claims = JWTParser.parse(jwt).getJWTClaimsSet();

        Object claim = claims.getClaim(key);
        if (claim == null) {
            return null;
        }

        if (!type.isInstance(claim)) {
            throw new ClassCastException("Claim cannot be cast to " + type.getName());
        }

        return type.cast(claim);
    }
}
