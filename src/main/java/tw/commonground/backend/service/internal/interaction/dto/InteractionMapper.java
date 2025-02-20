package tw.commonground.backend.service.internal.interaction.dto;

import tw.commonground.backend.service.internal.interaction.entity.InteractionEntity;
import tw.commonground.backend.service.internal.interaction.entity.InteractionType;
import tw.commonground.backend.shared.entity.Reaction;
import tw.commonground.backend.shared.util.DateTimeUtils;

public final class InteractionMapper {
    private InteractionMapper() {
        // hide constructor
    }

    public static InteractionType toInteractionType(Reaction reaction) {
        return switch (reaction) {
            case LIKE -> InteractionType.LIKE_COUNT;
            case DISLIKE -> InteractionType.DISLIKE_COUNT;
            case REASONABLE -> InteractionType.REASONABLE_COUNT;
            default -> throw new IllegalArgumentException("Invalid reaction: " + reaction);
        };
    }

    public static InteractionResponse toInteractionResponse(InteractionEntity entity) {
        return InteractionResponse.builder()
                .id(entity.getId().toString())
                .type(entity.getType().name().toLowerCase())
                .userId(entity.getUserId().toString())
                .relatedObject(toRelatedObject(entity))
                .timestamp(DateTimeUtils.toIso8601String(entity.getTimestamp()))
                .build();
    }

    public static RelatedObject toRelatedObject(InteractionEntity entity) {
        return RelatedObject.builder()
                .type(entity.getObjectType().name())
                .id(entity.getObjectId().toString())
                .build();
    }
}
