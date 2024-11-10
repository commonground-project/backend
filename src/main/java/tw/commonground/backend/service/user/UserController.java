package tw.commonground.backend.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/api/user")
    public List<UserEntity> getUser() {
        return userService.getUser();
    }

    @PostMapping("/api/setup/complete")
    public ResponseEntity<?> completeSetup(
            @RequestBody UserSetupRequest setupRequest,
            @AuthenticationPrincipal OAuth2User principal) {

        String email = principal.getAttribute("email");
        Optional<UserEntity> userEntityOptional = userRepository.findAllByEmail(email);

        if (userEntityOptional.isPresent()) {
            UserEntity user = userEntityOptional.get();
            user.setUsername(setupRequest.getUsername());
            user.setNickname(setupRequest.getNickname());
            user.setEmail(setupRequest.getEmail());
            user.setProfileImage(setupRequest.getProfileImage());
            user.setRole("ROLE_USER");
            userRepository.save(user);
            return ResponseEntity.ok("User setup completed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
}
