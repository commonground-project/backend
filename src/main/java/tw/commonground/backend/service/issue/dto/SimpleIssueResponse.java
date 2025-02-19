package tw.commonground.backend.service.issue.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SimpleIssueResponse {

    private String id;

    private String createdAt;

    private String updatedAt;

    private String title;

    private String description;
}
