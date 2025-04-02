package tw.commonground.backend.service.user.entity;


import jakarta.persistence.*;
import lombok.*;
import tw.commonground.backend.security.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements SimpleUserEntity, FullUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, updatable = false, nullable = false)
    private UUID uuid;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String nickname;

    private byte[] profileImage;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    private UserOccupation occupation;

    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserSettingEntity setting;

    @PrePersist
    public void prePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }
}
