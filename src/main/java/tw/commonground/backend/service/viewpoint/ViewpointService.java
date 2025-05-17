package tw.commonground.backend.service.viewpoint;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.viewpoint.dto.ViewpointRequest;
import tw.commonground.backend.service.viewpoint.entity.*;
import tw.commonground.backend.shared.content.ContentParser;
import tw.commonground.backend.shared.tracing.Traced;
import tw.commonground.backend.shared.entity.Reaction;
import tw.commonground.backend.shared.event.comment.UserViewpointCommentedEvent;
import tw.commonground.backend.shared.event.react.UserViewpointReactedEvent;

import java.util.*;
import java.util.stream.Collectors;

@Traced
@Service
@CacheConfig(cacheNames = "viewpoint")
public class ViewpointService {
    private static final String VIEWPOINT_KEY = "Viewpoint";

    private static final String VIEWPOINT_REACTION_LOCK_FORMAT = "viewpoint.reaction.%s.%d";

    private final ViewpointRepository viewpointRepository;

    private final ViewpointReactionRepository viewpointReactionRepository;

    private final FactService factService;

    private final IssueService issueService;

    private final LockService lockService;

    private final ViewpointFactRepository viewpointFactRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    public ViewpointService(ViewpointRepository viewpointRepository,
                            ViewpointReactionRepository viewpointReactionRepository,
                            FactService factService,
                            ViewpointFactRepository viewpointFactRepository,
                            IssueService issueService, LockService lockService,
                            ApplicationEventPublisher applicationEventPublisher) {
        this.viewpointRepository = viewpointRepository;
        this.viewpointReactionRepository = viewpointReactionRepository;
        this.factService = factService;
        this.viewpointFactRepository = viewpointFactRepository;
        this.issueService = issueService;
        this.lockService = lockService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Cacheable(key = "{'allViewpoints', #pageable.pageNumber}")
    public Page<ViewpointEntity> getViewpoints(Pageable pageable) {
        return viewpointRepository.findAll(pageable);
    }

    @Cacheable(value = "issueViewpoint",  key = "{#issueId, #pageable.pageNumber}")
    public Page<ViewpointEntity> getIssueViewpoints(UUID issueId, Pageable pageable) {
        return viewpointRepository.findAllByIssueId(issueId, pageable);
    }

    @Caching(evict = {
            @CacheEvict(value = "viewpoint", allEntries = true),
            @CacheEvict(value = "issue", key = "#issueId"),
            @CacheEvict(value = "issue", key = "'allIssues'"),
            @CacheEvict(value = "issueViewpoint", allEntries = true)
    })
    @Transactional
    public ViewpointEntity createIssueViewpoint(UUID issueId, ViewpointRequest request, FullUserEntity user) {
        factService.throwIfFactsNotExist(request.getFacts());
        issueService.throwIfIssueNotExist(issueId);

        String content = ContentParser.convertLinkIntToUuid(request.getContent(), request.getFacts());
        String issueTitle = issueService.getIssue(issueId).getTitle();

        ViewpointEntity viewpointEntity = new ViewpointEntity();
        viewpointEntity.setTitle(request.getTitle());
        viewpointEntity.setContent(content);
        viewpointEntity.setIssue(new IssueEntity(issueId));
        viewpointEntity.setAuthor(user);
        viewpointRepository.save(viewpointEntity);

        for (UUID factId : request.getFacts()) {
            viewpointFactRepository.saveByViewpointIdAndFactId(viewpointEntity.getId(), factId);
        }

        applicationEventPublisher.publishEvent(new ViewpointCreatedEvent(user, viewpointEntity, issueTitle));
        applicationEventPublisher.publishEvent(new UserViewpointCommentedEvent(this,
                user.getId(), viewpointEntity.getId(), request.getContent()));

        return viewpointEntity;
    }

    @Cacheable(key = "#id")
    public ViewpointEntity getViewpoint(UUID id) {
        return viewpointRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(VIEWPOINT_KEY, "id", id.toString()));
    }

    @CacheEvict(allEntries = true)
    @Transactional
    public ViewpointEntity createViewpoint(ViewpointRequest request, FullUserEntity user) {
        factService.throwIfFactsNotExist(request.getFacts());

        String content = ContentParser.convertLinkIntToUuid(request.getContent(), request.getFacts());

        ViewpointEntity viewpointEntity = new ViewpointEntity();
        viewpointEntity.setTitle(request.getTitle());
        viewpointEntity.setContent(content);
        viewpointEntity.setAuthor(user);
        viewpointRepository.save(viewpointEntity);

        for (UUID factId : request.getFacts()) {
            viewpointFactRepository.saveByViewpointIdAndFactId(viewpointEntity.getId(), factId);
        }

        return viewpointEntity;
    }

