package tw.commonground.backend.service.viewpoint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.exception.ValidationException;
import tw.commonground.backend.service.fact.FactService;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.issue.IssueService;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.viewpoint.dto.ViewpointRequest;
import tw.commonground.backend.service.viewpoint.entity.*;
import tw.commonground.backend.shared.content.ContentContainFactParser;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ViewpointService {
    private final ViewpointRepository viewpointRepository;

    private final ViewpointReactionRepository viewpointReactionRepository;

    private final FactService factService;

    private final IssueService issueService;

    private final ViewpointFactRepository viewpointFactRepository;
    private final UserRepository userRepository;

    public ViewpointService(ViewpointRepository viewpointRepository,
                            ViewpointReactionRepository viewpointReactionRepository,
                            FactService factService,
                            ViewpointFactRepository viewpointFactRepository,
                            IssueService issueService, UserRepository userRepository) {
        this.viewpointRepository = viewpointRepository;
        this.viewpointReactionRepository = viewpointReactionRepository;
        this.factService = factService;
        this.viewpointFactRepository = viewpointFactRepository;
        this.issueService = issueService;
        this.userRepository = userRepository;
    }

    public Page<ViewpointEntity> getViewpoints(Pageable pageable) {
        return viewpointRepository.findAll(pageable);
    }

    public Page<ViewpointEntity> getIssueViewpoints(UUID issueId, Pageable pageable) {
        return viewpointRepository.findAllByIssueId(issueId, pageable);
    }

    @Transactional
    public ViewpointEntity createIssueViewpoint(UUID issueId, ViewpointRequest request, FullUserEntity user) {
        factService.throwIfFactsNotExist(request.getFacts());
        issueService.throwIfIssueNotExist(issueId);

        String content = ContentContainFactParser.convertLinkIntToUuid(request.getContent(), request.getFacts());

        ViewpointEntity viewpointEntity = new ViewpointEntity();
        viewpointEntity.setTitle(request.getTitle());
        viewpointEntity.setContent(content);
        viewpointEntity.setIssue(new IssueEntity(issueId));
        viewpointEntity.setAuthor(user);
        viewpointRepository.save(viewpointEntity);

        for (UUID factId : request.getFacts()) {
            viewpointFactRepository.saveByViewpointIdAndFactId(viewpointEntity.getId(), factId);
        }

        return viewpointEntity;
    }

    public ViewpointEntity getViewpoint(UUID id) {
        return viewpointRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Viewpoint", "id", id.toString()));
    }

    @Transactional
    public ViewpointEntity createViewpoint(ViewpointRequest request, FullUserEntity user) {
        factService.throwIfFactsNotExist(request.getFacts());

        String content = ContentContainFactParser.convertLinkIntToUuid(request.getContent(), request.getFacts());

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

    @Transactional
    public ViewpointEntity updateViewpoint(UUID id, ViewpointRequest request) {
        factService.throwIfFactsNotExist(request.getFacts());
        ViewpointEntity viewpointEntity = viewpointRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Viewpoint", "id", id.toString()));

        String content = ContentContainFactParser.convertLinkIntToUuid(request.getContent(), request.getFacts());

        viewpointEntity.setTitle(request.getTitle());
        viewpointEntity.setContent(content);
        viewpointRepository.save(viewpointEntity);

        for (UUID factId : request.getFacts()) {
            viewpointFactRepository.saveByViewpointIdAndFactId(viewpointEntity.getId(), factId);
        }

        return viewpointEntity;
    }

    public void deleteViewpoint(UUID id) {
        viewpointRepository.deleteById(id);
    }

    @Transactional
    public ViewpointReactionEntity reactToViewpoint(Long userId, UUID viewpointId, Reaction reaction) {
        throwIfViewpointNotExist(viewpointId);
        throwIfUserNotExist(userId);

        ViewpointReactionKey viewpointReactionKey = new ViewpointReactionKey(userId, viewpointId);

        Optional<ViewpointReactionEntity> reactionOptional = viewpointReactionRepository.findById(viewpointReactionKey);
        return reactionOptional
                .map(viewpointReactionEntity -> handleExistingReaction(viewpointReactionEntity, viewpointId, reaction))
                .orElseGet(() -> handleNewReaction(viewpointReactionKey, viewpointId, reaction));
    }

    private void throwIfUserNotExist(Long userId) {
        if(!userRepository.existsById(userId)) {
            throw new ValidationException("User not found");
            // can't determine this is just user not found or null user id
        }
    }

    private ViewpointReactionEntity handleNewReaction(ViewpointReactionKey reactionKey, UUID viewpointId,
                                                      Reaction reaction) {


        if (reaction != Reaction.NONE) {
            viewpointReactionRepository.insertReaction(reactionKey, reaction.name());
            // can cause NullPointerException here
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
            throw new ValidationException("Viewpoint not found");
        }
    }

    public List<FactEntity> getFactsOfViewpoint(UUID viewpointId) {
        return viewpointFactRepository.findFactsByViewpointId(viewpointId);
    }

    public Map<UUID, List<FactEntity>> getFactsForViewpoints(List<UUID> viewpointIds) {
        List<ViewpointFactProjection> results = viewpointFactRepository.findFactsByViewpointIds(viewpointIds);
        return results.stream()
                .collect(Collectors.groupingBy(
                        ViewpointFactProjection::getViewpointId,
                        Collectors.mapping(ViewpointFactProjection::getFact, Collectors.toList())
                ));
    }

    public Map<UUID, Reaction> getReactionsForViewpoints(Long userId, List<UUID> viewpointIds) {
        List<ViewpointReactionEntity> reactions = viewpointReactionRepository
                .findReactionsByUserIdAndViewpointIds(userId, viewpointIds);

        return reactions.stream()
                .collect(Collectors.toMap(
                        ViewpointReactionEntity::getViewpointId,
                        ViewpointReactionEntity::getReaction
                ));
    }

    public Reaction getReactionForViewpoint(Long userId, UUID viewpointId) {
        ViewpointReactionKey id = new ViewpointReactionKey(userId, viewpointId);
        return viewpointReactionRepository.findReactionById(id).orElse(Reaction.NONE);
    }

//    public ViewpointEntity addFactToViewpoint(UUID id, UUID factId) {
//        ViewpointEntity viewpointEntity = viewpointRepository.findViewpointEntityById(id).orElseThrow(
//                () -> new EntityNotFoundException("Viewpoint", "id", id.toString()));
//        FactEntity factEntity = factRepository.findById(factId).orElseThrow(
//                () -> new EntityNotFoundException("Fact", "id", factId.toString()));
//        viewpointEntity.getFacts().add(factEntity);
//        viewpointRepository.save(viewpointEntity);
//        return viewpointEntity;
//    }

//    public void deleteFactFromViewpoint(UUID id, UUID factId) {
//        ViewpointEntity viewpointEntity = viewpointRepository.findViewpointEntityById(id).orElseThrow(
//                () -> new EntityNotFoundException("Viewpoint", "id", id.toString()));
//        FactEntity factEntity = factRepository.findById(factId).orElseThrow(
//                () -> new EntityNotFoundException("Fact", "id", factId.toString()));
//        viewpointEntity.getFacts().remove(factEntity);
//        viewpointRepository.save(viewpointEntity);
//    }
}
