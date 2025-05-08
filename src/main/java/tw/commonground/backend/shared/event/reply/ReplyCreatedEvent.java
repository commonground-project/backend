package tw.commonground.backend.shared.event.reply;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;
import tw.commonground.backend.service.reply.dto.QuoteReply;
import tw.commonground.backend.service.reply.entity.ReplyEntity;
import tw.commonground.backend.service.user.entity.FullUserEntity;

import java.util.List;

@Getter
@ToString
public class ReplyCreatedEvent extends ApplicationEvent {

    private final FullUserEntity user;

    private final ReplyEntity replyEntity;

    private final List<QuoteReply> quotes;

    public ReplyCreatedEvent(FullUserEntity user, ReplyEntity replyEntity, List<QuoteReply> quotes) {
        super(replyEntity);
        this.user = user;
        this.replyEntity = replyEntity;
        this.quotes = quotes;
    }
}
