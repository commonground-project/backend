package tw.commonground.backend.service.internal.issue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.service.internal.fact.dto.InternalDetailFactResponse;
import tw.commonground.backend.service.internal.viewpoint.dto.InternalDetailViewpointResponse;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
public class InternalDetailIssueResponse {

    @JsonProperty("issue_id")
    private UUID id;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    private String title;

    private String description;

    private String insight;

    @JsonProperty("publisher_id")
    private UUID authorId;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("author_avatar")
    private String authorAvatar;

    private List<InternalDetailFactResponse> facts;

    private List<InternalDetailViewpointResponse> viewpoints;
}
