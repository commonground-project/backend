package tw.commonground.backend.exception;

public class SearchServiceException extends RuntimeException {
    public SearchServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
