package tw.commonground.backend.service.user.dto.setting;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingNotificationDto {

    @NotNull
    private boolean newReplyInMyViewpoint;

    @NotNull
    private boolean newReferenceToMyReply;

    @NotNull
    private boolean newNodeOfTimelineToFollowedIssue;

    @NotNull
    private boolean newReplyInFollowedIssue;

    @NotNull
    private boolean newReplyInFollowedViewpoint;
}
