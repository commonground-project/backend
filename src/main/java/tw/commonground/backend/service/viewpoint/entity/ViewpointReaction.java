package tw.commonground.backend.service.viewpoint.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ViewpointReaction {
    @Enumerated(EnumType.STRING)
    private Reaction reaction;

    public ViewpointReaction() {
        this.reaction = Reaction.NONE;
    }
}
