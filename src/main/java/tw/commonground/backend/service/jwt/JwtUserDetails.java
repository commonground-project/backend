package tw.commonground.backend.service.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import tw.commonground.backend.service.user.entity.FullUserEntity;
import tw.commonground.backend.service.user.entity.UserRole;

import java.util.UUID;
import java.util.function.Supplier;

@Getter
public class JwtUserDetails implements FullUserEntity {

    private final Supplier<Long> fetchEntityId;

    private final DecodedJWT jwtString;

    private Long id;

    private final UUID uuid;

    private final String username;

    private final String email;

    private final String nickname;

    private final UserRole role;

    public JwtUserDetails(DecodedJWT jwt, Supplier<Long> fetchEntityId) {
        this.jwtString = jwt;
        this.fetchEntityId = fetchEntityId;

        this.uuid = UUID.fromString(jwt.getSubject());
        this.username = jwt.getClaim("username").asString();
        this.email = jwt.getClaim("email").asString();
        this.nickname = jwt.getClaim("nickname").asString();
        this.role = UserRole.valueOf(jwt.getClaim("role").asString());
    }

    @Override
    public Long getId() {
        if (id == null) {
            id = fetchEntityId.get();
        }

        return id;
    }
}
