package tw.commonground.backend.service.issue.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class IssueFollowKey {
    private Long userId;
    private UUID issueId;
}
