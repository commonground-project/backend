package tw.commonground.backend.service.read.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.service.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ReadEntity {
    @EmbeddedId
    private ReadKey id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = true)
    private IssueEntity issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viewpoint_id", nullable = true)
    private ViewpointEntity viewpoint;

    private Boolean readStatus;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime timestamp;

    public UUID getObjectId() {
        if (id.getObjectType() == ReadObjectType.ISSUE) {
            return issue != null ? issue.getId() : null;
        } else if (id.getObjectType() == ReadObjectType.VIEWPOINT) {
            return viewpoint != null ? viewpoint.getId() : null;
        }
        return null;
    }
}
