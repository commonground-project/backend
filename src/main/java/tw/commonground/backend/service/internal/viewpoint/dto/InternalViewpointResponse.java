package tw.commonground.backend.service.internal.viewpoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class InternalViewpointResponse {
    @JsonProperty("viewpoint_id")
    UUID viewpointId;

    @JsonProperty("created_at")
    LocalDateTime createdAt;

    @JsonProperty("updated_at")
    LocalDateTime updatedAt;

    String content;

    @JsonProperty("author_id")
    UUID authorId;

    @JsonProperty("like_count")
    int likeCount;

    @JsonProperty("dislike_count")
    int dislikeCount;

    @JsonProperty("reasonable_count")
    int reasonableCount;

    @JsonProperty("view_count")
    int viewCount;

    @JsonProperty("issue_id")
    UUID issueId;

    @JsonProperty("fact_tags")
    List<String> factTags;

    @JsonProperty("reply_count")
    int replyCount;

    @JsonProperty("reply_content")
    Object replyContent;

    @JsonProperty("sentiment_score")
    float sentimentScore;
}
