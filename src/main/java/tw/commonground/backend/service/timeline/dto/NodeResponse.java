package tw.commonground.backend.service.timeline.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class NodeResponse {

    private String id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String title;

    private String description;

    private String date;
}
