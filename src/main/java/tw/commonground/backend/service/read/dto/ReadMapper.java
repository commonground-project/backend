package tw.commonground.backend.service.read.dto;

import tw.commonground.backend.service.read.entity.ReadEntity;

import java.util.UUID;

public final class ReadMapper {
    private ReadMapper() {
        // hide constructor
    }

    public static ReadResponse toResponse(ReadEntity readEntity) {
        return ReadResponse.builder()
                .userId(readEntity.getUser().getUuid())
                .objectId(readEntity.getObjectId())
                .readStatus(readEntity.getReadStatus())
                .updatedAt(readEntity.getTimestamp().toString())
                .build();
    }

    public static SimpleReadResponse toSimpleResponse(UUID userId, UUID objectId, Boolean readStatus) {
        return SimpleReadResponse.builder()
                .userId(userId)
                .objectId(objectId)
                .readStatus(readStatus)
                .build();
    }
}
