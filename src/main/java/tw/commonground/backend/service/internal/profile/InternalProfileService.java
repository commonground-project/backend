package tw.commonground.backend.service.internal.profile;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import tw.commonground.backend.service.internal.profile.dto.InternalProfileMapper;
import tw.commonground.backend.service.internal.profile.dto.InternalProfileResponse;
import tw.commonground.backend.service.internal.profile.entity.InternalProfileEntity;
import tw.commonground.backend.service.internal.profile.entity.InternalProfileRepository;
import tw.commonground.backend.service.user.UserCreatedEvent;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InternalProfileService {
    private final UserRepository userRepository;
    private final InternalProfileRepository internalProfileRepository;

    public InternalProfileService(UserRepository userRepository, InternalProfileRepository internalProfileRepository) {
        this.userRepository = userRepository;
        this.internalProfileRepository = internalProfileRepository;
    }

    public List<InternalProfileResponse> getProfiles() {
        List<InternalProfileEntity> internalProfileEntities = internalProfileRepository.findAll();
        return InternalProfileMapper.toResponses(internalProfileEntities);
    }

    public InternalProfileResponse getProfile(UUID userUuid) {
        Long userId = userRepository.getIdByUid(userUuid);
        InternalProfileEntity internalProfileEntity =  internalProfileRepository.findById(userId)
                .orElseGet(() -> {
                    InternalProfileEntity newProfileEntity = InternalProfileEntity.builder()
                            .uuid(userUuid)
                            .gender("")
                            .occupation("")
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

    public void createProfile(UserEntity userEntity) {
        if (userEntity == null || userEntity.getId() == null) {
            throw new IllegalArgumentException("UserEntity cannot be null and must have an ID before creating a profile.");
        }

        InternalProfileEntity newProfile = InternalProfileEntity.builder()
                .id(userEntity.getId())
                .uuid(userEntity.getUuid())
                .user(userEntity)
                .gender("")
                .occupation("")
                .location("")
                .browsingTags(List.of())
                .searchKeywords(List.of())
                .createdAt(LocalDateTime.now())
                .lastActiveAt(LocalDateTime.now())
                .activityFrequency(List.of())
                .userTopIp(List.of())
                .build();

        internalProfileRepository.save(newProfile);
    }

    @EventListener
    public void onUserCreatedEvent(UserCreatedEvent userEvent) {
        UserEntity userEntity = userEvent.getUserEntity();

        if (userEntity == null || userEntity.getId() == null) {
            throw new IllegalStateException("UserEntity is null or does not have an ID when creating profile.");
        }

        internalProfileRepository.findById(userEntity.getId()).ifPresentOrElse(
                profile -> {},
                () -> createProfile(userEntity)
        );
    }

    @EventListener
    public void onApplicationStart(ContextRefreshedEvent event) {
        List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
        for (UserEntity user : users) {
            internalProfileRepository.findById(user.getId()).ifPresentOrElse(
                    profile -> {},
                    () -> createProfile(user)
            );
        }
    }
}
