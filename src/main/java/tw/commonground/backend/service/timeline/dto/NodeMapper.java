package tw.commonground.backend.service.timeline.dto;

import tw.commonground.backend.service.timeline.entity.NodeEntity;

import java.time.format.DateTimeFormatter;

public final class NodeMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private NodeMapper() {
        // hide the constructor
    }

    public static NodeResponse toResponse(NodeEntity entity) {
        return NodeResponse.builder()
                .id(entity.getId().toString())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .date(entity.getDate().format(FORMATTER))
                .build();
    }
}
