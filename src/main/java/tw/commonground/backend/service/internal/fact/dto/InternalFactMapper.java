package tw.commonground.backend.service.internal.fact.dto;

import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.internal.reference.dto.InternalReferenceMapper;
import tw.commonground.backend.shared.util.DateTimeUtils;

public final class InternalFactMapper {

    private InternalFactMapper() {
        // hide the constructor
    }

    public static InternalDetailFactResponse toDetailResponse(FactEntity factEntity) {
        return InternalDetailFactResponse.builder()
                .id(factEntity.getId())
                .title(factEntity.getTitle())
                .createdAt(DateTimeUtils.toIso8601String(factEntity.getCreatedAt()))
                .updatedAt(DateTimeUtils.toIso8601String(factEntity.getUpdatedAt()))
                .authorId(factEntity.getAuthorId())
                .references(factEntity.getReferences().stream()
                        .map(InternalReferenceMapper::toInternalDetailResponse).toList())
                .build();
    }
}

