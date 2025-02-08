package  tw.commonground.backend.service.internal.issue.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import tw.commonground.backend.service.issue.entity.IssueEntity;

public record InternalIssueResponse(
        UUID issueId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String title,

        @JsonProperty("publisherId")
        UUID authorId,

        int viewCount,
        int viewpointCount
) {
    public static InternalIssueResponse fromEntity(IssueEntity issue, int viewpointCount) {
        return new InternalIssueResponse(
                issue.getId(),
                issue.getCreatedAt(),
                issue.getUpdatedAt(),
                issue.getTitle(),
                issue.getAuthorId(),
                0, // TODO: Replace hardcoded viewCount with actual value
                viewpointCount
        );
    }
}
