package tw.commonground.backend.service.jwt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.commonground.backend.service.jwt.dto.RefreshTokenResponse;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.UUID;

@Traced
@RestController
@RequestMapping("/api")
public class JwtController {

    private final JwtService jwtService;

    public JwtController(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
    }

    @GetMapping("/jwt/refresh/{refreshToken}")
    public RefreshTokenResponse refreshToken(@PathVariable UUID refreshToken) {
        return jwtService.refreshToken(refreshToken);
    }
}
