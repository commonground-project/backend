package tw.commonground.backend.service.internal.similarity.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.*;
import tw.commonground.backend.service.issue.entity.IssueEntity;
import tw.commonground.backend.service.user.entity.UserEntity;

@Getter
@Setter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IssueSimilarityEntity {

    @EmbeddedId
    private IssueSimilarityKey key;

    @ManyToOne
    @MapsId("issueId")
    @ToString.Exclude
    private IssueEntity issue;

    @ManyToOne
    @MapsId("userId")
    @ToString.Exclude
    private UserEntity user;

    private Double similarity;

}
