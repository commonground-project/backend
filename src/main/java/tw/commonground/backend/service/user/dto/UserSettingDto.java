package tw.commonground.backend.service.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSettingDto {

    @NotNull
    private boolean newReplyInMyViewpoint;

    @NotNull
    private boolean newReferenceToMyReply;
}
