package tw.commonground.backend.service.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSettingDto {
    private boolean newReplyInMyViewpoint;

    private boolean newReferenceToMyReply;
}
