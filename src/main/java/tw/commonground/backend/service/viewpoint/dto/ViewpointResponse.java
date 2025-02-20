package tw.commonground.backend.service.viewpoint.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.commonground.backend.service.fact.dto.FactResponse;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@Builder
public class ViewpointResponse {
    private UUID id;

    private String createdAt;

    private String updatedAt;

    private String title;

    private String content;

    private UUID authorId;

    private String authorName;

    private String authorAvatar;

    private Integer likeCount;

    private Integer reasonableCount;

    private Integer dislikeCount;

    private ViewpointReactionResponse userReaction;

    private List<FactResponse> facts;
}
