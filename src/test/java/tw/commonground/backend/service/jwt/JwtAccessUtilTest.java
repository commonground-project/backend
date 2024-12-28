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

/**
 * Unit tests for the {@link JwtAccessUtil} class.
 *
 * <p>This class validates the behavior of handling JWT tokens in a secure manner.
 * For security reasons, the JWT payload does not include the long ID directly.
 * Instead, it contains a UUID, which is used to query the corresponding long ID
 * from the database only when necessary. This design ensures that sensitive
 * information (like long IDs) is not exposed directly in the JWT.</p>
 *
 * <p>The tests focus on the following scenarios:</p>
 * <ul>
 *     <li>When the ID is null, the system should correctly retrieve the long ID
 *         from the database using the UUID provided in the JWT.</li>
 *     <li>Once the ID is retrieved, it should be cached and subsequent requests
 *         should not trigger additional database queries, ensuring efficiency.</li>
 * </ul>
 *
 */
@SuppressWarnings("MethodName")
@ExtendWith(MockitoExtension.class)
class JwtAccessUtilTest {

    @Mock
    private DecodedJWT mockJwt;

    @Mock
    private UserRepository userRepository;

    private JwtAccessUtil jwtAccessUtil;

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

    @Test
    void testVerifyAccessToken_idIsNull_callsFetchEntityIdAndSetsId() {
        FullUserEntity user = createUser();
        String token = jwtAccessUtil.generateAccessToken(user);
        JwtUserDetails jwtUserDetails = jwtAccessUtil.verifyAccessToken(token);
        Mockito.when(userRepository.getIdByUid(user.getUuid())).thenReturn(1L);

        assertThat(jwtUserDetails.getId()).isEqualTo(1L);

        Object fieldValue = ReflectionTestUtils.getField(jwtUserDetails, "id");
        assertThat(fieldValue).isEqualTo(1L);

        Mockito.verify(userRepository, Mockito.times(1)).getIdByUid(user.getUuid());
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
