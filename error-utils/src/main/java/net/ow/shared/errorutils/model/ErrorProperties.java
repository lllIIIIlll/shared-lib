package net.ow.shared.errorutils.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorProperties implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private HttpStatus status;

    private String code;

    private String title;

    private String message;

    public ErrorProperties() {}

    public ErrorProperties(HttpStatus status, String code, String title, String message) {
        this.status = status;
        this.code = code;
        this.title = title;
        this.message = message;
    }

    public static ErrorPropertiesBuilder builder() {
        return new ErrorPropertiesBuilder();
    }

    public static class ErrorPropertiesBuilder {
        private HttpStatus status;
        private String code;
        private String title;
        private String message;

        ErrorPropertiesBuilder() {}

        public ErrorPropertiesBuilder status(final HttpStatus status) {
            this.status = status;
            return this;
        }

        public ErrorPropertiesBuilder code(final String code) {
            this.code = code;
            return this;
        }

        public ErrorPropertiesBuilder title(final String title) {
            this.title = title;
            return this;
        }

        public ErrorPropertiesBuilder message(final String message) {
            this.message = message;
            return this;
        }

        public ErrorProperties build() {
            return new ErrorProperties(this.status, this.code, this.title, this.message);
        }

        public String toString() {
            return "ErrorProperties.ErrorPropertiesBuilder(status=" + this.status + ", code=" + this.code + ", title="
                    + this.title + ", message=" + this.message + ")";
        }
    }
}
