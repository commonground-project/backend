package tw.commonground.backend.service.viewpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewpointReactionResponse extends ViewpointReactionBase {
    public ViewpointReactionResponse(String reaction) {
        this.setReaction(reaction);
    }
}