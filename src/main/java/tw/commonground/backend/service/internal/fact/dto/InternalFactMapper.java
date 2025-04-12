package tw.commonground.backend.service.internal.fact.dto;

import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.internal.reference.dto.InternalReferenceMapper;
import tw.commonground.backend.shared.util.DateTimeUtils;

public class InternalFactMapper {
    public static InternalDetailFactResponse toDetailResponse(FactEntity factEntity) {
        return InternalDetailFactResponse.builder()
                .id(factEntity.getId())
                .title(factEntity.getTitle())
                .createdAt(DateTimeUtils.toIso8601String(factEntity.getCreatedAt()))
                .updatedAt(DateTimeUtils.toIso8601String(factEntity.getUpdatedAt()))
                .authorId(factEntity.getAuthorId())
                .authorName(factEntity.getAuthorName())
                .authorAvatar(factEntity.getAuthorAvatar())
                .references(factEntity.getReferences().stream().map(InternalReferenceMapper::toInternalDetailResponse).toList())
                .build();
    }
}

