package tw.commonground.backend.service.reply.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
public class QuoteReplyResponse {

    private UUID replyId;

    private UUID authorId;

    private String authorName;

    private String authorAvatar;

    private String content;

    private Integer start;

    private Integer end;

}
