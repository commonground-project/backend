package tw.commonground.backend.service.newcontent.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NewcontentEntity {

    @EmbeddedId
    private NewcontentKey id;

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

    private Boolean newcontentStatus;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime timestamp;

    public UUID getObjectId() {
        if (id.getObjectType() == NewcontentObjectType.ISSUE) {
            return issue != null ? issue.getId() : null;
        } else if (id.getObjectType() == NewcontentObjectType.VIEWPOINT) {
            return viewpoint != null ? viewpoint.getId() : null;
        }
        return null;
    }

}