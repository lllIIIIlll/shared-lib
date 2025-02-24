package net.ow.shared.commonlog.provider.client.information;

import com.nimbusds.jwt.JWTClaimsSet;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.ow.shared.common.utils.JWTUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;

@Slf4j
@Setter
@NonNullApi
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JWTClientInformationProvider implements ClientInformationProvider {
    private static final String BEARER = "Bearer ";

    private Set<String> claimKeys;

    @Override
    public String getClientInformation(HttpServletRequest request) {
        if (null == claimKeys || claimKeys.isEmpty()) {
            return Strings.EMPTY;
        }

        JWTClaimsSet claims;
        try {
            String jwt = request.getHeader(HttpHeaders.AUTHORIZATION).replace(BEARER, "");
            claims = JWTUtils.getClaimsSet(jwt);
        } catch (ParseException e) {
            log.error("Fail to parse JWT with error  - {}", e.getMessage());
            return Strings.EMPTY;
        }

        StringBuilder stringBuilder = new StringBuilder();
        claimKeys.forEach(
                claimKey -> {
                    Object claimValue = claims.getClaim(claimKey);
                    if (null != claimValue) {
                        stringBuilder
                                .append("[")
                                .append(claimKey)
                                .append("] ")
                                .append(claimValue)
                                .append(" ");
                    }
                });

        return stringBuilder.toString().trim();
    }
}
