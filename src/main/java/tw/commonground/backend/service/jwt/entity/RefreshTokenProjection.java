package tw.commonground.backend.service.jwt.entity;

import tw.commonground.backend.service.user.entity.FullUserEntity;

public interface RefreshTokenProjection {
    String getId();

    String getExpirationTime();

    String getIsActive();

    FullUserEntity getUser();
}
