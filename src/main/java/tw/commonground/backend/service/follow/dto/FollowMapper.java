package tw.commonground.backend.service.follow.dto;

import tw.commonground.backend.service.follow.entity.FollowEntity;
import tw.commonground.backend.shared.util.DateTimeUtils;

public final class FollowMapper {

    private FollowMapper() {
        // hide the constructor
    }

    public static FollowResponse toFollowResponse(FollowEntity entity) {
        return FollowResponse.builder()
                .follow(entity.getFollow())
                .updatedAt(DateTimeUtils.toIso8601String(entity.getUpdatedAt()))
                .build();
    }
}
