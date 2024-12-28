package tw.commonground.backend.service.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.user.entity.UserRepository;
import tw.commonground.backend.service.user.entity.UserRole;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtAccessUtilTest {

    @Mock
    private DecodedJWT mockJwt;

    @Mock
    private UserRepository userRepository;

    JwtAccessUtil jwtAccessUtil;

    @BeforeEach
    void setUp() {
        jwtAccessUtil = new JwtAccessUtil(null, userRepository);
        ReflectionTestUtils.setField(jwtAccessUtil, "secret", "testSecret");
        ReflectionTestUtils.setField(jwtAccessUtil, "accessTokenExpiration", "PT15M");
        ReflectionTestUtils.setField(jwtAccessUtil, "refreshTokenExpiration", "P30D");
        ReflectionTestUtils.setField(jwtAccessUtil, "issuer", "commonground");

        jwtAccessUtil.postConstruct();
    }

    @Test
    void testVerifyAccessToken_idIsNull_callsFetchEntityId() {
        FullUserEntity user = createUser();
        String token = jwtAccessUtil.generateAccessToken(user);
        JwtUserDetails jwtUserDetails = jwtAccessUtil.verifyAccessToken(token);
        Mockito.when(userRepository.getIdByUid(user.getUuid())).thenReturn(1L);

        assertThat(jwtUserDetails.getId()).isEqualTo(1L);

        Mockito.verify(userRepository, Mockito.times(1)).getIdByUid(user.getUuid());
    }

    @Test
    void testVerifyAccessToken_idIsNotNull_doesNotCallFetchEntityId() {
        FullUserEntity user = createUser();

        String token = jwtAccessUtil.generateAccessToken(user);
        JwtUserDetails jwtUserDetails = jwtAccessUtil.verifyAccessToken(token);
        ReflectionTestUtils.setField(jwtUserDetails, "id", 1L);

        jwtUserDetails.getId();

        Mockito.verify(userRepository, Mockito.never()).getIdByUid(user.getUuid());
        assertThat(jwtUserDetails.getId()).isEqualTo(1L);
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