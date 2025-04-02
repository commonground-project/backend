package tw.commonground.backend.service.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.exception.EntityNotFoundException;
import tw.commonground.backend.service.internal.account.TokenUserDetails;
import tw.commonground.backend.service.internal.account.exception.ServiceAccountUnsupportedOperationException;
import tw.commonground.backend.service.user.dto.UpdateUserRequest;
import tw.commonground.backend.service.user.dto.UserMapper;
import tw.commonground.backend.service.user.dto.UserResponse;
import tw.commonground.backend.service.user.dto.UserSetupRequest;
import tw.commonground.backend.service.user.entity.DetailUserEntity;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.List;

@Traced
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable @NotBlank String username) {
        DetailUserEntity userEntity = userService.getDetailedUserByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User", "username", username));

        UserResponse response = UserMapper.toResponse(userEntity);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable @NotBlank String username,
                                                   @Valid @RequestBody UpdateUserRequest updateRequest) {
        DetailUserEntity userEntity = userService.updateUser(username, updateRequest);
        UserResponse response = UserMapper.toResponse(userEntity);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal FullUserEntity user) {
        if (user instanceof TokenUserDetails) {
            throw new ServiceAccountUnsupportedOperationException("/user/me");
        }

        DetailUserEntity userEntity = userService.getDetailedUserByUsername(user.getUsername()).orElseThrow(
                () -> new EntityNotFoundException("User", "username", user.getUsername()));

        UserResponse response = UserMapper.toResponse(userEntity);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/setup")
    public ResponseEntity<UserResponse> userSetup(@Valid @RequestBody UserSetupRequest setupRequest,
                                                  @AuthenticationPrincipal FullUserEntity user) {
        DetailUserEntity userEntity = userService.completeSetup(setupRequest, user.getEmail());
        UserResponse response = UserMapper.toResponse(userEntity);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/user/avatar/{username}", produces = "image/png")
    public ResponseEntity<byte[]> getProfileImage(@Valid @NotBlank @PathVariable String username) {
        return ResponseEntity.ok(userService.getProfileImage(username));
    }
}
