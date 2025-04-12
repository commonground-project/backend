package tw.commonground.backend.service.internal.reference.dto;

import tw.commonground.backend.service.reference.ReferenceEntity;
import tw.commonground.backend.shared.util.DateTimeUtils;

public class InternalReferenceMapper {
    public static InternalDetailReferenceResponse toInternalDetailResponse(ReferenceEntity referenceEntity) {
        return new InternalDetailReferenceResponse(
                referenceEntity.getId(),
                DateTimeUtils.toIso8601String(referenceEntity.getCreateAt()),
                referenceEntity.getUrl(),
                referenceEntity.getFavicon(),
                referenceEntity.getTitle(),
                referenceEntity.getDescription());
    }
}
