package tw.commonground.backend.service.reply.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.service.reply.entity.Reaction;

@Getter
@Setter
@ToString
public class ReactionRequest {

    private Reaction reaction;

}
