package tw.commonground.backend.service.internal.profile;

import jakarta.transaction.Transactional;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import tw.commonground.backend.service.internal.profile.dto.InternalProfileMapper;
import tw.commonground.backend.service.internal.profile.dto.InternalProfileResponse;
import tw.commonground.backend.service.internal.profile.entity.InternalProfileEntity;
import tw.commonground.backend.service.internal.profile.entity.InternalProfileRepository;
import tw.commonground.backend.shared.event.user.UserCreatedEvent;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.shared.tracing.Traced;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Traced
@Service
public class InternalProfileService {
    private final UserRepository userRepository;

    private final InternalProfileRepository internalProfileRepository;

    public InternalProfileService(UserRepository userRepository, InternalProfileRepository internalProfileRepository) {
        this.userRepository = userRepository;
        this.internalProfileRepository = internalProfileRepository;
    }

    public List<InternalProfileResponse> getProfiles() {
        List<InternalProfileEntity> profileEntities = internalProfileRepository.findAll();
        return InternalProfileMapper.toResponses(profileEntities);
    }

    public InternalProfileResponse getProfile(UUID userUuid) {
        Long userId = userRepository.getIdByUid(userUuid);
        InternalProfileEntity internalProfileEntity =  internalProfileRepository.findById(userId)
                .orElseGet(() -> {
                    InternalProfileEntity newProfileEntity = InternalProfileEntity.builder()
                            .uuid(userUuid)
                            .location("")
                            .browsingTags(List.of())
                            .searchKeywords(List.of())
                            .createdAt(LocalDateTime.now())
                            .lastActiveAt(LocalDateTime.now())
                            .activityFrequency(List.of())
                            .userTopIp(List.of())
                            .build();
                    return internalProfileRepository.save(newProfileEntity);
                });
        return InternalProfileMapper.toResponse(internalProfileEntity);
    }

    @Transactional
    public void createProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        InternalProfileEntity newProfile = InternalProfileEntity.builder()
                .uuid(user.getUuid())
                .user(user)
                .location("")
                .browsingTags(List.of())
                .searchKeywords(List.of())
                .activityFrequency(List.of())
                .userTopIp(List.of())
                .build();

        internalProfileRepository.save(newProfile);
    }

    @EventListener
    @Transactional
    public void onUserCreatedEvent(UserCreatedEvent userEvent) {
        UserEntity userEntity = userEvent.getUserEntity();

        internalProfileRepository.findById(userEntity.getId()).ifPresentOrElse(
                profile -> { },
                () -> createProfile(userEntity.getId())
        );
    }

    @EventListener
    @Transactional
    public void onApplicationStart(ContextRefreshedEvent event) {
        long userCount = userRepository.count();
        long profileCount = internalProfileRepository.count();

        if (userCount == profileCount) {
            return;
        }

        List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
        for (UserEntity user : users) {
            internalProfileRepository.findById(user.getId()).ifPresentOrElse(
                    profile -> { },
                    () -> createProfile(user.getId())
            );
        }
    }
}
