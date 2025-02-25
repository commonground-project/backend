package tw.commonground.backend.service.issue.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class IssueFactKey implements Serializable {

    private UUID issueId;

    private UUID factId;
}
