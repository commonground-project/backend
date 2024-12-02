package tw.commonground.backend.service.viewpoint.entity;

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
public class ViewpointFactEntity {

    public ViewpointFactEntity(ViewpointEntity viewpoint, FactEntity fact) {
        this.viewpoint = viewpoint;
        this.fact = fact;
    }

    public ViewpointFactEntity(UUID viewpointId, UUID factId) {
        this.key = new ViewpointFactKey(viewpointId, factId);
    }

    @EmbeddedId
    private ViewpointFactKey key;

    @ManyToOne
    @MapsId("viewpointId")
    private ViewpointEntity viewpoint;

    @ManyToOne
    @MapsId("factId")
    private FactEntity fact;
}
