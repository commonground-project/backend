package tw.commonground.backend.service.jwt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.commonground.backend.service.jwt.dto.RefreshTokenResponse;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.shared.tracing.Traced;

@Traced
@RestController
@RequestMapping("/api")
@ConditionalOnProperty(value = "feature.debug.jwt.enabled", havingValue = "true")
public class JwtDebugController {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    public JwtDebugController(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @GetMapping("/debug/generate/{userId}")
    public RefreshTokenResponse generateToken(@PathVariable Long userId) {
        FullUserEntity user = userRepository.findById(userId).orElseThrow();
        return jwtService.generateTokens(user);
    }
}
