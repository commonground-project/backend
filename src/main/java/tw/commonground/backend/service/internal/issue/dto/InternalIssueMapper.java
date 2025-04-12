package tw.commonground.backend.service.internal.issue.dto;

import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.shared.util.DateTimeUtils;

public final class InternalIssueMapper {
    private InternalIssueMapper() {
        // hide constructor
    }

    public static InternalIssueResponse toResponse(IssueEntity issue, int viewpointCount) {
        return InternalIssueResponse.builder()
                .issueId(issue.getId())
                .createdAt(DateTimeUtils.toIso8601String(issue.getCreatedAt()))
                .updatedAt(DateTimeUtils.toIso8601String(issue.getUpdatedAt()))
                .title(issue.getTitle())
                .authorId(issue.getAuthorId())
                .viewCount(0) // TODO: Replace hardcoded viewCount with actual value
                .viewpointCount(viewpointCount)
                .build();
    }
}
