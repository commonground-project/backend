package tw.commonground.backend.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.ObjectError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {

        ErrorResponseException exception = new ErrorResponseException(status);
        exception.setTitle("Validation error");
        exception.setType(URI.create("type:VALIDATION_ERROR"));

        StringBuilder detail = new StringBuilder();
        detail.append("Validation failed for the following fields:\n ");
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            detail.append(error.getDefaultMessage());
            if (error != ex.getBindingResult().getAllErrors().getLast()) {
                detail.append("\n ");
            }
        }
        exception.setDetail(detail.toString());

        return super.handleExceptionInternal(exception, null, headers, status, request);
    }
}
