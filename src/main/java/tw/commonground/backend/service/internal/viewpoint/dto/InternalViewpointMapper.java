package tw.commonground.backend.service.internal.viewpoint.dto;

import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;

import java.util.List;
import java.util.UUID;

public final class InternalViewpointMapper {

    private InternalViewpointMapper() {
        // hide constructor
    }

    public static InternalViewpointResponse toResponse(
            ViewpointEntity viewpoint, int replyCount, Object replyContent) {

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
