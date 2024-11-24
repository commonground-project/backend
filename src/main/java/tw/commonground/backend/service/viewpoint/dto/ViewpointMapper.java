package tw.commonground.backend.service.viewpoint.dto;


import tw.commonground.backend.service.viewpoint.entity.ViewpointReaction;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;

import java.util.List;

public final class ViewpointMapper {

    public ViewpointMapper() {
        // hide the constructor
    }

    public static ViewpointReactionResponse toReactionResponse(ViewpointReaction viewpointReaction) {
        return new ViewpointReactionResponse(viewpointReaction.getReaction().name());
    }

    public static ViewpointResponse toResponse(ViewpointEntity viewpointEntity) {
        return new ViewpointResponse(
            viewpointEntity.getId(),
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


    public static List<ViewpointResponse> toResponses(List<ViewpointEntity> viewpointEntities) {
        return viewpointEntities.stream()
                .map(viewpointEntity -> new ViewpointResponse(
                        viewpointEntity.getId(),
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
