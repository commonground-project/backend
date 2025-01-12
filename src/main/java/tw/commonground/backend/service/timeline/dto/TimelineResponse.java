package tw.commonground.backend.service.timeline.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TimelineResponse {
    private List<NodeResponse> content;
}
