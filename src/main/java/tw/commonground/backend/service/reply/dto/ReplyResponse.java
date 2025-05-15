package tw.commonground.backend.service.reply.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.service.fact.dto.FactResponse;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
public class ReplyResponse {

    private UUID id;

    private String createdAt;

    private String updatedAt;

    private String content;

    private UUID authorId;

    private String authorName;

    private String authorAvatar;

    private ReplyReactionResponse userReaction;

    private Integer likeCount;

    private Integer reasonableCount;

    private Integer dislikeCount;

    private List<QuoteReplyResponse> quotes;

    private Boolean readStatus;

    private List<FactResponse> facts;

}
