package tw.commonground.backend.service.user;

import ch.qos.logback.core.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.user.dto.UserSetupRequest;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/api/user")
    public List<UserEntity> getUser() {
        return userService.getUser();   // spec no id
    }

    @PostMapping("/api/setup")
    public ResponseEntity<?> completeSetup(@RequestBody UserSetupRequest setupRequest, @AuthenticationPrincipal OAuth2User principal) {
        String result = userService.completeSetup(setupRequest, principal);
        return ResponseEntity.ok(result);
    }


}
