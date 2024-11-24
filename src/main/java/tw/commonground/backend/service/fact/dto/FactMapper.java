package tw.commonground.backend.service.fact.dto;

import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.reference.ReferenceMapper;

public class FactMapper {

    private final ReferenceMapper referenceMapper = new ReferenceMapper();

    public FactResponse toResponse(FactEntity factEntity) {
        return FactResponse.builder()
                .id(factEntity.getId())
                .title(factEntity.getTitle())
                .createAt(factEntity.getCreateAt())
                .updateAt(factEntity.getUpdatedAt())
                .authorId(factEntity.getAuthorId())
                .authorName(factEntity.getAuthorName())
                .references(factEntity.getReferences().stream().map(referenceMapper::toResponse).toList())
                .build();
    }

}
