package tw.commonground.backend.service.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.user.dto.UserMapper;
import tw.commonground.backend.service.user.dto.UserResponse;
import tw.commonground.backend.service.user.dto.UserSetupRequest;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserEntity;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("login successful");
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUser() {
        List<UserEntity> userEntities = userService.getUsers();
        List<UserResponse> response = UserMapper.toResponses(userEntities);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable @NotBlank String username) {
        FullUserEntity userEntity = userService.getUserByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User", "username", username));

        UserResponse response = UserMapper.toResponse(userEntity);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal FullUserEntity user) {
        UserResponse response = UserMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/setup")
    public ResponseEntity<UserResponse> userSetup(@Valid @RequestBody UserSetupRequest setupRequest,
                                                  @AuthenticationPrincipal FullUserEntity user) {
        FullUserEntity userEntity = userService.completeSetup(setupRequest, user.getEmail());
        UserResponse response = UserMapper.toResponse(userEntity);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/user/profile-image/{username}", produces = "image/png")
    public ResponseEntity<byte[]> getProfileImage(@Valid @NotBlank @PathVariable String username) {
        return ResponseEntity.ok(userService.getProfileImage(username));
    }
}
