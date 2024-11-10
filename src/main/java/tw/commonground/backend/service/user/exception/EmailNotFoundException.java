package tw.commonground.backend.service.user.exception;

public class EmailNotFoundException extends RuntimeException {
  public EmailNotFoundException(String email) {
    super("Email not found: " + email);
  }
}
