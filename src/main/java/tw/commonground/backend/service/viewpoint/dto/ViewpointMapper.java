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
        return ViewpointResponse.builder()
                .id(viewpointEntity.getId())
                .createdAt(viewpointEntity.getCreatedAt())
                .updatedAt(viewpointEntity.getUpdatedAt())
                .title(viewpointEntity.getTitle())
                .content(viewpointEntity.getContent())
                .authorId(viewpointEntity.getAuthorId())
                .authorName(viewpointEntity.getAuthorName())
                .authorAvatar(viewpointEntity.getAuthorAvatar())
                .userReaction(viewpointEntity.getUserReaction())
                .likeCount(viewpointEntity.getLikeCount())
                .reasonableCount(viewpointEntity.getReasonableCount())
                .dislikeCount(viewpointEntity.getDislikeCount())
                .build();
//                viewpointEntity.getFacts()
    }


    public static List<ViewpointResponse> toResponses(List<ViewpointEntity> viewpointEntities) {
        return viewpointEntities.stream()
                .map(viewpointEntity -> ViewpointResponse.builder()
                        .id(viewpointEntity.getId())
                        .createdAt(viewpointEntity.getCreatedAt())
                        .updatedAt(viewpointEntity.getUpdatedAt())
                        .title(viewpointEntity.getTitle())
                        .content(viewpointEntity.getContent())
                        .authorId(viewpointEntity.getAuthorId())
                        .authorName(viewpointEntity.getAuthorName())
                        .authorAvatar(viewpointEntity.getAuthorAvatar())
                        .userReaction(viewpointEntity.getUserReaction())
                        .likeCount(viewpointEntity.getLikeCount())
                        .reasonableCount(viewpointEntity.getReasonableCount())
                        .dislikeCount(viewpointEntity.getDislikeCount())
                        .build())
                .toList(); //     viewpointEntity.getFacts()
    }


}
