package net.ow.shared.commonlog.provider.client.information;

import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

@NonNullApi
@NoArgsConstructor
public class DefaultClientInformationProvider implements ClientInformationProvider {
    /**
     * Retrieves client information from the HTTP request.
     *
     * <p>Extracts the client's remote address from the HTTP request. If the remote address is null
     * or blank, it returns an empty string. This method is typically used for logging or diagnostic
     * purposes to obtain the IP address of the client initiating the request.
     *
     * @param request The HttpServletRequest object containing all parameters and attributes of the
     *     client request.
     * @return The client's remote address, or an empty string if the remote address is null or
     *     blank.
     */
    @Override
    public String getClientInformation(HttpServletRequest request) {
        String remoteAddress = request.getRemoteAddr();
        return null == remoteAddress || remoteAddress.isBlank() ? Strings.EMPTY : remoteAddress;
    }
}
