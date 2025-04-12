package tw.commonground.backend.service.internal.viewpoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
public class InternalViewpointResponse {
    @JsonProperty("viewpoint_id")
    private UUID viewpointId;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    private String content;

    @JsonProperty("author_id")
    private UUID authorId;

    @JsonProperty("like_count")
    private int likeCount;

    @JsonProperty("dislike_count")
    private int dislikeCount;

    @JsonProperty("reasonable_count")
    private int reasonableCount;

    @JsonProperty("view_count")
    private int viewCount;

    @JsonProperty("issue_id")
    private UUID issueId;

    @JsonProperty("fact_tags")
    private List<String> factTags;

    @JsonProperty("reply_count")
    private int replyCount;

    @JsonProperty("reply_content")
    private Object replyContent;

    @JsonProperty("sentiment_score")
    private float sentimentScore;
}
