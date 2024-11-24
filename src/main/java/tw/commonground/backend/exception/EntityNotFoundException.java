package tw.commonground.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

public class EntityNotFoundException extends ErrorResponseException {
    public EntityNotFoundException(String entityName, String fieldName, String fieldValue) {
        super(HttpStatus.NOT_FOUND);
        this.setType(URI.create("type:ENTITY_NOT_FOUND"));
        this.setTitle(String.format("%s not found", entityName));
        this.setDetail(String.format("%s with %s %s not found", entityName, fieldName, fieldValue));
    }

    public EntityNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND);
        this.setType(URI.create("type:ENTITY_NOT_FOUND"));
        this.setTitle("Entity not found");
        this.setDetail(message);
    }
}
