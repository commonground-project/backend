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
    private Long id;

    private UUID uuid;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private UserEntity user;

    private String gender;
    private String occupation;
    private String location;

//    @Column(columnDefinition = "text[]")
//    @Type(value = tw.commonground.backend.service.internal.profile.entity.CustomStringArrayType.class)
//    private String[] browsingTags;
    @Column(name = "browsingTags")
    @Convert(converter = ListToStringConverter.class)
    private List<String> browsingTags;

//    @Column(columnDefinition = "text[]")
//    @Type(value = tw.commonground.backend.service.internal.profile.entity.CustomStringArrayType.class)
//    private String[] searchKeywords;
    @Column(name = "searchKeywords")
    @Convert(converter = ListToStringConverter.class)
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
