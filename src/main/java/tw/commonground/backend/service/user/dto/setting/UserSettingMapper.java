package tw.commonground.backend.service.user.dto.setting;

import tw.commonground.backend.service.user.entity.UserSettingEntity;

public final class UserSettingMapper {
    private UserSettingMapper() {
        // hide the constructor
    }

    public static UserSettingDto toDto(UserSettingEntity entity) {
        return UserSettingDto.builder()
                .notification(UserSettingNotificationDto.builder()
                        .newReplyInMyViewpoint(entity.getNewReplyInMyViewpoint())
                        .newReferenceToMyReply(entity.getNewReferenceToMyReply())
                        .newNodeOfTimelineToFollowedIssue(entity.getNewNodeOfTimelineToFollowedIssue())
                        .newReplyInFollowedIssue(entity.getNewReplyInFollowedIssue())
                        .newReplyInFollowedViewpoint(entity.getNewReplyInFollowedViewpoint())
                        .build())
                .build();
    }
}
