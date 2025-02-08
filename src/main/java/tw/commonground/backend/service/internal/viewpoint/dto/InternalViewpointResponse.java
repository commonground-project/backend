package tw.commonground.backend.service.internal.viewpoint.dto;

import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record InternalViewpointResponse(
        UUID viewpointId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String content,
        UUID authorId,
        int likeCount,
        int dislikeCount,
        int reasonableCount,
        int viewCount,
        UUID issueId,
        List<String> factTags,
        int replyCount,
        Object replyContent,
        float sentimentScore
) {

    public static InternalViewpointResponse fromEntity(ViewpointEntity viewpoint, int replyCount, Object replyContent) {
        UUID issueId = (viewpoint.getIssue() != null) ? viewpoint.getIssue().getId() : null;

        return new InternalViewpointResponse(
                viewpoint.getId(),
                viewpoint.getCreatedAt(),
                viewpoint.getUpdatedAt(),
                viewpoint.getContent(),
                viewpoint.getAuthorId(),
                viewpoint.getLikeCount(),
                viewpoint.getDislikeCount(),
                viewpoint.getReasonableCount(),
                0, // TODO: Replace hardcoded viewCount with actual value
                issueId,
                List.of(), // TODO: Replace hardcoded factTags with actual value
                replyCount,
                replyContent,
                0.0f // TODO: Replace hardcoded sentimentScore with actual value
        );
    }
}
