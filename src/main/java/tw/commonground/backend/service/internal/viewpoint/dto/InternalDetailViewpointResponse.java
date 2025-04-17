package tw.commonground.backend.service.internal.viewpoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.service.fact.dto.FactResponse;
import tw.commonground.backend.service.internal.fact.dto.InternalDetailFactResponse;
import tw.commonground.backend.service.viewpoint.dto.ViewpointReactionResponse;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@Builder
@ToString
public class InternalDetailViewpointResponse {

    @JsonProperty("user_id")
    private UUID authorId;

    @JsonProperty("interaction_id")
    private UUID id;

    private String content;

    @JsonProperty("like_count")
    private Integer likeCount;

    @JsonProperty("dislike_count")
    private Integer dislikeCount;

    @JsonProperty("reasonable_count")
    private Integer reasonableCount;

    @JsonProperty("timestamp")
    private String createdAt;

    private String updatedAt;

    private String title;

    private String authorName;

    private String authorAvatar;

//    private ViewpointReactionResponse userReaction;

    private List<InternalDetailFactResponse> facts;
}
