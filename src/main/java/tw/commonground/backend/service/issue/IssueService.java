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
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.shared.content.ContentParser;
import tw.commonground.backend.shared.tracing.Traced;

import java.time.LocalDateTime;
import java.util.*;

@Traced
@Service
public class IssueService {

    private final IssueRepository issueRepository;

    private final IssueFollowRepository issueFollowRepository;

    private final ManualFactRepository manualFactRepository;

    private final FactRepository factRepository;

    private final FactService factService;

    public IssueService(IssueRepository issueRepository,
                        IssueFollowRepository issueFollowRepository,
                        ManualFactRepository manualFactRepository,
                        FactRepository factRepository,
                        FactService factService) {
        this.issueRepository = issueRepository;
        this.issueFollowRepository = issueFollowRepository;
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

    public IssueEntity createIssue(IssueRequest request, FullUserEntity user) {
        factService.throwIfFactsNotExist(request.getFacts());

        String insight;
        try {
            insight = ContentParser.convertLinkIntToUuid(request.getInsight(), request.getFacts());
        } catch (Exception e) {
            throw new ValidationException("Insight is invalid: " + e.getMessage());
        }

        IssueEntity issueEntity = new IssueEntity();
        issueEntity.setTitle(request.getTitle());
        issueEntity.setDescription(request.getDescription());
        issueEntity.setInsight(insight);
        issueEntity.setAuthor(user);
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
            issueEntity.setInsight(ContentParser.convertLinkIntToUuid(issueRequest.getInsight(),
                    issueRequest.getFacts()));
        } catch (Exception e) {
            throw new ValidationException("Insight is invalid: " + e.getMessage());
        }

        issueEntity.setTitle(issueRequest.getTitle());
        issueEntity.setDescription(issueRequest.getDescription());
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

    @Transactional
    public IssueFollowEntity followIssue(Long userId, UUID issueId, Boolean follow) {
        IssueFollowKey id = new IssueFollowKey(userId, issueId);
        if (issueFollowRepository.findById(id).isPresent()) {
            issueFollowRepository.updateFollowById(id, follow);
        } else {
            issueFollowRepository.insertFollowById(id, follow);
        }
        IssueFollowEntity issueFollowEntity = new IssueFollowEntity();
        issueFollowEntity.setId(id);
        issueFollowEntity.setFollow(follow);
        issueFollowEntity.setUpdatedAt(LocalDateTime.now());
        return issueFollowEntity;
    }

    public Boolean getFollowForIssue(Long userId, UUID issueId) {
        IssueFollowKey id = new IssueFollowKey(userId, issueId);
        return issueFollowRepository.findFollowById(id).orElse(false);
    }

    public List<Long> getIssueFollowersById(UUID issueId) {
        return issueFollowRepository.findUsersIdByIssueIdAndFollowTrue(issueId).orElse(Collections.emptyList());
    }

    public void throwIfIssueNotExist(UUID id) {
        if (!issueRepository.existsById(id)) {
            throw new EntityNotFoundException("Issue", "id", id.toString());
        }
    }
}
