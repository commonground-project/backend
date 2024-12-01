package tw.commonground.backend.service.pagination;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

@Getter
public class ValidationException extends ErrorResponseException {

    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST);

        this.setType(URI.create("type:USER_ALREADY_SETUP"));
        this.setTitle("User already setup");
        this.setDetail(message);
    }
}
