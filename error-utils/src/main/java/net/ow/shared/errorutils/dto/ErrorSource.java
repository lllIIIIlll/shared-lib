package net.ow.shared.errorutils.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An object containing reference to primary source of the error.
 *
 * @see <a href="https://jsonapi.org/format/#errors>?Error Objects</a>
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorSource implements Serializable {
    /**
     * A <a href="https://tools.ietf.org/html/rfc6901>JSON Pointer</a> to the value in the request document that
     * caused the error [e.g. "/data" for a primary data object, or "/data/attributes/title" for a specific attribute].
     * This Must point to a value in the request document that exists.
     */
    private String pointer;

    /**
     * Indicates which URI query parameter caused the error
     */
    private String parameter;

    /**
     * Indicates the name of a single request handler which caused the error.
     */
    private String header;

    public static ErrorSource withJsonPointer(String pointer) {
        return new ErrorSource(pointer, null, null);
    }

    public static ErrorSource withParameter(String parameter) {
        return new ErrorSource(null, parameter, null);
    }

    public static ErrorSource withHeader(String header) {
        return new ErrorSource(null, null, header);
    }
}
