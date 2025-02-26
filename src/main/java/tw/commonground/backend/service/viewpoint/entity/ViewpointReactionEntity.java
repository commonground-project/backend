package tw.commonground.backend.service.viewpoint.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.shared.entity.Reaction;

import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
public class ViewpointReactionEntity {

    @EmbeddedId
    private ViewpointReactionKey id;

    @Enumerated(EnumType.STRING)
    private Reaction reaction;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @MapsId("viewpointId")
    @ManyToOne(fetch = FetchType.LAZY)
    private ViewpointEntity viewpoint;

    public ViewpointReactionEntity() {
        this.reaction = Reaction.NONE;
    }

    public ViewpointReactionEntity(UserEntity user, ViewpointEntity viewpoint, Reaction reaction) {
        this.id = new ViewpointReactionKey(user.getId(), viewpoint.getId());
        this.user = user;
        this.viewpoint = viewpoint;
        this.reaction = reaction;
    }

    public UUID getViewpointId() {
        return id.getViewpointId();
    }
}
