package tw.commonground.backend.service.timeline.dto;

import tw.commonground.backend.service.timeline.entity.NodeEntity;

import java.util.List;
import java.util.stream.Collectors;

public final class TimelineMapper {
    private TimelineMapper() {
        // hide the constructor
    }

    public static TimelineResponse toResponse(List<NodeEntity> nodes) {
        List<NodeResponse> content = nodes.stream()
                .map(NodeMapper::toResponse)
                .collect(Collectors.toList());
        return new TimelineResponse(content);
    }
}
