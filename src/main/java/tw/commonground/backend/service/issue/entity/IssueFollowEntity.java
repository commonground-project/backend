package tw.commonground.backend.service.issue.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tw.commonground.backend.service.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class IssueFollowEntity {
    @EmbeddedId
    private IssueFollowKey id;

    private Boolean follow;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @MapsId("issueId")
    @ManyToOne(fetch = FetchType.LAZY)
    private IssueEntity issue;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public IssueFollowEntity() {
        this.follow = false;
    }

    public IssueFollowEntity(IssueFollowKey id, Boolean follow) {
        this.id = id;
        this.follow = follow;
    }

    public UUID getIssueId() {
        return id.getIssueId();
    }
}
