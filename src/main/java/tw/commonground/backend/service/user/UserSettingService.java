package tw.commonground.backend.service.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.user.dto.setting.UserSettingDto;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserSettingEntity;
import tw.commonground.backend.service.user.entity.UserSettingRepository;

@Service
public class UserSettingService {

    private final UserSettingRepository userSettingRepository;
    private final UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    public UserSettingService(UserSettingRepository userSettingRepository, UserService userService) {
        this.userSettingRepository = userSettingRepository;
        this.userService = userService;
    }

    public UserSettingEntity getUserSetting(Long userId) {
        userService.throwIfUserNotExist(userId);
        UserEntity user = entityManager.getReference(UserEntity.class, userId);

        return userSettingRepository.findById(userId)
                .orElseGet(() -> {
                    UserSettingEntity newSetting = UserSettingEntity.builder()
                            .newReplyInMyViewpoint(true)
                            .newReferenceToMyReply(true)
                            .newNodeOfTimelineToFollowedIssue(true)
                            .user(user)
                            .build();
                    return userSettingRepository.save(newSetting);
                });
    }

    public UserSettingEntity updateUserSetting(Long userId, UserSettingDto userSettingDto) {
        userService.throwIfUserNotExist(userId);
        UserEntity user = entityManager.getReference(UserEntity.class, userId);

        userSettingRepository.findById(userId)
                .ifPresentOrElse(userSettingEntity -> {
                    userSettingEntity.setNewReplyInMyViewpoint(userSettingDto.getNotification()
                            .isNewReplyInMyViewpoint());
                    userSettingEntity.setNewReferenceToMyReply(userSettingDto.getNotification()
                            .isNewReferenceToMyReply());
                    userSettingEntity.setNewNodeOfTimelineToFollowedIssue(userSettingDto.getNotification()
                            .isNewNodeOfTimelineToFollowedIssue());
                    userSettingRepository.save(userSettingEntity);
                }, () -> {
                    UserSettingEntity newSetting = UserSettingEntity.builder()
                            .newReplyInMyViewpoint(userSettingDto.getNotification().isNewReplyInMyViewpoint())
                            .newReferenceToMyReply(userSettingDto.getNotification().isNewReferenceToMyReply())
                            .newNodeOfTimelineToFollowedIssue(userSettingDto
                                    .getNotification().isNewNodeOfTimelineToFollowedIssue())
                            .user(user)
                            .build();
                    userSettingRepository.save(newSetting);
                });

        return userSettingRepository.findById(userId).orElseThrow();
    }
}
