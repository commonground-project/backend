package tw.commonground.backend.service.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmailNotFoundException extends RuntimeException {
  public EmailNotFoundException(String email) {
    super("Email not found: " + email);
  }
}
