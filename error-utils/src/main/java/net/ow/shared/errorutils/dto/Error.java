package net.ow.shared.errorutils.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Error objects provide additional information about problems encountered while performing an operation.
 *
 * @see <a href="https://jsonapi.org/format/#errors">Error Objects</a>
 */
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

    private Error(
            String id, String code, String title, String detail, ErrorSource source, Map<String, Serializable> meta) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.detail = detail;
        this.source = source;
        this.meta = meta;
    }

    public static ErrorBuilder builder() {
        return new ErrorBuilder();
    }

    public static class ErrorBuilder {
        private String id;
        private String code;
        private String title;
        private String detail;
        private ErrorSource source;
        private Map<String, Serializable> meta;

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

        ErrorBuilder() {}

        public ErrorBuilder id(final String id) {
            this.id = id;
            return this;
        }

        public ErrorBuilder code(final String code) {
            this.code = code;
            return this;
        }

        public ErrorBuilder title(final String title) {
            this.title = title;
            return this;
        }

        public ErrorBuilder detail(final String detail) {
            this.detail = detail;
            return this;
        }

        public ErrorBuilder source(final ErrorSource source) {
            this.source = source;
            return this;
        }

        public Error build() {
            return new Error(this.id, this.code, this.title, this.detail, this.source, this.meta);
        }

        public String toString() {
            return "Error.ErrorBuilder(id=" + this.id + ", code=" + this.code + ", title=" + this.title + ", detail="
                    + this.detail + ", source=" + this.source + ", meta=" + this.meta + ")";
        }
    }
}
