package tw.commonground.backend.service.reply.dto;

import lombok.Getter;
import lombok.Setter;
import tw.commonground.backend.service.reply.entity.Reaction;

@Getter
@Setter
public class ReactionRequest {

    private Reaction reaction;

}
