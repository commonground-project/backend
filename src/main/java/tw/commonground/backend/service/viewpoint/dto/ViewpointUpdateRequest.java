package tw.commonground.backend.service.viewpoint.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ViewpointUpdateRequest {
    @NotBlank
    private String title;
    private String content;
    private List<UUID> facts;

}
