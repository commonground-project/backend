package tw.commonground.backend.service.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.user.dto.UserMapper;
import tw.commonground.backend.service.user.dto.UserResponse;
import tw.commonground.backend.service.user.dto.UserSetupRequest;
import tw.commonground.backend.service.user.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("login successful");
    }

    @GetMapping("/api/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUser() {
        List<UserEntity> userEntities = userService.getUsers();
        List<UserResponse> response = UserMapper.fromEntities(userEntities);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        Optional<UserEntity> userEntityOptional = userService.getUserById(id);
        if (userEntityOptional.isEmpty()) {
            // Todo: wait for impl rfc 7807
            return ResponseEntity.notFound().build();
        }

        UserEntity userEntity = userEntityOptional.get();
        UserResponse response = UserMapper.toResponse(userEntity);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/users/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal OAuth2User principal) {
        UserEntity userEntity = userService.getMe(principal);
        UserResponse response = UserMapper.toResponse(userEntity);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/setup")
    @PreAuthorize("hasRole('SETUP_REQUIRED')")
    public ResponseEntity<String> completeSetup(@RequestBody UserSetupRequest setupRequest, @AuthenticationPrincipal OAuth2User principal) {
        String result = userService.completeSetup(setupRequest, principal);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/api/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful");
    }
}
