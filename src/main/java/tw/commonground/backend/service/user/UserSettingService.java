package tw.commonground.backend.service.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.user.dto.UserSettingDto;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserSettingEntity;
import tw.commonground.backend.service.user.entity.UserSettingRepository;

@Service
public class UserSettingService {

    private final UserSettingRepository userSettingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public UserSettingService(UserSettingRepository userSettingRepository) {
        this.userSettingRepository = userSettingRepository;
    }

    public UserSettingEntity getUserSetting(Long userId) {
        UserEntity user = entityManager.getReference(UserEntity.class, userId);

        return userSettingRepository.findById(userId)
                .orElseGet(() -> {
                    UserSettingEntity newSetting = UserSettingEntity.builder()
                            .newReplyInMyViewpoint(true)
                            .newReferenceToMyReply(true)
                            .user(user)
                            .build();
                    return userSettingRepository.save(newSetting);
                });
    }

    public UserSettingEntity updateUserSetting(Long userId, UserSettingDto userSettingDto) {
        UserEntity user = entityManager.getReference(UserEntity.class, userId);

        userSettingRepository.findById(userId)
                .ifPresentOrElse(userSettingEntity -> {
                    userSettingEntity.setNewReplyInMyViewpoint(userSettingDto.isNewReplyInMyViewpoint());
                    userSettingEntity.setNewReferenceToMyReply(userSettingDto.isNewReferenceToMyReply());
                    userSettingRepository.save(userSettingEntity);
                }, () -> {
                    UserSettingEntity newSetting = UserSettingEntity.builder()
                            .newReplyInMyViewpoint(userSettingDto.isNewReplyInMyViewpoint())
                            .newReferenceToMyReply(userSettingDto.isNewReferenceToMyReply())
                            .user(user)
                            .build();
                    userSettingRepository.save(newSetting);
                });

        return userSettingRepository.findById(userId).orElseThrow();
    }
}
