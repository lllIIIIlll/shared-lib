package net.ow.shared.common.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import java.text.ParseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class JWTUtilsTest {
    private MockedStatic<JWTParser> mockedJWTParser;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        mockedJWTParser = mockStatic(JWTParser.class);

        JWT jwt = mock(JWT.class);
        when(JWTParser.parse(anyString())).thenReturn(jwt);

        JWTClaimsSet claimsSet = mock(JWTClaimsSet.class);
        when(jwt.getJWTClaimsSet()).thenReturn(claimsSet);
        when(claimsSet.getClaim("name")).thenReturn("John Doe");
        when(claimsSet.getClaim("age")).thenReturn(30);
        when(claimsSet.getClaim("nonExistentKey")).thenReturn(null);
    }

    @AfterEach
    void tearDown() {
        mockedJWTParser.close();
    }

    @Test
    @SneakyThrows
    void getClaimTest_whenEmptyJWT_thenReturnsNull() {
        Object claim = JWTUtils.getClaim("", "name", String.class);
        assertNull(claim);
    }

    @Test
    @SneakyThrows
    void getClaimTest_whenValidJWTAndKey_thenReturnsValue() {
        Object name = JWTUtils.getClaim("valid.jwt.token", "name", String.class);
        assertEquals("John Doe", name);
    }

    @Test
    @SneakyThrows
    void getClaimTest_whenValidJWTAndInvalidKey_thenReturnsNull() {
        Object nonExistentClaim =
                JWTUtils.getClaim("valid.jwt.token", "nonExistentKey", String.class);
        assertNull(nonExistentClaim);
    }

    @Test
    void getClaimTest_whenInvalidJWT_thenThrowsParseException() {
        mockedJWTParser
                .when(() -> JWTParser.parse("invalid.jwt.token"))
                .thenThrow(ParseException.class);

        String invalidJwt = "invalid.jwt.token";
        assertThrows(
                ParseException.class,
                () -> {
                    JWTUtils.getClaim(invalidJwt, "name", String.class);
                });
    }

    @Test
    @SneakyThrows
    void getClaimTest_whenTypeMismatch_thenThrowsClassCastException() {
        assertThrows(
                ClassCastException.class,
                () -> {
                    JWTUtils.getClaim("valid.jwt.token", "name", Integer.class);
                });
    }
}
