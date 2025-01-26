package tw.commonground.backend.service.user;

import org.springframework.stereotype.Service;
import tw.commonground.backend.service.user.dto.UserSettingDto;
import tw.commonground.backend.service.user.entity.UserSettingEntity;
import tw.commonground.backend.service.user.entity.UserSettingRepository;

@Service
public class UserSettingService {

    private final UserSettingRepository userSettingRepository;

    public UserSettingService(UserSettingRepository userSettingRepository) {
        this.userSettingRepository = userSettingRepository;
    }

    public UserSettingEntity getUserSetting(Long userId) {
        return userSettingRepository.findById(userId)
                .orElseGet(() -> {
                    UserSettingEntity newSetting = UserSettingEntity.builder()
                            .id(userId)
                            .newReplyInMyViewpoint(true)
                            .newReferenceToMyReply(true)
                            .build();
                    return userSettingRepository.save(newSetting);
                });
    }

    public UserSettingEntity updateUserSetting(Long userId, UserSettingDto userSettingDto) {
        userSettingRepository.findById(userId)
                .ifPresentOrElse(userSettingEntity -> {
                    userSettingEntity.setNewReplyInMyViewpoint(userSettingDto.isNewReplyInMyViewpoint());
                    userSettingEntity.setNewReferenceToMyReply(userSettingDto.isNewReferenceToMyReply());
                    userSettingRepository.save(userSettingEntity);
                }, () -> {
                    UserSettingEntity newSetting = UserSettingEntity.builder()
                            .id(userId)
                            .newReplyInMyViewpoint(userSettingDto.isNewReplyInMyViewpoint())
                            .newReferenceToMyReply(userSettingDto.isNewReferenceToMyReply())
                            .build();
                    userSettingRepository.save(newSetting);
                });

        return userSettingRepository.findById(userId).orElseThrow();
    }
}
