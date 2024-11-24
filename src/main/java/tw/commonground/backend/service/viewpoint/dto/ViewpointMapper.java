package tw.commonground.backend.service.viewpoint.dto;


import tw.commonground.backend.service.viewpoint.entity.ViewPointReaction;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;

import java.util.List;

public final class ViewpointMapper {

    private ViewpointMapper() {
        // hide the constuctor
    }

    public static ViewpointReactionResponse toReactionResponse(ViewPointReaction viewpointReaction) {
        return new ViewpointReactionResponse(viewpointReaction.getReaction().name());
    }

    public static ViewPointResponse toResponse(ViewpointEntity viewpointEntity) {
        return new ViewPointResponse(
            viewpointEntity.getCreatedAt(),
            viewpointEntity.getUpdatedAt(),
            viewpointEntity.getTitle(),
            viewpointEntity.getContent(),
            viewpointEntity.getAuthorId(),
            viewpointEntity.getAuthorName(),
            viewpointEntity.getAuthorAvatar(),
            viewpointEntity.getUserReaction(),
            viewpointEntity.getLikeCount(),
            viewpointEntity.getReasonableCount(),
            viewpointEntity.getDislikeCount(),
            viewpointEntity.getFacts()
        );
    }


    public static List<ViewPointResponse> toResponses(List<ViewpointEntity> viewpointEntities) {
        return viewpointEntities.stream()
                .map(viewpointEntity -> new ViewPointResponse(
                        viewpointEntity.getCreatedAt(),
                        viewpointEntity.getUpdatedAt(),
                        viewpointEntity.getTitle(),
                        viewpointEntity.getContent(),
                        viewpointEntity.getAuthorId(),
                        viewpointEntity.getAuthorName(),
                        viewpointEntity.getAuthorAvatar(),
                        viewpointEntity.getUserReaction(),
                        viewpointEntity.getLikeCount(),
                        viewpointEntity.getReasonableCount(),
                        viewpointEntity.getDislikeCount(),
                        viewpointEntity.getFacts()
                ))
                .toList();
    }


}
