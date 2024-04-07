package net.ow.shared.errorutils.errors;

import java.io.Serializable;
import net.ow.shared.errorutils.model.ErrorProperties;
import org.springframework.http.HttpStatus;

public interface ServiceError extends Serializable {
    HttpStatus getStatus();

    String getCode();

    String getTitle();

    /**
     * To be used with LocaleMessageSource.getMessage(message, params)
     */
    String getMessage();

    default String getCodePrefix(HttpStatus status) {
        return status.value() + "-";
    }

    /**
     * Standard initialisation template, to be called from the constructor.
     */
    default void initialise(HttpStatus status, String code) {
        String errorCode = getCodePrefix(status) + code;

        setErrorProperties(ErrorProperties.builder()
                .status(status)
                .code(errorCode)
                .title("error." + errorCode + ".title")
                .message("error." + errorCode + ".message")
                .build());
    }

    /**
     * Implementation will generally look like the example below.
     *
     * <code>
     * this.errorProperties = errorProperties;
     * </code>
     */
    void setErrorProperties(ErrorProperties errorProperties);
}
