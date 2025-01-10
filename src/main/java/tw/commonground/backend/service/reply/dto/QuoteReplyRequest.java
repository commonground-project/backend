package tw.commonground.backend.service.reply.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class QuoteReplyRequest {

    private UUID replyId;

    private Integer start;

    private Integer end;

}
