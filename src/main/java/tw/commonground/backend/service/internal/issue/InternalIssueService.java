package  tw.commonground.backend.service.internal.issue;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.internal.issue.dto.InternalDetailIssueResponse;
import tw.commonground.backend.service.issue.entity.ManualIssueFactEntity;
import tw.commonground.backend.service.internal.issue.dto.InternalIssueMapper;
import tw.commonground.backend.service.internal.issue.dto.InternalIssueRequest;
import tw.commonground.backend.service.issue.IssueService;
import tw.commonground.backend.service.internal.reference.InternalReferenceService;
import tw.commonground.backend.service.internal.viewpoint.dto.InternalDetailViewpointResponse;
import tw.commonground.backend.service.internal.viewpoint.dto.InternalViewpointMapper;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.internal.issue.dto.InternalIssueResponse;
import tw.commonground.backend.service.issue.entity.IssueRepository;
import tw.commonground.backend.service.reference.ReferenceEntity;
import tw.commonground.backend.service.viewpoint.ViewpointService;
import tw.commonground.backend.service.viewpoint.repository.ViewpointRepositoryContainer;
import tw.commonground.backend.shared.content.ContentParser;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
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
    private final IssueService issueService;
    private final ViewpointRepositoryContainer viewpointRepositoryContainer;
    private final InternalReferenceService internalReferenceService;

    public InternalIssueService(IssueRepository issueRepository,
                                ViewpointService viewpointService,
                                ViewpointRepositoryContainer viewpointRepositoryContainer,
                                InternalReferenceService internalReferenceService,
                                IssueService issueService) {
        this.issueRepository = issueRepository;
        this.viewpointService = viewpointService;
        this.viewpointRepositoryContainer = viewpointRepositoryContainer;
        this.internalReferenceService = internalReferenceService;
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
            @CacheEvict(value = "issue", allEntries = true),
            @CacheEvict(value = "fact", allEntries = true),
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

        List<UUID> newFacts = request.getFacts().stream().filter(
                fact -> !facts.contains(fact)
        ).toList();

        issueService.createManualFact(issue.getId(), newFacts);

        int viewpointCount =  viewpointService.getIssueViewpoints(issueId, Pageable.unpaged()).getContent().size();

        return InternalIssueMapper.toResponse(issue, viewpointCount);
    }

    public InternalDetailIssueResponse getDetailIssue(UUID issueId) {
        IssueEntity issueEntity = issueRepository.findById(issueId).orElseThrow(
                () -> new EntityNotFoundException("InternalIssue", "id", issueId.toString())
        );
        List<ViewpointEntity> viewpoints = viewpointRepositoryContainer.findAllByIssueId(issueId);
        List<InternalDetailViewpointResponse> internalDetailViewpointResponses = new ArrayList<>();
        for (ViewpointEntity viewpoint : viewpoints) {
            List<FactEntity> facts = viewpointService.getFactsOfViewpoint(viewpoint.getId());
            for (FactEntity fact : facts) {
                Set<ReferenceEntity> references = fact.getReferences();
                for (ReferenceEntity reference : references) {
                    internalReferenceService.createDescriptionForReference(reference);
                }
            }
            internalDetailViewpointResponses.add(InternalViewpointMapper.toDetailResponse(viewpoint, facts));
        }

        // we also need to deal with the fact directly related to this issue
        List<FactEntity> facts = issueEntity.getManualFacts()
                .stream()
                .map(ManualIssueFactEntity::getFact)
                .toList();
        for (FactEntity fact : facts) {
            Set<ReferenceEntity> references = fact.getReferences();
            for (ReferenceEntity reference : references) {
                internalReferenceService.createDescriptionForReference(reference);
            }
        }
        return InternalIssueMapper.toDetailResponse(issueEntity, internalDetailViewpointResponses);
    }
}
