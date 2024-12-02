package tw.commonground.backend.service.issue.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class IssueEntity implements SimpleIssueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String title;

    private String description;

    @Lob
    private String insight;

    private String authorId;

    private String authorName;

    private String authorAvatar;

//  Added for POST /api/issue/{id}/viewpoints endpoint
    @OneToMany(mappedBy = "issue")
    private Set<ManualFactEntity> manualFacts;

}
