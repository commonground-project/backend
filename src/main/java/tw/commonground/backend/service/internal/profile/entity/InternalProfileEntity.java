package tw.commonground.backend.service.internal.profile.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import tw.commonground.backend.service.user.entity.UserEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalProfileEntity {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", referencedColumnName = "uuid", nullable = false)
    private UserEntity user;

    private String gender;
    private String occupation;
    private String location;

    private List<String> browsingTags;
    private List<String> searchKeywords;

    private LocalDateTime createdAt;
    private LocalDateTime lastActiveAt;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityFrequencyEntity> activityFrequency;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTopIpEntity> userTopIp;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastActiveAt = LocalDateTime.now();
    }
}
