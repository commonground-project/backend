package tw.commonground.backend.service.internal.reference;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.commonground.backend.service.internal.reference.dto.InternalDetailReferenceResponse;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.UUID;

@Traced
@RestController
@RequestMapping("/api/internal")
public class InternalReferenceController {

    private final InternalReferenceService internalReferenceService;

    public InternalReferenceController(InternalReferenceService internalReferenceService) {
        this.internalReferenceService = internalReferenceService;
    }

    @GetMapping("/reference/detail/{referenceId}")
    public ResponseEntity<InternalDetailReferenceResponse> getDetailReference(@PathVariable UUID referenceId) {
        InternalDetailReferenceResponse reference = internalReferenceService.getDetailReference(referenceId);
        return ResponseEntity.ok(reference);
    }
}