    @Caching(evict = {
            @CacheEvict(value = "viewpoint", allEntries = true),
            @CacheEvict(value = "issueViewpoint", allEntries = true)
    })
    @Transactional
    public ViewpointEntity updateViewpoint(UUID id, ViewpointRequest request) {
        factService.throwIfFactsNotExist(request.getFacts());
        ViewpointEntity viewpointEntity = viewpointRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(VIEWPOINT_KEY, "id", id.toString()));

        String content = ContentParser.convertLinkIntToUuid(request.getContent(), request.getFacts());

        viewpointEntity.setTitle(request.getTitle());
        viewpointEntity.setContent(content);
        viewpointRepository.save(viewpointEntity);

        for (UUID factId : request.getFacts()) {
            viewpointFactRepository.saveByViewpointIdAndFactId(viewpointEntity.getId(), factId);
        }

        return viewpointEntity;
    }

    @Caching(evict = {
            @CacheEvict(value = "viewpoint", allEntries = true),
            @CacheEvict(value = "issueViewpoint", allEntries = true)
    })
    public void deleteViewpoint(UUID id) {
        viewpointRepository.deleteById(id);
    }

    @Caching(evict = {
            @CacheEvict(value = "viewpoint", allEntries = true),
            @CacheEvict(value = "issueViewpoint", allEntries = true),
            @CacheEvict(value = "reactionViewpoint", allEntries = true)
    })
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ViewpointReactionEntity reactToViewpoint(Long userId, UUID viewpointId, Reaction reaction) {
        String lockKey = String.format(VIEWPOINT_REACTION_LOCK_FORMAT, viewpointId, userId);

        return lockService.executeWithLock(lockKey, () -> {
            ViewpointReactionKey viewpointReactionKey = new ViewpointReactionKey(userId, viewpointId);

            Optional<ViewpointReactionEntity> reactionOptional =
                    viewpointReactionRepository.findById(viewpointReactionKey);

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
            viewpointReactionRepository.insertReaction(reactionKey, reaction.name());
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
            viewpointReactionRepository.updateReaction(reactionEntity.getId(), newReaction.name());
            updateReactionCount(viewpointId, newReaction, 1);
        } else {
            viewpointReactionRepository.updateReaction(reactionEntity.getId(), newReaction.name());
            updateReactionCount(viewpointId, previousReaction, -1);
            updateReactionCount(viewpointId, newReaction, 1);
        }

        reactionEntity.setReaction(newReaction);
        return reactionEntity;
    }

    private void updateReactionCount(UUID viewpointId, Reaction reaction, int delta) {
        viewpointRepository.updateReactionCount(viewpointId, reaction, delta);
    }

    public void throwIfViewpointNotExist(UUID viewpointId) {
        if (!viewpointRepository.existsById(viewpointId)) {
            throw new EntityNotFoundException(VIEWPOINT_KEY, "id", viewpointId.toString());
        }
    }

    @Cacheable({"fact"})
    public List<FactEntity> getFactsOfViewpoint(UUID viewpointId) {
        return viewpointFactRepository.findFactsByViewpointId(viewpointId);
    }

    @Cacheable({"fact"})
    public Map<UUID, List<FactEntity>> getFactsForViewpoints(List<UUID> viewpointIds) {
        List<ViewpointFactProjection> results = viewpointFactRepository.findFactsByViewpointIds(viewpointIds);
        return results.stream()
                .collect(Collectors.groupingBy(
                        ViewpointFactProjection::getViewpointId,
                        Collectors.mapping(ViewpointFactProjection::getFact, Collectors.toList())
                ));
    }

    @Cacheable({"reactionViewpoint"})
    public Map<UUID, Reaction> getReactionsForViewpoints(Long userId, List<UUID> viewpointIds) {
        List<ViewpointReactionEntity> reactions = viewpointReactionRepository
                .findReactionsByUserIdAndViewpointIds(userId, viewpointIds);

        return reactions.stream()
                .collect(Collectors.toMap(
                        ViewpointReactionEntity::getViewpointId,
                        ViewpointReactionEntity::getReaction
                ));
    }

    @Cacheable({"reactionViewpoint"})
    public Reaction getReactionForViewpoint(Long userId, UUID viewpointId) {
        ViewpointReactionKey id = new ViewpointReactionKey(userId, viewpointId);
        return viewpointReactionRepository.findReactionById(id).orElse(Reaction.NONE);
    }
}
