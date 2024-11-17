package tw.commonground.backend.service.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal DefaultOAuth2User user) {
        UserEntity userEntity = userService.getMe(user.getName());
        UserResponse response = UserMapper.toResponse(userEntity);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/setup")
    public ResponseEntity<UserResponse> userSetup(@Valid @RequestBody UserSetupRequest setupRequest, @AuthenticationPrincipal DefaultOAuth2User user) {
        UserEntity userEntity = userService.completeSetup(setupRequest, user.getName());
        UserResponse response = UserMapper.toResponse(userEntity);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful");
    }
}
