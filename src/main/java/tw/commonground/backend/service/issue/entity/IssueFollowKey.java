package tw.commonground.backend.service.issue.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class IssueFollowKey {
    private Long userId;
    private UUID issueId;
}
