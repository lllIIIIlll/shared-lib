package net.ow.shared.commonlog.provider.client.information;

import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.http.HttpServletRequest;

@NonNullApi
@FunctionalInterface
public interface ClientInformationProvider {
    /**
     * Retrieves client information from the HTTP request.
     *
     * <p>This method is typically used for logging or diagnostic purposes to obtain the information
     * of the client initiating the request.
     *
     * @param request The HttpServletRequest object containing all parameters and attributes of the
     *     client request.
     * @return The client's information, or an empty string if the client information is null or
     *     blank.
     */
    String getClientInformation(HttpServletRequest request);
}
