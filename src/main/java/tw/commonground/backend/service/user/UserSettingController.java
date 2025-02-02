package tw.commonground.backend.service.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.user.dto.setting.UserSettingDto;
import tw.commonground.backend.service.user.dto.setting.UserSettingMapper;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserSettingEntity;

@RestController
@RequestMapping("/api")
public class UserSettingController {

    private final UserSettingService userSettingService;

    public UserSettingController(UserSettingService userSettingService) {
        this.userSettingService = userSettingService;
    }

    @GetMapping("/user/settings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserSettingDto> getUserSetting(@AuthenticationPrincipal FullUserEntity user) {
        UserSettingEntity userSetting = userSettingService.getUserSetting(user.getId());
        UserSettingDto userSettingDto = UserSettingMapper.toDto(userSetting);
        return ResponseEntity.ok(userSettingDto);
    }

    @PutMapping("/user/settings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserSettingDto> updateUserSetting(@AuthenticationPrincipal FullUserEntity user,
                                                            @RequestBody @Valid UserSettingDto userSettingDto) {
        UserSettingEntity updatedUserSetting = userSettingService.updateUserSetting(user.getId(), userSettingDto);
        UserSettingDto updatedUserSettingDto = UserSettingMapper.toDto(updatedUserSetting);
        return ResponseEntity.ok(updatedUserSettingDto);
    }
}
