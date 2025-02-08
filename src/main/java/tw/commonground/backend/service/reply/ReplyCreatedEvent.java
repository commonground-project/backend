package tw.commonground.backend.service.reply;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import tw.commonground.backend.service.reply.dto.QuoteReply;
import tw.commonground.backend.service.reply.entity.ReplyEntity;

import java.util.List;

@Getter
public class ReplyCreatedEvent extends ApplicationEvent {

    private final ReplyEntity replyEntity;

    private final List<QuoteReply> quotes;

    public ReplyCreatedEvent(ReplyEntity replyEntity, List<QuoteReply> quotes) {
        super(replyEntity);
        this.replyEntity = replyEntity;
        this.quotes = quotes;
    }
}
