package tw.commonground.backend.service.user.exception;

import lombok.Getter;

@Getter
public class UserAlreadySetupException extends RuntimeException {
    private final String email;

    public UserAlreadySetupException(String email) {
        super("User already setup with email: " + email);
        this.email = email;
    }
}
