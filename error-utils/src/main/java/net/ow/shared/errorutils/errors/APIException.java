package net.ow.shared.errorutils.errors;

import java.io.Serializable;
import java.util.Map;
import net.ow.shared.errorutils.dto.ErrorSource;
import net.ow.shared.errorutils.util.LocaleMessageSource;

public class APIException extends RuntimeException {
    private final ServiceError error;

    private final Serializable[] messageParams;

    private ErrorSource source;

    private Map<String, Serializable> meta;

    private APIException(
            final ServiceError error,
            final Serializable[] messageParams,
            final ErrorSource source,
            final Map<String, Serializable> meta) {
        this.error = error;
        this.messageParams = messageParams;
        this.source = source;
        this.meta = meta;
    }

    public APIException(ServiceError error) {
        this.error = error;
        this.messageParams = null;
    }

    public APIException(ServiceError error, Throwable cause) {
        super(cause);
        this.error = error;
        this.messageParams = null;
    }

    public APIException(ServiceError error, Serializable... messageParams) {
        this.error = error;
        this.messageParams = messageParams;
    }

    public APIException(ServiceError error, Throwable cause, Serializable... messageParams) {
        super(cause);
        this.error = error;
        this.messageParams = messageParams;
    }

    public ServiceError getError() {
        return error;
    }

    public Map<String, Serializable> getMeta() {
        return meta;
    }

    public ErrorSource getSource() {
        return source;
    }

    public String getTitle(LocaleMessageSource messageSource) {
        return messageSource.getMessage(error.getTitle());
    }

    public String getMessage() {
        return error.getMessage();
    }

    public String getMessage(LocaleMessageSource messageSource) {
        return messageSource.getMessage(error.getMessage(), (Object[]) messageParams);
    }

    public APIException withSource(final ErrorSource source) {
        return this.source == source ? this : new APIException(this.error, this.messageParams, source, this.meta);
    }

    public APIException withMeta(final Map<String, Serializable> meta) {
        return this.meta == meta ? this : new APIException(this.error, this.messageParams, this.source, meta);
    }
}
