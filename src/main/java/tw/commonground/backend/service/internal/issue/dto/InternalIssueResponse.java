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
    private UUID issueId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    private String title;

    @JsonProperty("publisher_id")
    private UUID authorId;

    @JsonProperty("view_count")
    private int viewCount;

    @JsonProperty("viewpoint_count")
    private int viewpointCount;
}
