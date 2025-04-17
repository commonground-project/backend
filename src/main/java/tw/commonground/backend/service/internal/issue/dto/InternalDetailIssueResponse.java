package tw.commonground.backend.service.internal.issue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.service.internal.fact.dto.InternalDetailFactResponse;
import tw.commonground.backend.service.internal.viewpoint.dto.InternalDetailViewpointResponse;
import tw.commonground.backend.service.issue.dto.IssueFollowResponse;

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

    private String authorName;

    private String authorAvatar;

    private IssueFollowResponse userFollow;

    private List<InternalDetailFactResponse> facts;

    private List<InternalDetailViewpointResponse> viewpoints;
}
