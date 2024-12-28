package tw.commonground.backend.service.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tw.commonground.backend.service.jwt.dto.RefreshTokenResponse;
import tw.commonground.backend.service.jwt.entity.RefreshTokenEntity;
import tw.commonground.backend.service.jwt.entity.RefreshTokenRepository;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRole;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtAccessUtil jwtAccessUtil;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private JwtService jwtService;

    @Test
    void testGenerateTokens() {
        UserEntity user = createUser();
        RefreshTokenEntity refreshTokenEntity = createActiveRefreshToken(System.currentTimeMillis() + 1000);

        Mockito.when(jwtAccessUtil.generateAccessToken(user)).thenReturn("accessToken");
        Mockito.when(jwtAccessUtil.generateRefreshToken(user)).thenReturn(refreshTokenEntity);

        RefreshTokenResponse response = jwtService.generateTokens(user);

        Mockito.verify(jwtAccessUtil, Mockito.times(1)).generateAccessToken(user);
        Mockito.verify(jwtAccessUtil, Mockito.times(1)).generateRefreshToken(user);
//        todo: verify refreshTokenRepository method call

        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo(refreshTokenEntity.getId().toString());
    }

    @Test
    void refreshToken() {
    }

    private RefreshTokenEntity createActiveRefreshToken(Long expirationTime) {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setId(UUID.randomUUID());
        refreshTokenEntity.setIsActive(true);
        refreshTokenEntity.setExpirationTime(expirationTime);
        return refreshTokenEntity;
    }

    private RefreshTokenEntity createInactiveRefreshToken(Long expirationTime) {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setId(UUID.randomUUID());
        refreshTokenEntity.setIsActive(false);
        refreshTokenEntity.setExpirationTime(expirationTime);
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