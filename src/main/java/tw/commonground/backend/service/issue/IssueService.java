package tw.commonground.backend.service.issue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.exception.ValidationException;
import tw.commonground.backend.service.fact.FactService;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.fact.entity.FactRepository;
import tw.commonground.backend.service.issue.dto.IssueRequest;
import tw.commonground.backend.service.issue.entity.*;
import tw.commonground.backend.shared.content.ContentContainFactParser;

import java.util.*;

@Service
public class IssueService {

    private final IssueRepository issueRepository;

    private final ManualFactRepository manualFactRepository;

    private final FactRepository factRepository;

    private final FactService factService;

    public IssueService(IssueRepository issueRepository, ManualFactRepository manualFactRepository,
                        FactRepository factRepository, FactService factService) {
        this.issueRepository = issueRepository;
        this.manualFactRepository = manualFactRepository;
        this.factRepository = factRepository;
        this.factService = factService;
    }

    public Page<SimpleIssueEntity> getIssues(Pageable pageable) {
        return issueRepository.findAllIssueEntityBy(pageable);
    }

    public IssueEntity getIssue(UUID id) {
        return issueRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Issue", "id", id.toString())
        );
    }

    public IssueEntity createIssue(IssueRequest request) {
        factService.throwIfFactsNotExist(request.getFacts());

        String insight;
        try {
            insight = ContentContainFactParser.convertLinkIntToUuid(request.getInsight(), request.getFacts());
        } catch (Exception e) {
            throw new ValidationException("Insight is invalid: " + e.getMessage());
        }

        IssueEntity issueEntity = IssueEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .insight(insight)
//                .authorId(request.getAuthorId())
//                .authorName(request.getAuthorName())
//                .authorAvatar(request.getAuthorAvatar())
                .build();
        return issueRepository.save(issueEntity);
    }

    public IssueEntity updateIssue(UUID id, IssueRequest issueRequest) {
        IssueEntity issueEntity = issueRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Issue", "id", id.toString())
        );

        issueRequest.getFacts().forEach(factId -> {
            if (!factRepository.existsById(factId)) {
                throw new EntityNotFoundException("Fact", "id", factId.toString());
            }
        });

        try {
            issueEntity.setInsight(ContentContainFactParser.convertLinkIntToUuid(issueRequest.getInsight(),
                    issueRequest.getFacts()));
        } catch (Exception e) {
            throw new ValidationException("Insight is invalid: " + e.getMessage());
        }

        issueEntity.setTitle(issueRequest.getTitle());
        issueEntity.setDescription(issueRequest.getDescription());
//        issueEntity.setAuthorId(issueRequest.getAuthorId());
//        issueEntity.setAuthorName(issueRequest.getAuthorName());
//        issueEntity.setAuthorAvatar(issueRequest.getAuthorAvatar());
        return issueRepository.save(issueEntity);
    }

    public void deleteIssue(UUID id) {
//        Todo: need to use soft delete
        issueRepository.deleteById(id);
    }

    public Page<FactEntity> getIssueFacts(UUID id, Pageable pageable) {
        List<FactEntity> factEntities = new ArrayList<>();
        Page<ManualIssueFactEntity> manualFactEntities = manualFactRepository.findAllByKey_IssueId(id, pageable);

        for (ManualIssueFactEntity manualIssueFactEntity : manualFactEntities) {
            factEntities.add(manualIssueFactEntity.getFact());
        }

//      Todo: parse viewpoint facts
        return new PageImpl<>(factEntities, pageable, manualFactEntities.getTotalElements());
    }

    @Transactional
    public List<FactEntity> createManualFact(UUID id, List<UUID> factIds) {
        factService.throwIfFactsNotExist(factIds);

        if (!issueRepository.existsById(id)) {
            throw new EntityNotFoundException("Issue", "id", id.toString());
        }

        List<FactEntity> factEntities = new ArrayList<>();
        for (UUID factId : factIds) {
            FactEntity factEntity = factRepository.findById(factId).orElseThrow(
                    () -> new EntityNotFoundException("Fact", "id", factId.toString())
            );

            manualFactRepository.saveByIssueIdAndFactId(id, factId);
            factEntities.add(factEntity);
        }

        return factEntities;
    }
}
