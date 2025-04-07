package tw.commonground.backend.service.internal.similarity.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.*;
import tw.commonground.backend.service.user.entity.UserEntity;
import tw.commonground.backend.service.viewpoint.entity.ViewpointEntity;

@Getter
@Setter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ViewpointSimilarityEntity {
    @EmbeddedId
    private ViewpointSimilarityKey key;

    @ManyToOne
    @MapsId("viewpointId")
    @ToString.Exclude
    private ViewpointEntity viewpoint;

    @ManyToOne
    @MapsId("userId")
    @ToString.Exclude
    private UserEntity user;

    private Double similarity;
}
