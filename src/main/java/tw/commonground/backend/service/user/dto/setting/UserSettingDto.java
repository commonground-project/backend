package tw.commonground.backend.service.user.dto.setting;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingDto {
    private UserSettingNotificationDto notification;
}
