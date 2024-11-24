package tw.commonground.backend.service.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

@Getter
public class UserAlreadySetupException extends ErrorResponseException {
    private final String email;

    public UserAlreadySetupException(String email) {
        super(HttpStatus.BAD_REQUEST);
        this.email = email;

        this.setType(URI.create("type:USER_ALREADY_SETUP"));
        this.setTitle("User already setup");
        this.setDetail(String.format("User with email %s already setup", email));
    }
}
