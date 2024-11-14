package tw.commonground.backend.service.user.exception;

public class IdNotFoundException extends Exception {
    public IdNotFoundException(Long id) {
        super("User with id " + id + " not found.");
    }
}
