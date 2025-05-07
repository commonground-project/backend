package tw.commonground.backend.service.issue.dto;

import tw.commonground.backend.service.fact.dto.FactMapper;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.follow.dto.FollowResponse;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.issue.entity.SimpleIssueEntity;
import tw.commonground.backend.shared.content.ContentContainFact;
import tw.commonground.backend.shared.content.ContentParser;
import tw.commonground.backend.shared.util.DateTimeUtils;

import java.util.List;

public final class IssueMapper {
    private IssueMapper() {
        // hide the constructor
    }

    public static IssueResponse toResponse(
            IssueEntity entity,
            Boolean follow,
            List<FactEntity> factEntities,
            Integer viewpointCount) {

        ContentContainFact insight = ContentParser.separateContentAndFacts(entity.getInsight(),
                factEntities.stream().map(FactEntity::getId).toList());

        return IssueResponse.builder()
                .id(entity.getId().toString())
                .createdAt(DateTimeUtils.toIso8601String(entity.getCreatedAt()))
                .updatedAt(DateTimeUtils.toIso8601String(entity.getUpdatedAt()))
                .title(entity.getTitle())
                .description(entity.getDescription())
                .insight(insight.getText())
                .authorId(entity.getAuthorId())
                .authorName(entity.getAuthorName())
                .authorAvatar(entity.getAuthorAvatar())
                .userFollow(FollowResponse.builder().follow(follow).build())
                .viewpointCount(viewpointCount)
                .facts(factEntities.stream().map(FactMapper::toResponse).toList())
                .build();
    }

    public static SimpleIssueResponse toResponse(SimpleIssueEntity entity, Integer viewpointCount) {
        return SimpleIssueResponse.builder()
                .id(entity.getId().toString())
                .createdAt(DateTimeUtils.toIso8601String(entity.getCreatedAt()))
                .updatedAt(DateTimeUtils.toIso8601String(entity.getUpdatedAt()))
                .title(entity.getTitle())
                .description(entity.getDescription())
                .viewpointCount(viewpointCount)
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
