package tw.commonground.backend.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tw.commonground.backend.service.image.ImageService;
import tw.commonground.backend.service.user.dto.UserInitRequest;
import tw.commonground.backend.service.user.dto.UserSetupRequest;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.user.entity.UserRole;
import tw.commonground.backend.service.user.exception.EmailNotFoundException;

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
                .username(userInitRequest.getUsername())
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

//    public UserResponse loadUserByEmail(String email) throws EmailNotFoundException {
//        Optional<UserEntity> user = userRepository.findByEmail(email);
//        if (user.isPresent()) {
//            UserEntity userEntity = user.get();
//            return new UserResponse(
//                    userEntity.getUsername(),
//                    userEntity.getNickname(),
//                    userEntity.getEmail(),
//                    userEntity.getProfileImage(),
//                    userEntity.getRole()
//            );
//        } else {
//            throw new EmailNotFoundException(email);
//        }
//    }
//
//    public UserResponse loadUserById(Long id) throws IdNotFoundException {
//        Optional<UserEntity> user = userRepository.findById(id);
//        if (user.isPresent()) {
//            UserEntity userEntity = user.get();
//            return new UserResponse(
//                    userEntity.getUsername(),
//                    userEntity.getNickname(),
//                    userEntity.getEmail(),
//                    userEntity.getProfileImage(),
//                    userEntity.getRole()
//            );
//        } else {
//            throw new IdNotFoundException(id);
//        }
//    }

    @Secured("ROLE_ADMIN")
    public List<UserEntity> getUsers() {
        return (List<UserEntity>) userRepository.findAll();
    }

    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<UserEntity> getUserIdByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String completeSetup(UserSetupRequest setupRequest, OAuth2User principal) {
        String email = principal.getAttribute("email");
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);

        if (userEntityOptional.isPresent()) {
            UserEntity user = userEntityOptional.get();
            user.setUsername(setupRequest.getUsername());
            user.setNickname(setupRequest.getNickname());
            user.setRole(UserRole.ROLE_USER);
            userRepository.save(user);

            return "User setup completed successfully.";
        } else {
            throw new EmailNotFoundException(email);
        }
    }

    public UserEntity getMe(OAuth2User principal) {
        String email = principal.getAttribute("email");
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);

        if (userEntityOptional.isPresent()) {
            return userEntityOptional.get();
        } else {
            throw new EmailNotFoundException(email);
        }
    }
}
