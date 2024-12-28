package tw.commonground.backend.service.issue;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.fact.FactService;
import tw.commonground.backend.service.fact.dto.FactMapper;
import tw.commonground.backend.service.fact.dto.FactResponse;
import tw.commonground.backend.service.fact.dto.LinkFactsRequest;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.issue.dto.IssueMapper;
import tw.commonground.backend.service.issue.dto.IssueRequest;
import tw.commonground.backend.service.issue.dto.IssueResponse;
import tw.commonground.backend.service.issue.dto.SimpleIssueResponse;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.shared.pagination.PaginationMapper;
import tw.commonground.backend.shared.pagination.PaginationRequest;
import tw.commonground.backend.shared.pagination.PaginationParser;
import tw.commonground.backend.shared.pagination.WrappedPaginationResponse;
import tw.commonground.backend.service.issue.entity.SimpleIssueEntity;
import tw.commonground.backend.shared.content.ContentContainFact;
import tw.commonground.backend.shared.content.ContentContainFactParser;

import java.util.*;

@RestController
@RequestMapping("/api")
public class IssueController {
    private static final int MAX_SIZE = 200;

    private final IssueService issueService;

    private final FactService factService;

    private final Set<String> sortableColumn = Set.of("title", "createdAt", "updatedAt");

    private final PaginationParser paginationParser = new PaginationParser(sortableColumn, MAX_SIZE);

    public IssueController(IssueService issueService, FactService factService) {
        this.issueService = issueService;
        this.factService = factService;
    }

    @GetMapping("/issues")
    public WrappedPaginationResponse<List<SimpleIssueResponse>> listIssues(@Valid PaginationRequest pagination) {
        Pageable pageable = paginationParser.parsePageable(pagination);
        Page<SimpleIssueEntity> pageIssues = issueService.getIssues(pageable);

        List<SimpleIssueResponse> issueResponses = pageIssues.getContent()
                .stream()
                .map(IssueMapper::toResponse)
                .toList();

        return new WrappedPaginationResponse<>(issueResponses, PaginationMapper.toResponse(pageIssues));
    }

    @PostMapping("/issues")
    public ResponseEntity<IssueResponse> createIssue(@AuthenticationPrincipal FullUserEntity user,
                                                     @Valid @RequestBody IssueRequest issueRequest) {

        IssueEntity issueEntity = issueService.createIssue(issueRequest, user);
        ContentContainFact contentContainFact = ContentContainFactParser
                .separateContentAndFacts(issueEntity.getInsight());

        List<FactEntity> factResponses = factService.getFacts(contentContainFact.getFacts());
        IssueResponse response = IssueMapper.toResponse(issueEntity, factResponses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/issue/{id}")
    public ResponseEntity<IssueResponse> getIssue(@PathVariable UUID id) {
        IssueEntity issueEntity = issueService.getIssue(id);
        ContentContainFact contentContainFact = ContentContainFactParser
                .separateContentAndFacts(issueEntity.getInsight());

        List<FactEntity> factResponses = factService.getFacts(contentContainFact.getFacts());
        IssueResponse response = IssueMapper.toResponse(issueEntity, factResponses);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/issue/{id}")
    public ResponseEntity<IssueResponse> updateIssue(@PathVariable UUID id,
                                                     @Valid @RequestBody IssueRequest issueRequest) {
        IssueEntity issueEntity = issueService.updateIssue(id, issueRequest);
        ContentContainFact contentContainFact = ContentContainFactParser
                .separateContentAndFacts(issueEntity.getInsight());

        List<FactEntity> factResponses = factService.getFacts(contentContainFact.getFacts());
        IssueResponse response = IssueMapper.toResponse(issueEntity, factResponses);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/issue/{id}")
    public void deleteIssue(@PathVariable String id) {
        issueService.deleteIssue(UUID.fromString(id));
    }

    @GetMapping("/issue/{id}/facts")
    public WrappedPaginationResponse<List<FactResponse>> getIssueFacts(@PathVariable UUID id,
                                                                       @Valid PaginationRequest pagination) {
        PaginationParser validator = new PaginationParser(Collections.emptySet(), MAX_SIZE);
        Pageable pageable = validator.parsePageable(pagination);
        Page<FactEntity> pageFacts = issueService.getIssueFacts(id, pageable);

        List<FactResponse> factResponses = pageFacts.getContent()
                .stream()
                .map(FactMapper::toResponse)
                .toList();

        return new WrappedPaginationResponse<>(factResponses, PaginationMapper.toResponse(pageFacts));
    }

    @PostMapping("/issue/{id}/facts")
    public Map<String, List<FactResponse>> linkFactsToIssue(@PathVariable UUID id,
                                                            @Valid @RequestBody LinkFactsRequest request) {
        List<FactEntity> factEntities = issueService.createManualFact(id, request.getFactIds());
        return Map.of("facts", factEntities.stream().map(FactMapper::toResponse).toList());
    }
}
