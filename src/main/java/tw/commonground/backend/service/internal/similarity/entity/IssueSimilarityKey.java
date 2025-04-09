package tw.commonground.backend.service.internal.similarity.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class IssueSimilarityKey {
    private Long userId;
    private UUID issueId;
}
