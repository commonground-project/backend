package tw.commonground.backend.service.viewpoint.dto;

import lombok.Getter;
import lombok.Setter;
import tw.commonground.backend.shared.entity.Reaction;

@Getter
@Setter
public class ViewpointReactionResponse extends ViewpointReactionBase {
    public ViewpointReactionResponse(Reaction reaction) {
        this.setReaction(reaction);
    }
}
