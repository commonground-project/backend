package tw.commonground.backend.service.internal.viewpoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.service.internal.fact.dto.InternalDetailFactResponse;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@Builder
@ToString
public class InternalDetailViewpointResponse {

    private UUID id;

    private String content;

    @JsonProperty("like_count")
    private Integer likeCount;

    @JsonProperty("dislike_count")
    private Integer dislikeCount;

    @JsonProperty("reasonable_count")
    private Integer reasonableCount;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    private String title;

    @JsonProperty("publisher_id")
    private UUID authorId;

    private List<InternalDetailFactResponse> facts;
}
