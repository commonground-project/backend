package tw.commonground.backend.service.reference;

import tw.commonground.backend.service.reference.dto.ReferenceResponse;

public final class ReferenceMapper {

    private ReferenceMapper() {
        // hide the constructor
    }

    public static ReferenceResponse toResponse(ReferenceEntity referenceEntity) {
        return new ReferenceResponse(
                referenceEntity.getId(),
                referenceEntity.getCreateAt(),
                referenceEntity.getUrl(),
                referenceEntity.getFavicon(),
                referenceEntity.getTitle());
    }
}
