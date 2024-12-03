package tw.commonground.backend.service.issue.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SimpleIssueEntity {
    UUID getId();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    String getTitle();

    String getDescription();
}
