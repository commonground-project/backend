package tw.commonground.backend.service.internal.issue.dto;

import tw.commonground.backend.service.issue.entity.IssueEntity;

public final class InternalIssueMapper {
    private InternalIssueMapper() {
        // hide constructor
    }

    public static InternalIssueResponse toResponse(IssueEntity issue, int viewpointCount) {
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
