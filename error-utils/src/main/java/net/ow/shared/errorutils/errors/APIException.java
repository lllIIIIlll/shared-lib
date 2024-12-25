package net.ow.shared.errorutils.errors;

import java.io.Serializable;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import net.ow.shared.errorutils.dto.ErrorSource;
import net.ow.shared.errorutils.util.LocaleMessageSource;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class APIException extends RuntimeException {
    private final ServiceError error;

    private final Serializable[] messageParams;

    @With
    private ErrorSource source;

    @With
    private Map<String, Serializable> meta;

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

    public String getTitle(LocaleMessageSource messageSource) {
        return messageSource.getMessage(error.getTitle());
    }

    public String getMessage() {
        return error.getMessage();
    }

    public String getMessage(LocaleMessageSource messageSource) {
        return messageSource.getMessage(error.getMessage(), (Object[]) messageParams);
    }
}
