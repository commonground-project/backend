package tw.commonground.backend.service.user.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public class BaseEntityWithAuthor {
    private UUID authorId;

    private String authorName;

    private String authorAvatar;

    public void setAuthor(FullUserEntity user) {
        this.authorId = user.getUuid();
        this.authorName = user.getNickname();
        this.authorAvatar = "/api/user/avatar/" + user.getUsername();
    }
}
