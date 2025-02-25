package tw.commonground.backend.service.viewpoint.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ViewpointReactionKey implements Serializable {
    private Long userId;
    private UUID viewpointId;
}
