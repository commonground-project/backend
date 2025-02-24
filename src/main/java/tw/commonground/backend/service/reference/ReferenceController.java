package tw.commonground.backend.service.reference;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.reference.dto.ReferenceRequest;
import tw.commonground.backend.service.reference.dto.ReferenceResponse;
import tw.commonground.backend.service.reference.dto.WebsiteInfoResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.commonground.backend.shared.tracing.Traced;


@Traced
@RestController
@RequestMapping("/api")
public class ReferenceController {
    private final ReferenceService referenceService;

    public ReferenceController(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @GetMapping("/website/check")
    public ResponseEntity<WebsiteInfoResponse> getUrlInfo(ReferenceRequest referenceRequest) {
        WebsiteInfoResponse websiteInfoResponse = referenceService.getWebsiteInfo(referenceRequest.getUrl());
        return ResponseEntity.ok(websiteInfoResponse);
    }

    @PostMapping("/references")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReferenceResponse> createReference(@Valid @RequestBody ReferenceRequest referenceRequest) {
        ReferenceEntity referenceEntity = referenceService.createReferenceFromUrl(referenceRequest.getUrl());
        ReferenceResponse response = ReferenceMapper.toResponse(referenceEntity);
        return ResponseEntity.ok(response);
    }
}
