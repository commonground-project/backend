package tw.commonground.backend.service.viewpoint;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.commonground.backend.exception.EntityNotFoundException;
//import tw.commonground.backend.service.fact.entity.FactEntity;
//import tw.commonground.backend.service.fact.entity.FactRepository;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.viewpoint.dto.ViewpointMapper;
import tw.commonground.backend.service.viewpoint.dto.ViewpointUpdateRequest;
import tw.commonground.backend.service.viewpoint.entity.Reaction;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionId;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.lang.String.valueOf;
import static tw.commonground.backend.service.viewpoint.entity.Reaction.*;

@Service
public class ViewpointService {
    private final ViewpointRepository viewpointRepository;
//    private final FactRepository factRepository;
    private final UserRepository userRepository;

    private final ViewpointMapper viewpointMapper = new ViewpointMapper();
    private final ViewpointReactionRepository viewpointReactionRepository;

    public ViewpointService(ViewpointRepository viewpointRepository, ViewpointReactionRepository viewpointReactionRepository, UserRepository userRepository) {
        this.viewpointRepository = viewpointRepository;
        this.viewpointReactionRepository = viewpointReactionRepository;
        this.userRepository = userRepository;
    }

//    public ViewpointService(ViewpointRepository viewpointRepository, FactRepository factRepository) {
//        this.viewpointRepository = viewpointRepository;
//        this.factRepository = factRepository;
//    }

    // TODO: issue viewpoint api

    // service checks if the viewpoint exists in the database
    public ViewpointEntity getViewpoint(UUID id) {
        return viewpointRepository.findViewpointEntityById(id).orElseThrow(
                () -> new EntityNotFoundException("Viewpoint", "id", id.toString()));

    }

    // TODO: issue viewpoint api
    public void deleteViewpoint(UUID id) {
        viewpointRepository.deleteById(id);
        viewpointRepository.flush();
    }

    @Transactional
    public ViewpointReactionEntity reactToViewpoint(String email, UUID viewpointId, String reaction) {

        Long userId = userRepository.findIdByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User", "email", email)).getId();;

        ViewpointReactionId viewpointReactionId = new ViewpointReactionId(userId, viewpointId);

        // check if the user and viewpoint exist in the database
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User", "id", valueOf(userId)));
        ViewpointEntity viewpointEntity = viewpointRepository.findViewpointEntityById(viewpointId).orElseThrow(
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

    public ViewpointEntity updateViewpoint(UUID id, ViewpointUpdateRequest updateRequest) {
        ViewpointEntity viewpointEntity = viewpointRepository.findViewpointEntityById(id).orElseThrow(
                () -> new EntityNotFoundException("Viewpoint", "id", id.toString()));
        viewpointEntity.setTitle(updateRequest.getTitle());
        viewpointEntity.setContent(updateRequest.getContent());
        // updateRequest contains a list of factIds, need to get the fact entities from the database
//        List<UUID> factIds = updateRequest.getFacts();
//        List<FactEntity> facts = factIds.stream()
//                .map(factId -> factRepository.findById(factId)
//                        .orElseThrow(() -> new EntityNotFoundException("Fact", "id", factId.toString())))
//                .collect(Collectors.toList());
//        viewpointEntity.setFacts(facts);
        viewpointRepository.save(viewpointEntity);
        return viewpointEntity;
    }

    public ViewpointEntity createViewpoint(ViewpointUpdateRequest updateRequest) {
        ViewpointEntity viewpointEntity = new ViewpointEntity();
        viewpointEntity.setTitle(updateRequest.getTitle());
        viewpointEntity.setContent(updateRequest.getContent());
        viewpointEntity.setCreatedAt(LocalDateTime.now());
        viewpointEntity.setUpdatedAt(LocalDateTime.now());

        viewpointRepository.save(viewpointEntity);
        return viewpointEntity;
    }

//    public void deleteFactFromViewpoint(UUID id, UUID factId) {
//        ViewpointEntity viewpointEntity = viewpointRepository.findViewpointEntityById(id).orElseThrow(
//                () -> new EntityNotFoundException("Viewpoint", "id", id.toString()));
//        FactEntity factEntity = factRepository.findById(factId).orElseThrow(
//                () -> new EntityNotFoundException("Fact", "id", factId.toString()));
//        viewpointEntity.getFacts().remove(factEntity);
//        viewpointRepository.save(viewpointEntity);
//    }
}
