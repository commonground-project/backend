package tw.commonground.backend.service.viewpoint.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.shared.entity.Reaction;

@Getter
@Setter
@ToString
public class ViewpointReactionResponse extends ViewpointReactionBase {
    public ViewpointReactionResponse(Reaction reaction) {
        this.setReaction(reaction);
    }
}
