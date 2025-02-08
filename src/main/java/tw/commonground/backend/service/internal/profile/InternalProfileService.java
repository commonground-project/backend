package tw.commonground.backend.service.internal.profile;

import org.springframework.stereotype.Service;

import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.internal.profile.dto.InternalProfileMapper;
import tw.commonground.backend.service.internal.profile.dto.InternalProfileResponse;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class InternalProfileService {
    private final UserRepository userRepository;

    public InternalProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<InternalProfileResponse> getProfiles() {
        List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
        return InternalProfileMapper.toResponses(users);
    }

    public InternalProfileResponse getProfile(UUID userId) {
        UserEntity user = (UserEntity) userRepository.findUserEntityByUuid(userId)
                .orElseThrow(() -> new EntityNotFoundException("InternalProfile", "user id", userId.toString()));

        return InternalProfileMapper.toResponse(user);
    }
}
