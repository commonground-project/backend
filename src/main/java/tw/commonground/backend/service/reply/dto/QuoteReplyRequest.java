package tw.commonground.backend.service.reply.dto;


import lombok.ToString;

import java.util.UUID;

@ToString
public class QuoteReplyRequest extends QuoteReply {

    QuoteReplyRequest(UUID replyId, Integer start, Integer end) {
        super(replyId, start, end);
    }
}
