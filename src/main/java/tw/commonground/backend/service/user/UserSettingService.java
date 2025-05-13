package tw.commonground.backend.service.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.user.dto.setting.UserSettingDto;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserSettingEntity;
import tw.commonground.backend.service.user.entity.UserSettingRepository;
import tw.commonground.backend.shared.tracing.Traced;

@Traced
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
                            .newReplyInFollowedIssue(true)
                            .newReplyInFollowedViewpoint(true)
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
                    userSettingEntity.setNewReplyInFollowedIssue(userSettingDto.getNotification()
                            .isNewReplyInFollowedIssue());
                    userSettingEntity.setNewReplyInFollowedViewpoint(userSettingDto.getNotification()
                            .isNewReplyInFollowedViewpoint());
                    userSettingRepository.save(userSettingEntity);
                }, () -> {
                    UserSettingEntity newSetting = UserSettingEntity.builder()
                            .newReplyInMyViewpoint(userSettingDto.getNotification().isNewReplyInMyViewpoint())
                            .newReferenceToMyReply(userSettingDto.getNotification().isNewReferenceToMyReply())
                            .newNodeOfTimelineToFollowedIssue(userSettingDto
                                    .getNotification().isNewNodeOfTimelineToFollowedIssue())
                            .newReplyInFollowedIssue(userSettingDto
                                    .getNotification().isNewReplyInFollowedIssue())
                            .newReplyInFollowedViewpoint(userSettingDto
                                    .getNotification().isNewReplyInFollowedViewpoint())
                            .user(user)
                            .build();
                    userSettingRepository.save(newSetting);
                });

        return userSettingRepository.findById(userId).orElseThrow();
    }
}
