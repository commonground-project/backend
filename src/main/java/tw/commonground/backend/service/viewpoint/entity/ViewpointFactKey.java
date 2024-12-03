package tw.commonground.backend.service.viewpoint.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ViewpointFactKey implements Serializable {

    private UUID viewpointId;

    private UUID factId;
}
