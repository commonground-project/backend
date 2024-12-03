package tw.commonground.backend.service.viewpoint.dto;


import tw.commonground.backend.service.fact.dto.FactMapper;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionEntity;

import java.util.List;

public final class ViewpointMapper {

    private ViewpointMapper() {
        // hide the constructor
    }

    public static ViewpointReactionResponse toReactionResponse(ViewpointReactionEntity viewpointReactionEntity) {
        return new ViewpointReactionResponse(viewpointReactionEntity.getReaction());
    }

    public static ViewpointResponse toResponse(ViewpointEntity viewpointEntity, List<FactEntity> factEntities) {
        return ViewpointResponse.builder()
                .id(viewpointEntity.getId())
                .createdAt(viewpointEntity.getCreatedAt())
                .updatedAt(viewpointEntity.getUpdatedAt())
                .title(viewpointEntity.getTitle())
                .content(viewpointEntity.getContent())
                .authorId(viewpointEntity.getAuthorId())
                .authorName(viewpointEntity.getAuthorName())
                .authorAvatar(viewpointEntity.getAuthorAvatar())
                .likeCount(viewpointEntity.getLikeCount())
                .reasonableCount(viewpointEntity.getReasonableCount())
                .dislikeCount(viewpointEntity.getDislikeCount())
                .facts(factEntities.stream().map(FactMapper::toResponse).toList())
                .build();
    }
}
