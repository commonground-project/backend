package tw.commonground.backend.service.issue;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.fact.FactService;
import tw.commonground.backend.service.fact.dto.FactMapper;
import tw.commonground.backend.service.fact.dto.FactResponse;
import tw.commonground.backend.service.fact.dto.LinkFactsRequest;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.follow.FollowService;
import tw.commonground.backend.service.issue.dto.*;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.shared.entity.RelatedObject;
import tw.commonground.backend.shared.pagination.PaginationMapper;
import tw.commonground.backend.shared.pagination.PaginationRequest;
import tw.commonground.backend.shared.pagination.PaginationParser;
import tw.commonground.backend.shared.pagination.WrappedPaginationResponse;
import tw.commonground.backend.service.issue.entity.SimpleIssueEntity;
import tw.commonground.backend.shared.content.ContentContainFact;
import tw.commonground.backend.shared.content.ContentParser;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.*;

@Traced
@RestController
@RequestMapping("/api")
public class IssueController {
    private static final int MAX_SIZE = 200;

    private final IssueService issueService;

    private final FactService factService;

    private final Set<String> sortableColumn = Set.of("title", "createdAt", "updatedAt");

    private final PaginationParser paginationParser = new PaginationParser(sortableColumn, MAX_SIZE);

    private final FollowService followService;

    public IssueController(IssueService issueService, FactService factService, FollowService followService) {
        this.issueService = issueService;
        this.factService = factService;
        this.followService = followService;
    }

    @GetMapping("/issues")
    public WrappedPaginationResponse<List<SimpleIssueResponse>> listIssues(@Valid PaginationRequest pagination) {
        Pageable pageable = paginationParser.parsePageable(pagination);
        Page<SimpleIssueEntity> pageIssues = issueService.getIssues(pageable);

        List<SimpleIssueResponse> issueResponses = pageIssues.getContent()
                .stream()
                .map(issue -> {
                    Integer viewpointCount = issueService.getViewpointCount(issue.getId());
                    return IssueMapper.toResponse(issue, viewpointCount);
                })
                .toList();

        return new WrappedPaginationResponse<>(issueResponses, PaginationMapper.toResponse(pageIssues));
    }

    @PostMapping("/issues")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IssueResponse> createIssue(@AuthenticationPrincipal FullUserEntity user,
                                                     @Valid @RequestBody IssueRequest issueRequest) {

        IssueEntity issueEntity = issueService.createIssue(issueRequest, user);
        ContentContainFact contentContainFact = ContentParser
                .separateContentAndFacts(issueEntity.getInsight());

        List<FactEntity> factResponses = factService.getFacts(contentContainFact.getFacts());
        Boolean follow = followService.getFollow(user.getId(), issueEntity.getId(), RelatedObject.ISSUE);
        Integer viewpointCount = issueService.getViewpointCount(issueEntity.getId());
        IssueResponse response = IssueMapper.toResponse(issueEntity, follow, factResponses, viewpointCount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/issue/{id}")
    public ResponseEntity<IssueResponse> getIssue(@AuthenticationPrincipal FullUserEntity user,
                                                  @PathVariable UUID id) {
        IssueEntity issueEntity = issueService.getIssue(id);
        ContentContainFact contentContainFact = ContentParser
                .separateContentAndFacts(issueEntity.getInsight());

        List<FactEntity> factResponses = factService.getFacts(contentContainFact.getFacts());
        Boolean follow = false;
        if (user != null) {
            follow = followService.getFollow(user.getId(), issueEntity.getId(), RelatedObject.ISSUE);
        }
        Integer viewpointCount = issueService.getViewpointCount(issueEntity.getId());
        IssueResponse response = IssueMapper.toResponse(issueEntity, follow, factResponses, viewpointCount);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/issue/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IssueResponse> updateIssue(@AuthenticationPrincipal FullUserEntity user,
                                                     @PathVariable UUID id,
                                                     @Valid @RequestBody IssueRequest issueRequest) {
        IssueEntity issueEntity = issueService.updateIssue(id, issueRequest);
        ContentContainFact contentContainFact = ContentParser
                .separateContentAndFacts(issueEntity.getInsight());

        List<FactEntity> factResponses = factService.getFacts(contentContainFact.getFacts());
        Boolean follow = followService.getFollow(user.getId(), issueEntity.getId(), RelatedObject.ISSUE);
        Integer viewpointCount = issueService.getViewpointCount(id);
        IssueResponse response = IssueMapper.toResponse(issueEntity, follow, factResponses, viewpointCount);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/issue/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('USER')")
    public Map<String, List<FactResponse>> linkFactsToIssue(@PathVariable UUID id,
                                                            @Valid @RequestBody LinkFactsRequest request) {
        List<FactEntity> factEntities = issueService.createManualFact(id, request.getFactIds());
        return Map.of("facts", factEntities.stream().map(FactMapper::toResponse).toList());
    }
}
