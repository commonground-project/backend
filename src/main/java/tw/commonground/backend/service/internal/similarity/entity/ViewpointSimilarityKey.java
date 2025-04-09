package tw.commonground.backend.service.internal.similarity.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ViewpointSimilarityKey {
    private Long userId;
    private UUID viewpointId;
}
