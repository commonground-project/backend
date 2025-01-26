package tw.commonground.backend.service.urlinfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.exception.ValidationException;
import tw.commonground.backend.service.urlinfo.dto.UrlRequest;
import tw.commonground.backend.service.urlinfo.dto.UrlInfoResponse;

import java.io.IOException;

@RestController
@RequestMapping("/api/website")
public class UrlinfoController {

    @GetMapping("/check")
    public ResponseEntity<?> getUrlInfo(@RequestBody UrlRequest urlRequest) {
        try {
            Document doc = Jsoup.connect(urlRequest.getUrl()).get();
            String title = doc.title();
            Element iconElement = doc.select("link[rel~=(?i)^(shortcut|icon|favicon)]").first();
            String icon = iconElement != null ? iconElement.attr("href") : "";
            return ResponseEntity.ok(new UrlInfoResponse(title.isEmpty() ? "NO TITLE" : title, icon));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationException("Requested website"
                    ,"The website of the given URL is not found. Check the URL and try again."));}
    }
}