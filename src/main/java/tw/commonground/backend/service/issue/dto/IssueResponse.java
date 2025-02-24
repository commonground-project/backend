package tw.commonground.backend.service.issue.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.service.fact.dto.FactResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
public class IssueResponse {

    private String id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String title;

    private String description;

    private String insight;

    private UUID authorId;

    private String authorName;

    private String authorAvatar;

    private IssueFollowResponse userFollow;

    private List<FactResponse> facts;
}
