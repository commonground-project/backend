package tw.commonground.backend.service.jwt;

import org.springframework.stereotype.Service;
import tw.commonground.backend.service.jwt.dto.RefreshTokenResponse;
import tw.commonground.backend.service.jwt.entity.RefreshTokenProjection;
import tw.commonground.backend.service.jwt.entity.RefreshTokenRepository;
import tw.commonground.backend.service.jwt.exception.RefreshTokenInvalidException;
import tw.commonground.backend.service.user.entity.FullUserEntity;

import java.util.UUID;

@Service
public class JwtService {

    private final JwtUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    public JwtService(JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public FullUserEntity authenticate(String accessToken) {
        return jwtUtil.verifyAccessToken(accessToken);
    }

    public RefreshTokenResponse generateTokens(FullUserEntity fullUserEntity) {
        String accessToken = jwtUtil.generateAccessToken(fullUserEntity);
        String refreshToken = jwtUtil.generateRefreshToken(fullUserEntity);
        Long expirationTime = System.currentTimeMillis() + jwtUtil.getRefreshTokenExpirationMillis();

        return new RefreshTokenResponse(refreshToken, expirationTime, accessToken);
    }

    public RefreshTokenResponse refreshToken(UUID refreshToken) {
        RefreshTokenProjection refreshTokenProjection = refreshTokenRepository
                .findByIdAndIsActiveAndExpirationTimeBefore(refreshToken,
                        true, System.currentTimeMillis()).orElseThrow(RefreshTokenInvalidException::new);

        String accessToken = jwtUtil.generateAccessToken(refreshTokenProjection.getUser());
        String newRefreshToken = jwtUtil.generateRefreshToken(refreshTokenProjection.getUser());

        Long expirationTime = System.currentTimeMillis() + jwtUtil.getRefreshTokenExpirationMillis();

        return new RefreshTokenResponse(newRefreshToken, expirationTime, accessToken);
    }
}
