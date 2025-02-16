package  tw.commonground.backend.service.internal.issue;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.internal.issue.dto.InternalIssueMapper;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.internal.issue.dto.InternalIssueResponse;
import tw.commonground.backend.service.issue.entity.IssueRepository;
import tw.commonground.backend.service.viewpoint.ViewpointService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InternalIssueService {

    private final IssueRepository issueRepository;
    private final ViewpointService viewpointService;

    public InternalIssueService(IssueRepository issueRepository, ViewpointService viewpointService) {
        this.issueRepository = issueRepository;
        this.viewpointService = viewpointService;
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
}
