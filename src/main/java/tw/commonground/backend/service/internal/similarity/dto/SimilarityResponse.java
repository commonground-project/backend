package tw.commonground.backend.service.internal.similarity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@Builder
@ToString
public class SimilarityResponse {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("viewpoints_list")
    private List<ViewpointSimilarityResponse> viewpoints;

    @JsonProperty("issues_list")
    private List<IssueSimilarityResponse> issues;

}

@Getter
@Setter
@Builder
@ToString
class ViewpointSimilarityResponse {
    @JsonProperty("viewpoint_id")
    private UUID viewpointId;

    @JsonProperty("similarity")
    private Double similarity;
}

@Getter
@Setter
@Builder
@ToString
class IssueSimilarityResponse {
    @JsonProperty("issue_id")
    private UUID issueId;

    @JsonProperty("similarity")
    private Double similarity;
}
