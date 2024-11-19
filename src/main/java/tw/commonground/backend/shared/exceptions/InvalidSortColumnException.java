package tw.commonground.backend.shared.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidSortColumnException extends ExceptionResponse {
    public InvalidSortColumnException(String column, String url) {
        super(
                "INVALID_SORT_COLUMN",
                HttpStatus.NOT_FOUND.value(),
                "you can't sort by a non-existent column",
                "there are no column: " + column,
                url
        );
    }
}
