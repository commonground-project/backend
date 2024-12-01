package tw.commonground.backend.service.jwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

public class RefreshTokenInvalidException extends ErrorResponseException {
    public RefreshTokenInvalidException() {
        super(HttpStatus.BAD_REQUEST);

        this.setType(URI.create("type:REFRESH_TOKEN_INVALID"));
        this.setTitle("Refresh token invalid");
        this.setDetail("Refresh token is expired or invalid");
    }
}
