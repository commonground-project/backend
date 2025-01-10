package tw.commonground.backend.service.viewpoint.dto;


import tw.commonground.backend.service.fact.dto.FactMapper;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.viewpoint.entity.Reaction;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReactionEntity;
import tw.commonground.backend.shared.content.ContentContainFact;
import tw.commonground.backend.shared.content.ContentParser;

import java.util.List;

public final class ViewpointMapper {

    private ViewpointMapper() {
        // hide the constructor
    }

    public static ViewpointReactionResponse toReactionResponse(ViewpointReactionEntity viewpointReactionEntity) {
        return new ViewpointReactionResponse(viewpointReactionEntity.getReaction());
    }

    public static ViewpointReactionResponse toReactionResponse(Reaction reaction) {
        return new ViewpointReactionResponse(reaction);
    }

    public static ViewpointResponse toResponse(ViewpointEntity viewpointEntity, Reaction reaction,
                                               List<FactEntity> factEntities) {

        ContentContainFact content = ContentParser.separateContentAndFacts(viewpointEntity.getContent(),
                factEntities.stream().map(FactEntity::getId).toList());

        return ViewpointResponse.builder()
                .id(viewpointEntity.getId())
                .createdAt(viewpointEntity.getCreatedAt())
                .updatedAt(viewpointEntity.getUpdatedAt())
                .title(viewpointEntity.getTitle())
                .content(content.getText())
                .authorId(viewpointEntity.getAuthorId())
                .authorName(viewpointEntity.getAuthorName())
                .authorAvatar(viewpointEntity.getAuthorAvatar())
                .likeCount(viewpointEntity.getLikeCount())
                .reasonableCount(viewpointEntity.getReasonableCount())
                .dislikeCount(viewpointEntity.getDislikeCount())
                .userReaction(toReactionResponse(reaction))
                .facts(factEntities.stream().map(FactMapper::toResponse).toList())
                .build();
    }
}
