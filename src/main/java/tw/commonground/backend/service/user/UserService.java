package tw.commonground.backend.service.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.exception.ValidationException;
import tw.commonground.backend.service.image.ImageService;
import tw.commonground.backend.service.user.dto.UpdateUserRequest;
import tw.commonground.backend.service.user.dto.UserInitRequest;
import tw.commonground.backend.service.user.dto.UserSetupRequest;
import tw.commonground.backend.service.user.entity.DetailUserEntity;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.security.UserRole;
import tw.commonground.backend.service.user.exception.UserAlreadySetupException;
import tw.commonground.backend.shared.tracing.Traced;
import tw.commonground.backend.shared.util.DateTimeUtils;

import java.util.List;
import java.util.Optional;

@Traced
@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final ImageService imageService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${application.admin.email:}")
    private String[] adminEmail;

    public UserService(
            UserRepository userRepository,
            ImageService imageService,
            ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.applicationEventPublisher = applicationEventPublisher;
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

        applicationEventPublisher.publishEvent(new UserCreatedEvent(userEntity));

        return userEntity;
    }

    @Secured("ROLE_ADMIN")
    public List<UserEntity> getUsers() {
        return (List<UserEntity>) userRepository.findAll();
    }

    public Optional<DetailUserEntity> getDetailedUserByUsername(String username) {
        return userRepository.findDetailUserEntityByUsername(username);
    }

    public Optional<FullUserEntity> getUserByEmail(String email) {
        return userRepository.findUserEntityByEmail(email);
    }

    public DetailUserEntity updateUser(String username, UpdateUserRequest request) {
        UserEntity userEntity = userRepository.getUserEntityByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", username));

        userEntity.setUsername(Optional.ofNullable(request.getUsername()).orElse(userEntity.getUsername()));
        userEntity.setNickname(Optional.ofNullable(request.getNickname()).orElse(userEntity.getNickname()));
        userEntity.setRole(Optional.ofNullable(request.getRole()).orElse(userEntity.getRole()));
        userEntity.setGender(request.getGender());
        userEntity.setOccupation(request.getOccupation());
        userEntity.setBirthdate(request.getBirthdate());

        userRepository.save(userEntity);

        // Use to clear hibernate second level cache before fetching and returning user
        entityManager.clear();
        return userRepository
            .findDetailUserEntityByUsername(Optional.ofNullable(request.getUsername()).orElse(username))
            .orElseThrow(() -> new EntityNotFoundException("User", "username", username));
    }

    public DetailUserEntity completeSetup(UserSetupRequest setupRequest, String email) {
        FullUserEntity fullUser = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", "email", email));

        if (fullUser.getRole() != UserRole.ROLE_NOT_SETUP) {
            // Use UserAlreadySetupException to provide more context,
            // instead of `@PreAuthorize("hasRole('SETUP_REQUIRED')")`
            throw new UserAlreadySetupException(email);
        } else {
            UserRole defaultRole = UserRole.ROLE_USER;
            if (adminEmail != null) {
                for (String admin : adminEmail) {
                    if (admin.equals(email)) {
                        defaultRole = UserRole.ROLE_ADMIN;
                        break;
                    }
                }
            }

            if (userRepository.existsByUsername(setupRequest.getUsername())) {
                throw new ValidationException("Username already exists");
            }

            userRepository.setupUserById(fullUser.getId(),
                    setupRequest.getUsername(),
                    setupRequest.getNickname(),
                    defaultRole);

            // Setup user information for AI recommendation system
            userRepository.setupUserInformationById(fullUser.getId(),
                    setupRequest.getBirthdate(),
                    setupRequest.getOccupation(),
                    setupRequest.getGender());

            // Use to clear hibernate second level cache before fetching and returning user
            entityManager.clear();
            return userRepository.findDetailUserEntityByEmail(fullUser.getEmail()).orElseThrow();
        }
    }

    public FullUserEntity getMe(String email) {
        Optional<FullUserEntity> userEntityOptional = userRepository.findUserEntityByEmail(email);
        return userEntityOptional.orElseThrow(() -> new EntityNotFoundException("User", "email", email));
    }

    public byte[] getProfileImage(String username) {
        return userRepository.getUserEntityByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User", "email", username)
        ).getProfileImage();
    }

    public void throwIfUserNotExist(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", "id", userId.toString());
        }
    }
}
