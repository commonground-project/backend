package tw.commonground.backend.service.reply.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
public class QuoteReply {

    private UUID replyId;

    private Integer start;

    private Integer end;

}
