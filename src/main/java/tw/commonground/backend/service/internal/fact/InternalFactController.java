package tw.commonground.backend.service.internal.fact;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.commonground.backend.service.fact.dto.FactMapper;
import tw.commonground.backend.service.fact.dto.FactRequest;
import tw.commonground.backend.service.fact.dto.FactResponse;

@RestController
@RequestMapping("/api/internal/facts")
public class InternalFactController {

    private final InternalFactService internalFactService;

    public InternalFactController(InternalFactService internalFactService) {
        this.internalFactService = internalFactService;
    }


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FactResponse> createFact(@Valid @RequestBody FactRequest factRequest) {
        return ResponseEntity.ok(FactMapper.toResponse(internalFactService.createFact(factRequest)));
    }

}
