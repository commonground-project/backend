package tw.commonground.backend.service.issue.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.commonground.backend.service.fact.entity.FactEntity;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ManualFactEntity {

    public ManualFactEntity(UUID issueId, UUID factId) {
        this.key = new IssueFactKey(issueId, factId);
    }

    @EmbeddedId
    private IssueFactKey key;

    @ManyToOne
    @MapsId("issueId")
    @JoinColumn(name = "issue_id")
    private IssueEntity issue;

    @ManyToOne
    @MapsId("factId")
    @JoinColumn(name = "fact_id")
    private FactEntity fact;
}
