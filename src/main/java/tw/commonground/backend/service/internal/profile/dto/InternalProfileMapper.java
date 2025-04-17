package tw.commonground.backend.service.internal.profile.dto;

import tw.commonground.backend.service.internal.profile.entity.InternalProfileEntity;
import tw.commonground.backend.shared.util.DateTimeUtils;

import java.util.List;

public final class InternalProfileMapper {

    private InternalProfileMapper() {
        // hide constructor
    }

    public static InternalProfileResponse toResponse(InternalProfileEntity internalProfileEntity) {
        return InternalProfileResponse.builder()
                .userUuid(internalProfileEntity.getUuid())
                .gender(internalProfileEntity.getUser().getGender().toString())
                .occupation(internalProfileEntity.getUser().getOccupation().toString())
                .birthdate(internalProfileEntity.getUser().getBirthdate().toString())
                .location(internalProfileEntity.getLocation())
                .browsingTags(internalProfileEntity.getBrowsingTags())
                .searchKeywords(internalProfileEntity.getSearchKeywords())
                .createdAt(DateTimeUtils.toIso8601String(internalProfileEntity.getCreatedAt()))
                .lastActiveAt(DateTimeUtils.toIso8601String(internalProfileEntity.getLastActiveAt()))
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
