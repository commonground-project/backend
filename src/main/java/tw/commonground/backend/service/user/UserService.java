package tw.commonground.backend.service.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tw.commonground.backend.service.image.ImageService;
import tw.commonground.backend.service.user.dto.UserInitRequest;
import tw.commonground.backend.service.user.dto.UserSetupRequest;
import tw.commonground.backend.service.user.entity.FullUserEntity;
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

    @PersistenceContext
    private EntityManager entityManager;

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

    public Optional<FullUserEntity> getUserById(Long id) {
        return userRepository.findUserEntityById(id);
    }

    public Optional<FullUserEntity> getUserByEmail(String email) {
        return userRepository.findUserEntityByEmail(email);
    }

    public FullUserEntity completeSetup(UserSetupRequest setupRequest, String email) {
        // Todo: wait for impl rfc 7807, should throw generic not found exception
        FullUserEntity fullUser = userRepository.findUserEntityByEmail(email)
                .orElseThrow();

        if (fullUser.getRole() != UserRole.ROLE_NOT_SETUP) {
            // Use UserAlreadySetupException to provide more context,
            // instead of `@PreAuthorize("hasRole('SETUP_REQUIRED')")`
            // Todo: will response internal server error before impl rfc 7807
            throw new UserAlreadySetupException(email);
        } else {
            userRepository.setupUserById(fullUser.getId(),
                    setupRequest.getUsername(),
                    setupRequest.getNickname(),
                    UserRole.ROLE_USER);

            // Use to clear hibernate second level cache before fetching and returning user
            entityManager.clear();
            return userRepository.findUserEntityByEmail(email).orElseThrow();
        }
    }

    public FullUserEntity getMe(String email) {
        Optional<FullUserEntity> userEntityOptional = userRepository.findUserEntityByEmail(email);
        return userEntityOptional.orElseThrow(() -> new EmailNotFoundException(email));
    }
}
