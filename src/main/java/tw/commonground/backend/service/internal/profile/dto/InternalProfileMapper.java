package tw.commonground.backend.service.internal.profile.dto;

import tw.commonground.backend.service.internal.profile.entity.InternalProfileEntity;

import java.util.List;

public final class InternalProfileMapper {

    private InternalProfileMapper() {
        // hide constructor
    }

    public static InternalProfileResponse toResponse(InternalProfileEntity internalProfileEntity) {
        return InternalProfileResponse.builder()
                .userUuid(internalProfileEntity.getUuid())
                .gender(internalProfileEntity.getGender())
                .occupation(internalProfileEntity.getOccupation())
                .location(internalProfileEntity.getLocation())
                .browsingTags(internalProfileEntity.getBrowsingTags())
                .searchKeywords(internalProfileEntity.getSearchKeywords())
                .createdAt(internalProfileEntity.getCreatedAt())
                .lastActiveAt(internalProfileEntity.getLastActiveAt())
                .activityFrequency(internalProfileEntity.getActivityFrequency())
                .userTopIp(internalProfileEntity.getUserTopIp())
                .build();
    }

    public static List<InternalProfileResponse> toResponses(List<InternalProfileEntity> internalProfileEntities) {
        return internalProfileEntities.stream()
                .map(InternalProfileMapper::toResponse)
                .toList();
    }
}
