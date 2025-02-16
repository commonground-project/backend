package tw.commonground.backend.service.internal.profile.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tw.commonground.backend.service.user.entity.UserEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class InternalProfileEntity {
    @Id
    private Long id;

    private UUID uuid;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private UserEntity user;

    private String gender;
    private String occupation;
    private String location;

    @Column(name = "browsingTags")
    @Convert(converter = ListToStringConverter.class)
    private List<String> browsingTags;

    @Column(name = "searchKeywords")
    @Convert(converter = ListToStringConverter.class)
    private List<String> searchKeywords;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastActiveAt;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityFrequencyEntity> activityFrequency;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTopIpEntity> userTopIp;
}
