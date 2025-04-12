package tw.commonground.backend.service.reference.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReferenceRequest {
    @NotBlank(message = "url is required")
    private String url;
}
