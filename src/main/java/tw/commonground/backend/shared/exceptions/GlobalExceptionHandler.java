package tw.commonground.backend.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.awt.dnd.InvalidDnDOperationException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            ExceptionResponse.class,
            IdNotFoundException.class,
            InvalidDnDOperationException.class
    })
    public ResponseEntity<ExceptionResponse> exceptionOccur(ExceptionResponse exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }
}
