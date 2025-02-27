package tw.commonground.backend.service.reply.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.shared.entity.Reaction;

@Getter
@Setter
@Builder
@ToString
public class ReplyReactionResponse {
    private Reaction reaction;
}
