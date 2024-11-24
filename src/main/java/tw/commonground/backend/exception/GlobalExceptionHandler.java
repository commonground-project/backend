package tw.commonground.backend.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.ObjectError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(@NonNull NoResourceFoundException ex,
                                                                    @NonNull HttpHeaders headers,
                                                                    @NonNull HttpStatusCode status,
                                                                    @NonNull WebRequest request) {

        ErrorResponseException exception = new ErrorResponseException(status);
        exception.setTitle("Not found");
        exception.setType(URI.create("type:NOT_FOUND"));
        exception.setDetail("The requested resource was not found.");

        return super.handleExceptionInternal(exception, null, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            @NonNull HttpRequestMethodNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        ErrorResponseException exception = new ErrorResponseException(status);
        exception.setTitle("Method not supported");
        exception.setType(URI.create("type:METHOD_NOT_SUPPORTED"));
        exception.setDetail("The requested method is not supported for this resource.");

        return super.handleExceptionInternal(exception, null, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {

        ErrorResponseException exception = new ErrorResponseException(status);
        StringBuilder detail = createValidException(exception);

        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            detail.append(error.getDefaultMessage());
            if (error != ex.getBindingResult().getAllErrors().getLast()) {
                detail.append("\n ");
            }
        }
        exception.setDetail(detail.toString());

        return super.handleExceptionInternal(exception, null, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex,
                                                                      @NonNull HttpHeaders headers,
                                                                      @NonNull HttpStatusCode status,
                                                                      @NonNull WebRequest request) {

        ErrorResponseException exception = new ErrorResponseException(status);
        StringBuilder detail = createValidException(exception);

        ex.getAllValidationResults().forEach(validationResult ->
            validationResult.getResolvableErrors().forEach(error -> {
                detail.append(error.getDefaultMessage());
                if (error != validationResult.getResolvableErrors().getLast()) {
                    detail.append("\n ");
                }
        }));

        exception.setDetail(detail.toString());

        return super.handleExceptionInternal(exception, null, headers, status, request);
    }

    private StringBuilder createValidException(ErrorResponseException exception) {
        StringBuilder detail = new StringBuilder();
        exception.setTitle("Validation error");
        exception.setType(URI.create("type:VALIDATION_ERROR"));

        detail.append("Validation failed for the following fields:\n ");

        return detail;
    }
}
