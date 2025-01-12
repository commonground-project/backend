package tw.commonground.backend.service.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tw.commonground.backend.service.jwt.dto.RefreshTokenResponse;
import tw.commonground.backend.service.jwt.entity.RefreshTokenEntity;
import tw.commonground.backend.service.jwt.entity.RefreshTokenProjectionImpl;
import tw.commonground.backend.service.jwt.entity.RefreshTokenRepository;
import tw.commonground.backend.service.jwt.exception.RefreshTokenInvalidException;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("MethodName")
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final Long EXPIRATION_TIME = 1736658684976L;

    @Mock
    private JwtAccessUtil jwtAccessUtil;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Spy
    private Clock clock = Clock.fixed(Clock.systemDefaultZone().instant(), Clock.systemDefaultZone().getZone());

    @InjectMocks
    private JwtService jwtService;

    @Test
    void testGenerateTokens() {
        UserEntity user = createUser();
        RefreshTokenEntity refreshTokenEntity = createActiveRefreshToken();

        when(jwtAccessUtil.generateAccessToken(Mockito.argThat(u -> user.getId().equals(u.getId()))))
                .thenReturn("accessToken");

        when(jwtAccessUtil.generateRefreshToken(Mockito.argThat(u -> user.getId().equals(u.getId()))))
                .thenReturn(refreshTokenEntity);

        when(jwtAccessUtil.getRefreshTokenExpirationMillis())
                .thenReturn(1000L);

        RefreshTokenResponse response = jwtService.generateTokens(user);

        verify(jwtAccessUtil, Mockito.times(1))
                .generateAccessToken(user);

        verify(jwtAccessUtil, Mockito.times(1))
                .generateRefreshToken(user);

        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getExpirationTime()).isEqualTo(clock.millis() + jwtAccessUtil.getRefreshTokenExpirationMillis());
        assertThat(response.getRefreshToken()).isEqualTo(refreshTokenEntity.getId().toString());
    }

    @Test
    void testRefreshToken_validRefreshToken() {
        UserEntity user = createUser();
        RefreshTokenEntity refreshTokenEntity = createActiveRefreshToken();
        refreshTokenEntity.setUser(user);

        RefreshTokenEntity newRefreshTokenEntity = createActiveRefreshToken();
        newRefreshTokenEntity.setUser(user);

        when(refreshTokenRepository
                .findByIdAndIsActiveAndExpirationTimeAfter(
                        Mockito.eq(refreshTokenEntity.getId()),
                        Mockito.eq(true),
                        Mockito.anyLong()))
                .thenReturn(Optional.of(createRefreshTokenProjection(refreshTokenEntity)));

        when(jwtAccessUtil.generateAccessToken(Mockito.argThat(u -> user.getId().equals(u.getId()))))
                .thenReturn("accessToken");

        when(jwtAccessUtil.generateRefreshToken(Mockito.argThat(u -> user.getId().equals(u.getId()))))
                .thenReturn(newRefreshTokenEntity);

        RefreshTokenResponse response = jwtService.refreshToken(refreshTokenEntity.getId());

        verify(refreshTokenRepository, Mockito.times(1))
                .findByIdAndIsActiveAndExpirationTimeAfter(
                        Mockito.eq(refreshTokenEntity.getId()),
                        Mockito.eq(true), Mockito.anyLong());

        verify(jwtAccessUtil, Mockito.times(1))
                .generateAccessToken(Mockito.argThat(u -> user.getId().equals(u.getId())));

        verify(jwtAccessUtil, Mockito.times(1))
                .generateRefreshToken(Mockito.argThat(u -> user.getId().equals(u.getId())));

        verify(refreshTokenRepository, Mockito.times(1))
                .inactivateById(refreshTokenEntity.getId());

        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo(newRefreshTokenEntity.getId().toString());
    }

    @Test
    void testRefreshToken_invalidRefreshToken() {
        RefreshTokenEntity refreshTokenEntity = createInactiveRefreshToken();

        when(refreshTokenRepository
                .findByIdAndIsActiveAndExpirationTimeAfter(
                        Mockito.eq(refreshTokenEntity.getId()),
                        Mockito.eq(true),
                        Mockito.anyLong()))
                .thenReturn(Optional.empty());

        UUID refreshToken = refreshTokenEntity.getId();
        assertThrows(RefreshTokenInvalidException.class, () -> jwtService.refreshToken(refreshToken));

        verify(refreshTokenRepository, Mockito.times(1))
                .findByIdAndIsActiveAndExpirationTimeAfter(
                        Mockito.eq(refreshTokenEntity.getId()),
                        Mockito.eq(true),
                        Mockito.anyLong());

        verify(jwtAccessUtil, Mockito.never()).generateAccessToken(Mockito.any());
        verify(jwtAccessUtil, Mockito.never()).generateRefreshToken(Mockito.any());
        verify(refreshTokenRepository, Mockito.never()).inactivateById(Mockito.any());
    }

    private RefreshTokenProjectionImpl createRefreshTokenProjection(RefreshTokenEntity refreshTokenEntity) {
        RefreshTokenProjectionImpl refreshTokenProjection = new RefreshTokenProjectionImpl();
        refreshTokenProjection.setId(refreshTokenEntity.getId().toString());
        refreshTokenProjection.setUser(createUser());
        return refreshTokenProjection;
    }

    private RefreshTokenEntity createActiveRefreshToken() {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setId(UUID.randomUUID());
        refreshTokenEntity.setIsActive(true);
        refreshTokenEntity.setExpirationTime(EXPIRATION_TIME);
        return refreshTokenEntity;
    }

    private RefreshTokenEntity createInactiveRefreshToken() {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setId(UUID.randomUUID());
        refreshTokenEntity.setIsActive(false);
        refreshTokenEntity.setExpirationTime(EXPIRATION_TIME);
        return refreshTokenEntity;
    }

    private UserEntity createUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUuid(UUID.randomUUID());
        user.setUsername("username");
        user.setEmail("email");
        user.setNickname("nickname");
        user.setRole(UserRole.ROLE_USER);
        return user;
    }
}
