package tw.commonground.backend.service.issue.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FollowResponse {
    private Boolean follow;
    private String updatedAt;
}
