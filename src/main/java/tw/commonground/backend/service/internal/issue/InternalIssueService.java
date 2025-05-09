package  tw.commonground.backend.service.internal.issue;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.internal.issue.dto.InternalIssueMapper;
import tw.commonground.backend.service.internal.issue.dto.InternalIssueRequest;
import tw.commonground.backend.service.issue.IssueService;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.internal.issue.dto.InternalIssueResponse;
import tw.commonground.backend.service.issue.entity.IssueRepository;
import tw.commonground.backend.service.viewpoint.ViewpointService;
import tw.commonground.backend.shared.content.ContentParser;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Traced
@Service
public class InternalIssueService {

    private final IssueRepository issueRepository;
    private final ViewpointService viewpointService;
    private final IssueService issueService;

    public InternalIssueService(IssueRepository issueRepository, ViewpointService viewpointService, IssueService issueService) {
        this.issueRepository = issueRepository;
        this.viewpointService = viewpointService;
        this.issueService = issueService;
    }

    public List<InternalIssueResponse> getIssues() {
        List<IssueEntity> issues = issueRepository.findAll();

        return issues.stream()
                .map(issue -> {
                    int viewpointCount = viewpointService.getIssueViewpoints(issue.getId(), Pageable.unpaged())
                            .getContent().size();
                    return InternalIssueMapper.toResponse(issue, viewpointCount);
                })
                .collect(Collectors.toList());
    }

    public InternalIssueResponse getIssue(UUID issueId) {
        IssueEntity issue = issueRepository.findById(issueId).orElseThrow(
                () -> new EntityNotFoundException("InternalIssue", "issue id", issueId.toString())
        );

        int viewpointCount = viewpointService.getIssueViewpoints(issueId, Pageable.unpaged()).getContent().size();
        return InternalIssueMapper.toResponse(issue, viewpointCount);
    }

    @Caching(evict = {
            @CacheEvict(value = "issue", key = "'allIssues'"),
            @CacheEvict(value = "issue", key = "#issueId"),
            @CacheEvict(value = "fact", key = "'allFacts'")
    })
    public InternalIssueResponse updateIssueInsight(UUID issueId, InternalIssueRequest request) {
        IssueEntity issue = issueRepository.findById(issueId).orElseThrow(
                () -> new EntityNotFoundException("InternalIssue", "issue id", issueId.toString())
        );

        String content = ContentParser.convertLinkIntToUuid(request.getInsight(), request.getFacts());
        issue.setInsight(content);
        issueRepository.save(issue);
        List<UUID> facts = issue.getManualFacts().stream().map(
                manualFact -> manualFact.getFact().getId()
        ).toList();

        List<UUID> newFacts = request.facts.stream().filter(
                fact -> !facts.contains(fact)
        ).toList();

        issueService.createManualFact(issue.getId(), newFacts);

        int viewpointCount =  viewpointService.getIssueViewpoints(issueId, Pageable.unpaged()).getContent().size();

        return InternalIssueMapper.toResponse(issue, viewpointCount);
    }
}
