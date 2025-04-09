package tw.commonground.backend.service.internal.similarity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class SimilarityRequest {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("issue_list")
    private List<IssueSimilarityRequest> issueList;

    @JsonProperty("viewpoint_list")
    private List<ViewpointSimilarityRequest> viewpointList;
}

