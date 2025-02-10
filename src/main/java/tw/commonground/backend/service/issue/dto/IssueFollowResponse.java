package tw.commonground.backend.service.issue.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class IssueFollowResponse {
    private Boolean follow;

    private LocalDateTime updatedAt;
}
