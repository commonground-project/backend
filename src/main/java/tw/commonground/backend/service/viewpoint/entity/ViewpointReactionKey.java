package tw.commonground.backend.service.viewpoint.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ViewpointReactionKey implements Serializable {
    private Long userId;
    private UUID viewpointId;
}
