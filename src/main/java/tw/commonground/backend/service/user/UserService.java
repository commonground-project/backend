package tw.commonground.backend.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tw.commonground.backend.service.image.ImageService;
import tw.commonground.backend.service.user.dto.UserInitRequest;
import tw.commonground.backend.service.user.dto.UserSetupRequest;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.user.entity.UserRole;
import tw.commonground.backend.service.user.exception.EmailNotFoundException;
import tw.commonground.backend.service.user.exception.UserAlreadySetupException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final ImageService imageService;

    public UserService(UserRepository userRepository, ImageService imageService) {
        this.userRepository = userRepository;
        this.imageService = imageService;
    }

    public UserEntity createUser(UserInitRequest userInitRequest, UserRole role) {
        UserEntity userEntity = UserEntity.builder()
                .email(userInitRequest.getEmail())
                .role(role)
                .build();

        userInitRequest.getProfileImageUrl().ifPresentOrElse(profileImageUrl -> {
            Mono<byte[]> imageMono = imageService.fetchImage(profileImageUrl.toString());
            byte[] image = imageMono.block();
            userEntity.setProfileImage(image);
        }, () -> logger.info("No profile image url provided for user {}", userInitRequest.getEmail()));

        userRepository.save(userEntity);
        return userEntity;
    }

    @Secured("ROLE_ADMIN")
    public List<UserEntity> getUsers() {
        return (List<UserEntity>) userRepository.findAll();
    }

    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<UserEntity> getUserIdByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserEntity completeSetup(UserSetupRequest setupRequest, String email) {
        // Todo: wait for impl rfc 7807, should throw generic not found exception
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow();

        if (userEntity.getRole() != UserRole.ROLE_NOT_SETUP) {
            // Use UserAlreadySetupException to provide more context,
            // instead of `@PreAuthorize("hasRole('SETUP_REQUIRED')")`
            // Todo: will response internal server error before impl rfc 7807
            throw new UserAlreadySetupException(email);
        } else {
            userEntity.setRole(UserRole.ROLE_USER);
            userEntity.setUsername(setupRequest.getUsername());
            userEntity.setNickname(setupRequest.getNickname());
            return userRepository.save(userEntity);
        }
    }

    public UserEntity getMe(String email) {
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);

        if (userEntityOptional.isPresent()) {
            return userEntityOptional.get();
        } else {
            throw new EmailNotFoundException(email);
        }
    }
}
