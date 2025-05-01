package tw.commonground.backend.service.issue.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class SimpleIssueResponse {

    private String id;

    private String createdAt;

    private String updatedAt;

    private String title;

    private String description;

    private Integer viewpointCount;
}
