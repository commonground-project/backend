package tw.commonground.backend.service.internal.similarity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class IssueSimilarityRequest {

    @JsonProperty("issue_id")
    private UUID issueId;

    @JsonProperty("similarity")
    private Double similarity;
}
