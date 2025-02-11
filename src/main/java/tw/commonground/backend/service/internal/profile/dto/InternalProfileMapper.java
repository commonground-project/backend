package tw.commonground.backend.service.internal.profile.dto;

import tw.commonground.backend.service.user.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public final class InternalProfileMapper {

    private InternalProfileMapper() {
        // hide constructor
    }

    public static InternalProfileResponse toResponseFromId(UUID userId) {
        return InternalProfileResponse.builder()
                .userId(userId)
                .build();
    }

    public static InternalProfileResponse toResponse(UserEntity user) {
        return InternalProfileResponse.builder()
                .userId(user.getUuid())
                .build();
    }

    public static List<InternalProfileResponse> toResponses(List<UserEntity> users) {
        return users.stream()
                .map(InternalProfileMapper::toResponse)
                .toList();
    }
}
