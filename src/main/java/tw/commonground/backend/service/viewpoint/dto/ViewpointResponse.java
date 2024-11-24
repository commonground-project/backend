package tw.commonground.backend.service.viewpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
//import tw.commonground.backend.service.fact.entity.FactEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointReaction;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@Builder
public class ViewpointResponse {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String title;
    private String content;
    private UUID authorId;
    private String authorName;
    private URI authorAvatar;
    private ViewpointReaction userReaction;
    private Integer likeCount;
    private Integer reasonableCount;
    private Integer dislikeCount;
//    private List<FactEntity> facts;

}
