package tw.commonground.backend.service.fact;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.fact.dao.FactRequest;
import tw.commonground.backend.service.fact.dao.FactResponse;
import tw.commonground.backend.service.reference.ReferenceRequest;
import tw.commonground.backend.service.reference.ReferenceResponse;
import tw.commonground.backend.shared.exceptions.ExceptionResponse;
import tw.commonground.backend.shared.exceptions.InvalidSortColumnException;
import tw.commonground.backend.shared.pagination.WrappedPaginationResponse;

import java.util.*;

@RestController
public class FactController {
    private final FactService factService;

    public FactController(FactService factService) {
        this.factService = factService;
    }

    private final Set<String> validSortColumnOfFact = Set.of(
            "id",
            "createAt",
            "updateAt",
            "title",
            "authorId",
            "authorName");

    @GetMapping("/api/facts")
    public WrappedPaginationResponse<List<FactResponse>> listFacts(
            @RequestParam int page,
            @RequestParam String sort,
            @RequestParam int size,
            HttpServletRequest request
    ) throws ExceptionResponse {

        List<String> sortBy = Arrays.stream(sort.split(",")).toList();

        if (!validSortColumnOfFact.contains(sortBy.getFirst())) {
            throw new InvalidSortColumnException(sortBy.getFirst(), request.getRequestURI());
        }

        WrappedPaginationResponse<List<FactResponse>> factResponses;
        factResponses = factService.getFacts(page, size, sortBy.getFirst(), sortBy.getLast());
        return factResponses;
    }

    @PostMapping("/api/facts")
    public FactResponse createFact(@RequestBody FactRequest factRequest) {
        if (factRequest.getReferences() == null) {
            factRequest.setReferences(new HashSet<>());
        }

        return factService.createFact(factRequest);
    }

    @GetMapping("/api/fact/{id}")
    public FactResponse getFact(
            @PathVariable String id, HttpServletRequest request
    ) throws ExceptionResponse {
        return factService.getFact(UUID.fromString(id), request);
    }

    @PutMapping("/api/fact/{id}")
    public FactResponse updateFact(
            @PathVariable String id, @RequestBody FactRequest factRequest, HttpServletRequest request
    ) throws ExceptionResponse {
        if (factRequest.getReferences() == null) {
            factRequest.setReferences(new HashSet<>());
        }

        return factService.updateFact(UUID.fromString(id), factRequest, request);
    }

    @DeleteMapping("/api/fact/{id}")
    public ResponseEntity<String> deleteFact(
            @PathVariable String id, HttpServletRequest request
    ) throws ExceptionResponse {
        factService.deleteFact(UUID.fromString(id), request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/api/fact/{id}/references")
    public List<ReferenceResponse> getFactReferences(
            @PathVariable String id, HttpServletRequest request
    ) throws ExceptionResponse {
        return factService.getFactReferences(UUID.fromString(id), request);
    }

    @PostMapping("/api/fact/{id}/references")
    public List<ReferenceResponse> updateFactReferences(
            @PathVariable String id, @RequestBody List<ReferenceRequest> referenceRequests, HttpServletRequest request
    ) throws ExceptionResponse {
        return factService.updateFactReferences(UUID.fromString(id), referenceRequests, request);
    }

    @DeleteMapping("/api/fact/{id}/reference/{referenceId}")
    public ResponseEntity<String> deleteFactReferences(
            @PathVariable String id, @PathVariable long referenceId, HttpServletRequest request
    ) throws ExceptionResponse {
        factService.deleteFactReferences(UUID.fromString(id), referenceId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
