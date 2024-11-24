package tw.commonground.backend.service.viewpoint.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewpointReaction {
    @Enumerated(EnumType.STRING)
    private Reaction reaction;
}
