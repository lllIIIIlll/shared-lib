package net.ow.shared.commonlog.provider.client.information;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultClientInformationProviderTest {
    @InjectMocks private DefaultClientInformationProvider clientInformationProvider;

    @Mock private HttpServletRequest request;

    @Test
    void getClientInformationTest_whenRemoteAddressIsNull_thenReturnsEmptyString() {
        when(request.getRemoteAddr()).thenReturn(null);
        String result = clientInformationProvider.getClientInformation(request);
        assertEquals("", result);
    }

    @Test
    void getClientInformationTest_whenRemoteAddressIsEmptyString_thenReturnsEmptyString() {
        when(request.getRemoteAddr()).thenReturn("");
        String result = clientInformationProvider.getClientInformation(request);
        assertEquals("", result);
    }

    @Test
    void getClientInformationTest_OK() {
        String remoteAddress = "192.168.1.1";
        when(request.getRemoteAddr()).thenReturn(remoteAddress);
        String result = clientInformationProvider.getClientInformation(request);
        assertEquals(remoteAddress, result);
    }
}
