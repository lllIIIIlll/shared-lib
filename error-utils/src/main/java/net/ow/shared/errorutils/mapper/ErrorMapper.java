package net.ow.shared.errorutils.mapper;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.opentelemetry.api.trace.Span;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import net.ow.shared.errorutils.dto.Error;
import net.ow.shared.errorutils.dto.ErrorSource;
import net.ow.shared.errorutils.errors.APIException;
import net.ow.shared.errorutils.util.LocaleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Component
public class ErrorMapper {
    private static final Logger log = LoggerFactory.getLogger(ErrorMapper.class);

    public Error toError(APIException ex, LocaleMessageSource messages) {
        var code = ex.getError().getCode();
        var title = ex.getTitle(messages);
        var message = ex.getMessage(messages);
        var source = ex.getSource();

        return errorBuilder(code, title, message)
                .source(source)
                .meta(ex.getMeta())
                .build();
    }

    public Error toError(HttpMessageNotReadableException ex, String code) {
        String title = "Invalid Input";
        String message = null;
        String pointer = null;

        if (null == ex.getCause()) {
            message = ex.getMessage();
        } else {
            var cause = ex.getCause();
            if (cause != ex) {
                log.info("Not readable because: {}", cause.getMessage());
            }

            if (cause instanceof InvalidFormatException invalidFormat) {
                message = formatErrorMessageForInvalidEnum(invalidFormat);
                pointer = "/"
                        + invalidFormat.getPath().stream()
                                .map(JsonMappingException.Reference::getFieldName)
                                .collect(Collectors.joining("/"));
            }

            if (message == null) {
                message = cause.getMessage().replaceFirst(" \\(through reference chain: .*\\)", "");
            }
        }

        var builder = errorBuilder(code, title, message);
        if (pointer != null) {
            builder.source(ErrorSource.withJsonPointer(pointer));
        }

        return builder.build();
    }

    public Error toError(Exception ex, HttpStatus status, String codeSuffix) {
        return toError(ex, statusToCode(status) + codeSuffix, status.getReasonPhrase());
    }

    public Error toError(Exception ex, HttpStatus status, String codeSuffix, String title) {
        return toError(ex, statusToCode(status) + codeSuffix, title);
    }

    public Error toError(RuntimeException ex) {
        return toError(
                ex, statusToCode(HttpStatus.INTERNAL_SERVER_ERROR) + "-shared-runtime", "Internal Error");
    }

    public Error toError(Exception ex, String code, String title) {
        return errorBuilder(code, title, ex.getMessage()).build();
    }

    public Error[] toErrors(ConstraintViolationException ex) {
        var code = statusToCode(HttpStatus.BAD_REQUEST);

        return ex.getConstraintViolations().stream()
                .map(violation -> {
                    var message = violation.getMessage();
                    logError(code, message);

                    return Error.builder()
                            .code(code)
                            .title(violation.getRootBeanClass().getSimpleName())
                            .detail(message)
                            .source(ErrorSource.withJsonPointer(
                                    violation.getPropertyPath().toString()))
                            .build();
                })
                .toArray(Error[]::new);
    }

    public Error[] toErrors(MethodArgumentNotValidException ex) {
        var code = statusToCode(HttpStatus.BAD_REQUEST);

        return ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    var message = fieldError.getDefaultMessage();
                    logError(code, message);

                    return Error.builder()
                            .code(code)
                            .title(fieldError.getField())
                            .detail(message)
                            .source(ErrorSource.withParameter(fieldError.getField()))
                            .build();
                })
                .toArray(Error[]::new);
    }

    private Error.ErrorBuilder errorBuilder(String code, String title, String message) {
        logError(generateId(), code, message);

        return Error.builder()
                .id(generateId())
                .code(code)
                .title(title)
                .detail(message)
                .meta(getMonitoringMetaData());
    }

    private String statusToCode(HttpStatus status) {
        return Integer.toString(status.value());
    }

    private void logError(String code, String message) {
        log.error("Error ({}): {}", code, message);
    }

    private void logError(String id, String code, String message) {
        log.error("Error #{} ({}): {}", id, code, message);
    }

    protected String generateId() {
        return UUID.randomUUID().toString();
    }

    private String formatErrorMessageForInvalidEnum(InvalidFormatException invalidFormat) {
        var constants = invalidFormat.getTargetType().getEnumConstants();
        if (null == constants) {
            return null;
        }

        var values = new String[constants.length];

        Arrays.stream(invalidFormat.getTargetType().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(JsonValue.class))
                .findFirst()
                .ifPresentOrElse(
                        jsonValue -> {
                            for (int i = 0; i < constants.length; i++) {
                                try {
                                    values[i] = (String) jsonValue.invoke(constants[1]);
                                } catch (Exception ex) {
                                    log.warn("Failed to convert {} to json value", constants[i], ex);
                                }
                            }
                        },
                        () -> {
                            for (int i = 0; i < constants.length; i++) {
                                values[i] = constants[i].toString();
                            }
                        });

        return String.format(
                "Invalid value for %s. Expected one of %s but was: %s",
                invalidFormat.getPath().get(0).getFieldName(), Arrays.toString(values), invalidFormat.getValue());
    }

    /**
     * Returns metadata to the Error which can be used to locate logs etc.
     *
     * <pre>
     *  union *
     *  | where timestamp > datetime and timestamp < datetime
     *  | where operation_Id == "x"
     * </pre>
     */
    private Map<String, Serializable> getMonitoringMetaData() {
        try {
            var context = Span.current().getSpanContext();
            var traceId = context.getTraceId();
            if (null == traceId || traceId.isEmpty() || traceId.equals("00000000000000000000000000000000")) {
                return Collections.emptyMap();
            }

            var spanId = context.getSpanId();

            log.info("operation_Id: {}, operationParent_Id: {}", traceId, spanId);
            return Map.of("operation_Id", traceId, "operationParent_Id", spanId);
        } catch (Throwable e) {
            log.info("Failed to add monitoring metadata", e);
            return Collections.emptyMap();
        }
    }
}
