package tw.commonground.backend.service.viewpoint.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tw.commonground.backend.service.user.entity.UserEntity;

@Getter
@Setter
@Entity
public class ViewpointReactionEntity {

    @EmbeddedId
    private ViewpointReactionKey id;

    @Enumerated(EnumType.STRING)
    private Reaction reaction;

    @ManyToOne
    @MapsId("userId")
    private UserEntity user;

    @ManyToOne
    @MapsId("viewpointId")
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
}
