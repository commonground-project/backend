package tw.commonground.backend.service.reference;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.commonground.backend.service.fact.FactService;


@RestController
@RequestMapping("/api")
public class ReferenceController {
    private final FactService factService;

    public ReferenceController(FactService factService) {
        this.factService = factService;
    }

    @PostMapping("/reference")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReferenceResponse> createReference(@Valid @RequestBody ReferenceRequest referenceRequest) {
        ReferenceEntity referenceEntity = factService.parseReferenceEntity(referenceRequest.getUrl());
        ReferenceResponse response = ReferenceMapper.toResponse(referenceEntity);
        return ResponseEntity.ok(response);
    }
}
