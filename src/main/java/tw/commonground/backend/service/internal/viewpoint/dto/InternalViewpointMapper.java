package tw.commonground.backend.service.internal.viewpoint.dto;

import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.internal.fact.dto.InternalFactMapper;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.shared.content.ContentContainFact;
import tw.commonground.backend.shared.content.ContentParser;
import tw.commonground.backend.shared.util.DateTimeUtils;

import java.util.List;
import java.util.UUID;

// import static tw.commonground.backend.service.viewpoint.dto.ViewpointMapper.toReactionResponse;

public final class InternalViewpointMapper {

    private InternalViewpointMapper() {
        // hide constructor
    }

    public static InternalViewpointResponse toResponse(
            ViewpointEntity viewpoint, int replyCount, Object replyContent) {

        UUID issueId = (viewpoint.getIssue() != null) ? viewpoint.getIssue().getId() : null;

        return InternalViewpointResponse.builder()
                .viewpointId(viewpoint.getId())
                .createdAt(DateTimeUtils.toIso8601String(viewpoint.getCreatedAt()))
                .updatedAt(DateTimeUtils.toIso8601String(viewpoint.getUpdatedAt()))
                .content(viewpoint.getContent())
                .authorId(viewpoint.getAuthorId())
                .likeCount(viewpoint.getLikeCount())
                .dislikeCount(viewpoint.getDislikeCount())
                .reasonableCount(viewpoint.getReasonableCount())
                .viewCount(0) // TODO: Replace hardcoded viewCount with actual value
                .issueId(issueId)
                .factTags(List.of()) // TODO: Replace hardcoded factTags with actual value
                .replyCount(replyCount)
                .replyContent(replyContent)
                .sentimentScore(0.0f) // TODO: Replace hardcoded sentimentScore with actual value
                .build();
    }

    public static InternalDetailViewpointResponse toDetailResponse(ViewpointEntity viewpointEntity, List<FactEntity> factEntities) {

        ContentContainFact content = ContentParser.separateContentAndFacts(viewpointEntity.getContent(),
                factEntities.stream().map(FactEntity::getId).toList());

        return InternalDetailViewpointResponse.builder()
                .id(viewpointEntity.getId())
                .createdAt(DateTimeUtils.toIso8601String(viewpointEntity.getCreatedAt()))
                .updatedAt(DateTimeUtils.toIso8601String(viewpointEntity.getUpdatedAt()))
                .title(viewpointEntity.getTitle())
                .content(content.getText())
                .authorId(viewpointEntity.getAuthorId())
                .authorName(viewpointEntity.getAuthorName())
                .authorAvatar(viewpointEntity.getAuthorAvatar())
                .likeCount(viewpointEntity.getLikeCount())
                .reasonableCount(viewpointEntity.getReasonableCount())
                .dislikeCount(viewpointEntity.getDislikeCount())
//                .userReaction(toReactionResponse(reaction))
                .facts(factEntities.stream().map(InternalFactMapper::toDetailResponse).toList())
                .build();
    }
}
