package tw.commonground.backend.service.fact;

import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public FactResponse toResponse(FactEntity factEntity) {
        if (factEntity == null) { return null; }

        return FactResponse.builder()
                .id(factEntity.getId())
                .title(factEntity.getTitle())
                .createAt(factEntity.getCreateAt())
                .updateAt(factEntity.getUpdatedAt())
                .authorId(factEntity.getAuthorId())
                .authorName(factEntity.getAuthorName())
                .build();
    }

}
