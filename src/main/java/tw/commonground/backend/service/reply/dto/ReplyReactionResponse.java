package tw.commonground.backend.service.reply.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.commonground.backend.shared.entity.Reaction;

@Getter
@Setter
@Builder
public class ReplyReactionResponse {
    private Reaction reaction;
}
