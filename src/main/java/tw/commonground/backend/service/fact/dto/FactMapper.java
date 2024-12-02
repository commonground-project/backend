package tw.commonground.backend.service.fact.dto;

import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.reference.ReferenceMapper;

public final class FactMapper {

    private FactMapper() {
        // hide the constructor
    }

    public static FactResponse toResponse(FactEntity factEntity) {
        return FactResponse.builder()
                .id(factEntity.getId())
                .title(factEntity.getTitle())
                .createAt(factEntity.getCreateAt())
                .updateAt(factEntity.getUpdatedAt())
                .authorId(factEntity.getAuthorId())
                .authorName(factEntity.getAuthorName())
                .references(factEntity.getReferences().stream().map(ReferenceMapper::toResponse).toList())
                .build();
    }

}
