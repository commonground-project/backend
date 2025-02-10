package  tw.commonground.backend.service.internal.issue.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
public class InternalIssueResponse {
    @JsonProperty("issue_id")
    UUID issueId;

    @JsonProperty("created_at")
    LocalDateTime createdAt;

    @JsonProperty("updated_at")
    LocalDateTime updatedAt;

    String title;

    @JsonProperty("publisher_id")
    UUID authorId;

    @JsonProperty("view_count")
    int viewCount;

    @JsonProperty("viewpoint_count")
    int viewpointCount;
}
