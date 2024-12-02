package tw.commonground.backend.service.viewpoint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.exception.EntityNotFoundException;
//import tw.commonground.backend.service.fact.entity.FactEntity;
//import tw.commonground.backend.service.fact.entity.FactRepository;
import tw.commonground.backend.service.fact.FactService;
import tw.commonground.backend.service.fact.dto.FactMapper;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.fact.entity.FactRepository;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.viewpoint.dto.ViewpointRequest;
import tw.commonground.backend.service.viewpoint.entity.*;
import tw.commonground.backend.shared.content.ContentContainFactParser;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;
import static tw.commonground.backend.service.viewpoint.entity.Reaction.*;

@Service
public class ViewpointService {
    private final ViewpointRepository viewpointRepository;

    private final UserRepository userRepository;

    private final ViewpointReactionRepository viewpointReactionRepository;

    private final FactService factService;

    private final FactRepository factRepository;

    private final ViewpointFactRepository viewpointFactRepository;

    public ViewpointService(ViewpointRepository viewpointRepository,
                            ViewpointReactionRepository viewpointReactionRepository,
                            UserRepository userRepository,
                            FactRepository factRepository, FactService factService, ViewpointFactRepository viewpointFactRepository) {
        this.viewpointRepository = viewpointRepository;
        this.viewpointReactionRepository = viewpointReactionRepository;
        this.userRepository = userRepository;
        this.factRepository = factRepository;
        this.factService = factService;
        this.viewpointFactRepository = viewpointFactRepository;
    }

    // TODO: issue viewpoint api

    public Page<ViewpointEntity> getViewpoints(Pageable pageable) {
        return viewpointRepository.findAll(pageable);
    }

    public Page<ViewpointEntity> getIssueViewpoints(UUID issueId, Pageable pageable) {
        return viewpointRepository.findAllByIssueId(issueId, pageable);
    }

    public ViewpointEntity getViewpoint(UUID id) {
        return viewpointRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Viewpoint", "id", id.toString()));
    }

    @Transactional
    public ViewpointEntity createViewpoint(ViewpointRequest request) {
        factService.throwIfFactsNotExist(request.getFacts());

        String content = ContentContainFactParser.convertLinkIntToUuid(request.getContent(), request.getFacts());

        ViewpointEntity viewpointEntity = new ViewpointEntity();
        viewpointEntity.setTitle(request.getTitle());
        viewpointEntity.setContent(content);
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

    // TODO: issue viewpoint api
    public void deleteViewpoint(UUID id) {
        viewpointRepository.deleteById(id);
    }

    @Transactional
    public ViewpointReactionEntity reactToViewpoint(String email, UUID viewpointId, String reaction) {

        Long userId = userRepository.findIdByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User", "email", email)).getId();
        ;

        ViewpointReactionId viewpointReactionId = new ViewpointReactionId(userId, viewpointId);

        // check if the user and viewpoint exist in the database
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User", "id", valueOf(userId)));
        ViewpointEntity viewpointEntity = viewpointRepository.findById(viewpointId).orElseThrow(
                () -> new EntityNotFoundException("Viewpoint", "id", viewpointId.toString()));

        // check if the viewpointReactionEntity exists in the database, if not means the user has not reacted to the viewpoint
        Reaction previousReaction = viewpointReactionRepository.findReactionById(viewpointReactionId).orElseGet(() -> {
            return Reaction.NONE;
        });

        switch (previousReaction) {
            case NONE:
                break;
            case LIKE:
                viewpointRepository.decrementLikeCount(viewpointId);
                break;
            case REASONABLE:
                viewpointRepository.decrementReasonableCount(viewpointId);
                break;
            case DISLIKE:
                viewpointRepository.decrementDislikeCount(viewpointId);
                break;
            default:
                throw new IllegalArgumentException("Invalid reaction: " + previousReaction);
        }

        ViewpointReactionEntity viewpointReactionEntity = new ViewpointReactionEntity();
        // create a new ViewpointReactionEntity with the given userId and viewpointId
        viewpointReactionEntity.setId(viewpointReactionId);
        viewpointReactionEntity.setReaction(Reaction.valueOf(reaction));

        switch (Reaction.valueOf(reaction)) {
            case NONE: // delete the viewpointReactionEntity if the reaction is NONE
                viewpointReactionRepository.delete(viewpointReactionEntity);
                viewpointRepository.flush();
                break;
            case LIKE:
                viewpointReactionEntity.setReaction(LIKE);
                viewpointRepository.incrementLikeCount(viewpointId);
                viewpointReactionRepository.save(viewpointReactionEntity);
                break;
            case REASONABLE:
                viewpointReactionEntity.setReaction(REASONABLE);
                viewpointRepository.incrementReasonableCount(viewpointId);
                viewpointReactionRepository.save(viewpointReactionEntity);
                break;
            case DISLIKE:
                viewpointReactionEntity.setReaction(DISLIKE);
                viewpointRepository.incrementDislikeCount(viewpointId);
                viewpointReactionRepository.save(viewpointReactionEntity);
                break;
            default:
                throw new IllegalArgumentException("Invalid reaction: " + reaction);
        }
        return viewpointReactionEntity;
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
