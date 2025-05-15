package tw.commonground.backend.service.read.dto;

import tw.commonground.backend.service.read.entity.ReadEntity;

import java.util.UUID;

public final class ReadMapper {
    private ReadMapper() {
        // hide constructor
    }

    public static ReadResponse toResponse(ReadEntity readEntity) {
        return ReadResponse.builder()
                .userId(readEntity.getUser().getId())
                .objectId(readEntity.getObjectId())
                .readStatus(readEntity.getReadStatus())
                .build();
    }

    public static ReadResponse toResponse(Long userId, UUID objectId, Boolean readStatus) {
        return ReadResponse.builder()
                .userId(userId)
                .objectId(objectId)
                .readStatus(readStatus)
                .build();
    }
}
