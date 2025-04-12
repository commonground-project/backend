package tw.commonground.backend.service.jwt.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.service.user.entity.FullUserEntity;

@Getter
@Setter
@ToString
public class RefreshTokenProjectionImpl implements RefreshTokenProjection {
    private String id;
    private String expirationTime;
    private String isActive;
    private FullUserEntity user;
}
