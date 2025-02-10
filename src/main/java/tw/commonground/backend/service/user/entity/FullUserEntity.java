package tw.commonground.backend.service.user.entity;

import tw.commonground.backend.security.UserRole;

import java.util.UUID;

public interface FullUserEntity {
    Long getId();

    UUID getUuid();

    String getUsername();

    String getEmail();

    String getNickname();

    UserRole getRole();
}
