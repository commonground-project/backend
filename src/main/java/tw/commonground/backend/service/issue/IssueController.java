package tw.commonground.backend.service.issue;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.fact.dto.FactMapper;
import tw.commonground.backend.service.fact.dto.FactResponse;
import tw.commonground.backend.service.fact.dto.LinkFactsRequest;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.issue.dto.IssueMapper;
import tw.commonground.backend.service.issue.dto.IssueRequest;
import tw.commonground.backend.service.issue.dto.IssueResponse;
import tw.commonground.backend.service.issue.dto.SimpleIssueResponse;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.pagination.PaginationMapper;
import tw.commonground.backend.pagination.PaginationRequest;
import tw.commonground.backend.pagination.PaginationValidator;
import tw.commonground.backend.pagination.WrappedPaginationResponse;
import tw.commonground.backend.service.issue.entity.SimpleIssueEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
public class IssueController {
    private static final int MAX_SIZE = 200;

    private final IssueService issueService;

    private final Set<String> sortableColumn = Set.of("title", "createAt", "updateAt", "authorId", "authorName");

    private final PaginationValidator paginationValidator = new PaginationValidator(sortableColumn, MAX_SIZE);

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("/api/issues")
    public WrappedPaginationResponse<List<SimpleIssueResponse>> listIssues(@Valid PaginationRequest pagination) {
        Pageable pageable = paginationValidator.validatePaginationRequest(pagination);
        Page<SimpleIssueEntity> pageIssues = issueService.getIssues(pageable);

        List<SimpleIssueResponse> issueResponses = pageIssues.getContent()
                .stream()
                .map(IssueMapper::toResponse)
                .toList();

        return new WrappedPaginationResponse<>(issueResponses, PaginationMapper.toResponse(pageIssues));
    }

    @PostMapping("/api/issues")
    public IssueResponse createIssue(@Valid IssueRequest issueRequest) {
        IssueEntity issueEntity = issueService.createIssue(issueRequest);
        return IssueMapper.toResponse(issueEntity);
    }

    @GetMapping("/api/issue/{id}")
    public IssueResponse getIssue(@PathVariable String id) {
        return IssueMapper.toResponse(issueService.getIssue(UUID.fromString(id)));
    }

    @PutMapping("/api/issue/{id}")
    public IssueResponse updateIssue(@PathVariable UUID id, @Valid IssueRequest issueRequest) {
        return IssueMapper.toResponse(issueService.updateIssue(id, issueRequest));
    }

    @DeleteMapping("/api/issue/{id}")
    public void deleteIssue(@PathVariable String id) {
        issueService.deleteIssue(UUID.fromString(id));
    }

    @GetMapping("/api/issue/{id}/facts")
    public WrappedPaginationResponse<List<FactResponse>> getIssueFacts(@PathVariable UUID id,
                                                                       @Valid PaginationRequest pagination) {
        Pageable pageable = paginationValidator.validatePaginationRequest(pagination);
        Page<FactEntity> pageFacts = issueService.getIssueFacts(id, pageable);

        List<FactResponse> factResponses = pageFacts.getContent()
                .stream()
                .map(FactMapper::toResponse)
                .toList();

        return new WrappedPaginationResponse<>(factResponses, PaginationMapper.toResponse(pageFacts));
    }

    @PostMapping("/api/issue/{id}/facts")
    public Map<String, List<FactResponse>> linkFactsToIssue(@PathVariable UUID id,
                                                            @RequestBody LinkFactsRequest request) {
        List<FactEntity> factEntities = issueService.createManualFact(id, request.getFactIds());
        return Map.of("facts", factEntities.stream().map(FactMapper::toResponse).toList());
    }
}
