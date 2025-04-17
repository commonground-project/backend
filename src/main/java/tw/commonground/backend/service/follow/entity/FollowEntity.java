package tw.commonground.backend.service.follow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;
import tw.commonground.backend.shared.entity.RelatedObject;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@EntityListeners(AuditingEntityListener.class)
public class FollowEntity {

    @EmbeddedId
    private FollowKey id;

    private Boolean follow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id", insertable = false, updatable = false)
    private IssueEntity issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id", insertable = false, updatable = false)
    private ViewpointEntity viewpoint;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public FollowEntity() {
        this.follow = false;
    }

    public FollowEntity(FollowKey id, Boolean follow) {
        this.id = id;
        this.follow = follow;
    }

    public UUID getObjectId() {
        if (id.getObjectType() == RelatedObject.ISSUE && issue != null) {
            return issue.getId();
        } else if (id.getObjectType() == RelatedObject.VIEWPOINT && viewpoint != null) {
            return viewpoint.getId();
        }
        return null;
    }

}
