package tw.commonground.backend.service.reply.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ReplyRequest {

    private String content;

    private List<QuoteReplyRequest> quotes;

    private List<UUID> facts;

}
