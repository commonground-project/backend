package tw.commonground.backend.service.issue.dto;

import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.issue.entity.SimpleIssueEntity;

public final class IssueMapper {
    private IssueMapper() {
        // hide the constructor
    }

    public static IssueResponse toResponse(IssueEntity entity) {
        return IssueResponse.builder()
                .id(entity.getId().toString())
                .createAt(entity.getCreateAt())
                .updatedAt(entity.getUpdatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .insight(entity.getInsight())
                .authorId(entity.getAuthorId())
                .authorName(entity.getAuthorName())
                .authorAvatar(entity.getAuthorAvatar())
//                .insightFacts(entity.getInsightFacts().stream().map(FactMapper::toResponse).toList())
                .build();
    }

    public static SimpleIssueResponse toResponse(SimpleIssueEntity entity) {
        return SimpleIssueResponse.builder()
                .id(entity.getId().toString())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .build();
    }

    public static IssueEntity toEntity(IssueRequest request) {
        return IssueEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .insight(request.getInsight())
                .build();
    }
}
