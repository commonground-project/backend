package tw.commonground.backend.service.read.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
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
public class ReadEntity {
    @EmbeddedId
    private ReadKey id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @MapsId("objectId")
    @ManyToOne(fetch = FetchType.LAZY)
    private IssueEntity issue;

    @MapsId("objectId")
    @ManyToOne(fetch = FetchType.LAZY)
    private ViewpointEntity viewpoint;

    private Boolean readStatus;

    @LastModifiedDate
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private ReadObjectType objectType;

    public UUID getObjectId() {
        if (objectType == ReadObjectType.ISSUE) {
            return issue != null ? issue.getId() : null;
        } else if (objectType == ReadObjectType.VIEWPOINT) {
            return viewpoint != null ? viewpoint.getId() : null;
        }
        return null;
    }
}
