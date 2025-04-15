package tw.commonground.backend.service.follow.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class FollowResponse {
    private Boolean follow;
}
