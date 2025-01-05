package tw.commonground.backend.service.viewpoint.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import tw.commonground.backend.service.viewpoint.entity.Reaction;

@Getter
@Setter
public class ViewpointReactionBase {
    @NotNull
    private Reaction reaction;
    // TODO: test it at playground, and need to add annotation @Valid at RequestBody
}
