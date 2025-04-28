package tw.commonground.backend.service.viewpoint;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.fact.FactService;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.issue.IssueService;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.lock.LockService;
import tw.commonground.backend.service.reply.entity.ReplyRepository;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.viewpoint.dto.ViewpointRequest;
import tw.commonground.backend.service.viewpoint.entity.*;
import tw.commonground.backend.service.viewpoint.repository.*;
import tw.commonground.backend.shared.content.ContentParser;
import tw.commonground.backend.shared.tracing.Traced;
import tw.commonground.backend.shared.entity.Reaction;
import tw.commonground.backend.shared.event.comment.UserViewpointCommentedEvent;
import tw.commonground.backend.shared.event.react.UserViewpointReactedEvent;

import java.util.*;
import java.util.stream.Collectors;

@Traced
@Service
public class ViewpointService {
    private static final String VIEWPOINT_KEY = "Viewpoint";

    private static final String VIEWPOINT_REACTION_LOCK_FORMAT = "viewpoint.reaction.%s.%d";

    private final ViewpointRepositoryContainer viewpointRepositoryContainer;

    private final FactService factService;

    private final IssueService issueService;

    private final LockService lockService;

    private final ReplyRepository replyRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    public ViewpointService(ViewpointRepositoryContainer viewpointRepositoryContainer,
                            FactService factService,
                            IssueService issueService,
                            LockService lockService,
                            ReplyRepository replyRepository,
                            ApplicationEventPublisher applicationEventPublisher) {
        this.viewpointRepositoryContainer = viewpointRepositoryContainer;
        this.factService = factService;
        this.issueService = issueService;
        this.lockService = lockService;
        this.replyRepository = replyRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Page<ViewpointEntity> getViewpoints(Pageable pageable) {
        return viewpointRepositoryContainer.findAllViewpoints(pageable);
    }

    public Page<ViewpointEntity> getIssueViewpoints(UUID issueId, Pageable pageable) {
        return viewpointRepositoryContainer.findAllViewpointsByIssueId(issueId, pageable);
    }

    @Transactional
    public ViewpointEntity createIssueViewpoint(UUID issueId, ViewpointRequest request, FullUserEntity user) {
        factService.throwIfFactsNotExist(request.getFacts());
        issueService.throwIfIssueNotExist(issueId);

        String content = ContentParser.convertLinkIntToUuid(request.getContent(), request.getFacts());

        ViewpointEntity viewpointEntity = new ViewpointEntity();
        viewpointEntity.setTitle(request.getTitle());
        viewpointEntity.setContent(content);
        viewpointEntity.setIssue(new IssueEntity(issueId));
        viewpointEntity.setAuthor(user);
        viewpointRepositoryContainer.save(viewpointEntity);

        for (UUID factId : request.getFacts()) {
            viewpointRepositoryContainer.saveByViewpointIdAndFactId(viewpointEntity.getId(), factId);
        }

        applicationEventPublisher.publishEvent(new UserViewpointCommentedEvent(this,
                user.getId(), viewpointEntity.getId(), request.getContent()));

        return viewpointEntity;
    }

    public ViewpointEntity getViewpoint(UUID id) {
        return viewpointRepositoryContainer.findViewpointById(id).orElseThrow(
                () -> new EntityNotFoundException(VIEWPOINT_KEY, "id", id.toString()));
    }

    @Transactional
    public ViewpointEntity createViewpoint(ViewpointRequest request, FullUserEntity user) {
        factService.throwIfFactsNotExist(request.getFacts());

        String content = ContentParser.convertLinkIntToUuid(request.getContent(), request.getFacts());

        ViewpointEntity viewpointEntity = new ViewpointEntity();
        viewpointEntity.setTitle(request.getTitle());
        viewpointEntity.setContent(content);
        viewpointEntity.setAuthor(user);
        viewpointRepositoryContainer.save(viewpointEntity);

        for (UUID factId : request.getFacts()) {
            viewpointRepositoryContainer.saveByViewpointIdAndFactId(viewpointEntity.getId(), factId);
        }

        return viewpointEntity;
    }

    @Transactional
    public ViewpointEntity updateViewpoint(UUID id, ViewpointRequest request) {
        factService.throwIfFactsNotExist(request.getFacts());
        ViewpointEntity viewpointEntity = viewpointRepositoryContainer.findViewpointById(id).orElseThrow(
                () -> new EntityNotFoundException(VIEWPOINT_KEY, "id", id.toString()));

        String content = ContentParser.convertLinkIntToUuid(request.getContent(), request.getFacts());

        viewpointEntity.setTitle(request.getTitle());
        viewpointEntity.setContent(content);
        viewpointRepositoryContainer.save(viewpointEntity);

        for (UUID factId : request.getFacts()) {
            viewpointRepositoryContainer.saveByViewpointIdAndFactId(viewpointEntity.getId(), factId);
        }

        return viewpointEntity;
    }

    public void deleteViewpoint(UUID id) {
        viewpointRepositoryContainer.deleteViewpointById(id);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ViewpointReactionEntity reactToViewpoint(Long userId, UUID viewpointId, Reaction reaction) {
        String lockKey = String.format(VIEWPOINT_REACTION_LOCK_FORMAT, viewpointId, userId);

        return lockService.executeWithLock(lockKey, () -> {
            ViewpointReactionKey viewpointReactionKey = new ViewpointReactionKey(userId, viewpointId);

            Optional<ViewpointReactionEntity> reactionOptional =
                    viewpointRepositoryContainer.findViewpointReactionById(viewpointReactionKey);

            ViewpointReactionEntity viewpointReactionEntity = reactionOptional
                    .map(reactionEntity -> handleExistingReaction(reactionEntity, viewpointId, reaction))
                    .orElseGet(() -> handleNewReaction(viewpointReactionKey, viewpointId, reaction));

            applicationEventPublisher.publishEvent(new UserViewpointReactedEvent(this,
                    userId, viewpointId, reaction));

            return viewpointReactionEntity;
        });
    }

    private ViewpointReactionEntity handleNewReaction(ViewpointReactionKey reactionKey, UUID viewpointId,
                                                      Reaction reaction) {

        if (reaction != Reaction.NONE) {
            viewpointRepositoryContainer.insertReaction(reactionKey, reaction.name());
            updateReactionCount(viewpointId, reaction, 1);
        }

        ViewpointReactionEntity reactionEntity = new ViewpointReactionEntity();
        reactionEntity.setId(reactionKey);
        reactionEntity.setReaction(reaction);

        return reactionEntity;
    }

    private ViewpointReactionEntity handleExistingReaction(ViewpointReactionEntity reactionEntity, UUID viewpointId,
                                                           Reaction newReaction) {

        Reaction previousReaction = reactionEntity.getReaction();

        if (previousReaction == newReaction) {
            return reactionEntity;
        }

        if (previousReaction == Reaction.NONE) {
            viewpointRepositoryContainer.updateReaction(reactionEntity.getId(), newReaction.name());
            updateReactionCount(viewpointId, newReaction, 1);
        } else {
            viewpointRepositoryContainer.updateReaction(reactionEntity.getId(), newReaction.name());
            updateReactionCount(viewpointId, previousReaction, -1);
            updateReactionCount(viewpointId, newReaction, 1);
        }

        reactionEntity.setReaction(newReaction);
        return reactionEntity;
    }

    private void updateReactionCount(UUID viewpointId, Reaction reaction, int delta) {
        viewpointRepositoryContainer.updateReactionCount(viewpointId, reaction, delta);
    }

    public void throwIfViewpointNotExist(UUID viewpointId) {
        if (!viewpointRepositoryContainer.existsById(viewpointId)) {
            throw new EntityNotFoundException(VIEWPOINT_KEY, "id", viewpointId.toString());
        }
    }

    public List<FactEntity> getFactsOfViewpoint(UUID viewpointId) {
        return viewpointRepositoryContainer.findFactsByViewpointId(viewpointId);
    }

    public Map<UUID, List<FactEntity>> getFactsForViewpoints(List<UUID> viewpointIds) {
        List<ViewpointFactProjection> results = viewpointRepositoryContainer.findFactsByViewpointIds(viewpointIds);
        return results.stream()
                .collect(Collectors.groupingBy(
                        ViewpointFactProjection::getViewpointId,
                        Collectors.mapping(ViewpointFactProjection::getFact, Collectors.toList())
                ));
    }

    public Map<UUID, Reaction> getReactionsForViewpoints(Long userId, List<UUID> viewpointIds) {
        List<ViewpointReactionEntity> reactions = viewpointRepositoryContainer
                .findReactionsByUserIdAndViewpointIds(userId, viewpointIds);

        return reactions.stream()
                .collect(Collectors.toMap(
                        ViewpointReactionEntity::getViewpointId,
                        ViewpointReactionEntity::getReaction
                ));
    }

    public Reaction getReactionForViewpoint(Long userId, UUID viewpointId) {
        ViewpointReactionKey id = new ViewpointReactionKey(userId, viewpointId);
        return viewpointRepositoryContainer.findReactionById(id).orElse(Reaction.NONE);
    }

    public Map<UUID, Integer> getReplyCountForViewpoints(List<UUID> viewpointIds) {
        return viewpointIds.stream()
                .collect(Collectors.toMap(
                        viewpointId -> viewpointId,
                        replyRepository::countByViewpointId
                ));
    }

    public Integer getReplyCountByViewpointId(UUID viewpointId) {
        return replyRepository.countByViewpointId(viewpointId);
    }
}
