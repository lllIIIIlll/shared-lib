package net.ow.shared.errorutils.fixture;

import lombok.Getter;
import net.ow.shared.errorutils.errors.ServiceError;
import net.ow.shared.errorutils.model.ErrorProperties;
import org.springframework.http.HttpStatus;

@Getter
public enum TestServiceError implements ServiceError {
    TEST_ONE(HttpStatus.BAD_REQUEST, "test-one");

    private ErrorProperties errorProperties;

    TestServiceError(HttpStatus status, String code) {
        initialise(status, code);
    }

    @Override
    public HttpStatus getStatus() {
        return errorProperties.getStatus();
    }

    @Override
    public String getCode() {
        return errorProperties.getCode();
    }

    @Override
    public String getTitle() {
        return errorProperties.getTitle();
    }

    @Override
    public String getMessage() {
        return errorProperties.getMessage();
    }

    @Override
    public void setErrorProperties(ErrorProperties errorProperties) {
        this.errorProperties = errorProperties;
    }
}
