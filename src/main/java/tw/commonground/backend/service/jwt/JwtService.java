package tw.commonground.backend.service.jwt;

import org.springframework.stereotype.Service;
import tw.commonground.backend.service.jwt.dto.RefreshTokenResponse;
import tw.commonground.backend.service.jwt.entity.RefreshTokenEntity;
import tw.commonground.backend.service.jwt.entity.RefreshTokenProjection;
import tw.commonground.backend.service.jwt.entity.RefreshTokenRepository;
import tw.commonground.backend.service.jwt.exception.RefreshTokenInvalidException;
import tw.commonground.backend.service.user.entity.FullUserEntity;

import java.util.UUID;

@Service
public class JwtService {

    private final JwtAccessUtil jwtAccessUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    public JwtService(JwtAccessUtil jwtAccessUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtAccessUtil = jwtAccessUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public FullUserEntity authenticate(String accessToken) {
        return jwtAccessUtil.verifyAccessToken(accessToken);
    }

    public RefreshTokenResponse generateTokens(FullUserEntity fullUserEntity) {
        String accessToken = jwtAccessUtil.generateAccessToken(fullUserEntity);
        RefreshTokenEntity refreshToken = jwtAccessUtil.generateRefreshToken(fullUserEntity);
        Long expirationTime = System.currentTimeMillis() + jwtAccessUtil.getRefreshTokenExpirationMillis();

        return new RefreshTokenResponse(refreshToken.getId().toString(), expirationTime, accessToken);
    }

    public RefreshTokenResponse refreshToken(UUID refreshToken) {
        RefreshTokenProjection refreshTokenProjection = refreshTokenRepository
                .findByIdAndIsActiveAndExpirationTimeAfter(refreshToken,
                        true, System.currentTimeMillis()).orElseThrow(RefreshTokenInvalidException::new);

        String accessToken = jwtAccessUtil.generateAccessToken(refreshTokenProjection.getUser());
        RefreshTokenEntity newRefreshToken = jwtAccessUtil.generateRefreshToken(refreshTokenProjection.getUser());
        String newRefreshTokenString = newRefreshToken.getId().toString();

        return new RefreshTokenResponse(newRefreshTokenString,
                newRefreshToken.getExpirationTime(), accessToken);
    }
}
