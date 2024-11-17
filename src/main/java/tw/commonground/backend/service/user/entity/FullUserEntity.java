package tw.commonground.backend.service.user.entity;

public interface FullUserEntity {
    Long getId();

    String getUsername();

    String getEmail();

    String getNickname();

    UserRole getRole();
}
