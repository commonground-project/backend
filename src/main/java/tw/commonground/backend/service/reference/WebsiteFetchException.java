package tw.commonground.backend.service.reference;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

public class WebsiteFetchException extends ErrorResponseException {
    public WebsiteFetchException() {
        super(HttpStatus.BAD_REQUEST);
        this.setType(URI.create("type:WEBSITE_NOT_FOUND"));
        this.setTitle("Requested website not found");
        this.setDetail("The website of the given URL is not found. Check the URL and try again.");
    }
}
