package net.ow.shared.commonlog.provider.client.information;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.SneakyThrows;
import net.ow.shared.common.utils.JWTUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
class JWTClientInformationProviderTest {
    @InjectMocks private JWTClientInformationProvider jwtClientInformationProvider;

    @Mock private HttpServletRequest httpServletRequest;

    @Mock private JWTClaimsSet jwtClaimsSet;

    private MockedStatic<JWTUtils> jwtUtilsMockedStatic;

    @BeforeEach
    void setUp() {
        jwtClientInformationProvider.setClaimKeys(new HashSet<>());
        jwtUtilsMockedStatic = mockStatic(JWTUtils.class);
    }

    @AfterEach
    void tearDown() {
        jwtUtilsMockedStatic.close();
    }

    @Test
    @SuppressWarnings("all")
    void getClientInformationTest_whenClaimKeysNull_thenReturnsEmptyString() {
        jwtClientInformationProvider.setClaimKeys(null);
        assertTrue(jwtClientInformationProvider.getClientInformation(httpServletRequest).isEmpty());
    }

    @Test
    void getClientInformationTest_whenClaimKeysEmpty_thenReturnsEmptyString() {
        jwtClientInformationProvider.setClaimKeys(Collections.emptySet());
        assertTrue(jwtClientInformationProvider.getClientInformation(httpServletRequest).isEmpty());
    }

    @Test
    @SneakyThrows
    void getClientInformationTest_whenValidJWT_thenReturnsClientInformation() {
        String jwt = "valid.jwt.token";

        Set<String> claimKeys = new HashSet<>();
        claimKeys.add("sub");
        claimKeys.add("email");
        jwtClientInformationProvider.setClaimKeys(claimKeys);

        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + jwt);
        when(JWTUtils.getClaimsSet(jwt)).thenReturn(jwtClaimsSet);

        when(jwtClaimsSet.getClaim("sub")).thenReturn("12345");
        when(jwtClaimsSet.getClaim("email")).thenReturn("test@example.com");

        String result = jwtClientInformationProvider.getClientInformation(httpServletRequest);

        assertEquals("[sub] 12345 [email] test@example.com", result);
    }

    @Test
    @SneakyThrows
    void getClientInformationTest_whenInvalidJWT_thenReturnsEmptyString() {
        String jwt = "invalid.jwt.token";

        Set<String> claimKeys = new HashSet<>();
        claimKeys.add("sub");
        jwtClientInformationProvider.setClaimKeys(claimKeys);

        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + jwt);
        when(JWTUtils.getClaimsSet(jwt)).thenThrow(new ParseException("Invalid JWT", 0));

        String result = jwtClientInformationProvider.getClientInformation(httpServletRequest);
        assertEquals("", result);
    }

    @Test
    void getClientInformationTest_whenClaimKeyNotInJWT_thenReturnsEmptyString()
            throws ParseException {
        String jwt = "valid.jwt.token";

        Set<String> claimKeys = new HashSet<>();
        claimKeys.add("sub");
        jwtClientInformationProvider.setClaimKeys(claimKeys);

        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + jwt);
        when(JWTUtils.getClaimsSet(jwt)).thenReturn(jwtClaimsSet);

        when(jwtClaimsSet.getClaim("sub")).thenReturn(null);

        String result = jwtClientInformationProvider.getClientInformation(httpServletRequest);

        assertEquals("", result);
    }
}
