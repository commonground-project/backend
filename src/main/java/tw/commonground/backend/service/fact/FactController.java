package tw.commonground.backend.service.fact;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.fact.dto.FactMapper;
import tw.commonground.backend.service.fact.dto.FactRequest;
import tw.commonground.backend.service.fact.dto.FactResponse;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.reference.ReferenceMapper;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.shared.pagination.PaginationMapper;
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

    private final Set<String> sortableColumn = Set.of("title", "createdAt", "updatedAt");

    private final PaginationParser paginationParser = new PaginationParser(sortableColumn, MAX_SIZE);

    public FactController(FactService factService) {
        this.factService = factService;
    }

    @GetMapping("/api/facts")
    public ResponseEntity<WrappedPaginationResponse<List<FactResponse>>> listFacts(
            @Valid PaginationRequest pagination) {
        Pageable pageable = paginationParser.parsePageable(pagination);
        Page<FactEntity> factEntityPage = factService.getFacts(pageable);

        List<FactResponse> factResponses = factEntityPage.getContent()
                .stream()
                .map(FactMapper::toResponse)
                .toList();

        return ResponseEntity.ok(new WrappedPaginationResponse<>(factResponses,
                PaginationMapper.toResponse(factEntityPage)));
    }

    @PostMapping("/api/facts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FactResponse> createFact(@AuthenticationPrincipal FullUserEntity user,
                                                   @Valid @RequestBody FactRequest factRequest) {
        return ResponseEntity.ok(FactMapper.toResponse(factService.createFact(factRequest, user)));
    }

    @GetMapping("/api/fact/{id}")
    public ResponseEntity<FactResponse> getFact(@PathVariable String id) {
        return ResponseEntity.ok(FactMapper.toResponse(factService.getFact(UUID.fromString(id))));
    }

    @PutMapping("/api/fact/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FactResponse> updateFact(@PathVariable String id,
                                                   @Valid @RequestBody FactRequest factRequest) {
        return ResponseEntity.ok(FactMapper.toResponse(factService.updateFact(UUID.fromString(id), factRequest)));
    }

    @DeleteMapping("/api/fact/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFact(@PathVariable String id) {
        factService.deleteFact(UUID.fromString(id));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/api/fact/{id}/references")
    public ResponseEntity<List<ReferenceResponse>> getFactReferences(@PathVariable String id) {
        return ResponseEntity.ok(factService.getFactReferences(UUID.fromString(id))
                .stream().map(ReferenceMapper::toResponse).toList());
    }

    @PostMapping("/api/fact/{id}/references")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReferenceResponse>> updateFactReferences(@PathVariable String id,
                                                                        @RequestBody List<@Valid ReferenceRequest>
                                                                                referenceRequests) {

        return ResponseEntity.ok(factService.createFactReferences(UUID.fromString(id), referenceRequests)
                .stream().map(ReferenceMapper::toResponse).toList());
    }

    @DeleteMapping("/api/fact/{id}/reference/{referenceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFactReferences(@PathVariable String id, @PathVariable String referenceId) {
        factService.deleteFactReferences(UUID.fromString(id), UUID.fromString(referenceId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
