package tw.commonground.backend.service.issue.entity;

import java.util.UUID;

public interface SimpleIssueEntity {
    UUID getId();
    String getTitle();
    String getDescription();
}
