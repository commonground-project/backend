package tw.commonground.backend.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtStateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtStateUtil.class);

    @Value("${jwt.secret:secret}")
    private String secret;

    @Value("${jwt.stateExpiration:PT10S}")
    private String stateExpiration;

    @Value("${jwt.issuer:commonground}")
    private String issuer;

    @Getter
    private Long stateExpirationMillis;

    private JWTVerifier stateVerifier;

    @PostConstruct
    public void postConstruct() {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        stateVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();

        try {
            stateExpirationMillis = Duration.parse(stateExpiration).toMillis();
        } catch (Exception e) {
            throw new IllegalArgumentException(String
                    .format("Failed to parse %s to Duration, %s", stateExpiration, e));
        }
    }

    public boolean verifyState(String state) {
        try {
            stateVerifier.verify(state);
            return true;
        } catch (Exception e) {
            logger.error("Failed to verify state {}", e.getMessage());
            return false;
        }
    }

    public String generateState() {
        return JWT.create()
                .withIssuer(issuer)
                .withJWTId(UUID.randomUUID().toString())
                .withSubject("state")
                .withIssuedAt(new Date())
                .withNotBefore(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + stateExpirationMillis))
                .sign(Algorithm.HMAC256(secret));
    }
}
