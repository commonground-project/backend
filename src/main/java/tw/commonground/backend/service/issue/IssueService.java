package tw.commonground.backend.service.issue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.fact.dto.FactMapper;
import tw.commonground.backend.service.fact.dto.FactResponse;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.fact.entity.FactRepository;
import tw.commonground.backend.service.issue.dto.IssueRequest;
import tw.commonground.backend.service.issue.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class IssueService {

    private final IssueRepository issueRepository;

    private final ManualFactRepository manualFactRepository;

    private final FactRepository factRepository;

    public IssueService(IssueRepository issueRepository, ManualFactRepository manualFactRepository, FactRepository factRepository) {
        this.issueRepository = issueRepository;
        this.manualFactRepository = manualFactRepository;
        this.factRepository = factRepository;
    }

    public Page<SimpleIssueEntity> getIssues(Pageable pageable) {
        return issueRepository.findAllIssueEntityBy(pageable);
    }

    public IssueEntity getIssue(UUID id) {
        return issueRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Issue", "id", id.toString())
        );
    }

    public IssueEntity createIssue(IssueRequest issueRequest) {
        IssueEntity issueEntity = IssueEntity.builder()
                .title(issueRequest.getTitle())
                .description(issueRequest.getDescription())
                .insight(issueRequest.getInsight())
//                .authorId(issueRequest.getAuthorId())
//                .authorName(issueRequest.getAuthorName())
//                .authorAvatar(issueRequest.getAuthorAvatar())
                .build();
        return issueRepository.save(issueEntity);
    }

    public IssueEntity updateIssue(UUID id, IssueRequest issueRequest) {
        IssueEntity issueEntity = issueRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Issue", "id", id.toString())
        );
        issueEntity.setTitle(issueRequest.getTitle());
        issueEntity.setDescription(issueRequest.getDescription());
        issueEntity.setInsight(issueRequest.getInsight());
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
        Page<ManualFactEntity> manualFactEntities = manualFactRepository.findAllByIssueId(id, pageable);

        for (ManualFactEntity manualFactEntity : manualFactEntities) {
            factEntities.add(manualFactEntity.getFact());
        }

//      Todo: parse insight facts
        return new PageImpl<>(factEntities, pageable, manualFactEntities.getTotalElements());
    }

    public List<FactEntity> createManualFact(UUID id, List<UUID> factIds) {
        if (!issueRepository.existsById(id)) {
            throw new EntityNotFoundException("Issue", "id", id.toString());
        }

        List<ManualFactEntity> manualFactEntities = new ArrayList<>();
        List<FactEntity> factEntities = new ArrayList<>();
        for (UUID factId : factIds) {
            if (!factRepository.existsById(factId)) {
                throw new EntityNotFoundException("Fact", "id", factId.toString());
            }

            manualFactEntities.add(new ManualFactEntity(id, factId));
            FactEntity factEntity = factRepository.findById(factId).orElseThrow(
                    () -> new EntityNotFoundException("Fact", "id", factId.toString())
            );

            factEntities.add(factEntity);
        }

        manualFactRepository.saveAll(manualFactEntities);
        return factEntities;
    }
}
