package tw.commonground.backend.service.internal.account.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

@Getter
public class ServiceAccountUnsupportedOperationException extends ErrorResponseException {
    private final String operation;

    public ServiceAccountUnsupportedOperationException(String operation) {
        super(HttpStatus.BAD_REQUEST);
        this.operation = operation;

        this.setType(URI.create("type:SERVICE_ACCOUNT_UNSUPPORTED_OPERATION"));
        this.setTitle("Service account unsupported operation");
        this.setDetail(String.format("The operation %s is not supported for service account", operation));
    }
}
