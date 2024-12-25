package net.ow.shared.errorutils.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

/**
 * Error objects provide additional information about problems encountered while performing an operation.
 *
 * @see <a href="https://jsonapi.org/format/#errors">Error Objects</a>
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Error {
    private String id;

    private String code;

    private String title;

    /**
     * Error to be displayed in user.
     * Can be situation-specific but:
     * - DO NOT include personal/account info
     * - DO NOT include information ro stock trace
     */
    private String detail;

    private ErrorSource source;

    /**
     * Anything else that might be useful to the user and/or support
     */
    private Map<String, Serializable> meta;

    public static class ErrorBuilder {
        public ErrorBuilder meta(Map<String, Serializable> data) {
            if (null != data && !data.isEmpty()) {
                if (null == this.meta) {
                    this.meta = data;
                } else {
                    this.meta = new HashMap<>(this.meta);
                    this.meta.putAll(data);
                }
            }

            return this;
        }
    }
}
