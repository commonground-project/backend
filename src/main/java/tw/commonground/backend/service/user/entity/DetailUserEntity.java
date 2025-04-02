package tw.commonground.backend.service.user.entity;

import tw.commonground.backend.security.UserRole;

import java.time.LocalDate;
import java.util.UUID;

public interface DetailUserEntity {
    Long getId();

    UUID getUuid();

    String getUsername();

    String getEmail();

    String getNickname();

    UserRole getRole();

    UserOccupation getOccupation();

    UserGender getGender();

    LocalDate getBirthdate();
}
