package tw.commonground.backend.shared.content;

import lombok.Data;
import tw.commonground.backend.service.reply.dto.QuoteReply;

import java.util.List;
import java.util.UUID;

@Data
public class ContentReply {

    private String text;

    private List<UUID> facts;

    private List<QuoteReply> quotes;
}
