package tw.commonground.backend.service.internal.issue.dto;

import tw.commonground.backend.service.internal.fact.dto.InternalFactMapper;
import tw.commonground.backend.service.issue.entity.ManualIssueFactEntity;
import tw.commonground.backend.service.internal.viewpoint.dto.InternalDetailViewpointResponse;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.shared.content.ContentParser;
import tw.commonground.backend.shared.util.DateTimeUtils;

import java.util.List;

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

    public static InternalDetailIssueResponse toDetailResponse(
            IssueEntity issue, List<InternalDetailViewpointResponse> internalDetailViewpointResponses) {
        return InternalDetailIssueResponse.builder()
                .id(issue.getId())
                .createdAt(DateTimeUtils.toIso8601String(issue.getCreatedAt()))
                .updatedAt(DateTimeUtils.toIso8601String(issue.getUpdatedAt()))
                .title(issue.getTitle())
                .authorId(issue.getAuthorId())
                .description(issue.getDescription())
                .insight(ContentParser.separateContentAndFacts(issue.getInsight(), List.of()).getText())
                // TODO: Replace with actual facts
                .viewpoints(internalDetailViewpointResponses)
                .facts(issue.getManualFacts()
                        .stream()
                        .map(ManualIssueFactEntity::getFact)
                        .map(InternalFactMapper::toDetailResponse)
                        .toList())
                .build();
    }
}
