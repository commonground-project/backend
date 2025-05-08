package tw.commonground.backend.service.newcontent.dto;

import tw.commonground.backend.service.newcontent.entity.NewcontentEntity;

public final class NewcontentMapper {
    private NewcontentMapper() {
        // hide constructor
    }

    public static NewcontentResponse toResponse(NewcontentEntity newcontentEntity) {
        return NewcontentResponse.builder()
                .userId(newcontentEntity.getUser().getId())
                .objectId(newcontentEntity.getObjectId())
                .newcontentStatus(newcontentEntity.getNewcontentStatus())
                .build();
    }
}
