package net.ow.shared.errorutils.errors;

import java.nio.file.AccessDeniedException;
import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ow.shared.errorutils.dto.APIResponse;
import net.ow.shared.errorutils.dto.Error;
import net.ow.shared.errorutils.mapper.ErrorMapper;
import net.ow.shared.errorutils.util.LocaleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Component
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {
    private final LocaleMessageSource messages;

    private final ErrorMapper errorMapper;

    /**
     * Handles application-specific exceptions
     */
    @ExceptionHandler({APIException.class})
    public ResponseEntity<APIResponse> onAPIException(APIException e) {
        log.error("Application threw APIException", e);
        return toResponseEntity(e.getError().getStatus(), errorMapper.toError(e, messages));
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse> onHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HTTP message not readable", e);
        var status = HttpStatus.BAD_REQUEST;
        return toResponseEntity(status, errorMapper.toError(e, "400-shared-not_readable"));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<APIResponse> onAuthenticationNotFound(
            HttpServletRequest request, AuthenticationCredentialsNotFoundException e) {
        log.error("Spring Security threw AuthenticationCredentialsNotFoundException: {}", e.getMessage());
        // TODO: Log user info
        log.error(
                "Unauthorised request for {} {} from {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());

        var status = HttpStatus.UNAUTHORIZED;
        return toResponseEntity(status, errorMapper.toError(e, status, "shared-auth", "Unauthorised"));
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
    public ResponseEntity<APIResponse> onDataAccessException(HttpServletRequest request, RuntimeException e) {
        log.warn("Spring Security threw {}: {}", e.getClass().getSimpleName(), e.getMessage());
        // TODO: Log user info
        log.warn(
                "Unidentified user denied access to {} {} from {}",
                request.getMethod(),
                request.getPathInfo(),
                request.getRemoteAddr());
        var status = HttpStatus.FORBIDDEN;
        return toResponseEntity(status, errorMapper.toError(e, status, "-shared-access_denied", "Access Denied"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIResponse> onConstraintViolationException(ConstraintViolationException e) {
        log.error("Application threw ConstraintViolationException", e);
        var status = HttpStatus.BAD_REQUEST;
        return toResponseEntity(status, errorMapper.toErrors(e));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Application threw MethodArgumentNotValidException", e);
        var status = HttpStatus.BAD_REQUEST;
        return toResponseEntity(status, errorMapper.toErrors(e));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIResponse> onRuntimeException(RuntimeException e) {
        log.error("Application threw RuntimeException", e);
        return toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, errorMapper.toError(e));
    }

    private ResponseEntity<APIResponse> toResponseEntity(HttpStatus status, Error... errors) {
        return ResponseEntity.status(status).body(APIResponse.error(errors));
    }
}
