package tw.commonground.backend.service.issue.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.commonground.backend.service.fact.dto.FactResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class IssueResponse {

    private String id;

    private LocalDateTime createAt;

    private LocalDateTime updatedAt;

    private String title;

    private String description;

    private String insight;

    private String authorId;

    private String authorName;

    private String authorAvatar;

    private List<FactResponse> insightFacts;
}
