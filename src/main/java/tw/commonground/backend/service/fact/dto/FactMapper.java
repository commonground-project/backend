package tw.commonground.backend.service.fact.dto;

import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.reference.ReferenceMapper;
import tw.commonground.backend.shared.util.DateTimeUtils;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class FactMapper {

    private FactMapper() {
        // hide the constructor
    }

    public static FactResponse toResponse(FactEntity factEntity) {
        return FactResponse.builder()
                .id(factEntity.getId())
                .title(factEntity.getTitle())
                .createdAt(DateTimeUtils.toIso8601String(factEntity.getCreatedAt()))
                .updatedAt(DateTimeUtils.toIso8601String(factEntity.getUpdatedAt()))
                .authorId(factEntity.getAuthorId())
                .authorName(factEntity.getAuthorName())
                .authorAvatar(factEntity.getAuthorAvatar())
                .references(factEntity.getReferences().stream().map(ReferenceMapper::toResponse).toList())
                .build();
    }

    public static Set<FactEntity> toEntities(Collection<UUID> factIds) {
        return factIds.stream()
                .map(factId -> FactEntity.builder().id(factId).build())
                .collect(Collectors.toSet());
    }
}
