package tw.commonground.backend.service.timeline;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.exception.ValidationException;
import tw.commonground.backend.service.issue.IssueService;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.timeline.dto.NodeRequest;
import tw.commonground.backend.service.timeline.entity.NodeEntity;
import tw.commonground.backend.service.timeline.entity.NodeRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
public class TimelineService {

    private final IssueService issueService;

    private final NodeRepository nodeRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    @PersistenceContext
    private EntityManager entityManager;

    public TimelineService(IssueService issueService,
                           NodeRepository nodeRepository,
                           ApplicationEventPublisher applicationEventPublisher) {
        this.issueService = issueService;
        this.nodeRepository = nodeRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<NodeEntity> getNodes(UUID issueId) {
        issueService.throwIfIssueNotExist(issueId);
        return nodeRepository.findAllByIssueId(issueId);
    }

    public NodeEntity getNode(UUID nodeId) {
        return nodeRepository.findById(nodeId)
                .orElseThrow(() -> new EntityNotFoundException("Node", "id", nodeId.toString()));
    }

    public NodeEntity createNode(UUID issueId, NodeRequest request) {
        issueService.throwIfIssueNotExist(issueId);

        NodeEntity node = new NodeEntity();
        node.setTitle(request.getTitle());
        node.setDescription(request.getDescription());

        try {
            node.setDate(LocalDate.parse(request.getDate()));
        } catch (DateTimeParseException e) {
            throw new ValidationException("date", "Invalid date format");
        }

        IssueEntity issue = entityManager.getReference(IssueEntity.class, issueId);
        node.setIssue(issue);

        applicationEventPublisher.publishEvent(new NodeCreatedEvent(issue, node));

        return nodeRepository.save(node);
    }

    public NodeEntity updateNode(UUID nodeId, NodeRequest request) {
        NodeEntity node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new EntityNotFoundException("Node", "id", nodeId.toString()));

        node.setTitle(request.getTitle());
        node.setDescription(request.getDescription());

        try {
            node.setDate(LocalDate.parse(request.getDate()));
        } catch (DateTimeParseException e) {
            throw new ValidationException("date", "Invalid date format");
        }

        return nodeRepository.save(node);
    }

    public void deleteNode(UUID nodeId) {
        nodeRepository.deleteById(nodeId);
    }
}
