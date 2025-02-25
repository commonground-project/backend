package tw.commonground.backend.service.internal.interaction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RelatedObject {
    private String type;

    private String id;
}
