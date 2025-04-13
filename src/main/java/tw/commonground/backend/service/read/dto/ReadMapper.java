package tw.commonground.backend.service.read.dto;

import tw.commonground.backend.service.read.entity.ReadEntity;

public class ReadMapper {
    private ReadMapper() {
        // hide constructor
    }

    public static ReadResponse toResponse(ReadEntity readEntity) {
        return ReadResponse.builder()
                .userId(readEntity.getUser().getId())
                .objectId(readEntity.getObjectId())
                .objectType(readEntity.getObjectType())
                .readStatus(readEntity.getReadStatus())
                .build();
    }
}
