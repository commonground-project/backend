package tw.commonground.backend.service.internal.interaction;

import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.internal.interaction.entity.InteractionEntity;
import tw.commonground.backend.service.internal.interaction.entity.InteractionRepository;
import tw.commonground.backend.service.internal.interaction.entity.InteractionType;
import tw.commonground.backend.service.internal.interaction.entity.RelatedObjectType;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.shared.entity.Reaction;
import tw.commonground.backend.shared.event.react.UserReplyReactedEvent;
import tw.commonground.backend.shared.event.react.UserViewpointReactedEvent;
import tw.commonground.backend.service.internal.interaction.dto.InteractionMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InteractionService {

    private final UserRepository userRepository;
    private final InteractionRepository interactionRepository;

    public InteractionService(UserRepository userRepository, InteractionRepository interactionRepository) {
        this.userRepository = userRepository;
        this.interactionRepository = interactionRepository;
    }

    @EventListener
    @Transactional
    public void onUserViewpointReactedEvent(UserViewpointReactedEvent event) {
        if (event.getReaction() == Reaction.NONE) {
            return;
        }

        InteractionEntity interactionEntity = createInteractionEntityBuilder(event.getDateTime(), event.getUserId(),
                event.getEntityId(), RelatedObjectType.VIEWPOINT, event.getReaction());

        interactionRepository.save(interactionEntity);
    }

    @EventListener
    @Transactional
    public void onUserReplyReactedEvent(UserReplyReactedEvent event) {
        if (event.getReaction() == Reaction.NONE) {
            return;
        }

        InteractionEntity interactionEntity = createInteractionEntityBuilder(event.getDateTime(), event.getUserId(),
                event.getEntityId(), RelatedObjectType.REPLY, event.getReaction());

        interactionRepository.save(interactionEntity);
    }

    private InteractionEntity createInteractionEntityBuilder(LocalDateTime dateTime, Long userId, UUID entityId,
                                                             RelatedObjectType objectType,
                                                             Reaction reaction) {
        UUID userUid = userRepository.getUidById(userId);
        InteractionType interactionType = InteractionMapper.toInteractionType(reaction);

        return InteractionEntity.builder()
                .userId(userUid)
                .objectId(entityId)
                .timestamp(dateTime)
                .type(interactionType)
                .objectType(objectType).build();
    }

    public List<InteractionEntity> getAllInteractions() {
        return interactionRepository.findAllByOrderByTimestampDesc();
    }
}
