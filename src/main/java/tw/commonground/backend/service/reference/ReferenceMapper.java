package tw.commonground.backend.service.reference;

import com.fasterxml.jackson.annotation.JsonProperty;
import tw.commonground.backend.service.reference.dto.ReferenceResponse;
import tw.commonground.backend.service.reference.dto.ReferenceResponseForAI;
import tw.commonground.backend.shared.util.DateTimeUtils;

import java.util.UUID;

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

    public static ReferenceResponseForAI toResponseForAI(ReferenceEntity referenceEntity) {
        return new ReferenceResponseForAI(
                referenceEntity.getId(),
                DateTimeUtils.toIso8601String(referenceEntity.getCreateAt()),
                referenceEntity.getUrl(),
                referenceEntity.getFavicon(),
                referenceEntity.getTitle(),
                referenceEntity.getDescription());
    }
}


