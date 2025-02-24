package tw.commonground.backend.service.viewpoint.entity;

import jakarta.persistence.*;
import lombok.*;
import tw.commonground.backend.service.fact.entity.FactEntity;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ViewpointFactEntity {
    @EmbeddedId
    private ViewpointFactKey key;

    @ManyToOne
    @MapsId("viewpointId")
    private ViewpointEntity viewpoint;

    @ManyToOne
    @MapsId("factId")
    private FactEntity fact;
}
