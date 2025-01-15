package tw.commonground.backend.service.reply.dto;


import java.util.UUID;

public class QuoteReplyRequest extends QuoteReply {

    QuoteReplyRequest(UUID replyId, Integer start, Integer end) {
        super(replyId, start, end);
    }
}
