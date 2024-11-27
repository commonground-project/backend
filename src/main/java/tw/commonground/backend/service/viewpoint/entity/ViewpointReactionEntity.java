package tw.commonground.backend.service.viewpoint.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
public class ViewpointReactionEntity {

    @EmbeddedId
    private ViewpointReactionId id;

    @Enumerated(EnumType.STRING)
    private Reaction reaction;

    public ViewpointReactionEntity() {
        this.reaction = Reaction.NONE;
    }
}