package tw.commonground.backend.service.reply.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.commonground.backend.service.reply.entity.Reaction;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ReactionResponse {

    private Reaction reaction;

    private Integer likeCount;

    private Integer reasonableCount;

    private Integer dislikeCount;

    private LocalDateTime updatedAt;
}
