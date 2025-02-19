package tw.commonground.backend.service.internal.viewpoint.dto;

import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.shared.util.DateTimeUtils;

import java.util.List;
import java.util.UUID;

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
}
