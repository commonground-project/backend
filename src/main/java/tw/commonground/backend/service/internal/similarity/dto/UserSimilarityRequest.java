package tw.commonground.backend.service.internal.similarity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UserSimilarityRequest {
    @JsonProperty("issue_list")
    private List<IssueSimilarityRequest> issueList;

    @JsonProperty("viewpoint_list")
    private List<ViewpointSimilarityRequest> viewpointList;
}
