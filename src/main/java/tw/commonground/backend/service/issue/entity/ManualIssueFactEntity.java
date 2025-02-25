package tw.commonground.backend.service.issue.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tw.commonground.backend.service.fact.entity.FactEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "issue_fact_entity")
@EntityListeners(AuditingEntityListener.class)
public class ManualIssueFactEntity {

    public ManualIssueFactEntity(UUID issueId, UUID factId) {
        this.key = new IssueFactKey(issueId, factId);
    }

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @EmbeddedId
    private IssueFactKey key;

    @ManyToOne
    @MapsId("issueId")
    private IssueEntity issue;

    @ManyToOne
    @MapsId("factId")
    private FactEntity fact;
}
