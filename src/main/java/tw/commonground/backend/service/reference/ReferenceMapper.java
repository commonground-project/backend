package tw.commonground.backend.service.reference;

import tw.commonground.backend.service.reference.dto.ReferenceResponse;
import tw.commonground.backend.shared.util.DateTimeUtils;

public final class ReferenceMapper {

    private ReferenceMapper() {
        // hide the constructor
    }

    public static ReferenceResponse toResponse(ReferenceEntity referenceEntity) {
        return new ReferenceResponse(
                referenceEntity.getId(),
                DateTimeUtils.toIso8601String(referenceEntity.getCreateAt()),
                referenceEntity.getUrl(),
                referenceEntity.getFavicon(),
                referenceEntity.getTitle());
    }
}
