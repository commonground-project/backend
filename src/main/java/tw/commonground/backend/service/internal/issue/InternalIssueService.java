package  tw.commonground.backend.service.internal.issue;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.internal.issue.dto.InternalDetailIssueResponse;
import tw.commonground.backend.service.internal.issue.dto.InternalIssueMapper;
import tw.commonground.backend.service.internal.reference.InternalReferenceService;
import tw.commonground.backend.service.internal.viewpoint.dto.InternalDetailViewpointResponse;
import tw.commonground.backend.service.internal.viewpoint.dto.InternalViewpointMapper;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.internal.issue.dto.InternalIssueResponse;
import tw.commonground.backend.service.issue.entity.IssueRepository;
import tw.commonground.backend.service.reference.ReferenceEntity;
import tw.commonground.backend.service.viewpoint.ViewpointService;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointRepository;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Traced
@Service
public class InternalIssueService {

    private final IssueRepository issueRepository;
    private final ViewpointService viewpointService;
    private final ViewpointRepository viewpointRepository;
    private final InternalReferenceService internalReferenceService;

    public InternalIssueService(IssueRepository issueRepository, ViewpointService viewpointService, ViewpointRepository viewpointRepository, InternalReferenceService internalReferenceService) {
        this.issueRepository = issueRepository;
        this.viewpointService = viewpointService;
        this.viewpointRepository = viewpointRepository;
        this.internalReferenceService = internalReferenceService;
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

    public InternalDetailIssueResponse getDetailIssue(UUID issueId) {
        IssueEntity issueEntity = issueRepository.findById(issueId).orElseThrow(
                () -> new EntityNotFoundException("Issue", "id", issueId.toString())
        );

        List<ViewpointEntity> viewpoints = viewpointRepository.findAllByIssueId(issueId);
        List<InternalDetailViewpointResponse> internalDetailViewpointResponses = new ArrayList<>();
        for (ViewpointEntity viewpoint : viewpoints) {
            List<FactEntity> facts = viewpointService.getFactsOfViewpoint(viewpoint.getId());
            for(FactEntity fact : facts) {
                Set<ReferenceEntity> references = fact.getReferences();
                for(ReferenceEntity reference : references) {
                    internalReferenceService.createDescriptionForReference(reference);
                }
            }
            internalDetailViewpointResponses.add(InternalViewpointMapper.toDetailResponse(viewpoint, facts));
        }
        return InternalIssueMapper.toDetailResponse(issueEntity, internalDetailViewpointResponses);
    }

}
