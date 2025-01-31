package tw.commonground.backend.service.reference;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.reference.dto.ReferenceRequest;
import tw.commonground.backend.service.reference.dto.WebsiteInfoResponse;

@RestController
@RequestMapping("/api/website")
public class ReferenceController {
    private final ReferenceService referenceService;

    public ReferenceController(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @GetMapping("/check")
    public ResponseEntity<WebsiteInfoResponse> getUrlInfo(ReferenceRequest referenceRequest) {
        WebsiteInfoResponse websiteInfoResponse = referenceService.getWebsiteInfo(referenceRequest.getUrl());
        return ResponseEntity.ok(websiteInfoResponse);
    }
}
