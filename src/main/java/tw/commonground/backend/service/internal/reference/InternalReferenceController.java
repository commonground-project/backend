package tw.commonground.backend.service.internal.reference;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.commonground.backend.service.internal.fact.dto.InternalDetailFactResponse;
import tw.commonground.backend.service.internal.reference.InternalReferenceService;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.List;
import java.util.UUID;

@Traced
@RestController
@RequestMapping("/api/internal/references")
public class InternalReferenceController {

    private final InternalReferenceService internalReferenceService;

    public InternalReferenceController(InternalReferenceService internalReferenceService, InternalReferenceService internalReferenceService1) {
        this.internalReferenceService = internalReferenceService1;
    }


    @GetMapping("/{referenceId}")
    public ResponseEntity<InternalDetailFactResponse> getDetailReference(@PathVariable UUID referenceId) {
        InternalDetailFactResponse reference = internalReferenceService.getDetailReference(referenceId);
        return ResponseEntity.ok(reference);
    }
}
