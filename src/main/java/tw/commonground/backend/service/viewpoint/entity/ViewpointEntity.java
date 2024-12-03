package tw.commonground.backend.service.viewpoint.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tw.commonground.backend.service.issue.entity.IssueEntity;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ViewpointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private UUID authorId;

    private String authorName;

    private URI authorAvatar;

    @ColumnDefault("0")
    private Integer likeCount = 0;

    @ColumnDefault("0")
    private Integer reasonableCount = 0;

    @ColumnDefault("0")
    private Integer dislikeCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    private IssueEntity issue;

    @OneToMany(mappedBy = "viewpoint")
    private Set<ViewpointFactEntity> facts;
}
