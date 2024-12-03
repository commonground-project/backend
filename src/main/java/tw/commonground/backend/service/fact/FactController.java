package tw.commonground.backend.service.fact;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.fact.dto.FactRequest;
import tw.commonground.backend.service.fact.dto.FactResponse;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.shared.pagination.PaginationParser;
import tw.commonground.backend.service.reference.ReferenceRequest;
import tw.commonground.backend.service.reference.ReferenceResponse;
import tw.commonground.backend.shared.pagination.PaginationRequest;
import tw.commonground.backend.shared.pagination.WrappedPaginationResponse;

import java.util.*;

@RestController
public class FactController {
    private static final int MAX_SIZE = 200;

    private final FactService factService;

    private final Set<String> sortableColumn = Set.of("title", "createdAt", "updatedAt", "authorId", "authorName");

    private final PaginationParser paginationParser = new PaginationParser(sortableColumn, MAX_SIZE);

    public FactController(FactService factService) {
        this.factService = factService;
    }

    @GetMapping("/api/facts")
    public WrappedPaginationResponse<List<FactResponse>> listFacts(@Valid PaginationRequest pagination) {
        Pageable pageable = paginationParser.parsePageable(pagination);
        return factService.getFacts(pageable);
    }

    @PostMapping("/api/facts")
    public FactResponse createFact(@AuthenticationPrincipal FullUserEntity user,
                                   @Valid @RequestBody FactRequest factRequest) {
        return factService.createFact(factRequest, user);
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
