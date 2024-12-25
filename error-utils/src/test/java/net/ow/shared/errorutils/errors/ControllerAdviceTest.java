package net.ow.shared.errorutils.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Set;

import lombok.SneakyThrows;
import net.ow.shared.errorutils.fixture.TestMessageSource;
import net.ow.shared.errorutils.fixture.TestServiceError;
import net.ow.shared.errorutils.fixture.TestableErrorMapper;
import net.ow.shared.errorutils.mapper.ErrorMapperTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class ControllerAdviceTest {
    @InjectMocks private ControllerAdvice controllerAdvice;

    @Mock ConstraintViolation<Object> constraintViolation1;

    @Mock ConstraintViolation<Object> constraintViolation2;

    @BeforeEach
    void initAll() {
        var messages = new TestMessageSource();
        var errorMapper = new TestableErrorMapper();
        controllerAdvice = new ControllerAdvice(messages, errorMapper);
    }

    @Test
    @SneakyThrows
    void testOnApiException() {
        // Given
        var ex = new APIException(TestServiceError.TEST_ONE, 20, "October");

        // When
        var response = controllerAdvice.onAPIException(ex);


        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorMapperTest.assertJson("""
                {
                  "errors": [ {
                    "id": "1234-5678-1234-5678",
                    "code": "400-test-one",
                    "title": "Error for Test One",
                    "detail": "Created on 20 October"
                  } ]
                }""", response.getBody());

    }

    @SneakyThrows
    @Test
    void testOnHttpMessageNotReadableException() {
        // Given
        var ex = new HttpMessageNotReadableException("not readable message",
                new RuntimeException("forced RuntimeException for the test"),
                new MockHttpInputMessage("httpInputMessage".getBytes()));

        // When
        var response = controllerAdvice.onHttpMessageNotReadableException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorMapperTest.assertJson("""
                {
                  "errors": [ {
                    "id": "1234-5678-1234-5678",
                    "code": "400-shared-not_readable",
                    "title": "Invalid Input",
                    "detail": "forced RuntimeException for the test"
                  } ]
                }""", response.getBody());

    }

    @SneakyThrows
    @Test
    void testInvalidFormatExceptionWithJsonValue() {
        // Given
        var invalidFormat = new InvalidFormatException(null, "You shall not parse!", "Smaug", EnumWithJsonValue.class);
        invalidFormat.prependPath(this, "testField");
        var ex = new HttpMessageNotReadableException("not readable message",
                invalidFormat,
                new MockHttpInputMessage("httpInputMessage".getBytes()));

        // When
        var response = controllerAdvice.onHttpMessageNotReadableException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorMapperTest.assertJson("""
                {
                  "errors": [ {
                    "id": "1234-5678-1234-5678",
                    "code": "400-shared-not_readable",
                    "title": "Invalid Input",
                    "detail": "Invalid value for testField. Expected one of [foo, bar] but was: Smaug",
                    "source": {
                      "pointer": "/testField"
                    }
                  } ]
                }""", response.getBody());

    }

    @SneakyThrows
    @Test
    void testInvalidFormatExceptionWithNonEnum() {
        // Given
        var invalidFormat = new InvalidFormatException(null, "You shall not parse!", "today", Date.class);
        invalidFormat.prependPath(this, "testField");
        var ex = new HttpMessageNotReadableException("not readable message",
                invalidFormat,
                new MockHttpInputMessage("httpInputMessage".getBytes()));

        // When
        var response = controllerAdvice.onHttpMessageNotReadableException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorMapperTest.assertJson("""
                {
                  "errors": [ {
                    "id": "1234-5678-1234-5678",
                    "code": "400-shared-not_readable",
                    "title": "Invalid Input",
                    "detail": "You shall not parse!",
                    "source": {
                      "pointer": "/testField"
                    }
                  } ]
                }""", response.getBody());

    }

    @SneakyThrows
    @Test
    void testOnRuntimeException() {
        // Given
        var ex = new RuntimeException("test message");

        // When
        var response = controllerAdvice.onRuntimeException(ex);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorMapperTest.assertJson("""
                {
                  "errors": [ {
                    "id": "1234-5678-1234-5678",
                    "code": "500-shared-runtime",
                    "title": "Internal Error",
                    "detail": "test message"
                  } ]
                }""", response.getBody());

    }

    @SneakyThrows
    @Test
    void testOnMethodArgumentNotValidException() {
        // Given
        var bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(new FieldError("saveRequest", "clientId", "Client Id is missing"),
                        new FieldError("saveRequest", "description", "Description is too long")));
        var methodParameter = mock(MethodParameter.class);

        var exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        // When
        var response = controllerAdvice.onMethodArgumentNotValidException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorMapperTest.assertJson("""
                {
                  "errors": [ {
                    "id": "1234-5678-1234-5678",
                    "code": "400-shared-method_argument_not_valid",
                    "title": "Invalid Argument",
                    "detail": "Client Id is missing",
                    "source": {
                      "parameter": "clientId"
                     }
                  },
                   {
                    "id": "1234-5678-1234-5678",
                    "code": "400-shared-method_argument_not_valid",
                    "title": "Invalid Argument",
                    "detail": "Description is too long",
                    "source": {
                      "parameter": "description"
                     }
                  }]
                }""", response.getBody());

    }

    @SneakyThrows
    @Test
    void testOnConstraintViolationException() {
        when(constraintViolation1.getMessage()).thenReturn("Age must be positive");
        var path1 = mock(Path.class);
        when(path1.toString()).thenReturn("/age");
        when(constraintViolation1.getPropertyPath()).thenReturn(path1);

        when(constraintViolation2.getMessage()).thenReturn("Name must not be empty");
        var path2 = mock(Path.class);
        when(path2.toString()).thenReturn("/name");
        when(constraintViolation2.getPropertyPath()).thenReturn(path2);

        var exception = new ConstraintViolationException("Validation failed", Set.of(constraintViolation1, constraintViolation2));

        // When
        var response = controllerAdvice.onConstraintViolationException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorMapperTest.assertJson("""
                {
                  "errors": [ {
                    "id": "1234-5678-1234-5678",
                    "code": "400-shared-constraint_violation",
                    "title": "Invalid Argument",
                    "detail": "Age must be positive",
                    "source": {
                      "pointer": "/age"
                     }
                  },
                  {
                    "id": "1234-5678-1234-5678",
                    "code": "400-shared-constraint_violation",
                    "title": "Invalid Argument",
                    "detail": "Name must not be empty",
                    "source": {
                      "pointer": "/name"
                    }
                  }]
                }""", response.getBody());
    }

    public enum EnumWithJsonValue {
        FOO("Foo"),
        BAR("Bar");

        private final String value;

        EnumWithJsonValue(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value.toLowerCase();
        }
    }
}