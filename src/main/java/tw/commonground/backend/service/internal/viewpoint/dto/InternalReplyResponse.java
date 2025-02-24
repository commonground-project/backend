package tw.commonground.backend.service.internal.viewpoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
public class InternalReplyResponse {
    @JsonProperty("user_id")
    private UUID authorId;

    @JsonProperty("interaction_id")
    private UUID id;

    private String content;

    @JsonProperty("like_count")
    private Integer likeCount;

    @JsonProperty("dislike_count")
    private Integer dislikeCount;

    @JsonProperty("reasonable_count")
    private Integer reasonableCount;

    @JsonProperty("timestamp")
    private LocalDateTime createdAt;
}
