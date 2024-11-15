package tw.commonground.backend.service.reference;

import org.springframework.stereotype.Component;

@Component
public class ReferenceMapper {
    public ReferenceResponse toResponse(ReferenceEntity referenceEntity) {
        return ReferenceResponse.builder()
                .createAt(referenceEntity.getCreateAt())
                .url(referenceEntity.getUrl())
                .icon(referenceEntity.getFavicon())
                .title(referenceEntity.getTitle())
                .build();
    }
}
