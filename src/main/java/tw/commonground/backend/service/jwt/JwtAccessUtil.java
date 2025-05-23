package tw.commonground.backend.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
import java.util.concurrent.TimeUnit;

@Component
public class JwtAccessUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAccessUtil.class);

    private static final int MILLISECONDS = 1000;

    private static final int CACHE_EXPIRATION = 15;

    private static final int MAXIMUM_SIZE = 10000;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    @Value("${jwt.secret:secret}")
    private String secret;

    @Value("${jwt.access-token.expiration:PT15M}")
    private String accessTokenExpiration;

    @Value("${jwt.refresh.token.expiration:P30D}")
    private String refreshTokenExpiration;

    @Value("${jwt.issuer:commonground}")
    private String issuer;

    @Getter
    private Long accessTokenExpirationMillis;

    @Getter
    private Long refreshTokenExpirationMillis;

    private JWTVerifier accessTokenVerifier;

    private final Cache<String, JwtUserDetails> jwtCache = Caffeine.newBuilder()
            .expireAfterWrite(CACHE_EXPIRATION, TimeUnit.MINUTES)
            .maximumSize(MAXIMUM_SIZE)
            .build();

    public JwtAccessUtil(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
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
        return jwtCache.get(token, this::verifyAndCacheToken);
    }

    private JwtUserDetails verifyAndCacheToken(String token) {
        DecodedJWT jwt = accessTokenVerifier.verify(token);
        long ttl = jwt.getExpiresAt().getTime() - System.currentTimeMillis();
        ttl = Math.max(ttl / MILLISECONDS, 1);
        long finalTtl = ttl;
        jwtCache.policy().expireAfterWrite().ifPresent(expiry -> expiry.setExpiresAfter(finalTtl, TimeUnit.SECONDS));

        return new JwtUserDetails(jwt, () -> userRepository.getIdByUid(UUID.fromString(jwt.getSubject())));
    }

    public RefreshTokenEntity generateRefreshToken(FullUserEntity user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setUser(userEntity);
        refreshToken.setIsActive(true);
        refreshToken.setExpirationTime(System.currentTimeMillis() + refreshTokenExpirationMillis);

        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public void inactiveRefreshToken(UUID refreshToken) {
        refreshTokenRepository.inactivateById(refreshToken);
    }
}
