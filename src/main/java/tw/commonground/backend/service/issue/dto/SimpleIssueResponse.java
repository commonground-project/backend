package tw.commonground.backend.service.issue.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SimpleIssueResponse {

    private String id;

    private LocalDateTime createAt;

    private LocalDateTime updatedAt;

    private String title;

    private String description;
}
