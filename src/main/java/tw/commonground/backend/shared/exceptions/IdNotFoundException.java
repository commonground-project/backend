package tw.commonground.backend.shared.exceptions;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class IdNotFoundException extends ExceptionResponse {
    public IdNotFoundException(UUID id, String url) {
        super(
                "ID_NOT_FOUND",
                HttpStatus.NOT_FOUND.value(),
                "the data of request id not found",
                "there are no data with `" + id.toString() + "` id",
                url
        );
    }
}
