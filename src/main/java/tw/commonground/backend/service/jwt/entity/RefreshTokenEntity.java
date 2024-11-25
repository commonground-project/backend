package tw.commonground.backend.service.jwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import tw.commonground.backend.service.user.entity.UserEntity;

import java.util.UUID;

@Entity
@Getter
@Setter
@NamedEntityGraph
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @ColumnDefault("true")
    private Boolean isActive;

    private Long expirationTime;
}
