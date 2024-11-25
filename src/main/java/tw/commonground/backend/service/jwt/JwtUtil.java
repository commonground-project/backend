package tw.commonground.backend.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tw.commonground.backend.service.jwt.entity.RefreshTokenEntity;
import tw.commonground.backend.service.jwt.entity.RefreshTokenRepository;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    @Value("${jwt.secret:secret}")
    private String secret;

    @Value("${jwt.accessTokenExpiration:PT15M}")
    private String accessTokenExpiration;

    @Value("${jwt.refreshTokenExpiration:P30D}")
    private String refreshTokenExpiration;

    @Value("${jwt.issuer:commonground}")
    private String issuer;

    @Getter
    private Long accessTokenExpirationMillis;

    @Getter
    private Long refreshTokenExpirationMillis;

    private JWTVerifier accessTokenVerifier;

    public JwtUtil(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void postConstruct() {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        accessTokenVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withClaimPresence("role")
                .withClaimPresence("username")
                .acceptLeeway(1)
                .build();

        try {
            this.accessTokenExpirationMillis = Duration.parse(accessTokenExpiration).toMillis();
        } catch (Exception e) {
            throw new IllegalArgumentException(String
                    .format("Failed to parse %s to Duration, %s", accessTokenExpiration, e));
        }

        try {
            this.refreshTokenExpirationMillis = Duration.parse(refreshTokenExpiration).toMillis();
        } catch (Exception e) {
            throw new IllegalArgumentException(String
                    .format("Failed to parse %s to Duration, %s", refreshTokenExpiration, e));
        }
    }

    public String generateAccessToken(FullUserEntity user) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getUuid().toString())
                .withClaim("role", user.getRole().name())
                .withClaim("username", user.getUsername())
                .withClaim("email", user.getEmail())
                .withClaim("nickname", user.getNickname())
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(new Date())
                .withNotBefore(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpirationMillis))
                .sign(Algorithm.HMAC256(secret));
    }

    public JwtUserDetails verifyAccessToken(String token) {
        DecodedJWT jwt = accessTokenVerifier.verify(token);
        return new JwtUserDetails(jwt, () -> userRepository.getIdByUid(jwt.getSubject()));
    }

    public String generateRefreshToken(FullUserEntity user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setUser(userEntity);
        refreshToken.setIsActive(true);
        refreshToken.setExpirationTime(System.currentTimeMillis() + refreshTokenExpirationMillis);

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getId().toString();
    }
}
