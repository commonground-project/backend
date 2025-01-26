package tw.commonground.backend.service.user.dto;

import tw.commonground.backend.service.user.entity.UserSettingEntity;

public final class UserSettingMapper {
    private UserSettingMapper() {
        // hide the constructor
    }

    public static UserSettingDto toDto(UserSettingEntity entity) {
        return UserSettingDto.builder()
                .newReplyInMyViewpoint(entity.getNewReplyInMyViewpoint())
                .newReferenceToMyReply(entity.getNewReferenceToMyReply())
                .build();
    }
}
