package tw.commonground.backend.service.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingEntity {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private UserEntity user;

    @ColumnDefault("true")
    private Boolean newReplyInMyViewpoint;

    @ColumnDefault("true")
    private Boolean newReferenceToMyReply;

    @ColumnDefault("true")
    private Boolean newNodeOfTimelineToFollowedIssue;

}
