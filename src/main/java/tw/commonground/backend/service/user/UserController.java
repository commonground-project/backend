package tw.commonground.backend.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.user.dto.UserResponse;
import tw.commonground.backend.service.user.dto.UserSetupRequest;
import tw.commonground.backend.service.user.entity.UserEntity;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/api")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("login successful");
    }

    @GetMapping("/api/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUser() {
        List<UserEntity> userEntities = userService.getUser();
        List<UserResponse> response = UserResponse.fromEntities(userEntities);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserEntity userEntity = userService.getUserById(id);
        UserResponse response = new UserResponse(
                userEntity.getUsername(),
                userEntity.getNickname(),
                userEntity.getEmail(),
                userEntity.getProfileImage(),
                userEntity.getRole()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/users/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal OAuth2User principal) {
        UserEntity userEntity = userService.getMe(principal);
        UserResponse response = new UserResponse(
                userEntity.getUsername(),
                userEntity.getNickname(),
                userEntity.getEmail(),
                userEntity.getProfileImage(),
                userEntity.getRole()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/setup")
    @PreAuthorize("hasRole('SET_UP_REQUIRED')")
    public ResponseEntity<?> completeSetup(@RequestBody UserSetupRequest setupRequest, @AuthenticationPrincipal OAuth2User principal) {
        String result = userService.completeSetup(setupRequest, principal);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/api/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Logout successful");
    }


    @GetMapping("/api/auth/google/url")
    public ResponseEntity<String> getGoogleAuthUrl() {
        return ResponseEntity.ok("https://accounts.google.com/o/oauth2/auth?client_id=...&redirect_uri=...");
    }


}
