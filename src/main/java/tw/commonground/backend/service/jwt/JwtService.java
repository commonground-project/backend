package tw.commonground.backend.service.jwt;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tw.commonground.backend.service.jwt.dto.RefreshTokenResponse;
import tw.commonground.backend.service.jwt.entity.RefreshTokenEntity;
import tw.commonground.backend.service.jwt.entity.RefreshTokenProjection;
import tw.commonground.backend.service.jwt.entity.RefreshTokenRepository;
import tw.commonground.backend.service.jwt.exception.RefreshTokenInvalidException;
import tw.commonground.backend.service.user.entity.FullUserEntity;

import java.time.Clock;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtAccessUtil jwtAccessUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    private final Clock clock;

    public JwtService(Clock clock, JwtAccessUtil jwtAccessUtil, RefreshTokenRepository refreshTokenRepository) {
        this.clock = clock;
        this.jwtAccessUtil = jwtAccessUtil;
        this.refreshTokenRepository = refreshTokenRepository;

        refreshTokenRepository.deleteAllByExpirationTimeBefore(System.currentTimeMillis());
    }

    public FullUserEntity authenticate(String accessToken) {
        return jwtAccessUtil.verifyAccessToken(accessToken);
    }

    public RefreshTokenResponse generateTokens(FullUserEntity fullUserEntity) {
        String accessToken = jwtAccessUtil.generateAccessToken(fullUserEntity);
        RefreshTokenEntity refreshToken = jwtAccessUtil.generateRefreshToken(fullUserEntity);
        Long expirationTime = clock.millis() + jwtAccessUtil.getRefreshTokenExpirationMillis();

        return new RefreshTokenResponse(refreshToken.getId().toString(), expirationTime, accessToken);
    }

    public RefreshTokenResponse refreshToken(UUID refreshToken) {
        RefreshTokenProjection refreshTokenProjection = refreshTokenRepository
                .findByIdAndIsActiveAndExpirationTimeAfter(refreshToken,
                        true, clock.millis()).orElseThrow(RefreshTokenInvalidException::new);

        String accessToken = jwtAccessUtil.generateAccessToken(refreshTokenProjection.getUser());
        RefreshTokenEntity newRefreshToken = jwtAccessUtil.generateRefreshToken(refreshTokenProjection.getUser());
        String newRefreshTokenString = newRefreshToken.getId().toString();

        refreshTokenRepository.inactivateById(refreshToken);

        return new RefreshTokenResponse(newRefreshTokenString,
                newRefreshToken.getExpirationTime(), accessToken);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void rotateRefreshTokens() {
        refreshTokenRepository.deleteAllByExpirationTimeBefore(clock.millis());
    }
}
