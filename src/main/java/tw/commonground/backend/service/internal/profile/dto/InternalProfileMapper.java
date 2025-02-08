package tw.commonground.backend.service.internal.profile.dto;

import tw.commonground.backend.service.user.entity.UserEntity;

import java.util.List;

public final class InternalProfileMapper {

    private InternalProfileMapper() {
        // hide constructor
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
