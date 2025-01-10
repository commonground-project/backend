package tw.commonground.backend.service.issue.dto;

import tw.commonground.backend.service.fact.dto.FactMapper;
import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.issue.entity.SimpleIssueEntity;
import tw.commonground.backend.shared.content.ContentContainFact;
import tw.commonground.backend.shared.content.ContentParser;

import java.util.List;

public final class IssueMapper {
    private IssueMapper() {
        // hide the constructor
    }

    public static IssueResponse toResponse(IssueEntity entity, List<FactEntity> factEntities) {

        ContentContainFact insight = ContentParser.separateContentAndFacts(entity.getInsight(),
                factEntities.stream().map(FactEntity::getId).toList());

        return IssueResponse.builder()
                .id(entity.getId().toString())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .insight(insight.getText())
                .authorId(entity.getAuthorId())
                .authorName(entity.getAuthorName())
                .authorAvatar(entity.getAuthorAvatar())
                .facts(factEntities.stream().map(FactMapper::toResponse).toList())
                .build();
    }

    public static SimpleIssueResponse toResponse(SimpleIssueEntity entity) {
        return SimpleIssueResponse.builder()
                .id(entity.getId().toString())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
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
