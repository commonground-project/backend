package tw.commonground.backend.service.internal.viewpoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import tw.commonground.backend.service.reply.entity.ReplyEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class InternalReplyResponse {
    @JsonProperty("userId")
    private UUID authorId;
    @JsonProperty("interactionId")
    private UUID id;
    private String content;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer reasonableCount;
    @JsonProperty("timestamp")
    private LocalDateTime createdAt;

    public static InternalReplyResponse fromEntity(ReplyEntity reply) {
        return InternalReplyResponse.builder()
                .id(reply.getId())
                .authorId(reply.getAuthorId())
                .content(reply.getContent())
                .likeCount(reply.getLikeCount())
                .dislikeCount(reply.getDislikeCount())
                .reasonableCount(reply.getReasonableCount())
                .createdAt(reply.getCreatedAt())
                .build();
    }
}
