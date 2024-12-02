package tw.commonground.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

public class ValidationException extends ErrorResponseException {
    public ValidationException(String fieldName, String message) {
        super(HttpStatus.BAD_REQUEST);
        this.setType(URI.create("type:VALIDATION_ERROR"));
        this.setTitle(String.format("%s validation error", fieldName));
        this.setDetail(message);
    }

    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST);
        this.setType(URI.create("type:VALIDATION_ERROR"));
        this.setTitle("Validation error");
        this.setDetail(message);
    }
}
