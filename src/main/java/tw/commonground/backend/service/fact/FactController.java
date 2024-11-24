package tw.commonground.backend.service.fact;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.fact.dto.FactRequest;
import tw.commonground.backend.service.fact.dto.FactResponse;
import tw.commonground.backend.service.pagination.PaginationValidator;
import tw.commonground.backend.service.reference.ReferenceRequest;
import tw.commonground.backend.service.reference.ReferenceResponse;
import tw.commonground.backend.service.pagination.PaginationRequest;
import tw.commonground.backend.service.pagination.WrappedPaginationResponse;

import java.util.*;

@RestController
public class FactController {
    private final FactService factService;
    private final PaginationValidator paginationValidator = new PaginationValidator();

    private final Set<String> sortableColumn = Set.of("title", "createAt", "updateAt", "authorId", "authorName");

    public FactController(FactService factService) {
        this.factService = factService;
    }

    @GetMapping("/csrf-token")
    public CsrfToken csrfToken(CsrfToken token) {
        return token;
    }

    @GetMapping("/api/facts")
    public WrappedPaginationResponse<List<FactResponse>> listFacts(@Valid PaginationRequest pagination) {
        Pageable pageable = paginationValidator.validatePaginationRequest(pagination, sortableColumn);
        return factService.getFacts(pageable);
    }

    @PostMapping("/api/facts")
    public FactResponse createFact(@Valid @RequestBody FactRequest factRequest) {
        return factService.createFact(factRequest);
    }

    @GetMapping("/api/fact/{id}")
    public FactResponse getFact(@PathVariable String id) {
        return factService.getFact(UUID.fromString(id));
    }

    @PutMapping("/api/fact/{id}")
    public FactResponse updateFact(@PathVariable String id, @Valid @RequestBody FactRequest factRequest) {
        return factService.updateFact(UUID.fromString(id), factRequest);
    }

    @DeleteMapping("/api/fact/{id}")
    public ResponseEntity<String> deleteFact(@PathVariable String id) {
        factService.deleteFact(UUID.fromString(id));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/api/fact/{id}/references")
    public List<ReferenceResponse> getFactReferences(@PathVariable String id) {
        return factService.getFactReferences(UUID.fromString(id));
    }

    @PostMapping("/api/fact/{id}/references")
    public List<ReferenceResponse> updateFactReferences(@PathVariable String id,
                                                        @RequestBody List<@Valid ReferenceRequest> referenceRequests) {

        return factService.createFactReferences(UUID.fromString(id), referenceRequests);
    }

    @DeleteMapping("/api/fact/{id}/reference/{referenceId}")
    public ResponseEntity<String> deleteFactReferences(@PathVariable String id, @PathVariable String referenceId) {
        factService.deleteFactReferences(UUID.fromString(id), UUID.fromString(referenceId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
